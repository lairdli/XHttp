```
tips:如果您在电脑上直接打开，推荐使用Typora客户端打开该文档
     如果您在AndroidStudio打开，推荐下载markdown插件，Preferences-Plugins-Markdown-Navgitor
```
# 自定义网络框架XHttp使用说明

## 1 简介

该自定义网络框架通过解析XML 请求接口，解析对象，参数，基于Android HttpURLConnections请求网络资源。

> - 支持get/post请求，默认支持get请求
> - 支持xml可配置请求参数，解析对象，缓存时间等
> - 支持本地mock测试数据（json）
> - 支持请求结果自动解析
> - 支持同步/异步请求，默认都是异步请求



## 2 集成说明
可选择aar集成或者私服maven依赖集成

将离线xhttp.aar copy至app模块lib目录下

app模块build.gradle中声明如下



```
repositories{
		flatDir{dirs 'libs'}
}
dependencies {
    implementation (name:'xhttp',ext:'aar')
}
```

### 2.1 aar集成

### 2.2 私服maven依赖

工程buid.grade

```Dart
...
allprojects {
    repositories {
        maven { url "http://192.168.3.246:8081/repository/maven-public-android/" }
        google()
        jcenter()
        
    }
}
...
```

app模块 build.grade

```Dart
...
dependencies {
    ...
    implementation'com.avit.xhttp:xhttp:+'
}
```

## 3 使用说明

### step1 Applications初始化

```java
        XHttpClientConfig config = new XHttpClientConfig(getApplicationContext());
//        config.setHttpXmlConfig(
//                new HttpXmlConfig(R.xml.portal_url,AppConfig.URL_ASG,"asg"),
//                new HttpXmlConfig(R.xml.epg_url, AppConfig.URL_EPG,"epg"));
//        config.setParseCallback(new PortParse());
        XHttpClient.init(config);
```

### step2 实现接口请求类

以json_device_check为例
```java
public class json_device_check implements portal_req_inf {

   /**
    * @return
    * @see portal_req_inf#isValid()
    */
   @Override
   public boolean isValid() {
      return true;
   }

   /**
    * @return
    * @see portal_req_inf#isCache()
    */
   @Override
   public boolean isCache() {
      return false;
   }

   private static String mac_addr = "";
   private String sc_code = "";
   private String md5 = "";
}
```

### step3 实现接口解析类
   以DeviceCheck类为例

```java
public class DeviceCheck extends MessageCode {

   private String userCode;

   private String matchResult;

   private String locationCode;

   private String groupId;
}
```
### step4 实现xml配置

以json_device_check为例,res-xml-portal_url.xml配置如下
```xml
<?xml version="1.0" encoding="UTF-8"?>
<url
    Expires="0"
    NetType="get"
    Url="http://10.5.1.45:9005/asg?attribute=">
    <!--湖南益阳测试IP Url="http://10.5.1.45:9005/asg?attribute="-->
    <!-- http://192.168.3.187:8085/asg?attribute=-->

    <!-- 测试 http://192.168.3.193:9005/asg?attribute=-->
<Node
    EntityClass="com.avit.ott.data.bean.common.DeviceCheck"
    MockJson = "pay_config_data.json"
    Key="json_device_check" />

</url>
```
> Expires ：本地缓存时间
> NetType：请求方式（get/post）
> Url： api接口根地址
> EntityClass： json自动解析对象
> Key：          api接口名
> MockJson       assert目录下本地测试json(如有配置改项，走本地数据)

### step 5 api调用
XHttpClient直接调用 

#### 5.1 Activity 调用

```java
/**
 *
 * @param activity
 * @param reqObject  请求对象，类似json_get_asg_info js_get_asg_info = new json_get_asg_info();
 * @param callBack   回调
 */
public static void invokeRequest(final Activity activity,
                                 final Object reqObject,
                                 final RequestCallback callBack)



```
#### 5.2 Fragment调用

```java
/**
 *
 * @param fragment
 * @param reqObject  请求对象，类似json_get_asg_info js_get_asg_info = new json_get_asg_info();
 * @param callBack   回调
 */
public static void invokeRequest(final Fragment fragment,
                                 final Object reqObject,
                                 final RequestCallback callBack
)

```
#### 5.3 Context 调用

```java
/**
 *
 * @param context
 * @param reqObject  请求对象，类似json_get_asg_info js_get_asg_info = new json_get_asg_info();
 * @param callBack   回调
 */
public static void invokeRequest(final Context context,
                                 final Object reqObject,
                                 final RequestCallback callBack
)
```





以json_device_check为例




```java
json_device_check js_device_check = new json_device_check(getApplicationContext());

HttpClient.invokeRequest(APPLICATION, js_device_check, new RequestCallback() {
    @Override
    public void onSuccess(String content) {

    }
    
    @Override
    public void onSuccess(Object obj) {
        if (obj != null && obj instanceof DeviceCheck) {


            DeviceCheck deviceCheck = (DeviceCheck) obj;
            if ("0".equals(deviceCheck.getMatchResult())) {
                PortManager.setUserCode(deviceCheck.getUserCode());
            }
            PortManager.setLocationCode(deviceCheck.getLocationCode());
            loadSupportConfig();
        }
    }
    
    @Override
    public void onFail(String errorMessage) {
    }
});
```

#### 5.4 同步调用



```
public static Object invokeSyncRequest(Object reqObject)；
```



参考代码

```
public  List<GetWikiss.Wikis> getWikiInfoByTagOrChildTag(String tag) {
    List<GetWikiss.Wikis> wikiList = new ArrayList<>();

    GetWikiInfoByTagOrChildTag getWikiInfoByTagOrChildTag = new GetWikiInfoByTagOrChildTag();
    getWikiInfoByTagOrChildTag.putParam("tag", tag);
    getWikiInfoByTagOrChildTag.putParam("page", 1);
    getWikiInfoByTagOrChildTag.putParam("pagesize", PAGE_SIZE);

    GetWikiss getWikiss = (GetWikiss) HttpClient.invokeSyncRequest(getWikiInfoByTagOrChildTag);

    if (getWikiss == null || getWikiss.getWikis() == null) {
        return wikiList;
    }

    return Arrays.asList(getWikiss.getWikis());
}
```