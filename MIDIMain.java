import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.*;
import java.awt.event.*;

public class MIDIMain implements ActionListener
{
	/**
	 * Adobe kuler (color scheme website)
	 * 
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
	
	private JFrame window = new JFrame("M.E.A.T.");		//The window the components are displayed on
	//Midi Editor Accessing Tool
	private static GUI visual = new GUI();							//The drawing component used to display most graphics
	private CursorListener mouse = new CursorListener();	//Mouse Listener used to register mouse movement and inputs
	private KeyboardListener key = new KeyboardListener();	//Key Listener used to register key inputs
	private JFileChooser fileIn = new JFileChooser();		//The file directory that opens when choosing a file
	private MIDIFilter filter = new MIDIFilter();			//The file filter for the JFileChooser
	private MIDIPlayer player = new MIDIPlayer();			//The class that can play midi files
	private MIDIReader reader = new MIDIReader();			//The class that reads and creates midi files
	private static ToolBar toolBar;							//The tool bar that holds the buttons for use in the editors
	private static JScrollBar scroll;						//The scroll bar used in the track editor
	private static InfoBar info;							//The song information bar
	
	private static byte selected = 0;						//The selection mode (0 = single | 1 = selecting notes | 2 = selected notes)
	private static Rectangle selectBox;						//The rectangle that is used to select notes
	
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
			resize();
			mouseControl();
			keyControl();
			if(scroll.getValue() != scrollY)
				scrollY = visual.setComponentsOfScrollBar();
			
			//Process
			if(mode == 1)
				Tracks.trackLayout();
			if(mode >= 2 && player.isPlaying())
				x = scale[0]*player.getTickPosition();
			
			//Outputs
			window.repaint();
			
			//Pause (Repeat)
			pause(10);
			window.setFocusable(true);
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
        window.setMinimumSize(new Dimension(700, 360));
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
		JMenu[] menu = {new JMenu("FILE"), new JMenu("EDIT"), new JMenu("VIEW"), new JMenu("SONG"), new JMenu("SOUNDBANK"), new JMenu("HELP")}; 
		//The menu inside each branch is created
		JMenuItem[] file = {new JMenuItem("New"), new JMenuItem("Open"), new JMenuItem("Save"), new JMenuItem("Save As"), new JMenuItem("Quit")};
		JMenuItem[] edit = {new JMenuItem("Copy"), new JMenuItem("Cut"), new JMenuItem("Paste"), new JMenuItem("Delete")};
		JMenuItem[] view = {new JMenuItem("Precision"), new JMenuItem("Zoom")};
		JMenuItem[] song = {new JMenuItem("Rename Song"), new JMenuItem("Set Tempo"), new JMenuItem("Set Length"), new JMenuItem("Set Time Signature")};
		JMenuItem[] soundbank = {new JMenuItem("Load Soundbank"), new JMenuItem("Set to Default")};
		JMenuItem[] help = {new JMenuItem("Terms"), new JMenuItem("Tutorials")};
		
		//Initialization of menu items are grouped together
		for(byte i = 0; i < 6; i++)
		{
			menu[i].addActionListener(this);
			menuBar.add(menu[i]);
			if(i < 5)
			{
				file[i].addActionListener(this);
				menu[0].add(file[i]);
			}
			if(i < 4)
			{
				edit[i].addActionListener(this);
				menu[1].add(edit[i]);
				song[i].addActionListener(this);
				menu[3].add(song[i]);
			}
			if(i < 2)
			{
				view[i].addActionListener(this);
				soundbank[i].addActionListener(this);
				help[i].addActionListener(this);
				menu[2].add(view[i]);
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
	
	//removeTrackButton(byte trackNum) removes the buttons from the GUI component
	//byte trackNum = button track being removed
	public static void removeTrackButton(byte trackNum)
	{
		visual.remove(Tracks.getTrackEntryButton(trackNum));
		visual.remove(Tracks.getInstrumentListButton(trackNum));
	}
	
	//Mode() sets the components correctly to represent the current menu type
	public void mode()
	{
		//Overall reset to controls
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
		if(window.getWidth() - 16 != GUI.screenWidth || window.getHeight() - 62 != GUI.screenHeight)
		{
			GUI.screenWidth = (short) (window.getWidth() - 16);
			GUI.screenHeight = (short) (window.getHeight()- 62);
			
			if(mode > 0)
			{
				Tracks.tracksVisible = (byte)((GUI.screenHeight - GUI.fullAddHeight - info.getHeight() - 20)/(Tracks.trackHeight+Tracks.trackSpace));
				visual.resizeComponents();
				Tracks.resizeButtons();
			}
		}
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
	
	//toolbarButtons(ActionEvent e) responds to the button inputs in the toolbar
	//ActionEvent e = input information
	public void toolbarButtons(ActionEvent e)
	{
		//Tool #1: PLAY
		if(e.getSource() == toolBar.getTools(0))
		{
			//If in note editor
			if(mode == 2)
				MIDISong.saveTrack(track);
			player.play(true);
			//If Song is playing
			if(player.isPlaying())
				player.setTickPosition(x/scale[0]);
		}
		//Tool #2: STOP
		if(e.getSource() == toolBar.getTools(1))
		{
			//If in note editor
			if(mode == 2)
				MIDISong.saveTrack(track);
			player.setTickPosition(0);
			player.play(true);
			x = 0;
		}
		//Tool #3: ADD
		if(e.getSource() == toolBar.getTools(2))
		{
			//track editor
			if(mode == 1)
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
			else if(mode == 2)
			{
				MIDISong.addNote(track, x/scale[0], (byte)(Notes.MAX_TONE - (y + (GUI.screenHeight - GUI.fullAddHeight)/2)/scale[1]), (byte)78, x/scale[0]+8);
			}
		}
		//Tool #4 REMOVE / DELETE
		if(e.getSource() == toolBar.getTools(3))
		{
			if(mode == 1)
			{
				for(byte t = 0; t < MIDISong.getTracksLength(); t++)
				{
					if(MIDISong.getTracks(t).isSelected())
						MIDISong.deleteTrack(t);
				}
			}
			if(mode == 2)
				MIDISong.removeSelectedNotes(track);
		}
		//Tool #5 SKIP LEFT
		if(e.getSource() == toolBar.getTools(4))
		{
			x -= (GUI.screenWidth - GUI.sideBarWidth);
			gridLimits();
			if(player.isPlaying())
				player.setTickPosition(x/scale[0]);
		}
		//Tool #6 SKIP RIGHT
		if(e.getSource() == toolBar.getTools(5))
		{
			x += (GUI.screenWidth - GUI.sideBarWidth);
			gridLimits();
			if(player.isPlaying())
				player.setTickPosition(x/scale[0]);
		}
		//Tool #7 SHIFT UP
		if(e.getSource() == toolBar.getTools(6))
		{
			if(mode == 1)
			{
				//Cannot shift the first track in a sequence
				for(byte t = 1; t < MIDISong.getTracksLength(); t++)
				{
					//If track is selected
					if(MIDISong.getTracks(t).isSelected())
					{
						MIDISong.moveTrack(t, (byte)(t-1));
						break;
					}
				}
			}
			if(mode == 2)
			{
				for(byte n = 0; n < Notes.getNumNotes(); n++)
				{
					//If note is selected
					if(MIDISong.getNotes(track, n).isSelected())
					{
						MIDISong.getNotes(track, n).setTone((byte)(MIDISong.getNotes(track, n).getTone() + 1));
					}
				}
			}
		}
		//Tool #8 SHIFT DOWN
		if(e.getSource() == toolBar.getTools(7))
		{
			if(mode == 1)
			{
				//Cannot shift the last track in a sequence
				for(byte t = 0; t < MIDISong.getTracksLength() - 1; t++)
				{
					//If track is selected
					if(MIDISong.getTracks(t).isSelected())
					{
						MIDISong.moveTrack(t, (byte)(t+1));
						break;
					}
				}
			}
			if(mode == 2)
			{
				for(byte n = 0; n < Notes.getNumNotes(); n++)
				{
					//If note is selected
					if(MIDISong.getNotes(track, n).isSelected())
					{
						MIDISong.getNotes(track, n).setTone((byte)(MIDISong.getNotes(track, n).getTone() - 1));
					}
				}
			}
		}
		//Tool #9 TOGGLE INFOBAR
		if(e.getSource() == toolBar.getTools(8))
		{
			if(info.isVisible())
				info.setVisibleAnimation(false);	
			else
				info.setVisibleAnimation(true);
		}
		//Tool #10 SELECT ALL
		if(e.getSource() == toolBar.getTools(9))
		{
			Notes.selectAllNotes();
			//selection mode set to multi-select
			selected = 2;
		}
		//Tool #11: GO BACK
		if(e.getSource() == toolBar.getTools(ToolBar.toolLength - 1))
		{
			MIDISong.closeTrack(track);
			track = -1;
			mode = 1;
			mode();
		}
	}
	
	//menuBarInput() responds to inputs made in the menu bar
	//ActionEvent e = input information
	public void menuBarInput(ActionEvent e)
	{
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
				NotifyAnimation.sendMessage("Notification","Opening: "+MIDIReader.getFileName(0));
	        } else {
	        	NotifyAnimation.sendMessage("Notification","File was not be opened.");
	        }
		}
		//MenuBar -> File -> Save
		if(e.getActionCommand().equals("Save"))
		{
			if(mode == 2)
				MIDISong.saveTrack(track);
			reader.saveFile(MIDISong.saveSequence(), MIDIReader.getFileName(0));
			NotifyAnimation.sendMessage("Notification","File saved...");
		}
		//MenuBar -> File -> Save As
		if(e.getActionCommand().equals("Save As"))
		{
			if(mode == 2)
				MIDISong.saveTrack(track);
			String s = JOptionPane.showInputDialog("What will you save the file as?", MIDIReader.getFileName(0));
			
			if(!s.startsWith("") || s.length() > 0)
			{
				MIDIReader.setFileName(s);
				reader.saveFile(MIDISong.saveSequence(), s);
				NotifyAnimation.sendMessage("Notification","File saved...");
			}
			else
			{
				NotifyAnimation.sendMessage("invalid","Name is invalid. File was not saved.");
			}
		}
		//MenuBar -> File -> Quit
		if(e.getActionCommand().equals("Quit"))
		{
			System.exit(1);
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
			MIDISong.removeSelectedNotes(track);
		}
		//MenuBar -> View -> Precision
		if(e.getActionCommand().equals("Precision"))
		{
			try
			{
				short p = Short.parseShort(JOptionPane.showInputDialog("How many ticks will appear on screen? (6 - 620)", (GUI.screenWidth - GUI.sideBarWidth)/scale[0]));
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
			if(!s.startsWith("") || s.length() > 0)
				MIDIReader.setFileName(s);
		}
		//MenuBar -> Song -> Set Tempo
		if(e.getActionCommand().equals("Set Tempo"))
		{
			try
			{
				long t = Long.parseLong(JOptionPane.showInputDialog("What will be the new tempo (micorseconds per beat)?", MIDISong.getLength()/MIDISong.getMeasureLength()));
				if(t > 0)
					MIDISong.setTempo(t);
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
				if(l*MIDISong.getMeasureLength() > 640)
					MIDISong.setLength(l);
				else
					NotifyAnimation.sendMessage("Invaild", "The length given is either too small or invalid.");
			}catch(NumberFormatException ex){NotifyAnimation.sendMessage("Invaild", "The length given invalid.");}
		}
		//MenuBar -> Song -> Set Time Signature
		if(e.getActionCommand().equals("Set Time Signature"))
		{
			try
			{
				byte t = Byte.parseByte(JOptionPane.showInputDialog("How many beats per measure should there be?", MIDISong.getBeatNum()));
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
	}
	
	//keyControl() responds to key inputs in the program
	public void keyControl()
	{
		//Space
		if(key.getSpace())
		{
			//If in note editor
			if(mode == 2)
				MIDISong.saveTrack(track);
			player.play(true);
			//If Song is playing
			if(player.isPlaying())
				player.setTickPosition(x/scale[0]);
			key.setSpace(false);
		}
		//Delete
		if(key.getDelete())
		{
			MIDISong.removeSelectedNotes(track);
			key.setDelete(false);
		}
		//A
		if(key.getLetters()[0])
		{
			x -= (GUI.screenWidth - GUI.sideBarWidth);
			gridLimits();
			if(player.isPlaying())
				player.setTickPosition(x/scale[0]);
			key.setLetter(false, (byte) 0);
		}
		//C
		if(key.getControl() && key.getLetters()[2])
		{
			Notes.copyNotes();
			key.setLetter(false, (byte) 2);
			key.setControl(false);
		}
		//D
		if(key.getLetters()[3])
		{
			x += (GUI.screenWidth - GUI.sideBarWidth);
			gridLimits();
			if(player.isPlaying())
				player.setTickPosition(x/scale[0]);
			key.setLetter(false, (byte) 3);
		}
		//V
		if(key.getControl() && key.getLetters()[21])
		{
			Notes.pasteNotes(x/scale[0], (byte)0);		
			key.setLetter(false, (byte) 21);
			key.setControl(false);
		}
		//X
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
			for(byte t = 0; t < MIDISong.getTracksLength(); t++)
			{
				//Left Click
				if(CursorListener.getClick() == 1)
				{
					if(CursorListener.getLocation()[0] - GUI.mouseDisplacement > 50 && CursorListener.getLocation()[0] - GUI.mouseDisplacement < GUI.screenWidth - 50 && CursorListener.getLocation()[1] - GUI.windowBarHeight > Tracks.trackSpace+GUI.toolBarHeight+(Tracks.trackHeight + 5)*t-MIDIMain.getScrollValue() && CursorListener.getLocation()[1] - GUI.windowBarHeight  < Tracks.trackSpace+GUI.toolBarHeight+(Tracks.trackHeight + 5)*t - MIDIMain.getScrollValue() + Tracks.trackHeight)
					{
						SelectableObject.unSelectAll();
						MIDISong.getTracks(t).selection(true);
					}
					if(MIDISong.getTracks(t).getSlider().contains((short)(CursorListener.getLocation()[0] - GUI.mouseDisplacement), (short)(CursorListener.getLocation()[1] - GUI.windowBarHeight)))
					{
						MIDISong.getTracks(t).getSlider().setValue((short)(CursorListener.getLocation()[0] - GUI.mouseDisplacement));
						player.setVolume(t, MIDISong.getTracks(t).getVolume());
					}
					player.muteTrack(MIDISong.getTracks(t).getSlider().setButton(true, MIDISong.getTracks(t).getSlider().buttonContains((short)(CursorListener.getLocation()[0] - GUI.mouseDisplacement), (short)(CursorListener.getLocation()[1] - GUI.windowBarHeight))), t);
				}
				else
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
			}
			else if(visual.getExtension().getSlider().contains((short)(CursorListener.getLocation()[0] - GUI.mouseDisplacement), (short)(CursorListener.getLocation()[1] - GUI.windowBarHeight)))
			{
				visual.getExtension().getSlider().setValue((short)(CursorListener.getLocation()[0] - GUI.mouseDisplacement));
				for(int i = 0; i < Notes.getNumNotes(); i++)
				{
					if(MIDISong.getNotes(track, i).isSelected())
					{
						MIDISong.getNotes(track, i).setVolume((byte)(127*visual.getExtension().getSlider().getDecimal()));
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
			else if(visual.getExtension().getSlider().contains((short)(CursorListener.getLocation()[0] - GUI.mouseDisplacement), (short)(CursorListener.getLocation()[1] - GUI.windowBarHeight)))
			{
				visual.getExtension().getSlider().setValue((short)(CursorListener.getLocation()[0] - GUI.mouseDisplacement));
				for(int i = 0; i < Notes.getNumNotes(); i++)
				{
					if(MIDISong.getNotes(track, i).isSelected())
					{
						MIDISong.getNotes(track, i).setVolume((byte)(127*visual.getExtension().getSlider().getDecimal()));
					}
				}
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
		if(x + (GUI.screenWidth - GUI.sideBarWidth) > MIDISong.getLength()*scale[0])
			x = MIDISong.getLength()*scale[0] - (GUI.screenWidth - GUI.sideBarWidth);
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
	
	//isInfoBarVisible()
	public static boolean isInfoBarVisible()
	{
		return info.isVisible();
	}
}
