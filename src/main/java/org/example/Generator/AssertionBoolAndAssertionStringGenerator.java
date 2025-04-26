package org.example.Generator;

import org.example.Function.Operation;
import org.example.JSONTN.JSONTreeNode;

public class AssertionBoolAndAssertionStringGenerator {

    private Operation generateAssertionString;
    private boolean includeInSchemaString = true;


    public String generateAssertionValue(JSONTreeNode node){
        return generateAssertionString.execute(node);
    }


    public AssertionBoolAndAssertionStringGenerator(){

    }
    public AssertionBoolAndAssertionStringGenerator(Operation generateAssertionString){
        this.generateAssertionString = generateAssertionString;
    }


    public boolean includeInSchemaString() {
        return includeInSchemaString;
    }

    public void setIncludeInSchemaString(boolean includeInSchemaString) {
        this.includeInSchemaString = includeInSchemaString;
    }

    public Operation getGenerateAssertionString() {
        return generateAssertionString;
    }

    public void setAssertionValueStringGenerationMethod(Operation generateAssertionString) {
        this.generateAssertionString = generateAssertionString;
    }
}
