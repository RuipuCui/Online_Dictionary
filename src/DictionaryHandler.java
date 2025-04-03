import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.io.FileReader;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class DictionaryHandler {

    public static synchronized String Handler(String json, String dictFile){
        //convert the dictionary.json file into map data structure
        Map<String, List<String>> dictionary = readDictionary(dictFile);

        //convert the json message from the client into map data structure
        Map<String, Object> jsonMap = MessageProtocal.readMessage(json);
        String type = (String) jsonMap.get("type");

        if(type.equals("query")){
            return queryHandler(jsonMap, dictionary);
        }else if(type.equals("add")){
            return addHandler(jsonMap, dictionary, dictFile);
        }else if(type.equals("remove")){
            return removeHandler(jsonMap, dictionary, dictFile);
        }else{
            return MessageProtocal.errorMessage("unsupported functionality");
        }

    }

    private static Map<String, List<String>> readDictionary(String dictFile){
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, List<String>>>() {}.getType();
        try {
            return gson.fromJson(new FileReader(dictFile), type);
        }catch(IOException e){
            throw new RuntimeException("Cannot load dictionary file", e);
        }
    }

    private static String queryHandler(Map<String, Object> jsonMap, Map<String, List<String>> dictionary){
        String word = (String) jsonMap.get("word");
        List<String> meanings = dictionary.get(word);
        if(meanings == null){
            return MessageProtocal.errorMessage("the word does not exit");
        }

        return MessageProtocal.queryReply(meanings);
    }

    private static String addHandler(Map<String, Object> jsonMap, Map<String, List<String>> dictionary, String dictFile){
        String word = (String) jsonMap.get("word");
        if(dictionary.containsKey(word)){
            return MessageProtocal.errorMessage("the word already exits");
        }
        List<String> meanings = (List<String>) jsonMap.get("meanings");
        dictionary.put(word, meanings);

        try (FileWriter writer = new FileWriter(dictFile)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create(); // for readable output
            gson.toJson(dictionary, writer);
            return MessageProtocal.successMessage("new word added");
        } catch (IOException e) {
            throw new RuntimeException("Failed to update dictionary file", e);
        }

    }

    private static String removeHandler(Map<String, Object> jsonMap, Map<String, List<String>> dictionary, String dictFile){
        String word = (String) jsonMap.get("word");
        if(!dictionary.containsKey(word)){
            return MessageProtocal.errorMessage("the word does not exit or already removed");
        }

        dictionary.remove(word);

        try (FileWriter writer = new FileWriter(dictFile)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create(); // for readable output
            gson.toJson(dictionary, writer);
            return MessageProtocal.successMessage("word removed");
        } catch (IOException e) {
            throw new RuntimeException("Failed to update dictionary file", e);
        }
    }

}
