package io.github.dingxinliang88.maker.meta;

/**
 * 元数据异常类
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class MetaException extends RuntimeException {

    public MetaException(String message) {
        super(message);
    }

    public MetaException(String message, Throwable cause) {
        super(message, cause);
    }
}
