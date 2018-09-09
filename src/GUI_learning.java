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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JButton;

public class GUI_learning {

	private JFrame frame;
	private JTextField updatesPerSecondTextField;
	private boolean isRunning;
	private boolean isConnectedToComPort;
	private int millisecondsPerScreenshot;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
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
		frame.setBounds(100, 100, 339, 255);
		this.isRunning = false;
		this.isConnectedToComPort = false;
        final int initialDelay = 0;
        millisecondsPerScreenshot = 100000;
        
        Ambilight ambilight = new Ambilight();
        ambilight.createInstanceOfComPort();
        ambilight.initialize();
        Runnable ambilightRunnable = new Runnable() {
            public void run() {
            	if (isRunning && isConnectedToComPort) {
	              long startTime = System.nanoTime();
	              ambilight.takeScreenshot();
	              ambilight.calculateColorOfAllLEDs();
	              ambilight.flushBufferedImage();
	              ambilight.sendDataToCompPort();
	              long endTime = System.nanoTime();
	              long duration = (endTime - startTime)/1000000;  //divide by 1000000 to get milliseconds.
	              System.out.println(duration);
            	}
            }
          };

		JCheckBox OnOrOffButton = new JCheckBox("On/off");
		OnOrOffButton.setHorizontalAlignment(SwingConstants.CENTER);
		OnOrOffButton.setBounds(118, 165, 69, 36);
		OnOrOffButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent event) {
		        JCheckBox cb = (JCheckBox) event.getSource();
		        if (cb.isSelected()) {
		            // do something if check box is selected
		        	isRunning = true;
		        	System.out.println("Starting ambilight.");
		        } else {
		            // check box is unselected, do something else
		        	isRunning = false;
		        	System.out.println("Pausing ambilight.");
		        }
		    }
		});
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		    	ambilight.disconnectComPort();
		    	System.exit(0);
		    }
		});
		frame.getContentPane().setLayout(null);
		frame.getContentPane().add(OnOrOffButton);
		
		updatesPerSecondTextField = new JTextField("Refresh rate");
		updatesPerSecondTextField.addActionListener(new ActionListener() {
		    @Override
			public void actionPerformed(ActionEvent arg0) {
				String inputText = updatesPerSecondTextField.getText();
				millisecondsPerScreenshot = Integer.parseInt(inputText);
				System.out.println(millisecondsPerScreenshot);
		        ScheduledExecutorService ambilightThread = Executors.newSingleThreadScheduledExecutor();
		        ambilightThread.scheduleAtFixedRate(ambilightRunnable, initialDelay, millisecondsPerScreenshot, TimeUnit.MILLISECONDS);
			}
		});
		updatesPerSecondTextField.setBounds(68, 45, 156, 27);
		updatesPerSecondTextField.setHorizontalAlignment(SwingConstants.LEFT);
		frame.getContentPane().add(updatesPerSecondTextField);
		updatesPerSecondTextField.setColumns(3);
		
		JComboBox<String> comboBoxComPorts = new JComboBox<String>();
		comboBoxComPorts.setBounds(68, 96, 156, 27);
		frame.getContentPane().add(comboBoxComPorts);
		
		JButton connectInitialize = new JButton("Connect");
		connectInitialize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (isConnectedToComPort) {
					ambilight.disconnectComPort();
				}
				ambilight.connectComPort();
				isConnectedToComPort = true;
			}
		});
		connectInitialize.setBounds(108, 135, 89, 23);
		frame.getContentPane().add(connectInitialize);
		ArrayList<String> comPortList= ambilight.listAllAvailabelComPorts(); 
		comPortList.forEach((comport->comboBoxComPorts.addItem(comport)));
		
		


	}
}
