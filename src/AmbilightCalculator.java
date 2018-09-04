import java.awt.Color;
import java.awt.image.BufferedImage;

public class AmbilightCalculator {
	private static final int SCREEN_HEIGHT = 1080;
	private static final int SCREEN_WIDTH = 1920;
	private static final int NEOPIXELS_WIDTH = 28;
	private static final int NEOPIXELS_HEIGHT = 16;
	private static final int NEOPIXELS_SUM = 2*NEOPIXELS_HEIGHT + NEOPIXELS_WIDTH;
	private static final int NEOPIXEL_AVG_RECT_SIDE_HEIGHT = SCREEN_HEIGHT/NEOPIXELS_HEIGHT;
	private static final int RECT_SIDE_WIDTH = 80;
	private static final int RECT_TOP_HEIGHT = 70;
	private static final int RECT_TOP_WIDTH = SCREEN_WIDTH/NEOPIXELS_WIDTH;
	private static final int PREAMBLE_LENGTH = 10;
	private static final int messageSize = NEOPIXELS_SUM*3 + PREAMBLE_LENGTH;
	private static final int BYTESPERLED = 3;
	public byte[] LED_DATA;
	public Color colorOfCurrentRectangle;
	public BufferedImage bufferedImage;

	public AmbilightCalculator() {
		this.LED_DATA = new byte[messageSize];
	}
	public void calculateColorOfAllLEDs() {
		this.calcColorOfSideRectangles();
        this.calcColorOfTopRectangles();
	}
	
	private void calcColorOfSideRectangles() {
        int k = PREAMBLE_LENGTH;
        int j = PREAMBLE_LENGTH + BYTESPERLED*NEOPIXELS_HEIGHT + BYTESPERLED*NEOPIXELS_WIDTH;
        for (int i = 0; i < NEOPIXELS_HEIGHT; i++) {
        	this.getAvgColorOfSideRectangle(0, SCREEN_HEIGHT - (i + 1)*NEOPIXEL_AVG_RECT_SIDE_HEIGHT);
        	this.setLEDdata(k);
        	k = k + BYTESPERLED;
        	this.getAvgColorOfSideRectangle(SCREEN_WIDTH - RECT_SIDE_WIDTH, (i)*NEOPIXEL_AVG_RECT_SIDE_HEIGHT);
        	this.setLEDdata(j);
        	j = j + BYTESPERLED;
        }
	}
	
	private void calcColorOfTopRectangles() {
		int j = PREAMBLE_LENGTH + BYTESPERLED*NEOPIXELS_HEIGHT;
	    for (int i = 0; i < NEOPIXELS_WIDTH; i++) {
	    	this.getAvgColorOfTopRectangle((i)*RECT_TOP_WIDTH);
	    	this.setLEDdata(j);
	    	j = j + BYTESPERLED;
	    }

	}	
	
	
	/*
     * (x0,y0) is your upper left coordinate
     */
    private void getAvgColorOfSideRectangle(int x0, int y0) {
        int x1 = x0 + RECT_SIDE_WIDTH;
        int y1 = y0 + NEOPIXEL_AVG_RECT_SIDE_HEIGHT;
        long sumRed = 0, sumGreen = 0, sumBlue = 0;
        for (int x = x0; x < x1; x+=2) {
            for (int y = y0; y < y1; y+=2) {
                Color pixel = new Color(this.bufferedImage.getRGB(x, y));
                sumRed += pixel.getRed();
                sumGreen += pixel.getGreen();
                sumBlue += pixel.getBlue();
            }
        }
        int divisorForAveraging = RECT_SIDE_WIDTH * NEOPIXEL_AVG_RECT_SIDE_HEIGHT;
        this.colorOfCurrentRectangle = new Color((int)(sumRed / divisorForAveraging), (int)(sumGreen / divisorForAveraging), (int)(sumBlue / divisorForAveraging));
    }
      private void getAvgColorOfTopRectangle(int x0) {
    	  int x1 = x0 + RECT_TOP_WIDTH;
          int y1 = RECT_TOP_HEIGHT;
          long sumRed = 0, sumGreen = 0, sumBlue = 0;
          for (int x = x0; x < x1; x+=2) {
              for (int y = 0; y < y1; y+=2) {
                  Color pixel = new Color(this.bufferedImage.getRGB(x, y));
                  sumRed += pixel.getRed();
                  sumGreen += pixel.getGreen();
                  sumBlue += pixel.getBlue();
              }
          }
          int divisorForAveraging = RECT_TOP_HEIGHT * RECT_TOP_HEIGHT;
          this.colorOfCurrentRectangle = new Color((int)(sumRed / divisorForAveraging), (int)(sumGreen / divisorForAveraging), (int)(sumBlue / divisorForAveraging));
	}
      
  	private void setLEDdata(int j) {
    	LED_DATA[j] = (byte) this.colorOfCurrentRectangle.getBlue();
    	LED_DATA[j + 1] = (byte) this.colorOfCurrentRectangle.getGreen();
    	LED_DATA[j + 2] = (byte) this.colorOfCurrentRectangle.getRed();
	}
  	
    public void setMessagePreamble() {
        this.LED_DATA[0] = (byte)0x00;
        this.LED_DATA[1] = (byte)0x01;
        this.LED_DATA[2] = (byte)0x02;
        this.LED_DATA[3] = (byte)0x03;
        this.LED_DATA[4] = (byte)0x04;
        this.LED_DATA[5] = (byte)0x05;
        this.LED_DATA[6] = (byte)0x06;
        this.LED_DATA[7] = (byte)0x07;
        this.LED_DATA[8] = (byte)0x08;
        this.LED_DATA[9] = (byte)0x09;	
    }
}