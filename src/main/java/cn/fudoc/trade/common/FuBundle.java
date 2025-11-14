package cn.fudoc.trade.common;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.NotNull;

public class FuBundle extends DynamicBundle {

    private static final FuBundle INSTANCE = new FuBundle("messages.MyBundle");


    public FuBundle(@NotNull String pathToBundle) {
        super(pathToBundle);
    }


    public static String message(String key, Object... params) {
        return INSTANCE.getMessage(key, params);
    }
}