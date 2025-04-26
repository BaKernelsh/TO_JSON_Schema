package org.example.Function;

import org.example.JSONTN.JSONTreeNode;

@FunctionalInterface
public interface Operation {
    String execute(JSONTreeNode node);
}
