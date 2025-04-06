package org.example.JSONTN;

public abstract class JSONTreeNode {

    private JSONTreeNodeType type;
    private String name = null;


    public JSONTreeNode() {
    }

    public JSONTreeNode(JSONTreeNodeType type) {
        this.type = type;
    }

    public JSONTreeNodeType getType() {
        return type;
    }

    public void setType(JSONTreeNodeType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeAsString(){
        return "string";
    }

}
