package com.avit.xhttp;

import android.os.Handler;
import android.text.TextUtils;

import com.avit.xhttp.cache.CacheManager;
import com.avit.xhttp.callback.ParseCallback;
import com.avit.xhttp.callback.RequestCallback;
import com.avit.xhttp.xmlconfig.RequestEntity;
import com.avit.xhttp.xmlconfig.RequestEntityManager;
import com.avit.xhttp.utils.AvitLog;
import com.avit.xhttp.utils.BaseUtils;
import com.google.gson.Gson;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class HttpRequest  {
    private final static String LOG_TAG = "HttpRequest";
    // 区分get还是post的枚举
    public static final String REQUEST_GET = "get";
    public static final String REQUEST_POST = "post";

    RequestManager getHostManager() {
        return hostManager;
    }

    // 宿主Manager
    private RequestManager hostManager;

    private HttpUriRequest request = null;
    protected RequestEntity requestEntity = null;
    private RequestCallback requestCallback = null;
    private ParseCallback parseCallback = null;
    private List<RequestParameter> parameter = null;
    private String postparams;
    private String url = null; // 原始url
    private String newUrl = null; // 拼接key-value后的url
    private HttpResponse response = null;
    private DefaultHttpClient httpClient;
    // URL及HttpURLConnection对象
    private URL mURL;
    private HttpURLConnection mConnection;
    // 请求中断标志位
    private Boolean interrupted = false;

    Class rspClass;
    private Object reqObj;

    // 切换回UI线程
    protected Handler handler;

    protected boolean cacheRequestData = true;

    // 头信息
    HashMap<String, String> headers;

    static long deltaBetweenServerAndClientTime; // 服务器时间和客户端时间的差值


    public HttpRequest(){}

    /**
     * 传入请求参数
     * @param data
     * @param params
     * @param callBack
     */
    public HttpRequest(RequestManager hostManager, final RequestEntity data, final List<RequestParameter> params,
                       final RequestCallback callBack) {
        requestEntity = data;
        this.hostManager = hostManager;
        url = requestEntity.getUrl();
        this.parameter = params;
        requestCallback = callBack;

        if (httpClient == null) {
            httpClient = new DefaultHttpClient();
        }

        handler = new Handler();

        headers = new HashMap<String, String>();
    }

    /**
     * 无请求参数
     * @param data
     * @param callBack
     */
    public HttpRequest(RequestManager hostManager, final RequestEntity data,
                       final RequestCallback callBack) {
        requestEntity = data;
        this.hostManager = hostManager;
        url = requestEntity.getUrl();
        this.reqObj = null;
        this.parameter = null;
        requestCallback = callBack;


        if (httpClient == null) {
            httpClient = new DefaultHttpClient();
        }

        handler = new Handler();

        headers = new HashMap<String, String>();
    }

    /**
     * 传入请求对象
     * @param data
     * @param req_inf
     * @param callBack
     */
    public HttpRequest(RequestManager hostManager, final RequestEntity data, final Object req_inf,
                       final RequestCallback callBack, final ParseCallback parseCallback) {
        requestEntity = data;
        this.hostManager = hostManager;
        url = requestEntity.getUrl();
        this.reqObj = req_inf;
        this.parameter = null;
        this.requestCallback = callBack;
        this.parseCallback = parseCallback;


        if (httpClient == null) {
            httpClient = new DefaultHttpClient();
        }

        handler = new Handler();

        headers = new HashMap<String, String>();
    }

    public HttpRequest(final RequestEntity data, final Object req_inf
                       ) {
        requestEntity = data;
        url = requestEntity.getUrl();
        this.reqObj = req_inf;
        this.parameter = null;


        if (httpClient == null) {
            httpClient = new DefaultHttpClient();
        }

        headers = new HashMap<String, String>();
    }

    /**
     * 获取HttpUriRequest请求
     *
     * @return
     */
    public HttpUriRequest getRequest() {
        return request;
    }

    protected String startRequest() {
        String res = null;
        switch (requestEntity.getNetType()) {
            case REQUEST_GET:
                // 类型为HTTP-GET时，将请求参数组装到URL链接字符串上

                if ((parameter != null) && (parameter.size() > 0)) {
                    newUrl = getURLByNormalParams();
                } else if (reqObj != null) {
                    //Requst Object 请求URL
                    newUrl = genSendUrl(reqObj);
                } else {
                    newUrl = url;
                }
                AvitLog.d(" REQUEST_GET：\n" + newUrl);
                // 正式发送GET请求到服务器
                // 如果这个get的API有缓存时间（大于0）

                if (requestEntity.getExpires() > 0) {
                    final String content = CacheManager.getInstance()
                            .getFileCache(newUrl);
                    if (content != null) {
                        AvitLog.d(" load data from local cache");
                        handServerData(content);
                        res = content;
                    }
                }
                res = sendHttpGetToServer(newUrl);

                break;
            case REQUEST_POST:
                // 发送POST请求到服务器


                if (reqObj != null) {
                    //Requst Object 请求URL
                    postparams = new Gson().toJson(reqObj);
                }
                AvitLog.d(LOG_TAG, "json_REQUEST_POST:" + postparams);
                res = sendHttpPostToServer(requestEntity.getUrl());

                break;

        }
        if(hostManager!=null) {
            hostManager.requestList.remove(this);
        }

        return res;
    }


    /**
     * 发起GET请求
     *
     * @param url
     */
    private String sendHttpGetToServer(String url) {
        String res = "";
        InputStream inputStream = null;

        try {
            mURL = new URL(url);
            mConnection = (HttpURLConnection) mURL.openConnection();
            mConnection.setRequestProperty("Content-Type", "application/json");
            // 连接服务器的超时时长
            mConnection.setConnectTimeout(6000);
            // 从服务器读取数据的超时时长
            mConnection.setReadTimeout(6000);

            if (mConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // 如果未设置请求中断，则进行读取数据的工作
                if (!interrupted) {
                    // read content from response..
                    inputStream = mConnection.getInputStream();
                    final String result = readFromResponse(inputStream);
                    if (requestEntity.getNetType().equals(REQUEST_GET)
                            && requestEntity.getExpires() > 0) {
                        CacheManager.getInstance().putFileCache(newUrl,
                                result,
                                requestEntity.getExpires());
                    }
                    res = result;
                    handServerData(result);

                } else { // 中断请求

                    return "";
                }
            } else {
                AvitLog.e(LOG_TAG,"==>ResponseCode"+mConnection.getResponseCode());
                handleNetworkError("网络异常，请检查网络 ResponseCode : " + mConnection.getResponseCode());
            }
        } catch (MalformedURLException e) {
            handleNetworkError("网络异常，请检查网络");
           e.printStackTrace();

        } catch (IOException e) {
            handleNetworkError("网络异常，请检查网络");
            e.printStackTrace();
        } finally {
//            hostManager.requestList.remove(this);
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(mConnection!=null){
                mConnection.disconnect();
            }

        }
        return res;
    }

    /**
     * 发起POST请求
     *
     * @param url
     */
    private String sendHttpPostToServer(String url) {
        String res = "";
        InputStream inputStream = null;
        OutputStream outputStream = null;
        BufferedWriter bufferedWriter = null;

        try {
            mURL = new URL(url);
            mConnection = (HttpURLConnection) mURL.openConnection();

            mConnection.setRequestProperty("Content-Type", "application/json");
            // 连接服务器的超时时长
            mConnection.setConnectTimeout(6000);
            // 从服务器读取数据的超时时长
            mConnection.setReadTimeout(6000);
            // 允许输入输出
            mConnection.setDoOutput(true);
            mConnection.setDoInput(true);
            // 向请求体中写入参数
            if (parameter != null && !parameter.isEmpty()) {
                String paramString = convertParam2String();
                outputStream = mConnection.getOutputStream();
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                bufferedWriter.write(paramString);
                bufferedWriter.close();
            }

            if (!TextUtils.isEmpty(postparams)) {
                outputStream = mConnection.getOutputStream();
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                bufferedWriter.write(postparams);
                bufferedWriter.close();
            }
            if (mConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                if (!interrupted) {
                    inputStream = mConnection.getInputStream();
                    final String result = readFromResponse(inputStream);
                    res = result;
                    handServerData(result);
                } else {
                    return "";
                }
            } else {
                handleNetworkError("网络异常，请检查网络 ResponseCode : " + mConnection.getResponseCode());
            }
        } catch (MalformedURLException e) {

            handleNetworkError("网络异常，请检查网络");
            e.printStackTrace();

        } catch (IOException e) {

            handleNetworkError("网络异常，请检查网络");
            e.printStackTrace();

        } finally {
//            hostManager.requestList.remove(this);


            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (bufferedWriter != null){
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(mConnection!=null){
                mConnection.disconnect();
            }

        }
        return res;
    }


    /**
     * 将请求参数转换为String
     */
    private String convertParam2String() {
        StringBuilder paramsBuilder = new StringBuilder();
        for (int i = 0; i < parameter.size(); i++) {
            RequestParameter param = parameter.get(i);
            paramsBuilder.append(param.getName()).append("=").append(param.getValue());
            if (i < parameter.size() - 1)
                paramsBuilder.append("&");
        }

        return paramsBuilder.toString();
    }


    /**
     * 从http response中读取响应数据
     * @param inputStream
     * @return
     * @throws IOException
     */
    private String readFromResponse(InputStream inputStream) throws IOException {
        String line;
        StringBuilder builder = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        while ((line = br.readLine()) != null) {
            builder.append(line);
        }
        return builder.toString();
    }


    /**
     * 处理服务器返回的数据
     *
     * @param content
     */
    protected void handServerData(final String content) {
        AvitLog.d(LOG_TAG, "HttpRequest=> REQUEST_RECEIVE");
        // Logger.json(content);
        AvitLog.json(content);

        rspClass = RequestEntityManager.getSrvDataClass(requestEntity);

        if (rspClass != null && parseCallback != null) {
            try {
                parseCallback.onParse(content, rspClass, requestCallback, handler);
            } catch (RequstException e) {
                e.printStackTrace();
            }
        } else {
            // to Content
            if (requestCallback != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        requestCallback.onSuccess(content);
                    }
                });
            }
        }


    }

    /**
     * 通过请求参数生成URL
     *
     * @return
     */
    private String getURLByNormalParams() {
        String tmpURL = null;
        // 添加参数
        final StringBuffer paramBuffer = new StringBuffer();
        // 这里要对key进行排序,普通请求URL
        sortKeys();

        for (final RequestParameter p : parameter) {
            if (paramBuffer.length() == 0) {
                paramBuffer.append(p.getName() + "="
                        + BaseUtils.UrlEncodeUnicode(p.getValue()));
            } else {
                paramBuffer.append("&" + p.getName() + "="
                        + BaseUtils.UrlEncodeUnicode(p.getValue()));
            }
        }
        tmpURL = url + "?" + paramBuffer.toString();
        return tmpURL;
    }


    public void handleNetworkError(final String errorMsg) {
        if (requestCallback != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    requestCallback.onFail(errorMsg);
                }
            });
        }
    }

    static String inputStreamToString(final InputStream is) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = -1;
        while ((i = is.read()) != -1) {
            baos.write(i);
        }
        return baos.toString();
    }

    void sortKeys() {
        for (int i = 1; i < parameter.size(); i++) {
            for (int j = i; j > 0; j--) {
                RequestParameter p1 = parameter.get(j - 1);
                RequestParameter p2 = parameter.get(j);
                if (compare(p1.getName(), p2.getName())) {
                    // 交互p1和p2这两个对象，写的超级恶心
                    String name = p1.getName();
                    String value = p1.getValue();

                    p1.setName(p2.getName());
                    p1.setValue(p2.getValue());

                    p2.setName(name);
                    p2.setValue(value);
                }
            }
        }
    }

    // 返回true说明str1大，返回false说明str2大
    boolean compare(String str1, String str2) {
        String uppStr1 = str1.toUpperCase();
        String uppStr2 = str2.toUpperCase();

        boolean str1IsLonger = true;
        int minLen = 0;

        if (str1.length() < str2.length()) {
            minLen = str1.length();
            str1IsLonger = false;
        } else {
            minLen = str2.length();
            str1IsLonger = true;
        }

        for (int index = 0; index < minLen; index++) {
            char ch1 = uppStr1.charAt(index);
            char ch2 = uppStr2.charAt(index);
            if (ch1 != ch2) {
                if (ch1 > ch2) {
                    return true; // str1大
                } else {
                    return false; // str2大
                }
            }
        }

        return str1IsLonger;
    }




    /**
     * 更新服务器时间和本地时间的差值
     */
    void updateDeltaBetweenServerAndClientTime() {
        if (response != null) {
            final Header header = response.getLastHeader("Date");
            if (header != null) {
                final String strServerDate = header.getValue();
                try {
                    if ((strServerDate != null) && !strServerDate.equals("")) {
                        final SimpleDateFormat sdf = new SimpleDateFormat(
                                "EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH);
                        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));

                        Date serverDateUAT = sdf.parse(strServerDate);

                        deltaBetweenServerAndClientTime = serverDateUAT
                                .getTime()
                                + 8 * 60 * 60 * 1000
                                - System.currentTimeMillis();
                    }
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Date getServerTime() {
        return new Date(System.currentTimeMillis()
                + deltaBetweenServerAndClientTime);
    }




    /**
     * @description: TODO
     * @param req
     * @return String
     */
    public String genSendUrl(Object req) {

        String sendStr = requestEntity.getUrl() + req.getClass().getSimpleName();
        Field[] fields = req.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.get(req) != null) {
                    if (field.getName().equals("MAP")) {
                        sendStr += field.get(req);
                    } else {
                        sendStr += "&" + field.getName() + "=" + URLEncoder.encode(("" + field.get(req)), "UTF-8");

                    }
                }
            }
        } catch (IllegalArgumentException e) {

            e.printStackTrace();
            AvitLog.e(LOG_TAG, "IllegalArgumentException :" + e.getMessage());
            return null;

        } catch (IllegalAccessException e) {

            e.printStackTrace();
            AvitLog.e(LOG_TAG, "IllegalAccessException :" + e.getMessage());
            return null;

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
            AvitLog.e(LOG_TAG, "IllegalAccessException :" + e.getMessage());
            return null;

        }

        return sendStr.trim();
    }


    /**
     * 中断请求
     */
    void disconnect() {
        // 设置标志位
        interrupted = true;
        // 如果当前请求正处于与服务器连接状态下，则断开连接
        if (mConnection != null)
            mConnection.disconnect();
    }



}