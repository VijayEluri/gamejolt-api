package com.gamejolt.net;


public class Response {
    public final int code;
    public final byte[] content;

    Response(int code, byte[] content) {
        this.code = code;
        this.content = content;
    }

    public byte[] getContent() {
        return content;
    }

    public String getContentAsString() {
        return new String(content);
    }

    public int getCode() {
        return code;
    }
}
