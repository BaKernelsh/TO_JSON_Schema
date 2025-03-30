import org.example.JSONTN.JSONArrayTN;
import org.example.JSONTN.JSONStringTN;
import org.example.JSONTN.JSONTreeNodeType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JSONArrayTNTest {

    @Test
    public void addItemsOfSameTypeAndValueTest(){

        JSONStringTN node1 = new JSONStringTN(JSONTreeNodeType.STRING);
        node1.setValue("value");

        JSONStringTN node2 = new JSONStringTN(JSONTreeNodeType.STRING);
        node2.setValue("value");

        JSONArrayTN arrayNode = new JSONArrayTN(JSONTreeNodeType.ARRAY);
        arrayNode.addItem(node1);
        arrayNode.addItem(node2);

        //powinno dodać oba
        Assertions.assertEquals(2, arrayNode.getItems().size());
    }


}
