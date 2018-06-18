import java.awt.*;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
/**
 * 
 */

/**
 * @author Carl Christian
 *
 */
class Ambilight {
	private SerialComm ComPort;
	private Rectangle areaForScreenShot;
	private Robot robot;
	private AmbilightCalculator calculator;
	
    public static void main(String[] args) throws IOException {
        Ambilight ambilight = new Ambilight();
        ambilight.initialize();
        Runnable runnable = new Runnable() {
            public void run() {
              long startTime = System.nanoTime();
              ambilight.takeScreenshot();
              ambilight.calculateColorOfAllLEDs();
              ambilight.flushBufferedImage();
              ambilight.sendDataToCompPort();
              long endTime = System.nanoTime();
              long duration = (endTime - startTime)/1000000;  //divide by 1000000 to get milliseconds.
              System.out.println(duration);
            }
          };
          ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
          service.scheduleAtFixedRate(runnable, 0, 100, TimeUnit.MILLISECONDS);
    }
    public void initialize() {
        // list all COM in GUI, ask GUI for COM drop down choice?
        this.connectComPort();
        this.createInstanceOfRobot();
        this.setAreaForScreenshot();
        this.calculator =  new AmbilightCalculator();
        this.setMessagePreamble(); // Won't ever be overwritten.
    }
    
    private void connectComPort() {
    	this.ComPort = new SerialComm();
    	try {
			this.ComPort.ConnectPort(115200, "COM4");
		} catch (IOException e) {
			e.printStackTrace();
		} 
    }
    
    //Call this method when exiting program.
    private void disconnectComPort() {
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
    

	private void takeScreenshot() {
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