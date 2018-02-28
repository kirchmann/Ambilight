import java.awt.*;
import java.awt.image.*;
import java.io.IOException;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
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
        com.ConnectPort(9600, "COM3");
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
            System.out.println("Width: "+width+" height: "+height);
            System.out.println("blue: "+ blue[0][100]);

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
        double endTime = System.nanoTime();
        System.out.printf("%f", (double)((endTime - startTime)/1000000));
        System.out.println("ms");
        k=0;
        // Extract every fourth pixel value on top side of screen
        for (x = 0;x < width-4;x += 4) {
        	tColor = image.getRGB(x,0);
        	blue[2][k] = tColor & 0xff;
     		green[2][k] = (tColor & 0xff00) >> 8;
     		red[2][k] = (tColor & 0xff0000) >> 16;
     		k++;
     		
        }
    }
    
}