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
        for (i=0;i<10;i++){
        	System.out.println(i); // Display the string.
        }
        Robot robot;
        try{
        	robot = new Robot(); 
        	Rectangle area = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()); 
     		BufferedImage bi = robot.createScreenCapture(area);	
     		// loop over the circumference of the image and do an averaging that is depndent on the number of neopixels
   		    // retrieve image
     		File outputfile = new File("saved.png");
     		ImageIO.write(bi, "png", outputfile);

     		System.out.println("screensize: "+ Toolkit.getDefaultToolkit().getScreenSize()); // Display the string.
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
    }
}