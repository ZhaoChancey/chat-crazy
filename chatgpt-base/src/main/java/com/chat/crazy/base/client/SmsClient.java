package com.chat.crazy.base.client;

import com.chat.crazy.base.handler.response.ResultCode;
import com.chat.crazy.base.util.OkHttpUtils;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class SmsClient {

    private final String url = "http://api01.monyun.cn:7901/sms/v2/std/single_send";
    private final String apikey = "60928d149f4651b584bb025ea6004c2b";

    private final Gson gson = new Gson();
    public ResultCode sendMsg(String phone, String code) {
        ResultCode res = ResultCode.SUCCESS;
        try {
            Map<String, String> map = new HashMap<>();
            map.put("apikey", apikey);
            map.put("mobile", phone);
            String content = "您的验证码是" + code + "，在10分钟内有效。如非本人操作请忽略本短信。";
            map.put("content", URLEncoder.encode(content, "GBK"));
            String result = OkHttpUtils.postJson(url, null, gson.toJson(map));
            log.info("手机：{}， 验证码：{}， 发送结果：{}", phone, code, result);
        } catch (Exception e) {
            res = ResultCode.INTERNAL_SERVER_ERROR;
            log.error("手机：{}， 验证码：{}, send phone code error:{}", phone, code, ExceptionUtils.getStackTrace(e));
        }
        return res;
    }
}
