package com.chat.crazy.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtils {

    public static String toString(Object object) {
        return new Gson().toJson(object);
    }

    public static <T> String toString(List<T> objects) {
        return new Gson().toJson(objects);
    }

    public static <K, V> String toString(Map<K, V> map) {
        return new Gson().toJson(map);
    }

    public static <T> T string2Object(String jsonStr, Class<T> clazz) {
        T obj = new Gson().fromJson(jsonStr, clazz);
        return obj;
    }

    public static <T> T string2Object(JsonElement jsonElement, Class<T> clazz) {
        T obj = new Gson().fromJson(jsonElement, clazz);
        return obj;
    }

    public static <T> List<T> string2ObjArr(String jsonStr, Class<T> clazz) {
        if(jsonStr == null || jsonStr.equals("")){
            jsonStr = "[]";
        }
        List<T> list = new ArrayList<>();
        Gson gson = new Gson();
        JsonArray arry = new JsonParser().parse(jsonStr).getAsJsonArray();
        for (JsonElement jsonElement : arry) {
            list.add(gson.fromJson(jsonElement, clazz));
        }
        return list;
    }

    public static <K, V> Map<K, V> string2ObjMap(String jsonStr) {
        java.lang.reflect.Type type = new TypeToken<Map<K, V>>() {}.getType();
        Map<K, V> map = new Gson().fromJson(jsonStr, type);
        return map;
    }


    public static <K, V> Map<K, V> string2ObjMap(String jsonStr, Class<K> kClazz, Class<V> vClazz) {
        java.lang.reflect.Type type = TypeToken.getParameterized(Map.class, kClazz, vClazz).getType();
        return new Gson().fromJson(jsonStr, type);
    }

    public static <V> Map<String, List<V>> string2ListValueMap(String jsonStr, Class<V> vClazz) {
        Map<String, List<V>> result = new HashMap<>();
        JsonObject jsonObject = new JsonParser().parse(jsonStr).getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String k = entry.getKey();
            List<V> list = string2ObjArr(entry.getValue().toString(), vClazz);
            result.put(k, list);
        }
        return result;
    }

}

