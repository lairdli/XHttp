package com.avit.xhttp.xmlconfig;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.os.Environment;
import android.text.TextUtils;


import com.avit.xhttp.HttpXmlConfig;
import com.avit.xhttp.R;
import com.avit.xhttp.utils.AvitLog;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RequestEntityManager {
    private static Map<String, RequestEntity> requestEntityMap;

    private static List<HttpXmlConfig> httpXmlConfigList;

    private static final String SYS_URL_KEY = "persist.sys.avit.url";


    public static void init(Context context, List<HttpXmlConfig> httpXmlConfigs) {
        requestEntityMap = new HashMap<>();
        httpXmlConfigList = new ArrayList<>();
        httpXmlConfigList.addAll(httpXmlConfigs);
        if (context != null) {
            fetchUrlDataFromXml(context);
        }
    }


    private synchronized static void fetchUrlDataFromXml(final Context context) {

        if (httpXmlConfigList.isEmpty()) {

            httpXmlConfigList.add(new HttpXmlConfig(R.xml.portal_url, "asg"));
        }


        for (HttpXmlConfig httpXmlConfig : httpXmlConfigList) {
            AvitLog.d("httpXmlConfig :" + httpXmlConfig.toString());
            initRequstListFromXml(context, httpXmlConfig.getXmlResId(),
                    getBaseUrl(httpXmlConfig.getBaseUrl(), httpXmlConfig.getNickName()));
        }
    }

    private static void initRequstListFromXml(Context context, int xmlResName, String baseUrl) {
        AvitLog.d("initRequstListFromXml start ");
        final XmlResourceParser xmlParser = context
                .getResources().getXml(xmlResName);


        int eventCode;
        String expires = null;
        String netType = null;
        String url = null;
        try {
            eventCode = xmlParser.getEventType();
            while (eventCode != XmlPullParser.END_DOCUMENT) {
                switch (eventCode) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if ("url".equals(xmlParser.getName())) {
                            expires = xmlParser.getAttributeValue(null,
                                    "Expires");
                            netType = xmlParser.getAttributeValue(null,
                                    "NetType");
                            if (TextUtils.isEmpty(baseUrl)) {
                                url = xmlParser.getAttributeValue(null,
                                        "Url");
                            }else {
                                url = baseUrl;
                            }


                        }
                        if ("Node".equals(xmlParser.getName())) {
                            final String key = xmlParser.getAttributeValue(null,
                                    "Key");

                            String node_expires = xmlParser.getAttributeValue(null,
                                    "Expires");
                            String node_netType = xmlParser.getAttributeValue(null,
                                    "NetType");
                            String node_url = xmlParser.getAttributeValue(null,
                                    "Url");
                            String entityClass = xmlParser.getAttributeValue(null,
                                    "EntityClass");
                            String mockJson = xmlParser.getAttributeValue(null,
                                    "MockJson");


                            if (TextUtils.isEmpty(node_expires)) {
                                node_expires = expires;
                            }
                            if (TextUtils.isEmpty(node_netType)) {
                                node_netType = netType;
                            }
                            if (TextUtils.isEmpty(node_url)) {
                                node_url = url;
                            }
                            final RequestEntity requestEntity = new RequestEntity();
                            requestEntity.setKey(key);
                            requestEntity.setExpires(Long.parseLong(node_expires));
                            requestEntity.setNetType(node_netType);
                            requestEntity.setEntityClass(entityClass);
                            requestEntity.setUrl(node_url);
                            requestEntity.setMockJson(mockJson);
                            requestEntityMap.put(key, requestEntity);

                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    default:
                        break;
                }
                eventCode = xmlParser.next();
            }
        } catch (final XmlPullParserException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            xmlParser.close();
        }

        AvitLog.d("initRequstListFromXml end ");
    }

    private static String getBaseUrl(String defaultUrl, String nickName) {

        String url = getSystemProperties(SYS_URL_KEY + nickName);
        return TextUtils.isEmpty(url) ? defaultUrl : url;
    }

    private static String getSystemProperties(String key) {
        String result = "";
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");

            Method get = c.getMethod("get", String.class);
            result = (String) get.invoke(c, key);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static RequestEntity findURLByKey(final String findKey) {
        // 如果urlList还没有数据（第一次），或者被回收了，那么（重新）加载xml
        if (requestEntityMap == null || requestEntityMap.isEmpty()) {
            throw new RuntimeException("u shoud init HttpConfig pass Context");
        }

        if (requestEntityMap.containsKey(findKey)) {
            return requestEntityMap.get(findKey);
        }

        return null;
    }


    public static RequestEntity findURLByKey(final Context context,
                                             final String findKey) {
        // 如果urlList还没有数据（第一次），或者被回收了，那么（重新）加载xml
        if (requestEntityMap == null || requestEntityMap.isEmpty())
            fetchUrlDataFromXml(context);

        if (requestEntityMap.containsKey(findKey)) {
            return requestEntityMap.get(findKey);
        }

        return null;
    }


    public static Class getSrvDataClass(RequestEntity requestEntity) {
        if (requestEntity.getEntityClass() != null) {
            try {
                return Class.forName(requestEntity.getEntityClass());
            } catch (ClassNotFoundException e) {

                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }

    }

}