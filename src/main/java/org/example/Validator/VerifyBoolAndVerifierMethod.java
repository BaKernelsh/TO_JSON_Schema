package org.example.Validator;

import org.example.Function.OperationReturnsValidationResultAndErrorMessage;
import org.example.JSONString;
import org.example.JSONTN.*;

import javax.xml.validation.Validator;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class VerifyBoolAndVerifierMethod<NodeObjectType extends JSONTreeNode, AssertionObjectType> {

    private OperationReturnsValidationResultAndErrorMessage<NodeObjectType, AssertionObjectType> verifierMethod;
    private boolean verify = true;
    private Class<?> assertionValueClass = null;
    private boolean asArray = false;

    public ValidationResultAndErrorMessage verify(NodeObjectType node, JSONTreeNode assertion, JSONValidator validatorInstance){
        if(verify) {
            return castAssertionAndExecuteVerifyingMethod(node, assertion, validatorInstance, verifierMethod);
        }
        else
            return ValidationResultAndErrorMessage.newInstanceValid();

    }

    private ValidationResultAndErrorMessage castAssertionAndExecuteVerifyingMethod(NodeObjectType node, JSONTreeNode assertion, JSONValidator validatorInstance,
                                            OperationReturnsValidationResultAndErrorMessage<NodeObjectType, AssertionObjectType> method){

        if(asArray)
            return method.execute(node, (AssertionObjectType) assertion, validatorInstance);

        if(assertionValueClass == String.class) {
            AssertionObjectType assertionValue = (AssertionObjectType) ((JSONStringTN) assertion).getValue();
            return method.execute(node, assertionValue, validatorInstance);
        }

        else if(assertionValueClass == Double.class) {
            AssertionObjectType assertionValue = (AssertionObjectType) ((JSONNumberTN) assertion).getValue();
            return method.execute(node, assertionValue, validatorInstance);
        }

        else if(assertionValueClass == Integer.class) {
            AssertionObjectType assertionValue = (AssertionObjectType) Integer.valueOf(((JSONNumberTN) assertion).getValue().intValue());
            return method.execute(node, assertionValue, validatorInstance);
        }

        else if(assertionValueClass == Boolean.class) {
            AssertionObjectType assertionValue = (AssertionObjectType) ((JSONBooleanTN) assertion).getValue();
            return method.execute(node, assertionValue, validatorInstance);
        }

        else if(assertionValueClass == JSONObjectTN.class) {
            return method.execute(node, (AssertionObjectType) assertion, validatorInstance);
        }

        else
            return method.execute(node, (AssertionObjectType) assertion, validatorInstance);
    }



    public VerifyBoolAndVerifierMethod(OperationReturnsValidationResultAndErrorMessage<NodeObjectType, AssertionObjectType> verifierMethod) {
        this.verifierMethod = verifierMethod;
    }

    private VerifyBoolAndVerifierMethod(Class<?> assertionValueClass){
        this.assertionValueClass = assertionValueClass;
    }



    public void forEachElementInArray(OperationReturnsValidationResultAndErrorMessage<NodeObjectType, AssertionObjectType> methodForEach){
        asArray = true;

        verifierMethod = ((node, assertion, validatorInstance) -> {
           LinkedHashSet<JSONTreeNode> values = ((JSONArrayTN) assertion).getItems();
           asArray = false;
           for(var value : values){
              ValidationResultAndErrorMessage result = castAssertionAndExecuteVerifyingMethod(node, value, validatorInstance, methodForEach);
              if(!result.isValid())
                  return result;
           }

            asArray = true;
            return ValidationResultAndErrorMessage.newInstanceValid();
        });

    }



    public static VerifyBoolAndVerifierMethod<JSONTreeNode, String> withAssertionValueAsString(){
        return new VerifyBoolAndVerifierMethod<JSONTreeNode, String>(String.class);
    }
    public static VerifyBoolAndVerifierMethod<JSONTreeNode, Double> withAssertionValueAsDouble(){
        return new VerifyBoolAndVerifierMethod<JSONTreeNode, Double>(Double.class);
    }

    public static VerifyBoolAndVerifierMethod<JSONTreeNode, Integer> withAssertionValueAsInteger(){
        return new VerifyBoolAndVerifierMethod<JSONTreeNode, Integer>(Integer.class);
    }

    public static VerifyBoolAndVerifierMethod<JSONTreeNode, Boolean> withAssertionValueAsBoolean(){
        return new VerifyBoolAndVerifierMethod<JSONTreeNode, Boolean>(Integer.class);
    }

    public static VerifyBoolAndVerifierMethod<JSONTreeNode, JSONObjectTN> withAssertionValueAsObject(){
        return new VerifyBoolAndVerifierMethod<JSONTreeNode, JSONObjectTN>(JSONObjectTN.class);
    }

    public static VerifyBoolAndVerifierMethod<JSONTreeNode, JSONTreeNode> withAssertionValueAsArray(){ // nic nie zmienia, moze poprawic czytelnosc kodu klienta ?
        return new VerifyBoolAndVerifierMethod<JSONTreeNode, JSONTreeNode>(JSONTreeNode.class);
    }


    public static VerifyBoolAndVerifierMethod<JSONTreeNode, String> withAssertionValueAsArrayOfString(){
        VerifyBoolAndVerifierMethod<JSONTreeNode, String> newInstance = withAssertionValueAsString();
        return withAssertionValueAsString();
    }

    public static VerifyBoolAndVerifierMethod<JSONTreeNode, Double> withAssertionValueAsArrayOfDouble(){
        return withAssertionValueAsDouble();
    }
    public static VerifyBoolAndVerifierMethod<JSONTreeNode, Integer> withAssertionValueAsArrayOfInteger(){
        return withAssertionValueAsInteger();
    }

    public static VerifyBoolAndVerifierMethod<JSONTreeNode, Boolean> withAssertionValueAsArrayOfBoolean(){
        return withAssertionValueAsBoolean();
    }

    public static VerifyBoolAndVerifierMethod<JSONTreeNode, JSONObjectTN> withAssertionValueAsArrayOfObject(){
        return withAssertionValueAsObject();
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

    private void setAsArray(){
        asArray = true;
    }
}
