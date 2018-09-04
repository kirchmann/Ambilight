import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
public class GUI_learning {

	private JFrame frame;
	private JTextField updatesPerSecondTextField;
	private JLabel updatesPerSecondTextField_label;
	private boolean isRunning;

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
		frame.setBounds(100, 100, 450, 300);
		this.isRunning = false;
        Ambilight ambilight = new Ambilight();
        ambilight.initialize();
        Runnable ambilightRunnable = new Runnable() {
            public void run() {
            	if (isRunning) {
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
        ScheduledExecutorService ambilightThread = Executors.newSingleThreadScheduledExecutor();
        ambilightThread.scheduleAtFixedRate(ambilightRunnable, 0, 100, TimeUnit.MILLISECONDS);
		JCheckBox OnOrOffButton = new JCheckBox("On/off");
		OnOrOffButton.setBounds(20, 57, 69, 72);
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
		
		updatesPerSecondTextField = new JTextField();
		updatesPerSecondTextField.setBounds(72, 22, 50, 27);
		updatesPerSecondTextField.setText("Input");
		updatesPerSecondTextField.setHorizontalAlignment(SwingConstants.LEFT);
		frame.getContentPane().add(updatesPerSecondTextField);
		updatesPerSecondTextField.setColumns(3);
		
		updatesPerSecondTextField_label = new JLabel("Updates / s:");
		updatesPerSecondTextField_label.setBounds(10, 17, 69, 36);
		updatesPerSecondTextField_label.setLabelFor(updatesPerSecondTextField);
		frame.getContentPane().add(updatesPerSecondTextField_label);
		
		
		
		
	}

}
