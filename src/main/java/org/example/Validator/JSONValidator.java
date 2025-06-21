package org.example.Validator;

import org.example.Exception.JSONSchemaGeneratorException;
import org.example.Exception.JSONValidationException;
import org.example.Exception.UnknownValidationKeywordException;
import org.example.Generator.Generator;
import org.example.JSONString;
import org.example.JSONTN.*;

import java.util.*;

public class JSONValidator {
    //<typ, <keyword,verifier>
    private HashMap<String, HashMap<String, VerifyBoolAndVerifierMethod>> verifiers = new HashMap<>();
    private final Generator generator = new Generator();
    private OnUnknownKeyword onUnknownKeyword = OnUnknownKeyword.THROW;
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

    public boolean validateAgainstSchema(JSONTreeNode node, JSONObjectTN schema) throws JSONValidationException,
                                                                              UnknownValidationKeywordException
    {
        boolean result = true;

        ArrayList<JSONTreeNode> assertions = schema.getProperties();

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


        VerifyBoolAndVerifierMethod placeholder = new VerifyBoolAndVerifierMethod((node,assertion, validatorInstance) ->
        {
            ValidationResultAndErrorMessage result = new ValidationResultAndErrorMessage();
            result.setValid(true);
            return result;
        });

        VerifyBoolAndVerifierMethod verifyType = new VerifyBoolAndVerifierMethod((node,assertion, validatorInstance) ->
        {
            String nodeType = node.getTypeAsString();
            String requiredType = ((JSONStringTN) assertion).getValue();

            ValidationResultAndErrorMessage result = new ValidationResultAndErrorMessage();
            if(nodeType.equals(requiredType))
                result.setValid(true);
            else{
                result.setValid(false);
                if(node.isRoot())
                    result.setMessage("Invalid type. Should be: " + requiredType);
                else
                    result.setMessage("Invalid type of "+node.getName()+" property. Should be: " +requiredType);
            }
            return result;
        });


        VerifyBoolAndVerifierMethod<JSONTreeNode, String> verifyType3 = VerifyBoolAndVerifierMethod.withAssertionValueAsString();

        verifyType3.setVerifierMethod((node,assertion, validatorInstance) ->
        {
            String nodeType = node.getTypeAsString();
            String requiredType = assertion;

            ValidationResultAndErrorMessage result = new ValidationResultAndErrorMessage();
            if(nodeType.equals(requiredType))
                result.setValid(true);
            else{
                result.setValid(false);
                if(node.isRoot())
                    result.setMessage("Invalid type. Should be: " + requiredType);
                else
                    result.setMessage("Invalid type of "+node.getName()+" property. Should be: " +requiredType);
            }
            return result;
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
        verifiers.get("integer").put("maximum", verifyMaximum);
        verifiers.get("integer").put("$schema", verify$schema);
        verifiers.get("integer").put("type", verifyType3);
        verifiers.get("integer").put("multipleOf", verifyMultipleOfInteger);

        verifiers.get("number").put("minimum", verifyMinimum);
        verifiers.get("number").put("maximum", verifyMaximum);
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
                    result.setValid(validatorInstance.validateAgainstSchema(nodePropertyWithSameName, (JSONObjectTN) commonProp));
                    if(!result.isValid())  //nie ma ustawionej metody dla keywordu i ma byc kontynuowana walidacja
                        result.setIgnoreInvalidity(true);
                }catch (Exception e){
                    result.setValid(false);
                    result.setMessage(e.getMessage());
                    return result;
                }
            }

            return result;
        });

        verifiers.get("object").put("$schema", verify$schema);
        verifiers.get("object").put("type", verifyType);
        verifiers.get("object").put("required", verifyRequired2);
        verifiers.get("object").put("properties", verifyProperties);
        verifiers.get("object").put("maxProperties", placeholder);
        verifiers.get("object").put("minProperties", placeholder);

        verifiers.get("null").put("$schema", verify$schema);
        verifiers.get("null").put("type", verifyType);





    }

    public void setUnknownKeywordBehavior(OnUnknownKeyword onUnknownKeyword){
        this.onUnknownKeyword = onUnknownKeyword;
    }

}
