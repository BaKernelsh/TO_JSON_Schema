import org.example.Generator.AssertionBoolAndAssertionStringGenerator;
import org.example.Generator.AssertionConfiguration;
import org.example.Generator.Generator;
import org.example.JSONTN.*;
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
            JSONArrayTN arrNode = new JSONArrayTN(JSONTreeNodeType.ARRAY);
            nmbNode.setName("numberProp");
            nmbNode.setValue("123");
            objNode.addProperty(nmbNode);
            JSONStringTN strNode = new JSONStringTN(JSONTreeNodeType.STRING);
            strNode.setName("str");
            strNode.setValue("qwerty");
            objNode.addProperty(strNode);
            JSONNumberTN nmbNode2 = new JSONNumberTN(JSONTreeNodeType.NUMBER);
            nmbNode2.setName("numberProp");
            nmbNode2.setValue("123456");
            arrNode.setName("arrProp");
            arrNode.addItem(nmbNode2);
            objNode.addProperty(arrNode);

            AssertionConfiguration config = new AssertionConfiguration();
            config.setAssertion("number", "minimum", node -> {
                if(node.getTypeAsString().equals("number"))
                    return "Double.toString(((JSONNumberTN) node).getValue()";
                else
                    return "Double.toString(((JSONNumberTN) node).getValue()).split(\"\\.\")[0]";
            });

            config.setAllUnused();
            config.setAllUsed();
            Generator generator = new Generator(config);
            //generator.assertionConfig = config;

            String schemaString = "";
            schemaString = generator.generateSchemaString(objNode, schemaString, 0);
            System.out.println(schemaString);

        });

    }
}
