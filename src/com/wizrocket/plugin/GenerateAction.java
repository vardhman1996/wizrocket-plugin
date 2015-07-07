package com.wizrocket.plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;

import groovy.util.XmlParser;


import org.w3c.dom.*;
//import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by VardhmanMehta on 03/07/15.
 */
public class  GenerateAction extends AnAction {

    private static Logger logger  = Logger.getLogger("WizRocket");
    public GenerateAction () {
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
        if(psiFile == null || editor == null) {
            event.getPresentation().setEnabled(false);
            return;
        }
        //logger.info("Virtual File toString " + editor.getDocument().getText());
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(editor.getDocument().getText()));
            dom = db.parse(is);
            Element docElm = dom.getDocumentElement();
            
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
//        DocumentProvider document = new DocumentProvider(editor);
//        final Document doc = document.getDoc();
//
//
//
//        doc.addDocumentListener(new DocumentListener() {
//            @Override
//            public void beforeDocumentChange(DocumentEvent documentEvent) {
//
//            }
//
//            @Override
//            public void documentChanged(DocumentEvent documentEvent) {
//                Logger.getLogger("docSource").info(currentFile.getName());
//                if(currentFile.getName().equals("AndroidManifest.xml")) {
//                    XmlParser parser;
//                    try {
//                        parser = new XmlParser();
//                        Node root = parser.parseText(doc.getText());
//                        NodeList apps = (NodeList) root.get("application");
//                        logger.info("apps: " + apps);
//                        Node application = (Node) apps.get(0);
//                        logger.info("app: " + application.toString());
//                        Object attribute = application.attribute("android\\:allowBackup");
//                        logger.info("Android appliation class: " + attribute);
//                        Logger.getLogger("parsedText").info(root.toString());
//                    } catch (ParserConfigurationException | SAXException | IOException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }
//        });
    }
}
