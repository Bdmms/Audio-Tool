import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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

public class TutorialArchive extends JPanel implements ActionListener, MouseListener
{
	private static final long serialVersionUID = 1L;
	private static JFrame tutorial;
	private JComboBox<String> sections;
	private ArrayList<String> doc = new ArrayList<String>();
	private ArrayList<File> files = new ArrayList<File>();
	private byte max = 0;
	private byte step = 0;
	private boolean paged = false;
	private boolean image = false;
	
	public TutorialArchive()
	{
		readFolder(new File("tutorial"));
		
		String[] s = new String[files.size()];
		for(int i = 0; i < s.length; i++)
		{
			s[i] = files.get(i).getName();
		}
		sections = new JComboBox<String>(s);
		sections.setBounds(10, 25, 200, 20);
		sections.setVisible(true);
		sections.addActionListener(this);
		
		this.setPreferredSize(new Dimension(400,200));
		this.setLayout(null);
		this.add(sections);
	}
	
	public void setUpPage()
	{
		if(isActive())
			tutorial.dispose();
		tutorial = new JFrame("Tutorials");
		tutorial.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		tutorial.setSize(400, 200);
		tutorial.setResizable(false);
		tutorial.setLocation(0, 0);
		tutorial.add(this);
		tutorial.addMouseListener(this);
		tutorial.pack();
		tutorial.setAlwaysOnTop(true);
		tutorial.setVisible(true);
		tutorial.setBackground(Color.WHITE);
		
		sections.setSelectedIndex(0);
	}
	
	public void readDocument(File file)
	{
		doc.clear();
		
		if(file.getName().endsWith(".txt"))
		{
			BufferedReader read;
			
			try {
				read = new BufferedReader(new FileReader(file));
				
				String s = null;
				do
				{
					s = read.readLine();
					if(!s.equals("END"))
						doc.add(s);
				}while(!s.equals("END"));
				
				read.close();
				
			} catch (FileNotFoundException e) {NotifyAnimation.sendMessage("Error", "Instrument list could not be read.");
			} catch (IOException e) {NotifyAnimation.sendMessage("Error", "Instrument list could not be read.");}
		}
		else
		{
			image = true;
			doc.add(file.getPath());
		}
	}

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
	
	public File readForPage(File folder, byte page) 
	{
		for (File fileEntry : folder.listFiles()) 
	    {
			if(page == 0)
				return fileEntry;
	        page--;
	    }
		return folder;
	}
	
	public void readFolder(File folder) 
	{
		if(folder.exists())
		{
		    for (File fileEntry : folder.listFiles()) 
		    {
		        files.add(fileEntry);
		    }
		}
		else
		{
			NotifyAnimation.sendMessage("Error", "The tutorial folder cannot be located.");
		}
	}
	
	public void countFolder(File folder)
	{
		for (File fileEntry : folder.listFiles()) 
	    {
			if (!fileEntry.isDirectory())
	        	max++;
	    }
	}
	
	public boolean isActive()
	{
		try
		{
			tutorial.isVisible();
			return true;
		}
		catch(Exception e){return false;}
	}
	
	//paintComponent(Graphics g) responds to the .repaint() method when used
	//Graphics g = component of the JPanel used to create visual elements
	public void paintComponent(Graphics g) 
	{
		g.setColor(Color.WHITE);
		g.fillRect(0, 50, 400, 150);
		g.setColor(GUI.colours[GUI.getColourScheme()][3]);
		g.fillRect(0, 0, 400, 50);
		
		g.setFont(GUI.boldFont);
		g.setColor(Color.BLACK);
		g.drawString("SELECT AN ARCHIVE", 20, 15);
		
		int space = 50;
		int width = 400;
		if(image)
		{
			try {
				BufferedImage image = ImageIO.read(new File(doc.get(0)));
				g.drawImage(image, 0, space, image.getWidth(), image.getHeight(), this);
				space += image.getHeight();
				width = image.getWidth();
			} catch (IOException e) {}
		}
		else
		{
			space += 15;
			if(!doc.isEmpty())
			{
				g.setFont(GUI.defaultFont);
				for(int i = 0; i < doc.size(); i++)
				{
					String s = doc.get(i);
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
		tutorial.setSize(width, space + 20);
	}
	
	public int decodeImageText(Graphics g, String text, int space)
	{
		short indent = 0;
		try {
			if(text.contains("{"))
			{
				indent = Short.parseShort(text.substring(text.indexOf('{') + 1, text.indexOf('}')));
				text = text.replace(text.substring(text.indexOf('{'), text.indexOf('}') + 1), "");
			}
			BufferedImage image = ImageIO.read(new File(text));
			g.drawImage(image, indent, space, image.getWidth(), image.getHeight(), this);
			space += image.getHeight();
		} catch (IOException e) {}
		return space;
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(sections))
		{
			readFile(files.get(sections.getSelectedIndex()));
			this.repaint();
		}
	}

	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1)
		{
			if(paged == true)
			{
				step++;
				if(step >= max)
					step = (byte)(max - 1);
				
				readDocument(readForPage(files.get(sections.getSelectedIndex()), step));
				this.repaint();
			}
		}
	}
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}
	public void mousePressed(MouseEvent e) {
	}
	public void mouseReleased(MouseEvent e) {
	}
}
