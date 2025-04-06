package org.example.JSONTN;

public class JSONNumberTN extends JSONTreeNode{

    private Double value;


    public JSONNumberTN() {
        super();
    }

    public JSONNumberTN(JSONTreeNodeType type) {
        super(type);
    }


    public Double getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = Double.valueOf(value);
    }

    public String getTypeAsString(){
        if(value % 1 == 0)
            return "integer";
        else
            return "number";
    }
}
