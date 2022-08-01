package io.github.fvarrui.javapackager.utils.updater;

import com.google.gson.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Json {

    /**
     * Reads/Parses the provided String to a {@link JsonElement}.
     */
    public static JsonElement from(String s){
        return JsonParser.parseString(s);
    }

    /**
     * Writes/Parses the provided {@link JsonElement} to a String.
     */
    public static String toString(JsonElement el){
        return new Gson().toJson(el);
    }

    /**
     * Returns the json-element. This can be a json-array or a json-object.
     *
     * @param url The url which leads to the json file.
     * @return JsonElement
     * @throws Exception When status code other than 200.
     */
    public static JsonElement fromUrl(String url) throws IOException {
        HttpURLConnection con = null;
        JsonElement element;
        try {
            con = (HttpURLConnection) new URL(url).openConnection();
            con.addRequestProperty("User-Agent", "JavaPackager");
            con.setConnectTimeout(1000);
            con.connect();

            if (con.getResponseCode() == 200) {
                try (InputStreamReader inr = new InputStreamReader(con.getInputStream())) {
                    element = JsonParser.parseReader(inr);
                }
            } else {
                throw new IOException("error: "+con.getResponseCode()+" message: \""+con.getResponseMessage()+ "\" url: " + url);
            }
        } catch (IOException e) {
            if (con != null) con.disconnect();
            throw e;
        } finally {
            if (con != null) con.disconnect();
        }
        return element;
    }

    public static JsonArray fromUrlAsJsonArray(String url) throws IOException {
        JsonElement element = fromUrl(url);
        if (element != null && element.isJsonArray()) {
            return element.getAsJsonArray();
        } else {
            throw new IOException("Its not a json array! Check it out -> " + url);
        }
    }

    /**
     * Turns a JsonArray with its objects into a list.
     *
     * @param url The url where to find the json file.
     * @return A list with JsonObjects or null if there was a error with the url.
     */
    public static List<JsonObject> fromUrlAsList(String url) throws IOException {
        List<JsonObject> objectList = new ArrayList<>();
        JsonElement element = fromUrl(url);
        if (element != null && element.isJsonArray()) {
            final JsonArray ja = element.getAsJsonArray();
            for (int i = 0; i < ja.size(); i++) {
                JsonObject jo = ja.get(i).getAsJsonObject();
                objectList.add(jo);
            }
            return objectList;
        } else {
            throw new IOException("Its not a json array! Check it out -> " + url);
        }
    }

    /**
     * Gets a single JsonObject.
     *
     * @param url The url where to find the json file.
     * @return A JsonObject or null if there was a error with the url.
     */
    public static JsonObject fromUrlAsObject(String url) throws IOException {
        JsonElement element = fromUrl(url);
        if (element != null && element.isJsonObject()) {
            return element.getAsJsonObject();
        } else {
            throw new IOException("Its not a json object! Check it out -> " + url);
        }
    }

}
