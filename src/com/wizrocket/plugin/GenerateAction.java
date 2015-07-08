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
import java.util.List;
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

            //logger.info("Element = " + docElm.getAttribute("package"));

            NodeList usesPermList = docElm.getElementsByTagName("uses-permission");
            if (usesPermList == null || usesPermList.getLength() < 1) return;

            validateUsesPermissions(usesPermList);

            NodeList applicationList = docElm.getElementsByTagName("application");
            if (applicationList == null || applicationList.getLength() != 1) return;
            Node applicationNode = applicationList.item(0);

            validateAndroidName(applicationNode);

            NodeList children = applicationNode.getChildNodes();

            validateRequiredMeta(children);

            validateRequiredReceiver(children);

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

    private void validateUsesPermissions(NodeList usesPermList) {
        for (int i = 0; i < usesPermList.getLength(); i++) {
            Node tempUsesItem = usesPermList.item(i);
            NamedNodeMap usesMap = tempUsesItem.getAttributes();
            usesSet.add(usesMap.getNamedItem("android:name").getNodeValue());
        }
        if (usesSet.contains("android.permission.READ_PHONE_STATE") && usesSet.contains("android.permission.INTERNET")) {
            logger.info("Contains required uses permissions");
        } else {
            logger.info("Does not contain required uses permissions");
        }
        if(!usesSet.contains("android.permission.ACCESS_NETWORK_STATE")||!usesSet.contains("android.permission.GET_ACCOUNTS")||!usesSet.contains("android.permission.ACCESS_COARSE_LOCATION")||!usesSet.contains("android.permission.WRITE_EXTERNAL_STORAGE")) {
            logger.info("Does not contain recommended uses permissions");
        }
    }

    private void validateRequiredMeta(NodeList children) {
        int length = children.getLength();
        for (int i = 0; i<length; i++) {
            Node item = children.item(i);
            Matcher m;
            if (!item.getNodeName().equals("meta-data")) continue;
            NamedNodeMap metaAttr = item.getAttributes();

            if(metaAttr.getNamedItem("android:name").getNodeValue().equals("WIZROCKET_ACCOUNT_ID")) {
                m = accoundID.matcher(metaAttr.getNamedItem("android:value").getNodeValue());
                if(m.find()) {
                    logger.info("Correct format of Account ID");
                } else {
                    logger.info("Account ID not in the correct format");
                }
            }

            if(metaAttr.getNamedItem("android:name").getNodeValue().equals("WIZROCKET_TOKEN")) {
                m = accountToken.matcher(metaAttr.getNamedItem("android:value").getNodeValue());
                if(m.find()) {
                    logger.info("Correct format of Account Token");
                } else {
                    logger.info("Account Token not in the correct format");
                }
            }
        }
    }

    private void validateRequiredReceiver(NodeList children) {
        int length = children.getLength();
        for(int i = 0; i < length; i++) {
            Node item = children.item(i);
            if(!item.getNodeName().equals("receiver")) continue;

            NamedNodeMap receiverAttr = item.getAttributes();

            if(receiverAttr.getNamedItem("android:name").getNodeValue().equals("com.wizrocket.android.sdk.InstallReferrerBroadcastReceiver") && receiverAttr.getNamedItem("android:exported").getNodeValue().equals("true")) {
                logger.info("Receiver configured correctly");
            } else {
                logger.info("Receiver configured incorrectly");
            }

            NodeList receiverChildren = item.getChildNodes();
            int newLength = receiverChildren.getLength();
            for(int j = 0; j < newLength; j++) {
                Node childrenItem = receiverChildren.item(j);
                if(!childrenItem.getNodeName().equals("intent-filter")) continue;
                NodeList intentChildren = childrenItem.getChildNodes();
                int size = intentChildren.getLength();
                for(int k = 0; k < size; k++) {
                    Node childrenItem2 = intentChildren.item(k);
                    if(!childrenItem2.getNodeName().equals("action")) continue;
                    NamedNodeMap actionAttr = childrenItem2.getAttributes();

                    if(actionAttr.getNamedItem("android:name").getNodeValue().equals("com.android.vending.INSTALL_REFERRER")) {
                        logger.info("Correct action class for receiver");
                    } else {
                        logger.info("Incorrect action class for receiver");
                    }
                }
            }
        }
    }
}
