package com.wizRocket.NewPlugin;

/**
 * Created by VardhmanMehta on 02/07/15.
 */
public class DomModel {
    interface Root extends com.intellij.util.xml.DomElement {
        Id getId();
    }
    interface Id extends com.intellij.util.xml.DomElement {
        String getValue();
    }

}
