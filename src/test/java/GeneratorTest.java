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
            JSONTreeNode result = generator.generateSchemaTree(json);
            Assertions.assertInstanceOf(JSONNumberTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.NUMBER, result.getType());
            Assertions.assertEquals(Double.valueOf("456"), ((JSONNumberTN) result).getValue());
        });

    }

    @Test
    public void generateTreeFromSingleNull(){

        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("null");
            JSONTreeNode result = generator.generateSchemaTree(json);
            Assertions.assertInstanceOf(JSONNullTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.NULL, result.getType());
        });
    }

    @Test
    public void generateTreeFromSingleTrue(){

        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("true");
            JSONTreeNode result = generator.generateSchemaTree(json);
            Assertions.assertInstanceOf(JSONBooleanTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.BOOLEAN, result.getType());
            Assertions.assertTrue(((JSONBooleanTN) result).getValue());
        });
    }

    @Test
    public void generateTreeFromSingleFalse(){

        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("false");
            JSONTreeNode result = generator.generateSchemaTree(json);
            Assertions.assertInstanceOf(JSONBooleanTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.BOOLEAN, result.getType());
            Assertions.assertFalse(((JSONBooleanTN) result).getValue());
        });
    }

    @Test
    public void generateTreeFromSingleString(){

        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("\"some5tring', {}[]:\"");
            JSONTreeNode result = generator.generateSchemaTree(json);
            Assertions.assertInstanceOf(JSONStringTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.STRING, result.getType());
            Assertions.assertEquals("some5tring', {}[]:",((JSONStringTN) result).getValue());
        });
    }

    @Test
    public void generateTreeFromSingleStringShouldThrowUnterminatedString(){

        var e = Assertions.assertThrows(JSONSchemaGeneratorException.class, () -> {
            JSONString json = new JSONString("\"some5tring', {}[]:");
            generator.generateSchemaTree(json);
        });
        Assertions.assertEquals("Unterminated string", e.getMessage());
    }



}
