package cn.fudoc.trade.core.exception;

import lombok.Getter;

@Getter
public class FuStockException extends RuntimeException{

    private final String errorMsg;

    public FuStockException(String message) {
        super(message);
        this.errorMsg = message;
    }
}
