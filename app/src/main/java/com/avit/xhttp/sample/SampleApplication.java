package com.avit.xhttp.sample;

import android.app.Application;

import com.avit.xhttp.XHttpClient;
import com.avit.xhttp.XHttpClientConfig;
import com.avit.xhttp.HttpXmlConfig;
import com.avit.xhttp.R;
import com.avit.xhttp.portal.PortParse;

public class SampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        XHttpClientConfig config = new XHttpClientConfig(getApplicationContext());
//        config.setHttpXmlConfig(
//                new HttpXmlConfig(R.xml.portal_url,AppConfig.URL_ASG,"asg"),
//                new HttpXmlConfig(R.xml.epg_url, AppConfig.URL_EPG,"epg"));
//        config.setParseCallback(new PortParse());
        XHttpClient.init(config);
    }
}
