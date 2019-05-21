package com.avit.xhttp.callback;

public interface HttpCallback {
    void onRequestSuccess(String data);
    void onRequestFail(String msg);
}
