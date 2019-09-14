import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JButton;

import java.util.logging.Logger;

public class GUI_learning {
	private static final Logger LOGGER = Logger.getLogger( GUI_learning.class.getName() );
	private JFrame frame;
	private JTextField updatesPerSecondTextField;
	private boolean isRunning;
	private boolean isConnectedToComPort;
	private int millisecondsPerScreenshot;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		LOGGER.info("Entered Main.");
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI_learning window = new GUI_learning();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI_learning() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 315, 255);
		this.isRunning = false;
		this.isConnectedToComPort = false;
        final int initialDelay = 0;
        millisecondsPerScreenshot = 65;
        
        Ambilight ambilight = new Ambilight();
        ambilight.createInstanceOfComPort();
        ambilight.initialize();
        Runnable ambilightRunnable = new Runnable() {
            public void run() {
            	if (isRunning && isConnectedToComPort) {
	              ambilight.takeScreenshot();
	              ambilight.calculateColorOfAllLEDs();
	              ambilight.flushBufferedImage();
	              ambilight.sendDataToCompPort();
            	}
            }
          };

		JCheckBox OnOrOffButton = new JCheckBox("On/off");
		OnOrOffButton.setHorizontalAlignment(SwingConstants.CENTER);
		OnOrOffButton.setBounds(114, 173, 69, 36);
		OnOrOffButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent event) {
		        JCheckBox cb = (JCheckBox) event.getSource();
		        if (cb.isSelected()) {
		        	isRunning = true;
		        	LOGGER.info("Starting Ambilight by clicking checkbox.");
		        } else {
		        	isRunning = false;
		        	LOGGER.info("Pausing Ambilight by un-clicking checkbox.");
		        }
		    }
		});
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		    	LOGGER.info("Window closed.");
		    	if (isConnectedToComPort) {
		    		ambilight.disconnectComPort();
		    	}
	    		System.exit(0);
		    }
		});
		frame.getContentPane().setLayout(null);
		frame.getContentPane().add(OnOrOffButton);
		updatesPerSecondTextField = new JTextField("65");
		updatesPerSecondTextField.addActionListener(new ActionListener() {
		    @Override
			public void actionPerformed(ActionEvent arg0) {
		    	String inputText = updatesPerSecondTextField.getText();
		    	millisecondsPerScreenshot = Integer.parseInt(inputText);
				LOGGER.info("User set refresh rate: " + inputText);
		        ScheduledExecutorService ambilightThread = Executors.newSingleThreadScheduledExecutor();
		        ambilightThread.scheduleAtFixedRate(ambilightRunnable, initialDelay, millisecondsPerScreenshot, TimeUnit.MILLISECONDS);
			}
		});
		updatesPerSecondTextField.setBounds(114, 45, 111, 27);
		updatesPerSecondTextField.setHorizontalAlignment(SwingConstants.LEFT);
		frame.getContentPane().add(updatesPerSecondTextField);
		updatesPerSecondTextField.setColumns(3);
		
		JComboBox<String> comboBoxComPorts = new JComboBox<String>();
		comboBoxComPorts.setBounds(114, 93, 111, 27);
		frame.getContentPane().add(comboBoxComPorts);
		
		JButton connectInitialize = new JButton("Connect");
		connectInitialize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (isConnectedToComPort) {
					ambilight.disconnectComPort();
				}
				ambilight.connectComPort(comboBoxComPorts.getSelectedItem().toString());
				isConnectedToComPort = true;
			}
		});
		connectInitialize.setBounds(114, 143, 89, 23);
		frame.getContentPane().add(connectInitialize);
		
		JLabel lblRefreshRate = new JLabel("Refresh rate");
		lblRefreshRate.setBounds(10, 51, 94, 14);
		frame.getContentPane().add(lblRefreshRate);
		
		JLabel lblComPort = new JLabel("Com port");
		lblComPort.setBounds(10, 99, 94, 14);
		frame.getContentPane().add(lblComPort);
		ArrayList<String> comPortList= ambilight.listAllAvailabelComPorts(); 
		comPortList.forEach((comport->comboBoxComPorts.addItem(comport)));
		
		


	}
}
