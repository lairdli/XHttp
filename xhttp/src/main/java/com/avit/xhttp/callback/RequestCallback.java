package com.avit.xhttp.callback;



public interface RequestCallback
{
	public void onSuccess(String content);

	public void onSuccess(Object obj);

	public void onFail(String errorMessage);

}
