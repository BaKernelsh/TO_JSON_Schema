package org.example.Generator;

import org.example.Exception.JSONSchemaGeneratorException;
import org.example.JSONTN.JSONStringTN;
import org.example.JSONTN.JSONTreeNode;

public class AssertionConfiguration {

    private boolean minLength;
    private boolean maxLength;

    public String addAssertionsToSchemaString(JSONTreeNode node, String schemaString) throws JSONSchemaGeneratorException {

        if(schemaString == null)
            throw new JSONSchemaGeneratorException("addAssertions: schemaString argument cannot be null");

        switch(node.getTypeAsString()){
            case "string":
                return addStringAssertions(schemaString, node);

            default:
                throw new JSONSchemaGeneratorException("addAssertions: invalid type value");
        }

    }

    //jesli bedzie chciał dodac wlasne assertions do danego typu to zrobi klase dziedziczaca i z overriduje te metode - wywola tą i swój kod
    private String addStringAssertions(String schemaString, JSONTreeNode node){
        JSONStringTN strNode = (JSONStringTN) node;

        if(minLength)
            schemaString = schemaString.concat(",\n\"minLength\": " + strNode.getValue().length());

        if(maxLength)
            schemaString = schemaString.concat(",\n\"maxLength\": " + strNode.getValue().length());

        return schemaString;
    }


    public AssertionConfiguration(){
        minLength = true;
        maxLength = true;
    }

    public boolean isMinLength() {
        return minLength;
    }

    public void setMinLength(boolean minLength) {
        this.minLength = minLength;
    }

    public boolean isMaxLength() {
        return maxLength;
    }

    public void setMaxLength(boolean maxLength) {
        this.maxLength = maxLength;
    }

}
