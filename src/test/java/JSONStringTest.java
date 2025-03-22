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



}
