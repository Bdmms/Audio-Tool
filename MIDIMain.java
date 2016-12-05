import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.*;
import java.awt.event.*;

public class MIDIMain implements ActionListener
{
	/**
	 * Audio Tool Project
	 * 
	 * By: Ethan Lee and Sean Rannie
	 * Date: October 15, 2016
	 * 
	 * The Main class contains all of the components of the window and
	 * responds to all user inputs to create the appropriate response since
	 * it is connected to all other classes in the project. This class controls
	 * most of the menu information (i.e. menu type, menu location, etc.)
	 */
	
	private JFrame window = new JFrame("MIDI EDITOR TOOL");	//The window the components are displayed on
	private GUI visual = new GUI();							//The drawing component used to display most graphics
	private CursorListener mouse = new CursorListener();	//Mouse Listener used to register mouse movement and inputs
	private KeyboardListener key = new KeyboardListener();	//Key Listener used to register key inputs
	private JFileChooser fileIn = new JFileChooser();		//The file directory that opens when choosing a file
	private MIDIFilter filter = new MIDIFilter();			//The file filter for the JFileChooser
	private MIDIPlayer player = new MIDIPlayer();			//The class that can play midi files
	private MIDIReader reader = new MIDIReader();			//The class that reads and creates midi files
	private ToolBar toolBar;								//The tool bar that holds the buttons for use in the editors
	private JScrollBar scroll;								//The scroll bar used in the track editor
	private InfoBar info;									//The song information bar
	
	private static byte selected = 0;						//The selection mode (0 = single | 1 = selecting notes | 2 = selected notes)
	private static Rectangle selectBox;						//The rectangle that is used to select notes
	
	private static boolean play = false;					//Determines if the song is being played or not
	private static byte mode = 0;							//Determines which menu the program displays
	private static byte track = -1;							//Determines which track has been entered
	private static short[] scale = {20, 20};				//The values that are used to space the grid layout (x, y)
	private static short scrollY = 0;						//The value assigned to the scroll bar
	private static short y = 1000;							//The y value used in the note editor (At 1000 to start user at mid-range notes)
	private static long x = 0;								//The x value used in the note editor
	
	public static void main(String[] args) {
		//Removes forced static methods and variables
		new MIDIMain();
	}
	
	//Constructor method
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
			keyControl();
			if(scroll.getValue() != scrollY)
				scrollY = visual.setComponentsOfScrollBar();
			
			//Process
			if(mode == 1)
				Tracks.trackLayout();
			if(mode >= 2 && play)
				x = scale[0]*player.getTickPosition();
			
			//Outputs
			window.repaint();
			
			//Pause (Repeat)
			pause(10);
		}
	}

	//initialization() initializes basic graphical components
	public void initialization(){
		//drawing component is initialized and contains buttons
		visual.setPreferredSize(new Dimension(720,480));
		visual.setLayout(null);
		setGUIComponents();	//Special method for setting components
		
		//JFrame is initialized and contains all other components and listeners
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(720, 480);
        window.setBackground(Color.WHITE);
        window.setResizable(true);
        window.setLocation(0, 0);
        window.add(visual);
        window.setJMenuBar(setMenuBar());	//Special method for setting the menu bar
        window.pack();
        window.addMouseListener(mouse);
        window.addMouseMotionListener(mouse);
        window.addMouseWheelListener(mouse);
        window.addKeyListener(key);
        window.setVisible(true);
        
        setFileChooser();
	}
	
	//createFileChooser() prepares the fileChooser (file explorer) in the program for when file selection is needed
	public void setFileChooser()
	{
		fileIn.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileIn.setAcceptAllFileFilterUsed(false);
		fileIn.addChoosableFileFilter(filter);
		fileIn.setFileFilter(filter);
	}
	
	//createToolBar() initializes the tool bar
	public void setGUIComponents()
	{
		toolBar = visual.getToolBar();
		scroll = visual.getScrollBar();
		info = visual.getInfoBar();
		for(byte i = 0; i < ToolBar.toolLength; i++)
		{
			toolBar.getTools(i).addActionListener(this);
		}
	}
	
	//createMenuBar() initializes a JMenuBar and returns it
	public JMenuBar setMenuBar(){
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
	
	//setTrackButtons(byte length) sets the track buttons / instrument lists according to the number of tracks in the song
	//byte length = number of tracks in the song
	public void setTrackButtons(byte length)
	{
		for(byte i = 0; i < Tracks.getButtonLength(); i++)
		{
			visual.remove(Tracks.getTrackEntryButton(i));
			visual.remove(Tracks.getInstrumentListButton(i));
		}
		Tracks.resetAllButtons();
			
		for(byte i = 0; i < length; i++)
		{
			addTrackButtons();
		}
	}
	
	//addInstrumentList() adds an instrument button to the interface
	public void addTrackButtons()
	{
		if(Tracks.isNotMaximum())
		{
			Tracks.addTrackButtons();
			Tracks.getTrackEntryButton(Tracks.getButtonLength() - 1).addActionListener(this);
			Tracks.getInstrumentListButton(Tracks.getButtonLength() - 1).addActionListener(this);
			visual.add(Tracks.getTrackEntryButton(Tracks.getButtonLength() - 1));
			visual.add(Tracks.getInstrumentListButton(Tracks.getButtonLength() - 1));
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
		
		//Welcome Screen
		if(mode == 0)
		{
			//Tool bar
			toolBar.setVisible(false);
			for(byte i = 0; i < ToolBar.toolLength; i++)
			{
				toolBar.getTools(i).setVisible(false);
			}
			
			//Track buttons
			for(byte i = 0; i < Tracks.getButtonLength(); i++)
			{
				Tracks.getTrackEntryButton(i).setVisible(false);
				Tracks.getInstrumentListButton(i).setVisible(false);
			}
			
			//Scroll bar
			scroll.setVisible(false);
			
			//Info bar
			info.setVisible(false);
			
			//Menu bar
			for(byte i = 1; i < 5; i++)
	        {
				window.getJMenuBar().getMenu(i).setEnabled(false);
	        }
		}
		//Track Editor
		else if(mode == 1)
		{
			//Tool bar
			toolBar.setVisible(true);
			for(byte i = 0; i < ToolBar.toolLength - 6; i++)
			{
				toolBar.getTools(i).setVisible(true);
			}
			toolBar.getTools(ToolBar.toolLength - 1).setVisible(false);
			
			//Track buttons
			for(byte i = 0; i < Tracks.getButtonLength(); i++)
			{
				Tracks.getTrackEntryButton(i).setVisible(true);
				Tracks.getInstrumentListButton(i).setVisible(true);
			}
			
			//Scroll bar
			scroll.setVisible(true);
			if(Tracks.getButtonLength() > 5)	//If scroll bar is needed in list
				scroll.setValues(scrollY, (Tracks.trackHeight+5)*5, 0, (Tracks.trackHeight+5)*Tracks.getButtonLength());
			else
				scroll.setValues(0, 100, 0, 100);
			scrollY = visual.setComponentsOfScrollBar();
			
			//Info bar
			info.setVisible(true);
			
			//Menu bar
			window.getJMenuBar().getMenu(2).setEnabled(false);
		}
		//Note Editor
		else if(mode >= 2)
		{
			//Tool bar
			toolBar.setVisible(true);
			for(byte i = 0; i < ToolBar.toolLength - 6; i++)
			{
				toolBar.getTools(i).setVisible(true);
			}
			toolBar.getTools(ToolBar.toolLength - 1).setVisible(true);
			
			//Track buttons
			for(byte i = 0; i < Tracks.getButtonLength(); i++)
			{
				Tracks.getTrackEntryButton(i).setVisible(false);
				Tracks.getInstrumentListButton(i).setVisible(false);
			}
			
			//Scroll bar
			scroll.setVisible(false);
			
			//Info bar
			info.setVisible(false);
			
			//Menu bar
			window.getJMenuBar().getMenu(3).setEnabled(false);
		}
	}
	
	//actionPerformed(ActionEvent e) is the main direct listener for all components in the JFrame
	//ActionEvent e = event triggered containing event information
	public void actionPerformed(ActionEvent e) 
	{
		//Tool #1: PLAY
		if(e.getSource() == toolBar.getTools(0))
		{
			//If song is playing
			if(play == true)
			{
				play = false;
				player.stop();
			}
			else
			{
				if(mode == 2)
					MIDISong.saveTrack(track);
				play = true;
				player.play(true);
			}
		}
		//Tool #2: STOP
		if(e.getSource() == toolBar.getTools(1))
		{
			if(play == true)
			{
				play = false;
				player.stop();
				player.setTickPosition(0);
				x = 0;
			}
			else
			{
				if(mode == 2)
					MIDISong.saveTrack(track);
				play = true;
				player.setTickPosition(0);
				player.play(true);
			}
		}
		//Tool #3: ADD
		if(e.getSource() == toolBar.getTools(2))
		{
			//track editor
			if(mode == 1)
			{
				MIDISong.addTrack();
				addTrackButtons();
				
				if(Tracks.getButtonLength() > 5)
					scroll.setValues(scrollY, (Tracks.trackHeight+5)*5, 0, (Tracks.trackHeight+5)*Tracks.getButtonLength());
				else
					scroll.setValues(0, 100, 0, 100);
				scrollY = visual.setComponentsOfScrollBar();
			}
			//note editor
			else if(mode == 2)
			{
				MIDISong.addNote(track, x/scale[0], (byte)(Notes.MAX_TONE - (y + (GUI.screenHeight - GUI.fullAddHeight)/2)/scale[1]), (byte)78, x/scale[0]+8);
			}
		}
		//Tool #4 REMOVE / DELETE
		if(e.getSource() == toolBar.getTools(3))
		{
			for(int i = Notes.getNumNotes()-1; i >= 0; i--)
			{
				if(MIDISong.getNotes(track, i).isSelected())
				{
					MIDISong.removeNote(track, i);
				}
			}
		}
		//Tool #18: GO BACK
		if(e.getSource() == toolBar.getTools(ToolBar.toolLength - 1))
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
			setTrackButtons((byte)1);
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
	            player.setAllInstruments();
				setTrackButtons(MIDISong.getTracksLength());
	            mode = 1;
				mode();
				NotifyAnimation.sendMessage("Notification","Opening: "+reader.getFileName());
	        } else {
	        	NotifyAnimation.sendMessage("Notification","File was not be opened.");
	        }
		}
		//MenuBar -> File -> Save
		if(e.getActionCommand().equals("Save"))
		{
			NotifyAnimation.sendMessage("Notification","File saved... (Not really)");
		}
		//MenuBar -> File -> Save As
		if(e.getActionCommand().equals("Save As"))
		{
			if(mode == 2)
				MIDISong.saveTrack(track);
			reader.saveFile(MIDISong.saveSequence(), JOptionPane.showInputDialog("What will you save the file as?", reader.getFileName()));
			NotifyAnimation.sendMessage("Notification","File saved...");
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
		//MenuBar -> Edit -> Undo
		if(e.getActionCommand().equals("Undo"))
		{
			long n = Long.parseLong(JOptionPane.showInputDialog("What will be the new tempo?", MIDISong.getTempo()));
			MIDISong.setTempo(n);
			//player.setTempo((float)n);
		}
		//MenuBar -> Edit -> Copy
		if(e.getActionCommand().equals("Copy"))
		{
			Notes.copyNotes();
		}
		//MenuBar -> Edit -> Paste
		if(e.getActionCommand().equals("Paste"))
		{
			Notes.pasteNotes(x/scale[0], (byte)0);
		}
		//MenuBar -> Edit -> Delete
		if(e.getActionCommand().equals("Delete"))
		{
			for(int i = 0; i < Notes.getNumNotes(); i++)
			{
				if(MIDISong.getNotes(track, i).isSelected())
				{
					MIDISong.removeNote(track, i);
				}
			}
		}
		//MenuBar -> Soundbank -> Load Soundbank
		if(e.getActionCommand().equals("Load Soundbank"))
		{
			filter.setFilterMIDI(false);
			int v = fileIn.showOpenDialog(window);
			//If file is usable
			if (v == JFileChooser.APPROVE_OPTION) {
	            player.setSoundBank(reader.getSoundbank(fileIn.getSelectedFile()));
	            NotifyAnimation.sendMessage("Notification","Opening: "+fileIn.getSelectedFile());
	        } else {
	        	NotifyAnimation.sendMessage("Notification","File was not be opened.");
	        }
		}
		//MenuBar -> Soundbank -> Load Soundbank
		if(e.getActionCommand().equals("Set to Default"))
		{
			player.setSoundBankDefault();
			NotifyAnimation.sendMessage("Notification","Opening: Default Soundbank");
		}
		
		//Track buttons
		for(byte i = 0; i < Tracks.getButtonLength(); i++)
		{
			//If source is equal to one of the track buttons
			if(e.getSource() == Tracks.getTrackEntryButton(i))
			{
				track = i;
				MIDISong.openTrack(track);
				mode = 2;
				mode();
			}
			//If source is equal to one of the instrument combo boxes
			if(e.getSource() == Tracks.getInstrumentListButton(i))
			{
				player.setInstrument((byte)Tracks.getInstrumentListButton(i).getSelectedIndex(), i);
				MIDISong.getTracks(i).setInstrument((byte)Tracks.getInstrumentListButton(i).getSelectedIndex());
			}
		}
	}
	
	public void keyControl()
	{
		//C
		if(key.getControl() && key.getLetters()[2])
		{
			Notes.copyNotes();
			key.setLetter(false, (byte) 2);
			key.setControl(false);
		}
		//V
		if(key.getControl() && key.getLetters()[21])
		{
			Notes.pasteNotes(x/scale[0], (byte)0);		
			key.setLetter(false, (byte) 21);
			key.setControl(false);
		}
		//T
		if(key.getLetters()[19] == true)
		{
			long n = Long.parseLong(JOptionPane.showInputDialog("What will be the new tempo?", MIDISong.getTempo()));
			MIDISong.setTempo(n);
			
			key.setLetter(false, (byte)19);
		}
	}
	
	//mouseControl() responds to mouse inputs recorded in the CursorListener class
	public void mouseControl()
	{
		//Note Editor
		if(mode >= 2)
		{
			gridControl();
		}
		//Track Editor
		else if(mode == 1)
		{
			//When the Mouse Wheel is moving
			if(CursorListener.getMouseWheel() != 0)
			{
				scroll.setValue(scroll .getValue() + CursorListener.getMouseWheel()*25);
				CursorListener.setMouseWheel((byte) 0);
			}
		}
	}
	
	//singleSelectControls() processes the inputs for selecting a group of notes
	public void singleSelectControls()
	{
		//Left Click
		if(CursorListener.getClick() == 1)
		{
			//Object is being held
			if(CursorListener.getObjectNumber() >= 0)
			{
				MIDISong.getNotes(track, CursorListener.getObjectNumber()).selection(true);
				MIDISong.getNotes(track, CursorListener.getObjectNumber()).setLocation(CursorListener.getLocation()[0] - GUI.mouseDisplacement - GUI.sideBarWidth - CursorListener.getOrigin()[0], (short)(y + CursorListener.getLocation()[1] - GUI.fullAddHeight - GUI.windowBarHeight));
			}
			//Left Click while not holding an object
			else
			{
				Notes.unSelectAll();
				if(CursorListener.getLocation()[0] - GUI.mouseDisplacement < GUI.sideBarWidth && CursorListener.getLocation()[1] > GUI.fullAddHeight + GUI.windowBarHeight)
				{
					player.NoteOn((byte)(Notes.MAX_TONE - (CursorListener.getLocation()[1] - GUI.windowBarHeight - GUI.fullAddHeight + y)/scale[1]), (byte) 78);
				}
				else
				{
					x = CursorListener.getLocationDif()[0];
					y = CursorListener.getLocationDif()[1];
				}
			}
		}
		else
		{
			player.NoteOff();
		}
		//Right Click
		if(CursorListener.getClick() == 3)
		{
			//If on an object
			if(CursorListener.getObjectNumber() >= 0)
			{
				MIDISong.getNotes(track, CursorListener.getObjectNumber()).setEnd((CursorListener.getLocation()[0] + x - GUI.sideBarWidth));
				selected = 0;
			}
			else
			{
				selected = 1;
			}
		}
		else
		{
			//If objects have been selected
			if(selected == 1)
			{
				Notes.selectContained(getSelectBox());
				selected = 2;
			}
			else
				selected = 0;
		}
	}
	
	//multiSelectControls() processes the inputs for selecting a group of notes
	public void multiSelectControls()
	{
		//Left Click
		if(CursorListener.getClick() == 1)
		{
			//Selected Object is being held
			if(CursorListener.getObjectNumber() >= 0 && MIDISong.getNotes(track, CursorListener.getObjectNumber()).isSelected())
			{
				for(int i = 0; i < Notes.getNumNotes(); i++)
				{
					if(MIDISong.getNotes(track, i).isSelected() && i != CursorListener.getObjectNumber())
					{
						MIDISong.getNotes(track, i).setLocation(CursorListener.getLocation()[0] - GUI.mouseDisplacement - GUI.sideBarWidth - CursorListener.getOrigin()[0] + (MIDISong.getNotes(MIDIMain.getTrackMenu(), i).getX() - MIDISong.getNotes(MIDIMain.getTrackMenu(), CursorListener.getObjectNumber()).getX()), 
								(short)(y + CursorListener.getLocation()[1] - GUI.fullAddHeight - GUI.windowBarHeight + (MIDISong.getNotes(MIDIMain.getTrackMenu(), i).getY() - MIDISong.getNotes(MIDIMain.getTrackMenu(), CursorListener.getObjectNumber()).getY())));
					}
				}
				MIDISong.getNotes(track, CursorListener.getObjectNumber()).setLocation(CursorListener.getLocation()[0] - GUI.mouseDisplacement - GUI.sideBarWidth - CursorListener.getOrigin()[0], (short)(y + CursorListener.getLocation()[1] - GUI.fullAddHeight - GUI.windowBarHeight));
			}
			else
			{
				Notes.unSelectAll();
				selected = 0;
			}
		}
		//Right Click
		if(CursorListener.getClick() == 3)
		{
			//Selected Object is being clicked
			if(CursorListener.getObjectNumber() >= 0 && MIDISong.getNotes(track, CursorListener.getObjectNumber()).isSelected())
			{
				for(int i = 0; i < Notes.getNumNotes(); i++)
				{
					if(MIDISong.getNotes(track, i).isSelected() && i != CursorListener.getObjectNumber())
					{
						MIDISong.getNotes(track, i).setEnd((CursorListener.getLocation()[0] + x - GUI.sideBarWidth + (MIDISong.getNotes(MIDIMain.getTrackMenu(), i).getEndTick()*scale[0] - MIDISong.getNotes(MIDIMain.getTrackMenu(), CursorListener.getObjectNumber()).getEndTick()*scale[0])));
					}
				}
				MIDISong.getNotes(track, CursorListener.getObjectNumber()).setEnd((CursorListener.getLocation()[0] + x - GUI.sideBarWidth));
			}
			else
			{
				Notes.unSelectAll();
				selected = 0;
			}
		}
	}
	
	//gridControl() processes the inputs used in the note editor
	public void gridControl()
	{
		//If selecting one objects
		if(selected < 2)
			singleSelectControls();
		//If selecting multiple objects
		else if(selected == 2)
			multiSelectControls();

		//Middle Click
		if(CursorListener.getClick() == 2)
		{
			scale[0] = (short) (CursorListener.getLocationDif()[0]);
			scale[1] = (short) (CursorListener.getLocationDif()[1]);
		}
		
		//When the Mouse Wheel is moving
		if(CursorListener.getMouseWheel() != 0)
		{
			scale[0] -= CursorListener.getMouseWheel()*2;
			scale[1] -= CursorListener.getMouseWheel()*2;
			//If scale is in limits
			if(scale[0] > 0 && scale[1] > 0 && scale[0] < 100 && scale[1] < 100)
			{
				x += CursorListener.getMouseWheel()*(CursorListener.getLocation()[0])/scale[0];
				y += CursorListener.getMouseWheel()*(CursorListener.getLocation()[1])/scale[1];
			}
			CursorListener.setMouseWheel((byte) 0);
		}
		
		gridLimits();
	}
	
	//gridLimits() limits the value that can be assigned to coordinate variables
	public void gridLimits()
	{
		//Limits to the scale
		if(scale[0] < 1)
			scale[0] = 1;
		if(scale[1] < 5)
			scale[1] = 5;
		if(scale[0] > 100)
			scale[0] = 100;
		if(scale[1] > 100)
			scale[1] = 100;
		
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
	private static void pause(int t)
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
	
	//getXCoordinate() returns the x value of the display
	public static long getXCoordinate()
	{
		return x;
	}
	
	//getYCoordinate() returns the y value of the display
	public static short getYCoordinate()
	{
		return y;
	}
	
	//getScrollValue() returns the value assigned to the scrollBar
	public static short getScrollValue()
	{
		return scrollY;
	}
	
	//getPreLength() returns the x scaling of the note editor grid
	public static short getPreLength()
	{
		return scale[0];
	}
	
	//getPreHeight() returns the y scaling of the note editor grid
	public static short getPreHeight()
	{
		return scale[1];
	}
	
	//getTrackMenu() returns the current track being viewed from number order
	public static byte getTrackMenu()
	{
		return track;
	}
	
	//getSelectBox() returns the highlighted box used to select notes
	public static Rectangle getSelectBox()
	{
		selectBox = new Rectangle(CursorListener.getOrigin()[0] - GUI.mouseDisplacement, CursorListener.getLocation()[1] - GUI.fullAddHeight, CursorListener.getLocation()[0] - CursorListener.getOrigin()[0], CursorListener.getOrigin()[1] - CursorListener.getLocation()[1]);
		return selectBox;
	}
	
	//isSelecting() returns whether multiple notes are being selected
	public static boolean isSelecting()
	{
		if(selected == 1)
			return true;
		else
			return false;
	}
}
