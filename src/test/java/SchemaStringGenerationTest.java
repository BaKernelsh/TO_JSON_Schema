import org.example.Generator.AssertionConfiguration;
import org.example.Generator.Generator;

import org.example.JSONString;
import org.example.JSONTN.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SchemaStringGenerationTest {

    static Generator generator;

    @BeforeAll
    public static void setGeneratorInstance(){
        AssertionConfiguration assertionConfiguration = new AssertionConfiguration();
        assertionConfiguration.setAllUnused();

        generator = new Generator(assertionConfiguration);
    }

    @Test
    public void generateSchemaStringFromNull(){
        Assertions.assertDoesNotThrow(() -> {
            JSONNullTN nullNode = new JSONNullTN(JSONTreeNodeType.NULL);
            String schemaString = "";
            schemaString = generator.generateSchemaString(nullNode, schemaString, 0);
            Assertions.assertEquals("{\n  \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n  \"type\": \"null\"\n}", schemaString);
            System.out.println(schemaString);
        });

    }

    @Test
    public void generateSchemaStringFromBoolean(){
        Assertions.assertDoesNotThrow(() -> {
            JSONBooleanTN boolNode = new JSONBooleanTN(JSONTreeNodeType.BOOLEAN);
            String schemaString = "";
            schemaString = generator.generateSchemaString(boolNode, schemaString, 0);
            Assertions.assertEquals("{\n  \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n  \"type\": \"boolean\"\n}", schemaString);
            //System.out.println(schemaString);
        });
    }

    @Test
    public void generateSchemaStringFromInteger(){
        Assertions.assertDoesNotThrow(() -> {
            JSONNumberTN integerNode = new JSONNumberTN(JSONTreeNodeType.NUMBER);
            integerNode.setValue("123456");
            String schemaString = "";
            schemaString = generator.generateSchemaString(integerNode, schemaString, 0);
            Assertions.assertEquals("{\n  \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n  \"type\": \"integer\"\n}", schemaString);
            //System.out.println(schemaString);
        });
    }

    @Test
    public void generateSchemaStringFromDouble(){
        Assertions.assertDoesNotThrow(() -> {
            JSONNumberTN numberNode = new JSONNumberTN(JSONTreeNodeType.NUMBER);
            numberNode.setValue("123456.7890");
            String schemaString = "";
            schemaString = generator.generateSchemaString(numberNode, schemaString, 0);
            Assertions.assertEquals("{\n  \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n  \"type\": \"number\"\n}", schemaString);
            System.out.println(schemaString);
        });
    }

    @Test
    public void generateSchemaStringFromDoubleInScientific(){
        Assertions.assertDoesNotThrow(() -> {
            JSONNumberTN numberNode = new JSONNumberTN(JSONTreeNodeType.NUMBER);
            numberNode.setValue("12e-2");
            String schemaString = "";
            schemaString = generator.generateSchemaString(numberNode, schemaString, 0);
            Assertions.assertEquals("{\n  \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n  \"type\": \"number\"\n}", schemaString);
            //System.out.println(schemaString);
        });
    }

    @Test
    public void generateSchemaStringFromString(){
        Assertions.assertDoesNotThrow(() -> {
            JSONStringTN stringNode = new JSONStringTN(JSONTreeNodeType.STRING);
            stringNode.setValue("jakis Napis");
            String schemaString = "";
            schemaString = generator.generateSchemaString(stringNode, schemaString, 0);
            Assertions.assertEquals("{\n  \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n  \"type\": \"string\"\n}", schemaString);
            //System.out.println(schemaString);
        });
    }

    @Test
    public void generateSchemaStringFromEmptyObject(){
        Assertions.assertDoesNotThrow(() -> {
            JSONObjectTN objectNode = new JSONObjectTN(JSONTreeNodeType.OBJECT);
            String schemaString = "";
            schemaString = generator.generateSchemaString(objectNode, schemaString, 0);
            System.out.println(schemaString);
            Assertions.assertEquals("{\n\"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n\"type\": \"object\",\n\"properties\": {\n},\n\"required\":[]\n}", schemaString);
        });
    }

    @Test
    public void generateSchemaStringFromObjectWithString(){
        Assertions.assertDoesNotThrow(() -> {
            JSONObjectTN objectNode = new JSONObjectTN(JSONTreeNodeType.OBJECT);

            JSONStringTN stringNode = new JSONStringTN(JSONTreeNodeType.STRING);
            stringNode.setName("prop1");
            stringNode.setValue("jakis Napis");
            objectNode.addProperty(stringNode);

            String schemaString = "";
            schemaString = generator.generateSchemaString(objectNode, schemaString, 0);
            System.out.println(schemaString);
            Assertions.assertEquals("{\n\"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n\"type\": \"object\",\n\"properties\": {\n\"prop1\": {\n\"type\": \"string\"\n}\n},\n\"required\":[\"prop1\"]\n}", schemaString);
        });
    }

    @Test
    public void generateSchemaStringFromObjectWithMultipleStrings(){
        Assertions.assertDoesNotThrow(() -> {
            JSONObjectTN objectNode = new JSONObjectTN(JSONTreeNodeType.OBJECT);

            JSONStringTN stringNode = new JSONStringTN(JSONTreeNodeType.STRING);
            stringNode.setName("prop1");
            stringNode.setValue("jakis Napis");
            objectNode.addProperty(stringNode);

            JSONStringTN stringNode2 = new JSONStringTN(JSONTreeNodeType.STRING);
            stringNode2.setName("prop2");
            stringNode2.setValue("str2");
            objectNode.addProperty(stringNode2);

            String schemaString = "";
            schemaString = generator.generateSchemaString(objectNode, schemaString, 0);
            System.out.println(schemaString);
            Assertions.assertEquals("{\n\"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n\"type\": \"object\",\n\"properties\": {\n\"prop1\": {\n\"type\": \"string\"\n},\n\"prop2\": {\n" +
                    "\"type\": \"string\"\n" +
                    "}\n},\n\"required\":[\"prop1\", \"prop2\"]\n}", schemaString);
        });
    }

    @Test
    public void generateSchemaStringFromObjectWithObject(){
        Assertions.assertDoesNotThrow(() -> {
            JSONObjectTN objectNode = new JSONObjectTN(JSONTreeNodeType.OBJECT);

            JSONObjectTN subObject = new JSONObjectTN(JSONTreeNodeType.OBJECT);
            subObject.setName("prop1");
            objectNode.addProperty(subObject);

            JSONStringTN stringNode = new JSONStringTN(JSONTreeNodeType.STRING);
            stringNode.setName("prop1");
            stringNode.setValue("jakis Napis");
            subObject.addProperty(stringNode);

            String schemaString = "";
            schemaString = generator.generateSchemaString(objectNode, schemaString, 0);
            System.out.println(schemaString);
            Assertions.assertEquals("{\n\"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n\"type\": \"object\",\n\"properties\": {\n\"prop1\": {\n\"type\": \"object\",\n\"properties\": {\n\"prop1\": {\n\"type\": \"string\"\n}\n},\n\"required\":[\"prop1\"]\n}\n},\n\"required\":[\"prop1\"]\n}", schemaString);
        });
    }


    @Test
    public void generateSchemaStringFromObjectWithEmptyArray(){
        Assertions.assertDoesNotThrow(() -> {
            JSONObjectTN objectNode = new JSONObjectTN(JSONTreeNodeType.OBJECT);

            JSONArrayTN arrayProperty = new JSONArrayTN(JSONTreeNodeType.ARRAY);
            arrayProperty.setName("prop1");
            objectNode.addProperty(arrayProperty);

            String schemaString = "";
            schemaString = generator.generateSchemaString(objectNode, schemaString, 0);
            System.out.println(schemaString);
            Assertions.assertEquals("{\n\"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n\"type\": \"object\",\n\"properties\": {\n\"prop1\": {\n\"type\": \"array\",\n\"items\": {}\n}\n},\n\"required\":[\"prop1\"]\n}", schemaString);
        });
    }

    @Test
    public void generateSchemaStringFromObjectWithMultipleArrays(){
        Assertions.assertDoesNotThrow(() -> {
            JSONObjectTN objectNode = new JSONObjectTN(JSONTreeNodeType.OBJECT);

            JSONArrayTN arrayProperty = new JSONArrayTN(JSONTreeNodeType.ARRAY);
            arrayProperty.setName("prop1");
            objectNode.addProperty(arrayProperty);

            JSONNumberTN numberItem = new JSONNumberTN(JSONTreeNodeType.NUMBER);
            numberItem.setValue("123");
            arrayProperty.addItem(numberItem);

            JSONBooleanTN trueItem = new JSONBooleanTN(JSONTreeNodeType.BOOLEAN);
            trueItem.setValue(true);
            arrayProperty.addItem(trueItem);

            JSONArrayTN arrayProperty2 = new JSONArrayTN(JSONTreeNodeType.ARRAY);
            arrayProperty2.setName("prop2");
            objectNode.addProperty(arrayProperty2);

            String schemaString = "";
            schemaString = generator.generateSchemaString(objectNode, schemaString, 0);
            //System.out.println(schemaString);
            Assertions.assertEquals("{\n\"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n\"type\": \"object\",\n\"properties\": {\n\"prop1\": {\n\"type\": \"array\",\n\"items\": [\n{\n\"type\": \"integer\"\n},\n{\n\"type\": \"boolean\"\n}\n]\n},\n\"prop2\": {\n\"type\": \"array\",\n\"items\": {}\n}\n},\n\"required\":[\"prop1\", \"prop2\"]\n}", schemaString);
        });
    }


    @Test
    public void generateSchemaStringFromEmptyArray(){
        Assertions.assertDoesNotThrow(() -> {
            JSONArrayTN arrayNode = new JSONArrayTN(JSONTreeNodeType.ARRAY);

            String schemaString = "";
            schemaString = generator.generateSchemaString(arrayNode, schemaString, 0);
            System.out.println(schemaString);
            Assertions.assertEquals("{\n\"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n\"type\": \"array\",\n\"items\": {}\n}", schemaString);
        });
    }

    @Test
    public void generateSchemaStringFromArrayWithString(){
        Assertions.assertDoesNotThrow(() -> {
            JSONArrayTN arrayNode = new JSONArrayTN(JSONTreeNodeType.ARRAY);
            JSONStringTN stringItem = new JSONStringTN(JSONTreeNodeType.STRING);
            stringItem.setValue("blabla");
            arrayNode.addItem(stringItem);

            String schemaString = "";
            schemaString = generator.generateSchemaString(arrayNode, schemaString, 0);
            System.out.println(schemaString);
            Assertions.assertEquals("{\n\"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n\"type\": \"array\",\n\"items\": [\n{\n\"type\": \"string\"\n}\n]\n}", schemaString);
        });
    }

    @Test
    public void generateSchemaStringFromArrayWithMultipleStrings(){
        Assertions.assertDoesNotThrow(() -> {
            JSONArrayTN arrayNode = new JSONArrayTN(JSONTreeNodeType.ARRAY);
            JSONStringTN stringItem = new JSONStringTN(JSONTreeNodeType.STRING);
            stringItem.setValue("blabla");
            arrayNode.addItem(stringItem);

            JSONStringTN stringItem2 = new JSONStringTN(JSONTreeNodeType.STRING);
            stringItem2.setValue("blabla");
            arrayNode.addItem(stringItem2);

            String schemaString = "";
            schemaString = generator.generateSchemaString(arrayNode, schemaString, 0);
            System.out.println(schemaString);
            Assertions.assertEquals("{\n\"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n\"type\": \"array\",\n\"items\": [\n{\n\"type\": \"string\"\n},\n{\n\"type\": \"string\"\n}\n]\n}", schemaString);
        });
    }

    @Test
    public void generateSchemaStringFromArrayWithObject(){
        Assertions.assertDoesNotThrow(() -> {
            JSONArrayTN arrayNode = new JSONArrayTN(JSONTreeNodeType.ARRAY);
            JSONObjectTN objectItem = new JSONObjectTN(JSONTreeNodeType.OBJECT);
            objectItem.setName("obiekt");
            arrayNode.addItem(objectItem);

            JSONStringTN stringItem = new JSONStringTN(JSONTreeNodeType.STRING);
            stringItem.setValue("blabla");
            stringItem.setName("str");
            //arrayNode.addItem(stringItem);

            String schemaString = "";
            schemaString = generator.generateSchemaString(arrayNode, schemaString, 0);
            System.out.println(schemaString);
            Assertions.assertEquals("{\n\"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n\"type\": \"array\",\n\"items\": [\n{\n\"type\": \"object\",\n\"properties\": {\n},\n\"required\":[]\n}\n]\n}", schemaString);
        });
    }

    @Test
    public void generateSchemaStringFromArrayWithArray(){
        Assertions.assertDoesNotThrow(() -> {
            JSONArrayTN arrayNode = new JSONArrayTN(JSONTreeNodeType.ARRAY);
            JSONArrayTN arrayItem = new JSONArrayTN(JSONTreeNodeType.ARRAY);
            arrayItem.setName("arr");
            JSONArrayTN arrayItem2 = new JSONArrayTN(JSONTreeNodeType.ARRAY);
            arrayItem2.setName("arr2");
            arrayNode.addItem(arrayItem);
            arrayItem.addItem(arrayItem2);

            String schemaString = "";
            schemaString = generator.generateSchemaString(arrayNode, schemaString, 0);
            System.out.println(schemaString);
            Assertions.assertEquals("{\n\"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n\"type\": \"array\",\n\"items\": [\n{\n\"type\": \"array\",\n\"items\": {}\n}\n]\n}", schemaString);
        });
    }

    @Test
    public void generateArraySchemaFromString(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("[{\"k\":23},{\"k\":[]}]");
            JSONTreeNode tree = generator.generateSchemaTree(json);

            String schemaString = "";
            schemaString = generator.generateSchemaString(tree, schemaString, 0);
            System.out.println(schemaString);


        });
    }



    @Test
    public void generateSchemaStringWithStringAssertions(){
        Assertions.assertDoesNotThrow(() -> {
            JSONObjectTN objNode = new JSONObjectTN(JSONTreeNodeType.OBJECT);
            JSONStringTN strNode = new JSONStringTN(JSONTreeNodeType.STRING);
            strNode.setValue("blabla");
            strNode.setName("string");
            objNode.addProperty(strNode);

            String schemaString = "";
            schemaString = generator.generateSchemaString(objNode, schemaString, 0);
            System.out.println(schemaString);
        });

    }

    @Test
    public void generateSchemaTest(){
        Assertions.assertDoesNotThrow(() -> {

            String json = "{\"prop1\": {\"str\": \"Jakis  Napis123.67\",},\"prop2\": {\"obj\":{\"o\": {\"zx\": {}, \"n\":5}}},}";
            Generator defaultGenerator = new Generator();
            long st = 0;
            long et = 0;
            st= System.currentTimeMillis();
            String schemaString = defaultGenerator.generateSchema(json);
            et = System.currentTimeMillis();
            long t = et-st;
            System.out.println(t);
            System.out.println(schemaString);
        });

    }

    @Test
    public void generateSchemaWithArraysTest(){
        Assertions.assertDoesNotThrow(() -> {

            String json = "{\"prop1\": {\"str\": \"Jakis  Napis123.67\",},\"prop2\": {\"obj\": [{\"o\": {}}, \"qwe\", [5]]},}";
            Generator defaultGenerator = new Generator();
            long st = 0;
            long et = 0;
            st= System.currentTimeMillis();
            String schemaString = defaultGenerator.generateSchema(json);
            et = System.currentTimeMillis();
            long t = et-st;
            //System.out.println(t);
            System.out.println(schemaString);
        });

    }

    @Test
    public void generateSchemaWithNestedArraysTest(){
        Assertions.assertDoesNotThrow(() -> {

            String json = "[{\"arr\":[[\"srt\", {\"p\": 5}, []]]}]";
            Generator defaultGenerator = new Generator();
            long st = 0;
            long et = 0;
            st= System.currentTimeMillis();
            String schemaString = defaultGenerator.generateSchema(json);
            et = System.currentTimeMillis();
            long t = et-st;
            //System.out.println(t);
            System.out.println(schemaString);
        });

    }


}
