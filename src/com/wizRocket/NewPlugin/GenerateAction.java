package com.wizRocket.NewPlugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import groovy.util.XmlParser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.logging.Logger;


/**
 * Created by VardhmanMehta on 01/07/15.
 */
public class GenerateAction extends AnAction {

    public GenerateAction() {
        // Set the menu item name.
        super("Text _Boxes");
        // Set the menu item name, description and icon.
        // super("Text _Boxes","Item description",IconLoader.getIcon("/Mypackage/icon.png"));
    }

    public void actionPerformed(AnActionEvent event) {
        /*DocumentBuilderFactory dBFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dBFactory.newDocumentBuilder();*/
        Project project = event.getData(PlatformDataKeys.PROJECT);
        final VirtualFile file = DataKeys.VIRTUAL_FILE.getData(event.getDataContext());
        Editor editor = DataKeys.EDITOR.getData(event.getDataContext());


        final Document doc = editor.getDocument();
        //DomManager manager = DomManager.getDomManager(project);
        doc.addDocumentListener(new DocumentListener() {
            @Override
            public void beforeDocumentChange(DocumentEvent documentEvent) {

            }

            @Override
            public void documentChanged(DocumentEvent documentEvent) {
                Object doc2 = documentEvent.getSource();
                try {
                    XmlParser parser = new XmlParser();


                    Logger.getLogger("Parsed").info(parser.parseText(doc.getText()).toString());
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (doc2.toString().contains("AndroidManifest.xml")) {
                    Logger.getLogger("WR").info(doc.getText());
                }

                //Logger.getLogger("WR2").info(doc2.toString());
            }
        });
        /*// TODO: insert action logic here
        Editor editor = DataKeys.EDITOR.getData(event.getDataContext());
        Handler handler = new Handler(editor);
        final Document doc = handler.doc();
        doc.addDocumentListener(new DocumentListener() {
            @Override
            public void beforeDocumentChange(DocumentEvent documentEvent) {

            }

            @Override
            public void documentChanged(DocumentEvent documentEvent) {

                Logger.getLogger("WR").info(doc.getText());
            }
        });
        Project project = event.getData(PlatformDataKeys.PROJECT);*/
        //GenerateDialog dialog = new GenerateDialog(project);
        //dialog.show();
        //String txt= Messages.showInputDialog(project, "What is your name?", "Input your name", Messages.getQuestionIcon());
        //Messages.showMessageDialog(project, "Hello, " + txt + "!\n I am glad to see you.", "Information", Messages.getInformationIcon());
    }


}
