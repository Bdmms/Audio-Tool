import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;

 // M.E.A.T (Midi Editor & Accessing Tool)
 // Audio Tool Project

/**
 * The Main class that contains all of the components of the window and
 * responds to all user inputs to create the appropriate response since
 * it is connected to all other classes in the project. This class controls
 * most of the menu information (i.e. menu type, menu location, etc.)
 * 
 * @author Ethan Lee and Sean Rannie [October 15, 2016]
 */
public class MIDIMain implements ActionListener, WindowListener
{
	private static JFrame window = new JFrame("M.E.A.T.");		//The window the components are displayed on
	private static GUI visual = new GUI();						//The drawing component used to display most graphics
	private static CursorListener mouse = new CursorListener();	//Mouse Listener used to register mouse movement and inputs
	private static KeyboardListener key = new KeyboardListener();//Key Listener used to register key inputs
	private static JFileChooser fileIn = new JFileChooser();	//The file directory that opens when choosing a file
	private static MIDIFilter filter = new MIDIFilter();		//The file filter for the JFileChooser
	private static MIDIPlayer player = new MIDIPlayer();		//The class that can play midi files
	private static MIDIReader reader = new MIDIReader();		//The class that reads and creates midi files
	private static ToolBar toolBar;								//The tool bar that holds the buttons for use in the editors
	private static JScrollBar scroll;							//The scroll bar used in the track editor
	private static InfoBar info;								//The song information bar
	private static TutorialArchive tutor = new TutorialArchive();//The tutorial window, which is a second window used to search information
	
	private static Rectangle selectBox;							//The rectangle that is used to select notes
	private static boolean focus = true;						//Whether the program window is in focus
	private static byte selected = 0;							//The selection mode 
	//If mode == 1 (0 = normal | >0 track is selected)| If mode == 2 (0 = single | 1 = selecting notes | 2 = selected notes)
	private static byte mode = 0;								//Determines which menu the program displays
	private static byte track = -1;								//Determines which track has been entered
	private static byte limit = 1;								//Determines the limit to the zoom
	private static short[] scale = {20, 20};					//The values that are used to space the grid layout (x, y)
	private static short scrollY = 0;							//The value assigned to the scroll bar
	private static short y = 1000;								//The y value used in the note editor (At 1000 to start user at mid-range notes)
	private static long x = 0;									//The x value used in the note editor
	
	/**
	 * Where the program starts.
	 * @param args
	 */
	public static void main(String[] args) 
	{
		new MIDIMain();
	}
	
	/**
	 * The constructor of MIDIMain. It allows non-static varibles to be
	 * used outside of main(String[] args).
	 */
	public MIDIMain()
	{
		//Initialization process
		initialization();

		//Program starts
		while(true)
		{
			//If screen isn't in focus, inputs do not have to be processed
			if(focus){
				//Inputs
				resize();
				mouseControl();
				keyControl();
				if(scroll.getValue() != scrollY)
					scrollY = visual.setComponentsOfScrollBar();
			}
			
			//Process
			if(mode == 1)
				Tracks.trackLayout();
			if(MIDIPlayer.isPlaying())
				x = scale[0]*MIDIPlayer.getTickPosition();
			
			//Outputs
			window.repaint();
			
			//Pause (Refresh)
			pause(10);
		}
	}

	/**
	 * <blockquote>
	 * <h1><i>initialization</i></h1>
	 * <p><pre>{@code public void initialization()}</pre></p> 
	 * Initializes basic graphical components of the program.</p> 
	 */
	public void initialization()
	{
		//{screenWidth, screenHeight, x location, y location, color palette)
		short[] config = reader.getConfig();
		
		//drawing component is initialized and contains buttons
		visual.setPreferredSize(new Dimension(config[0],config[1]));
		visual.setLayout(null);
		visual.setColourScheme((byte)config[4]);
		setGUIComponents();	//Special method for setting components
		
		//JFrame is initialized and contains all other components and listeners
        window.setSize(720, 480);
        window.setMinimumSize(new Dimension(700, 360));
        window.setBackground(Color.WHITE);
        window.setResizable(true);
        window.setLocation(config[2], config[3]);
        window.add(visual);
        window.setJMenuBar(setMenuBar());	//Special method for setting the menu bar
        window.pack();
        window.addMouseListener(mouse);
        window.addMouseMotionListener(mouse);
        window.addMouseWheelListener(mouse);
        window.addWindowListener(this);
        window.addKeyListener(key);
        toolBar.addKeyListener(key);
        window.setVisible(true);
        setFileChooser();
        
        mode(0);	//Set the menu mode
	}
	
	/**
	 * <blockquote>
	 * <h1><i>setFileChooser</i></h1>
	 * <p><pre>{@code public void setFileChooser()}</pre></p> 
	 * <p> Prepares the fileChooser (file explorer) in the program for when any type of file selection is needed.</p> 
	 */
	public void setFileChooser()
	{
		fileIn.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileIn.setAcceptAllFileFilterUsed(false);
		fileIn.setFileFilter(filter);
		fileIn.addChoosableFileFilter(filter);
	}
	
	//createToolBar() initializes the tool bar
	public void setGUIComponents()
	{	
		toolBar = visual.getToolBar();
		scroll = visual.getScrollBar();
		info = visual.getInfoBar();
		//Action listener is needed to be added to each 
		for(byte i = 0; i < ToolBar.toolLength; i++)
		{
			toolBar.getTools(i).addActionListener(this);
		}
		toolBar.getComboBox().addActionListener(this);
	}
	
	//createMenuBar() initializes a JMenuBar and returns it
	public JMenuBar setMenuBar()
	{
		//Menu bar component is created
		JMenuBar menuBar = new JMenuBar();
		//Each branch of the menu bar is created
		JMenu[] menu = {new JMenu("FILE"), new JMenu("EDIT"), new JMenu("VIEW"), new JMenu("SONG"), new JMenu("SOUNDBANK"), new JMenu("HELP")}; 
		//The menu inside each branch is created
		JMenuItem[] file = {new JMenuItem("New"), new JMenuItem("Open"), new JMenuItem("Save"), new JMenuItem("Save As"), new JMenuItem("Quit")};
		JMenuItem[] edit = {new JMenuItem("Copy"), new JMenuItem("Cut"), new JMenuItem("Paste"), new JMenuItem("Delete")};
		JMenuItem[] view = {new JMenuItem("Precision"), new JMenuItem("Zoom")};
		JMenuItem[] song = {new JMenuItem("Rename Song"), new JMenuItem("Set Tempo"), new JMenuItem("Set Length"), new JMenuItem("Set Time Signature")};
		JMenuItem[] soundbank = {new JMenuItem("Load Soundbank"), new JMenuItem("Set to Default")};
		JMenuItem[] help = {new JMenuItem("Tutorials")};
		
		//Initialization of menu items are grouped together
		for(byte i = 0; i < 6; i++)
		{
			menu[i].addActionListener(this);
			menuBar.add(menu[i]);
			//menus with less than 6 items
			if(i < 5)
			{
				file[i].addActionListener(this);
				menu[0].add(file[i]);
			}
			//menus with less than 5 items
			if(i < 4)
			{
				edit[i].addActionListener(this);
				menu[1].add(edit[i]);
				song[i].addActionListener(this);
				menu[3].add(song[i]);
			}
			//menus with less than 3 items
			if(i < 2)
			{
				view[i].addActionListener(this);
				soundbank[i].addActionListener(this);
				menu[2].add(view[i]);
				menu[4].add(soundbank[i]);
			}
			//menus with less than 2 items
			if(i < 1)
			{
				help[i].addActionListener(this);
				menu[5].add(help[i]);
			}
		}
		return menuBar;
	}
	
	//setTrackButtons(byte length) sets the track buttons / instrument lists according to the number of tracks in the song
	//byte length = number of tracks in the song
	public void setTrackButtons(byte length)
	{
		//Remove current set of buttons
		for(byte i = 0; i < Tracks.getButtonLength(); i++)
		{
			visual.remove(Tracks.getTrackEntryButton(i));
			visual.remove(Tracks.getInstrumentListButton(i));
		}
		Tracks.resetAllButtons();
		//Add the track buttons to specified length
		for(byte i = 0; i < length; i++)
		{
			addTrackButtons();
		}
	}
	
	//addInstrumentList() adds an instrument button to the interface
	public void addTrackButtons()
	{
		//If there are less then 16 tracks
		if(Tracks.isNotMaximum())
		{
			Tracks.addTrackButtons();
			Tracks.getTrackEntryButton(Tracks.getButtonLength() - 1).addActionListener(this);
			Tracks.getInstrumentListButton(Tracks.getButtonLength() - 1).addActionListener(this);
			visual.add(Tracks.getTrackEntryButton(Tracks.getButtonLength() - 1));
			visual.add(Tracks.getInstrumentListButton(Tracks.getButtonLength() - 1));
		}
	}
	
	//removeTrackButton(byte trackNum) removes the buttons from the GUI component
	//byte trackNum = button track being removed
	public static void removeTrackButton(byte trackNum)
	{
		visual.remove(Tracks.getTrackEntryButton(trackNum));
		visual.remove(Tracks.getInstrumentListButton(trackNum));
	}
	
	//mode() sets the components correctly to represent the current menu type
	//int newMode = the menu that is being set
	public void mode(int newMode)
	{
		mode = (byte)newMode;
		
		//Reset to selection
		selected = 0;
		
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
			visual.resizeComponents();
			
			//Tool bar
			toolBar.setVisible(true);
			toolBar.getTools(ToolBar.toolLength - 1).setVisible(false);
			
			//Track buttons
			for(byte i = 0; i < Tracks.getButtonLength(); i++)
			{
				Tracks.getTrackEntryButton(i).setVisible(true);
				Tracks.getInstrumentListButton(i).setVisible(true);
			}
			
			//Scroll bar
			scroll.setVisible(true);
			if(Tracks.getButtonLength() > Tracks.tracksVisible)	//If scroll bar is needed in list
				scroll.setValues(scrollY, (Tracks.trackHeight+5)*Tracks.tracksVisible, 0, (Tracks.trackHeight+5)*Tracks.getButtonLength());
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
		}
	}
	
	//resize() checks the size of the window containing the program
	public void resize()
	{
		//If window size does not equal the size in memory
		if(window.getWidth() - 16 != GUI.screenWidth || window.getHeight() - 62 != GUI.screenHeight)
		{
			GUI.screenWidth = (short) (window.getWidth() - 16);
			GUI.screenHeight = (short) (window.getHeight() - 62);
			
			//If mode is set to track editor or note editor
			if(mode > 0)
			{
				Tracks.tracksVisible = (byte)((GUI.screenHeight - GUI.fullAddHeight - info.getHeight() - 20)/(Tracks.trackHeight+Tracks.trackSpace));
				visual.resizeComponents();
				Tracks.resizeButtons();
				changeLimit();
			}
		}
	}
	
	//changeLimit() determining how large the scale can be
	public void changeLimit()
	{
		limit = 1;
		//repeat until length is a larger size than the window
		while((GUI.screenWidth - GUI.sideBarWidth) > limit*MIDISong.getLength())
			limit++;
	}
	
	//playPause() is the function for pause or playing the song
	public void playPause()
	{
		//If in note editor
		if(mode == 2)
			MIDISong.saveTrack(track);
		player.play(true);
		toolBar.changeIcon(MIDIPlayer.isPlaying());
		//If song is playing
		if(MIDIPlayer.isPlaying())
			player.setTickPosition(x/scale[0]);
	}
	
	//startStop() is the function for stopping or starting the song
	public void startStop()
	{
		//If in note editor
		if(mode == 2)
			MIDISong.saveTrack(track);
		player.setTickPosition(0);
		x = 0;
		player.play(true);
		toolBar.changeIcon(MIDIPlayer.isPlaying());
	}
	
	//add() is the function that adds an object
	public void add()
	{
		//track editor
		if(mode == 1)//ADD TRACK
		{
			MIDISong.addTrack();
			addTrackButtons();
			
			//If tracks exceed visibility limit
			if(Tracks.getButtonLength() > Tracks.tracksVisible)
				scroll.setValues(scrollY, (Tracks.trackHeight+5)*Tracks.tracksVisible, 0, (Tracks.trackHeight+5)*Tracks.getButtonLength());
			else
				scroll.setValues(0, 100, 0, 100);
			scrollY = visual.setComponentsOfScrollBar();
		}
		//note editor
		else if(mode == 2)//ADD NOTE
		{
			MIDISong.addNote(track, x/scale[0], (byte)(Notes.MAX_TONE - (y + (GUI.screenHeight - GUI.fullAddHeight)/2)/scale[1]), (byte)78, x/scale[0]+8);
		}
	}
	
	//delete() is the function that deletes an object
	public void delete()
	{
		//If in track editor
		if(mode == 1)//REMOVE TRACK
		{
			//Find selected track
			for(byte t = 0; t < MIDISong.getTracksLength(); t++)
			{
				if(MIDISong.getTracks(t).isSelected())
					MIDISong.deleteTrack(t);
			}
		}
		//Assumed to be note editor
		else//REMOVE NOTE
			MIDISong.removeSelectedNotes(track);
	}
	
	//skipLeft() translates the song and the grid to the left (amount varies)
	public void skipLeft()
	{
		//If in track editor
		if(mode == 1)
			player.setTickPosition(MIDIPlayer.getTickPosition() - (long)(MIDISong.getMeasureLength()*MIDISong.getTempoBpm()));
		//Assumed to be note editor
		else
		{
			x -= (GUI.screenWidth - GUI.sideBarWidth);
			gridLimits();
			if(MIDIPlayer.isPlaying())
				player.setTickPosition(x/scale[0]);
		}
	}
	
	//skipRight() translates the song and the grid to the Right (amount varies)
	public void skipRight()
	{
		//If in track editor
		if(mode == 1)
			player.setTickPosition(MIDIPlayer.getTickPosition() + (long)(MIDISong.getMeasureLength()*MIDISong.getTempoBpm()));
		//Assumed to be note editor
		else
		{
			x += (GUI.screenWidth - GUI.sideBarWidth);
			gridLimits();
			if(MIDIPlayer.isPlaying())
				player.setTickPosition(x/scale[0]);
		}
	}
	
	//save() saves the file with current settings and returns whether it was successful
	public boolean save()
	{
		//If in the note editor
		if(mode == 2)
			MIDISong.saveTrack(track);
		
		//If file has been given a name
		if(reader.isFileNamed())
		{
			reader.saveFile(MIDISong.saveSequence(), MIDIReader.getFile());
			NotifyAnimation.sendMessage("Notification","File saved...");
			return true;
		}
		else
			return saveAs();
	}
	
	//saveAs() asks for a name to be given to the file and saves it, it returns whether it was successful
	public boolean saveAs()
	{
		filter.setFilterMIDI(true);
		int v = fileIn.showSaveDialog(window);
		
		//If save file is approved
		if(v == JFileChooser.APPROVE_OPTION)
		{
			reader.saveFile(MIDISong.saveSequence(), fileIn.getSelectedFile());
			NotifyAnimation.sendMessage("Notification","File saved...");
			return true;
		}
		else
		{
			NotifyAnimation.sendMessage("invalid","Name is invalid. File was not saved.");
			return false;
		}
	}
	
	//close() turns off the program and saves config information
	public void close()
	{
		reader.saveConfig(window.getWidth(), window.getHeight(), window.getX(), window.getY(), GUI.getColourScheme());
		System.exit(1);
		//Program ends here
	}
	
	//actionPerformed(ActionEvent e) is the main direct listener for all components in the JFrame
	//ActionEvent e = event triggered containing event information
	public void actionPerformed(ActionEvent e) 
	{
		toolbarButtons(e);
		menuBarInput(e);
		
		//Track buttons
		for(byte i = 0; i < Tracks.getButtonLength(); i++)
		{
			//If source is equal to one of the track buttons
			if(e.getSource() == Tracks.getTrackEntryButton(i))
			{
				SelectableObject.unSelectAll();
				player.stop();
				track = i;
				MIDISong.openTrack(track);
				mode(2);
			}
			//If source is equal to one of the instrument combo boxes
			if(e.getSource() == Tracks.getInstrumentListButton(i))
			{
				player.setInstrument((byte)Tracks.getInstrumentListButton(i).getSelectedIndex(), i);
				MIDISong.getTracks(i).setInstrument((byte)Tracks.getInstrumentListButton(i).getSelectedIndex());
			}
		}
	}
	
	//toolbarButtons(ActionEvent e) responds to the button inputs in the tool bar
	//ActionEvent e = input information
	public void toolbarButtons(ActionEvent e)
	{
		//Tool #1: PLAY
		if(e.getSource() == toolBar.getTools(0))
		{
			playPause();
		}
		//Tool #2: STOP
		if(e.getSource() == toolBar.getTools(1))
		{
			startStop();
		}
		//Tool #3: ADD
		if(e.getSource() == toolBar.getTools(2))
		{
			add();
		}
		//Tool #4 REMOVE / DELETE
		if(e.getSource() == toolBar.getTools(3))
		{
			delete();
		}
		//Tool #5 SKIP LEFT
		if(e.getSource() == toolBar.getTools(4))
		{
			skipLeft();
		}
		//Tool #6 SKIP RIGHT
		if(e.getSource() == toolBar.getTools(5))
		{
			skipRight();
		}
		//Tool #7 SWAP TRACKS
		if(e.getSource() == toolBar.getTools(6))
		{
			//If in track editor
			if(mode == 1)
			{
				//If selected mode is neutral
				if(selected == 0)
				{
					//Check every track
					for(byte t = 0; t < MIDISong.getTracksLength(); t++)
					{
						//If track is selected
						if(MIDISong.getTracks(t).isSelected())
						{
							selected = (byte) (t + 1);
							NotifyAnimation.sendMessage("Notification", "Choose another track to switch with.");
						}
					}
				}
				else
				{
					selected = 0;
					NotifyAnimation.sendMessage("Notification", "Action was canceled");
				}
			}
		}
		//Tool #8 MERGE TRACKS
		if(e.getSource() == toolBar.getTools(7))
		{
			//If in track editor
			if(mode == 1)
			{
				//If selected mode is neutral
				if(selected == 0)
				{
					//Check every track
					for(byte t = 0; t < MIDISong.getTracksLength(); t++)
					{
						//If track is selected
						if(MIDISong.getTracks(t).isSelected())
						{
							selected = (byte) (t + 17);
							NotifyAnimation.sendMessage("Notification", "Choose the track to merge this track with.");
						}
					}
				}
				else
				{
					selected = 0;
					NotifyAnimation.sendMessage("Notification", "Action was canceled");
				}
			}
		}
		//Tool #9 TOGGLE INFOBAR
		if(e.getSource() == toolBar.getTools(8))
		{
			//If in track editor
			if(mode == 1)
			{
				//If info bar is visible
				if(info.isVisible())
					info.setVisibleAnimation(false);	
				else
					info.setVisibleAnimation(true);
			}
		}
		//Tool #10 SELECT ALL
		if(e.getSource() == toolBar.getTools(9))
		{
			//If in note editor
			if(mode == 2)
			{
				Notes.selectAllNotes();
				//selection mode set to multi-select
				selected = 2;
				NotifyAnimation.sendMessage("Notification", "All notes have been selected");
			}
		}
		//Tool #11: GO BACK
		if(e.getSource() == toolBar.getTools(ToolBar.toolLength - 1))
		{
			MIDISong.closeTrack(track);
			track = -1;
			mode(1);
		}
		//Colour Mode (Combo box in tool bar)
		if(e.getSource() == toolBar.getComboBox())
		{
			visual.setColourScheme((byte)toolBar.getComboBox().getSelectedIndex());
		}
	}
	
	//menuBarInput() responds to inputs made in the menu bar
	//ActionEvent e = input information
	public void menuBarInput(ActionEvent e)
	{
		//MenuBar -> File -> New
		if(e.getActionCommand().equals("New"))
		{
			player.stop();
			player.setTickPosition(0);
			MIDISong.setSong(reader.createFile());
			setTrackButtons((byte)1);
			mode(1);
		}
		//MenuBar -> File -> Open
		if(e.getActionCommand().equals("Open"))
		{
			player.stop();
			filter.setFilterMIDI(true);
			int v = fileIn.showOpenDialog(window);
			//If file is usable
			if (v == JFileChooser.APPROVE_OPTION) {
	            MIDISong.setSong(reader.readFile(fileIn.getSelectedFile()));
	            player.setAllInstruments();
				setTrackButtons(MIDISong.getTracksLength());
				mode(1);
				NotifyAnimation.sendMessage("Notification","Opening: "+MIDIReader.getFileName(0));
	        } else {
	        	NotifyAnimation.sendMessage("Notification","File was not be opened.");
	        }
		}
		//MenuBar -> File -> Save
		if(e.getActionCommand().equals("Save"))
		{
			//If a song has been set
			if(mode != 0)
			{
				save();
			}
		}
		//MenuBar -> File -> Save As
		if(e.getActionCommand().equals("Save As"))
		{
			//If a song has been set
			if(mode != 0)
			{
				//If in note editor (only save() automatically saves the track)
				if(mode == 2)
					MIDISong.saveTrack(track);
				saveAs();
			}
		}
		//MenuBar -> File -> Quit
		if(e.getActionCommand().equals("Quit"))
		{
			close();
		}
		//MenuBar -> Edit -> Copy
		if(e.getActionCommand().equals("Copy"))
		{
			Notes.copyNotes();
		}
		//MenuBar -> Edit -> Cut
		if(e.getActionCommand().equals("Cut"))
		{
			Notes.copyNotes();
			MIDISong.removeSelectedNotes(track);
		}
		//MenuBar -> Edit -> Paste
		if(e.getActionCommand().equals("Paste"))
		{
			Notes.pasteNotes(x/scale[0], (byte)0);
		}
		//MenuBar -> Edit -> Delete
		if(e.getActionCommand().equals("Delete"))
		{
			delete();
		}
		//MenuBar -> View -> Precision
		if(e.getActionCommand().equals("Precision"))
		{
			try
			{
				short p = Short.parseShort(JOptionPane.showInputDialog("How many ticks will appear on screen?", (GUI.screenWidth - GUI.sideBarWidth)/scale[0]));
				//If p is in a valid range (NOTE: gridLimits() will automatically restrict chosen value)
				if(p > 0)
					scale[0] = (short) ((GUI.screenWidth - GUI.sideBarWidth)/p);
				else
					NotifyAnimation.sendMessage("Invaild", "The value given for precision is invalid.");
			}catch(NumberFormatException ex){NotifyAnimation.sendMessage("Invaild", "The value given for precision is invalid.");}
		}
		//MenuBar -> View -> Zoom
		if(e.getActionCommand().equals("Zoom"))
		{
			try
			{
				short z = Short.parseShort(JOptionPane.showInputDialog("How many tones will be displayed on screen?", (GUI.screenHeight - GUI.fullAddHeight)/scale[1]));
				//If z is in a valid range (NOTE: gridLimits() will automatically restrict chosen value)
				if(z > 0)
					scale[1] = (short) ((GUI.screenHeight - GUI.fullAddHeight)/z);
				else
					NotifyAnimation.sendMessage("Invaild", "The value given for zoom is invalid.");
			}catch(NumberFormatException ex){NotifyAnimation.sendMessage("Invaild", "The value given for zoom is invalid.");}
		}
		//MenuBar -> Song -> Rename Song
		if(e.getActionCommand().equals("Rename Song"))
		{
			String s = JOptionPane.showInputDialog("What will you name the song?", MIDIReader.getFileName(0));
			
			//If name is not empty
			if(s.length() > 0)
				MIDIReader.setFileName(s);
			else
				NotifyAnimation.sendMessage("Invalid", "Name was not used.");
		}
		//MenuBar -> Song -> Set Tempo
		if(e.getActionCommand().equals("Set Tempo"))
		{
			try
			{
				int t = Integer.parseInt(JOptionPane.showInputDialog("What will be the new tempo (beats per minute)?", Math.round(MIDISong.getTempoBpm())));
				//If tempo is valid
				if(t > 0)
					//Due to the tempo being stored as microseconds per beat, a rounding error may occur
					MIDISong.setTempoBpm(t);
				else
					NotifyAnimation.sendMessage("Invaild", "The tempo value given is invalid.");
			}catch(NumberFormatException ex){NotifyAnimation.sendMessage("Invaild", "The tempo value given is invalid.");}
		}
		//MenuBar -> Song -> Set Length
		if(e.getActionCommand().equals("Set Length"))
		{
			try
			{
				int l = Integer.parseInt(JOptionPane.showInputDialog("How many measures should the song have?", MIDISong.getLength()/MIDISong.getMeasureLength()));
				//If length is greater than a reasonable amount (in this case the amount has to be greater than 640 ticks)
				if(l*MIDISong.getMeasureLength() > 640)
				{
					MIDISong.setLength(l);
					changeLimit();
				}
				else
					NotifyAnimation.sendMessage("Invaild", "The length given is either too small or invalid.");
			}catch(NumberFormatException ex){NotifyAnimation.sendMessage("Invaild", "The length given invalid.");}
		}
		//MenuBar -> Song -> Set Time Signature
		if(e.getActionCommand().equals("Set Time Signature"))
		{
			//Note only beats per measure can be changed
			try
			{
				byte t = Byte.parseByte(JOptionPane.showInputDialog("How many beats per measure should there be?", MIDISong.getBeatNum()));
				//If beats per measure is reasonable
				if(t > 0 && t <= 33)
				{
					MIDISong.setTimeSignature(t, (byte)4);
					NotifyAnimation.sendMessage("Notification", "The length of the song may not work with the time signature. Please adjust the length of the song if needed.");
				}
				else
					NotifyAnimation.sendMessage("Invaild", "The value given is either too large or invalid.");
			}catch(NumberFormatException ex){NotifyAnimation.sendMessage("Invaild", "The value given is invalid.");}
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
		//MenuBar -> Soundbank -> Set to Default
		if(e.getActionCommand().equals("Set to Default"))
		{
			player.setSoundBankDefault();
			NotifyAnimation.sendMessage("Notification","Opening: Default Soundbank");
		}
		//MenuBar -> Help -> Tutorials
		if(e.getActionCommand().equals("Tutorials"))
		{
			tutor.setUpPage();
		}
	}
	
	//keyControl() responds to key inputs in the program
	public void keyControl()
	{
		//Space (PLAY / PAUSE)
		if(key.getSpace())
		{
			playPause();
			key.setSpace(false);
		}
		//Enter (START / STOP)
		if(key.getEnter())
		{
			startStop();
			key.setEnter(false);
		}
		//Delete (DELETE)
		if(key.getDelete())
		{
			MIDISong.removeSelectedNotes(track);
			key.setDelete(false);
		}
		//A (SKIP LEFT)
		if(key.getLetters()[0])
		{
			skipLeft();
			key.setLetter(false, (byte) 0);
		}
		//C (COPY)
		if(key.getControl() && key.getLetters()[2])
		{
			Notes.copyNotes();
			key.setLetter(false, (byte) 2);
			key.setControl(false);
		}
		//D (SKIP RIGHT)
		if(key.getLetters()[3])
		{
			skipRight();
			key.setLetter(false, (byte) 3);
		}
		//S (MOVE DOWN)
		if(key.getLetters()[18])
		{
			y += (GUI.screenHeight - GUI.fullAddHeight)/2;
			gridLimits();
			key.setLetter(false, (byte) 18);
		}
		//V (PASTE)
		if(key.getControl() && key.getLetters()[21])
		{
			Notes.pasteNotes(x/scale[0], (byte)0);		
			key.setLetter(false, (byte) 21);
			key.setControl(false);
		}
		//W (MOVE UP)
		if(key.getLetters()[22])
		{
			y -= (GUI.screenHeight - GUI.fullAddHeight)/2;
			gridLimits();
			key.setLetter(false, (byte) 22);
		}
		//X (CUT)
		if(key.getControl() && key.getLetters()[23])
		{
			Notes.copyNotes();
			MIDISong.removeSelectedNotes(track);	
			key.setLetter(false, (byte) 23);
			key.setControl(false);
		}
	}
	
	//mouseControl() responds to mouse inputs recorded in the CursorListener class
	public void mouseControl()
	{
		//Note Editor
		if(mode == 2)
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
			//Check which track the mouse is selecting
			for(byte t = 0; t < MIDISong.getTracksLength(); t++)
			{
				//Left Click
				if(CursorListener.getClick() == 1)
				{
					player.muteTrack(MIDISong.getTracks(t).getSlider().setButton(true, MIDISong.getTracks(t).getSlider().buttonContains((short)(CursorListener.getLocation()[0] - GUI.mouseDisplacement), (short)(CursorListener.getLocation()[1] - GUI.windowBarHeight))), t);
					//If mouse is clicking on the volume slider
					if(MIDISong.getTracks(t).getSlider().contains((short)(CursorListener.getLocation()[0] - GUI.mouseDisplacement), (short)(CursorListener.getLocation()[1] - GUI.windowBarHeight)))
					{
						MIDISong.getTracks(t).getSlider().setValue((short)(CursorListener.getLocation()[0] - GUI.mouseDisplacement));
						MIDIPlayer.setVolume(t, MIDISong.getTracks(t).getVolume());
					}
					//If mouse is selecting the track window
					else if(CursorListener.getLocation()[0] - GUI.mouseDisplacement > 50 && CursorListener.getLocation()[0] - GUI.mouseDisplacement < GUI.screenWidth - 50 && CursorListener.getLocation()[1] - GUI.windowBarHeight > Tracks.trackSpace+GUI.toolBarHeight+(Tracks.trackHeight + 5)*t-MIDIMain.getScrollValue() && CursorListener.getLocation()[1] - GUI.windowBarHeight  < Tracks.trackSpace+GUI.toolBarHeight+(Tracks.trackHeight + 5)*t - MIDIMain.getScrollValue() + Tracks.trackHeight)
					{
						SelectableObject.unSelectAll();
						MIDISong.getTracks(t).selection(true);
						//If selection mode is set to merge tracks
						if(selected > 16)
						{
							MIDISong.mergeTrack((byte)(selected - 17), t);
							selected = 0;
						}
						//If selection mode is set to swap tracks
						else if(selected > 0)
						{
							MIDISong.moveTrack(t, (byte)(selected - 1));
							selected = 0;
						}
					}
				}
				else
					//Switches the mute button so that it will change states
					MIDISong.getTracks(t).getSlider().setButton(false, false);
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
				toolBar.resetVolume();
			}
			//If volume slider from tool bar extension is being clicked on
			else if(visual.getToolBar().getSlider().contains((short)(CursorListener.getLocation()[0] - GUI.mouseDisplacement), (short)(CursorListener.getLocation()[1] - GUI.windowBarHeight)))
			{
				visual.getToolBar().getSlider().setValue((short)(CursorListener.getLocation()[0] - GUI.mouseDisplacement));
				//Find the selected notes
				for(int i = 0; i < Notes.getNumNotes(); i++)
				{
					if(MIDISong.getNotes(track, i).isSelected())
					{
						MIDISong.getNotes(track, i).setVolume((byte)(127*visual.getToolBar().getSlider().getDecimal()));
					}
				}
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
			//Turns off any playing notes
			player.NoteOff();
		
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
				//Selecting process has completed
				Notes.selectContained(getSelectBox());
				toolBar.resetVolume();
				selected = 2;
			}
			else
				//Reset selection
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
				//gridX is the displacement of the mouse on the x-axis
				long gridX = CursorListener.getLocation()[0] - GUI.mouseDisplacement - GUI.sideBarWidth - CursorListener.getOrigin()[0];
				//gridY is the displacement of the mouse on the y-axis
				short gridY = (short)(y + CursorListener.getLocation()[1] - GUI.fullAddHeight - GUI.windowBarHeight);
				
				//Find all selected notes
				for(int i = 0; i < Notes.getNumNotes(); i++)
				{
					//If note is selected and is not the currently selected note
					if(MIDISong.getNotes(track, i).isSelected() && i != CursorListener.getObjectNumber())
					{
						//Checks if selected note is touching the edge of the boundaries
						if(gridX > 0 && gridX < MIDISong.getLength()*scale[0] - x)
						{
							MIDISong.getNotes(track, i).setLocation(gridX + (MIDISong.getNotes(MIDIMain.getTrackMenu(), i).getX() - MIDISong.getNotes(MIDIMain.getTrackMenu(), CursorListener.getObjectNumber()).getX()), 
								(short)(gridY + (MIDISong.getNotes(MIDIMain.getTrackMenu(), i).getY() - MIDISong.getNotes(MIDIMain.getTrackMenu(), CursorListener.getObjectNumber()).getY())));
						}
						else
							MIDISong.getNotes(track, i).setY((short)(gridY + (MIDISong.getNotes(MIDIMain.getTrackMenu(), i).getY() - MIDISong.getNotes(MIDIMain.getTrackMenu(), CursorListener.getObjectNumber()).getY())));
					}
				}
				//Currently selected note MUST move last in order for the notes to move together
				//Checks if selected note is touching the edge of the boundaries
				if(gridX > 0 && gridX < MIDISong.getLength()*scale[0] - x)
					MIDISong.getNotes(track, CursorListener.getObjectNumber()).setLocation(gridX, gridY);
				else
					MIDISong.getNotes(track, CursorListener.getObjectNumber()).setY(gridY);
			}
			//If volume slider from tool bar extension is being clicked on
			else if(visual.getToolBar().getSlider().contains((short)(CursorListener.getLocation()[0] - GUI.mouseDisplacement), (short)(CursorListener.getLocation()[1] - GUI.windowBarHeight)))
			{
				visual.getToolBar().getSlider().setValue((short)(CursorListener.getLocation()[0] - GUI.mouseDisplacement));
				//Find the selected notes
				for(int i = 0; i < Notes.getNumNotes(); i++)
				{
					if(MIDISong.getNotes(track, i).isSelected())
					{
						MIDISong.getNotes(track, i).setVolume((byte)(127*visual.getToolBar().getSlider().getDecimal()));
					}
				}
			}
			else
			{
				//Notes are unselected
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
				//Notes are unselected
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
			if(scale[0] >= limit && scale[1] > 5 && scale[0] <= 100 && scale[1] <= 100)
			{
				//grid zooms in on the location of the mouse
				x = (long) ((x + CursorListener.getLocation()[0] - GUI.sideBarWidth - GUI.mouseDisplacement)/(double)(scale[0] + CursorListener.getMouseWheel()*2)*scale[0] - (CursorListener.getLocation()[0] - GUI.sideBarWidth - GUI.mouseDisplacement));
				y = (short) ((y + CursorListener.getLocation()[1] - GUI.fullAddHeight - GUI.windowBarHeight)/(double)(scale[1] + CursorListener.getMouseWheel()*2)*scale[1] - (CursorListener.getLocation()[1] - GUI.fullAddHeight - GUI.windowBarHeight));
			}
			CursorListener.setMouseWheel((byte) 0);
		}
		
		gridLimits();
	}
	
	//gridLimits() limits the value that can be assigned to coordinate variables
	public void gridLimits()
	{
		//Limits to the scale
		if(scale[0] < limit)
			scale[0] = limit;
		if(scale[1] < 6)
			scale[1] = 6;
		if(scale[0] > 100)
			scale[0] = 100;
		if(scale[1] > 100)
			scale[1] = 100;
		
		//Limits to the coordinates
		if(x + (GUI.screenWidth - GUI.sideBarWidth) > MIDISong.getLength()*scale[0])
			//the right side of the screen cannot exceed length of the song
			x = MIDISong.getLength()*scale[0] - (GUI.screenWidth - GUI.sideBarWidth);
		if(x < 0)
			x = 0;
		if(y < 0)
			y = 0;
		if(y > 120*scale[1] - (GUI.screenHeight - GUI.fullAddHeight))
			//the bottom of the screen cannot exceed the tone values
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
		//Calculations have to be adjusted depending on the displacement of the mouse from an origin point
		
		//Horizontal mouse displacement is positive
		if(CursorListener.getLocation()[0] - CursorListener.getOrigin()[0] >= 0){
			//Upper left field
			if(CursorListener.getOrigin()[1] - CursorListener.getLocation()[1] >= 0)
				selectBox = new Rectangle(CursorListener.getOrigin()[0] - GUI.mouseDisplacement, CursorListener.getLocation()[1] - GUI.fullAddHeight, CursorListener.getLocation()[0] - CursorListener.getOrigin()[0], CursorListener.getOrigin()[1] - CursorListener.getLocation()[1]);
			//Upper right field
			else
				selectBox = new Rectangle(CursorListener.getOrigin()[0] - GUI.mouseDisplacement, CursorListener.getOrigin()[1] - GUI.fullAddHeight, CursorListener.getLocation()[0] - CursorListener.getOrigin()[0], CursorListener.getLocation()[1] - CursorListener.getOrigin()[1]);
		}
		//Horizontal mouse displacement is negative
		else{
			//Bottom left field
			if(CursorListener.getOrigin()[1] - CursorListener.getLocation()[1] >= 0)
				selectBox = new Rectangle(CursorListener.getLocation()[0] - GUI.mouseDisplacement, CursorListener.getLocation()[1] - GUI.fullAddHeight, CursorListener.getOrigin()[0] - CursorListener.getLocation()[0], CursorListener.getOrigin()[1] - CursorListener.getLocation()[1]);
			//Bottom right field
			else
				selectBox = new Rectangle(CursorListener.getLocation()[0] - GUI.mouseDisplacement, CursorListener.getOrigin()[1] - GUI.fullAddHeight, CursorListener.getOrigin()[0] - CursorListener.getLocation()[0], CursorListener.getLocation()[1] - CursorListener.getOrigin()[1]);
		}
		
		return selectBox;
	}
	
	//isSelecting() returns whether multiple notes are being selected
	public static boolean isSelecting()
	{
		//If selection mode is not neutral (in the process of selecting notes)
		if(selected == 1)
			return true;
		else
			return false;
	}
	
	//isInfoBarVisible() returns whether the info bar is being displayed on screen
	public static boolean isInfoBarVisible()
	{
		return info.isVisible();
	}

	//windowActivated(WindowEvent e) responds to when the window is given focus by the OS
	//WindowEvent e = event information
	public void windowActivated(WindowEvent e) {focus = true;}

	//windowClosed(WindowEvent e) responds after the program window is closed
	//WindowEvent e = event information
	public void windowClosed(WindowEvent e) {}

	//windowClosing(WindowEvent e) responds when the program window is closing
	//WindowEvent e = event information
	public void windowClosing(WindowEvent e) {
		if(mode > 0){
			int choice = JOptionPane.showConfirmDialog(window, "Would you like to save any changes?", "M.E.A.T is shutting down.", JOptionPane.YES_NO_CANCEL_OPTION);
			if(choice == JOptionPane.YES_OPTION){
				if(save())
					close();
			}
			else if(choice == JOptionPane.NO_OPTION)
				close();
		}
	}

	//windowDeactivated(WindowEvent e) responds to when the window loses focus from the OS
	//WindowEvent e = event information
	public void windowDeactivated(WindowEvent e) {focus = false;}

	//windowDeiconified(WindowEvent e) responds to when the window is returned to normal from minimized
	//WindowEvent e = event information
	public void windowDeiconified(WindowEvent e) {}

	//windowIconified(WindowEvent e) responds to when the window is minimized
	//WindowEvent e = event information
	public void windowIconified(WindowEvent e) {}

	//windowOpened(WindowEvent e) responds when the program window is opened
	//WindowEvent e = event information
	public void windowOpened(WindowEvent e) {}
}
