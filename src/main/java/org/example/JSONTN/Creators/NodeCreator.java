package org.example.JSONTN.Creators;

import org.example.Exception.JSONSchemaGeneratorException;
import org.example.Generator.Generator;
import org.example.JSONString;
import org.example.JSONTN.*;

public class NodeCreator {

    public static JSONTreeNode createNode(Character nextChar, JSONString json) throws JSONSchemaGeneratorException {

        switch(nextChar){
            case '0': case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
            case '-':
                String number = json.getNumber();
                JSONNumberTN newNumberNode = new JSONNumberTN(JSONTreeNodeType.NUMBER);
                newNumberNode.setValue(number);
                return newNumberNode;

            case 'n':
                json.getNull(); // jesli to nie będzie "null" to rzuci Exception
                return new JSONNullTN(JSONTreeNodeType.NULL);

            case 't': // jesli to nie będzie "true" to rzuci Exception
                json.getBoolean();
                JSONBooleanTN newTrueNode = new JSONBooleanTN(JSONTreeNodeType.BOOLEAN);
                newTrueNode.setValue(true);
                return newTrueNode;

            case 'f': // jesli to nie będzie "false" to rzuci Exception
                json.getBoolean();
                JSONBooleanTN newFalseNode = new JSONBooleanTN(JSONTreeNodeType.BOOLEAN);
                newFalseNode.setValue(false);
                return newFalseNode;

            case '"':
                json.getNextCharAndRemoveOmitWhitespaces();
                String string = json.getPropertyName();
                JSONStringTN newStringNode = new JSONStringTN(JSONTreeNodeType.STRING);
                newStringNode.setValue(string);
                return newStringNode;

            case '{':
                json.getNextCharAndRemoveOmitWhitespaces();
                JSONObjectTN newObjectNode = Generator.processObject(json);
                return newObjectNode;

            case '[':
                json.getNextCharAndRemoveOmitWhitespaces();
                JSONArrayTN newArrayNode = Generator.processArray(json);
                return newArrayNode;

            default:
                throw new JSONSchemaGeneratorException("Unexpected character: " + nextChar);
        }
    }
}
