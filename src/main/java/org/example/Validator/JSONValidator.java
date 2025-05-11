package org.example.Validator;

import org.example.Generator.Generator;
import org.example.JSONString;
import org.example.JSONTN.JSONNumberTN;
import org.example.JSONTN.JSONTreeNode;
import java.util.HashMap;

public class JSONValidator {
    //<typ, <keyword,verifier>
    private HashMap<String, HashMap<String, VerifyBoolAndVerifierMethod>> verifiers = new HashMap<>();
    private final Generator generator = new Generator();
    private OnUnknownKeyword onUnknownKeyword = OnUnknownKeyword.THROW;
    //TODO nie throwowanie przy nieudanej walidacji tylko zbieranie message zeby je póżniej wyświetlić w gui

    public boolean validateAgainstSchema(JSONTreeNode node, String schemaString) throws Exception {
        JSONString schema = new JSONString(schemaString);
        JSONTreeNode schemaRoot = generator.generateSchemaTree(schema);

        return validateAgainstSchema(node, schemaRoot);
    }

    private boolean validateAgainstSchema(JSONTreeNode node, JSONTreeNode schema) throws Exception {


        //throw new RuntimeException("Not implemented");
        //pobiera keyword
        String keyword = "minimum";
        //pobiera value
        JSONString keywordValue = new JSONString("5");
        return validateNodeAgainstKeyword(node, keyword, keywordValue, this);

    }


    public boolean validateNodeAgainstKeyword(JSONTreeNode node, String keyword, JSONString keywordValue, JSONValidator validatorInstance) throws Exception {

        VerifyBoolAndVerifierMethod verifier = verifiers.get(node.getTypeAsString()).get(keyword);
        if(verifier==null)
            return handleUnknownKeyword(keyword);

        ValidationResultAndErrorMessage validationResult = verifier.verify(node, keywordValue, validatorInstance);

        if(!validationResult.isValid())
            throw new Exception("Validation failed for keyword: " +keyword + " " +validationResult.getMessage());
        else
            return true;
    }


    public boolean handleUnknownKeyword(String keyword) throws RuntimeException{
        if(onUnknownKeyword == OnUnknownKeyword.FALSE)
            return false;
        if(onUnknownKeyword == OnUnknownKeyword.TRUE)
            return true;
        else
            throw new RuntimeException("Unknown validation keyword: "+keyword);
    }


    public JSONValidator(){
        verifiers.put("integer", new HashMap<>());

        verifiers.get("integer").put("minimum", new VerifyBoolAndVerifierMethod((node,keywordValue, validatorInstance) ->
                {
                    Double minimum = Double.parseDouble(keywordValue.getJson());
                    ValidationResultAndErrorMessage result = new ValidationResultAndErrorMessage();
                    if( ((JSONNumberTN) node).getValue() >= minimum )
                        result.setValid(true);
                    else {
                        result.setValid(false);
                        result.setMessage("integer value of "+node.getName()+" must be equal to or greater than "+minimum);
                    }
                    return result;
                }));
    }

}
