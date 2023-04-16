package com.chat.crazy;

import com.chat.crazy.handler.response.ResultStatusEnum;
import com.chat.crazy.util.OkHttpUtils;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class SmsClient {

    private final String url = "http://api01.monyun.cn:7901/sms/v2/std/single_send";
    private final String apikey = "60928d149f4651b584bb025ea6004c2b";

    private final Gson gson = new Gson();
    public ResultStatusEnum sendMsg(String phone, String code) {
        ResultStatusEnum res = ResultStatusEnum.SUCCESS;
        try {
            Map<String, String> map = new HashMap<>();
            map.put("apikey", apikey);
            map.put("mobile", phone);
            String content = "您的验证码是" + code + "，在10分钟内有效。如非本人操作请忽略本短信。";
            map.put("content", URLEncoder.encode(content, "GBK"));
            String result = OkHttpUtils.postJson(url, null, gson.toJson(map));
            log.info("手机：{}， 验证码：{}， 发送结果：{}", phone, code, result);
        } catch (Exception e) {
            res = ResultStatusEnum.FAIL;
            log.error("手机：{}， 验证码：{}, send phone code error:{}", phone, code, ExceptionUtils.getStackTrace(e));
        }
        return res;
    }
}
