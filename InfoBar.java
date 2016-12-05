import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.JPanel;


public class InfoBar extends JPanel{

	/**
	 * Date: Novemeber 21, 2016
	 * 
	 * This class is responsible for displaying the information of the song in the track menu
	 * This information includes the song's tempo, length and time signature
	 */
	
	private static final long serialVersionUID = 2L;
	private long length = 0;
	private long tempo = 0;
	private String songName = null;
	private String artistName = null;
	private JLabel [] field = new JLabel[8];
	
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
				field[i*2+j].setBounds((GUI.screenWidth/4 -10) * j + 10, i*30, GUI.screenWidth/4 -20, 30);
				field[i*2+j].setBackground(Color.WHITE);
				add(field[i*2+j]);
			}
		}
	}
	
	public void setTextFields()
	{		
		field[0].setText("Song Name:");
		field[1].setText("blah");
		field[2].setText("Created by:");
		field[3].setText("blah");
		field[4].setText("Length:");
		field[5].setText(MIDISong.getSequence().getMicrosecondLength() / 1000000 + " sec");
		field[6].setText("Tempo:");
		field[7].setText(Math.round(MIDISong.getTempoBpm()) + " bpm");
	}
	
	public void paintComponent(Graphics g) 
	{
		Graphics2D g2D = (Graphics2D) g;
		drawInfoBar(g2D);
		
		if(MIDISong.getSequence() != null)
			setTextFields();
	}
	
	public void drawInfoBar(Graphics2D g)
	{
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, (GUI.screenWidth / 2) -20, 120);
		g.setColor(Color.BLACK);
		g.setStroke(GUI.bold);
		g.drawRect(0, 0, (GUI.screenWidth / 2) -20, 120);
	}
	
	public void UpdateInfoBar()
	{
		length = MIDISong.getSequence().getTickLength() / 1000000;
		songName = MIDISong.getSequence().toString();
	}
}
