import org.example.JSONTN.JSONObjectTN;
import org.example.JSONTN.JSONTreeNodeType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;



public class JSONObjectTNTest {

    @Test
    public void addPropertyWithExistingNameTest(){
        JSONObjectTN parentObject = new JSONObjectTN();

        JSONObjectTN property = new JSONObjectTN(JSONTreeNodeType.OBJECT);
        property.setName("prop");

        JSONObjectTN propertyWithSameName = new JSONObjectTN(JSONTreeNodeType.OBJECT);
        propertyWithSameName.setName("prop");

        JSONObjectTN propertyWithOtherName = new JSONObjectTN(JSONTreeNodeType.OBJECT);
        propertyWithOtherName.setName("prop2");

        parentObject.addProperty(property);
        parentObject.addProperty(propertyWithOtherName);

        parentObject.addProperty(propertyWithSameName);

        Assertions.assertTrue(parentObject.getProperties().size() == 2);
        Assertions.assertTrue(parentObject.getProperties().contains(propertyWithSameName));
        Assertions.assertTrue(parentObject.getProperties().contains(propertyWithOtherName));

    }
}
