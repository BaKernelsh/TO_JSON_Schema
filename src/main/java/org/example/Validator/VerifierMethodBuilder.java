package org.example.Validator;

import org.example.JSONTN.JSONStringTN;
import org.example.JSONTN.JSONTreeNode;


public class VerifierMethodBuilder {

    private Class<? extends JSONTreeNode> nodeType = JSONTreeNode.class;
    private Class<? extends JSONTreeNode> assertionType = JSONTreeNode.class;


   public VerifierMethodBuilder withAssertionTypeAsString(){
       assertionType = JSONStringTN.class;
       return this;
   }

/*    public VerifyBoolAndVerifierMethod g(){
        return new VerifyBoolAndVerifierMethod<nodeType, assertionType>();
    }*/

}
