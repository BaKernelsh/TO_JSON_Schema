import org.example.Exception.JSONSchemaGeneratorException;
import org.example.Generator.AssertionConfiguration;
import org.example.Generator.Generator;
import org.example.JSONString;
import org.example.JSONTN.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class GeneratorForArraysTest {

    static Generator generator;

    @BeforeAll
    public static void setGeneratorInstance(){
        AssertionConfiguration assertionConfiguration = new AssertionConfiguration();
        assertionConfiguration.setAllUnused();

        generator = new Generator(assertionConfiguration);
    }

    @Test
    public void generateTreeFromEmptyArray(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("[]");
            JSONTreeNode result = generator.generateJsonTree(json);

            Assertions.assertInstanceOf(JSONArrayTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.ARRAY, result.getType());
            Assertions.assertEquals(0, ((JSONArrayTN) result).getItems().size());
        });
    }

    @Test
    public void generateTreeFromArrayWithArrayItem(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("[[]]");
            JSONTreeNode result = generator.generateJsonTree(json);

            Assertions.assertInstanceOf(JSONArrayTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.ARRAY, result.getType());
            Assertions.assertEquals(1, ((JSONArrayTN) result).getItems().size());

            JSONTreeNode subArray = ((JSONArrayTN) result).getItems().getFirst();
            Assertions.assertInstanceOf(JSONArrayTN.class, subArray);
            Assertions.assertEquals(JSONTreeNodeType.ARRAY, subArray.getType());
            Assertions.assertEquals(0, ((JSONArrayTN) subArray).getItems().size());
        });
    }


    @Test
    public void generateTreeFromArrayWithMultipleArrayItems(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("[[ [] ],[ \n]]");
            JSONTreeNode result = generator.generateJsonTree(json);

            Assertions.assertInstanceOf(JSONArrayTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.ARRAY, result.getType());
            Assertions.assertEquals(2, ((JSONArrayTN) result).getItems().size());

            JSONTreeNode subArray = ((JSONArrayTN) result).getItems().getFirst();
            Assertions.assertInstanceOf(JSONArrayTN.class, subArray);
            Assertions.assertEquals(JSONTreeNodeType.ARRAY, subArray.getType());
            Assertions.assertEquals(1, ((JSONArrayTN) subArray).getItems().size());

            subArray = ((JSONArrayTN) result).getItems().getLast();
            Assertions.assertInstanceOf(JSONArrayTN.class, subArray);
            Assertions.assertEquals(JSONTreeNodeType.ARRAY, subArray.getType());
            Assertions.assertEquals(0, ((JSONArrayTN) subArray).getItems().size());
        });
    }

    @Test
    public void generateTreeFromArrayWithStringItem(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("[\"jakis Napis\"]");
            JSONTreeNode result = generator.generateJsonTree(json);

            Assertions.assertInstanceOf(JSONArrayTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.ARRAY, result.getType());
            Assertions.assertEquals(1, ((JSONArrayTN) result).getItems().size());

            JSONTreeNode stringItem = ((JSONArrayTN) result).getItems().getFirst();
            Assertions.assertInstanceOf(JSONStringTN.class, stringItem);
            Assertions.assertEquals(JSONTreeNodeType.STRING, stringItem.getType());
            Assertions.assertEquals("jakis Napis", ((JSONStringTN) stringItem).getValue());
        });
    }

    @Test
    public void generateTreeFromArrayWithMultipleStringItems(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("[\"jakis Napis\", \"drugi.naPis\"]");
            JSONTreeNode result = generator.generateJsonTree(json);

            Assertions.assertInstanceOf(JSONArrayTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.ARRAY, result.getType());
            Assertions.assertEquals(2, ((JSONArrayTN) result).getItems().size());

            JSONTreeNode stringItem = ((JSONArrayTN) result).getItems().getFirst();
            Assertions.assertInstanceOf(JSONStringTN.class, stringItem);
            Assertions.assertEquals(JSONTreeNodeType.STRING, stringItem.getType());
            Assertions.assertEquals("jakis Napis", ((JSONStringTN) stringItem).getValue());

            stringItem = ((JSONArrayTN) result).getItems().getLast();
            Assertions.assertInstanceOf(JSONStringTN.class, stringItem);
            Assertions.assertEquals(JSONTreeNodeType.STRING, stringItem.getType());
            Assertions.assertEquals("drugi.naPis", ((JSONStringTN) stringItem).getValue());
        });
    }

    @Test
    public void generateTreeFromArrayWithNullItem(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("[null]");
            JSONTreeNode result = generator.generateJsonTree(json);

            Assertions.assertInstanceOf(JSONArrayTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.ARRAY, result.getType());
            Assertions.assertEquals(1, ((JSONArrayTN) result).getItems().size());

            JSONTreeNode nullItem = ((JSONArrayTN) result).getItems().getFirst();
            Assertions.assertInstanceOf(JSONNullTN.class, nullItem);
            Assertions.assertEquals(JSONTreeNodeType.NULL, nullItem.getType());
        });
    }

    @Test
    public void generateTreeFromArrayWithMultipleNullItems(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("[null, null]");
            JSONTreeNode result = generator.generateJsonTree(json);

            Assertions.assertInstanceOf(JSONArrayTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.ARRAY, result.getType());
            Assertions.assertEquals(2, ((JSONArrayTN) result).getItems().size());

            JSONTreeNode nullItem = ((JSONArrayTN) result).getItems().getFirst();
            Assertions.assertInstanceOf(JSONNullTN.class, nullItem);
            Assertions.assertEquals(JSONTreeNodeType.NULL, nullItem.getType());

            nullItem = ((JSONArrayTN) result).getItems().getLast();
            Assertions.assertInstanceOf(JSONNullTN.class, nullItem);
            Assertions.assertEquals(JSONTreeNodeType.NULL, nullItem.getType());
        });
    }

    @Test
    public void generateTreeFromArrayWithTrueItem(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("[true]");
            JSONTreeNode result = generator.generateJsonTree(json);

            Assertions.assertInstanceOf(JSONArrayTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.ARRAY, result.getType());
            Assertions.assertEquals(1, ((JSONArrayTN) result).getItems().size());

            JSONTreeNode booleanItem = ((JSONArrayTN) result).getItems().getFirst();
            Assertions.assertInstanceOf(JSONBooleanTN.class, booleanItem);
            Assertions.assertEquals(JSONTreeNodeType.BOOLEAN, booleanItem.getType());
            Assertions.assertTrue(((JSONBooleanTN) booleanItem).getValue());
        });
    }

    @Test
    public void generateTreeFromArrayWithFalseItem(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("[false]");
            JSONTreeNode result = generator.generateJsonTree(json);

            Assertions.assertInstanceOf(JSONArrayTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.ARRAY, result.getType());
            Assertions.assertEquals(1, ((JSONArrayTN) result).getItems().size());

            JSONTreeNode booleanItem = ((JSONArrayTN) result).getItems().getFirst();
            Assertions.assertInstanceOf(JSONBooleanTN.class, booleanItem);
            Assertions.assertEquals(JSONTreeNodeType.BOOLEAN, booleanItem.getType());
            Assertions.assertFalse(((JSONBooleanTN) booleanItem).getValue());
        });
    }

    @Test
    public void generateTreeFromArrayWithMultipleBoolItem(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("[false, true]");
            JSONTreeNode result = generator.generateJsonTree(json);

            Assertions.assertInstanceOf(JSONArrayTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.ARRAY, result.getType());
            Assertions.assertEquals(2, ((JSONArrayTN) result).getItems().size());

            JSONTreeNode booleanItem = ((JSONArrayTN) result).getItems().getFirst();
            Assertions.assertInstanceOf(JSONBooleanTN.class, booleanItem);
            Assertions.assertEquals(JSONTreeNodeType.BOOLEAN, booleanItem.getType());
            Assertions.assertFalse(((JSONBooleanTN) booleanItem).getValue());

            booleanItem = ((JSONArrayTN) result).getItems().getLast();
            Assertions.assertInstanceOf(JSONBooleanTN.class, booleanItem);
            Assertions.assertEquals(JSONTreeNodeType.BOOLEAN, booleanItem.getType());
            Assertions.assertTrue(((JSONBooleanTN) booleanItem).getValue());
        });
    }


    @Test
    public void generateTreeFromArrayWithIntegerItem(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("[123456]");
            JSONTreeNode result = generator.generateJsonTree(json);

            Assertions.assertInstanceOf(JSONArrayTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.ARRAY, result.getType());
            Assertions.assertEquals(1, ((JSONArrayTN) result).getItems().size());

            //sprawdzenie integera
            JSONTreeNode integerProperty = ((JSONArrayTN) result).getItems().getFirst();
            Assertions.assertInstanceOf(JSONNumberTN.class, integerProperty);
            Assertions.assertEquals(JSONTreeNodeType.NUMBER, integerProperty.getType());
            Assertions.assertEquals(123456, ((JSONNumberTN) integerProperty).getValue());
        });
    }

    @Test
    public void generateTreeFromArrayWithIntegerWithLeadingZerosItem(){
        var e = Assertions.assertThrows(JSONSchemaGeneratorException.class, () -> {
            JSONString json = new JSONString("[000123]");
            generator.generateJsonTree(json);
        });
        Assertions.assertEquals("Leading zeros are not allowed", e.getMessage());
    }

    @Test
    public void generateTreeFromArrayWithMultipleIntegerItems(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("[123456, 98735]");
            JSONTreeNode result = generator.generateJsonTree(json);

            Assertions.assertInstanceOf(JSONArrayTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.ARRAY, result.getType());
            Assertions.assertEquals(2, ((JSONArrayTN) result).getItems().size());

            //sprawdzenie integerow
            JSONTreeNode integerProperty = ((JSONArrayTN) result).getItems().getFirst();
            Assertions.assertInstanceOf(JSONNumberTN.class, integerProperty);
            Assertions.assertEquals(JSONTreeNodeType.NUMBER, integerProperty.getType());
            Assertions.assertEquals(123456, ((JSONNumberTN) integerProperty).getValue());

            integerProperty = ((JSONArrayTN) result).getItems().getLast();
            Assertions.assertInstanceOf(JSONNumberTN.class, integerProperty);
            Assertions.assertEquals(JSONTreeNodeType.NUMBER, integerProperty.getType());
            Assertions.assertEquals(98735, ((JSONNumberTN) integerProperty).getValue());
        });
    }


    @Test
    public void generateTreeFromArrayWithEmptyObjectItem(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("[{}]");
            JSONTreeNode result = generator.generateJsonTree(json);

            Assertions.assertInstanceOf(JSONArrayTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.ARRAY, result.getType());
            Assertions.assertEquals(1, ((JSONArrayTN) result).getItems().size());

            JSONTreeNode objectItem = ((JSONArrayTN) result).getItems().getFirst();
            Assertions.assertInstanceOf(JSONObjectTN.class, objectItem);
            Assertions.assertEquals(JSONTreeNodeType.OBJECT, objectItem.getType());
            Assertions.assertEquals(0, ((JSONObjectTN) objectItem).getProperties().size());

        });
    }



    @Test
    public void generateTreeFromArrayWithObjectItem(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("[{\"prop1\": \"string\", \"prop2\": null}]");
            JSONTreeNode result = generator.generateJsonTree(json);

            Assertions.assertInstanceOf(JSONArrayTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.ARRAY, result.getType());
            Assertions.assertEquals(1, ((JSONArrayTN) result).getItems().size());

            JSONTreeNode objectItem = ((JSONArrayTN) result).getItems().getFirst();
            Assertions.assertInstanceOf(JSONObjectTN.class, objectItem);
            Assertions.assertEquals(JSONTreeNodeType.OBJECT, objectItem.getType());
            Assertions.assertEquals(2, ((JSONObjectTN) objectItem).getProperties().size());

                JSONTreeNode stringProperty = ((JSONObjectTN) objectItem).getProperties().getFirst();
                Assertions.assertInstanceOf(JSONStringTN.class, stringProperty);
                Assertions.assertEquals(JSONTreeNodeType.STRING, stringProperty.getType());
                Assertions.assertEquals("prop1", stringProperty.getName());
                Assertions.assertEquals("string", ((JSONStringTN) stringProperty).getValue());

                JSONTreeNode nullProperty = ((JSONObjectTN) objectItem).getProperties().getLast();
                Assertions.assertInstanceOf(JSONNullTN.class, nullProperty);
                Assertions.assertEquals(JSONTreeNodeType.NULL, nullProperty.getType());
                Assertions.assertEquals("prop2", nullProperty.getName());

        });
    }

    @Test
    public void generateTreeFromArrayWithInvalidElement(){
        var e = Assertions.assertThrows(JSONSchemaGeneratorException.class, () -> {
            JSONString json = new JSONString("[invalid]");
            generator.generateJsonTree(json);
        });
        Assertions.assertEquals("Unexpected character: i", e.getMessage());
    }

}
