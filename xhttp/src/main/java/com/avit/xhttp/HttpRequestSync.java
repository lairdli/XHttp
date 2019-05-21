package com.avit.xhttp;

import android.text.TextUtils;

import com.avit.xhttp.xmlconfig.RequestEntity;
import com.avit.xhttp.xmlconfig.RequestEntityManager;
import com.avit.xhttp.utils.AvitLog;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;


public class HttpRequestSync extends HttpRequest {

    public HttpRequestSync(RequestEntity data, Object req_inf) {
        super(data, req_inf);
    }

    public Object executeSync() {

        String responseJson = startRequest();
        rspClass = RequestEntityManager.getSrvDataClass(requestEntity);

        return onParse(responseJson, rspClass);

    }


    public Object onParse(String content, Class rspClass) {

        //to Object
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(content);
            if (jsonObject.has("DataArea")) {
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

        return rspObject;
    }


    public Object parse(JSONObject jsonObject, Class cls) throws RequstException {


        if (jsonObject == null) {
            AvitLog.e("parse: the parameters jsonObject is null!!!");
            return null;
        }

        if (cls == null) {
            AvitLog.e("parse: the parameters cls is null!!!");
            return null;
        }

        try {
            Object g = new Gson().fromJson(jsonObject.toString(), cls);
            AvitLog.d("解析成功:" + g.toString());
            return g;
        } catch (Exception e) {
            AvitLog.e("cls 解析错误");
            e.printStackTrace();
        }

        return null;
    }

}
