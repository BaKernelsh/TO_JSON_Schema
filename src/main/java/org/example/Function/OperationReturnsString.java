package org.example.Function;

import org.example.JSONTN.JSONTreeNode;

@FunctionalInterface
public interface OperationReturnsString {
    String execute(JSONTreeNode node);
}
