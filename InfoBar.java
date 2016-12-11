import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.JPanel;


public class InfoBar extends JPanel{

	/**
	 * Date: November 21, 2016
	 * 
	 * This class is responsible for displaying the information of the song in the track menu
	 * This information includes the song's tempo, length and time signature
	 */
	
	private static final long serialVersionUID = 2L;		//default serial ID
	private short seconds = 0;								//the length of the song in seconds
	private JLabel [] field = new JLabel[8];				//arrays of JLabels to display info
	
	//constructor
	public InfoBar()
	{		
		for(int f = 0; f < field.length; f++)
		{
			field[f] = new JLabel();
		}
		
		for(int i =  0; i < field.length/2; i++)
		{			
			for(int j = 0; j < field.length/4; j++)
			{
				field[i*2+j] = new JLabel();
				field[i*2+j].setBounds((GUI.screenWidth/6 -10) * j + 10, i*30, GUI.screenWidth/4, 30);
				field[i*2+j].setBackground(Color.WHITE);
				add(field[i*2+j]);
			}
		}
	}
	
	//public String timeConverter() returns the time of the song in minutes
	public String timeConverter()
	{
		String time = null;
		
		seconds = (short) (MIDISong.getSequence().getMicrosecondLength() / 1000000);
		time = (seconds / 60) + ":" + String.format("%02d",(seconds % 60)) + " min";
		
		return time;		
	}
	
	//pubic void seTextFields() sets the text of each Jlabel
	public void setTextFields()
	{		
		field[0].setText("Song Name:");
		field[1].setText(MIDIReader.getFileName(0));
		field[2].setText("Created by:");
		field[3].setText("Artist 1");
		field[4].setText("Length:");
		field[5].setText(timeConverter());
		field[6].setText("Tempo:");
		field[7].setText(Math.round(MIDISong.getTempoBpm()) + " bpm");
	}
	
	//public void paintComponent draws the info bar and displays all of its info
	//Graphics g = component of the JLabel used to display the info bar
	public void paintComponent(Graphics g) 
	{
		Graphics2D g2D = (Graphics2D) g;
		drawInfoBar(g2D);
		
		if(MIDISong.getSequence() != null)
			setTextFields();
	}
	
	/*
	 * draws the info bar
	 * Graphics 2d g = the component used to draw the bar
	 */
	public void drawInfoBar(Graphics2D g)
	{
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, (GUI.screenWidth / 2) -20, 120);
		g.setColor(Color.BLACK);
		g.setStroke(GUI.bold);
		g.drawRect(0, 0, (GUI.screenWidth / 2) -20, 120);
	}
}
