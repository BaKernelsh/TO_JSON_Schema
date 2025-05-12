package org.example.Generator;

import org.example.Exception.JSONSchemaGeneratorException;
import org.example.JSONString;
import org.example.JSONTN.*;
import org.example.JSONTN.Creators.NodeCreator;

import java.util.ArrayList;


public class Generator { //TODO builder z allConfigurationTrue, allConfigurationFalse
    private AssertionConfiguration assertionConfig = new AssertionConfiguration();
    private SchemaStringElements schemaStringElements = new SchemaStringElements();


    public JSONTreeNode generateSchemaTree(JSONString json) throws JSONSchemaGeneratorException {
        Character nextChar = json.getNextCharOmitWhitespaces();

        if(nextChar == null)
            return null;

        return NodeCreator.createNode(nextChar, json, this);

    }


    public JSONObjectTN processObject(JSONString json) throws JSONSchemaGeneratorException {
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

                    Character commaOrClosingBracket = json.getNextCharOmitWhitespaces();
                    if(commaOrClosingBracket == null)
                        throw new JSONSchemaGeneratorException("Unexpected end of JSON");
                    if(commaOrClosingBracket == ','){
                        json.getNextCharAndRemoveOmitWhitespaces();
                        continue;
                    }
                    else if(commaOrClosingBracket == '}'){
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


    public JSONArrayTN processArray(JSONString json) throws JSONSchemaGeneratorException {
        JSONArrayTN newArrayNode = new JSONArrayTN(JSONTreeNodeType.ARRAY);

        while(true){
            Character nextChar = json.getNextCharOmitWhitespaces();

            if(nextChar == ']'){
                json.getNextCharAndRemoveOmitWhitespaces();
                return newArrayNode;
            }

            JSONTreeNode newItem = NodeCreator.createNode(nextChar, json, this);
            newItem.setName("item");
            newArrayNode.addItem(newItem);

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

    public String generateSchemaString(JSONTreeNode treeNode, String schemaString, int nestLevel) throws JSONSchemaGeneratorException {
        return generateSchemaString(treeNode, schemaString, nestLevel, false);
    }

    public String generateSchemaString(JSONTreeNode treeNode, String schemaString, int nestLevel, boolean subarray) throws JSONSchemaGeneratorException {
        if(treeNode.isRoot() //generator drzewa nie ustawia nazwy dla roota
           && treeNode.getType() != JSONTreeNodeType.OBJECT  //prymitywny typ
           && treeNode.getType() != JSONTreeNodeType.ARRAY) {
            return schemaStringElements.primitiveTypeAsRoot(treeNode, assertionConfig);
            //schemaString = assertionConfig.addAssertionsToSchemaString(treeNode, schemaString, schemaStringElements.indentationBeforeAssertions(nestLevel+1));
        }

        if(treeNode.isRoot()) //root jest obiektem albo tablica
            schemaString = schemaString.concat(schemaStringElements.objectOrArrayAsRoot(nestLevel));
        else //to nie root
            schemaString = schemaString.concat(schemaStringElements.typeKeywordAndColon(nestLevel));


        if(treeNode.getType() == JSONTreeNodeType.OBJECT){
            schemaString = schemaString.concat(schemaStringElements.objectTypeAndPropertiesKeyword(nestLevel));

            ArrayList<JSONTreeNode> properties = ((JSONObjectTN) treeNode).getProperties();
            if( properties.isEmpty() ){
                schemaString = schemaString.concat(schemaStringElements.propertiesClosingAndEmptyPropertiesAndObjectClosing(nestLevel));
                return schemaString;
            }

            int i = 0;
            while(i < properties.size()){
                JSONTreeNode current = properties.get(i);
                if(current.getType() != JSONTreeNodeType.OBJECT
                   && current.getType() != JSONTreeNodeType.ARRAY){
                    schemaString = schemaString.concat(schemaStringElements.primitivePropertyNameAndType(nestLevel, current));
                    // nestLevel+1 zeby dla nestLevel==0 dobrze generowalo wciecie
                    schemaString = assertionConfig.addAssertionsToSchemaString(current, schemaString, schemaStringElements.indentationBeforeAssertions(nestLevel+1));
                    schemaString = schemaString.concat(schemaStringElements.primitivePropertyClosing(nestLevel));
                }
                if(current.getType() == JSONTreeNodeType.OBJECT || current.getType() == JSONTreeNodeType.ARRAY){
                    schemaString = schemaString.concat(schemaStringElements.objectOrArrayPropertyName(nestLevel, current));
                    schemaString = generateSchemaString(current, schemaString, nestLevel+1, false);
                }


                if(i != properties.size() - 1)
                    schemaString = schemaString.concat(",");
                schemaString = schemaString.concat("\n");
                i++;
            }


            schemaString = schemaString.concat(schemaStringElements.propertiesClosing(nestLevel));
            schemaString = assertionConfig.addAssertionsToSchemaString(treeNode, schemaString, schemaStringElements.indentationBeforeAssertions(nestLevel));
            schemaString = schemaString.concat(schemaStringElements.objectClosing(nestLevel));
            return schemaString;
        }


        if(treeNode.getType() == JSONTreeNodeType.ARRAY){
            schemaString = schemaString.concat(schemaStringElements.arrayTypeAndItemsKeyword(nestLevel, subarray));

            JSONTreeNode[] items = ((JSONArrayTN) treeNode).getItems().toArray(new JSONTreeNode[0]);

            if(items.length == 0){
                schemaString = schemaString.concat(schemaStringElements.emptyArrayItemList());
                schemaString = schemaString.concat(schemaStringElements.arrayClosing(nestLevel));
                return schemaString;
            }

            schemaString = schemaString.concat("[\n");

            int i = 0;
            while(i < items.length){
                JSONTreeNode current = items[i];

                if(current.getType() != JSONTreeNodeType.OBJECT
                   && current.getType() != JSONTreeNodeType.ARRAY){
                    schemaString = schemaString.concat(schemaStringElements.primitiveArrayItem(nestLevel, current));
                    schemaString = assertionConfig.addAssertionsToSchemaString(current, schemaString, schemaStringElements.indentationBeforeAssertions(nestLevel+1));
                    schemaString = schemaString.concat(schemaStringElements.primitiveArrayItemClosing(nestLevel));
                }

                if(current.getType() == JSONTreeNodeType.OBJECT || current.getType() == JSONTreeNodeType.ARRAY){
                    if(current.getType() == JSONTreeNodeType.ARRAY){
                        schemaString = schemaString.concat(schemaStringElements.indentationForSubarray(nestLevel));
                        schemaString = generateSchemaString(current, schemaString, nestLevel+1,true);
                    }
                    else
                        schemaString = generateSchemaString(current, schemaString, nestLevel+1,false);
                }


                if(i != items.length - 1)
                    schemaString = schemaString.concat(",");
                schemaString = schemaString.concat("\n");


                i++;
            }

            //schemaString = schemaString.concat("]\n}");
            schemaString = schemaString.concat(schemaStringElements.itemListClosing(nestLevel));
            schemaString = schemaString.concat(schemaStringElements.arrayClosing(nestLevel));

            return schemaString;
        }

        throw new JSONSchemaGeneratorException("Unknown node type: " + treeNode.getType());
    }


    public void setAssertionConfiguration(AssertionConfiguration configuration){
        assertionConfig = configuration;
    }

    public Generator(){

    }

    public Generator(AssertionConfiguration assertionConfig){
        this.assertionConfig = assertionConfig;
        this.schemaStringElements.setFormattingReadable();
    }

    public Generator(AssertionConfiguration assertionConfig, boolean compactFormatting){
        this.assertionConfig = assertionConfig;
        if(compactFormatting)
            this.schemaStringElements.setFormattingCompact();
        else
            this.schemaStringElements.setFormattingReadable();
    }

}
