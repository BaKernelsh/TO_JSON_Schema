import org.example.Exception.JSONValidationException;
import org.example.Exception.UnknownValidationKeywordException;
import org.example.Generator.Generator;
import org.example.JSONTN.JSONArrayTN;
import org.example.JSONTN.JSONTreeNode;
import org.example.Validator.JSONValidator;
import org.example.Validator.ValidationResultAndErrorMessage;
import org.example.Validator.VerifyBoolAndVerifierMethod;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;

public class SettingValidationMethodsTest {

    @Test
    public void setGenericValidationMethodTest() {
        Assertions.assertDoesNotThrow(() -> {

            VerifyBoolAndVerifierMethod<JSONTreeNode, JSONTreeNode> verifyItems = VerifyBoolAndVerifierMethod.withAssertionValueAsJSONTreeNode();
            verifyItems.setVerifierMethod((node, assertion, validatorInstance) ->
            {
                LinkedHashSet<JSONTreeNode> itemsCopy = new LinkedHashSet<>(((JSONArrayTN) assertion).getItems());
                int itemsCopyCount = itemsCopy.size();
                LinkedHashSet<JSONTreeNode> nodeItems = ((JSONArrayTN) node).getItems();

                int i = 1;
                for (JSONTreeNode nodeItem : nodeItems) {
                    if (i > itemsCopyCount)
                        break;
                    JSONTreeNode item = itemsCopy.removeFirst();
                    try {
                        validatorInstance.validateAgainstSchema(nodeItem, item);
                    } catch (JSONValidationException | UnknownValidationKeywordException e) {
                        return new ValidationResultAndErrorMessage(false, e.getMessage());
                    }
                }

                return ValidationResultAndErrorMessage.newInstanceValid();
            });

            JSONValidator validator = new JSONValidator();
            validator.setVerifyingMethodForKeyword("array", "items", verifyItems);
            Generator generator = new Generator();

            String json = "[3,\"str\"]";
            String schema = generator.generateSchema(json);
            System.out.println(schema);

            boolean result = validator.validateAgainstSchema(json, schema);
            Assertions.assertTrue(result);
        });


    }
}
