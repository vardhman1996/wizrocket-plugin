package com.wizRocket.NewPlugin;


import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;



/**
 * Created by VardhmanMehta on 01/07/15.
 */
public class Handler extends EditorWriteActionHandler {
    private Document doc;
    public Handler(Editor editor) {
        doc = editor.getDocument();
    }

    public Document doc() {
        return doc;
    }
}
