import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
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
	private SettingsContainer settings;
	
	public AmbilightRunner() {
		this.getSettings();
		this.isRunning = true;
		this.isConnectedToComPort = false;
		this.millisecondsPerScreenshot = settings.refreshRate;
		
        this.ambilight = new Ambilight(this.settings);
        ambilight.createInstanceOfComPort();
        this.connectComPort(settings.comPort);
        ambilight.initialize();
        ambilightRunnable = new Runnable() {
            public void run() {
            	if (isRunning && isConnectedToComPort) {
            	  Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            	  if(screenSize.width != settings.screenSize.width || screenSize.height != settings.screenSize.height )
            	  {
            		  settings.screenSize.width = screenSize.width;
            		  settings.screenSize.height = screenSize.height;
            		  ambilight.setAreaForScreenshot();
            	  }
            	  
	              ambilight.takeScreenshot();
	              ambilight.calculateColorOfAllLEDs();
	              ambilight.flushBufferedImage();
	              ambilight.sendDataToCompPort();
            	}
            }
          };
	}
	
    public void getSettings() {
    	SettingsReader settingsReader = new SettingsReader();
    	this.settings = settingsReader.readSettingsJson();
    	this.settings.screenSize = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
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
	
	public boolean getIsRunning() {
		return this.isRunning;
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
