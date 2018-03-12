import java.io.IOException;
import java.io.OutputStream;
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
public class SerialComm {
    private SerialPort serialPort;
    private OutputStream outStream;
	// Constructor
    SerialComm(){
	}
	public void ConnectPort(int baudRate,String ComPort)throws IOException {
        try {
        	// Obtain a CommPortIdentifier object for the port you want to open
            CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(ComPort);
            System.out.println("Found port: "+portId.getName());
            // Get the port's ownership
            serialPort = (SerialPort) portId.open("Demo application", 5000);
            try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			serialPort.setSerialPortParams(115200,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);

        } catch (UnsupportedCommOperationException e) {
            throw new IOException("Unsupported serial port parameter");
        } catch (NoSuchPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PortInUseException e) {
			throw new IOException("Port in use.");
		}
        outStream = serialPort.getOutputStream();
        //String serialMessage = "a\n";
        
        
        //outStream.close();
        //serialPort.close();
	}

	
    /**
     * Get the serial port output stream
     * @return The serial port output stream
     */
    public OutputStream getSerialOutputStream() {
        return outStream;
    }
    
    /**
     * Write to the serial port output stream
     */
    public void sendData(byte[] data)
    {
        try
        {
        	outStream.write(data);
        	outStream.flush();
            
          
        }
        catch (Exception e)
        {
            String logText = "Failed to write data. (" + e.toString() + ")";
            System.out.println(logText);
        }
    }
    
    /**
     * Write to the serial port output stream
     */
    public void disconnect()
    {
        try
        {
        	outStream.close();
        	serialPort.close();
        	        
        }
        catch (Exception e)
        {
            String logText = "Failed disconnect. (" + e.toString() + ")";
            System.out.println(logText);
        }
    }
    
}
