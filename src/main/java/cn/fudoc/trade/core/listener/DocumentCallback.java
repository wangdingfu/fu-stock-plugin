package cn.fudoc.trade.core.listener;

import javax.swing.event.DocumentEvent;

public interface DocumentCallback {

    void callback(Integer type, DocumentEvent event);
}
