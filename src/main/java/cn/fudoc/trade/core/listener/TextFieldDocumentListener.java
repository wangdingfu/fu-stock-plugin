package cn.fudoc.trade.core.listener;



import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TextFieldDocumentListener implements DocumentListener {

    private final Integer type;

    private final DocumentCallback documentCallback;

    public TextFieldDocumentListener(Integer type, DocumentCallback documentCallback) {
        this.type = type;
        this.documentCallback = documentCallback;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        documentCallback.callback(type,e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        documentCallback.callback(type,e);

    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        documentCallback.callback(type,e);

    }
}
