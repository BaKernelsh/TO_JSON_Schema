package org.example;

public class JSONString {

    private String json;

    //pobiera nastepny character ignorujac whitespace
    public Character getNextCharOmitWhitespaces(){
        json = json.trim();
        if(json.length() > 0)
            return json.charAt(0);
        else
            return null;
    }

    //pobiera nastepny character ignorujac whitespace i usuwa go ze stringa
    public Character getNextCharAndRemoveOmitWhitespaces(){
        json = json.trim();
        if(json.length() > 0) {
            Character toReturn = json.charAt(0);
            json = json.substring(1);
            return toReturn;
        }
        else
            return null;
    }

    //pobiera nastepny character ignorujac whitespace (niczego nie usuwa ze stringa)
    public Character getNextChar(){
        for(int i=0; i<json.length(); i++){
            if(Character.isWhitespace(json.charAt(i)))
                continue;
            else{
                return json.charAt(i);
            }
        }
        return null;
    }



    public JSONString(String json) {
        this.json = json.trim();
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
