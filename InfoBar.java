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
	private boolean visible = true;
	private short seconds = 0;
	private short opacity = 255;
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
	
	public String timeConverter()
	{
		String time = null;
		
		seconds = (short) (MIDISong.getSequence().getMicrosecondLength() / 1000000);
		time = (seconds / 60) + ":" + String.format("%02d",(seconds % 60)) + " min";
		
		return time;		
	}
	
	public void setTextFields()
	{		
		field[0].setText("Song Name:");
		field[1].setText(MIDIReader.getFileName(0));
		field[2].setText("Created by:");
		field[3].setText("Artist");
		field[4].setText("Length:");
		field[5].setText(timeConverter());
		field[6].setText("Tempo:");
		field[7].setText(Math.round(MIDISong.getTempoBpm()) + " bpm");
	}
	
	public void setVisibleAnimation(boolean state)
	{
		visible = state;
		if(state == true)
			this.setVisible(true);
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
		if(visible == false && opacity > 16)
			opacity -= 16;
		else if(visible == false)
			this.setVisible(false);
		
		if(visible == true && opacity < 239)
			opacity += 16;
		else if(visible == true)
			opacity = 255;
		
		g.setColor(Color.LIGHT_GRAY);
		g.setColor(new Color(Color.LIGHT_GRAY.getRed(), Color.LIGHT_GRAY.getGreen(), Color.LIGHT_GRAY.getBlue(), opacity));
		g.fillRect(0, 0, (GUI.screenWidth / 2) -20, 120);
		g.setColor(new Color(0, 0, 0, opacity));
		g.setStroke(GUI.bold);
		g.drawRect(0, 0, (GUI.screenWidth / 2) -20, 120);
	}
}
