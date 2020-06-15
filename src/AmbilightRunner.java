import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
public class AmbilightRunner {
	private static final Logger LOGGER = Logger.getLogger( AmbilightRunner.class.getName() );
	private Ambilight ambilight;
	private int millisecondsPerScreenshot;
	private boolean isRunning;
	private boolean isConnectedToComPort;
	private Runnable ambilightRunnable;
	
	public AmbilightRunner() {
		this.isRunning = false;
		this.isConnectedToComPort = false;
		this.millisecondsPerScreenshot = 65;
		
        this.ambilight = new Ambilight();
        ambilight.createInstanceOfComPort();
        ambilight.initialize();
        ambilightRunnable = new Runnable() {
            public void run() {
            	if (isRunning && isConnectedToComPort) {
	              ambilight.takeScreenshot();
	              ambilight.calculateColorOfAllLEDs();
	              ambilight.flushBufferedImage();
	              ambilight.sendDataToCompPort();
            	}
            }
          };
	}
	
	void startAmbilight() {
        ScheduledExecutorService ambilightThread = Executors.newSingleThreadScheduledExecutor();
        ambilightThread.scheduleAtFixedRate(ambilightRunnable, 0, millisecondsPerScreenshot, TimeUnit.MILLISECONDS);
	}
	
	public ArrayList<String> listAllAvailabelComPorts(){
		return ambilight.listAllAvailabelComPorts();
	} 
	
	public void connectComPort(String comPort) {
		ambilight.connectComPort(comPort);
		this.isConnectedToComPort = true;
	}
	
	public void disconnectComPort() {
		ambilight.disconnectComPort();
		this.isConnectedToComPort = false;
	}
	
	public void setIsRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
	
	public void setIsConnectedToComPort(boolean isConnectedToComPort) {
		this.isConnectedToComPort = isConnectedToComPort;
	}
	
	public boolean getIsConnectedToComPort() {
		return this.isConnectedToComPort;
	}
	
	public void setMillisecondsPerScreenshot(int millisecondsPerScreenshot){
    	LOGGER.info("setMillisecondsPerScreenshot, old val: " + this.millisecondsPerScreenshot + ", new val: " + millisecondsPerScreenshot);
		this.millisecondsPerScreenshot = millisecondsPerScreenshot;

	}
}
