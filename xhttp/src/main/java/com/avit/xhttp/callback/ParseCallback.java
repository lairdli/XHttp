package com.avit.xhttp.callback;


import android.os.Handler;

import com.avit.xhttp.RequstException;


public interface ParseCallback
{

	public void onParse(String content, Class cls, RequestCallback requestCallback, Handler handler) throws RequstException;

	public void onParseString(String content, Class cls, RequestCallback requestCallback, Handler handler) throws RequstException;

	public void onParseFail(String errorMessage);

}
