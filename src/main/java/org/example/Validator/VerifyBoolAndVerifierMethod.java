package org.example.Validator;

import org.example.Function.OperationReturnsValidationResultAndErrorMessage;
import org.example.JSONString;
import org.example.JSONTN.*;

import javax.xml.validation.Validator;

public class VerifyBoolAndVerifierMethod<NodeObjectType extends JSONTreeNode, AssertionObjectType> {

    private OperationReturnsValidationResultAndErrorMessage<NodeObjectType, AssertionObjectType> verifierMethod;
    private boolean verify = true;
    private Class<?> assertionValueClass = null;

    public ValidationResultAndErrorMessage verify(NodeObjectType node, JSONTreeNode assertion, JSONValidator validatorInstance){
        if(verify) {
            if(assertion.getType() == null) //nie ustawil
                return verifierMethod.execute(node, (AssertionObjectType) assertion, validatorInstance);

            if(assertionValueClass == String.class) {
                AssertionObjectType assertionValue = (AssertionObjectType) ((JSONStringTN) assertion).getValue();
                return verifierMethod.execute(node, assertionValue, validatorInstance);
            }

            if(assertionValueClass == Double.class) {
                AssertionObjectType assertionValue = (AssertionObjectType) ((JSONNumberTN) assertion).getValue();
                return verifierMethod.execute(node, assertionValue, validatorInstance);
            }

            if(assertionValueClass == Integer.class) {
                AssertionObjectType assertionValue = (AssertionObjectType) Integer.valueOf(((JSONNumberTN) assertion).getValue().intValue());
                return verifierMethod.execute(node, assertionValue, validatorInstance);
            }

            if(assertionValueClass == Boolean.class) {
                AssertionObjectType assertionValue = (AssertionObjectType) ((JSONBooleanTN) assertion).getValue();
                return verifierMethod.execute(node, assertionValue, validatorInstance);
            }

            if(assertionValueClass == JSONObjectTN.class) {
                return verifierMethod.execute(node, (AssertionObjectType) assertion, validatorInstance);
            }

            if(assertionValueClass == JSONArrayTN.class) {
                return verifierMethod.execute(node, (AssertionObjectType) assertion, validatorInstance);
            }

            return ValidationResultAndErrorMessage.newInstanceValid(); //USUNAC
        }
            //return verifierMethod.execute(node, assertion, validatorInstance);
        else
            return ValidationResultAndErrorMessage.newInstanceValid();
    }



    public VerifyBoolAndVerifierMethod(OperationReturnsValidationResultAndErrorMessage<NodeObjectType, AssertionObjectType> verifierMethod) {
        this.verifierMethod = verifierMethod;
    }




/*    public void forEachItemInArray(OperationReturnsValidationResultAndErrorMessage<NodeObjectType, AssertionObjectType> methodForEachElement){

        verifierMethod = ((node, assertion, validatorInstance) -> {
           boolean result = true;

           for()

        });

    }*/


    public static VerifyBoolAndVerifierMethod<JSONTreeNode, String> withAssertionValueAsString(){
        return new VerifyBoolAndVerifierMethod<JSONTreeNode, String>(String.class);
    }

    private VerifyBoolAndVerifierMethod(Class<?> assertionValueClass){
        this.assertionValueClass = assertionValueClass;
    }

    public VerifyBoolAndVerifierMethod() {
    }


    public void setVerifierMethod(OperationReturnsValidationResultAndErrorMessage<NodeObjectType, AssertionObjectType> verifierMethod){
        this.verifierMethod = verifierMethod;
    }

    public boolean getVerify() {
        return verify;
    }

    public void setVerify(boolean verify) {
        this.verify = verify;
    }
}
