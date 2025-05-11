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
            schemaString = assertionConfig.addAssertionsToSchemaString(treeNode, schemaString, this.indentationBeforeAssertions(0)) + "}";
            return schemaString;
        }
        //schemaString = assertionConfig.addAssertionsToSchemaString(treeNode, schemaString, schemaStringElements.indentationBeforeAssertions(nestLevel+1));

        else {
            //return "{\n  \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n  \"type\": \"" + treeNode.getTypeAsString() + "\"\n}";}
            String schemaString = "{\n  \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n  \"type\": \"" + treeNode.getTypeAsString() + "\"";
            schemaString = assertionConfig.addAssertionsToSchemaString(treeNode, schemaString, this.indentationBeforeAssertions(0)) + "\n}";
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

    public String typeKeywordAndColon(int nestLevel){
        if(compact)
            return "{\"type\":\"";
        else {
            String indentation = indentation(nestLevel+1);
            return "{\n"+indentation+"\"type\": \"";
        }
    }

    public String objectTypeAndPropertiesKeyword(int nestLevel){
        if(compact)
            return "object\",\"properties\":{";
        else{
            String indentation;
            if(nestLevel==0) //ten obiekt to root
                indentation = indentation(nestLevel);
            else
                indentation = indentation(nestLevel+1);
            return "object\",\n"+indentation+"\"properties\": {\n";
        }
    }

    public String propertiesClosingAndEmptyPropertiesAndObjectClosing(int nestLevel){
        if(compact)
            return "},\"required\":[]}";
        else{
            String indentation = indentation(nestLevel);
            return indentation+"},\n"+indentation+"\"required\": []\n"+ indentation(nestLevel-1) +"}";
        }
    }

    public String primitivePropertyNameAndType(int nestLevel, JSONTreeNode current){
        if(compact)
            return "\"" + current.getName() + "\":{\"type\":\"" +
                    current.getTypeAsString() + "\"";
        else{
            if(nestLevel==0)
                return indentation(nestLevel+1)+"\"" + current.getName() + "\": {\n"+indentation(nestLevel+2)+"\"type\": \"" +
                    current.getTypeAsString() + "\"";
            else
                return indentation(nestLevel+2)+"\"" + current.getName() + "\": {\n"+indentation(nestLevel+3)+"\"type\": \"" +
                    current.getTypeAsString() + "\"";
        }
    }

    public String objectOrArrayPropertyName(int nestLevel, JSONTreeNode current){
        if(compact)
            return "\"" + current.getName() + "\":";
        else{
            String indentation = indentation(nestLevel+1);
            return indentation+"\"" + current.getName() + "\": ";
        }
    }


    public String primitivePropertyClosing(int nestLevel){
        if(compact)
            return "}";
        else{
            String indentation = indentation(nestLevel+1);
            if(nestLevel==0)
                return "\n"+indentation+"}";
            else
                return "\n"+indentation(nestLevel+2)+"}";
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
                return indentation(nestLevel+1)+"}";

        }
    }

    public String objectClosing(int nestLevel){
        if(compact)
            return "}";
        else{
            String indentation = indentation(nestLevel-1);
            if(nestLevel==0)
                return "\n"+indentation+"}";
            else
                return "\n"+indentation(nestLevel)+"}";
        }
    }

    public String indentationBeforeAssertions(int nestLevel){
        if(compact)
            return "";
        else{
            String indentation = indentation(nestLevel);
            if(nestLevel==0)
                return indentation;
            else
                return indentation(nestLevel+1);
        }
    }

    public String arrayTypeAndItemsKeyword(int nestLevel, boolean subarray){
        if(compact)
            return "array\",\"items\":";
        else {
            if (!subarray) {
                String indentation;
                if (nestLevel == 0)
                    indentation = indentation(nestLevel);
                else
                    indentation = indentation(nestLevel + 1);
                return "array\",\n" + indentation + "\"items\": ";
            }
            else{
                return "array\",\n" + indentation(nestLevel+2) + "\"items\": ";
            }
        }
    }

    public String emptyArrayItemList(){
        return "{}\n";
    }

    public String arrayClosing(int nestLevel){
        if(compact)
            return "}";
        else{
            String indentation;
            if(nestLevel==0) //ten array to root
                return "}";
            else
                indentation = indentation(nestLevel);
            return indentation+"}";
        }
    }

    public String primitiveArrayItem(int nestLevel, JSONTreeNode current){
        if(compact)
            return "{\"type\":\"" + current.getTypeAsString() + "\"}";
        else{
            String indentation;
            if(nestLevel==0) { //ten array to root
                indentation = indentation(nestLevel + 1);
                //return indentation + "{\n"+indentation(nestLevel+2)+"\"type\": \"" + current.getTypeAsString() + "\"\n"+indentation+"}";
                return indentation + "{\n"+indentation(nestLevel+2)+"\"type\": \"" + current.getTypeAsString() + "\"";
            }
            else {
                indentation = indentation(nestLevel + 3);
                //return indentation + "{\n"+indentation(nestLevel+4)+"\"type\": \"" + current.getTypeAsString() + "\"\n"+indentation+"}";
                return indentation + "{\n"+indentation(nestLevel+4)+"\"type\": \"" + current.getTypeAsString() + "\"";
            }
        }
    }

    public String primitiveArrayItemClosing(int nestLevel){
        if(compact)
            return "}";
        else{
            String indentation;
            if(nestLevel==0) //ten array to root
                indentation = indentation(nestLevel + 1);
            else
                indentation = indentation(nestLevel + 3);
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
            String indentation;
            if(nestLevel==0){ //ten array to root
                indentation = indentation(nestLevel);
            }
            else
                indentation = indentation(nestLevel+2);
            return indentation+"]\n";
        }
    }

}
