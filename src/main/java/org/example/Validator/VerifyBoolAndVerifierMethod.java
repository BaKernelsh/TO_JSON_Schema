package org.example.Validator;

import org.example.Function.OperationReturnsValidationResultAndErrorMessage;
import org.example.JSONString;
import org.example.JSONTN.JSONTreeNode;

import javax.xml.validation.Validator;

public class VerifyBoolAndVerifierMethod {

    private OperationReturnsValidationResultAndErrorMessage verifierMethod;
    private boolean verify = true;


    public ValidationResultAndErrorMessage verify(JSONTreeNode node, JSONTreeNode assertion, JSONValidator validatorInstance){
        if(verify)
            return verifierMethod.execute(node, assertion, validatorInstance);
        else
            return ValidationResultAndErrorMessage.newInstanceValid();
    }


    public VerifyBoolAndVerifierMethod(OperationReturnsValidationResultAndErrorMessage verifierMethod) {
        this.verifierMethod = verifierMethod;
    }

    public VerifyBoolAndVerifierMethod() {
    }

    public void setVerifierMethod(OperationReturnsValidationResultAndErrorMessage verifierMethod){
        this.verifierMethod = verifierMethod;
    }

    public boolean getVerify() {
        return verify;
    }

    public void setVerify(boolean verify) {
        this.verify = verify;
    }
}
