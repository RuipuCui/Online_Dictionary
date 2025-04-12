import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageProtocol {

    public static String queryMessage(String word){
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("type", "query");
        queryMap.put("word", word);

        Gson gson = new Gson();
        return gson.toJson(queryMap);
    }

    public static String queryReply(List<String> meanings){
        Gson gson = new Gson();

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("meanings", gson.toJsonTree(meanings));

        return gson.toJson(jsonObject);
    }

    public static String getMeaningFromReply(String queryReply){
        if(queryReply.contains("ERROR")){
            return queryReply;
        }
        Gson gson = new Gson();
        Map<String, List<String>> meaningMap = gson.fromJson(queryReply, new TypeToken<Map<String, List<String>>>(){}.getType());
        List<String> meanings = meaningMap.get("meanings");
        return String.join("\n", meanings);
    }

    public static String addMessage(String word, List<String> meanings){
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("type", "add");
        queryMap.put("word", word);
        queryMap.put("meanings", meanings);

        Gson gson = new Gson();
        return gson.toJson(queryMap);
    }

    public static String removeMessage(String word){
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("type", "remove");
        queryMap.put("word", word);

        Gson gson = new Gson();
        return gson.toJson(queryMap);
    }

    public static String addMeaningsMessage(String word, String newMeaning){
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("type", "add meaning");
        queryMap.put("word", word);
        queryMap.put("meaning", newMeaning);

        Gson gson = new Gson();
        return gson.toJson(queryMap);
    }

    public static String updateMeaningMessage(String word, String originalMeaning, String newMeaning){
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("type", "update meaning");
        queryMap.put("word", word);
        queryMap.put("original meaning", originalMeaning);
        queryMap.put("new meaning", newMeaning);

        Gson gson = new Gson();
        return gson.toJson(queryMap);
    }

    public static Map<String, Object> processJsonMessage(String json){
        Gson gson = new Gson();
        return gson.fromJson(json, Map.class);
    }

    public static String errorMessage(String err){
        Gson gson = new Gson();

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("ERROR", gson.toJsonTree(err));

        return gson.toJson(jsonObject);
    }

    public static String successMessage(String success){
        Gson gson = new Gson();

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("SUCCESS", gson.toJsonTree(success));

        return gson.toJson(jsonObject);
    }





}
