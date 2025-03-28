package org.example.JSONTN;

public abstract class JSONTreeNode {

    private String type;
    private String name;


    public JSONTreeNode() {
    }

    public JSONTreeNode(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
