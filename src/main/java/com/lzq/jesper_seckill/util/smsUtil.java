package com.lzq.jesper_seckill.util;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class smsUtil {
    //查账户信息的http地址
    public static String APIKEY = "514ce1b7fb1e8b57341378f8af943598";
    public static String SIGN = "【云片网】";
    public static String TEMPLATE_CODE = "template_code";
    public static String TEMPLATE = "您的验证码是";
    //查账户信息的http地址
    private static String URI_GET_USER_INFO = "https://sms.yunpian.com/v2/user/get.json";
    //智能匹配模板发送接口的http地址
    private static String URI_SEND_SMS = "https://sms.yunpian.com/v2/sms/single_send.json";
    //模板发送接口的http地址
    private static String URI_TPL_SEND_SMS = "https://sms.yunpian.com/v2/sms/tpl_single_send.json";
    //发送语音验证码接口的http地址
    private static String URI_SEND_VOICE = "https://voice.yunpian.com/v2/voice/send.json";
    //绑定主叫、被叫关系的接口http地址
    private static String URI_SEND_BIND = "https://call.yunpian.com/v2/call/bind.json";
    //解绑主叫、被叫关系的接口http地址
    private static String URI_SEND_UNBIND = "https://call.yunpian.com/v2/call/unbind.json";
    //编码格式。发送编码格式统一用UTF-8
    private static String ENCODING = "UTF-8";
    private static RestTemplate restTemplate = new RestTemplate();
    /*
    * 取账户信息
    * */
    public static String getUserInfo(String apikey) throws IOException,
            URISyntaxException {
        Map< String, String > params = new HashMap< String, String >();
        params.put("apikey", apikey);
        return post(URI_GET_USER_INFO, params);
    }
    /*
    * 智能匹配模板接口发短信
    * */
    public static String sendSms(String apikey, String text, String mobile) throws IOException {
        Map<String,String> params = new HashMap<String,String>();
        params.put("apikey", apikey);
        params.put("text", text);
        params.put("mobile", mobile);
        return post(URI_SEND_SMS, params);
    }
    public static String tplSendSms(String apikey, long tpl_id, String tpl_value, String mobile) throws IOException {
        Map < String, String > params = new HashMap < String, String > ();
        params.put("apikey", apikey);
        params.put("tpl_id", String.valueOf(tpl_id));
        params.put("tpl_value", tpl_value);
        params.put("mobile", mobile);
        return post(URI_TPL_SEND_SMS, params);
    }
    public static String sendVoice(String apikey, String mobile, String code) {
        Map < String, String > params = new HashMap < String, String > ();
        params.put("apikey", apikey);
        params.put("mobile", mobile);
        params.put("code", code);
        return post(URI_SEND_VOICE, params);
    }
    public static String post(String url, Map<String,String> params) {
        MultiValueMap<String,String> params1 = new LinkedMultiValueMap<>();
        params.entrySet().forEach(param -> {
            params1.add(param.getKey(),param.getValue());
        });
        String responseBody = null;
        try {
            //发送post请求
            responseBody = restTemplate.postForObject(url, params1, String.class);
        }catch (HttpClientErrorException e){
            //异常处理
            e.printStackTrace();
        }
        return responseBody;
    }

}
