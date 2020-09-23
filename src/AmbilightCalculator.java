import java.awt.Color;
import java.awt.image.BufferedImage;

public class AmbilightCalculator {
	private int screenHeight;
	private int screenWidth;
	private int neopixelsWidth;
	private int neopixelsHeight;
	private int nrOfNeopixels;
	private int neopixelAvgRectSideHeight;
	private static final int RECT_SIDE_WIDTH = 80;
	private static final int RECT_TOP_HEIGHT = 70;
	private int recTopWidth;
	private static final int PREAMBLE_LENGTH = 10;
	private int messageSize;
	private static final int BYTESPERLED = 3;
	public byte[] LED_DATA;
	public Color colorOfCurrentRectangle;
	public BufferedImage bufferedImage;

	public AmbilightCalculator(SettingsContainer settings) {
		this.neopixelsWidth = settings.nrOfNeopixelWidth;
		this.neopixelsHeight = settings.nrOfNeopixelHeight;
		this.screenHeight = settings.screenSize.height;
		this.screenWidth = settings.screenSize.width;
		this.nrOfNeopixels = 2*neopixelsHeight + neopixelsWidth;
		this.neopixelAvgRectSideHeight = screenHeight/neopixelsHeight;
		this.recTopWidth = screenWidth/neopixelsWidth;
		this.messageSize = nrOfNeopixels*3 + PREAMBLE_LENGTH;
		this.LED_DATA = new byte[messageSize];
		this.setMessagePreamble();
	}
	public void calculateColorOfAllLEDs() {
		this.calcColorOfSideRectangles();
        this.calcColorOfTopRectangles();
	}
	
	private void calcColorOfSideRectangles() {
        int k = PREAMBLE_LENGTH;
        int j = PREAMBLE_LENGTH + BYTESPERLED*neopixelsHeight + BYTESPERLED*neopixelsWidth;
        for (int i = 0; i < neopixelsHeight; i++) {
        	this.getAvgColorOfSideRectangle(0, screenHeight - (i + 1)*neopixelAvgRectSideHeight);
        	this.setLEDdata(k);
        	k = k + BYTESPERLED;
        	this.getAvgColorOfSideRectangle(screenWidth - RECT_SIDE_WIDTH, (i)*neopixelAvgRectSideHeight);
        	this.setLEDdata(j);
        	j = j + BYTESPERLED;
        }
	}
	
	private void calcColorOfTopRectangles() {
		int j = PREAMBLE_LENGTH + BYTESPERLED*neopixelsHeight;
	    for (int i = 0; i < neopixelsWidth; i++) {
	    	this.getAvgColorOfTopRectangle((i)*recTopWidth);
	    	this.setLEDdata(j);
	    	j = j + BYTESPERLED;
	    }

	}	
	
	
	/*
     * (x0,y0) is your upper left coordinate
     */
    private void getAvgColorOfSideRectangle(int x0, int y0) {
        int x1 = x0 + RECT_SIDE_WIDTH;
        int y1 = y0 + neopixelAvgRectSideHeight;
        long sumRed = 0, sumGreen = 0, sumBlue = 0;
        for (int x = x0; x < x1; x+=2) {
            for (int y = y0; y < y1; y+=2) {
                Color pixel = new Color(this.bufferedImage.getRGB(x, y));
                sumRed += pixel.getRed();
                sumGreen += pixel.getGreen();
                sumBlue += pixel.getBlue();
            }
        }
        int divisorForAveraging = RECT_SIDE_WIDTH * neopixelAvgRectSideHeight;
        this.colorOfCurrentRectangle = new Color((int)(sumRed / divisorForAveraging), (int)(sumGreen / divisorForAveraging), (int)(sumBlue / divisorForAveraging));
    }
      private void getAvgColorOfTopRectangle(int x0) {
    	  int x1 = x0 + recTopWidth;
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