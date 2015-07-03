package com.wizrocket.plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.editor.Editor;

/**
 * Created by VardhmanMehta on 03/07/15.
 */
public class  GenerateAction extends AnAction {
    public GenerateAction () {
        super();
    }

    public void actionPerformed(AnActionEvent event) {
        Editor editor = DataKeys.EDITOR.getData(event.getDataContext());


    }
}
