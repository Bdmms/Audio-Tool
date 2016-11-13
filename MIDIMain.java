import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.*;
import java.awt.event.*;

public class MIDIMain implements ActionListener
{
	/**
	 * Date: October 15, 2016
	 * 
	 * The Main class contains all of the components of the window and
	 * responds to all user inputs to create the appropriate response since
	 * it is connected to all other classes in the project. This class controls
	 * most of the menu information (i.e. menu type, menu location, etc.)
	 * 
	 * Note 1: All comments in this project are displayed above the targeted line of code 
	 */
	
	private JFileChooser fileIn = new JFileChooser();		//The file directory that opens when choosing a file
	private MIDIFilter filter = new MIDIFilter();			//The file filter for the JFileChooser
	private JFrame window = new JFrame("MIDI EDITOR TOOL");	//The window the components are displayed on
	private GUI visual = new GUI();							//The drawing component used to display most graphics
	private CursorListener mouse = new CursorListener();	//Mouse Listener used to register mouse movement and inputs
	private KeyboardListener key = new KeyboardListener();	//Key Listener used to register key inputs
	private MIDIPlayer player = new MIDIPlayer();			//The class that can play midi files
	private MIDIReader reader = new MIDIReader();			//The class that reads and creates midi files
	
	private JScrollBar scroll = new JScrollBar();			//The scroll bar used in the track editor
	private JButton[] tools = new JButton[18];				//Tool bar buttons
	private JButton[] trackButtons = new JButton[0];		//Buttons for tracks
	
	private static boolean play = false;					//Determines if the song is being played or not
	private static byte mode = 0;							//Determines which menu the program displays
	private static byte track = -1;							//Determines which track has been entered
	private static long x = 0;								//The x value used in the note editor
	private static short y = 0;								//The y value used in the note editor
	private static short[] scale = {20, 20};				//The values that are used to space the grid layout (x, y)
	private static short scrollY = 0;						//The value assigned to the scroll bar
	
	public static void main(String[] args) {
		//Removes forced static methods and variables
		new MIDIMain();
	}

	//initialization() initializes basic graphical components
	public void initialization(){
		//scroll bar is initialized
		
		scroll.setBounds(680, 50, 20, 320);
		scroll.setUnitIncrement(10);
		
		//drawing component is initialized and contains buttons
		visual.setPreferredSize(new Dimension(720,480));
		visual.setLayout(null);
		visual.add(createToolBar());
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
        window.addKeyListener(key);
        window.addMouseListener(mouse);
        window.addMouseMotionListener(mouse);
        window.addMouseWheelListener(mouse);
        window.setVisible(true);
        
        createFileChooser();
	}
	
	//createFileChooser() prepares the fileChooser (file explorer) in the program for when file selection is needed
	public void createFileChooser()
	{
		fileIn.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileIn.setAcceptAllFileFilterUsed(false);
		fileIn.addChoosableFileFilter(filter);
		fileIn.setFileFilter(filter);
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
		//Size = (720 pixels x 23 pixels)
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
	
	//setTrackButtons(byte length) sets the track buttons according to the number of tracks in the song
	//byte length = number of tracks in the song
	public void setTrackButtons(byte length)
	{
		for(byte i = 0; i < trackButtons.length; i++)
		{
			visual.remove(trackButtons[i]);
		}
		
		trackButtons = new JButton[length];
		for(byte i = 0; i < trackButtons.length; i++)
		{
			trackButtons[i] = new JButton("Track "+(i+1));
			trackButtons[i].setBackground(Color.WHITE);
			trackButtons[i].setFont(new Font("A", Font.BOLD, 14));
			trackButtons[i].setSize(100, 20); 
			trackButtons[i].setVisible(true);
			trackButtons[i].addActionListener(this);
			visual.add(trackButtons[i]);
		}
	}
	
	//Initial method
	public MIDIMain()
	{
		//Initialization process
		initialization();
		mode();
		
		//Program starts
		while(true)
		{
			//Inputs
			mouseControl();
			scrollBar();
			
			//Process
			if(mode == 1)
				trackLayout();
			if(mode == 2 && play)
				x = scale[0]*player.getTickPosition();
			
			//Outputs
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
		//set trackButtons
		for(byte i = 0; i < trackButtons.length; i++)
		{
			if(mode == 1)
				trackButtons[i].setVisible(true);
			else
				trackButtons[i].setVisible(false);
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
			for(byte i = 0; i < tools.length; i++)
			{
				tools[i].setVisible(false);
			}
			for(byte i = 1; i < 5; i++)
	        {
				window.getJMenuBar().getMenu(i).setEnabled(false);
	        }
		}
		//Track Editor
		else if(mode == 1)
		{
			tools[17].setVisible(false);
			scroll.setVisible(true);
			
			//If scroll bar is needed in list
			if(trackButtons.length > 5)
				scroll.setValues(scrollY, (Tracks.trackHeight+5)*5, 0, (Tracks.trackHeight+5)*trackButtons.length);
			else
				scroll.setValues(0, 100, 0, 100);
			
			window.getJMenuBar().getMenu(2).setEnabled(false);
		}
		//Note Editor
		else if(mode == 2)
		{
			tools[17].setVisible(true);
			scroll.setVisible(false);
			window.getJMenuBar().getMenu(3).setEnabled(false);
		}
	}
	
	//scrollBar() processes the input of the scroll bar
	public void scrollBar()
	{
		//Track editor
		if(mode == 1)
		{
			scrollY = (short) scroll.getValue();
			try
			{
				for(byte i = 0; i < trackButtons.length; i++)
				{
					trackButtons[i].setLocation(70, 20+GUI.toolBarHeight-scrollY+(Tracks.trackHeight+5)*i); 
				}
			}
			catch(NullPointerException ex){
				//The null pointer exception is fairly common when mixed with the action listener
			}
		}
	}
	
	//trackLayout() sets the components correctly in the track editor
	public void trackLayout()
	{
		try
		{
			//Checks if button is behind components
			for(byte i = 0; i < trackButtons.length; i++)
			{
				if(trackButtons[i].getLocation().getY() < GUI.toolBarHeight)
					trackButtons[i].setEnabled(false);
				else
					trackButtons[i].setEnabled(true);
			}
		}
		catch(NullPointerException ex){}//Program is actually more likely to trigger and exception
	}
	
	//actionPerformed(ActionEvent e) is the main direct listener for all components in the JFrame
	//ActionEvent e = event triggered containing event information
	public void actionPerformed(ActionEvent e) 
	{
		//Tool #1: PLAY
		if(e.getSource() == tools[0])
		{
			//If song is playing
			if(play == true)
			{
				play = false;
				player.stop();
			}
			else
			{
				play = true;
				player.play(true);
			}
		}
		//Tool #2: STOP
		if(e.getSource() == tools[1])
		{
			if(play == true)
			{
				play = false;
				player.setTickPosition(0);
				x = 0;
				player.stop();
			}
			else
			{
				play = true;
				player.setTickPosition(0);
				player.play(true);
			}
		}
		//Tool #3: ADD
		if(e.getSource() == tools[2])
		{
			//track editor
			if(mode == 1)
			{
				MIDISong.addTrack();
				setTrackButtons(MIDISong.getTracksLength());
				
				if(trackButtons.length > 5)
					scroll.setValues(scrollY, (Tracks.trackHeight+5)*5, 0, (Tracks.trackHeight+5)*trackButtons.length);
				else
					scroll.setValues(0, 100, 0, 100);
			}
			//note editor
			else if(mode == 2)
			{
				MIDISong.addNote(track);
			}
		}
		//Tool #18: GO BACK
		if(e.getSource() == tools[17])
		{
			MIDISong.closeTrack(track);
			track = -1;
			mode = 1;
			mode();
		}
		
		//MenuBar -> File -> New
		if(e.getActionCommand().equals("New"))
		{
			MIDISong.setSong(reader.createFile());
			setTrackButtons(MIDISong.getTracksLength());
			mode = 1;
			mode();
		}
		//MenuBar -> File -> Open
		if(e.getActionCommand().equals("Open"))
		{
			filter.setFilterMIDI(true);
			int v = fileIn.showOpenDialog(window);
			//If file is usable
			if (v == JFileChooser.APPROVE_OPTION) {
	            MIDISong.setSong(reader.readFile(fileIn.getSelectedFile()));
	            NotifyAnimation.sendMessage("Notification","Opening: "+reader.getFileName());
				setTrackButtons(MIDISong.getTracksLength());
	            mode = 1;
				mode();
	        } else {
	        	NotifyAnimation.sendMessage("Notification","File was not be opened.");
	        }
		}
		//MenuBar -> File -> Save
		if(e.getActionCommand().equals("Save"))
		{
			NotifyAnimation.sendMessage("Notification","File saved...");
		}
		//MenuBar -> File -> Save As
		if(e.getActionCommand().equals("Save As"))
		{
			NotifyAnimation.sendMessage("Notification","Save as what!?");
		}
		//MenuBar -> File -> Delete File
		if(e.getActionCommand().equals("Delete File"))
		{
			NotifyAnimation.sendMessage("Error","Are you sure about that!?");
		}
		//MenuBar -> File -> Quit
		if(e.getActionCommand().equals("Quit"))
		{
			System.exit(1);
		}
		
		//Track buttons
		for(byte i = 0; i < trackButtons.length; i++)
		{
			//If source is equall to one of the track buttons
			if(e.getSource() == trackButtons[i])
			{
				track = i;
				MIDISong.openTrack(track);
				mode = 2;
				mode();
			}
		}
	}
	
	//mouseControl() responds to mouse inputs recorded in the CursorListener class
	public void mouseControl()
	{
		//Note Editor
		if(mode == 2)
		{
			//Middle Click
			if(CursorListener.getClick() == 2)
			{
				scale[0] = (short) (CursorListener.getLocationDif()[0]);
				scale[1] = (short) (CursorListener.getLocationDif()[1]);
			}
			//Left Click while holding an object
			if(CursorListener.getClick() == 1)
			{
				//Object is being held
				if(CursorListener.getObjectNumber() >= 0)
					MIDISong.getNotes(track)[CursorListener.getObjectNumber()].setLocation(CursorListener.getLocation()[0] + x - scale[0]/2, (short)(CursorListener.getLocation()[1] + y - GUI.windowBarHeight - MIDIMain.getPreHeight()/2));
				//Left Click while not holding an object
				else
				{
					x = CursorListener.getLocationDif()[0];
					y = CursorListener.getLocationDif()[1];
				}
			}
			//Right Click on an object
			if(CursorListener.getClick() == 3 && CursorListener.getObjectNumber() >= 0)
			{
				MIDISong.getNotes(track)[CursorListener.getObjectNumber()].setEnd((CursorListener.getLocation()[0] + x));
			}
			//When the Mouse Wheel is moving
			if(CursorListener.getMouseWheel() != 0)
			{
				scale[0] -= CursorListener.getMouseWheel()*2;
				scale[1] -= CursorListener.getMouseWheel()*2;
				if(scale[0] > 0 && scale[1] > 0 && scale[0] < 100 && scale[1] < 100)
				{
					x += CursorListener.getMouseWheel()*(CursorListener.getLocation()[0])/scale[0];
					y += CursorListener.getMouseWheel()*(CursorListener.getLocation()[1])/scale[1];
				}
				CursorListener.setMouseWheel((byte) 0);
			}
		}
		//Track Editor
		else if(mode == 1)
		{
			//When the Mouse Wheel is moving
			if(CursorListener.getMouseWheel() != 0)
			{
				scroll.setValue(scroll .getValue() + CursorListener.getMouseWheel()*10);
				CursorListener.setMouseWheel((byte) 0);
			}
		}
		
		//MOVE THIS:
		//Limits to the scale
		if(scale[0] < 1)
			scale[0] = 1;
		if(scale[1] < 5)
			scale[1] = 5;
		if(scale[0] > 100)
			scale[0] = 100;
		if(scale[1] > 100)
			scale[1] = 100;
		
		//MOVE THIS:
		//Limits to the coordinates
		if(x < 0)
			x = 0;
		if(y < 0)
			y = 0;
		if(y > 120*scale[1] - (GUI.screenHeight - GUI.fullAddHeight))
			y = (short) (120*scale[1] - (GUI.screenHeight - GUI.fullAddHeight));
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
	public static long getXCoordinate()
	{
		return x;
	}
	
	public static short getYCoordinate()
	{
		return y;
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
	
	public static byte getTrackMenu()
	{
		return track;
	}
}
