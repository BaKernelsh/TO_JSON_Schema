import org.example.Generator.Generator;
import org.example.JSONTN.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SchemaStringGenerationTest {

    @Test
    public void generateSchemaStringFromNull(){
        JSONNullTN nullNode = new JSONNullTN(JSONTreeNodeType.NULL);
        String schemaString = "";
        schemaString = Generator.generateSchemaString(nullNode, schemaString);
        Assertions.assertEquals("{\n  \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n  \"type\": \"null\"\n}", schemaString);
        System.out.println(schemaString);
    }

    @Test
    public void generateSchemaStringFromBoolean(){
        JSONBooleanTN boolNode = new JSONBooleanTN(JSONTreeNodeType.BOOLEAN);
        String schemaString = "";
        schemaString = Generator.generateSchemaString(boolNode, schemaString);
        Assertions.assertEquals("{\n  \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n  \"type\": \"boolean\"\n}", schemaString);
        System.out.println(schemaString);
    }

    @Test
    public void generateSchemaStringFromInteger(){
        JSONNumberTN integerNode = new JSONNumberTN(JSONTreeNodeType.NUMBER);
        integerNode.setValue("123456");
        String schemaString = "";
        schemaString = Generator.generateSchemaString(integerNode, schemaString);
        Assertions.assertEquals("{\n  \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n  \"type\": \"integer\"\n}", schemaString);
        System.out.println(schemaString);
    }

    @Test
    public void generateSchemaStringFromDouble(){
        JSONNumberTN numberNode = new JSONNumberTN(JSONTreeNodeType.NUMBER);
        numberNode.setValue("123456.7890");
        String schemaString = "";
        schemaString = Generator.generateSchemaString(numberNode, schemaString);
        Assertions.assertEquals("{\n  \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n  \"type\": \"number\"\n}", schemaString);
        System.out.println(schemaString);
    }

    @Test
    public void generateSchemaStringFromDoubleInScientific(){
        JSONNumberTN numberNode = new JSONNumberTN(JSONTreeNodeType.NUMBER);
        numberNode.setValue("12e-2");
        String schemaString = "";
        schemaString = Generator.generateSchemaString(numberNode, schemaString);
        Assertions.assertEquals("{\n  \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n  \"type\": \"number\"\n}", schemaString);
        System.out.println(schemaString);
    }

    @Test
    public void generateSchemaStringFromString(){
        JSONStringTN stringNode = new JSONStringTN(JSONTreeNodeType.STRING);
        stringNode.setValue("jakis Napis");
        String schemaString = "";
        schemaString = Generator.generateSchemaString(stringNode, schemaString);
        Assertions.assertEquals("{\n  \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n  \"type\": \"string\"\n}", schemaString);
        System.out.println(schemaString);
    }

    @Test
    public void generateSchemaStringFromEmptyObject(){
        JSONObjectTN objectNode = new JSONObjectTN(JSONTreeNodeType.OBJECT);
        String schemaString = "";
        schemaString = Generator.generateSchemaString(objectNode, schemaString);
        System.out.println(schemaString);
        Assertions.assertEquals("{\n\"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n\"type\": \"object\",\n\"properties\": {\n},\n\"required\":[]\n}", schemaString);
    }
}
