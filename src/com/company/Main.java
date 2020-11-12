package com.company;

import org.json.*;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.TreeMap;

public class Main {
    public static void main(String[] args) throws IOException, JSONException {
        //получение данных
        URL url = new URL("https://api.vk.com/method/friends.get?user_id=137795470&fields=city&access_token=6a6f00e00e32891b7922d7c1ccb908a78de2c0f6b2abfe7a66ac9b634338b5b8c02848d4a1fe8f309d553&v=5.126");
        URLConnection yc = url.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        yc.getInputStream()));
        String inputLine = readAll(in);
        in.close();
        //чтение списка друзей и их количества
        JSONObject json = new JSONObject(inputLine);
        int count = (int) json.getJSONObject("response").get("count");
        JSONArray friends = json.getJSONObject("response").getJSONArray("items");
        //заполнение частотного словаря по городам
        Map<String, Double> filledDictionary = fillDictionary(friends, count);

        //вывод данных
        System.out.println("Статистика друзей по городам данного пользователя");
        for (String key : filledDictionary.keySet()) {
            System.out.printf("%s: %.2f%%\n", key, filledDictionary.get(key));
        }
    }

    private static Map<String, Double> fillDictionary(JSONArray friends, int countFriends) {
        Map<String, Double> result = new TreeMap<>();
        for (int i = 0; i < friends.length(); i++) {
            try { //обход списка друзей, получение названия города, запись в словарь
                String city = friends.getJSONObject(i).getJSONObject("city").get("title").toString();
                result.compute(city, (name, count) -> count == null ? 1 : count + 1);
            } catch (Exception ignored) {
            }
        } //перевод в процентное соотношение
        result.replaceAll((k, v) -> (result.get(k) / countFriends * 100));
        return result;
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
