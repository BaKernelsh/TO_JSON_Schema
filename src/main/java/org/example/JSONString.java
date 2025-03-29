package org.example;

import org.example.Exception.JSONSchemaGeneratorException;
import org.example.JSONTN.JSONNumberTN;

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

    //zwraca nazwe property, i usuwa ja ze stringa razem z konczacym " albo '   wczesniej poczatkowy " (albo ') powinien byc usuniety
    public String getPropertyName(char endsWith) throws JSONSchemaGeneratorException {
        int indexOfEnd = json.indexOf(endsWith);
        if(indexOfEnd == -1)
            throw new JSONSchemaGeneratorException("Unterminated string");

        String propertyName = json.substring(0,indexOfEnd);
        json = json.substring(indexOfEnd+1);
        return propertyName;
    }


    public String getNull() throws JSONSchemaGeneratorException {
        json = json.trim();
        if(getNextChar() == 'n') {
            String nullString;
            if(json.length() >= 5) {
                nullString = json.substring(0, 5);
                json = json.substring(5);
            }
            else{
                nullString = json;
                json = "";
            }

            if(!nullString.equals("null")){
                throw new JSONSchemaGeneratorException("UnexpectedCharacter");
            }

            return nullString;
        }
        else{
            return null;
        }
    }

    public String getBoolean() throws JSONSchemaGeneratorException {
        json = json.trim();
        if(getNextChar() == 't'){
            String trueString;
            if(json.length() >= 4){
                trueString = json.substring(0,4);
                json = json.substring(4);
            }
            else{
                trueString = json;
                json = "";
            }

            if(!trueString.equals("true"))
                throw new JSONSchemaGeneratorException("Unexpected character");

            return trueString;
        }
        else if(getNextChar() == 'f'){
            String falseString;
            if(json.length() >= 5){
                falseString = json.substring(0,5);
                json = json.substring(5);
            }
            else{
                falseString = json;
                json = "";
            }

            if(!falseString.equals("true"))
                throw new JSONSchemaGeneratorException("Unexpected character");

            return falseString;
        }
        else{
            return null;
        }
    }

    public String getNumber(){
        json = json.trim();
        if(json.charAt(0) != '-' && !Character.isDigit(json.charAt(0)))
            return null;

        int indexAfterLastNumberCharacter = 0;

        for(int i=0; i< json.length(); i++){
            char currentChar = json.charAt(i);
            if(!(Character.isDigit(currentChar) || currentChar == '.' || currentChar == 'e' || currentChar == 'E' || currentChar == '-' || currentChar == '+')){
                indexAfterLastNumberCharacter = i;
                break;
            }
        }

        String numberString = json.substring(0, indexAfterLastNumberCharacter);
        json = json.substring(indexAfterLastNumberCharacter);
        return numberString;
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
