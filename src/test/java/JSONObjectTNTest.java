import org.example.JSONTN.JSONObjectTN;
import org.example.JSONTN.JSONTreeNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class JSONObjectTNTest {

    @Test
    public void addPropertyWithExistingNameTest(){
        JSONObjectTN parentObject = new JSONObjectTN();

        JSONObjectTN property = new JSONObjectTN("object");
        property.setName("prop");

        JSONObjectTN propertyWithSameName = new JSONObjectTN("object");
        propertyWithSameName.setName("prop");

        JSONObjectTN propertyWithOtherName = new JSONObjectTN("object");
        propertyWithOtherName.setName("prop2");

        parentObject.addProperty(property);
        parentObject.addProperty(propertyWithOtherName);

        parentObject.addProperty(propertyWithSameName);

        Assertions.assertTrue(parentObject.getProperties().size() == 2);
        Assertions.assertTrue(parentObject.getProperties().contains(propertyWithSameName));
        Assertions.assertTrue(parentObject.getProperties().contains(propertyWithOtherName));

    }
}
