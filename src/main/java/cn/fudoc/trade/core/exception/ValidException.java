package cn.fudoc.trade.core.exception;

import com.intellij.openapi.ui.ValidationInfo;
import lombok.Getter;

public class ValidException extends RuntimeException{

    @Getter
    private final ValidationInfo validationInfo;

    public ValidException(ValidationInfo validationInfo) {
        this.validationInfo = validationInfo;
    }
}
