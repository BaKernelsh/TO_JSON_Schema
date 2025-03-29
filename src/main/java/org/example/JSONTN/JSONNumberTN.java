package org.example.JSONTN;

public class JSONNumberTN<T> extends JSONTreeNode{

    private T value;


    public JSONNumberTN() {
        super();
    }

    public JSONNumberTN(JSONTreeNodeType type) {
        super(type);
    }


    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
