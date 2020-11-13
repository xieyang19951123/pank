package com.xy.pank.untils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class HttpGetAndPost {
    @Autowired
    private RedisUtil redisUtil;

    @Value("${vendor.wx.config.app_id}")
    private String appId;

    @Value("${vendor.wx.pay.secret}")
    private String secret;

    public  Map<String,String> doGet( Map<String, String> param,String url) {

        // 创建Httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();

        String resultString = "";
        CloseableHttpResponse response = null;
        try {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                for (String key : param.keySet()) {
                    builder.addParameter(key, param.get(key));
                }
            }
            URI uri = builder.build();

            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);

            // 执行请求
            response = httpclient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Map<String,String> parse =  JSON.parseObject(resultString,Map.class);
        return parse;
    }

    /**
     * 获取用户openId
     * @param code 校验码
     * @return openId
     */
    public   Map<String,String> getOpenId(String code){
        HashMap<String, String> param = new HashMap<>();
        param.put("appid",appId);
        param.put("secret",secret);
        param.put("code",code);
        param.put("grant_type","authorization_code");
        Map<String,String> openId = doGet(param,"https://api.weixin.qq.com/sns/oauth2/access_token");
        System.out.println(openId);

        return openId;
    }


    public   Map<String,String> getUser(String code){


        Map<String,String> openid = getOpenId(code);
        //ystem.out.println("===============================");
        if(openid.get("errcode")!=null){
            return null;
        }
        //String token = getToken();
        Map<String,String> params = new HashMap<>();
        params.put("access_token",getToken());
        params.put("openid",openid.get("openid"));
        params.put("lang","zh_CN");
        Map<String, String> stringStringMap = doGet(params, "https://api.weixin.qq.com/cgi-bin/user/info");
        System.out.println(stringStringMap);
        if(stringStringMap.get("headimgurl")==null){
            System.out.println(1112);
            return null;
        }
        return  stringStringMap;
    }
    public   String getToken(){
        String access_token = "";
        String grant_type = "client_credential";//获取access_token填写client_credential
        //这个url链接地址和参数皆不能变
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=" + grant_type + "&appid=" +appId + "&secret=" + secret;
        String access_token1 = (String)redisUtil.get("access_token");
        if(StringUtils.isNotEmpty(access_token1)){
            return access_token1;
        }
        try {
            URL urlGet = new URL(url);
            HttpURLConnection http = (HttpURLConnection) urlGet.openConnection();
            http.setRequestMethod("GET"); // 必须是get方式请求
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.setDoInput(true);
            System.setProperty("sun.net.client.defaultConnectTimeout", "30000");// 连接超时30秒
            System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒
            http.connect();
            InputStream is = http.getInputStream();
            int size = is.available();
            byte[] jsonBytes = new byte[size];
            is.read(jsonBytes);
            String message = new String(jsonBytes, "UTF-8");
            JSONObject demoJson = JSONObject.parseObject(message);
            System.out.println("JSON字符串：" + demoJson);
            access_token = demoJson.getString("access_token");
            if (StringUtils.isNotBlank(access_token)) {
                //String key = RedisDBase.BGY_MEMBER + "access_token";
                redisUtil.set("access_token", access_token, 7200);
            } else {
                return message + "微信返回错误提示";
            }
            is.close();
        } catch (Exception e) {
            log.error("call getAccessToken error",e);
        }
        return access_token;}



}
