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
            // Screen side of row in red, green and blue: left, right, top 
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
            
            // Initialize arrays for left and right side
            red[2]	= new int[width/4];
            green[2] = new int[width/4];
            blue[2]	= new int[width/4];

            get_RGB_arrays(bi,red,blue,green);
            bi.flush();
            averageRGB(red,blue,green);
            System.out.println("Width: "+width+" height: "+height);
            System.out.println("blue: "+ Integer.toBinaryString(blue[0][100]));

     		// loop over the circumference of the image and do an averaging that is dependent on the number of neopixels
     		//https://www.tutorialspoint.com/java_dip/java_buffered_image.htm
     		// neopixels width: 30
            // neopixels height: 16
            // pixel width: 1920
            // pixel height: 1080
            // 1920/4/30 = 16
            // 1080/4/16 = 9
        } catch (Exception e) {
        	e.printStackTrace();
        }
        while(true) {
            //int message = 255;
            //byte lower =(byte)(message & 0xFF); //Get the lower 8bits
            // TODO add start of message. 
            /*
            byte MESSAGE_START[] = {0x01, 0x04, 0x06, 0x08, 0x010, 0x12, 0x14, 0x16, 0x18, 0x20};
        	for (int i = 0;i<10;i++) {
        		com.getSerialOutputStream().write(MESSAGE_START[i]);
        	}
        	*/
            //byte MESSAGE_START[] = {0x01, 0x04, 0x06, 0x08, 0x010, 0x12, 0x14, 0x16, 0x18, 0x20};
            
        	byte startMarker = 0x3C;
            byte endMarker = 0x3E;
            byte MESSAGE_START_FULL[] = {startMarker, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x10,0x11, 0x12, 0x13, 0x14, 0x015, 0x16, 0x17, 0x18, 0x19, 0x20, 0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x30, endMarker};
            com.getSerialOutputStream().write(MESSAGE_START_FULL);
        	/*
        	try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/

        	/*
        	for (int i = 15;i<30;i++) {
        		com.getSerialOutputStream().write(0xFF);
        	}
        	*/
        }
        
        //com.disconnect();
    }
    private static void get_RGB_arrays(BufferedImage image,int red[][],int green[][],int blue[][]) {
    	// TODO Instead of separate loops, extract both in same loop and then loop from height-4 to width-4 for top values
    	final int width  = image.getWidth();
        final int height = image.getHeight();
        int tColor;
        int k = 0;
        int x,y;
        double startTime = System.nanoTime();
        for (y=0;y<height-4;y+=4) {
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
        for (x = 0;x < width-4;x += 4) {
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
    
    private static void averageRGB(int red[][],int green[][],int blue[][]) {
    	// TODO Instead of separate loops, extract both in same loop and then loop from height-4 to width-4 for top values
        int[][] R_avg = new int[3][30];
        int[][] G_avg = new int[3][30];
        int[][] B_avg = new int[3][30];
        int i,j,k;
        int sum_R = 0;
        int sum_G = 0;
        int sum_B = 0;
        double startTime = System.nanoTime();
        for (i = 0;i<3;i++) {
	        for (j = 0;j<30;j++) {
	        	for (k = 30*j;k<30*j;k++) {
	        		sum_R = red[i][k];
	        		sum_G = green[i][k];
	        		sum_B = blue[i][k];
	        	}
	        	R_avg[i][j] = sum/30;
	        }
        }
        double endTime = System.nanoTime();
        System.out.println("Time averaging:");
        System.out.printf("%f", (double)((endTime - startTime)/1000000));
        System.out.println("ms");
        }
    
}