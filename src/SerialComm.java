import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;

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
    private String comPortName;
    SerialComm(){
	}
	public void ConnectPort(int baudRate)throws IOException {
        try {
            CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(this.comPortName);
            System.out.println("Found port: " + this.comPortName);
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
	}
	
	public ArrayList<String> listAllAvailabelComPorts() {
		Enumeration<CommPortIdentifier> pList = CommPortIdentifier.getPortIdentifiers();
		ArrayList<String> listOfComPorts = new ArrayList<String>(); 
		
		while (pList.hasMoreElements()) {
		      CommPortIdentifier cpi = (CommPortIdentifier) pList.nextElement();
		      listOfComPorts.add(cpi.getName());
		      System.out.print("Port " + cpi.getName() + " ");
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
	public String getcomPortName() {
		return comPortName;
	}
	public void setcomPortName(String comPortName) {
		this.comPortName = comPortName;
	}
    
}
