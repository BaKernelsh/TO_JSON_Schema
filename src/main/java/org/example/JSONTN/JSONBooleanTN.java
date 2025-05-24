package org.example.JSONTN;

public class JSONBooleanTN extends JSONTreeNode{

    private Boolean value;


    public JSONBooleanTN() {
        super();
    }

    public JSONBooleanTN(JSONTreeNodeType type) {
        super(type);
    }


    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    public String getTypeAsString(){
        return "boolean";
    }
}
