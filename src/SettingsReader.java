import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
/**
 * @author Carl Christian
 *
 */

public class SettingsReader {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) 
    {
        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();
         
        try (FileReader reader = new FileReader("ambilight_settings.json"))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
 
            JSONObject list = (JSONObject) obj;
            System.out.println(list);
             
            //Iterate over employee array
            parseEmployeeObject(list);
 
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    
    private static void parseEmployeeObject(JSONObject jsonObj) 
    {
        //Get employee object within list
        JSONObject neoObject = (JSONObject) jsonObj.get("neopixels");
         
        //Get employee first name
        Long height = (Long) neoObject.get("height");    
        System.out.println(height);
         
        //Get employee last name
        Long width = (Long) neoObject.get("width");  
        System.out.println(width);
        
        Long refreshRate = (Long) jsonObj.get("millisecondsPerScreenshot");  
        System.out.println(refreshRate);

    }
}
