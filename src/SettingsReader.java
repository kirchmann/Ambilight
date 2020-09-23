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
    
    private static void readSettingsFromJsonObj(JSONObject jsonObj, SettingsContainer settingsContainer) 
    {
        JSONObject neoObject = (JSONObject) jsonObj.get("neopixels");
         
        settingsContainer.nrOfNeopixelHeight = ((Long) neoObject.get("height")).intValue();
        settingsContainer.nrOfNeopixelWidth = ((Long) neoObject.get("width")).intValue();  
        settingsContainer.refreshRate = ((Long) jsonObj.get("millisecondsPerScreenshot")).intValue();  
        settingsContainer.comPort = (String) jsonObj.get("comport");
    }
    
    public SettingsContainer readSettingsJson() {
    	SettingsContainer settings = new SettingsContainer();
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader("ambilight_settings.json"))
        {
            Object obj = jsonParser.parse(reader);
            JSONObject jsonObj = (JSONObject) obj;
            System.out.println(jsonObj);
            readSettingsFromJsonObj(jsonObj, settings);
 
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    	return settings;
    	
    }
}
