package org.example.Validator;

import org.example.Exception.JSONSchemaGeneratorException;
import org.example.Exception.JSONValidationException;
import org.example.Exception.UnknownValidationKeywordException;
import org.example.Generator.Generator;
import org.example.JSONString;
import org.example.JSONTN.*;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JSONValidator {
    //<typ, <keyword,verifier>
    private HashMap<String, HashMap<String, VerifyBoolAndVerifierMethod>> verifiers = new HashMap<>();
    private final Generator generator = new Generator();
    private OnUnknownKeyword onUnknownKeyword = OnUnknownKeyword.KEYWORD_VALIDATION_SUCCESFUL_CONTINUE_VALIDATION;
    //TODO nie throwowanie przy nieudanej walidacji tylko zbieranie message zeby je póżniej wyświetlić w gui


    //TODO przypadek kiedy schema to true / false
    public boolean validateAgainstSchema(String json, String schemaString) throws JSONValidationException,
                                          JSONSchemaGeneratorException, UnknownValidationKeywordException
    {
        JSONTreeNode jsonRoot = generator.generateJsonTree(new JSONString(json));
        return validateAgainstSchema(jsonRoot, schemaString);
    }

    public boolean validateAgainstSchema(JSONTreeNode node, String schemaString) throws JSONValidationException,
                                                JSONSchemaGeneratorException, UnknownValidationKeywordException
    {
        JSONString schema = new JSONString(schemaString);
        JSONObjectTN schemaRoot = (JSONObjectTN) generator.generateJsonTree(schema);

        return validateAgainstSchema(node, schemaRoot);
    }

    public boolean validateAgainstSchema(JSONTreeNode node, JSONTreeNode schema) throws JSONValidationException,
                                                                              UnknownValidationKeywordException
    {
        if(schema instanceof JSONBooleanTN)
            return ((JSONBooleanTN) schema).getValue();

        boolean result = true;

        ArrayList<JSONTreeNode> assertions = ((JSONObjectTN) schema).getProperties();

        for(var assertion : assertions){
            boolean resultForAssertion = validateNodeAgainstAssertion(node, assertion, this);
            if(!resultForAssertion)
                result = false;
        } //TODO zwracanie zbioru errorow przy po kontynuowaniu walidacji mimo nieobecnych metod dla asercji ?

        return result;
    }


    private boolean validateNodeAgainstAssertion(JSONTreeNode node, JSONTreeNode assertion, JSONValidator validatorInstance) throws JSONValidationException, UnknownValidationKeywordException {
        String keyword = assertion.getName();

        VerifyBoolAndVerifierMethod verifier = verifiers.get(node.getTypeAsString()).get(keyword);
        if(verifier==null) {
            System.out.println("nie ma assertion " +assertion.getName());
            return handleUnknownKeyword(keyword);
        }


        ValidationResultAndErrorMessage validationResult = verifier.verify(node, assertion, validatorInstance);

        if(!validationResult.isValid() ){
            if(validationResult.ignoreInvalidity())
                return false;
            throw new JSONValidationException("Validation failed for keyword: " +keyword + " " +validationResult.getMessage());
        }
        else
            return true;
    }



    private boolean handleUnknownKeyword(String keyword) throws UnknownValidationKeywordException{
        if(onUnknownKeyword == OnUnknownKeyword.KEYWORD_VALIDATION_UNSUCCESFUL_CONTINUE_VALIDATION)
            return false;
        if(onUnknownKeyword == OnUnknownKeyword.KEYWORD_VALIDATION_SUCCESFUL_CONTINUE_VALIDATION)
            return true;
        else
            throw new UnknownValidationKeywordException("Unknown validation keyword: "+keyword);
    }


    public JSONValidator(){
        verifiers.put("integer", new HashMap<>());
        verifiers.put("number", new HashMap<>());
        verifiers.put("object", new HashMap<>());
        verifiers.put("null", new HashMap<>());
        verifiers.put("array", new HashMap<>());
        verifiers.put("string", new HashMap<>());
        verifiers.put("boolean", new HashMap<>());

        VerifyBoolAndVerifierMethod<JSONTreeNode, String> verifyStringPattern = VerifyBoolAndVerifierMethod.withAssertionValueAsString();
        verifyStringPattern.setVerifierMethod((node, assertion, validatorInstance) ->
        {
            String nodeString = ((JSONStringTN) node).getValue();
            boolean nodeMatchesPattern = Pattern.matches(assertion, nodeString);

            ValidationResultAndErrorMessage result = new ValidationResultAndErrorMessage();
            if(nodeMatchesPattern)
                result.setValid(true);
            else{
                result.setValid(false);
                if(node.isRoot())
                    result.setMessage("String does not match pattern " + assertion);
                else
                    result.setMessage("String "+ node.getName() +"does not match pattern " + assertion);
            }
            return result;
        });

        VerifyBoolAndVerifierMethod<JSONTreeNode, Double> verifyStringMaxLength = VerifyBoolAndVerifierMethod.withAssertionValueAsDouble();
        verifyStringMaxLength.setVerifierMethod((node, assertion, validatorInstance) ->
        {
            long nodeStringLength = ((JSONStringTN) node).getValue().length();

            ValidationResultAndErrorMessage result = new ValidationResultAndErrorMessage();
            if(nodeStringLength <= assertion)
                result.setValid(true);
            else{
                result.setValid(false);
                if(node.isRoot())
                    result.setMessage("Length of this string should be equal to or less than " + assertion.intValue());
                else
                    result.setMessage("Length of "+ node.getName() + " should be equal to or less than " + assertion.intValue());
            }
            return result;
        });

        VerifyBoolAndVerifierMethod<JSONTreeNode, Double> verifyStringMinLength = VerifyBoolAndVerifierMethod.withAssertionValueAsDouble();
        verifyStringMinLength.setVerifierMethod((node, assertion, validatorInstance) ->
        {
            long nodeStringLength = ((JSONStringTN) node).getValue().length();

            ValidationResultAndErrorMessage result = new ValidationResultAndErrorMessage();
            if(nodeStringLength >= assertion)
                result.setValid(true);
            else{
                result.setValid(false);
                if(node.isRoot())
                    result.setMessage("Length of this string should be equal to or greater than " + assertion.intValue());
                else
                    result.setMessage("Length of "+ node.getName() + " should be equal to or greater than " + assertion.intValue());
            }
            return result;
        });


        VerifyBoolAndVerifierMethod placeholder = new VerifyBoolAndVerifierMethod((node,assertion, validatorInstance) ->
        {
            ValidationResultAndErrorMessage result = new ValidationResultAndErrorMessage();
            result.setValid(true);
            return result;
        });

/*        VerifyBoolAndVerifierMethod verifyType = new VerifyBoolAndVerifierMethod((node,assertion, validatorInstance) ->
        {
            String nodeType = node.getTypeAsString();
            String requiredType = ((JSONStringTN) assertion).getValue();
            System.out.println(nodeType);
            System.out.println(requiredType);

            ValidationResultAndErrorMessage result = new ValidationResultAndErrorMessage();
            if(nodeType.equals(requiredType) || (nodeType.equals("integer") && requiredType.equals("number")))
                result.setValid(true);
            else{
                result.setValid(false);
                if(node.isRoot())
                    result.setMessage("Invalid type. Should be: " + requiredType);
                else
                    result.setMessage("Invalid type of "+node.getName()+" property. Should be: " +requiredType);
            }
            return result;
        });*/


/*        VerifyBoolAndVerifierMethod<JSONTreeNode, String> verifyType3 = VerifyBoolAndVerifierMethod.withAssertionValueAsString();

        verifyType3.setVerifierMethod((node,assertion, validatorInstance) ->
        {
            String nodeType = node.getTypeAsString();
            String requiredType = assertion;

            ValidationResultAndErrorMessage result = new ValidationResultAndErrorMessage();
            if(nodeType.equals(requiredType) || (nodeType.equals("integer") && requiredType.equals("number")))
                result.setValid(true);
            else{
                result.setValid(false);
                if(node.isRoot())
                    result.setMessage("Invalid type. Should be: " + requiredType);
                else
                    result.setMessage("Invalid type of "+node.getName()+" property. Should be: " +requiredType);
            }
            return result;
        });*/

        VerifyBoolAndVerifierMethod<JSONTreeNode, JSONTreeNode> verifyType = VerifyBoolAndVerifierMethod.withAssertionValueAsJSONTreeNode();
        verifyType.setVerifierMethod((node, assertion, validatorInstance) ->
        {
            String nodeType = node.getTypeAsString();
            LinkedHashSet<String> requiredTypes = new LinkedHashSet<>();

            ValidationResultAndErrorMessage result = new ValidationResultAndErrorMessage();

            try {
                if (assertion.getType() == JSONTreeNodeType.STRING) {
                    requiredTypes.add(((JSONStringTN) assertion).getValue());
                } else if (assertion.getType() == JSONTreeNodeType.ARRAY) {
                    for (JSONTreeNode assertionArrayItem : ((JSONArrayTN) assertion).getItems()) {
                        if (requiredTypes.add(((JSONStringTN) assertionArrayItem).getValue()) == false) { //jeden typ byl w tablicy 2 razy
                            result.setValid(false);
                            result.setMessage("If value of keyword \"type\" is an array, elements of the array MUST be strings and MUST be unique.");
                            return result;
                        }
                    }
                }
                else
                    throw new ClassCastException();

                for(String requiredType : requiredTypes){
                    if(nodeType.equals(requiredType) || (nodeType.equals("integer") && requiredType.equals("number"))) {
                        result.setValid(true);
                        return result;
                    }
                }

                result.setValid(false);
                if(node.isRoot())
                    if(requiredTypes.size() > 1)
                        result.setMessage("Invalid type. Should be one of those: " + String.join(",", requiredTypes));
                    else
                        result.setMessage("Invalid type. Should be: " + requiredTypes.getFirst());
                else
                    if(requiredTypes.size() > 1)
                        result.setMessage("Invalid type of "+node.getName()+" property. Should be one of those: " + String.join(",", requiredTypes));
                    else
                        result.setMessage("Invalid type of "+node.getName()+" property. Should be: " + requiredTypes.getFirst());

                return result;

            }catch(ClassCastException e){
                result.setValid(false);
                result.setMessage("The value of \"type\" keyword MUST be either a string or an array. If it is an array, elements of the array MUST be strings and MUST be unique.");
                return result;
            }
        });

        VerifyBoolAndVerifierMethod verify$schema = new VerifyBoolAndVerifierMethod((node,assertion, validatorInstance) ->
        {
            ValidationResultAndErrorMessage result = new ValidationResultAndErrorMessage();
            result.setValid(true);
            return result;
        });

        VerifyBoolAndVerifierMethod verifyMinimum = new VerifyBoolAndVerifierMethod((node,assertion, validatorInstance) ->
        {
            Double minimum = ((JSONNumberTN) assertion).getValue();
            ValidationResultAndErrorMessage result = new ValidationResultAndErrorMessage();
            if( ((JSONNumberTN) node).getValue() >= minimum )
                result.setValid(true);
            else {
                result.setValid(false);
                result.setMessage("integer value of "+node.getName()+" must be equal to or greater than "+minimum);
            }
            return result;
        });

        VerifyBoolAndVerifierMethod verifyMaximum = new VerifyBoolAndVerifierMethod((node,assertion, validatorInstance) ->
        {
            Double maximum = ((JSONNumberTN) assertion).getValue();
            ValidationResultAndErrorMessage result = new ValidationResultAndErrorMessage();
            if( ((JSONNumberTN) node).getValue() <= maximum )
                result.setValid(true);
            else {
                result.setValid(false);
                result.setMessage("integer value of "+node.getName()+" must be equal to or lower than "+maximum);
            }
            return result;
        });


        VerifyBoolAndVerifierMethod<JSONTreeNode, Double> verifyExclusiveMinimum = VerifyBoolAndVerifierMethod.withAssertionValueAsDouble();
        verifyExclusiveMinimum.setVerifierMethod((node, assertion, validatorInstance) ->
        {
            ValidationResultAndErrorMessage result = new ValidationResultAndErrorMessage();
            if( ((JSONNumberTN) node).getValue() > assertion )
                result.setValid(true);
            else {
                result.setValid(false);
                result.setMessage(" value of "+node.getName()+" must be greater than "+assertion);
            }
            return result;
        });

        VerifyBoolAndVerifierMethod<JSONTreeNode, Double> verifyExclusiveMaximum = VerifyBoolAndVerifierMethod.withAssertionValueAsDouble();
        verifyExclusiveMaximum.setVerifierMethod((node, assertion, validatorInstance) ->
        {
            ValidationResultAndErrorMessage result = new ValidationResultAndErrorMessage();
            if( ((JSONNumberTN) node).getValue() < assertion )
                result.setValid(true);
            else {
                result.setValid(false);
                result.setMessage(" value of "+node.getName()+" must be lower than "+assertion);
            }
            return result;
        });


        VerifyBoolAndVerifierMethod<JSONTreeNode, Double> verifyMultipleOfInteger = VerifyBoolAndVerifierMethod.withAssertionValueAsDouble();
        verifyMultipleOfInteger.setVerifierMethod((node, assertion, validatorInstance) -> {

            Integer nodeValue = ((JSONNumberTN) node).getValue().intValue();
            ValidationResultAndErrorMessage result;

            if((nodeValue / assertion) % 1 == 0){
                result = ValidationResultAndErrorMessage.newInstanceValid();
            }
            else{
                result = new ValidationResultAndErrorMessage();
                result.setValid(false);
                result.setMessage("Property "+node.getName()+" should be multiple of "+assertion);
            }

            return result;
        });

        VerifyBoolAndVerifierMethod<JSONTreeNode, Double> verifyMultipleOfDouble = VerifyBoolAndVerifierMethod.withAssertionValueAsDouble();
        verifyMultipleOfDouble.setVerifierMethod((node, assertion, validatorInstance) -> {

            Double nodeValue = ((JSONNumberTN) node).getValue();
            ValidationResultAndErrorMessage result;

            if((nodeValue / assertion) % 1 == 0){
                result = ValidationResultAndErrorMessage.newInstanceValid();
            }
            else{
                result = new ValidationResultAndErrorMessage();
                result.setValid(false);
                result.setMessage("Property "+node.getName()+" should be multiple of "+assertion);
            }

            return result;
        });


        verifiers.get("integer").put("minimum", verifyMinimum);
        verifiers.get("integer").put("exclusiveMinimum", verifyExclusiveMinimum);
        verifiers.get("integer").put("maximum", verifyMaximum);
        verifiers.get("integer").put("exclusiveMaximum", verifyExclusiveMaximum);
        verifiers.get("integer").put("$schema", verify$schema);
        verifiers.get("integer").put("type", verifyType);
        verifiers.get("integer").put("multipleOf", verifyMultipleOfInteger);

        verifiers.get("number").put("minimum", verifyMinimum);
        verifiers.get("number").put("exclusiveMinimum", verifyExclusiveMinimum);
        verifiers.get("number").put("maximum", verifyMaximum);
        verifiers.get("number").put("exclusiveMaximum", verifyExclusiveMaximum);
        verifiers.get("number").put("$schema", verify$schema);
        verifiers.get("number").put("type", verifyType);
        verifiers.get("number").put("multipleOf", verifyMultipleOfDouble);



        VerifyBoolAndVerifierMethod verifyRequired = new VerifyBoolAndVerifierMethod((node,assertion, validatorInstance) ->
        {

            ArrayList<String> nodeProperties = ((JSONObjectTN) node).getPropertyNames();
            LinkedHashSet<JSONTreeNode> items = ((JSONArrayTN) assertion).getItems();
            ArrayList<String> requiredProperties = new ArrayList<>();
            items.forEach(item -> {
                String propertyName = ((JSONStringTN) item).getValue();
                requiredProperties.add(propertyName);
            });
            System.out.println("requirde properties:");

            for(var rp : requiredProperties){
                System.out.println(rp);
            }

            boolean allPresent = true;
            String missingProperty="";

            for(var requiredProperty : requiredProperties){
                allPresent = nodeProperties.stream().anyMatch(property -> property.equals(requiredProperty));
                if(!allPresent){
                    missingProperty = requiredProperty;
                    break;
                }
            }


            ValidationResultAndErrorMessage result = new ValidationResultAndErrorMessage();
            if(allPresent)
                result.setValid(true);
            else{
                result.setValid(false);
                result.setMessage("Object " +node.getName()+" should contain property: "+ missingProperty); //TODO else gdy node to root
            }
            return result;
        });


        VerifyBoolAndVerifierMethod<JSONTreeNode, String> verifyRequired2 = VerifyBoolAndVerifierMethod.withAssertionValueAsArrayOfString();
        verifyRequired2.forEachElementInArray(((node, assertionValue, validatorInstance) -> {
            System.out.println("WAlidowanie required");
            ArrayList<String> nodeProperties = ((JSONObjectTN) node).getPropertyNames();

            boolean propertyIsPresentInNode = nodeProperties.stream().anyMatch(property -> property.equals(assertionValue));

            ValidationResultAndErrorMessage result = new ValidationResultAndErrorMessage();
            if(propertyIsPresentInNode)
                result.setValid(true);
            else{
                result.setValid(false);
                result.setMessage("Object " +node.getName()+" should contain property: "+ assertionValue); //TODO else gdy node to root
            }
            return result;
        }));


        VerifyBoolAndVerifierMethod verifyProperties = new VerifyBoolAndVerifierMethod((node,assertion, validatorInstance) ->
        {

            ArrayList<JSONTreeNode> nodeProperties = ((JSONObjectTN) node).getProperties();

            List<JSONTreeNode> propertiesPresentInBoth = ((JSONObjectTN) assertion).getProperties().stream()
                    .takeWhile(prop -> nodeProperties.stream().anyMatch(nodeprop -> nodeprop.getName().equals(prop.getName())) ).toList();


            ValidationResultAndErrorMessage result = ValidationResultAndErrorMessage.newInstanceValid();

            for(var commonProp : propertiesPresentInBoth){
                JSONTreeNode nodePropertyWithSameName =  nodeProperties.stream().filter(nodeProp -> commonProp.getName().equals(nodeProp.getName())).findAny().get();
                try {
                    //nie ma ustawionej metody dla keywordu i ma byc kontynuowana walidacja - walidacja nie rzuca wyjatku wiec to sie wykonuje
                    if(validatorInstance.validateAgainstSchema(nodePropertyWithSameName, commonProp) == false){
                        result.setValid(false);
                        result.setIgnoreInvalidity(true);
                    }
                }catch (Exception e){
                    result.setValid(false);
                    result.setMessage(e.getMessage());
                    return result;
                }
            }

            return result;
        });

        VerifyBoolAndVerifierMethod<JSONTreeNode, Integer> verifyMaxProperties = VerifyBoolAndVerifierMethod.withAssertionValueAsInteger();
        verifyMaxProperties.setVerifierMethod((node, assertion, validatorInstance) ->
        {
            long nodePropertiesCount = ((JSONObjectTN) node).getProperties().size();

            ValidationResultAndErrorMessage result = new ValidationResultAndErrorMessage();
            if(nodePropertiesCount <= assertion)
                result.setValid(true);
            else{
                result.setValid(false);
                if(node.isRoot())
                    result.setMessage("Object should contain a maximum of "+assertion+ " properties" );
                else
                    result.setMessage("Object "+node.getName() +" should contain a maximum of "+assertion+ " properties" );
            }
            return result;
        });

        VerifyBoolAndVerifierMethod<JSONTreeNode, Integer> verifyMinProperties = VerifyBoolAndVerifierMethod.withAssertionValueAsInteger();
        verifyMinProperties.setVerifierMethod((node, assertion, validatorInstance) ->
        {
            long nodePropertiesCount = ((JSONObjectTN) node).getProperties().size();

            ValidationResultAndErrorMessage result = new ValidationResultAndErrorMessage();
            if(nodePropertiesCount >= assertion)
                result.setValid(true);
            else{
                result.setValid(false);
                if(node.isRoot())
                    result.setMessage("Object should contain a minimum of "+assertion+ " properties" );
                else
                    result.setMessage("Object "+node.getName() +" should contain a minimum of "+assertion+ " properties" );
            }
            return result;
        });

        VerifyBoolAndVerifierMethod<JSONTreeNode, JSONObjectTN> verifyDependentRequired = VerifyBoolAndVerifierMethod.withAssertionValueAsObject();
        verifyDependentRequired.setVerifierMethod( (node, assertion, validatorInstance) ->
        {
            ArrayList<String> validatedObjectPropertiesNames = ((JSONObjectTN) node).getPropertyNames();

            ValidationResultAndErrorMessage result = new ValidationResultAndErrorMessage();
            result.setValid(true);

            //try{
                //validatedObjectPropertiesNames.forEach(name -> {
                for(String name : validatedObjectPropertiesNames){
                    try {
                        JSONArrayTN propInAssertion = (JSONArrayTN) assertion.getProperties().stream()
                                .filter(assPropName -> assPropName.getName().equals(name))
                                .toList().getFirst();

                        LinkedHashSet<JSONStringTN> namesInArrayAsJSONStringTN =
                                propInAssertion.getItems().stream()
                                .map(e -> (JSONStringTN) e)
                                .collect(Collectors.toCollection(LinkedHashSet::new));


                        for (JSONStringTN e : namesInArrayAsJSONStringTN) {
                            String nameInArray = e.getValue();
                            if (!validatedObjectPropertiesNames.contains(nameInArray)) {
                                result.setValid(false);
                                result.setMessage("Property "+ name +" requires \"" + nameInArray + "\" property to be present");
                                return result;
                                //break;
                            }
                        }


                    }catch (NoSuchElementException e) {}
                     catch (ClassCastException e) {
                        System.out.println(e.getMessage());
                        result.setValid(false);
                        result.setMessage("Properties of keyword \"dependent required\" should be arrays of strings");
                        return result;
                     }
                }
                //});
            return result;
        });

        verifiers.get("object").put("$schema", verify$schema);
        verifiers.get("object").put("type", verifyType);
        verifiers.get("object").put("required", verifyRequired2);
        verifiers.get("object").put("properties", verifyProperties);
        verifiers.get("object").put("maxProperties", verifyMaxProperties);
        verifiers.get("object").put("minProperties", verifyMinProperties);
        verifiers.get("object").put("dependentRequired", verifyDependentRequired);


        verifiers.get("null").put("$schema", verify$schema);
        verifiers.get("null").put("type", verifyType);

        verifiers.get("string").put("$schema", verify$schema);
        verifiers.get("string").put("type", verifyType);
        verifiers.get("string").put("minLength", verifyStringMinLength);
        verifiers.get("string").put("maxLength", verifyStringMaxLength);
        verifiers.get("string").put("pattern", verifyStringPattern);

        verifiers.get("array").put("$schema", verify$schema);
        verifiers.get("array").put("type", verifyType);


        VerifyBoolAndVerifierMethod<JSONTreeNode, JSONTreeNode> verifyAllOf = VerifyBoolAndVerifierMethod.withAssertionValueAsArray();
        verifyAllOf.forEachElementInArray((node, assertion, validatorInstance) ->
        {
            try {
                return new ValidationResultAndErrorMessage(validatorInstance.validateAgainstSchema(node, assertion));
            }catch(JSONValidationException|UnknownValidationKeywordException e){
                return new ValidationResultAndErrorMessage(false, e.getMessage());
            }
        });

        verifiers.get("integer").put("allOf", verifyAllOf);
        verifiers.get("number").put("allOf", verifyAllOf);
        verifiers.get("object").put("allOf", verifyAllOf);
        verifiers.get("null").put("allOf", verifyAllOf);
        verifiers.get("array").put("allOf", verifyAllOf);
        verifiers.get("string").put("allOf", verifyAllOf);

        verifiers.get("boolean").put("$schema", verify$schema);
        verifiers.get("boolean").put("type", verifyType);
        verifiers.get("boolean").put("allOf", verifyAllOf);

    }

    public void setUnknownKeywordBehavior(OnUnknownKeyword onUnknownKeyword){
        this.onUnknownKeyword = onUnknownKeyword;
    }

}
