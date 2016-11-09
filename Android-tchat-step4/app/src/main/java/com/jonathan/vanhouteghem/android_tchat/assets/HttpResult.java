package com.jonathan.vanhouteghem.android_tchat.assets;

public class HttpResult {

    public int code;
    public String json;

    public HttpResult(int code, String s) {
        this.code = code;
        this.json = s;
    }
}
