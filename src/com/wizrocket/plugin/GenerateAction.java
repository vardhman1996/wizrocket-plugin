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

    private static Pattern p = Pattern.compile("^(\\w\\w\\w)-(\\w\\w\\w)-(\\w\\w\\w\\w)$");
    private HashSet<String> usesSet = new HashSet<>();
    private static Logger logger = Logger.getLogger("WizRocket");

    public GenerateAction() {
        super();
    }


    public void actionPerformed(AnActionEvent event) {

        List tags;
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

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

    }

    private void validateAndroidName(Node applicationNode) {
        NamedNodeMap attributesMap = applicationNode.getAttributes();
        if (attributesMap.getNamedItem("android:name") != null && attributesMap.getNamedItem("android:name").toString().equals("android:name=\"com.wizrocket.android.sdk.Application\"")) {
            logger.info("Android name recognized");
        } else {
            logger.info("Android name missing");
        }
    }

    private void validateUsesPermissions(NodeList usesPermList) {
        for (int i = 0; i < usesPermList.getLength(); i++) {
            Node tempUsesItem = usesPermList.item(i);
            NamedNodeMap usesMap = tempUsesItem.getAttributes();
            usesSet.add(usesMap.getNamedItem("android:name").toString());
        }

        if (usesSet.contains("android:name=\"android.permission.READ_PHONE_STATE\"") && usesSet.contains("android:name=\"android.permission.INTERNET\"")) {
            logger.info("Contains required uses permissions");
        }
    }

//    private void validateRequiredMeta(NodeList children) {
//        int length = children.getLength();
//        for (int i =0; i<length; i++) {
//            Node item = children.item(i);
//            if (!item.getNodeName().equals("meta-data")) continue;
//            NamedNodeMap metaAttr = item.getAttributes();
//
//
//        }
//    }
}
