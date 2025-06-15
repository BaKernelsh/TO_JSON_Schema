import org.example.Exception.JSONSchemaGeneratorException;
import org.example.Generator.AssertionConfiguration;
import org.example.Generator.Generator;
import org.example.JSONString;
import org.example.JSONTN.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class GeneratorTest {

    static Generator generator;

    @BeforeAll
    public static void setGeneratorInstance(){
        AssertionConfiguration assertionConfiguration = new AssertionConfiguration();
        assertionConfiguration.setAllUnused();

        generator = new Generator(assertionConfiguration);
    }

    @Test
    public void generateTreeFromSingleNumber(){

        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("456");
            JSONTreeNode result = generator.generateJsonTree(json);
            Assertions.assertInstanceOf(JSONNumberTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.NUMBER, result.getType());
            Assertions.assertEquals(Double.valueOf("456"), ((JSONNumberTN) result).getValue());
        });

    }

    @Test
    public void generateTreeFromSingleNull(){

        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("null");
            JSONTreeNode result = generator.generateJsonTree(json);
            Assertions.assertInstanceOf(JSONNullTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.NULL, result.getType());
        });
    }

    @Test
    public void generateTreeFromSingleTrue(){

        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("true");
            JSONTreeNode result = generator.generateJsonTree(json);
            Assertions.assertInstanceOf(JSONBooleanTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.BOOLEAN, result.getType());
            Assertions.assertTrue(((JSONBooleanTN) result).getValue());
        });
    }

    @Test
    public void generateTreeFromSingleFalse(){

        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("false");
            JSONTreeNode result = generator.generateJsonTree(json);
            Assertions.assertInstanceOf(JSONBooleanTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.BOOLEAN, result.getType());
            Assertions.assertFalse(((JSONBooleanTN) result).getValue());
        });
    }

    @Test
    public void generateTreeFromSingleString(){

        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("\"some5tring', {}[]:\"");
            JSONTreeNode result = generator.generateJsonTree(json);
            Assertions.assertInstanceOf(JSONStringTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.STRING, result.getType());
            Assertions.assertEquals("some5tring', {}[]:",((JSONStringTN) result).getValue());
        });
    }

    @Test
    public void generateTreeFromEmptyString(){

        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("\"\"");
            JSONTreeNode result = generator.generateJsonTree(json);
            Assertions.assertInstanceOf(JSONStringTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.STRING, result.getType());
            Assertions.assertEquals("",((JSONStringTN) result).getValue());
        });
    }

    @Test
    public void generateTreeFromSingleStringShouldThrowUnterminatedString(){

        var e = Assertions.assertThrows(JSONSchemaGeneratorException.class, () -> {
            JSONString json = new JSONString("\"some5tring', {}[]:");
            generator.generateJsonTree(json);
        });
        Assertions.assertEquals("Unterminated string", e.getMessage());
    }




    @Test
    public void generateTreeFromStringWithEscapedQuotationsInsideTest(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("\"The value of \\\"additionalProperties\\\" MUST be a valid JSON Schema. Boolean \\\"false\\\" forbids everything.\"");
            JSONTreeNode result = generator.generateJsonTree(json);
            System.out.println(((JSONStringTN) result).getValue());

            Assertions.assertInstanceOf(JSONStringTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.STRING, result.getType());
            Assertions.assertEquals("The value of \\\"additionalProperties\\\" MUST be a valid JSON Schema. Boolean \\\"false\\\" forbids everything.",
                    ((JSONStringTN) result).getValue());
        });
    }

    @Test
    public void generateTreeFromStringWithUnescapedQuotationsInsideTest(){
        var e =Assertions.assertThrows(JSONSchemaGeneratorException.class, () -> {
            JSONString json = new JSONString("{\"p\":\"The value of \"additionalProperties\\\" MUST be a valid JSON Schema. Boolean \\\"false\\\" forbids everything.\"}");
            generator.generateJsonTree(json);
        });
        System.out.println(e.getMessage());
    }

}
