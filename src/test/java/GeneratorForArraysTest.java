import org.example.Generator.Generator;
import org.example.JSONString;
import org.example.JSONTN.JSONArrayTN;
import org.example.JSONTN.JSONTreeNode;
import org.example.JSONTN.JSONTreeNodeType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GeneratorForArraysTest {

    @Test
    public void generateTreeFromEmptyArray(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("[]");
            JSONTreeNode result = Generator.generateSchemaTree(json);

            Assertions.assertInstanceOf(JSONArrayTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.ARRAY, result.getType());
            Assertions.assertEquals(0, ((JSONArrayTN) result).getItems().size());
        });
    }

    @Test
    public void generateTreeFromArrayWithArrayItem(){
        Assertions.assertDoesNotThrow(() -> {
            JSONString json = new JSONString("[[]]");
            JSONTreeNode result = Generator.generateSchemaTree(json);

            Assertions.assertInstanceOf(JSONArrayTN.class, result);
            Assertions.assertEquals(JSONTreeNodeType.ARRAY, result.getType());
            Assertions.assertEquals(1, ((JSONArrayTN) result).getItems().size());

            JSONTreeNode subArray = ((JSONArrayTN) result).getItems().getFirst();
            Assertions.assertInstanceOf(JSONArrayTN.class, subArray);
            Assertions.assertEquals(JSONTreeNodeType.ARRAY, subArray.getType());
            Assertions.assertEquals(0, ((JSONArrayTN) subArray).getItems().size());
        });
    }

}
