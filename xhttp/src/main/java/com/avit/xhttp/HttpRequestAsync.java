package com.avit.xhttp;


import com.avit.xhttp.callback.ParseCallback;
import com.avit.xhttp.callback.RequestCallback;
import com.avit.xhttp.xmlconfig.RequestEntity;

import java.util.List;

public class HttpRequestAsync extends HttpRequest implements Runnable {


    public HttpRequestAsync(RequestManager hostManager, RequestEntity data, List<RequestParameter> params, RequestCallback callBack) {
        super(hostManager, data, params, callBack);
    }

    public HttpRequestAsync(RequestManager hostManager, RequestEntity data, RequestCallback callBack) {
        super(hostManager, data, callBack);
    }

    public HttpRequestAsync(RequestManager hostManager, RequestEntity data, Object req_inf, RequestCallback callBack, ParseCallback parseCallback) {
        super(hostManager, data, req_inf, callBack, parseCallback);
    }

    @Override
    public void run() {
        startRequest();
    }
}