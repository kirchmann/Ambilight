import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JButton;

import java.util.logging.Logger;

public class Gui {
	private static final Logger LOGGER = Logger.getLogger( Gui.class.getName() );
	private JFrame frame;
	private JTextField updatesPerSecondTextField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		LOGGER.info("Entered Main.");
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui window = new Gui();
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
	public Gui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 315, 255);
        AmbilightRunner ambilightRunner = new AmbilightRunner();
        ambilightRunner.startAmbilight();

		JCheckBox OnOrOffButton = new JCheckBox("On/off");
		OnOrOffButton.setHorizontalAlignment(SwingConstants.CENTER);
		OnOrOffButton.setBounds(114, 173, 69, 36);
		OnOrOffButton.setSelected(ambilightRunner.getIsRunning());
		OnOrOffButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent event) {
		        JCheckBox cb = (JCheckBox) event.getSource();
		        if (cb.isSelected()) {
		        	ambilightRunner.setIsRunning(true);
		        	LOGGER.info("Starting Ambilight by clicking checkbox.");
		        } else {
		        	ambilightRunner.setIsRunning(false);
		        	LOGGER.info("Pausing Ambilight by un-clicking checkbox.");
		        }
		    }
		});
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		    	LOGGER.info("Window closed.");
		    	if (ambilightRunner.getIsConnectedToComPort()) {
		    		ambilightRunner.disconnectComPort();
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
		    	int millisecondsPerScreenshot = Integer.parseInt(inputText);
				LOGGER.info("User set refresh rate: " + inputText);
				ambilightRunner.setMillisecondsPerScreenshot(millisecondsPerScreenshot);
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
				if (ambilightRunner.getIsConnectedToComPort()) {
					ambilightRunner.disconnectComPort();
				}
				//ambilight.connectComPort(comboBoxComPorts.getSelectedItem().toString());
				ambilightRunner.connectComPort(comboBoxComPorts.getSelectedItem().toString());
				ambilightRunner.setIsConnectedToComPort(true);
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
		//ArrayList<String> comPortList= ambilight.listAllAvailabelComPorts();
		ArrayList<String> comPortList= ambilightRunner.listAllAvailabelComPorts();
		comPortList.forEach((comport->comboBoxComPorts.addItem(comport)));
		
		


	}
}
