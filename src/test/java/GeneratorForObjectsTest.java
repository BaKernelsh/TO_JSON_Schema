import org.example.Exception.JSONSchemaGeneratorException;
import org.example.Generator.Generator;
import org.example.JSONString;
import org.example.JSONTN.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GeneratorForObjectsTest {

    @Test
    public void generateTreeFromEmptyObject(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("{}");
            JSONTreeNode result = Generator.generateSchemaTree(json);
            Assertions.assertInstanceOf(JSONObjectTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.OBJECT, result.getType());
            Assertions.assertNull(result.getName());
            Assertions.assertEquals(0, ((JSONObjectTN) result).getProperties().size());
        });
    }

    @Test
    public void generateTreeFromObjectWithMissingPropertyQuotes(){
        var e = Assertions.assertThrows(JSONSchemaGeneratorException.class, () -> {
            JSONString json = new JSONString("{\"}");
            Generator.generateSchemaTree(json);
        });
        Assertions.assertEquals("Unterminated string", e.getMessage());
    }

    @Test
    public void generateTreeFromObjectWithMissingColonAfterPropName(){
        var e = Assertions.assertThrows(JSONSchemaGeneratorException.class, () -> {
            JSONString json = new JSONString("{\"prop\"  costam ");
            Generator.generateSchemaTree(json);
        });
        Assertions.assertEquals("Unexpected character", e.getMessage());
    }

    @Test
    public void generateTreeFromObjectShouldThrowUnexpectedEndOfJson(){
        var e = Assertions.assertThrows(JSONSchemaGeneratorException.class, () -> {
            JSONString json = new JSONString("{\"prop\":   ");
            Generator.generateSchemaTree(json);
        });
        Assertions.assertEquals("Unexpected end of JSON", e.getMessage());
    }

    @Test
    public void generateTreeFromObjectWithInvalidValueAfterColon(){
        var e = Assertions.assertThrows(JSONSchemaGeneratorException.class, () -> {
            JSONString json = new JSONString("{\"prop\":   wqerrty");
            Generator.generateSchemaTree(json);
        });
        Assertions.assertEquals("Unexpected character: w", e.getMessage());
    }


    @Test
    public void generateTreeFromObjectWithNull(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("{\"prop1\": null}");
            JSONTreeNode result = Generator.generateSchemaTree(json);
            //sprawdzenie roota
            Assertions.assertInstanceOf(JSONObjectTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.OBJECT, result.getType());
            Assertions.assertNull(result.getName());
            Assertions.assertEquals(1, ((JSONObjectTN) result).getProperties().size());

            //sprawdzenie nulla
            JSONTreeNode nullProperty = ((JSONObjectTN) result).getProperties().getFirst();
            Assertions.assertInstanceOf(JSONNullTN.class, nullProperty);
            Assertions.assertEquals(JSONTreeNodeType.NULL, nullProperty.getType());
            Assertions.assertEquals("prop1", nullProperty.getName());
        });
    }

    @Test
    public void generateTreeFromObjectWithMultipleNulls(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("{\"prop1\": null,\"prop2\": null, \"prop3\": null}");
            JSONTreeNode result = Generator.generateSchemaTree(json);
            //sprawdzenie roota
            Assertions.assertInstanceOf(JSONObjectTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.OBJECT, result.getType());
            Assertions.assertNull(result.getName());
            Assertions.assertEquals(3, ((JSONObjectTN) result).getProperties().size());

            //sprawdzenie nulli
            JSONTreeNode nullProperty;
            nullProperty = ((JSONObjectTN) result).getProperties().getFirst();
            Assertions.assertInstanceOf(JSONNullTN.class, nullProperty);
            Assertions.assertEquals(JSONTreeNodeType.NULL, nullProperty.getType());
            Assertions.assertEquals("prop1", nullProperty.getName());

            nullProperty = ((JSONObjectTN) result).getProperties().get(1);
            Assertions.assertInstanceOf(JSONNullTN.class, nullProperty);
            Assertions.assertEquals(JSONTreeNodeType.NULL, nullProperty.getType());
            Assertions.assertEquals("prop2", nullProperty.getName());

            nullProperty = ((JSONObjectTN) result).getProperties().get(2);
            Assertions.assertInstanceOf(JSONNullTN.class, nullProperty);
            Assertions.assertEquals(JSONTreeNodeType.NULL, nullProperty.getType());
            Assertions.assertEquals("prop3", nullProperty.getName());

        });
    }


    @Test
    public void generateTreeFromObjectWithTrue(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("{\"prop1\": true}");
            JSONTreeNode result = Generator.generateSchemaTree(json);
            //sprawdzenie roota
            Assertions.assertInstanceOf(JSONObjectTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.OBJECT, result.getType());
            Assertions.assertNull(result.getName());
            Assertions.assertEquals(1, ((JSONObjectTN) result).getProperties().size());

            //sprawdzenie boola
            JSONTreeNode booleanProperty = ((JSONObjectTN) result).getProperties().getFirst();
            Assertions.assertInstanceOf(JSONBooleanTN.class, booleanProperty);
            Assertions.assertEquals(JSONTreeNodeType.BOOLEAN, booleanProperty.getType());
            Assertions.assertEquals("prop1", booleanProperty.getName());
            Assertions.assertTrue(((JSONBooleanTN) booleanProperty).getValue());
        });
    }

    @Test
    public void generateTreeFromObjectWithFalse(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("{\"prop1\": false}");
            JSONTreeNode result = Generator.generateSchemaTree(json);
            //sprawdzenie roota
            Assertions.assertInstanceOf(JSONObjectTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.OBJECT, result.getType());
            Assertions.assertNull(result.getName());
            Assertions.assertEquals(1, ((JSONObjectTN) result).getProperties().size());

            //sprawdzenie boola
            JSONTreeNode booleanProperty = ((JSONObjectTN) result).getProperties().getFirst();
            Assertions.assertInstanceOf(JSONBooleanTN.class, booleanProperty);
            Assertions.assertEquals(JSONTreeNodeType.BOOLEAN, booleanProperty.getType());
            Assertions.assertFalse(((JSONBooleanTN) booleanProperty).getValue());
            Assertions.assertEquals("prop1", booleanProperty.getName());
        });
    }

    @Test
    public void generateTreeFromObjectWithMultipleBoolean(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("{\"prop1\": false, \"prop2\": true, \"prop3\": false, \"prop4\": true}");
            JSONTreeNode result = Generator.generateSchemaTree(json);
            //sprawdzenie roota
            Assertions.assertInstanceOf(JSONObjectTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.OBJECT, result.getType());
            Assertions.assertNull(result.getName());
            int propertiesCount = ((JSONObjectTN) result).getProperties().size();
            Assertions.assertEquals(4, propertiesCount);

            //sprawdzenie booli
            JSONTreeNode booleanProperty;
            booleanProperty = ((JSONObjectTN) result).getProperties().getFirst();
            Assertions.assertInstanceOf(JSONBooleanTN.class, booleanProperty);
            Assertions.assertEquals(JSONTreeNodeType.BOOLEAN, booleanProperty.getType());
            Assertions.assertFalse(((JSONBooleanTN) booleanProperty).getValue());
            Assertions.assertEquals("prop1", booleanProperty.getName());

            booleanProperty = ((JSONObjectTN) result).getProperties().get(1);
            Assertions.assertInstanceOf(JSONBooleanTN.class, booleanProperty);
            Assertions.assertEquals(JSONTreeNodeType.BOOLEAN, booleanProperty.getType());
            Assertions.assertTrue(((JSONBooleanTN) booleanProperty).getValue());
            Assertions.assertEquals("prop2", booleanProperty.getName());

            booleanProperty = ((JSONObjectTN) result).getProperties().get(2);
            Assertions.assertInstanceOf(JSONBooleanTN.class, booleanProperty);
            Assertions.assertEquals(JSONTreeNodeType.BOOLEAN, booleanProperty.getType());
            Assertions.assertFalse(((JSONBooleanTN) booleanProperty).getValue());
            Assertions.assertEquals("prop3", booleanProperty.getName());

            booleanProperty = ((JSONObjectTN) result).getProperties().get(3);
            Assertions.assertInstanceOf(JSONBooleanTN.class, booleanProperty);
            Assertions.assertEquals(JSONTreeNodeType.BOOLEAN, booleanProperty.getType());
            Assertions.assertTrue(((JSONBooleanTN) booleanProperty).getValue());
            Assertions.assertEquals("prop4", booleanProperty.getName());
        });
    }

    @Test
    public void generateTreeFromObjectWithString(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("{\"prop1\": \"Jakis  Napis\",}");
            JSONTreeNode result = Generator.generateSchemaTree(json);
            //sprawdzenie roota
            Assertions.assertInstanceOf(JSONObjectTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.OBJECT, result.getType());
            Assertions.assertNull(result.getName());
            Assertions.assertEquals(1, ((JSONObjectTN) result).getProperties().size());
            //sprawdzenie stringa
            JSONTreeNode stringProperty = ((JSONObjectTN) result).getProperties().getFirst();
            Assertions.assertInstanceOf(JSONStringTN.class, stringProperty);
            Assertions.assertEquals(JSONTreeNodeType.STRING, stringProperty.getType());
            Assertions.assertEquals("Jakis  Napis", ((JSONStringTN) stringProperty).getValue());
            Assertions.assertEquals("prop1", stringProperty.getName());
        });
    }

    @Test
    public void generateTreeFromObjectWithInteger(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("{\"prop\": 123}");
            JSONTreeNode result = Generator.generateSchemaTree(json);
            //sprawdzenie roota
            Assertions.assertInstanceOf(JSONObjectTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.OBJECT, result.getType());
            Assertions.assertNull(result.getName());
            Assertions.assertEquals(1, ((JSONObjectTN) result).getProperties().size());
            //sprawdzenie integera
            JSONTreeNode integerProperty = ((JSONObjectTN) result).getProperties().getFirst();
            Assertions.assertInstanceOf(JSONNumberTN.class, integerProperty);
            Assertions.assertEquals(JSONTreeNodeType.NUMBER, integerProperty.getType());
            Assertions.assertEquals("prop", integerProperty.getName());
            Assertions.assertEquals(123, ((JSONNumberTN) integerProperty).getValue());
        });
    }

    @Test
    public void generateTreeFromObjectWithNegativeInteger(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("{\"prop\": -123}");
            JSONTreeNode result = Generator.generateSchemaTree(json);
            //sprawdzenie roota
            Assertions.assertInstanceOf(JSONObjectTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.OBJECT, result.getType());
            Assertions.assertNull(result.getName());
            Assertions.assertEquals(1, ((JSONObjectTN) result).getProperties().size());
            //sprawdzenie integera
            JSONTreeNode integerProperty = ((JSONObjectTN) result).getProperties().getFirst();
            Assertions.assertInstanceOf(JSONNumberTN.class, integerProperty);
            Assertions.assertEquals(JSONTreeNodeType.NUMBER, integerProperty.getType());
            Assertions.assertEquals("prop", integerProperty.getName());
            Assertions.assertEquals(-123, ((JSONNumberTN) integerProperty).getValue());
        });
    }

    @Test
    public void generateTreeFromObjectWithDouble(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("{\"prop\": 123.45678909}");
            JSONTreeNode result = Generator.generateSchemaTree(json);
            //sprawdzenie roota
            Assertions.assertInstanceOf(JSONObjectTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.OBJECT, result.getType());
            Assertions.assertNull(result.getName());
            Assertions.assertEquals(1, ((JSONObjectTN) result).getProperties().size());
            //sprawdzenie integera
            JSONTreeNode integerProperty = ((JSONObjectTN) result).getProperties().getFirst();
            Assertions.assertInstanceOf(JSONNumberTN.class, integerProperty);
            Assertions.assertEquals(JSONTreeNodeType.NUMBER, integerProperty.getType());
            Assertions.assertEquals("prop", integerProperty.getName());
            Assertions.assertEquals(123.45678909, ((JSONNumberTN) integerProperty).getValue());
        });
    }

    @Test
    public void generateTreeFromObjectWithNegativeDouble(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("{\"prop\": -123.45678909}");
            JSONTreeNode result = Generator.generateSchemaTree(json);
            //sprawdzenie roota
            Assertions.assertInstanceOf(JSONObjectTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.OBJECT, result.getType());
            Assertions.assertNull(result.getName());
            Assertions.assertEquals(1, ((JSONObjectTN) result).getProperties().size());
            //sprawdzenie integera
            JSONTreeNode integerProperty = ((JSONObjectTN) result).getProperties().getFirst();
            Assertions.assertInstanceOf(JSONNumberTN.class, integerProperty);
            Assertions.assertEquals(JSONTreeNodeType.NUMBER, integerProperty.getType());
            Assertions.assertEquals("prop", integerProperty.getName());
            Assertions.assertEquals(-123.45678909, ((JSONNumberTN) integerProperty).getValue());
        });
    }

    @Test
    public void generateTreeFromObjectWithObject(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("{\"prop\": {}}");
            JSONTreeNode result = Generator.generateSchemaTree(json);
            //sprawdzenie roota
            Assertions.assertInstanceOf(JSONObjectTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.OBJECT, result.getType());
            Assertions.assertNull(result.getName());
            Assertions.assertEquals(1, ((JSONObjectTN) result).getProperties().size());

            JSONTreeNode objectProperty = ((JSONObjectTN) result).getProperties().getFirst();
            Assertions.assertInstanceOf(JSONObjectTN.class, objectProperty);
            Assertions.assertEquals(JSONTreeNodeType.OBJECT, objectProperty.getType());
            Assertions.assertEquals("prop", objectProperty.getName());
            Assertions.assertTrue(((JSONObjectTN) objectProperty).getProperties().isEmpty());
        });
    }

    @Test
    public void generateTreeFromObjectWithMultipleObjects(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("{\"prop1\": {},\"prop2\": {},}");
            JSONTreeNode result = Generator.generateSchemaTree(json);
            //sprawdzenie roota
            Assertions.assertInstanceOf(JSONObjectTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.OBJECT, result.getType());
            Assertions.assertNull(result.getName());
            Assertions.assertEquals(2, ((JSONObjectTN) result).getProperties().size());
            //sprawdzenie podobiektow
            JSONTreeNode objectProperty = ((JSONObjectTN) result).getProperties().getFirst();
            Assertions.assertInstanceOf(JSONObjectTN.class, objectProperty);
            Assertions.assertEquals(JSONTreeNodeType.OBJECT, objectProperty.getType());
            Assertions.assertEquals("prop1", objectProperty.getName());
            Assertions.assertTrue(((JSONObjectTN) objectProperty).getProperties().isEmpty());

            objectProperty = ((JSONObjectTN) result).getProperties().get(1);
            Assertions.assertInstanceOf(JSONObjectTN.class, objectProperty);
            Assertions.assertEquals(JSONTreeNodeType.OBJECT, objectProperty.getType());
            Assertions.assertEquals("prop2", objectProperty.getName());
            Assertions.assertTrue(((JSONObjectTN) objectProperty).getProperties().isEmpty());
        });
    }


    @Test
    public void generateTreeFromObjectWithMultipleNotEmptyAndNestedObjects(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("{\"prop1\": {\"str\": \"Jakis  Napis123.67\",},\"prop2\": {\"obj\":{}},}");
            JSONTreeNode result = Generator.generateSchemaTree(json);
            //sprawdzenie roota
            Assertions.assertInstanceOf(JSONObjectTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.OBJECT, result.getType());
            Assertions.assertNull(result.getName());
            Assertions.assertEquals(2, ((JSONObjectTN) result).getProperties().size());
            //sprawdzenie podobiektow
            JSONTreeNode objectProperty = ((JSONObjectTN) result).getProperties().getFirst();
            Assertions.assertInstanceOf(JSONObjectTN.class, objectProperty);
            Assertions.assertEquals(JSONTreeNodeType.OBJECT, objectProperty.getType());
            Assertions.assertEquals("prop1", objectProperty.getName());
            Assertions.assertEquals(1, ((JSONObjectTN) objectProperty).getProperties().size());
            //sprawdzenie stringa w pierwszym obiekcie
            JSONTreeNode stringSubproperty = ((JSONObjectTN) objectProperty).getProperties().getFirst();
            Assertions.assertInstanceOf(JSONStringTN.class, stringSubproperty);
            Assertions.assertEquals(JSONTreeNodeType.STRING, stringSubproperty.getType());
            Assertions.assertEquals("Jakis  Napis123.67", ((JSONStringTN) stringSubproperty).getValue());
            Assertions.assertEquals("str", stringSubproperty.getName());

            objectProperty = ((JSONObjectTN) result).getProperties().get(1);
            Assertions.assertInstanceOf(JSONObjectTN.class, objectProperty);
            Assertions.assertEquals(JSONTreeNodeType.OBJECT, objectProperty.getType());
            Assertions.assertEquals("prop2", objectProperty.getName());
            Assertions.assertEquals(1, ((JSONObjectTN) objectProperty).getProperties().size());

            JSONTreeNode objectSubproperty = ((JSONObjectTN) objectProperty).getProperties().getFirst();
            Assertions.assertInstanceOf(JSONObjectTN.class, objectSubproperty);
            Assertions.assertEquals(JSONTreeNodeType.OBJECT, objectSubproperty.getType());
            Assertions.assertEquals("obj", objectSubproperty.getName());
            Assertions.assertTrue(((JSONObjectTN) objectSubproperty).getProperties().isEmpty());
        });
    }

    @Test
    public void generateTreeFromObjectWithArray(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("{\"prop1\": [\"string\"]}");
            JSONTreeNode result = Generator.generateSchemaTree(json);
            //sprawdzenie roota
            Assertions.assertInstanceOf(JSONObjectTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.OBJECT, result.getType());
            Assertions.assertNull(result.getName());
            Assertions.assertEquals(1, ((JSONObjectTN) result).getProperties().size());

            JSONTreeNode arrayProperty = ((JSONObjectTN) result).getProperties().getFirst();
            Assertions.assertInstanceOf(JSONArrayTN.class, arrayProperty);
            Assertions.assertEquals(JSONTreeNodeType.ARRAY, arrayProperty.getType());
            Assertions.assertEquals("prop1", arrayProperty.getName());
            Assertions.assertEquals(1, ((JSONArrayTN) arrayProperty).getItems().size());
        });
    }

    @Test
    public void generateTreeFromObjectWithNestedArrays(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("{\"prop1\": [[\"string\"], null]}");
            JSONTreeNode result = Generator.generateSchemaTree(json);
            //sprawdzenie roota
            Assertions.assertInstanceOf(JSONObjectTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.OBJECT, result.getType());
            Assertions.assertNull(result.getName());
            Assertions.assertEquals(1, ((JSONObjectTN) result).getProperties().size());
            //sprawdzenie zewnetrznej tablicy
            JSONTreeNode arrayProperty = ((JSONObjectTN) result).getProperties().getFirst();
            Assertions.assertInstanceOf(JSONArrayTN.class, arrayProperty);
            Assertions.assertEquals(JSONTreeNodeType.ARRAY, arrayProperty.getType());
            Assertions.assertEquals("prop1", arrayProperty.getName());
            Assertions.assertEquals(2, ((JSONArrayTN) arrayProperty).getItems().size());

                JSONTreeNode nullProperty = ((JSONArrayTN) arrayProperty).getItems().getLast();
                Assertions.assertInstanceOf(JSONNullTN.class, nullProperty);
                Assertions.assertEquals(JSONTreeNodeType.NULL, nullProperty.getType());

            //sprawdzenie wewnetrznej tablicy
            JSONTreeNode innerArray = ((JSONArrayTN) arrayProperty).getItems().getFirst();
            Assertions.assertInstanceOf(JSONArrayTN.class, innerArray);
            Assertions.assertEquals(JSONTreeNodeType.ARRAY, innerArray.getType());
            Assertions.assertEquals(1, ((JSONArrayTN) innerArray).getItems().size());

                JSONTreeNode stringProperty = ((JSONArrayTN) innerArray).getItems().getFirst();
                Assertions.assertInstanceOf(JSONStringTN.class, stringProperty);
                Assertions.assertEquals(JSONTreeNodeType.STRING, stringProperty.getType());
                Assertions.assertEquals("string", ((JSONStringTN) stringProperty).getValue());
        });
    }
}
