package com.wizrocket.plugin;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;

/**
 * Created by VardhmanMehta on 03/07/15.
 */
public class DocumentProvider extends EditorWriteActionHandler {
    private Document document;
    public DocumentProvider(Editor editor) {
        document = editor.getDocument();
    }

    public Document getDoc() {
        return document;
    }

}
