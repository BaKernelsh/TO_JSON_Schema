package org.example.Generator;

import org.example.Exception.JSONSchemaGeneratorException;
import org.example.Function.OperationReturnsString;
import org.example.JSONTN.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

public class AssertionConfiguration { //TODO metody do wlaczenia wylaczenia wszystkich
    // <typPola, <nazwaAssertion, generatorWartosci(bool, funkcja)>>
    private HashMap<String, LinkedHashMap<String, AssertionBoolAndAssertionStringGenerator>> assertions = new HashMap<>();


    public String addAssertionsToSchemaString(JSONTreeNode node, String schemaString, String indentation) throws JSONSchemaGeneratorException {

        if(schemaString == null)
            throw new JSONSchemaGeneratorException("addAssertions: schemaString argument cannot be null");
        if(!isValidType(node.getTypeAsString()))
            throw new JSONSchemaGeneratorException("addAssertions: node has invalid type");

        var assertionsForNodeType = assertions.get(node.getTypeAsString());

        //indentation = shortenIndentationForObjectNode(node, indentation);

        for(var assertionName : assertionsForNodeType.keySet()){
            var gen = assertionsForNodeType.get(assertionName);
            if(gen != null && gen.includeInSchemaString())
                schemaString = schemaString.concat(",\n"+indentation+"\"" + assertionName + "\": " + gen.generateAssertionValue(node));
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


        assertions.get("number").put("minimum", new AssertionBoolAndAssertionStringGenerator(node -> Double.toString(((JSONNumberTN) node).getValue()) ));
        assertions.get("number").put("maximum", new AssertionBoolAndAssertionStringGenerator(node -> Double.toString(((JSONNumberTN) node).getValue()) ));
        assertions.get("number").put("multipleOf", new AssertionBoolAndAssertionStringGenerator(node -> Double.toString(((JSONNumberTN) node).getValue()) ));

        assertions.get("integer").put("multipleOf", new AssertionBoolAndAssertionStringGenerator(node -> Double.toString(((JSONNumberTN) node).getValue()).split("\\.")[0] ));
        assertions.get("integer").put("maximum", new AssertionBoolAndAssertionStringGenerator(node -> Double.toString(((JSONNumberTN) node).getValue()).split("\\.")[0] ));
        assertions.get("integer").put("exclusiveMaximum", new AssertionBoolAndAssertionStringGenerator(node -> Double.toString(((JSONNumberTN) node).getValue() +1).split("\\.")[0] ));
        assertions.get("integer").put("minimum", new AssertionBoolAndAssertionStringGenerator(node -> Double.toString(((JSONNumberTN) node).getValue()).split("\\.")[0] ));
        assertions.get("integer").put("exclusiveMinimum", new AssertionBoolAndAssertionStringGenerator(node -> Double.toString(((JSONNumberTN) node).getValue() -1).split("\\.")[0] ));


        assertions.get("string").put("maxLength", new AssertionBoolAndAssertionStringGenerator(node -> Integer.toString(((JSONStringTN) node).getValue().length()) ));
        assertions.get("string").put("minLength", new AssertionBoolAndAssertionStringGenerator(node -> Integer.toString(((JSONStringTN) node).getValue().length()) ));
        assertions.get("string").put("pattern", new AssertionBoolAndAssertionStringGenerator(node -> Pattern.quote(((JSONStringTN) node).getValue()) ));

        assertions.get("array").put("maxItems", new AssertionBoolAndAssertionStringGenerator(node -> Integer.toString(((JSONArrayTN) node).getItems().size()) ));
        assertions.get("array").put("minItems", new AssertionBoolAndAssertionStringGenerator(node -> Integer.toString(((JSONArrayTN) node).getItems().size()) ));
        assertions.get("array").put("uniqueItems", new AssertionBoolAndAssertionStringGenerator(node -> "false" ));


        assertions.get("object").put("required", new AssertionBoolAndAssertionStringGenerator(node -> "["+((JSONObjectTN) node).getPropertyNamesAsString()+"]" ));
        assertions.get("object").put("maxProperties", new AssertionBoolAndAssertionStringGenerator(node -> Integer.toString(((JSONObjectTN) node).getProperties().size()) ));
        assertions.get("object").put("minProperties", new AssertionBoolAndAssertionStringGenerator(node -> Integer.toString(((JSONObjectTN) node).getProperties().size()) ));
        //TODO dependentRequired

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
    public void setAssertion(String forType, String assertionName, OperationReturnsString assertionValueGenerator){
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

    private String shortenIndentationForObjectNode(JSONTreeNode node, String indentation){
        if(node.getType() == JSONTreeNodeType.OBJECT)
            return indentation.substring(0,indentation.length()-2);

        return indentation;
    }

}
