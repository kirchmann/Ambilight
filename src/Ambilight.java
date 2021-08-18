import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * @author Carl Christian
 *
 */
class Ambilight {
	private static final Logger LOGGER = Logger.getLogger( Ambilight.class.getName() );
	private SerialComm ComPort;
	private Rectangle areaForScreenShot;
	private Robot robot;
	private AmbilightCalculator calculator;
	private SettingsContainer settings;
	
	public Ambilight(SettingsContainer settings) {
		this.settings = settings;
	}
	
    public void initialize() {
    	LOGGER.info("Initialize.");
        this.createInstanceOfRobot();
        this.calculator =  new AmbilightCalculator(settings);
        this.setAreaForScreenshot();
        
    }
    
    public ArrayList<String> listAllAvailabelComPorts() {
		return ComPort.listAllAvailabelComPorts();
    }

    public void createInstanceOfComPort() {
    	this.ComPort = new SerialComm();
    }

    public void connectComPort(String comPort) {
    	this.ComPort.setcomPortName(comPort);
    	try {
			this.ComPort.ConnectPort(115200);
		} catch (IOException e) {
			e.printStackTrace();
		} 
    }
    
    //Call this method when exiting program.
    public void disconnectComPort() {
    	this.ComPort.disconnect();
    }
    
	private void createInstanceOfRobot() {
    	try {
			this.robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
    
    public void setAreaForScreenshot() {
    	this.areaForScreenShot = this.settings.screenSize;
    	if (calculator!=null) {
    		calculator.updateScreenSize(settings.screenSize.width, settings.screenSize.height);
    	}
    	LOGGER.info("Setting area for screenshot: " + this.areaForScreenShot.width + "x" + this.areaForScreenShot.height);
    }
    

	public void takeScreenshot() {
		LOGGER.info("Take screenshot");
		this.calculator.bufferedImage = this.robot.createScreenCapture(this.areaForScreenShot);
	}
	
	public void sendDataToCompPort() {
		LOGGER.info("Sending data");
		this.ComPort.writeLedData(this.calculator.LED_DATA);
	}
	
	public void setMessagePreamble() {
		this.calculator.setMessagePreamble();
	}
	
	public void calculateColorOfAllLEDs() {
		LOGGER.info("calculateColorOfAllLEDs");
		this.calculator.calculateColorOfAllLEDs();
	}
	
	public void flushBufferedImage() {
		LOGGER.info("flushBufferedImage");
		this.calculator.bufferedImage.flush();
	}

}    