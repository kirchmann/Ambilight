import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Carl Christian
 *
 */
class Ambilight {
	private SerialComm ComPort;
	private Rectangle areaForScreenShot;
	private Robot robot;
	private AmbilightCalculator calculator;
	
    public void initialize() {
        this.createInstanceOfRobot();
        this.setAreaForScreenshot();
        this.calculator =  new AmbilightCalculator();
        this.setMessagePreamble(); // Won't ever be overwritten.
    }
    
    public ArrayList<String> listAllAvailabelComPorts() {
		return ComPort.listAllAvailabelComPorts();
    }

    public void createInstanceOfComPort() {
    	this.ComPort = new SerialComm();
    }

    public void connectComPort() {
    	this.ComPort.setcomPortName("COM4");
    	try {
			this.ComPort.ConnectPort(115200);
		} catch (IOException e) {
			e.printStackTrace();
		} 
    }
    
    //Call this method when exiting program.
    public void disconnectComPort() {
    	System.out.println("Disconnecting from ComPort ");
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
    	this.areaForScreenShot = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()); 
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