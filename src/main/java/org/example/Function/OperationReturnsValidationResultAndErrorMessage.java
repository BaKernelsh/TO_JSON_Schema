package org.example.Function;

import org.example.JSONTN.JSONTreeNode;
import org.example.Validator.JSONValidator;
import org.example.Validator.ValidationResultAndErrorMessage;


@FunctionalInterface
public interface OperationReturnsValidationResultAndErrorMessage<NodeObjectType extends JSONTreeNode, AssertionObjectType> {

    ValidationResultAndErrorMessage execute(NodeObjectType node, AssertionObjectType assertion, JSONValidator validatorInstance);

}
