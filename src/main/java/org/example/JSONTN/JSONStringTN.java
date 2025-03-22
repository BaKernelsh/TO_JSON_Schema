package org.example.JSONTN;


public class JSONStringTN extends JSONTreeNode{

    private String value;

    public JSONStringTN(){

    }

    public JSONStringTN(String type){
        super(type);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
