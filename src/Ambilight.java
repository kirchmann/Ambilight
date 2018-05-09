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
class ambilight {
	static int SCREEN_HEIGHT = 1080;
	static int SCREEN_WIDTH = 1920;
	static int NEOPIXELS_WIDTH = 10;
	static int NEOPIXELS_HEIGHT = 5;
	static int NEOPIXELS_SUM = 2*NEOPIXELS_HEIGHT + NEOPIXELS_WIDTH;
	static int RECT_SIDE_HEIGHT = SCREEN_HEIGHT/NEOPIXELS_HEIGHT;
	static int RECT_SIDE_WIDTH = 100;
	static int RECT_TOP_HEIGHT = 100;
	static int RECT_TOP_WIDTH = SCREEN_WIDTH/NEOPIXELS_WIDTH;
	
    public static void main(String[] args) throws IOException {
        Robot robot;
        SerialComm com = new SerialComm();
        com.ConnectPort(115200, "COM3");
        try{
        	
        	robot = new Robot();
        	while(true) {
	        	Rectangle area = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()); 
	        	// get sub-rectangles from the entire area. split left and side into 5 rectangles
	        	// 0,0 in upper left triangle
	     		BufferedImage bi = robot.createScreenCapture(area);
	            // Screen side of row in red, green and blue: left 0, right 1, top 2
	            // 0- height/4 -> left side. height/4 to height/4 + width -> top side. height/4 + width to end -> right side 
	     		
	            
	            int messageSize = NEOPIXELS_SUM*3 + 10; //  is the size of the preamble
	            byte[] LED_DATA = new byte[messageSize];
	            Color temp;
	            int k = 9 + 3*NEOPIXELS_HEIGHT;
	            int j = k + 1 + 3*NEOPIXELS_WIDTH;
	            for (int i = 0; i < NEOPIXELS_HEIGHT; i++) {
	            	temp = getAvgColorRect(bi, 0, (i)*RECT_SIDE_HEIGHT, RECT_SIDE_WIDTH, RECT_SIDE_HEIGHT);
	            	//int test = temp.getBlue();
	            	// Inverted order for this part since it goes from up to down.
	            	LED_DATA[k] = (byte) temp.getRed();
	            	LED_DATA[k - 1] = (byte) temp.getGreen();
	            	LED_DATA[k - 2] = (byte) temp.getBlue();
	            	int test5 = temp.getBlue();
	            	byte test3b = (byte) test5;
	            	int test6 = temp.getRed();
	            	byte test6b = (byte) test6;
	            	k = k - 3;
	            	temp = getAvgColorRect(bi, SCREEN_WIDTH-RECT_SIDE_WIDTH, (i)*RECT_SIDE_HEIGHT, RECT_SIDE_WIDTH, RECT_SIDE_HEIGHT);
	            	int test2 = temp.getBlue();
	            	byte test2b = (byte) test2;
	            	LED_DATA[j] = (byte) temp.getBlue();
	            	int test3 = temp.getGreen();
	            	LED_DATA[j + 1] = (byte) temp.getGreen();
	            	int test4 = temp.getRed();
	            	LED_DATA[j + 2] = (byte) temp.getRed();
	            	j = j + 3;
	            }
	            j = 10 + 3*NEOPIXELS_HEIGHT;
	            for (int i = 0; i < NEOPIXELS_WIDTH; i++) {
	            	temp = getAvgColorRect(bi, (i)*RECT_TOP_WIDTH, 0, RECT_TOP_WIDTH, RECT_TOP_HEIGHT);
	            	int test2 = temp.getBlue();
	            	LED_DATA[j] = (byte) temp.getBlue();
	            	int test3 = temp.getGreen();
	            	LED_DATA[j + 1] = (byte) temp.getGreen();
	            	int test4 = temp.getRed();
	            	LED_DATA[j + 2] = (byte) temp.getRed();
	            	j = j + 3;
	            }
	            bi.flush();
	            
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
	            
                int testmessageSize = NEOPIXELS_SUM*3 + 10;
	            byte[] TEST_DATA = new byte[testmessageSize];
                for (int i = 10;i < testmessageSize;i++) { 
                	TEST_DATA[i] = (byte) (100);
                	if((i + 3) % 3 == 0) {
                		TEST_DATA[i] = (byte) 0x00;
                	}
                }
                TEST_DATA[0] = (byte)0x00;
                TEST_DATA[1] = (byte)0x01;
                TEST_DATA[2] = (byte)0x02;
                TEST_DATA[3] = (byte)0x03;
                TEST_DATA[4] = (byte)0x04;
                TEST_DATA[5] = (byte)0x05;
                TEST_DATA[6] = (byte)0x06;
                TEST_DATA[7] = (byte)0x07;
                TEST_DATA[8] = (byte)0x08;
                TEST_DATA[9] = (byte)0x09;
                com.getSerialOutputStream().write(TEST_DATA);
                //com.getSerialOutputStream().write(LED_DATA);
            }
            //com.disconnect();
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
    
    /*
     * Where bi is your image, (x0,y0) is your upper left coordinate, and (w,h)
     * are your width and height respectively
     */
    public static Color getAvgColorRect(BufferedImage bi, int x0, int y0, int w, int h) {
        int x1 = x0 + w;
        int y1 = y0 + h;
        long sumRed = 0, sumGreen = 0, sumBlue = 0;
        for (int x = x0; x < x1; x++) {
            for (int y = y0; y < y1; y++) {
            	if (x >= 1920 || y >=1080) {
            		int problem = 2;
            	}
                Color pixel = new Color(bi.getRGB(x, y));
                sumRed += pixel.getRed();
                sumGreen += pixel.getGreen();
                sumBlue += pixel.getBlue();
            }
        }
        int num = w * h;
        int test = (int)(sumRed / num);
        return new Color((int)(sumRed / num), (int)(sumGreen / num), (int)(sumBlue / num));
    }
}    