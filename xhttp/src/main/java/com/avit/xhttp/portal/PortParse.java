package com.avit.xhttp.portal;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.avit.xhttp.RequstException;
import com.avit.xhttp.callback.ParseCallback;
import com.avit.xhttp.callback.RequestCallback;
import com.avit.xhttp.utils.AvitLog;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by laird on 2016/10/12.
 */

public class PortParse implements ParseCallback {

    private final static String LOG_TAG = "PortParse";

    RequestCallback requestCallback;

    Handler handler;


    @Override
    public void onParse(String content, Class rspClass, final  RequestCallback requestCallback, Handler handler) throws RequstException {

        this.requestCallback = requestCallback;
        this.handler = handler;
        //to Object
        if(TextUtils.isEmpty(content)){
            return;
        }
//        Log.d(LOG_TAG,"content=->"+content);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(content);
            if(jsonObject.has("DataArea")){
                jsonObject = jsonObject.getJSONObject("DataArea");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Object rspObject = null;

        try {
             rspObject = parse(jsonObject, rspClass);
        } catch (RequstException e) {
            e.printStackTrace();
        }

        if (rspObject != null) {

            final Object finalRspObject = rspObject;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    requestCallback.onSuccess(finalRspObject);
                }
            });

        }
    }

    @Override
    public void onParseString(final String content, Class cls, final RequestCallback requestCallback, Handler handler) throws RequstException {
        this.requestCallback = requestCallback;
        this.handler = handler;
        //to Object
        if(TextUtils.isEmpty(content)){
            return;
        }
        Log.d(LOG_TAG,"content=->"+content);

            handler.post(new Runnable() {
                @Override
                public void run() {
                    requestCallback.onSuccess(content);
                }
            });

    }

    @Override
    public void onParseFail(final String errorMessage) {
        AvitLog.d(LOG_TAG,"parse errorMessage:"+errorMessage);

        handler.post(new Runnable() {
            @Override
            public void run() {
                PortParse.this.requestCallback.onFail(errorMessage);
            }
        });


    }



    /**
     * @description: TODO
     * @param jsonObject
     * @param cls
     * @return
     * @throws RequstException
     *             T
     */
    public Object parse(JSONObject jsonObject, Class cls) throws RequstException  {

        if (jsonObject == null) {
            onParseFail("parse: the parameters jsonObject is null!!!");
            throw new RequstException(LOG_TAG + " parse: the parameters jsonObject is null!!!");
        }

        if (cls == null) {
            onParseFail("parse: the parameters cls is null!!!");
            throw new RequstException(LOG_TAG + " parse: the parameters cls is null!!!");
        }

        try {
            Object g = new Gson().fromJson(jsonObject.toString(), cls);
            AvitLog.d(LOG_TAG,"解析成功:"+g.toString());
            return g;
        } catch (Exception e) {
            onParseFail("cls 解析错误");
            e.printStackTrace();
        }

        return null;
    }

}
