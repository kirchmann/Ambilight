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
        this.setAreaForScreenshot();
        this.calculator =  new AmbilightCalculator(settings);
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
    
    private void setAreaForScreenshot() {
    	this.areaForScreenShot = this.settings.screenSize;
    	LOGGER.info("Setting area for screenshot: " + this.areaForScreenShot.width + "x" + this.areaForScreenShot.height);
    }
    

	public void takeScreenshot() {
		this.calculator.bufferedImage = this.robot.createScreenCapture(this.areaForScreenShot);
	}
	
	public void sendDataToCompPort() {
		this.ComPort.writeLedData(this.calculator.LED_DATA);
	}
	
	public void setMessagePreamble() {
		this.calculator.setMessagePreamble();
	}
	
	public void calculateColorOfAllLEDs() {
		this.calculator.calculateColorOfAllLEDs();
	}
	
	public void flushBufferedImage() {
		this.calculator.bufferedImage.flush();
	}

}    