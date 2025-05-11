import org.example.JSONString;
import org.example.JSONTN.JSONNumberTN;
import org.example.JSONTN.JSONTreeNodeType;
import org.example.Validator.JSONValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ValidationTest {

    @Test
    public void IntegerMinimumValidationShouldSucceedTest(){
        Assertions.assertDoesNotThrow(() -> {
            JSONNumberTN nmbNode = new JSONNumberTN(JSONTreeNodeType.NUMBER);
            nmbNode.setName("nmb");
            nmbNode.setValue("5");

            JSONValidator validator = new JSONValidator();
            boolean result = validator.validateAgainstSchema(nmbNode, new JSONString("vb"));
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
            boolean result = validator.validateAgainstSchema(nmbNode, new JSONString("vb"));
            Assertions.assertFalse(result);
        });
    }
}
