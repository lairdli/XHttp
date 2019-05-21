package com.avit.xhttp;

import android.app.Activity;

import com.avit.xhttp.callback.ParseCallback;
import com.avit.xhttp.callback.RequestCallback;
import com.avit.xhttp.xmlconfig.RequestEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class RequestManager {
	ArrayList<HttpRequest> requestList = null;


    public RequestManager(Activity mActivity) {
        // 异步请求列表
        requestList = new ArrayList<HttpRequest>();
    }

	public RequestManager() {
		// 异步请求列表
		requestList = new ArrayList<HttpRequest>();
	}

	/**
	 * 添加Request到列表
	 */
	public void addRequest(final HttpRequest request) {
		requestList.add(request);
	}



	/**
	 * 无参数调用
	 */
	public HttpRequestAsync createRequest(final RequestEntity requestEntity,
                                     final RequestCallback requestCallback) {
		final HttpRequestAsync request = new HttpRequestAsync(this, requestEntity,requestCallback);

		addRequest(request);
		return request;
	}

	/**
	 * 有参数调用
	 */
	public HttpRequestAsync createRequest(final RequestEntity requestEntity,
                                     final List<RequestParameter> params,
                                     final RequestCallback requestCallback) {
		final HttpRequestAsync request = new HttpRequestAsync(this, requestEntity, params,
				requestCallback);

		addRequest(request);
		return request;
	}

	/**
	 * 有参数调用
	 */
	public HttpRequestAsync createRequest(final RequestEntity requestEntity,
                                     final Object req_inf,
                                     final RequestCallback requestCallback,final ParseCallback parseCallback) {
		final HttpRequestAsync request = new HttpRequestAsync(this, requestEntity, req_inf,
				requestCallback,parseCallback);

		addRequest(request);
		return request;
	}

    /**
     * 取消网络请求
     */
    public void cancelRequest() {
        if ((requestList != null) && (requestList.size() > 0)) {
            for (final HttpRequest request : requestList) {
                if (request.getRequest() != null) {
                    try {
                        request.getRequest().abort();
                        requestList.remove(request.getRequest());
                    } catch (final UnsupportedOperationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 取消所有的网络请求(包括正在执行的)
     */
    public void cancelAllRequest() {
        BlockingQueue queue = RequestThreadPool.getQuene();
        for (int i = requestList.size() - 1; i >= 0; i--) {
            HttpRequest request = requestList.get(i);
            if (queue.contains(request)) {
                queue.remove(request);
            } else {
                request.disconnect();
            }
        }
        requestList.clear();
    }

    /**
     * 取消未执行的网络请求
     */
    public void cancelBlockingRequest() {
        // 取交集(即取出那些在线程池的阻塞队列中等待执行的请求)
        List<HttpRequest> intersection = (List<HttpRequest>) requestList.clone();
        intersection.retainAll(RequestThreadPool.getQuene());
        // 分别删除
        RequestThreadPool.getQuene().removeAll(intersection);
        requestList.removeAll(intersection);
    }

    /**
     * 取消指定的网络请求
     */
    public void cancelDesignatedRequest(HttpRequest request) {
        if (!RequestThreadPool.removeTaskFromQueue(request)) {
            request.disconnect();
        }
    }
}
