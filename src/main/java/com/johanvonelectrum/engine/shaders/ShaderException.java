package com.johanvonelectrum.engine.shaders;

public class ShaderException extends Throwable {

    public ShaderException() {
        super();
    }

    public ShaderException(String message) {
        super(message);
    }

    public ShaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShaderException(Throwable cause) {
        super(cause);
    }
}
