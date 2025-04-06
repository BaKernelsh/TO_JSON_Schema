package org.example.JSONTN;

import java.util.ArrayList;

public class JSONObjectTN extends JSONTreeNode{

    private ArrayList<JSONTreeNode> properties = new ArrayList<>();
    //private ArrayList<String> required = new ArrayList<>();

    public JSONObjectTN(){

    }

    public JSONObjectTN(JSONTreeNodeType type){
        super(type);
    }

    public void addProperty(JSONTreeNode newProperty){
        properties.removeIf(property -> property.getName().equals(newProperty.getName()));

        properties.add(newProperty);
    }

    public ArrayList<String> getPropertyNames(){
        ArrayList<String> propertyNames = new ArrayList<>();
        properties.forEach(property -> {
            propertyNames.add(property.getName());
        });
        return propertyNames;
    }

    public String getPropertyNamesAsString(){
        String names = "";
        for(String p : getPropertyNames()){
            names = names.concat(p + ", ");
        }
        names = names.substring(0, names.length() - 2);
        return names;
    }

    public ArrayList<JSONTreeNode> getProperties() {
        return properties;
    }

    public void setProperties(ArrayList<JSONTreeNode> properties) {
        this.properties = properties;
    }

    public String getTypeAsString(){
            return "object";
    }

    /*public ArrayList<String> getRequired() {
        return required;
    }*/

    /*public void setRequired(ArrayList<String> required) {
        this.required = required;
    }*/
}
