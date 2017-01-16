import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * <b>[Date: December 19, 2016]</b>
 * <p>
 * This class contains all elements required to create a second
 * window for the tutorials. The window allows different file 
 * types to be viewed, which make up each tutorial. The class
 * also provides its own JPanel and MouseListener.
 * </p>
 */
public class TutorialArchive extends JPanel implements ActionListener, MouseListener, WindowStateListener
{
	private static final long serialVersionUID = 1L;
	private JFrame tutorial;									//The window for the tutorials
	private JComboBox<String> sections;							//The combo box that is used to navigate the files
	private ArrayList<String> doc = new ArrayList<String>();	//The array of the lines of text in a document
	private ArrayList<File> files = new ArrayList<File>();		//The array of files that exist in a folder (when opening a slideshow)
	private short fade = 0;										//The value of the fade transition
	private byte max = 0;										//The amount of slides that exist in a slideshow
	private byte step = 0;										//The current slide of the slideshow
	private byte transition = 0;								//The current transition mode being executed
	private boolean paged = false;								//If file is a slideshow (is made up of multiple files)
	private boolean image = false;								//If file is an image
	private boolean fullscreen = false;							//If window is in fullscreen
		
	/**
	 * <blockquote>
	 * <p><pre>{@code public TutorialArchive()}</pre></p> 
	 * The constructor. The window starts by identifying all files in the tutorial folder.</p> 
	 */
	public TutorialArchive()
	{
		readFolder(new File("tutorial"));
		
		String[] s = new String[files.size()];
		//Storing the names of every tutorial files (then adding them to the combo box)
		for(int i = 0; i < s.length; i++)
		{
			s[i] = files.get(i).getName();
		}
		
		sections = new JComboBox<String>(s);
		sections.setBounds(10, 25, 200, 20);
		sections.setVisible(true);
		sections.addActionListener(this);
		sections.setFocusable(false);
		
		this.setPreferredSize(new Dimension(400,200));
		this.setLayout(null);
		this.add(sections);
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void setUpPage()}</pre></p> 
	 * The display is reseted (this method is reusable). It sets up all components upon the reset.</p> 
	 */
	public void setUpPage()
	{
		//If the window had already been opened / created
		if(isActive())
			tutorial.dispose();
		tutorial = new JFrame("Tutorials");
		tutorial.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		tutorial.setResizable(true);
		tutorial.setLocation(0, 0);
		tutorial.add(this);
		tutorial.addMouseListener(this);
		tutorial.addWindowStateListener(this);
		tutorial.pack();
		tutorial.setVisible(true);
		tutorial.setBackground(Color.WHITE);
		//Sets the window to a default file
		sections.setSelectedIndex(0);
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void readDocument(File file)}</pre></p> 
	 * A file is identified as a document and is read.</p> 
	 * @param file = file being read
	 */
	public void readDocument(File file)
	{
		doc.clear();
		
		//If file extension is a text document
		if(file.getName().endsWith(".txt"))
		{
			//File is read
			try {
				BufferedReader read = new BufferedReader(new FileReader(file));
				
				String s = null;
				//lines of text are read until the document ends
				do
				{
					s = read.readLine();
					//If not at end of document, then add line of text
					if(!s.equals("END"))
						doc.add(s);
				}while(!s.equals("END"));
				
				read.close();
			} catch (FileNotFoundException e) {NotifyAnimation.sendMessage("Error", "Document could not be read.");
			} catch (IOException e) {NotifyAnimation.sendMessage("Error", "Document list could not be read.");
			} catch (NullPointerException e) {NotifyAnimation.sendMessage("Error", "Document list could not be read.");}
		}
		else
		{
			//If file is an image
			if(file.getName().endsWith(".png"))
			{
				image = true;
				doc.add(file.getPath());
			}
			//If file cannot be identified
			else
			{
				doc.add("Error: File could not be read");
			}
		}
	}

	/**
	 * <blockquote>
	 * <p><pre>{@code public void readFile(File file)}</pre></p> 
	 * Identifies if a the file is a folder or not and decides how to read it.</p> 
	 * @param file = file being read
	 */
	public void readFile(File file)
	{
		image = false;
		max = 0;
		step = 0;
		if(file.isFile())
		{
			paged = false;
			readDocument(file);
		}
		else
		{
			countFolder(file);
			paged = true;
			readDocument(readForPage(file, (byte)0));
		}
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public File readForPage(File folder, byte page) }</pre></p> 
	 * A specified file inside a folder is retrieved.</p> 
	 * @param folder = folder being read
	 * @param page = the index of the file being retrieved
	 * @return The file retrieved 
	 */
	public File readForPage(File folder, byte page) 
	{
		//Lists all files and the loops through them
		for (File fileEntry : folder.listFiles()) 
	    {
			//If the page has reached 0 (the file is found)
			if(page == 0)
				return fileEntry;
	        page--;
	    }
		//If file does not exist
		return folder;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void readFolder(File folder)}</pre></p> 
	 * Adds files from a folder to the file array.</p> 
	 * @param folder = folder being read
	 */
	public void readFolder(File folder) 
	{
		//If folder exists
		if(folder.exists())
		{
			//Goes through every file in the folder
		    for (File fileEntry : folder.listFiles()) 
		    {
		        files.add(fileEntry);
		    }
		}
		else
		{
			NotifyAnimation.sendMessage("Error", "The tutorial folder could not be located.");
		}
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void countFolder(File folder)}</pre></p> 
	 * Counts the number of files in the folder.</p> 
	 * @param folder = folder being read
	 */
	public void countFolder(File folder)
	{
		for (File fileEntry : folder.listFiles()) 
	    {
			//If file is not folder / directory
			if (!fileEntry.isDirectory())
	        	max++;
	    }
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public boolean isActive()}</pre></p> 
	 * Determines if window is active.</p> 
	 * @return Whether the window is active
	 */
	public boolean isActive()
	{
		//Tests to see if the tutorial window exists
		if(tutorial == null)
			return false;
		else
			return true;
	}
	
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public boolean isTransition()}</pre></p> 
	 * Determines if window is active.</p> 
	 * @return Whether the slideshow / paged document is changing pages
	 */
	public boolean isTransition()
	{
		//If not fading in or out
		if(transition == 0)
			return false;
		else
			return true;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public boolean isTransition()}</pre></p> 
	 * Runs the transition between screens, the file is loaded in between the fade in and fade out.</p> 
	 */
	public void fadePage()
	{
		//If fading out
		if(transition == 1)
		{
			fade += 8;
			
			//When the fade out completes
			if(fade >= 255)
			{
				fade = 255;
				transition = 2;
				readDocument(readForPage(files.get(sections.getSelectedIndex()), step));
			}
			this.repaint();
		}
		//If fading back in
		else if(transition == 2)
		{
			fade -= 8;
			
			//When the fade in completes
			if(fade <= 0)
			{
				fade = 0;
				transition = 0;
			}
			this.repaint();
		}
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void paintComponent(Graphics g)}</pre></p> 
	 * Responds to the .repaint() method when used.</p> 
	 * @param g = component of the JPanel used to create visual elements
	 */
	public void paintComponent(Graphics g) 
	{
		int space = 0;		//Space is used to keep track how much of the screen is taken up
		int width = 400;	//width refers to how wide the window needs to be
		
		if(!fullscreen)
			space += 50;
		
		//If file is an image
		if(image)
		{
			try {
				//Reads file as image
				BufferedImage image = ImageIO.read(new File(doc.get(0)));
				if(fullscreen)
				{
					//Background
					g.setColor(Color.BLACK);
					g.fillRect(0, 0, tutorial.getHeight(), tutorial.getWidth());
					
					//Centers image horizontally
					space += (tutorial.getWidth() - image.getWidth()) / 2;		
					
					int height = -20;	//height determines vertical location of image (-20 represents window bar at the top)
					//If image fits onto screen (determines height of the screen)
					if(Toolkit.getDefaultToolkit().getScreenSize().getHeight() > image.getHeight())
						//Centers image vertically
						height = (tutorial.getHeight() - image.getHeight()) / 2;
					//If image is too high, the program tries to fit as much of it on screen
					
					g.drawImage(image, space, height, image.getWidth(), image.getHeight(), this);
				}
				else
				{
					g.drawImage(image, 0, space, image.getWidth(), image.getHeight(), this);
					space += image.getHeight();
					width = image.getWidth();
				}
			} catch (IOException e) {};
		}
		//If file is a document
		else
		{
			g.setColor(Color.WHITE);
			g.fillRect(0, space, 400, 50 + doc.size()*15);
			g.setColor(Color.BLACK);
			space += 15;
			
			//If document is not empty
			if(!doc.isEmpty())
			{
				g.setFont(GUI.defaultFont);
				//Reads each line of the document
				
				for(int i = 0; i < doc.size(); i++)
				{
					String s = doc.get(i);
					//Checks for notation in order to draw an image
					if(s.contains("<"))
					{
						space = decodeImageText(g, s.substring(s.indexOf('<') + 1, s.indexOf('>')), space);
						s = s.replace(s.substring(s.indexOf('<'), s.indexOf('>') + 1), "");
					}
					g.drawString(s, 20, space);
					space += 15;
				}
			}
		}
	
		//If not in fullscreen
		if(!fullscreen)
		{
			g.setColor(GUI.colours[GUI.getColourScheme()][4]);
			g.fillRect(0, 0, width, 50);
			
			g.setFont(GUI.boldFont);
			g.setColor(Color.BLACK);
			g.drawString("SELECT AN ARCHIVE", 20, 15);
			
			//space and width determine the size of the screen
			tutorial.setSize(width, space + 20);
			
			space = 50;
			sections.setVisible(true);
		}
		else
		{
			space = 0;
			sections.setVisible(false);
		}
		
		//If in screen transition
		if(fade > 0)
		{
			g.setColor(new Color(0, 0, 0, fade));
			g.fillRect(0, space, tutorial.getWidth(), tutorial.getHeight());
		}
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public int decodeImageText(Graphics g, String text, int space)}</pre></p> 
	 * Decodes the notation for reading an image.</p> 
	 * @param g = component of the JPanel used to create visual elements
	 * @param text = notation taken from the original text (usually written as "<{indent}image>")
	 * @param space = location to draw image
	 * @return The new value of space after drawing the image
	 */
	public int decodeImageText(Graphics g, String text, int space)
	{
		short indent = 0; //The amount the image is placed from the left side of the window
		try {
			//If notation is identified
			if(text.contains("{"))
			{
				indent = Short.parseShort(text.substring(text.indexOf('{') + 1, text.indexOf('}')));
				text = text.replace(text.substring(text.indexOf('{'), text.indexOf('}') + 1), "");
			}
			//image is read and drawn
			BufferedImage image = ImageIO.read(new File(text));
			g.drawImage(image, indent, space, image.getWidth(), image.getHeight(), this);
			space += image.getHeight();
		} catch (IOException e) {}
		return space;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void actionPerformed(ActionEvent e)}</pre></p> 
	 * Responds to the combo box when the file is switched.</p> 
	 * @param e = event information
	 */
	public void actionPerformed(ActionEvent e) 
	{
		//If event sources from the sections combo box
		if(e.getSource().equals(sections))
		{
			//Trys to read file (file may have been deleted while program is running)
			try{
				readFile(files.get(sections.getSelectedIndex()));
			}catch(NullPointerException ex){doc.add("Error: File could not be read");}
			//Only needs to repaint one time after reading a file
			this.repaint();
		}
	}

	/**
	 * <blockquote>
	 * <p><pre>{@code public void mouseClicked(MouseEvent e)}</pre></p> 
	 * Responds to mouse clicks in the program window.</p> 
	 * @param e = mouse event information
	 */
	public void mouseClicked(MouseEvent e)
	{
		//If left clicked (Next slide)
		if(e.getButton() == MouseEvent.BUTTON1)
		{
			//If file is paged (slideshow) && If not on last page
			if(paged == true && step < max - 1)
			{
				step++;
				//Trigger transition
				transition = 1;
				fade = 0;
			}
		}
		//If right clicked (Back a slide)
		if(e.getButton() == MouseEvent.BUTTON3)
		{
			//If file is paged (slideshow) & If not on the first page
			if(paged == true && step > 0)
			{
				step--;
				//Trigger transition
				transition = 1;
				fade = 0;
			}
		}
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void mouseEntered(MouseEvent e)}</pre></p> 
	 * Responds to the cursor entering the window.</p> 
	 * @param e = information of the mouse event
	 */
	public void mouseEntered(MouseEvent e){
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void mouseExited(MouseEvent e)}</pre></p> 
	 * Responds to the cursor exiting the window.</p> 
	 * @param e = information of the mouse event
	 */
	public void mouseExited(MouseEvent e){
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void mousePressed(MouseEvent e)}</pre></p> 
	 * Responds to any digital input on the mouse being held.</p> 
	 * @param e = information of the mouse event
	 */
	public void mousePressed(MouseEvent e){
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void mouseReleased(MouseEvent e)}</pre></p> 
	 * Responds to any inputs on the mouse being released.</p> 
	 * @param e = information of the mouse event
	 */
	public void mouseReleased(MouseEvent e){
	}

	/**
	 * <blockquote>
	 * <p><pre>{@code public void windowStateChanged(WindowEvent e)}</pre></p> 
	 * Responds to the change in size when the window is resized.</p> 
	 * @param e = information of the window event
	 */
	public void windowStateChanged(WindowEvent e)
	{
		//If screen is at maximum size (fullscreen)
		if(e.getNewState() == JFrame.MAXIMIZED_BOTH)
		{
			fullscreen = true;
			tutorial.setExtendedState(JFrame.MAXIMIZED_BOTH);
		}
		else
		{
			fullscreen = false;
		}
		repaint();
	}
}
