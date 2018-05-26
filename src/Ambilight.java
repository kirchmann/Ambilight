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
	SerialComm ComPort;
	BufferedImage bufferedImage;
	Rectangle areaForScreenShot;
	Robot robot;
	byte[] LED_DATA;

	
    public static void main(String[] args) throws IOException {
        Robot robot;
        Ambilight ambilight = new Ambilight();
        ambilight.ComPort = new SerialComm();
        // list all COM in GUI, ask GUI for COM drop down choice
        ambilight.ComPort.ConnectPort(115200, "COM4");
    	ambilight.createInstanceOfRobot();
    	ambilight.areaForScreenShot = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()); 
    	while(true) {
    		System.out.println("In loop");
    		// 0,0 in upper left triangle
     		ambilight.bufferedImage = ambilight.robot.createScreenCapture(ambilight.areaForScreenShot);
            ambilight.LED_DATA = new byte[messageSize];
            Color temp;
            // many magic numbers here, needs to be fixed
            int k = 9 + 3*NEOPIXELS_HEIGHT;
            int j = k + 1 + 3*NEOPIXELS_WIDTH;
            for (int i = 0; i < NEOPIXELS_HEIGHT; i++) {
            	// make three separate functions? One for left, one for top and one for right side.
            	temp = ambilight.getAvgColorRect(0, (i)*NEOPIXEL_AVG_RECT_SIDE_HEIGHT, RECT_SIDE_WIDTH, NEOPIXEL_AVG_RECT_SIDE_HEIGHT);
            	//int test = temp.getBlue();
            	// Inverted order for this part since it goes from up to down.
            	ambilight.LED_DATA[k] = (byte) temp.getRed();
            	ambilight.LED_DATA[k - 1] = (byte) temp.getGreen();
            	ambilight.LED_DATA[k - 2] = (byte) temp.getBlue();
            	int test5 = temp.getBlue();
            	byte test3b = (byte) test5;
            	int test6 = temp.getRed();
            	byte test6b = (byte) test6;
            	k = k - 3;
            	temp = ambilight.getAvgColorRect(SCREEN_WIDTH-RECT_SIDE_WIDTH, (i)*NEOPIXEL_AVG_RECT_SIDE_HEIGHT, RECT_SIDE_WIDTH, NEOPIXEL_AVG_RECT_SIDE_HEIGHT);
            	int test2 = temp.getBlue();
            	byte test2b = (byte) test2;
            	ambilight.LED_DATA[j] = (byte) temp.getBlue();
            	int test3 = temp.getGreen();
            	ambilight.LED_DATA[j + 1] = (byte) temp.getGreen();
            	int test4 = temp.getRed();
            	ambilight.LED_DATA[j + 2] = (byte) temp.getRed();
            	j = j + 3;
            }
            j = 10 + 3*NEOPIXELS_HEIGHT;
            for (int i = 0; i < NEOPIXELS_WIDTH; i++) {
            	temp = ambilight.getAvgColorRect((i)*RECT_TOP_WIDTH, 0, RECT_TOP_WIDTH, RECT_TOP_HEIGHT);
            	int test2 = temp.getBlue();
            	ambilight.LED_DATA[j] = (byte) temp.getBlue();
            	int test3 = temp.getGreen();
            	ambilight.LED_DATA[j + 1] = (byte) temp.getGreen();
            	int test4 = temp.getRed();
            	ambilight.LED_DATA[j + 2] = (byte) temp.getRed();
            	j = j + 3;
            }
            ambilight.bufferedImage.flush();
            ambilight.setMessagePreamble();
            ambilight.ComPort.getSerialOutputStream().write(ambilight.LED_DATA);
        }
        //ambilight.ComPort.disconnect();
    }
    
    private void createInstanceOfRobot() {
    	try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
     * (x0,y0) is your upper left coordinate, and (w,h)
     * are your width and height respectively
     */
    public Color getAvgColorRect(int x0, int y0, int w, int h) {
        int x1 = x0 + w;
        int y1 = y0 + h;
        long sumRed = 0, sumGreen = 0, sumBlue = 0;
        for (int x = x0; x < x1; x++) {
            for (int y = y0; y < y1; y++) {
                Color pixel = new Color(this.bufferedImage.getRGB(x, y));
                sumRed += pixel.getRed();
                sumGreen += pixel.getGreen();
                sumBlue += pixel.getBlue();
            }
        }
        int num = w * h;
        int test = (int)(sumRed / num);
        return new Color((int)(sumRed / num), (int)(sumGreen / num), (int)(sumBlue / num));
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