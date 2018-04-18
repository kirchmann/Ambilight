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
	static int NEOPIXEL_WIDTH = 30;
	static int NEOPIXEL_HEIGHT = 15;
	static int NEOPIXELS_SUM = 2*NEOPIXEL_HEIGHT + NEOPIXEL_WIDTH;
	static int WIDTH = 1920/4;
	static int HEIGHT = 1080/4;
	static int LENGTH = 2*HEIGHT + WIDTH;
	static int LEFT_INDX = 0;
	static int TOP_INDX = HEIGHT;
	static int RIGHT_INDX = HEIGHT + WIDTH;
    public static void main(String[] args) throws IOException {
        Robot robot;
        SerialComm com = new SerialComm();
        com.ConnectPort(115200, "COM3");
        try{
        	
        	robot = new Robot();
        	while(true) {
	        	Rectangle area = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()); 
	     		BufferedImage bi = robot.createScreenCapture(area);
	            // Screen side of row in red, green and blue: left 0, right 1, top 2
	            // 0- height/4 -> left side. height/4 to height/4 + width -> top side. height/4 + width to end -> right side 
	            int[] red = new int[2*HEIGHT + WIDTH];
	            int[] green = new int[2*HEIGHT + WIDTH];
	            int[] blue = new int[2*HEIGHT + WIDTH];
	            	            
	            get_RGB_arrays(bi,red,blue,green);
	            bi.flush();
	            int messageSize = NEOPIXELS_SUM*3 + 2;
	        	byte startMarker = 0x3C;
	            byte endMarker = 0x3E;
	            byte[] LED_DATA = new byte[messageSize];
	            LED_DATA[0] = startMarker;
	            LED_DATA[messageSize-1] = endMarker;
	            averageRGB(red,blue,green,LED_DATA);
	          
	            // System.out.println("Width: "+width+" height: "+height);
	     		// neopixels width: 30
	            // neopixels height: 15
	            // pixel width: 1920
	            // pixel height: 1080
	            // 1920/4/30 = 16
	            // 1080/4/15 = 9
	            
	            /*
	            for (int i = 1;i<181;i++) {
	            	LED_DATA[i] = (byte)255;
	            }
	            */
	            
	                //int message = 255;
	                //byte lower =(byte)(message & 0xFF); //Get the lower 8bits
	                //byte MESSAGE_START_FULL[] = {startMarker, (byte)255,(byte)0,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x10,0x11,0x12,0x13,0x14,0x15,0x16,0x17,0x18,0x19,0x20,0x21,0x22,0x23,0x24,0x25,0x26,0x27,0x28,0x29,0x30,endMarker};
	                //com.getSerialOutputStream().write(MESSAGE_START_FULL);
	                //int len = 18;
	                //int offset = 0;
	                LED_DATA[1]=(byte)(0x01);
	                // Filter all except first occurrence of startMarker from LED_DATA
	                for (int i = 1;i < messageSize;i++) {
	                	if (LED_DATA[i] == startMarker) {
	                		LED_DATA[i] = (byte) (startMarker + 0x01);
	                	}
	                	/*
	                	if(i % 3 == 0) {
	                		LED_DATA[i] = (byte) 0x00;
	                	}
	                	if((i + 1) % 3 == 0) {
	                		LED_DATA[i] = (byte) 0x0F;
	                	}
	                	if((i + 2) % 3 == 0) {
	                		LED_DATA[i] = (byte) 0x00;
	                	}
	                	*/
	                }
	                //LED_DATA[len - 1]=endMarker;
	                int testmessageSize = 30 + 2;
		            byte[] TEST_DATA = new byte[testmessageSize];
		            TEST_DATA[0] = startMarker;
	                for (int i = 1;i < testmessageSize;i++) { 
	                	TEST_DATA[i] = (byte) (100);
	                	if((i + 2) % 3 == 0) {
	                		TEST_DATA[i] = (byte) 0x00;
	                	}
	                }
                	
	                TEST_DATA[31] = endMarker;
	                com.getSerialOutputStream().write(TEST_DATA);
	                //com.getSerialOutputStream().write(LED_DATA);
            }
            //com.disconnect();
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
    private static void get_RGB_arrays(BufferedImage image,int red[],int green[],int blue[]) {
    	// TODO Get numbers that are not exactly on the border (just an offset, average of several?), some programs have a black circumference. Use a box instead?
    	final int width  = image.getWidth();
        final int height = image.getHeight();
        int tColor;
        int k = 0;
        int i = 0;
        int x,y;
        int tBlue,tRed,tGreen;
        double startTime = System.nanoTime();
        // Extract every fourth pixel value on left side of screen. Since it is the left side, go from bottom up
        for (y =  height - 4; y > 0; y -= 4) {
    		tColor = image.getRGB(i,y);
        	blue[k] = (tColor & 0xff);
     		green[k] = ((tColor & 0xff00) >> 8);
     		red[k] = ((tColor & 0xff0000) >> 16);
     		k++;
        }
        // Extract every fourth pixel value on top side of screen
        for (x = 0;x < width - 4; x += 4) {
        	tColor = image.getRGB(x,0);
        	blue[k] = tColor & 0xff;
     		green[k] = (tColor & 0xff00) >> 8;
     		red[k] = (tColor & 0xff0000) >> 16;
     		k++;
        }
        for (y = 0; y < height - 4; y += 4) {
    		tColor = image.getRGB(width - 1,y);
        	blue[k] = (tColor & 0xff);
     		green[k] = ((tColor & 0xff00) >> 8);
     		red[k] = ((tColor & 0xff0000) >> 16);
     		k++;
        }
        double endTime = System.nanoTime();
        System.out.println("Time screenshot:");
        System.out.printf("%f", (double)((endTime - startTime)/1000000));
        System.out.println("ms");
    }
    
    private static void averageRGB(int red[],int green[],int blue[], byte LED_DATA[]) {
    	int[] R_avg = new int[NEOPIXELS_SUM];
        int[] G_avg = new int[NEOPIXELS_SUM];
        int[] B_avg = new int[NEOPIXELS_SUM];
        int section_heigth = HEIGHT/NEOPIXEL_HEIGHT;
        int section_width = WIDTH/NEOPIXEL_WIDTH;
        int i,k;
        int sum_R,sum_G,sum_B;
        double startTime = System.nanoTime();
        k = 0;
        for (i = 0; i < NEOPIXELS_SUM;i++) {
        	sum_R = 0;sum_G=0;sum_B=0;
        	for (k = 0;k < 10; k++) {
        		sum_R = sum_R + red[1];
        	}
        }
                
        k = 1; // First bit will be message start bit
        for ( i = 0; i < NEOPIXELS_SUM; i++) {
        	LED_DATA[k] = (byte)R_avg[i];
        	k++;
        	LED_DATA[k] = (byte)G_avg[i];
        	k++;
        	LED_DATA[k] = (byte)B_avg[i];
        	k++;
        }
        double endTime = System.nanoTime();
        System.out.println("Time averaging:");
        System.out.printf("%f", (double)((endTime - startTime)/1000000));
        System.out.println("ms");
        }
}