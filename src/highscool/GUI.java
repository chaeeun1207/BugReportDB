package highscool;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;

public class GUI extends JFrame implements ActionListener{
	
	public static void main(String args[]) {
		// JFrame: Window Frame
	    JFrame oFrame = new JFrame("Window Title");
	    // Set Window(oFrame) Size 300pixel(weight) x 400pixel (height)
	    oFrame.setSize(300, 400);
	    // Set Layout: "NULL" is better easy to draw some components
	    oFrame.setLayout(null);
	    
	    // 1. Label: Name Label
	    JLabel oLabel = new JLabel("Label", JLabel.CENTER );
	    // Set Position & Size (50 = x, 0 = y, 200 = weight, 10 = height)
	    oLabel.setBounds(50, 0, 200, 10);
	    // Add oLabel in to oFrame	    
	    oFrame.add(oLabel);
	    
	    // 2. TextField : Small Text Box(one line)
	    // 10 means length. oTextField should have text less than 10 characters.
	    JTextField oTextField = new JTextField(10);
	    //Set Position & Size (100 = x, 10 = y, 100 = weight, 20 = height)
	    oTextField.setBounds(100, 10, 100, 20);
	    // Set Text in oTextField
	    oTextField.setText("TEXT EXAMPLE");
	    // Add oTextField in to oFrame	    
	    oFrame.add(oTextField);
	    
	    // 3. TextArea : Big Text Box (two more line)
	    // Can't have Scroll function. So texts in this area can't show everything.
	    JTextArea oTextArea = new JTextArea();
	    //Set Position & Size (50 = x, 50 = y, 180 = weight, 50 = height)
	    oTextArea.setBounds(50, 50, 180, 50);
	    // Set Text in oTextField : \n means "enter"
	    oTextArea.setText("FIRST LINE: TEXT AREA EXAMPLE \n" 
	    		+ "second LINE: HIHIHI");	    		
	    // Add oTextArea in to oFrame	    
	    oFrame.add(oTextArea);
	    
	    // 4. TextArea with Scroll : Big Text Box (two more line) having Scoroll
	    JTextArea oTextArea2 = new JTextArea(); 
	    // Set Text in oTextField : \n means "enter"
	    oTextArea2.setText("FIRST LINE: TEXT AREA EXAMPLE \n" 
	    		+ "second LINE: HELLO \n"
	    		+ "third line : HIHIHI \n");	    // Build Scroll Panel
	    // If we want to make Scroll Function in Text Area,
	    // We should build ScrollPane and add "Text Area" in this panel
	    JScrollPane oScrollPane = new JScrollPane();
	    // Set Position & Size (50 = x, 100 = y, 100 = weight, 80 = height)
	    oScrollPane.setBounds(50, 100, 100, 80);
	    // View oTextArea2 in to oScrollPane
	    oScrollPane.setViewportView(oTextArea2);
	    // Add oScrollPane in to oFrame	    
	    oFrame.add(oScrollPane);
	    
	    // 5. Table without Scroll
	    // If you don't use Some Panel(such as Scroll Panel)
	    // Can't see the Title of each column
	    JTable table = new JTable();
	    table.setBounds(50,200,150,20);
	    table.setModel(new DefaultTableModel(
	    		new Object[][] {{"A1","B1"},{"A2","B2"}},
	    		new String[] {"A","B"}));
	    oFrame.add(table);
	    
	    // 6. Table with Scroll
	    JTable table2 = new JTable();
	    table2.setModel(new DefaultTableModel(
	    		new Object[][] {{"AA1","BB1"},{"AA2","BB2"}},
	    		new String[] {"AA","BB"}));
	    JScrollPane oScrollPane2 = new JScrollPane();
	    oScrollPane2.setBounds(50,250,150,50);
	    oScrollPane2.setViewportView(table2);	    
	    oFrame.add(oScrollPane2);
	    
	    // 7. Button for File Chooser
	    // If we choose file by this button,
	    // some text appear in the Text Box
	    JButton oButton = new JButton("btn1");
	    JTextField oTextField2 = new JTextField(300);
	    oButton.setBounds(10,310,100,20);
	    oButton.setText("Open File");
	    // Add the Action Listner for Getting the Event
	    oButton.addActionListener(new ActionListener(){
	    	// If Action in this button, we show the File Chooser Window
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.out.println("CLICK BUTTON");
				JFileChooser chooser = new JFileChooser(".");
				// File Type Specification
		        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text File", "txt");
		        chooser.setFileFilter(filter);		        
		        int result = chooser.showOpenDialog(new JFileChooser());
		        // If Open Button in File Chooser is Clicked
		        if (result == JFileChooser.APPROVE_OPTION) {		        	 
		            String fileName = chooser.getSelectedFile().getAbsolutePath();
		            //TextField2 Setting Text "FIle Name"
		            oTextField2.setText(fileName);
		        }
			}
	    });
	    oFrame.add(oButton);
	    oTextField2.setBounds(10,330,250,20);
	    oFrame.add(oTextField2);
	    
	    // Set Visibility: Default: No Visible
	    oFrame.setVisible(true);
	    // If Click "X" for closing, window will be closed
	    oFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	  }

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
