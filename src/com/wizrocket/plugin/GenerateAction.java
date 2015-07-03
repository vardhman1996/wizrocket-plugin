package com.wizrocket.plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import groovy.util.Node;
import groovy.util.XmlParser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by VardhmanMehta on 03/07/15.
 */
public class  GenerateAction extends AnAction {
    public GenerateAction () {
        super();
    }

    public void actionPerformed(AnActionEvent event) {
        Editor editor = DataKeys.EDITOR.getData(event.getDataContext());
        DocumentProvider document = new DocumentProvider(editor);
        final Document doc = document.getDoc();
        doc.addDocumentListener(new DocumentListener() {
            @Override
            public void beforeDocumentChange(DocumentEvent documentEvent) {

            }

            @Override
            public void documentChanged(DocumentEvent documentEvent) {
                Object docSource = documentEvent.getSource();
                Logger.getLogger("docSource").info(docSource.toString());
                if(docSource.toString().contains("AndroidManifest.xml")) {
                    XmlParser parser;
                    try {
                        parser = new XmlParser();
                        Node root = parser.parseText(doc.getText());
                        Logger.getLogger("ParsedText").info(root.toString());
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    } catch (SAXException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }
}
