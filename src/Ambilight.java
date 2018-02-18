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
     		//final byte[] pixels = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
            final int width = bi.getWidth();
            final int height = bi.getHeight();
            int[] red_left = new int[height/4];
            int[] red_right= new int[height/4];
            int[] red_top= new int[width/4];
            int[] green_left= new int[height/4];
            int[] green_right= new int[height/4];
            int[] green_top= new int[width/4];
            int[] blue_left=new int[height/4];
            int[] blue_right=new int[height/4];
            int[] blue_top=new int[width/4];
            int color_l;
            int color_r;
            
            System.out.println("Width: "+width+" height: "+height);
            // Extract every fourth pixel value on left and right side of screen 
            for (y=0;y<height-5;y+=4) {
            	color_l = bi.getRGB(0,y);
            	blue_left[k] = color_l & 0xff;
         		green_left[k] = (color_l & 0xff00) >> 8;
         		red_left[k] = (color_l & 0xff0000) >> 16;
         		color_r = bi.getRGB(width-1,y);
            	blue_right[k] = color_r & 0xff;
         		green_right[k] = (color_r & 0xff00) >> 8;
         		red_right[k] = (color_r & 0xff0000) >> 16;
         		k++;
            }
            System.out.println("y: "+y+" k: "+k);
            k=0;
            // Extract every fourth pixel value on top side of screen
            for (x=0;x<width-4;x+=4) {
            	color_l = bi.getRGB(x,0);
            	blue_top[k] = color_l & 0xff;
         		green_top[k] = (color_l & 0xff00) >> 8;
         		red_top[k] = (color_l & 0xff0000) >> 16;
         		k++;
            }
            System.out.println("x: "+x+" k: "+k);
     		
     		// loop over the circumference of the image and do an averaging that is dependent on the number of neopixels
     		//https://www.tutorialspoint.com/java_dip/java_buffered_image.htm
     		// neopixels width: 30
            // neopixels height: 16
            // pixel width: 1920
            // pixel height: 1080
            
            File outputfile = new File("saved.png");
     		ImageIO.write(bi, "png", outputfile);

     		System.out.println("screensize: "+ Toolkit.getDefaultToolkit().getScreenSize()); // Display the string.
     		System.out.println(bi.getAlphaRaster());
     		
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
    }
}