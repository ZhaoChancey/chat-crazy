package com.chat.crazy.util;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class OkHttpUtils {

    public static String get(String url, Map<String, String> headers, Map<String, String> params) throws IOException {
        if (params != null && params.size() > 0) {
            int i = 0;
            StringBuilder urlFormat = new StringBuilder(url);
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                Object val = entry.getValue();
                if (i == 0) {
                    urlFormat.append(String.format("?%s=%s", key, val));
                } else {
                    urlFormat.append(String.format("&%s=%s", key, val));
                }
                i++;
            }
            url = urlFormat.toString();
        }
        OkHttpClient Client = new OkHttpClient();
        Request.Builder request = new Request.Builder();
        request.url(url);
        request.get();
        if (headers != null && headers.size() > 0) {
            request.headers(Headers.of(headers));
        }
        Call call = Client.newCall(request.build());
        Response response = call.execute();
        String rsp = response.body().string();
        return rsp;
    }

    public static <T> T get(String url, Map<String, String> headers, Map<String, String> params, Class<T> clazz) throws IOException {
        String rspStr = get(url, headers, params);
        T rsp = JsonUtils.string2Object(rspStr, clazz);
        return rsp;
    }

    public static String post(String url, Map<String, String> headers, Map<String, String> params) throws IOException {
        OkHttpClient Client = new OkHttpClient();
        FormBody.Builder body = new FormBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            body.add(entry.getKey(), entry.getValue());
        }
        Request.Builder request = new Request.Builder()
                .url(url)
                .post(body.build());
        if (headers != null && headers.size() > 0) {
            request.headers(Headers.of(headers));
        }
        Call call = Client.newCall(request.build());
        Response response = call.execute();
        String rsp = response.body().string();
        return rsp;
    }

    public static <T> T post(String url, Map<String, String> headers, Map<String, String> params, Class<T> clazz) throws IOException {
        String rspStr = post(url, headers, params);
        T rsp = JsonUtils.string2Object(rspStr, clazz);
        return rsp;
    }

    public static String postJson(String url, Map<String, String> headers, String jsonStr) throws IOException {
        OkHttpClient Client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, jsonStr);
        Request.Builder request = new Request.Builder()
                .url(url)
                .post(body);
        if (headers != null && headers.size() > 0) {
            request.headers(Headers.of(headers));
        }
        Call call = Client.newCall(request.build());
        Response response = call.execute();
        String rsp = response.body().string();
        log.info("headers:{}", response.headers());
        return rsp;
    }

    public static <T> T postJson(String url, Map<String, String> headers, String jsonStr, Class<T> clazz) throws IOException {
        String rspStr = postJson(url, headers, jsonStr);
        log.info("postJson rsp:{}", rspStr);
        T rsp = JsonUtils.string2Object(rspStr, clazz);
        return rsp;
    }

    public static String put(String url, Map<String, String> headers, Map<String, String> params) throws IOException {
        OkHttpClient Client = new OkHttpClient();
        FormBody.Builder body = new FormBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            body.add(entry.getKey(), entry.getValue());
        }
        Request.Builder request = new Request.Builder()
                .url(url)
                .put(body.build());
        if (headers != null && headers.size() > 0) {
            request.headers(Headers.of(headers));
        }
        Call call = Client.newCall(request.build());
        Response response = call.execute();
        String rsp = response.body().string();
        return rsp;
    }

    public static <T> T put(String url, Map<String, String> headers, Map<String, String> params, Class<T> clazz) throws IOException {
        String rspStr = put(url, headers, params);
        T rsp = JsonUtils.string2Object(rspStr, clazz);
        return rsp;
    }

    public static String putJson(String url, Map<String, String> headers, String jsonStr) throws IOException {
        OkHttpClient Client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, jsonStr);
        Request.Builder request = new Request.Builder()
                .url(url)
                .put(body);
        if (headers != null && headers.size() > 0) {
            request.headers(Headers.of(headers));
        }
        Call call = Client.newCall(request.build());
        Response response = call.execute();
        String rsp = response.body().string();
        return rsp;
    }

    public static <T> T putJson(String url, Map<String, String> headers, String jsonStr, Class<T> clazz) throws IOException {
        String rspStr = putJson(url, headers, jsonStr);
        T rsp = JsonUtils.string2Object(rspStr, clazz);
        return rsp;
    }
}
