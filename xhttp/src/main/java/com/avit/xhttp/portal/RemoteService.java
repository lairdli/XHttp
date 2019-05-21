package com.avit.xhttp.portal;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v4.app.Fragment;

import com.avit.xhttp.RequstException;
import com.avit.xhttp.callback.RequestCallback;
import com.avit.xhttp.xmlconfig.RequestEntity;
import com.avit.xhttp.xmlconfig.RequestEntityManager;

public class RemoteService {
	private static RemoteService service = null;

	private RemoteService() {

	}

	static PortParse parseCallback;

	public static synchronized RemoteService getInstance() {
		if (RemoteService.service == null) {
			RemoteService.service = new RemoteService();
		}
		return RemoteService.service;
	}
	public void involeMockData(final Activity activity,
							   final String apiKey,
							   final RequestCallback callBack) {
		final RequestEntity requestEntity = RequestEntityManager.findURLByKey(activity, apiKey);
		String mockFile = requestEntity.getMockJson();
		String strResponse = new MockService().getJsonFromLocal(activity, mockFile);

		Class srvClass = RequestEntityManager.getSrvDataClass(requestEntity);

		if (srvClass != null) {
			Handler handler = new Handler();
			try {
				getParseCallback().onParse(strResponse, srvClass, callBack, handler);
			} catch (RequstException e) {
				callBack.onFail(e.getMessage());
				e.printStackTrace();
			}
		}


	}

	public void involeMockData(final Fragment fragment,
							   final String apiKey,
							   final RequestCallback callBack) {
		final RequestEntity requestEntity = RequestEntityManager.findURLByKey(fragment.getActivity(), apiKey);
		String mockFile = requestEntity.getMockJson();
		String strResponse = new MockService().getJsonFromLocal(fragment.getActivity(), mockFile);
		Class srvClass = RequestEntityManager.getSrvDataClass(requestEntity);

		if (srvClass != null) {
			Handler handler = new Handler();
			try {
				getParseCallback().onParse(strResponse, srvClass, callBack, handler);
			} catch (RequstException e) {
				callBack.onFail(e.getMessage());
				e.printStackTrace();
			}
		}
	}


	public void involeMockData(final Context context,
							   final String apiKey,
							   final RequestCallback callBack) {
		final RequestEntity requestEntity = RequestEntityManager.findURLByKey(context, apiKey);
		String mockFile = requestEntity.getMockJson();
		String strResponse = new MockService().getJsonFromLocal(context, mockFile);
		Class srvClass = RequestEntityManager.getSrvDataClass(requestEntity);

		if (srvClass != null) {
			Handler handler = new Handler();
			try {
				getParseCallback().onParse(strResponse, srvClass, callBack, handler);
			} catch (RequstException e) {
				callBack.onFail(e.getMessage());
				e.printStackTrace();
			}
		}
	}


	public void involeMockStringData(final Context context,
							   final String apiKey,
							   final RequestCallback callBack) {
		final RequestEntity requestEntity = RequestEntityManager.findURLByKey(context, apiKey);
		String mockFile = requestEntity.getMockJson();
		String strResponse = new MockService().getJsonFromLocal(context, mockFile);
		Class srvClass = RequestEntityManager.getSrvDataClass(requestEntity);

		if (srvClass != null) {
			Handler handler = new Handler();
			try {
				getParseCallback().onParseString(strResponse, srvClass, callBack, handler);
			} catch (RequstException e) {
				callBack.onFail(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public void involeMockObjectJson(final Context context,
                                     final String jsonFile, final Class srvClass ,
                                     final RequestCallback callBack) {
		String strResponse = new MockService().getJsonFromLocal(context, jsonFile);

		if (srvClass != null) {
			Handler handler = new Handler();
			try {
				getParseCallback().onParse(strResponse, srvClass, callBack, handler);
			} catch (RequstException e) {
				callBack.onFail(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public static PortParse getParseCallback() {
		if(parseCallback == null){
			parseCallback = new PortParse();
		}
		return parseCallback;
	}
}