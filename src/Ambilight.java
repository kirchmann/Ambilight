import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
/**
 * 
 */

/**
 * @author Carl Christian
 *
 */
class Ambilight {
	// TODO Move all calculations to separate class?
	static int SCREEN_HEIGHT = 1080;
	static int SCREEN_WIDTH = 1920;
	static int NEOPIXELS_WIDTH = 28;
	static int NEOPIXELS_HEIGHT = 16;
	static int NEOPIXELS_SUM = 2*NEOPIXELS_HEIGHT + NEOPIXELS_WIDTH;
	static int NEOPIXEL_AVG_RECT_SIDE_HEIGHT = SCREEN_HEIGHT/NEOPIXELS_HEIGHT;
	static int RECT_SIDE_WIDTH = 80;
	static int RECT_TOP_HEIGHT = 80;
	static int RECT_TOP_WIDTH = SCREEN_WIDTH/NEOPIXELS_WIDTH;
	static int PREAMBLE_LENGTH = 10;
	static int messageSize = NEOPIXELS_SUM*3 + PREAMBLE_LENGTH;
	static int BYTESPERLED = 3;
	private SerialComm ComPort;
	private BufferedImage bufferedImage;
	private Rectangle areaForScreenShot;
	private Robot robot;
	private byte[] LED_DATA;
	private Color colorOfCurrentRectangle;

	
    public static void main(String[] args) throws IOException {
        Ambilight ambilight = new Ambilight();
        ambilight.initialize();
        // list all COM in GUI, ask GUI for COM drop down choice?
    	while(true) {
    		long startTime = System.nanoTime();
    		// 0,0 in upper left triangle
     		ambilight.takeScreenshot();
            ambilight.calcColorOfSideRectangles();
            ambilight.calcColorOfTopRectangles();
            ambilight.flushBufferedImage();
            ambilight.sendDataToCompPort();
            long endTime = System.nanoTime();
    		long duration = (endTime - startTime)/1000000;  //divide by 1000000 to get milliseconds.
    		int asdasd = 5;
        }
        //ambilight.ComPort.disconnect();
    }
    public void initialize() {
        this.connectComPort();
        this.createInstanceOfRobot();
        this.setAreaForScreenshot();
        this.LED_DATA = new byte[messageSize];
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
    
    private void setAreaForScreenshot() {
    	this.areaForScreenShot = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()); 
    }
    
	private void createInstanceOfRobot() {
    	try {
			this.robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	private void takeScreenshot() {
		this.bufferedImage = this.robot.createScreenCapture(this.areaForScreenShot);
	}
	
	public void sendDataToCompPort() {
		this.ComPort.writeLedData(this.LED_DATA);
	}
	
	public void flushBufferedImage() {
		this.bufferedImage.flush();
	}
	
	private void calcColorOfSideRectangles() {
        int k = PREAMBLE_LENGTH;
        int j = PREAMBLE_LENGTH + BYTESPERLED*NEOPIXELS_HEIGHT + BYTESPERLED*NEOPIXELS_WIDTH;
        for (int i = 0; i < NEOPIXELS_HEIGHT; i++) {
        	this.getAvgColorOfSideRectangle(0, SCREEN_HEIGHT - (i + 1)*NEOPIXEL_AVG_RECT_SIDE_HEIGHT);
        	this.setLEDdata(k);
        	k = k + BYTESPERLED;
        	this.getAvgColorOfSideRectangle(SCREEN_WIDTH - RECT_SIDE_WIDTH, (i)*NEOPIXEL_AVG_RECT_SIDE_HEIGHT);
        	this.setLEDdata(j);
        	j = j + BYTESPERLED;
        }
	}
	
	private void calcColorOfTopRectangles() {
		int j = PREAMBLE_LENGTH + BYTESPERLED*NEOPIXELS_HEIGHT;
	    for (int i = 0; i < NEOPIXELS_WIDTH; i++) {
	    	this.getAvgColorOfTopRectangle((i)*RECT_TOP_WIDTH);
	    	this.setLEDdata(j);
	    	j = j + BYTESPERLED;
	    }

	}	
	
    	
	
	/*
     * (x0,y0) is your upper left coordinate
     */
    private void getAvgColorOfSideRectangle(int x0, int y0) {
        int x1 = x0 + RECT_SIDE_WIDTH;
        int y1 = y0 + NEOPIXEL_AVG_RECT_SIDE_HEIGHT;
        long sumRed = 0, sumGreen = 0, sumBlue = 0;
        for (int x = x0; x < x1; x++) {
            for (int y = y0; y < y1; y++) {
                Color pixel = new Color(this.bufferedImage.getRGB(x, y));
                sumRed += pixel.getRed();
                sumGreen += pixel.getGreen();
                sumBlue += pixel.getBlue();
            }
        }
        int divisorForAveraging = RECT_SIDE_WIDTH * NEOPIXEL_AVG_RECT_SIDE_HEIGHT;
        this.colorOfCurrentRectangle = new Color((int)(sumRed / divisorForAveraging), (int)(sumGreen / divisorForAveraging), (int)(sumBlue / divisorForAveraging));
    }
      private void getAvgColorOfTopRectangle(int x0) {
    	  int x1 = x0 + RECT_TOP_WIDTH;
          int y1 = RECT_TOP_HEIGHT;
          long sumRed = 0, sumGreen = 0, sumBlue = 0;
          for (int x = x0; x < x1; x++) {
              for (int y = 0; y < y1; y++) {
                  Color pixel = new Color(this.bufferedImage.getRGB(x, y));
                  sumRed += pixel.getRed();
                  sumGreen += pixel.getGreen();
                  sumBlue += pixel.getBlue();
              }
          }
          int divisorForAveraging = RECT_TOP_HEIGHT * RECT_TOP_HEIGHT;
          this.colorOfCurrentRectangle = new Color((int)(sumRed / divisorForAveraging), (int)(sumGreen / divisorForAveraging), (int)(sumBlue / divisorForAveraging));
	}
      
  	private void setLEDdata(int j) {
    	LED_DATA[j] = (byte) this.colorOfCurrentRectangle.getBlue();
    	LED_DATA[j + 1] = (byte) this.colorOfCurrentRectangle.getGreen();
    	LED_DATA[j + 2] = (byte) this.colorOfCurrentRectangle.getRed();
	}
      
    private void setMessagePreamble() {
        LED_DATA[0] = (byte)0x00;
        LED_DATA[1] = (byte)0x01;
        LED_DATA[2] = (byte)0x02;
        LED_DATA[3] = (byte)0x03;
        LED_DATA[4] = (byte)0x04;
        LED_DATA[5] = (byte)0x05;
        LED_DATA[6] = (byte)0x06;
        LED_DATA[7] = (byte)0x07;
        LED_DATA[8] = (byte)0x08;
        LED_DATA[9] = (byte)0x09;	
    }
}    