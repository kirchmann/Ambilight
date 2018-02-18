/**
 * The HelloWorldApp class implements an application that
 * simply prints "Hello World!" to standard output.
 */
import java.awt.*;
import java.io.File;
import java.awt.image.*;
import javax.imageio.ImageIO;
//import processing.serial.*;
class ambilight {
    public static void main(String[] args) {
        System.out.println("Hello World!"); // Display the string.
        int i;
        int x,y;
        int k = 0;

        for (i=0;i<10;i++){
        	System.out.println(i); // Display the string.
        }
        Robot robot;
        try{
        	robot = new Robot();
        	Rectangle area = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()); 
     		BufferedImage bi = robot.createScreenCapture(area);
     		get_RGB_array(bi);
     		//final byte[] pixels = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
            final int width  = bi.getWidth();
            final int height = bi.getHeight();
            // This structure will lead to space not getting used in first and second row since there are fewer pixels
            int[][] red_s	= new int[2][height/4];
            int[][] green_s	= new int[2][height/4];
            int[][] blue_s	= new int[2][height/4];
            
            int[] red_t		= new int[width/4];
            int[] green_t	= new int[width/4];
            int[] blue_t	= new int[width/4];
            int tColor;
            
            System.out.println("Width: "+width+" height: "+height);

            // Extract every fourth pixel value on left and right side of screen 
            double startTime = System.nanoTime();
            for (y=0;y<height-4;y+=4) {
            	tColor = bi.getRGB(0,y);
            	blue_s[0][k] = tColor & 0xff;
         		green_s[0][k] = (tColor & 0xff00) >> 8;
         		red_s[0][k] = (tColor & 0xff0000) >> 16;
         		tColor = bi.getRGB(width-1,y);
            	blue_s[1][k] = tColor & 0xff;
         		green_s[1][k] = (tColor & 0xff00) >> 8;
         		red_s[1][k] = (tColor & 0xff0000) >> 16;
         		k++;
            }
            double endTime = System.nanoTime();
            System.out.printf("%f", (double)((endTime - startTime)/1000000));
            System.out.println("ms");
            k=0;
            // Extract every fourth pixel value on top side of screen
            for (x = 0;x < width-4;x += 4) {
            	tColor = bi.getRGB(x,0);
            	blue_t[k] = tColor & 0xff;
         		green_t[k] = (tColor & 0xff00) >> 8;
         		red_t[k] = (tColor & 0xff0000) >> 16;
         		k++;
            }
            System.out.println("x: "+ x +" k: "+ k );
     		
     		// loop over the circumference of the image and do an averaging that is dependent on the number of neopixels
     		//https://www.tutorialspoint.com/java_dip/java_buffered_image.htm
     		// neopixels width: 30
            // neopixels height: 16
            // pixel width: 1920
            // pixel height: 1080
            // 1920/4/30 = 16
            // 1080/4/16 = 9
            
            File outputfile = new File("saved.png");
     		ImageIO.write(bi, "png", outputfile);

     		System.out.println("screensize: "+ Toolkit.getDefaultToolkit().getScreenSize()); // Display the string.
     		
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
    }
    private static void get_RGB_array(BufferedImage image) {
    	final int width  = image.getWidth();
        final int height = image.getHeight();
    }
    
}