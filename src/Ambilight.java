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
    public static void main(String[] args) throws IOException {
        System.out.println("Hello World!"); // Display the string.
        Robot robot;
        SerialComm com = new SerialComm();
        com.ConnectPort(115200, "COM3");
        try{
        	robot = new Robot();
        	Rectangle area = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()); 
     		BufferedImage bi = robot.createScreenCapture(area);
     		System.out.println("screensize: "+ Toolkit.getDefaultToolkit().getScreenSize()); // Display the string.
     		//final byte[] pixels = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
            final int width  = bi.getWidth();
            final int height = bi.getHeight();
            // Java supports jagged arrays, can turn the red_s and red_t into one matrix
            // https://www.geeksforgeeks.org/jagged-array-in-java/
            // Screen side of row in red, green and blue: left 0, right 1, top 2 
            int[][] red = new int[3][];
            int[][] green = new int[3][];
            int[][] blue = new int[3][];
            // Initialize arrays for left and right side
            red[0]	= new int[height/4];
            green[0] = new int[height/4];
            blue[0]	= new int[height/4];
            red[1]	= new int[height/4];
            green[1] = new int[height/4];
            blue[1]	= new int[height/4];
            
            // Initialize arrays for top side
            red[2]	= new int[width/4];
            green[2] = new int[width/4];
            blue[2]	= new int[width/4];
            
            get_RGB_arrays(bi,red,blue,green);
            bi.flush();
            int messageSize = NEOPIXELS_SUM*3 + 2;
        	byte startMarker = 0x3C;
            byte endMarker = 0x3E;
            byte[] LED_DATA = new byte[60*3+2];
            LED_DATA[0] = startMarker;
            LED_DATA[messageSize-1] = endMarker;
            averageRGB(red,blue,green,LED_DATA);
          
            System.out.println("Width: "+width+" height: "+height);
            //System.out.println("blue: "+ Integer.toBinaryString(blue[0][100]));
     		// loop over the circumference of the image and do an averaging that is dependent on the number of neopixels
     		//https://www.tutorialspoint.com/java_dip/java_buffered_image.htm
     		// neopixels width: 30
            // neopixels height: 15
            // pixel width: 1920
            // pixel height: 1080
            // 1920/4/30 = 16
            // 1080/4/15 = 9
            for (int i = 1;i<181;i++) {
            	LED_DATA[i] = (byte)255;
            }
            int b;
            while(true) {
                //int message = 255;
                //byte lower =(byte)(message & 0xFF); //Get the lower 8bits
                byte MESSAGE_START_FULL[] = {startMarker, (byte)255,(byte)0,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x10,0x11,0x12,0x13,0x14,0x15,0x16,0x17,0x18,0x19,0x20,0x21,0x22,0x23,0x24,0x25,0x26,0x27,0x28,0x29,0x30,endMarker};
                //com.getSerialOutputStream().write(MESSAGE_START_FULL);
                b = LED_DATA.length;
                com.getSerialOutputStream().write(LED_DATA);
                
            }
            //com.disconnect();
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
    private static void get_RGB_arrays(BufferedImage image,int red[][],int green[][],int blue[][]) {
    	// TODO Instead of separate loops, extract both in same loop and then loop from height-4 to width-4 for top values
    	// TODO Get numbers that are not exactly on the border (just an offset, average of several?), some programs have a black circumference
    	final int width  = image.getWidth();
        final int height = image.getHeight();
        int tColor;
        int k = 0;
        int x,y;
        double startTime = System.nanoTime();
        for (y = 0; y < height - 4; y += 4) {
        	tColor = image.getRGB(0,y);
        	blue[0][k] = tColor & 0xff;
     		green[0][k] = (tColor & 0xff00) >> 8;
     		red[0][k] = (tColor & 0xff0000) >> 16;
     		//System.out.println("blue: "+ blue_s[0][k]);
     		tColor = image.getRGB(width-1,y);
        	blue[1][k] = tColor & 0xff;
     		green[1][k] = (tColor & 0xff00) >> 8;
     		red[1][k] = (tColor & 0xff0000) >> 16;
     		k++;
        }
        k=0;
        // Extract every fourth pixel value on top side of screen
        for (x = 0;x < width - 4; x += 4) {
        	tColor = image.getRGB(x,0);
        	blue[2][k] = tColor & 0xff;
     		green[2][k] = (tColor & 0xff00) >> 8;
     		red[2][k] = (tColor & 0xff0000) >> 16;
     		k++;
        }
        double endTime = System.nanoTime();
        System.out.println("Time screenshot:");
        System.out.printf("%f", (double)((endTime - startTime)/1000000));
        System.out.println("ms");
    }
    
    private static void averageRGB(int red[][],int green[][],int blue[][], byte LED_DATA[]) {
    	// TODO Instead of separate loops, extract both in same loop and then loop from height-4 to width-4 for top values
        int height = red[0].length/NEOPIXEL_HEIGHT;
        int width = red[2].length/NEOPIXEL_WIDTH;
        // index NEOPIXEL_HEIGHT until NEOPIXEL_WIDTH will not be used in R_avg[0,1][]
    	int[][] R_avg = new int[3][NEOPIXEL_WIDTH];
        int[][] G_avg = new int[3][NEOPIXEL_WIDTH];
        int[][] B_avg = new int[3][NEOPIXEL_WIDTH];
        int i,j,k;
        int sum_R,sum_G,sum_B;
        double startTime = System.nanoTime();
        for (i = 0; i < 2; i++) {
	        for (j = 0; j < NEOPIXEL_HEIGHT; j++) {
	        	sum_R = 0;
	        	sum_G = 0;
	        	sum_B = 0;
	        	for (k = j * height; k < (j + 1) * height; k++) {
	        		sum_R += red[i][k];
	        		sum_G += green[i][k];
	        		sum_B += blue[i][k];
	        	}
	        	R_avg[i][j] = sum_R/height;
	        	G_avg[i][j] = sum_G/height;
	        	B_avg[i][j] = sum_B/height;
	        }
        }
        for (j = 0; j < NEOPIXEL_WIDTH; j++) {
        	sum_R = 0;
        	sum_G = 0;
        	sum_B = 0;
        	for (k = j * width; k < (j + 1) * width; k++) {
        		sum_R += red[2][k];
        		sum_G += green[2][k];
        		sum_B += blue[2][k];
        	}
        	R_avg[2][j] = sum_R/width;
        	G_avg[2][j] = sum_G/width;
        	B_avg[2][j] = sum_B/width;
        }
        k = 1;
        // Set left side, start k = 1 because of startMarker
        for ( i = 0; i < NEOPIXEL_HEIGHT; i++) {
        	LED_DATA[k] = (byte)R_avg[0][i];
        	k++;
        	LED_DATA[k] = (byte)G_avg[0][i];
        	k++;
        	LED_DATA[k] = (byte)B_avg[0][i];
        	k++;
        }
        // Set top
        for ( i = 0; i < NEOPIXEL_WIDTH; i++) {
        	LED_DATA[k] = (byte)R_avg[2][i];
        	k++;
        	LED_DATA[k] = (byte)G_avg[2][i];
        	k++;
        	LED_DATA[k] = (byte)B_avg[2][i];
        	k++;
        }
        // Set right side
        for ( i = 0; i < NEOPIXEL_HEIGHT; i++) {
        	LED_DATA[k] = (byte)R_avg[1][i];
        	k++;
        	LED_DATA[k] = (byte)G_avg[1][i];
        	k++;
        	LED_DATA[k] = (byte)B_avg[1][i];
        	k++;
        }
        
        
        double endTime = System.nanoTime();
        System.out.println("Time averaging:");
        System.out.printf("%f", (double)((endTime - startTime)/1000000));
        System.out.println("ms");
        }
}