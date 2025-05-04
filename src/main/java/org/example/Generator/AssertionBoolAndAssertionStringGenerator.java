package org.example.Generator;

import org.example.Function.OperationReturnsString;
import org.example.JSONTN.JSONTreeNode;

public class AssertionBoolAndAssertionStringGenerator {

    private OperationReturnsString generateAssertionString;
    private boolean includeInSchemaString = true;


    public String generateAssertionValue(JSONTreeNode node){
        return generateAssertionString.execute(node);
    }


    public AssertionBoolAndAssertionStringGenerator(){

    }
    public AssertionBoolAndAssertionStringGenerator(OperationReturnsString generateAssertionString){
        this.generateAssertionString = generateAssertionString;
    }


    public boolean includeInSchemaString() {
        return includeInSchemaString;
    }

    public void setIncludeInSchemaString(boolean includeInSchemaString) {
        this.includeInSchemaString = includeInSchemaString;
    }

    public OperationReturnsString getGenerateAssertionString() {
        return generateAssertionString;
    }

    public void setAssertionValueStringGenerationMethod(OperationReturnsString generateAssertionString) {
        this.generateAssertionString = generateAssertionString;
    }
}
