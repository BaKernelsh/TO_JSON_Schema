package org.example.Validator;

import org.example.Generator.Generator;
import org.example.JSONString;
import org.example.JSONTN.JSONNumberTN;
import org.example.JSONTN.JSONObjectTN;
import org.example.JSONTN.JSONStringTN;
import org.example.JSONTN.JSONTreeNode;

import java.util.ArrayList;
import java.util.HashMap;

public class JSONValidator {
    //<typ, <keyword,verifier>
    private HashMap<String, HashMap<String, VerifyBoolAndVerifierMethod>> verifiers = new HashMap<>();
    private final Generator generator = new Generator();
    private OnUnknownKeyword onUnknownKeyword = OnUnknownKeyword.THROW;
    //TODO nie throwowanie przy nieudanej walidacji tylko zbieranie message zeby je póżniej wyświetlić w gui

    public boolean validateAgainstSchema(JSONTreeNode node, String schemaString) throws Exception {
        JSONString schema = new JSONString(schemaString);
        JSONObjectTN schemaRoot = (JSONObjectTN) generator.generateSchemaTree(schema);

        return validateAgainstSchema(node, schemaRoot);
    }

    private boolean validateAgainstSchema(JSONTreeNode node, JSONObjectTN schema) throws Exception {

        boolean result = true;

        ArrayList<JSONTreeNode> assertions = schema.getProperties();
        for(var assertion : assertions){
            boolean resultForAssertion = validateNodeAgainstAssertion(node, assertion, this);
            if(!resultForAssertion)
                result = false;
        }

        return result;

        //pobiera keyword
        //String keyword = "minimum";
        //pobiera value
        //JSONString keywordValue = new JSONString("5");
        //return validateNodeAgainstKeyword(node, keyword, keywordValue, this);

    }


    public boolean validateNodeAgainstAssertion(JSONTreeNode node, JSONTreeNode assertion, JSONValidator validatorInstance) throws Exception {
        String keyword = assertion.getName();

        VerifyBoolAndVerifierMethod verifier = verifiers.get(node.getTypeAsString()).get(keyword);
        if(verifier==null)
            return handleUnknownKeyword(keyword);


        ValidationResultAndErrorMessage validationResult = verifier.verify(node, assertion, validatorInstance);

        if(!validationResult.isValid())
            throw new Exception("Validation failed for keyword: " +keyword + " " +validationResult.getMessage());
        else
            return true;
    }



    public boolean handleUnknownKeyword(String keyword) throws RuntimeException{
        if(onUnknownKeyword == OnUnknownKeyword.KEYWORD_VALIDATION_UNSUCCESFUL_CONTINUE_VALIDATION)
            return false;
        if(onUnknownKeyword == OnUnknownKeyword.KEYWORD_VALIDATION_SUCCESFUL_CONTINUE_VALIDATION)
            return true;
        else
            throw new RuntimeException("Unknown validation keyword: "+keyword);
    }


    public JSONValidator(){
        verifiers.put("integer", new HashMap<>());
        verifiers.put("number", new HashMap<>());

        VerifyBoolAndVerifierMethod verifyType = new VerifyBoolAndVerifierMethod((node,assertion, validatorInstance) ->
        {
            String nodeType = node.getTypeAsString();
            String requiredType = ((JSONStringTN) assertion).getValue();

            ValidationResultAndErrorMessage result = new ValidationResultAndErrorMessage();
            if(nodeType.equals(requiredType))
                result.setValid(true);
            else{
                result.setValid(false);
                if(node.getName() == null)
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

        verifiers.get("integer").put("minimum", verifyMinimum);
        verifiers.get("integer").put("$schema", verify$schema);
        verifiers.get("integer").put("type", verifyType);

        verifiers.get("number").put("minimum", verifyMinimum);
        verifiers.get("number").put("$schema", verify$schema);
        verifiers.get("number").put("type", verifyType);


    }

    public void setUnknownKeywordBehavior(OnUnknownKeyword onUnknownKeyword){
        this.onUnknownKeyword = onUnknownKeyword;
    }

}
