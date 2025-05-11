import org.example.Generator.AssertionConfiguration;
import org.example.Generator.Generator;
import org.example.JSONString;
import org.example.JSONTN.JSONNumberTN;
import org.example.JSONTN.JSONTreeNode;
import org.example.JSONTN.JSONTreeNodeType;
import org.example.Validator.JSONValidator;
import org.example.Validator.OnUnknownKeyword;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ValidationTest {

    static Generator generator;

    @BeforeAll
    public static void setGeneratorInstance(){
        AssertionConfiguration assertionConfiguration = new AssertionConfiguration();
        //assertionConfiguration.setAllUnused();

        generator = new Generator(assertionConfiguration);
    }

    @Test
    public void IntegerMinimumValidationShouldSucceedTest(){
        Assertions.assertDoesNotThrow(() -> {
            JSONNumberTN nmbNode = new JSONNumberTN(JSONTreeNodeType.NUMBER);
            nmbNode.setName("nmb");
            nmbNode.setValue("5");

            JSONValidator validator = new JSONValidator();
            boolean result = validator.validateAgainstSchema(nmbNode, "vb");
            Assertions.assertTrue(result);
        });
    }

    @Test
    public void IntegerMinimumValidationShouldThrowTest(){
        Assertions.assertThrows(Exception.class, () -> {
            JSONNumberTN nmbNode = new JSONNumberTN(JSONTreeNodeType.NUMBER);
            nmbNode.setName("nmb");
            nmbNode.setValue("4");

            JSONValidator validator = new JSONValidator();
            boolean result = validator.validateAgainstSchema(nmbNode, "vb");
            Assertions.assertFalse(result);
        });
    }

    @Test
    public void IntegerMinimumValidationShouldThrowUnknownValidationKeywordTest(){
        var e = Assertions.assertThrows(Exception.class, () -> {
            JSONString json = new JSONString("1234.56");
            JSONTreeNode root = generator.generateSchemaTree(json);
            String schema = generator.generateSchemaString(root, "", 0);
            System.out.println(schema);

            JSONValidator validator = new JSONValidator();
            validator.validateAgainstSchema(root, schema);
        });
        //System.out.println(e.getMessage());
        Assertions.assertEquals("Unknown validation keyword: maximum", e.getMessage());
    }

    @Test
    public void IntegerMinimumValidationWithUnknownValidationKeywordShouldReturnFalseTest(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("1234.56");
            JSONTreeNode root = generator.generateSchemaTree(json);
            String schema = generator.generateSchemaString(root, "", 0);
            System.out.println(schema);

            JSONValidator validator = new JSONValidator();
            validator.setUnknownKeywordBehavior(OnUnknownKeyword.KEYWORD_VALIDATION_UNSUCCESFUL_CONTINUE_VALIDATION);
            boolean result = validator.validateAgainstSchema(root, schema);
            Assertions.assertFalse(result);
        });
    }

}
