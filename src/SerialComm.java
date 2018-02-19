import java.io.IOException;
import java.io.InputStream;
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
    private InputStream inStream; //for testing communication
	// Constructor
    SerialComm(){
	}
	public void ConnectPort(int baudRate,String ComPort)throws IOException {
        try {
        	// Obtain a CommPortIdentifier object for the port you want to open
            CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(ComPort);
 
            // Get the port's ownership
            serialPort = (SerialPort) portId.open("Demo application", 5000);
			serialPort.setSerialPortParams(
                    baudRate,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
 
            serialPort.setFlowControlMode(
                    SerialPort.FLOWCONTROL_NONE);
        } catch (UnsupportedCommOperationException | PortInUseException ex) {
            throw new IOException("Unsupported serial port parameter");
        } catch (NoSuchPortException e) {
            throw new IOException(e.getMessage());
        }
        
	}
}
