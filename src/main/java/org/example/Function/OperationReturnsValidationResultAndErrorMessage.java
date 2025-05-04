package org.example.Function;

import org.example.JSONString;
import org.example.JSONTN.JSONTreeNode;
import org.example.Validator.JSONValidator;
import org.example.Validator.ValidationResultAndErrorMessage;

import javax.xml.validation.Validator;

@FunctionalInterface
public interface OperationReturnsValidationResultAndErrorMessage {

    ValidationResultAndErrorMessage execute(JSONTreeNode node, JSONString keywordValue, JSONValidator validatorInstance);
}
