package cn.fudoc.trade.common;

import com.intellij.BundleBase;
import com.intellij.DynamicBundle;
import org.jetbrains.annotations.NotNull;

import java.util.ResourceBundle;

public class FuBundle extends DynamicBundle {

    private static final ResourceBundle INSTANCE = ResourceBundle.getBundle("messages.MyBundle");


    public FuBundle(@NotNull String pathToBundle) {
        super(pathToBundle);
    }


    public static String message(String key, Object... params) {
        return BundleBase.message(INSTANCE, key, params);
    }
}