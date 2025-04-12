package org.example.Generator;

import org.example.Exception.JSONSchemaGeneratorException;
import org.example.JSONString;
import org.example.JSONTN.*;
import org.example.JSONTN.Creators.NodeCreator;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class Generator {

    public static JSONTreeNode generateSchemaTree(JSONString json) throws JSONSchemaGeneratorException {
        Character nextChar = json.getNextCharOmitWhitespaces();

        if(nextChar == null)
            return null;

        return NodeCreator.createNode(nextChar, json);

    }


    public static JSONObjectTN processObject(JSONString json) throws JSONSchemaGeneratorException {
        JSONObjectTN newObjectNode = new JSONObjectTN(JSONTreeNodeType.OBJECT);

        while(true) {
            Character nextChar = json.getNextCharAndRemoveOmitWhitespaces();


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

            if(nextChar == ']'){
                json.getNextCharAndRemoveOmitWhitespaces();
                return newArrayNode;
            }

            newArrayNode.addItem(NodeCreator.createNode(nextChar, json));


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


    public static String generateSchemaString(JSONTreeNode treeNode, String schemaString/*, boolean isRecurrentCall*/) throws JSONSchemaGeneratorException {
        if(treeNode.isRoot() //generator drzewa nie ustawia nazwy dla roota
           && treeNode.getType() != JSONTreeNodeType.OBJECT  //prymitywny typ
           && treeNode.getType() != JSONTreeNodeType.ARRAY)
            return "{\n  \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n  \"type\": \"" + treeNode.getTypeAsString() + "\"\n}";

        if(treeNode.isRoot()) //root jest obiektem albo tablica
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
                if(current.getType() == JSONTreeNodeType.OBJECT || current.getType() == JSONTreeNodeType.ARRAY){
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


        if(treeNode.getType() == JSONTreeNodeType.ARRAY){
            schemaString = schemaString.concat("array\",\n\"items\": ");

            JSONTreeNode[] items = ((JSONArrayTN) treeNode).getItems().toArray(new JSONTreeNode[0]);

            if(items.length == 0){
                schemaString = schemaString.concat("{}\n}");
                return schemaString;
            }

            schemaString = schemaString.concat("[\n");

            int i = 0;
            while(i < items.length){
                JSONTreeNode current = items[i];

                if(current.getType() != JSONTreeNodeType.OBJECT
                   && current.getType() != JSONTreeNodeType.ARRAY){
                    schemaString = schemaString.concat("{\n\"type\": \"" + current.getTypeAsString() + "\"\n}");
                }

                if(current.getType() == JSONTreeNodeType.OBJECT || current.getType() == JSONTreeNodeType.ARRAY){
                    schemaString = generateSchemaString(current, schemaString);
                }


                if(i != items.length - 1)
                    schemaString = schemaString.concat(",");
                schemaString = schemaString.concat("\n");


                i++;
            }

            schemaString = schemaString.concat("]\n}");
            return schemaString;
        }

        throw new JSONSchemaGeneratorException("Unknown node type: " + treeNode.getType());
    }



}
