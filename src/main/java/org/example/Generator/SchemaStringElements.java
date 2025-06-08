package org.example.Generator;

import org.example.Exception.JSONSchemaGeneratorException;
import org.example.JSONTN.JSONTreeNode;


public class SchemaStringElements {

    private boolean compact;

    public void setFormattingCompact() {
        compact = true;
    }

    public void setFormattingReadable() {
        compact = false;
    }

    private String indentation(int nestLevel){
        String indentation = "";
        for(int i=0; i<nestLevel+1; i++)
            indentation = indentation.concat("  ");
        return indentation;
    }

    public String primitiveTypeAsRoot(JSONTreeNode treeNode, AssertionConfiguration assertionConfig) throws JSONSchemaGeneratorException {
        if(compact) {
            //return "{\"$schema\":\"https://json-schema.org/draft/2020-12/schema\",\"type\":\"" + treeNode.getTypeAsString() + "\"}";
            String schemaString = "{\"$schema\":\"https://json-schema.org/draft/2020-12/schema\",\"type\":\"" + treeNode.getTypeAsString() + "\"";
            schemaString = assertionConfig.addAssertionsToSchemaString(treeNode, schemaString, this.indentationBeforeAssertions(0, treeNode)) + "}";
            return schemaString;
        }
        //schemaString = assertionConfig.addAssertionsToSchemaString(treeNode, schemaString, schemaStringElements.indentationBeforeAssertions(nestLevel+1));

        else {
            //return "{\n  \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n  \"type\": \"" + treeNode.getTypeAsString() + "\"\n}";}
            String schemaString = "{\n  \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n  \"type\": \"" + treeNode.getTypeAsString() + "\"";
            schemaString = assertionConfig.addAssertionsToSchemaString(treeNode, schemaString, this.indentationBeforeAssertions(0, treeNode)) + "\n}";
            return schemaString;
        }
    }

    public String objectOrArrayAsRoot(int nestLevel){
        if(compact)
           return "{\"$schema\":\"https://json-schema.org/draft/2020-12/schema\",\"type\":\"";
        else{
           String indentation = indentation(nestLevel);
           return "{\n"+indentation+"\"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n"+indentation+"\"type\": \"";
        }
    }

    public String openingAndTypeKeywordAndColon(int nestLevel){
        if(compact)
            return "{\"type\":\"";
        else {
            String indentation = assertionsIndentation(nestLevel);
            return "{\n"+indentation+"\"type\": \"";
        }
    }

    private String assertionsIndentation(int nestLevel) {
        int whitespacesNumber = (2*nestLevel + 1) * 2;

        String indentation = "";
        for(int i=0; i<whitespacesNumber; i++)
            indentation = indentation.concat(" ");
        return indentation;
    }

    public String objectTypeAndPropertiesKeyword(int nestLevel){
        if(compact)
            return "object\",\"properties\":{";
        else{
            String indentation = assertionsIndentation(nestLevel);
            return "object\",\n"+indentation+"\"properties\": {\n";
        }
    }

    public String propertiesClosingAndEmptyPropertiesAndObjectClosing(int nestLevel){
        if(compact)
            return "},\"required\":[]}";
        else{
            String indentation = assertionsIndentation(nestLevel);
            String closingBracketIndent = assertionsIndentation(nestLevel);
            closingBracketIndent = closingBracketIndent.substring(0, closingBracketIndent.length()-2);

            return indentation+"},\n"+indentation+"\"required\": []\n"+ closingBracketIndent +"}"; //zle
        }
    }

    public String primitivePropertyNameAndType(int nestLevel, JSONTreeNode current){
        if(compact)
            return "\"" + current.getName() + "\":{\"type\":\"" +
                    current.getTypeAsString() + "\"";
        else{
            String nameIndent = propertyNameIndentation(nestLevel);
            String assertionsIndent = nameIndent + "  ";
            return nameIndent+"\"" + current.getName() + "\": {\n"+assertionsIndent+"\"type\": \"" +
                    current.getTypeAsString() + "\"";
        }
    }



    public String objectOrArrayPropertyName(int nestLevel, JSONTreeNode current){
        if(compact)
            return "\"" + current.getName() + "\":";
        else{
            String indentation = propertyNameIndentation(nestLevel);
            return indentation+"\"" + current.getName() + "\": ";
        }
    }

    private String propertyNameIndentation(int nestLevel) {
        int whitespacesNumber = (nestLevel+1) * 4;

        String indentation = "";
        for(int i=0; i<whitespacesNumber; i++)
            indentation = indentation.concat(" ");

        return indentation;
    }

    public String primitivePropertyClosing(int nestLevel){
        if(compact)
            return "}";
        else{
            return "\n"+ propertyNameIndentation(nestLevel) + "}";
        }
    }

    public String propertiesClosing(int nestLevel){
        if(compact)
            return "}";
        else{
            String indentation = indentation(nestLevel);
            if(nestLevel==0)
                return indentation+"}";
            else
                return assertionsIndentation(nestLevel)+"}";
        }
    }

    public String objectClosing(int nestLevel){
        if(compact)
            return "}";
        else{
            if(nestLevel==0)
                return "\n}";
            else {
                String indentation = new String(new char[4*nestLevel]).replace("\0", " ");
                return "\n"+indentation+"}";
            }
        }
    }

    public String indentationBeforeAssertions(int nestLevel, JSONTreeNode node){
        if(compact)
            return "";
        else{
            return assertionsIndentation(nestLevel);
        }
    }

    public String arrayTypeAndItemsKeyword(int nestLevel){
        if(compact)
            return "array\",\"items\":";
        else {
            String indent = assertionsIndentation(nestLevel);
            return "array\",\n" + indent + "\"items\": ";
        }
    }

    public String indentationBeforeObjectItemOpening(int nestLevel){
        if(compact)
            return "";
        else
            return propertyNameIndentation(nestLevel);
    }

    public String emptyArrayItemList(){
        return "{}\n";
    }

    public String arrayClosing(int nestLevel){
        if(compact)
            return "}";
        else{
            String indentation = new String(new char[4*nestLevel]).replace("\0", " ");
            return indentation+"}";
        }
    }

    public String primitiveArrayItem(int nestLevel, JSONTreeNode current){
        if(compact)
            return "{\"type\":\"" + current.getTypeAsString() + "\"}";
        else{
            String openingIndent = propertyNameIndentation(nestLevel);
            String assertionsIndent = openingIndent + "  ";

            return openingIndent + "{\n"+assertionsIndent+"\"type\": \"" + current.getTypeAsString() + "\"";
        }
    }

    public String primitiveArrayItemClosing(int nestLevel){
        if(compact)
            return "}";
        else{
            String indentation = propertyNameIndentation(nestLevel);
            return "\n"+indentation+"}";

        }
    }

    public String indentationForSubarray(int nestLevel){
        if(compact)
            return "";
        else
            if(nestLevel==0)
                return indentation(nestLevel+1);
            else
                return indentation(nestLevel+2);
    }

    public String itemListClosing(int nestLevel){
        if(compact)
            return "]";
        else{
            String indentation = assertionsIndentation(nestLevel);
            return indentation+"]\n";
        }
    }

}
