package com.wizRocket.NewPlugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;

import javax.swing.*;

/**
 * Created by VardhmanMehta on 01/07/15.
 */
public class GenerateDialog extends DialogWrapper {
    public GenerateDialog(Project project) {
        super(project);
        setTitle("Warning!!!");
        Messages.showMessageDialog("You have done this wrong!!", "Warning", Messages.getInformationIcon());



        init();


    }
    protected JComponent createCenterPanel() {
        return null;
    }
}
