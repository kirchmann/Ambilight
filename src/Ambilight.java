import java.awt.*;
import java.awt.image.*;
//import processing.serial.*;
class ambilight {
    public static void main(String[] args) {
        System.out.println("Hello World!"); // Display the string.
        Robot robot;
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
            int[][] red_s	= new int[2][height/4];
            int[][] green_s	= new int[2][height/4];
            int[][] blue_s	= new int[2][height/4];
            
            int[] red_t		= new int[width/4];
            int[] green_t	= new int[width/4];
            int[] blue_t	= new int[width/4];
            get_RGB_arrays(bi,red_s,blue_s,green_s,red_t,blue_t,green_t);
            bi.flush();
            System.out.println("Width: "+width+" height: "+height);
            System.out.println("blue: "+ blue_s[0][100]);

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
    private static void get_RGB_arrays(BufferedImage image,int red_s[][],int green_s[][],int blue_s[][],int red_t[],int green_t[],int blue_t[]) {
    	final int width  = image.getWidth();
        final int height = image.getHeight();
        int tColor;
        int k = 0;
        int x,y;
        double startTime = System.nanoTime();
        for (y=0;y<height-4;y+=4) {
        	tColor = image.getRGB(0,y);
        	blue_s[0][k] = tColor & 0xff;
     		green_s[0][k] = (tColor & 0xff00) >> 8;
     		red_s[0][k] = (tColor & 0xff0000) >> 16;
     		//System.out.println("blue: "+ blue_s[0][k]);
     		tColor = image.getRGB(width-1,y);
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
        	tColor = image.getRGB(x,0);
        	blue_t[k] = tColor & 0xff;
     		green_t[k] = (tColor & 0xff00) >> 8;
     		red_t[k] = (tColor & 0xff0000) >> 16;
     		//System.out.println("blue: "+ blue_t[k]);
     		k++;
     		
        }
    }
    
}