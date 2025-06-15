package org.example.Test;


import org.example.Exception.JSONSchemaGeneratorException;
import org.example.Generator.Generator;
import org.example.JSONString;
import org.example.JSONTN.*;
import org.example.Validator.JSONValidator;
import org.example.Validator.OnUnknownKeyword;


import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedHashSet;


import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;

public class TestFilesVisitor extends SimpleFileVisitor<Path> {

    private Generator generator = new Generator();
    private JSONValidator validator = new JSONValidator();
    private JSONValidator validatorPassingUnknownKeywords = new JSONValidator();
    private int testsCount = 0;
    private int testsPassed = 0;

    public TestFilesVisitor(){
        super();
        validatorPassingUnknownKeywords.setUnknownKeywordBehavior(OnUnknownKeyword.KEYWORD_VALIDATION_UNSUCCESFUL_CONTINUE_VALIDATION);
    }


    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        System.out.println(file);
        try {
            String testCasesArrayString = getTestCasesArrayStringFromFile(file);
            JSONArrayTN testCasesArray = (JSONArrayTN) generator.generateJsonTree(new JSONString(testCasesArrayString));

            LinkedHashSet<JSONTreeNode> testCases = testCasesArray.getItems();

            testCases.forEach(testCaseItem -> {
                JSONObjectTN testCase = (JSONObjectTN) testCaseItem;
                System.out.println(
                        ((JSONStringTN)
                                testCase.getProperties().stream()
                                .filter(prop-> prop.getName().equals("description"))
                                .toList().getFirst()
                        ).getValue()
                );

                JSONTreeNode testSchema = testCase.getProperties().stream().
                        filter(prop-> prop.getName().equals("schema"))
                        .toList().getFirst();


                LinkedHashSet<JSONTreeNode> tests =
                        ((JSONArrayTN)
                            testCase.getProperties().stream()
                            .filter(prop -> prop.getName().equals("tests"))
                            .toList().getFirst()
                        ).getItems();

                tests.forEach(test -> {
                    testsCount++;
                    JSONObjectTN testObject = (JSONObjectTN) test;
                    System.out.println(
                            ((JSONStringTN)
                                testObject.getProperties().stream()
                                .filter(testProp -> testProp.getName().equals("description"))
                                .toList().getFirst()
                            ).getValue()
                    );

                    boolean isDocumentValid =
                            ((JSONBooleanTN)
                                testObject.getProperties().stream()
                                .filter(testProp -> testProp.getName().equals("valid"))
                                .toList().getFirst()
                            ).getValue();

                    JSONTreeNode jsonToValidate =
                            testObject.getProperties().stream()
                            .filter(testProp -> testProp.getName().equals("data"))
                            .toList().getFirst();

                    try {
                        if (testSchema instanceof JSONObjectTN) {
                            boolean validationResult = validatorPassingUnknownKeywords.validateAgainstSchema(jsonToValidate, (JSONObjectTN) testSchema);
                            if (isDocumentValid == validationResult) {
                                System.out.println("Test passed");
                                testsPassed++;
                            } else
                                System.out.println("Test failed");
                        }
                    }catch(Exception e){
                        //TODO zrobic osobną klasę exception do nieudanej walidacji i do nieznanego keywordu
                        if( !isDocumentValid ){
                            System.out.println("Test passed");
                            testsPassed++;
                        }

                        System.out.println(e.getMessage());
                    }
                });

            });

            System.out.println("Tests passed: "+testsPassed+" / "+testsCount);

        } catch (JSONSchemaGeneratorException e) {
            e.printStackTrace(System.out);
        }

        return CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs){
        if(dir.getFileName().toString().equals("optional")){
            return SKIP_SUBTREE;
        }
        return CONTINUE;
    }

    private String getTestCasesArrayStringFromFile(Path file) throws IOException {
        BufferedReader reader = Files.newBufferedReader(file);
        StringBuilder jsonArray = new StringBuilder();
        String line = reader.readLine();
        while(line != null){
            jsonArray.append("\n").append(line);
            line = reader.readLine();
        }
        return jsonArray.toString();

    }


    @Override // from FileVisitor
    public FileVisitResult visitFileFailed(Path file,
                                           IOException exc) {
        System.err.println(exc.getMessage());
        return CONTINUE;
    }

}
