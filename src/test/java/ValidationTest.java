import org.example.Generator.AssertionConfiguration;
import org.example.Generator.Generator;
import org.example.JSONString;
import org.example.JSONTN.JSONNumberTN;
import org.example.JSONTN.JSONTreeNode;
import org.example.JSONTN.JSONTreeNodeType;
import org.example.Validator.JSONValidator;
import org.example.Validator.OnUnknownKeyword;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ValidationTest {

     Generator generator;

    @BeforeEach
    public void setGeneratorInstance(){
        AssertionConfiguration assertionConfiguration = new AssertionConfiguration();
        //assertionConfiguration.setAllUnused();

        generator = new Generator(assertionConfiguration);
    }

    @Test
    public void IntegerMinimumValidationShouldSucceedTest(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("1234.56");
            AssertionConfiguration assertionConfiguration = new AssertionConfiguration();
            assertionConfiguration.setAllUnused();
            generator.setAssertionConfiguration(assertionConfiguration);
            JSONTreeNode root = generator.generateJsonTree(json);
            String schema = generator.generateSchemaString(root, "", 0);
            System.out.println(schema);

            JSONValidator validator = new JSONValidator();
            validator.validateAgainstSchema(root, schema);
            boolean result = validator.validateAgainstSchema(root,
                    "{\n" +
                    "  \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n" +
                    "  \"type\": \"number\",\n" +
                    "}");
            Assertions.assertTrue(result);
        });
    }

    @Test
    public void IntegerMinimumValidationShouldThrowTest(){ //SPRAWDZA TYPE, nie mininmum
        var e = Assertions.assertThrows(Exception.class, () -> {
            JSONNumberTN nmbNode = new JSONNumberTN(JSONTreeNodeType.NUMBER);
            nmbNode.setName("nmb");
            nmbNode.setValue("4");

            JSONValidator validator = new JSONValidator();
            boolean result = validator.validateAgainstSchema(nmbNode, "{\n" +
                    "  \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n" +
                    "  \"type\": \"number\",\n" +
                    "  \"minimum\": 1234.56,\n" +
                    "  \"maximum\": 1234.56\n" +
                    "}");
            Assertions.assertFalse(result);
        });
        System.out.println(e.getMessage());
        Assertions.assertEquals("Validation failed for keyword: type Invalid type of nmb property. Should be: number", e.getMessage());
    }

    @Test
    public void IntegerMinimumValidationShouldThrowUnknownValidationKeywordTest(){
        var e = Assertions.assertThrows(Exception.class, () -> {
            JSONString json = new JSONString("1234.56");
            JSONTreeNode root = generator.generateJsonTree(json);
            String schema = generator.generateSchemaString(root, "", 0);
            System.out.println(schema);

            JSONValidator validator = new JSONValidator();
            validator.validateAgainstSchema(root, schema);
        });
        //System.out.println(e.getMessage());
        Assertions.assertEquals("Unknown validation keyword: multipleOf", e.getMessage());
    }

    @Test
    public void IntegerMinimumValidationWithUnknownValidationKeywordShouldReturnFalseTest(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("1234.56");
            JSONTreeNode root = generator.generateJsonTree(json);
            String schema = generator.generateSchemaString(root, "", 0);
            System.out.println(schema);

            JSONValidator validator = new JSONValidator();
            validator.setUnknownKeywordBehavior(OnUnknownKeyword.KEYWORD_VALIDATION_UNSUCCESFUL_CONTINUE_VALIDATION);
            boolean result = validator.validateAgainstSchema(root, schema);
            Assertions.assertFalse(result);
        });
    }


    @Test
    public void ObjectRequiredValidationTest(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("{\"k\":1234.56,\"b\":3}");
            JSONTreeNode root = generator.generateJsonTree(json);

            String schema = "{\n" +
                    "  \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n" +
                    "  \"type\": \"object\",\n" +
                    "  \"required\": [\"k\", \"b\"],\n" +
                    "  \"maxProperties\": 2,\n" +
                    "  \"minProperties\": 2\n" +
                    "}";
            System.out.println(schema);

            JSONValidator validator = new JSONValidator();
            validator.setUnknownKeywordBehavior(OnUnknownKeyword.KEYWORD_VALIDATION_UNSUCCESFUL_CONTINUE_VALIDATION);
            boolean result = validator.validateAgainstSchema(root, schema);
            Assertions.assertTrue(result);
        });
    }

    @Test
    public void ObjectPropertiesValidationTest(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("{\"k\":1234.56,\"b\":3}");
            JSONTreeNode root = generator.generateJsonTree(json);

            String schema = "{\n" +
                    "  \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n" +
                    "  \"type\": \"object\",\n" +
                    "  \"properties\": {\n" +
                    "    \"k\": {\n" +
                    "      \"type\": \"number\",\n" +
                    "      \"minimum\": 1234.56,\n" +
                    "      \"maximum\": 1234.56,\n" +
                    "    },\n" +
                    "    \"b\": {\n" +
                    "      \"type\": \"integer\",\n" +
                    "      \"maximum\": 3,\n" +
                    "      \"minimum\": 3\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"required\": [\"k\", \"b\"],\n" +
                    "  \"maxProperties\": 2,\n" +
                    "  \"minProperties\": 2\n" +
                    "}";
            System.out.println(schema);

            JSONValidator validator = new JSONValidator();
            boolean result = validator.validateAgainstSchema(root, schema);
            Assertions.assertTrue(result);
        });
    }

    @Test
    public void ObjectPropertiesValidationShouldThrowUnknownValidationKeywordTest(){
        var e = Assertions.assertThrows(Exception.class, () -> {
            JSONString json = new JSONString("{\"k\":1234.56,\"b\":3}");
            JSONTreeNode root = generator.generateJsonTree(json);

            String schema = "{\n" +
                    "  \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n" +
                    "  \"type\": \"object\",\n" +
                    "  \"properties\": {\n" +
                    "    \"k\": {\n" +
                    "      \"type\": \"number\",\n" +
                    "      \"minimum\": 1234.56,\n" +
                    "      \"maximum\": 1234.56,\n" +
                    "      \"multipleOf\": 1234.56\n" +
                    "    },\n" +
                    "    \"b\": {\n" +
                    "      \"type\": \"integer\",\n" +
                    "      \"multipleOf\": 3,\n" +
                    "      \"maximum\": 3,\n" +
                    "      \"exclusiveMaximum\": 4,\n" +
                    "      \"minimum\": 3,\n" +
                    "      \"exclusiveMinimum\": 2\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"required\": [\"k\", \"b\"],\n" +
                    "  \"maxProperties\": 2,\n" +
                    "  \"minProperties\": 2\n" +
                    "}";
            System.out.println(schema);

            JSONValidator validator = new JSONValidator();
            validator.validateAgainstSchema(root, schema);

        });

        Assertions.assertEquals("Validation failed for keyword: properties Unknown validation keyword: multipleOf", e.getMessage());
        System.out.println(e.getMessage());
    }

    @Test
    public void ObjectPropertiesValidationShouldContinueAndReturnFalseTest(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("{\"k\":1234.56,\"b\":3}");
            JSONTreeNode root = generator.generateJsonTree(json);

            String schema = "{\n" +
                    "  \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n" +
                    "  \"type\": \"object\",\n" +
                    "  \"properties\": {\n" +
                    "    \"k\": {\n" +
                    "      \"type\": \"number\",\n" +
                    "      \"minimum\": 1234.56,\n" +
                    "      \"maximum\": 1234.56,\n" +
                    "      \"multipleOf\": 1234.56\n" +
                    "    },\n" +
                    "    \"b\": {\n" +
                    "      \"type\": \"integer\",\n" +
                    "      \"multipleOf\": 3,\n" +
                    "      \"maximum\": 3,\n" +
                    "      \"exclusiveMaximum\": 4,\n" +
                    "      \"minimum\": 3,\n" +
                    "      \"exclusiveMinimum\": 2\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"required\": [\"k\", \"b\"],\n" +
                    "  \"maxProperties\": 2,\n" +
                    "  \"minProperties\": 2\n" +
                    "}";
            System.out.println(schema);

            JSONValidator validator = new JSONValidator();
            validator.setUnknownKeywordBehavior(OnUnknownKeyword.KEYWORD_VALIDATION_UNSUCCESFUL_CONTINUE_VALIDATION);
            boolean result = validator.validateAgainstSchema(root, schema);
            Assertions.assertFalse(result);
        });
    }

}
