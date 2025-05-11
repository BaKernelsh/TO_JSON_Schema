import org.example.Generator.AssertionConfiguration;
import org.example.Generator.Generator;
import org.example.JSONString;
import org.example.JSONTN.JSONNumberTN;
import org.example.JSONTN.JSONObjectTN;
import org.example.JSONTN.JSONStringTN;
import org.example.JSONTN.JSONTreeNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SchemaTreeGenerationTest {

    static Generator generator;

    @BeforeAll
    public static void setGeneratorInstance(){
        AssertionConfiguration assertionConfiguration = new AssertionConfiguration();
        //assertionConfiguration.setAllUnused();

        generator = new Generator(assertionConfiguration);
    }


    @Test
    public void generateSchemaTree(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("1234.56");
            JSONTreeNode root = generator.generateSchemaTree(json);
            String schema = generator.generateSchemaString(root, "", 0);
            System.out.println(schema);
            JSONString schemaString = new JSONString(schema);

            JSONTreeNode schemaRoot = generator.generateSchemaTree(schemaString);
            Assertions.assertEquals("$schema", ((JSONObjectTN) schemaRoot).getProperties().getFirst().getName() );

            Assertions.assertInstanceOf(JSONStringTN.class, ((JSONObjectTN) schemaRoot).getProperties().get(1));
            Assertions.assertEquals("number", ((JSONStringTN) ((JSONObjectTN) schemaRoot).getProperties().get(1)).getValue() );
            
            Assertions.assertEquals(1234.56, ((JSONNumberTN) ((JSONObjectTN) schemaRoot).getProperties().get(2)).getValue() );

        });
    }
}
