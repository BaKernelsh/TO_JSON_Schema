package org.example.Generator;

import org.example.Exception.JSONSchemaGeneratorException;
import org.example.Function.Operation;
import org.example.JSONTN.JSONNumberTN;
import org.example.JSONTN.JSONStringTN;
import org.example.JSONTN.JSONTreeNode;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class AssertionConfiguration { //TODO metody do wlaczenia wylaczenia wszystkich
    // <typPola, <nazwaAssertion, generatorWartosci(bool, funkcja)>>
    private HashMap<String, LinkedHashMap<String, AssertionBoolAndAssertionStringGenerator>> assertions = new HashMap<>();


    public String addAssertionsToSchemaString(JSONTreeNode node, String schemaString) throws JSONSchemaGeneratorException {

        if(schemaString == null)
            throw new JSONSchemaGeneratorException("addAssertions: schemaString argument cannot be null");
        if(!isValidType(node.getTypeAsString()))
            throw new JSONSchemaGeneratorException("addAssertions: node has invalid type");

        var assertionsForNodeType = assertions.get(node.getTypeAsString());

        for(var assertionName : assertionsForNodeType.keySet()){
            var gen = assertionsForNodeType.get(assertionName);
            if(gen != null && gen.includeInSchemaString())
                schemaString = schemaString.concat(",\n\"" + assertionName + "\": " + gen.generateAssertionValue(node));
            //if(gen == null) dodac informacje o tym do jakiegos logu zamiast rzucac exception ?
        }

        return schemaString;
    }


    public AssertionConfiguration(){
        assertions.put("array", new LinkedHashMap<>());
        assertions.put("boolean", new LinkedHashMap<>());
        assertions.put("null", new LinkedHashMap<>());
        assertions.put("object", new LinkedHashMap<>());
        assertions.put("string", new LinkedHashMap<>());
        assertions.put("number", new LinkedHashMap<>());
        assertions.put("integer", new LinkedHashMap<>());

        assertions.get("string").put("minLength", new AssertionBoolAndAssertionStringGenerator(node -> Integer.toString(((JSONStringTN) node).getValue().length()) ));
        assertions.get("string").put("maxLength", new AssertionBoolAndAssertionStringGenerator(node -> Integer.toString(((JSONStringTN) node).getValue().length()) ));

        assertions.get("number").put("minimum", new AssertionBoolAndAssertionStringGenerator(node -> Double.toString(((JSONNumberTN) node).getValue()) ));
        assertions.get("number").put("maximum", new AssertionBoolAndAssertionStringGenerator(node -> Double.toString(((JSONNumberTN) node).getValue()) ));

        assertions.get("integer").put("minimum", new AssertionBoolAndAssertionStringGenerator(node -> Double.toString(((JSONNumberTN) node).getValue()).split("\\.")[0] ));
        assertions.get("integer").put("maximum", new AssertionBoolAndAssertionStringGenerator(node -> Double.toString(((JSONNumberTN) node).getValue()).split("\\.")[0] ));
    }

    private boolean isValidType(String type){

        if((!type.equals("array")) &&
           (!type.equals("boolean")) &&
           (!type.equals("null")) &&
           (!type.equals("object")) &&
           (!type.equals("string")) &&
           (!type.equals("number")) &&
           (!type.equals("integer"))
        ){ return false; }

        return true;
    }

    // dac wybor czy zastapic jak juz jest czy rzucic exception
    public void setAssertion(String forType, String assertionName, Operation assertionValueGenerator){
        if(forType == null)
            throw new RuntimeException("setAssertion: argument forType cannot be null");
        if(assertionName == null)
            throw new RuntimeException("setAssertion: argument assertionName cannot be null");
        if(assertionValueGenerator == null)
            throw new RuntimeException("setAssertion: argument assertionValueGenerator cannot be null");
        if(!isValidType(forType))
            throw new RuntimeException("setAssertion: invalid field type");

        assertions.get(forType).put(assertionName, new AssertionBoolAndAssertionStringGenerator(assertionValueGenerator));

    }

    public void setAllUnused(){
        for(var type : assertions.keySet()){
            for(var assertion : assertions.get(type).keySet()){
                assertions.get(type).get(assertion).setIncludeInSchemaString(false);
            }
        }
    }

    public void setAllUsed(){
        for(var type : assertions.keySet()){
            for(var assertion : assertions.get(type).keySet()){
                assertions.get(type).get(assertion).setIncludeInSchemaString(true);
            }
        }
    }


}
