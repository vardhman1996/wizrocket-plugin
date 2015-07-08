package com.wizrocket.plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by VardhmanMehta on 03/07/15.
 */
public class GenerateAction extends AnAction {

    private static Pattern accoundID = Pattern.compile("^(\\w\\w\\w)-(\\w\\w\\w)-(\\w\\w\\w\\w)$");
    private static Pattern accountToken = Pattern.compile("^(\\w\\w\\w)-(\\w\\w\\w)$");
    private HashSet<String> usesSet = new HashSet<>();
    private static Logger logger = Logger.getLogger("WizRocket");
    private String userPackage;

    public GenerateAction() {
        super();
    }


    public void actionPerformed(AnActionEvent event) {

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        PsiFile psiFile = event.getData(LangDataKeys.PSI_FILE);
        VirtualFile currentFile = DataKeys.VIRTUAL_FILE.getData(event.getDataContext());
        Editor editor = DataKeys.EDITOR.getData(event.getDataContext());

        //Logger.getLogger("Get Name").info(currentFile.getPath());
        if (psiFile == null || editor == null) {
            event.getPresentation().setEnabled(false);
            return;
        }
        //logger.info("Virtual File toString " + editor.getDocument().getText());
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(editor.getDocument().getText()));
            dom = db.parse(is);
            Element docElm = dom.getDocumentElement();
            userPackage = docElm.getAttribute("package");

            //logger.info("Element = " + docElm.getAttribute("package"));

            NodeList usesPermList = docElm.getElementsByTagName("uses-permission");
            NodeList gcmPermList = docElm.getElementsByTagName("permission");
            if (usesPermList == null || usesPermList.getLength() < 1) return;

            validateUsesPermissions(usesPermList, gcmPermList);

            NodeList applicationList = docElm.getElementsByTagName("application");
            if (applicationList == null || applicationList.getLength() != 1) return;
            Node applicationNode = applicationList.item(0);

            validateAndroidName(applicationNode);

            NodeList children = applicationNode.getChildNodes();

            validateRequiredMeta(children);

            validateRequiredReceiver(children);

            validateGcmReceiver(children);

            validateInAppNotifications(children);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

    }

    private void validateAndroidName(Node applicationNode) {
        NamedNodeMap attributesMap = applicationNode.getAttributes();
        if (attributesMap.getNamedItem("android:name") != null && attributesMap.getNamedItem("android:name").getNodeValue().equals("com.wizrocket.android.sdk.Application")) {
            logger.info("Android name recognized");
        } else {
            logger.info("Android name missing");
        }
    }

    private void validateUsesPermissions(NodeList usesPermList, NodeList gcmPermList) {
        for (int i = 0; i < usesPermList.getLength(); i++) {
            Node tempUsesItem = usesPermList.item(i);
            NamedNodeMap usesMap = tempUsesItem.getAttributes();
            usesSet.add(usesMap.getNamedItem("android:name").getNodeValue());
        }

        for (int i = 0; i < gcmPermList.getLength(); i++) {
            Node tempGcmPermItem = gcmPermList.item(i);
            NamedNodeMap gcmPermMap = tempGcmPermItem.getAttributes();
            usesSet.add(gcmPermMap.getNamedItem("android:name").getNodeValue());
            usesSet.add(gcmPermMap.getNamedItem("android:protectionLevel").getNodeValue());
        }

        logger.info(usesSet.toString());

        if (usesSet.contains("android.permission.READ_PHONE_STATE") && usesSet.contains("android.permission.INTERNET")) {
            logger.info("Contains required uses permissions");
        } else {
            logger.info("Does not contain required uses permissions");
        }
        if (!usesSet.contains("android.permission.ACCESS_NETWORK_STATE") ||
                !usesSet.contains("android.permission.GET_ACCOUNTS") ||
                !usesSet.contains("android.permission.ACCESS_COARSE_LOCATION") ||
                !usesSet.contains("android.permission.WRITE_EXTERNAL_STORAGE")) {

            logger.info("Does not contain recommended uses permissions");
        } else {
            logger.info("Does contain recommended uses permissions");
        }

        if (usesSet.contains("signature") && usesSet.contains(userPackage + ".permission.C2D_MESSAGE") && usesSet.contains("com.google.android.c2dm.permission.RECEIVE")) {
            logger.info("GCM uses permissions configured correctly");
        } else {
            logger.info("GCM uses permissions not used yet");
        }
    }

    private void validateRequiredMeta(NodeList children) {
        boolean one = false;
        boolean two = false;
        int length = children.getLength();
        for (int i = 0; i < length; i++) {
            Node item = children.item(i);
            Matcher m;
            if (!item.getNodeName().equals("meta-data")) continue;
            NamedNodeMap metaAttr = item.getAttributes();

            if (metaAttr.getNamedItem("android:name").getNodeValue().equals("WIZROCKET_ACCOUNT_ID")) {
                m = accoundID.matcher(metaAttr.getNamedItem("android:value").getNodeValue());
                if (m.find()) {
                    logger.info("Correct format of Account ID");
                } else {
                    logger.info("Account ID not in the correct format");
                }
            }

            if (metaAttr.getNamedItem("android:name").getNodeValue().equals("WIZROCKET_TOKEN")) {
                m = accountToken.matcher(metaAttr.getNamedItem("android:value").getNodeValue());
                if (m.find()) {
                    logger.info("Correct format of Account Token");
                } else {
                    logger.info("Account Token not in the correct format");
                }
            }

            if (metaAttr.getNamedItem("android:name").getNodeValue().equals("GCM_SENDER_ID")) {
                String namedItem = metaAttr.getNamedItem("android:value").getNodeValue();
                if (namedItem.startsWith("id:")) {
                    logger.info("GCM ID configured correctly");
                } else {
                    logger.info("GCM ID configured incorrectly");
                }
            }

            if (metaAttr.getNamedItem("android:name").getNodeValue().equals("com.google.android.gms.version")) {
                if (metaAttr.getNamedItem("android:value").getNodeValue().equals("@integer/google_play_services_version")) {
                    logger.info("Meta Data name and value set correctly for GCM");
                } else {
                    logger.info("Meta Data name and value set incorrectly for GCM");
                }
            }

            if(metaAttr.getNamedItem("android:name").getNodeValue().equals("WIZROCKET_INAPP_EXCLUDE")) {
                if(metaAttr.getNamedItem("android:value").getNodeValue().equals("SplashActivity")) {
                    logger.info("In-App notifications values set correctly");
                } else {
                    logger.info("In-App notifications values set incorrectly");
                }
            }
        }
    }

    private void validateRequiredReceiver(NodeList children) {
        int length = children.getLength();
        for (int i = 0; i < length; i++) {
            Node item = children.item(i);
            if (!item.getNodeName().equals("receiver")) continue;

            NamedNodeMap receiverAttr = item.getAttributes();

            if (receiverAttr.getNamedItem("android:name").getNodeValue().equals("com.wizrocket.android.sdk.InstallReferrerBroadcastReceiver")) {
                if (receiverAttr.getNamedItem("android:exported").getNodeValue().equals("true")) {
                    logger.info("Receiver configured correctly");
                } else {
                    logger.info("Receiver configured incorrectly");
                }
            }

            NodeList receiverChildren = item.getChildNodes();
            int newLength = receiverChildren.getLength();
            for (int j = 0; j < newLength; j++) {
                Node childrenItem = receiverChildren.item(j);
                if (!childrenItem.getNodeName().equals("intent-filter")) continue;
                NodeList intentChildren = childrenItem.getChildNodes();
                int size = intentChildren.getLength();
                for (int k = 0; k < size; k++) {
                    Node childrenItem2 = intentChildren.item(k);
                    if (!childrenItem2.getNodeName().equals("action")) continue;
                    NamedNodeMap actionAttr = childrenItem2.getAttributes();

                    if (actionAttr.getNamedItem("android:name").getNodeValue().equals("com.android.vending.INSTALL_REFERRER")) {
                        logger.info("Correct action class for receiver");
                    }
                }
            }
        }
    }

    private void validateGcmReceiver(NodeList children) {
        int length = children.getLength();
        for (int i = 0; i < length; i++) {
            Node item = children.item(i);
            if (!item.getNodeName().equals("receiver")) continue;

            NamedNodeMap receiverAttr = item.getAttributes();

            if (receiverAttr.getNamedItem("android:name").getNodeValue().equals("com.wizrocket.android.sdk.GcmBroadcastReceiver")) {
                if (receiverAttr.getNamedItem("android:permission").getNodeValue().equals("com.google.android.c2dm.permission.SEND")) {
                    logger.info("GCM Receiver configured correctly");
                } else {
                    logger.info("GCM Receiver configured incorrectly");
                }
            }

            NodeList gcmRecChildren = item.getChildNodes();
            int newLength = gcmRecChildren.getLength();
            for (int j = 0; j < newLength; j++) {
                Node childrenItem = gcmRecChildren.item(j);
                if (!childrenItem.getNodeName().equals("intent-filter")) continue;
                NodeList intentChildren = childrenItem.getChildNodes();
                int size = intentChildren.getLength();
                //logger.info(""+size);
                for (int k = 0; k < size; k++) {
                    //logger.info("reached here");
                    Node childrenItem2 = intentChildren.item(k);
                    if (!(childrenItem2.getNodeName().equals("action")
                            || childrenItem2.getNodeName().equals("category"))) continue;

                    NamedNodeMap attrMap = childrenItem2.getAttributes();

                    if (attrMap.getNamedItem("android:name").getNodeValue().equals("com.google.android.c2dm.intent.RECEIVE") ||
                            attrMap.getNamedItem("android:name").getNodeName().equals("com.google.android.c2dm.intent.REGISTRATION") ||
                            attrMap.getNamedItem("android:name").getNodeName().equals(userPackage)) {
                        logger.info("Correct action and category tags configured");
                    }
                }
            }
        }
    }

    private void validateInAppNotifications(NodeList children) {
        int length = children.getLength();
        for (int i = 0; i < length; i++) {
            Node item = children.item(i);
            if (!item.getNodeName().equals("activity")) continue;

            NamedNodeMap actionAttr = item.getAttributes();
            if(actionAttr.getNamedItem("android:name").getNodeValue().equals("com.wizrocket.android.sdk.InAppNotificationActivity") && actionAttr.getNamedItem("android:theme").getNodeValue().equals("@android:style/Theme.Translucent.NoTitleBar") && actionAttr.getNamedItem("android:configChanges").getNodeValue().equals("orientation|keyboardHidden")) {
                logger.info("In-App notifications configured correctly");
            } else {
                logger.info("In-App notifications configured incorrectly");
            }
        }
    }

//    private Node matchNodes (Node rootNode, Map<String, String> attributes, String nodeName) {
//        NodeList childNodes = rootNode.getChildNodes();
//        int length = childNodes.getLength();
//        for(int i = 0; i < length; i++) {
//            Node item = childNodes.item(i);
//            NamedNodeMap itemMap = item.getAttributes();
//            if(item.getNodeName().equals(nodeName)) {
//                return item;
//            }
//        }
//        return null;
//    }
}

