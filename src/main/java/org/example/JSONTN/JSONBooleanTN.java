package org.example.JSONTN;

public class JSONBooleanTN extends JSONTreeNode{

    private boolean value;


    public JSONBooleanTN() {
        super();
    }

    public JSONBooleanTN(JSONTreeNodeType type) {
        super(type);
    }


    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public String getTypeAsString(){
        return "boolean";
    }
}
