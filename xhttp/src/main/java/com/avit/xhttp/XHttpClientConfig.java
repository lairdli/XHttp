package com.avit.xhttp;

import android.content.Context;

import com.avit.xhttp.callback.ParseCallback;
import com.avit.xhttp.portal.PortParse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class XHttpClientConfig {
    // 默认核心池大小
    private static final int DEFAULT_CORE_SIZE = 2;
    // 最大线程数
    private static final int DEFAULT_MAX_SIZE = 2;
    // 池中空余线程存活时间
    private static final long DEFAULT_KEEP_ALIVE_TIME = 15;
    // 时间单位
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;
    // 线程池阻塞队列(默认队列长度为50)
    private static final int BLOCKING_QUEUE_SIZE = 50;
    private static BlockingQueue<Runnable> defaultQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_SIZE);

    // 上下文环境对象
    Context context;
    // 默认初始化
    int corePoolZie = DEFAULT_CORE_SIZE;
    int maxPoolSize = DEFAULT_MAX_SIZE;
    long keepAliveTime = DEFAULT_KEEP_ALIVE_TIME;
    TimeUnit timeUnit = DEFAULT_TIME_UNIT;
    BlockingQueue<Runnable> blockingQueue = defaultQueue;

    private List<HttpXmlConfig> httpXmlConfigArrayList = new ArrayList<>();

    public XHttpClientConfig(Context context) {
        this.context = context;
    }

    public XHttpClientConfig corePoolZie(int corePoolZie) {
        this.corePoolZie = corePoolZie;
        return this;
    }

    public XHttpClientConfig maxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
        return this;
    }

    public XHttpClientConfig keepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
        return this;
    }

    public XHttpClientConfig timeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
        return this;
    }

    public XHttpClientConfig blockingQueue(BlockingQueue<Runnable> blockingQueue) {
        this.blockingQueue = blockingQueue;
        return this;
    }

    public List<HttpXmlConfig> getHttpXmlConfigList() {
        return httpXmlConfigArrayList;
    }

    public void setHttpXmlConfig(HttpXmlConfig ... httpXmlConfig) {
        this.httpXmlConfigArrayList.clear();
        httpXmlConfigArrayList.addAll(Arrays.asList(httpXmlConfig));
    }

    ParseCallback parseCallback = new PortParse();

    public void setParseCallback(ParseCallback parseCallback) {
        this.parseCallback = parseCallback;
    }
}
