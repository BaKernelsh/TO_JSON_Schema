import org.example.Exception.JSONSchemaGeneratorException;
import org.example.JSONString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;


public class JSONStringTest {

    JSONString testJsonString;

    @BeforeEach
    public void setTestJson(){
        this.testJsonString = new JSONString("\n         {\"key1\": \"string\", \"key2\":{\"sk\":23}}"); // {"key1": "string", "key2":{"sk":23}}
    }

   @Test
   public void getObjectFirstCharTest(){

       Character ch = testJsonString.getNextChar();
       Assertions.assertEquals('{', ch);
   }

   @Test
   public void getCharsToEndRemoveWhitespacesTest(){
        int length = testJsonString.getJson().length();
        Character arr[] = new Character[length];

        for(int i=0; i<length; i++){
            arr[i] = testJsonString.getNextCharAndRemoveOmitWhitespaces();
            if(arr[i] == null) break;
        }

       System.out.println(Arrays.toString(arr));
        Character[] okArray = new Character[]{
                '{', '"', 'k', 'e', 'y', '1', '"', ':', '"',
                's', 't', 'r', 'i', 'n', 'g', '"', ',', '"',
                'k', 'e', 'y', '2', '"', ':', '{', '"', 's',
                'k', '"', ':', '2', '3', '}', '}', null, null
        };
       Assertions.assertArrayEquals(okArray, arr);
   }


   @Test
   public void getPropertyNameTest(){
       testJsonString.getNextCharAndRemoveOmitWhitespaces(); //usuwa {
       testJsonString.getNextCharAndRemoveOmitWhitespaces(); //usuwa "

       Assertions.assertDoesNotThrow(() -> {
           String propertyName = testJsonString.getPropertyName('"');
           Assertions.assertEquals("key1", propertyName);
       });
   }


   @Test
   public void getPropertyNameShouldThrowUnterminatedString(){

       JSONString testString = new JSONString("\n         {\"key1: null, key2:{sk:23}}");
       testString.getNextCharAndRemoveOmitWhitespaces(); //usuwa {
       testString.getNextCharAndRemoveOmitWhitespaces(); //usuwa "

       Assertions.assertThrows(JSONSchemaGeneratorException.class, () -> {
           testString.getPropertyName('"');
       });

   }

   @Test
   public void getNull(){
        JSONString testString = new JSONString("null");

        Assertions.assertDoesNotThrow(() -> {
            String str = testString.getNull();
            Assertions.assertTrue(str.equals("null"));
        });

       JSONString testString2 = new JSONString("null, cosdalej");

       Assertions.assertDoesNotThrow(() -> {
           
       });
   }


    @Test
    public void getNullShouldThrowUnexpectedCharacter(){


        Assertions.assertThrows(JSONSchemaGeneratorException.class, () -> {
            JSONString testString = new JSONString("nulg");
            testString.getNull();

        });

        //testString = new JSONString("null");
    }


    public void getBooleanTest(){
        //true
        Assertions.assertDoesNotThrow(() -> {
            JSONString testString = new JSONString("  \n  true, \"cos\": true");
            String resultString = testString.getBoolean();
            Assertions.assertEquals("true", resultString);
        });

        //false
        Assertions.assertDoesNotThrow(() -> {
            JSONString testString = new JSONString("  \n  false, \"cos\": true");
            String resultString = testString.getBoolean();
            Assertions.assertEquals("false", resultString);
        });

        //pierwsza litera to nie 't' ani 'f'
        Assertions.assertDoesNotThrow(() -> {
            JSONString testString = new JSONString("  \n  halse, \"cos\": true");
            String resultString = testString.getBoolean();
            Assertions.assertNull(resultString);
        });

    }

    @Test
    public void getBooleanShouldThrowUnexpectedCharacter(){
        //pierwsza litera 't', reszta zle
        Assertions.assertThrows(JSONSchemaGeneratorException.class, ()-> {
            JSONString testString = new JSONString("  \n  trzx, \"cos\": true");
            testString.getBoolean();
        });
    }


    @Test
    public void getNumberTest(){

        //nie minus lub cyfra na poczatku
        JSONString testString = new JSONString("\n         E, key2:{sk:23}}");
        String numberString = testString.getNumber();
        Assertions.assertNull(numberString);


        //liczba dodatnia
        testString = new JSONString("\n         456, key2:{sk:23}}");
        numberString = testString.getNumber();
        Assertions.assertEquals("456", numberString);

        //ujemna liczba
        testString = new JSONString("\n         -456, key2:{sk:23}}");
        numberString = testString.getNumber();
        Assertions.assertEquals("-456", numberString);

        //liczba z częścią dziesiętną
        testString = new JSONString("\n         -456.1234, key2:{sk:23}}");
        numberString = testString.getNumber();
        Assertions.assertEquals("-456.1234", numberString);


        //liczba z e-
        testString = new JSONString("\n         456e-5, key2:{sk:23}}");
        numberString = testString.getNumber();
        Assertions.assertEquals("456e-5", numberString);

        //liczba z E-
        testString = new JSONString("\n         456E-5, key2:{sk:23}}");
        numberString = testString.getNumber();
        Assertions.assertEquals("456E-5", numberString);

        //liczba z e+
        testString = new JSONString("\n         456e+5, key2:{sk:23}}");
        numberString = testString.getNumber();
        Assertions.assertEquals("456e+5", numberString);

        //liczba z E+
        testString = new JSONString("\n         456E+5, key2:{sk:23}}");
        numberString = testString.getNumber();
        Assertions.assertEquals("456E+5", numberString);


        //ujemna liczba z e-
        testString = new JSONString("\n         -456e-5, key2:{sk:23}}");
        numberString = testString.getNumber();
        Assertions.assertEquals("-456e-5", numberString);

        //ujemna liczba z E-
        testString = new JSONString("\n         -456E-5, key2:{sk:23}}");
        numberString = testString.getNumber();
        Assertions.assertEquals("-456E-5", numberString);

        //ujemna liczba z e+
        testString = new JSONString("\n        -456e+5, key2:{sk:23}}");
        numberString = testString.getNumber();
        Assertions.assertEquals("-456e+5", numberString);

        //ujemna liczba z E+
        testString = new JSONString("\n         -456E+5, key2:{sk:23}}");
        numberString = testString.getNumber();
        Assertions.assertEquals("-456E+5", numberString);


        //liczba z częścią dziesiętną z e-
        testString = new JSONString("\n         456.456e-5, key2:{sk:23}}");
        numberString = testString.getNumber();
        Assertions.assertEquals("456.456e-5", numberString);

        //liczba z częścią dziesiętną z E-
        testString = new JSONString("\n         456.456E-5, key2:{sk:23}}");
        numberString = testString.getNumber();
        Assertions.assertEquals("456.456E-5", numberString);

        //liczba z częścią dziesiętną z e+
        testString = new JSONString("\n         456.456e+5, key2:{sk:23}}");
        numberString = testString.getNumber();
        Assertions.assertEquals("456.456e+5", numberString);

        //liczba z częścią dziesiętną z E+
        testString = new JSONString("\n         456.456E+5, key2:{sk:23}}");
        numberString = testString.getNumber();
        Assertions.assertEquals("456.456E+5", numberString);

    }

}
