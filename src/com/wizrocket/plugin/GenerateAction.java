package com.wizrocket.plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;

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
                
            }
        });

    }
}
