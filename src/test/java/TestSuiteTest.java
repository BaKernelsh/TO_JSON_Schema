import org.example.Test.TestFilesVisitor;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestSuiteTest {

    @Test
    public void suiteTest() throws IOException {

        Path testDir = Path.of("JSON-Schema-Test-Suite-main\\tests\\draft2020-12");

        TestFilesVisitor fileVisitor = new TestFilesVisitor();
        Files.walkFileTree(testDir, fileVisitor);


    }
}
