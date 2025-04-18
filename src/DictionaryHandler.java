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
        Map<String, Object> jsonMap = MessageProtocol.processJsonMessage(json);
        String type = (String) jsonMap.get("type");

        return switch (type) {
            case "query" -> queryHandler(jsonMap, dictionary);
            case "add" -> addHandler(jsonMap, dictionary, dictFile);
            case "remove" -> removeHandler(jsonMap, dictionary, dictFile);
            case "add meaning" -> addMeaningHandler(jsonMap, dictionary, dictFile);
            case "update meaning" -> updateMeaningHandler(jsonMap, dictionary, dictFile);
            default -> MessageProtocol.errorMessage("unsupported functionality");
        };

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
            return MessageProtocol.errorMessage("the word does not exit");
        }

        return MessageProtocol.queryReply(meanings);
    }

    private static String addHandler(Map<String, Object> jsonMap, Map<String, List<String>> dictionary, String dictFile){
        String word = (String) jsonMap.get("word");
        if(dictionary.containsKey(word)){
            return MessageProtocol.errorMessage("the word already exits");
        }
        List<String> meanings = (List<String>) jsonMap.get("meanings");
        dictionary.put(word, meanings);

        try (FileWriter writer = new FileWriter(dictFile)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create(); // for readable output
            gson.toJson(dictionary, writer);
            return MessageProtocol.successMessage("new word added");
        } catch (IOException e) {
            throw new RuntimeException("Failed to update dictionary file", e);
        }

    }

    private static String removeHandler(Map<String, Object> jsonMap, Map<String, List<String>> dictionary, String dictFile){
        String word = (String) jsonMap.get("word");
        if(!dictionary.containsKey(word)){
            return MessageProtocol.errorMessage("the word does not exit or already removed");
        }

        dictionary.remove(word);

        try (FileWriter writer = new FileWriter(dictFile)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create(); // for readable output
            gson.toJson(dictionary, writer);
            return MessageProtocol.successMessage("word removed");
        } catch (IOException e) {
            throw new RuntimeException("Failed to update dictionary file", e);
        }
    }

    private static String addMeaningHandler(Map<String, Object> jsonMap, Map<String, List<String>> dictionary, String dictFile){
        String word = (String) jsonMap.get("word");
        if(!dictionary.containsKey(word)){
            return MessageProtocol.errorMessage("the word does not exits");
        }

        String newMeaning = (String) jsonMap.get("meaning");
        List<String> meanings = dictionary.get(word);
        if(meanings.contains(newMeaning)){
            return MessageProtocol.errorMessage("the meaning already exits");
        }
        meanings.add(newMeaning);

        try (FileWriter writer = new FileWriter(dictFile)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create(); // for readable output
            gson.toJson(dictionary, writer);
            return MessageProtocol.successMessage("new meaning added");
        } catch (IOException e) {
            throw new RuntimeException("Failed to update dictionary file", e);
        }

    }

    private static String updateMeaningHandler(Map<String, Object> jsonMap, Map<String, List<String>> dictionary, String dictFile){
        String word = (String) jsonMap.get("word");
        if(!dictionary.containsKey(word)){
            return MessageProtocol.errorMessage("the word does not exits");
        }

        String originalMeaning = (String) jsonMap.get("original meaning");
        String newMeaning = (String) jsonMap.get("new meaning");
        List<String> meanings = dictionary.get(word);
        if(!meanings.contains(originalMeaning)){
            return MessageProtocol.errorMessage("the meaning does not exits");
        }

        int index = meanings.indexOf(originalMeaning);
        if(newMeaning.equals("")){
            meanings.remove(index);
        }else {
            meanings.set(index, newMeaning);
        }

        try (FileWriter writer = new FileWriter(dictFile)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create(); // for readable output
            gson.toJson(dictionary, writer);
            return MessageProtocol.successMessage("new meaning updated");
        } catch (IOException e) {
            throw new RuntimeException("Failed to update dictionary file", e);
        }

    }



}
