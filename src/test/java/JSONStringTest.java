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
           String propertyName = testJsonString.getPropertyName();
           Assertions.assertEquals("key1", propertyName);
       });
   }


   @Test
   public void getPropertyNameShouldThrowUnterminatedString(){

       JSONString testString = new JSONString("\n         {\"key1: null, key2:{sk:23}}");
       testString.getNextCharAndRemoveOmitWhitespaces(); //usuwa {
       testString.getNextCharAndRemoveOmitWhitespaces(); //usuwa "

       Assertions.assertThrows(JSONSchemaGeneratorException.class, () -> {
           testString.getPropertyName();
       });

   }

   @Test
   public void getPropertyNameWithQuotationAtStartTest(){
       JSONString testString = new JSONString("\"");
       Assertions.assertDoesNotThrow(()-> {
           String emptyString = testString.getPropertyName();
           Assertions.assertEquals("", emptyString);
       });

   }

   @Test
   public void getNull(){
        //jest sam null
        JSONString testString = new JSONString("null");

        Assertions.assertDoesNotThrow(() -> {
            String str = testString.getNull();
            Assertions.assertTrue(str.equals("null"));
            Assertions.assertEquals("", testString.getJson());
        });

       //jest cos po nullu
       JSONString testString2 = new JSONString("null, \"");

       Assertions.assertDoesNotThrow(() -> {
           String str = testString2.getNull();
           Assertions.assertTrue(str.equals("null"));
           Assertions.assertEquals(", \"", testString2.getJson());
       });
   }


    @Test
    public void getNullShouldThrowUnexpectedCharacter(){

        Assertions.assertThrows(JSONSchemaGeneratorException.class, () -> {
            JSONString testString = new JSONString("nulq");
            testString.getNull();

        });
    }


    @Test
    public void getNullShouldThrowUnknownValue(){

        var e = Assertions.assertThrows(JSONSchemaGeneratorException.class, () -> {
            JSONString testString = new JSONString("nul");
            testString.getNull();
        });
        Assertions.assertEquals("Unknown value: nul", e.getMessage());
    }

    @Test
    public void getBooleanTest(){
        //true
        Assertions.assertDoesNotThrow(() -> {
            JSONString testString = new JSONString("true");
            String resultString = testString.getBoolean();
            Assertions.assertEquals("true", resultString);
            Assertions.assertEquals("", testString.getJson());
        });

        //true
        Assertions.assertDoesNotThrow(() -> {
            JSONString testString = new JSONString("  \n  true, \"cos\": true");
            String resultString = testString.getBoolean();
            Assertions.assertEquals("true", resultString);
            Assertions.assertEquals(", \"cos\": true", testString.getJson());
        });

        //false
        Assertions.assertDoesNotThrow(() -> {
            JSONString testString = new JSONString("  \n  false, \"cos\": true");
            String resultString = testString.getBoolean();
            Assertions.assertEquals("false", resultString);
            Assertions.assertEquals(", \"cos\": true", testString.getJson());
        });

        var e1 = Assertions.assertThrows(JSONSchemaGeneratorException.class, () -> {
            JSONString testString = new JSONString("tru");
            testString.getBoolean();
        });
        Assertions.assertEquals("Unknown value: tru", e1.getMessage());

        var e2 = Assertions.assertThrows(JSONSchemaGeneratorException.class, () -> {
            JSONString testString = new JSONString("fals");
            testString.getBoolean();
        });
        Assertions.assertEquals("Unknown value: fals", e2.getMessage());

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
        try {
            //nie minus lub cyfra na poczatku
            JSONString testString = new JSONString("\n         E, key2:{sk:23}}");
            String numberString = testString.getNumber();
            Assertions.assertNull(numberString);


            //liczba dodatnia
            testString = new JSONString("\n         456");
            numberString = testString.getNumber();
            Assertions.assertEquals("456", numberString);

            //ujemna liczba
            testString = new JSONString("\n         -456");
            numberString = testString.getNumber();
            Assertions.assertEquals("-456", numberString);

            //liczba dodatnia i cos dalej
            testString = new JSONString("\n         456, key2:{sk:23}}");
            numberString = testString.getNumber();
            Assertions.assertEquals("456", numberString);

            //ujemna liczba i cos dalej
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



        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void getNumberShouldThrowLeadingZeros(){
        var e = Assertions.assertThrows(JSONSchemaGeneratorException.class, () -> {
            JSONString testString = new JSONString("00123");
            testString.getNumber();
        });
        Assertions.assertEquals("Leading zeros are not allowed", e.getMessage());


        var e2 = Assertions.assertThrows(JSONSchemaGeneratorException.class, () -> {
            JSONString testString = new JSONString("0123");
            testString.getNumber();
        });
        Assertions.assertEquals("Leading zeros are not allowed", e2.getMessage());
    }

}
