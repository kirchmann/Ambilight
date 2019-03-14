import java.io.IOException;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Logger;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.util.logging.Logger;

/**
 * 
 */

/**
 * @author Carl Christian
 *
 */
public class SerialComm {
	private static final Logger LOGGER = Logger.getLogger( SerialComm.class.getName() );
    private SerialPort serialPort;
    private OutputStream outStream;
    private String comPortName;
    SerialComm(){
	}
	public void ConnectPort(int baudRate)throws IOException {
        try {
            CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(this.comPortName);
            LOGGER.info("Trying to connect to comport: " + this.comPortName);
            this.serialPort = (SerialPort) portId.open("Ambilight", 5000);
            try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			serialPort.setSerialPortParams(115200,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
        } catch (UnsupportedCommOperationException e) {
            throw new IOException("Unsupported serial port parameter");
        } catch (NoSuchPortException e) {
			e.printStackTrace();
		} catch (PortInUseException e) {
			throw new IOException("Port in use.");
		}
        outStream = serialPort.getOutputStream();
        LOGGER.info("Connected to comport: " + this.comPortName);
	}
	
	public ArrayList<String> listAllAvailabelComPorts() {
		LOGGER.info("List all com ports.");
		Enumeration<CommPortIdentifier> pList = CommPortIdentifier.getPortIdentifiers();
		ArrayList<String> listOfComPorts = new ArrayList<String>(); 
		LOGGER.info("Found com ports: ");
		while (pList.hasMoreElements()) {
		      CommPortIdentifier cpi = (CommPortIdentifier) pList.nextElement();
		      listOfComPorts.add(cpi.getName());
		      LOGGER.info(cpi.getName() + ", ");
	    }
		return listOfComPorts;
	}
	
    private OutputStream getSerialOutputStream() {
        return outStream;
    }
    
    public void writeLedData(byte[] LED_data)
    {
    	try {
			getSerialOutputStream().write(LED_data);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void disconnect()
    {
    	LOGGER.info("Disconnect from comport: " + this.comPortName);
        try
        {
        	outStream.close();
        	serialPort.close();
        	        
        }
        catch (Exception e)
        {
        	LOGGER.info("Failed to disconnect, error: "+ e.toString());
        }
    }
	public String getcomPortName() {
		return comPortName;
	}
	public void setcomPortName(String comPortName) {
		this.comPortName = comPortName;
	}
    
}
