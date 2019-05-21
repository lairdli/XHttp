package com.avit.xhttp;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;


import com.avit.xhttp.callback.ParseCallback;
import com.avit.xhttp.callback.RequestCallback;
import com.avit.xhttp.portal.PortParse;
import com.avit.xhttp.portal.RemoteService;
import com.avit.xhttp.utils.AvitLog;
import com.avit.xhttp.xmlconfig.RequestEntity;
import com.avit.xhttp.xmlconfig.RequestEntityManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XHttpClient {
    // 配置信息
    static XHttpClientConfig config;

    // 存放每个Activity对应的RequestManager
    static Map<Context, RequestManager> managerMapContext;

    // 存放每个Activity对应的RequestManager
    static Map<Activity, RequestManager> managerMap;

    // 存放每个Fragment对应的RequestManager
    static Map<Fragment, RequestManager> managerFragmentMap;

    static ParseCallback parseCallback;

    private static  final String TAG = "XHttpClient";

    /**
     * 初始化
     *
     * @param config 全局配置信息
     */
    public static void init(XHttpClientConfig config) {
        XHttpClient.config = config;
        managerMap = new HashMap<>();

        managerFragmentMap = new HashMap<>();

        managerMapContext = new HashMap<>();
        // 初始化线程池
        RequestThreadPool.init();

        RequestEntityManager.init(config.context,config.getHttpXmlConfigList());
    }

    /**
     * 执行HTTP请求(不含请求参数)
     *
     * @param activity 发起HTTP请求的Activity
     * @param apiKey   根据该值从XML文件中获取对应的URLEntity
     * @param callBack HTTP请求执行完毕后的回调接口
     */
    public static void invokeRequest(
            Activity activity, String apiKey, RequestCallback callBack) {
        invokeRequest(activity, apiKey, null, callBack);
    }


    /**
     * 执行HTTP请求(含请求参数)
     *
     * @param activity 发起HTTP请求的Activity
     * @param apiKey   根据该值从XML文件中获取对应的URLEntity
     * @param params   HTTP请求参数
     * @param callBack HTTP请求执行完毕后的回调接口
     */
    public static HttpRequest invokeRequest(
            Activity activity, String apiKey,
            List<RequestParameter> params, RequestCallback callBack) {
        // 根据apiKey从XML文件中读取封装的URL实体信息
        RequestEntity requestEntity = RequestEntityManager.findURLByKey(activity, apiKey);
        // 获取该activity对应的RequestManager对象，并创建HttpRequest对象
        RequestManager manager = checkRequestManager(activity, true);
        HttpRequestAsync request = manager.createRequest(
                requestEntity, params, callBack);
        // 执行请求
        RequestThreadPool.execute(request);

        return request;
    }

    public static void invokeRequest(final Activity activity,
                                     final Object reqObject,
                                     final RequestCallback callBack) {
        String apiKey = reqObject.getClass().getSimpleName();
        final RequestEntity requestEntity = RequestEntityManager.findURLByKey(activity, apiKey);
        if (requestEntity == null) {
            AvitLog.e(TAG, apiKey + " 解析对象本地xml未配置，请先配置");
            callBack.onFail(apiKey + " 解析对象本地xml未配置，请先配置");
            return;
        }
        if(requestEntity.getMockJson()!=null){
            AvitLog.d(TAG, apiKey + " 解析本地测试json");
            RemoteService.getInstance().involeMockData( activity,apiKey,callBack);
            return;
        }
        RequestManager manager = checkRequestManager(activity, true);
        HttpRequestAsync request = manager.createRequest(
                requestEntity, reqObject, callBack, config.parseCallback);
        // 执行请求
        RequestThreadPool.execute(request);
    }

    public static void invokeRequest(final Context context,
                                     final Object reqObject,
                                     final RequestCallback callBack
    ) {
        String apiKey = reqObject.getClass().getSimpleName();
        final RequestEntity requestEntity = RequestEntityManager.findURLByKey(context, apiKey);
        if (requestEntity == null) {
            AvitLog.e(TAG, apiKey + " 解析对象本地xml未配置，请先配置");
            callBack.onFail(apiKey + " 解析对象本地xml未配置，请先配置");
            return;
        }
        if(requestEntity.getMockJson()!=null){
            AvitLog.e(TAG, apiKey + " 解析本地测试json");
            RemoteService.getInstance().involeMockData( context,apiKey,callBack);
            return;
        }
        RequestManager manager = checkRequestManager(context, true);
        HttpRequestAsync request = manager.createRequest(
                requestEntity, reqObject, callBack, config.parseCallback);
        // 执行请求
        RequestThreadPool.execute(request);
    }

    public static void invokeRequest2(final Context context,
                                     final Object reqObject,
                                     final RequestCallback callBack
    ) {
        String apiKey = reqObject.getClass().getSimpleName();
        final RequestEntity requestEntity = RequestEntityManager.findURLByKey(context, apiKey);
        if (requestEntity == null) {
            AvitLog.e(TAG, apiKey + " 解析对象本地xml未配置，请先配置");
            callBack.onFail(apiKey + " 解析对象本地xml未配置，请先配置");
            return;
        }
        if(requestEntity.getMockJson()!=null){
            AvitLog.e(TAG, apiKey + " 解析本地测试json");
            RemoteService.getInstance().involeMockStringData( context,apiKey,callBack);
            return;
        }
        RequestManager manager = checkRequestManager(context, true);
        HttpRequestAsync request = manager.createRequest(
                requestEntity, reqObject, callBack, config.parseCallback);
        // 执行请求
        RequestThreadPool.execute(request);
    }

    public static void invokeRequest(final Fragment fragment,
                                     final Object reqObject,
                                     final RequestCallback callBack
    ) {
        String apiKey = reqObject.getClass().getSimpleName();
        final RequestEntity requestEntity = RequestEntityManager.findURLByKey(fragment.getActivity(), apiKey);
        if (requestEntity == null) {
            AvitLog.e(TAG, apiKey + " 解析对象本地xml未配置，请先配置");
            callBack.onFail(apiKey + " 解析对象本地xml未配置，请先配置");
            return;
        }
        if(requestEntity.getMockJson()!=null){
            AvitLog.e(TAG, apiKey + " 解析本地测试json");
            RemoteService.getInstance().involeMockData( fragment,apiKey,callBack);
            return;
        }
        RequestManager manager = checkRequestManager(fragment, true);
        HttpRequestAsync request = manager.createRequest(
                requestEntity, reqObject, callBack, config.parseCallback);
        // 执行请求
        RequestThreadPool.execute(request);
    }


    public static void invokeRequest(final Activity activity,
                                     final String apiKey,
                                     final RequestCallback callBack,
                                     final ParseCallback parseCallback) {
        final RequestEntity requestEntity = RequestEntityManager.findURLByKey(activity, apiKey);
        if (requestEntity == null) {
            AvitLog.e(TAG, apiKey + " 解析对象本地xml未配置，请先配置");
            callBack.onFail(apiKey + " 解析对象本地xml未配置，请先配置");
            return;
        }
        if(requestEntity.getMockJson()!=null){
            AvitLog.e(TAG, apiKey + " 解析本地测试json");
            RemoteService.getInstance().involeMockData( activity,apiKey,callBack);
            return;
        }
        RequestManager manager = checkRequestManager(activity, true);
        HttpRequestAsync request = manager.createRequest(
                requestEntity, null, callBack, config.parseCallback);
        // 执行请求
        RequestThreadPool.execute(request);
    }

    public static Object invokeSyncRequest(Object reqObject) {
        String apiKey = reqObject.getClass().getSimpleName();
        final RequestEntity requestEntity = RequestEntityManager.findURLByKey(apiKey);
        if (requestEntity == null) {
            AvitLog.e(TAG, apiKey + " 解析对象本地xml未配置，请先配置");
            return null;
        }
        return new HttpRequestSync(requestEntity,reqObject).executeSync();
    }

    /**
     * 取消指定Activity中发起的所有HTTP请求
     *
     * @param activity
     */
    public static void cancelAllRequest(Activity activity) {
        checkRequestManager(activity, false).cancelAllRequest();
    }

    /**
     * 取消线程池中整个阻塞队列所有HTTP请求
     */
    public static void cancelAllRequest() {
        RequestThreadPool.removeAllTask();
    }

    /**
     * 取消指定Activity中未执行的请求
     *
     * @param activity
     */
    public static void cancelBlockingRequest(Activity activity) {
        checkRequestManager(activity, false).cancelBlockingRequest();
    }

    /**
     * 取消指定请求
     *
     * @param activity
     * @param request
     */
    public static void cancelDesignatedRequest(Activity activity, HttpRequest request) {
        checkRequestManager(activity, false).cancelDesignatedRequest(request);
    }

    /**
     * 访问activity对应的RequestManager对象
     *
     * @param context
     * @param createNew 当RequestManager对象为null时是否创建新的RequestManager对象
     * @return
     */
    private static RequestManager checkRequestManager(Context context, boolean createNew) {
        RequestManager manager;
        if ((manager = managerMapContext.get(context)) == null) {
            if (createNew) {
                manager = new RequestManager();
                managerMapContext.put(context, manager);
            } else {
                throw new NullPointerException(managerMapContext.getClass().getSimpleName() + "'s RequestManager is null!");
            }
        }
        return manager;
    }

    /**
     * 访问activity对应的RequestManager对象
     *
     * @param activity
     * @param createNew 当RequestManager对象为null时是否创建新的RequestManager对象
     * @return
     */
    private static RequestManager checkRequestManager(Activity activity, boolean createNew) {
        RequestManager manager;
        if ((manager = managerMap.get(activity)) == null) {
            if (createNew) {
                manager = new RequestManager();
                managerMap.put(activity, manager);
            } else {
                throw new NullPointerException(activity.getClass().getSimpleName() + "'s RequestManager is null!");
            }
        }
        return manager;
    }


    /**
     * 访问activity对应的RequestManager对象
     *
     * @param fragment
     * @param createNew 当RequestManager对象为null时是否创建新的RequestManager对象
     * @return
     */
    private static RequestManager checkRequestManager(Fragment fragment, boolean createNew) {
        RequestManager manager;
        if ((manager = managerFragmentMap.get(fragment)) == null) {
            if (createNew) {
                manager = new RequestManager();
                managerFragmentMap.put(fragment, manager);
            } else {
                throw new NullPointerException(fragment.getClass().getSimpleName() + "'s RequestManager is null!");
            }
        }
        return manager;
    }


    /**
     * 关闭线程池，并等待任务执行完成，不接受新任务
     */
    public static void shutdown() {
        RequestThreadPool.shutdown();
    }

    /**
     * 关闭，立即关闭，并挂起所有正在执行的线程，不接受新任务
     */
    public static void shutdownRightnow() {
        RequestThreadPool.shutdownRightnow();
    }
}
