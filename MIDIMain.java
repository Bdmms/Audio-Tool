import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.*;
import java.awt.event.*;

public class MIDIMain implements ActionListener
{
	/**
	 * Note 1: All comments are displayed above the targeted line of code 
	 */
	
	//The window the components are displayed on
	private JFrame window = new JFrame("MIDI EDITOR TOOL");
	//The drawing component used to display most graphics
	private GUI visual = new GUI();
	//Mouse Listener used to register mouse movement and inputs
	private CursorListener mouse = new CursorListener();
	//Key Listener used to register key inputs
	//private KeyBoardListener key = new KeyBoardListener();
	
	//*Placeholder for buttons
	private JButton button = new JButton("Track 1");
	//The scroll bar used in the track editor
	private JScrollBar scroll = new JScrollBar();
	//Tool bar buttons
	private JButton[] tools = new JButton[18];
	
	//Determines which menu the program displays
	private static byte mode = 0;
	//The notes of the song (note this is a temporary location)
	private static Notes[] notes = new Notes[10];
	//The x and y values used in the note editor (x, y)
	private static short[] coordinates = {0,0};
	//The values that are used to space the grid layout (x, y)
	private static short[] scale = {20, 20};
	//The value assigned to the scroll bar
	private static short scrollY = 0;
	
	public static void main(String[] args) {
		//Removes forced static methods and variables
		new MIDIMain();
	}

	//initialization() initializes basic graphical components
	public void initialization(){
		for(byte i = 0; i < 10; i++)
		{
			notes[i] = new Notes();
		}
		
		//button is initialized
		button.setBackground(Color.WHITE);
		button.setFont(new Font("A", Font.BOLD, 14));
		button.addActionListener(this);
		button.setBounds(620, 0, 100, 20); 
		
		//scroll bar is initialized
		scroll.setBounds(680, 50, 20, 320);
		scroll.setValues(0, 100, 0, 400);
		scroll.setUnitIncrement(10);
		
		//drawing component is initialized and contains buttons
		visual.setPreferredSize(new Dimension(720,480));
		visual.setLayout(null);
		visual.add(createToolBar());
		visual.add(button);
		visual.add(scroll);
		
		//JFrame is initialized and contains all other components and listeners
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(720, 480);
        window.setBackground(Color.WHITE);
        window.setResizable(true);
        window.setLocation(0, 0);
        window.add(visual);
        //Special method for setting the menu bar
        window.setJMenuBar(createMenuBar());
        window.pack();
        //window.addKeyListener(key);
        window.addMouseListener(mouse);
        window.addMouseMotionListener(mouse);
        window.addMouseWheelListener(mouse);
        window.setVisible(true);
	}
	
	//createToolBar() initializes the tool bar and returns it
	public ToolBar createToolBar()
	{
		//The component containing the function buttons
		ToolBar toolBar = new ToolBar();
		
		toolBar.setSize(720,41);
		toolBar.setLayout(null);
		toolBar.setBackground(Color.LIGHT_GRAY);
		
		for(byte i = 0; i < tools.length; i++)
		{
			tools[i] = new JButton(new ImageIcon("Images/ButtonIcon.png"));
			tools[i].setSelectedIcon(new ImageIcon("Images/ButtonSelectedIcon.png"));
			tools[i].setBounds(5+40*i,5,30,30);
			tools[i].setBackground(Color.LIGHT_GRAY);
			tools[i].setBorderPainted(false);
			tools[i].addActionListener(this);
			toolBar.add(tools[i]);
		}
		
		return toolBar;
	}
	
	//createMenuBar() initializes a JMenuBar and returns it
	public JMenuBar createMenuBar(){
		//Menu bar component is created
		JMenuBar menuBar = new JMenuBar();
		//Each branch of the menu bar is created
		JMenu[] menu = {new JMenu("FILE"), new JMenu("EDIT"), new JMenu("VIEW"), new JMenu("ORGANIZE"), new JMenu("SOUNDBANK"), new JMenu("HELP")}; 
		//The menu inside each branch is created
		JMenuItem[] file = {new JMenuItem("New"), new JMenuItem("Open"), new JMenuItem("Save"), new JMenuItem("Save As"), new JMenuItem("Delete File"), new JMenuItem("Quit")};
		JMenuItem[] edit = {new JMenuItem("Undo"), new JMenuItem("Redo"), new JMenuItem("Copy"), new JMenuItem("Cut"), new JMenuItem("Paste"), new JMenuItem("Delete")};
		JMenuItem[] view = {new JMenuItem("Precision"), new JMenuItem("Zoom")};
		JMenuItem[] organize = {new JMenuItem("Track Order"), new JMenuItem("Track Colour")};
		JMenuItem[] soundbank = {new JMenuItem("Load Soundbank"), new JMenuItem("Set to Default")};
		JMenuItem[] help = {new JMenuItem("Terms"), new JMenuItem("Tutorials")};
		
		//Initialization of menu items are grouped together
		for(byte i = 0; i < 6; i++)
		{
			menu[i].addActionListener(this);
			file[i].addActionListener(this);
			edit[i].addActionListener(this);
			//Adding to menu bar
			menuBar.add(menu[i]);
			//Adding to menu branched
			menu[0].add(file[i]);
			menu[1].add(edit[i]);
			if(i < 2)
			{
				view[i].addActionListener(this);
				organize[i].addActionListener(this);
				soundbank[i].addActionListener(this);
				help[i].addActionListener(this);
				menu[2].add(view[i]);
				menu[3].add(organize[i]);
				menu[4].add(soundbank[i]);
				menu[5].add(help[i]);
			}
		}
		
		return menuBar;
	}
	
	public MIDIMain()
	{
		//Initialization process
		initialization();
		mode();
		
		//Program starts
		while(true)
		{
			//Inputs (More will be added)
			mouseControl();
			scrollBar();
			
			//Process
			if(mode == 1)
				trackLayout();
			
			//Outputs (More will be added)
			window.repaint();
			
			//Pause (Repeat)
			pause(10);
		}
	}
	
	//Mode() sets the components correctly to represent the current menu type
	public void mode()
	{
		//reset menubar
		for(byte i = 0; i < 6; i++)
        {
			window.getJMenuBar().getMenu(i).setEnabled(true);
        }
		
		//Unspecific menu type
		if(mode > 0)
		{
			visual.getComponent(0).setVisible(true);
			for(byte i = 0; i < tools.length - 6; i++)
			{
				tools[i].setVisible(true);
			}
		}
		
		//Welcome Screen
		if(mode == 0)
		{
			visual.getComponent(0).setVisible(false);
			scroll.setVisible(false);
			button.setVisible(false);
			for(byte i = 1; i < 5; i++)
	        {
				window.getJMenuBar().getMenu(i).setEnabled(false);
	        }
			for(byte i = 0; i < tools.length; i++)
			{
				tools[i].setVisible(false);
			}
		}
		//Track Editor
		else if(mode == 1)
		{
			tools[17].setVisible(false);
			scroll.setVisible(true);
			button.setVisible(true);
			window.getJMenuBar().getMenu(2).setEnabled(false);
		}
		//Note Editor
		else if(mode == 2)
		{
			tools[17].setVisible(true);
			scroll.setVisible(false);
			button.setVisible(false);
			window.getJMenuBar().getMenu(3).setEnabled(false);
		}
	}
	
	//scrollBar() processes the input of the scroll bar
	public void scrollBar()
	{
		if(mode == 1)
		{
			scrollY = (short) scroll.getValue();
			button.setLocation(GUI.toolBarHeight+30, GUI.toolBarHeight+30-scrollY);
		
		}
	}
	
	//trackLayout() sets the components correctly in the track editor
	public void trackLayout()
	{
		if(button.getLocation().getY() < 40)
			button.setEnabled(false);
		else
			button.setEnabled(true);
	}
	
	//actionPerformed(ActionEvent e) is the main direct listener for all components in the JFrame
	//ActionEvent e = event triggered containing event information
	public void actionPerformed(ActionEvent e) 
	{
		//Placeholder button
		if(e.getSource() == button)
		{
			mode = 2;
			mode();
		}
		//Tool #18: GO BACK
		if(e.getSource() == tools[17])
		{
			mode = 1;
			mode();
		}
		//MenuBar -> File -> New
		if(e.getActionCommand().equals("New"))
		{
			mode = 1;
			mode();
		}
		//MenuBar -> File -> Save
		if(e.getActionCommand().equals("Save"))
		{
			NotifyAnimation.sendMessage("File saved...");
		}
		//MenuBar -> File -> Save As
		if(e.getActionCommand().equals("Save As"))
		{
			NotifyAnimation.sendMessage("What are you doing with your life?");
		}
		//MenuBar -> File -> Quit
		if(e.getActionCommand().equals("Quit"))
		{
			System.exit(1);
		}
	}
	
	//mouseControl() responds to mouse inputs recorded in the CursorListener class
	public void mouseControl()
	{
		if(mode == 2)
		{
			if(CursorListener.getMiddleClick())
			{
				scale[0] = (short) (CursorListener.getLocationDif()[0]);
				scale[1] = (short) (CursorListener.getLocationDif()[1]);
			}
			
			if(CursorListener.getLeftClick() && CursorListener.getObjectNumber() >= 0)
			{
				notes[CursorListener.getObjectNumber()].setLocation(CursorListener.getLocation()[0] + coordinates[0], CursorListener.getLocation()[1] + coordinates[1]);
			}
			else if(CursorListener.getLeftClick())
			{
				coordinates[0] = CursorListener.getLocationDif()[0];
				coordinates[1] = CursorListener.getLocationDif()[1];
				
				if(coordinates[0] < 0)
					coordinates[0] = 0;
				if(coordinates[1] < 0)
					coordinates[1] = 0;
				if(coordinates[1] > 100*scale[1])
					coordinates[1] = (short) (100*scale[1]);
			}
			
			if(CursorListener.getRightClick() && CursorListener.getObjectNumber() >= 0)
			{
				notes[CursorListener.getObjectNumber()].setLength(CursorListener.getLocation()[0] + coordinates[0]);
			}
			
			if(CursorListener.getMouseWheel() != 0)
			{
				scale[0] -= CursorListener.getMouseWheel()*2;
				scale[1] -= CursorListener.getMouseWheel()*2;
				CursorListener.setMouseWheel((byte) 0);
			}
			
			if(scale[0] < 10)
				scale[0] = 10;
			if(scale[1] < 10)
				scale[1] = 10;
			if(scale[0] > 100)
				scale[0] = 100;
			if(scale[1] > 100)
				scale[1] = 100;
		}
		else if(mode == 1)
		{
			if(CursorListener.getMouseWheel() != 0)
			{
				scroll.setValue(scroll .getValue() + CursorListener.getMouseWheel()*10);
				CursorListener.setMouseWheel((byte) 0);
			}
		}
	}
	
	//pause(int t) stops the program temporary for the designated amount of time
	//int t = time paused in milliseconds (1/1000 of a second)
	public static void pause(int t)
	{
		try{
			Thread.sleep(t);
		} catch (Exception exc){}
	}
	
	//getMode() returns value of the current mode type
	public static byte getMode()
	{
		return mode;
	}
	
	//getCoordinates() returns the x and y values of the display
	public static short[] getCoordinates()
	{
		return coordinates;
	}
	
	public static short getScrollValue()
	{
		return scrollY;
	}
	
	public static short getPreLength()
	{
		return scale[0];
	}
	
	public static short getPreHeight()
	{
		return scale[1];
	}
	
	public static Notes getNote(int index)
	{
		return notes[index];
	}
}
