import org.example.Generator.AssertionBoolAndAssertionStringGenerator;
import org.example.Generator.AssertionConfiguration;
import org.example.Generator.Generator;
import org.example.JSONTN.JSONNumberTN;
import org.example.JSONTN.JSONObjectTN;
import org.example.JSONTN.JSONStringTN;
import org.example.JSONTN.JSONTreeNodeType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AssertionGenerationTest {

    @Test
    public void lambdaTest(){
        JSONStringTN strNode = new JSONStringTN(JSONTreeNodeType.STRING);
        strNode.setValue("qwerty");

        AssertionBoolAndAssertionStringGenerator a = new AssertionBoolAndAssertionStringGenerator();
        a.setAssertionValueStringGenerationMethod((node) -> {
            return ((JSONStringTN) node).getValue();
        });

        Assertions.assertEquals("qwerty", a.generateAssertionValue(strNode));
    }

    @Test
    public void setCustomAssertionAndGenerateSchemaTest(){

        Assertions.assertDoesNotThrow(() -> {
            JSONObjectTN objNode = new JSONObjectTN(JSONTreeNodeType.OBJECT);
            JSONNumberTN nmbNode = new JSONNumberTN(JSONTreeNodeType.NUMBER);
            nmbNode.setName("numberProp");
            nmbNode.setValue("123e-3");
            objNode.addProperty(nmbNode);
            JSONStringTN strNode = new JSONStringTN(JSONTreeNodeType.STRING);
            strNode.setName("str");
            strNode.setValue("qwerty");
            objNode.addProperty(strNode);

            AssertionConfiguration config = new AssertionConfiguration();
            config.setAssertion("number", "minimum", node -> {
                if(node.getTypeAsString().equals("number"))
                    return "Double.toString(((JSONNumberTN) node).getValue()";
                else
                    return "Double.toString(((JSONNumberTN) node).getValue()).split(\"\\.\")[0]";
            });

            config.setAllUnused();
            config.setAllUsed();
            Generator generator = new Generator();
            generator.assertionConfig = config;

            String schemaString = "";
            schemaString = generator.generateSchemaString(objNode, schemaString);
            System.out.println(schemaString);
        });

    }
}
