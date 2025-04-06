package org.example.Generator;

import org.example.Exception.JSONSchemaGeneratorException;
import org.example.JSONString;
import org.example.JSONTN.*;

import java.util.ArrayList;

public class Generator {

    public static JSONTreeNode generateSchemaTree(JSONString json) throws JSONSchemaGeneratorException {
        Character nextChar = json.getNextCharOmitWhitespaces();

        if(nextChar == null)// throw end of json ?
            return null;
        //TODO zlikwidowac to
        switch(nextChar){
            case '0': case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
            case '-':
                String number = json.getNumber();
                JSONNumberTN newNumberNode = new JSONNumberTN(JSONTreeNodeType.NUMBER);
                newNumberNode.setValue(number);
                return newNumberNode;

            case 'n':
                json.getNull(); // jesli to nie będzie "null" to rzuci Exception
                return new JSONNullTN(JSONTreeNodeType.NULL);

            case 't': // jesli to nie będzie "true" to rzuci Exception
                json.getBoolean();
                JSONBooleanTN newTrueNode = new JSONBooleanTN(JSONTreeNodeType.BOOLEAN);
                newTrueNode.setValue(true);
                return newTrueNode;

            case 'f': // jesli to nie będzie "false" to rzuci Exception
                json.getBoolean();
                JSONBooleanTN newFalseNode = new JSONBooleanTN(JSONTreeNodeType.BOOLEAN);
                newFalseNode.setValue(false);
                return newFalseNode;

            case '"':
                json.getNextCharAndRemoveOmitWhitespaces();
                String string = json.getPropertyName();
                JSONStringTN newStringNode = new JSONStringTN(JSONTreeNodeType.STRING);
                newStringNode.setValue(string);
                return newStringNode;

            case '{':
                json.getNextCharAndRemoveOmitWhitespaces();
                JSONObjectTN newObjectNode = processObject(json);
                return newObjectNode;

            case '[':
                json.getNextCharAndRemoveOmitWhitespaces();
                JSONArrayTN newArrayNode = processArray(json);
                return newArrayNode;

            default:
                throw new JSONSchemaGeneratorException("Unexpected character: " + nextChar);
        }


    }

    public static JSONObjectTN processObject(JSONString json) throws JSONSchemaGeneratorException {
        JSONObjectTN newObjectNode = new JSONObjectTN(JSONTreeNodeType.OBJECT);

        while(true) {
            Character nextChar = json.getNextCharAndRemoveOmitWhitespaces();
            //JSONTreeNode newProperty;

            switch (nextChar) {
                case '}':
                    return newObjectNode;

                case '"':
                    String propertyName = json.getPropertyName();
                    if(json.getNextCharAndRemoveOmitWhitespaces() != ':')
                        throw new JSONSchemaGeneratorException("Unexpected character");

                    JSONTreeNode newProperty = generateSchemaTree(json);
                    if(newProperty == null)
                        throw new JSONSchemaGeneratorException("Unexpected end of JSON");
                    newProperty.setName(propertyName);
                    newObjectNode.addProperty(newProperty);

                    if(json.getNextCharOmitWhitespaces() == ','){
                        json.getNextCharAndRemoveOmitWhitespaces();
                        continue;
                    }
                    else if(json.getNextCharOmitWhitespaces() == '}'){
                       json.getNextCharAndRemoveOmitWhitespaces();
                       return newObjectNode;
                    }
                    else{
                        throw new JSONSchemaGeneratorException("Unexpected character");
                    }

                default :
                    throw new JSONSchemaGeneratorException("Unexpected character");

            }
        }
    }


    public static JSONArrayTN processArray(JSONString json) throws JSONSchemaGeneratorException {
        JSONArrayTN newArrayNode = new JSONArrayTN(JSONTreeNodeType.ARRAY);

        while(true){
            Character nextChar = json.getNextCharOmitWhitespaces();

            switch(nextChar){
                case ']':
                    json.getNextCharAndRemoveOmitWhitespaces();
                    return newArrayNode;

                case '0': case '1': case '2': case '3': case '4':
                case '5': case '6': case '7': case '8': case '9':
                case '-':
                    String number = json.getNumber();
                    JSONNumberTN newNumberNode = new JSONNumberTN(JSONTreeNodeType.NUMBER);
                    newNumberNode.setValue(number);
                    newArrayNode.addItem(newNumberNode);
                    break;

                case 'n':
                    json.getNull(); // jesli to nie będzie "null" to rzuci Exception
                    newArrayNode.addItem(new JSONNullTN(JSONTreeNodeType.NULL));
                    break;

                case 't': // jesli to nie będzie "true" to rzuci Exception
                    json.getBoolean();
                    JSONBooleanTN newTrueNode = new JSONBooleanTN(JSONTreeNodeType.BOOLEAN);
                    newTrueNode.setValue(true);
                    newArrayNode.addItem(newTrueNode);
                    break;

                case 'f': // jesli to nie będzie "false" to rzuci Exception
                    json.getBoolean();
                    JSONBooleanTN newFalseNode = new JSONBooleanTN(JSONTreeNodeType.BOOLEAN);
                    newFalseNode.setValue(false);
                    newArrayNode.addItem(newFalseNode);
                    break;

                case '"':
                    json.getNextCharAndRemoveOmitWhitespaces();
                    String string = json.getPropertyName();
                    JSONStringTN newStringNode = new JSONStringTN(JSONTreeNodeType.STRING);
                    newStringNode.setValue(string);
                    newArrayNode.addItem(newStringNode);
                    break;

                case '{':
                    json.getNextCharAndRemoveOmitWhitespaces();
                    JSONObjectTN newObjectNode = processObject(json);
                    newArrayNode.addItem(newObjectNode);
                    break;

                case '[':
                    json.getNextCharAndRemoveOmitWhitespaces();
                    JSONArrayTN newSubarrayNode = processArray(json);
                    newArrayNode.addItem(newSubarrayNode);
                    break;

                default:
                    throw new JSONSchemaGeneratorException("Unexpected character: " + nextChar);


            }

            if(json.getNextCharOmitWhitespaces() == ',') {
                json.getNextCharAndRemoveOmitWhitespaces();
            }
            else if(json.getNextCharOmitWhitespaces() == ']'){
                json.getNextCharAndRemoveOmitWhitespaces();
                return newArrayNode;
            }
            else{
                throw new JSONSchemaGeneratorException("Unexpected character");
            }

        }



    }


    public static String generateSchemaString(JSONTreeNode treeNode, String schemaString/*, boolean isRecurrentCall*/){
        if(/*!isRecurrentCall*/  treeNode.getName() == null //generator drzewa nie ustawia nazwy dla roota
           && treeNode.getType() != JSONTreeNodeType.OBJECT  //prymitywny typ
           && treeNode.getType() != JSONTreeNodeType.ARRAY)
            return "{\n  \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n  \"type\": \"" + treeNode.getTypeAsString() + "\"\n}";

        if(treeNode.getName() == null) //root jest obiektem albo tablica
            schemaString = schemaString.concat("{\n\"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n\"type\": \"");
        else //to nie root
            schemaString = schemaString.concat("{\n\"type\": \"");


        if(treeNode.getType() == JSONTreeNodeType.OBJECT){
            schemaString = schemaString.concat("object\",\n\"properties\": {\n");

            ArrayList<JSONTreeNode> properties = ((JSONObjectTN) treeNode).getProperties();
            if( properties.isEmpty() ){
                schemaString = schemaString.concat("},\n\"required\":[]\n}");
                return schemaString;
            }

            int i = 0;
            while(i < properties.size()){
                JSONTreeNode current = properties.get(i);
                if(current.getType() != JSONTreeNodeType.OBJECT
                   && current.getType() != JSONTreeNodeType.ARRAY){
                    schemaString = schemaString.concat("\"" + current.getName() + "\": {\n\"type\": \"" +
                                                        current.getType().toString().toLowerCase() + "\"\n}");
                }
                if(current.getType() == JSONTreeNodeType.OBJECT){
                    schemaString = schemaString.concat("\"" + current.getName() + "\": ");
                    schemaString = generateSchemaString(current, schemaString);
                }




                if(i != properties.size() - 1)
                    schemaString = schemaString.concat(",");
                schemaString = schemaString.concat("\n");
                i++;
            }

            String propertyNames = ((JSONObjectTN) treeNode).getPropertyNamesAsString();
            schemaString = schemaString.concat("},\n\"required\":[" + propertyNames + "]\n}");
            return schemaString;
        }

            //if(treeNode.getName() == null)

            return "";
        //}
    }



}
