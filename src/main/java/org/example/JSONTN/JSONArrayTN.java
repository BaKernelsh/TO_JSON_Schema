package org.example.JSONTN;

import java.util.LinkedHashSet;

public class JSONArrayTN extends JSONTreeNode{

    private LinkedHashSet<JSONTreeNode> items = new LinkedHashSet<>();


    public JSONArrayTN() {
        super();
    }

    public JSONArrayTN(JSONTreeNodeType type) {
        super(type);
    }

    public void addItem(JSONTreeNode node){
        items.add(node);
    }

    public LinkedHashSet<JSONTreeNode> getItems() {
        return items;
    }

    public void setItems(LinkedHashSet<JSONTreeNode> items) {
        this.items = items;
    }
}
