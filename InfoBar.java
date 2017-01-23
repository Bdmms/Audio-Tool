import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * <b>[Date: November 21, 2016]</b>
 * <p>
 *	This class is responsible for displaying the information of the song in the track menu
 * This information includes the song's tempo, length and time signature.
 * </p>
 */
public class InfoBar extends JPanel
{
	private static final long serialVersionUID = 2L;//Default serial ID
	private JLabel[] field = new JLabel[8];		//The Labels of the information
	private boolean visible = true;					//Whether the info bar is in a transition between visible and not visible 
	private short opacity = 255;					//The opacity of the info bar
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public InfoBar()}</pre></p> 
	 * Initializes the info bar's components.</p> 
	 */
	public InfoBar()
	{		
		//Creating every column of information
		for(int i =  0; i < field.length/2; i++)
		{		
			//Creating every row in a column
			for(int j = 0; j < field.length/4; j++)
			{
				field[i*2+j] = new JLabel();
				field[i*2+j].setBounds((GUI.screenWidth/6 -10) * j + 10, i*30, GUI.screenWidth/2 -145, 30);
				field[i*2+j].setBackground(Color.WHITE);
				add(field[i*2+j]);
			}
		}
		
		//Static text for the JLabels (they do not change)
		field[0].setText("Song Name:");
		field[2].setText("Created by:");
		field[4].setText("Length:");
		field[6].setText("Tempo:");
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void resizeInfobar()}</pre></p> 
	 * Resizes the components of the info bar.</p> 
	 */
	public void resizeInfobar()
	{
		//Sets every JLabel's size
		for(int i = 0; i<field.length; i++)
		{
			field[i].setSize(GUI.screenWidth/2 -145, 30);
		}
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public short tickToTime()}</pre></p> 
	 * Determines the current position of the time through the tick position of the song.</p>
	 * @param seconds = the length of the song in seconds
	 * @return the time position of the <b>MIDIPlayer</b> 
	 */
	public short tickToTime(short seconds)
	{
		return (short)(((double) (MIDIPlayer.getTickPosition()) / MIDISong.getSequence().getTickLength() * seconds / 1000000));
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public String timeConverter(short seconds)}</pre></p> 
	 * Creates a String that contains the current time position over the total time length of the song.</p>
	 * @param seconds = the length of the song in seconds
	 * @return the String that contains the time information 
	 */
	public String timeConverter(short seconds)
	{
		short elapsed = tickToTime(seconds);
		//Example of format: (0:01 / 0:55)
		return elapsed/60 + ":" + String.format("%02d",(elapsed % 60)) + " / " + (seconds/60)  + ":" + String.format("%02d",(seconds % 60));		
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void setTextFields()}</pre></p> 
	 * Sets the text for all of the non-static JLabels.
	 */
	public void setTextFields()
	{		
		//Song/File Name
		field[1].setText(MIDIMain.getReadFileName(0));
		//Artist Name
		field[3].setText(MIDISong.getArtistName());
		//Time Stamp Information
		field[5].setText(timeConverter((short)(MIDISong.getSequence().getMicrosecondLength() / 1000000)));
		//Initial Tempo
		field[7].setText(Math.round(MIDISong.getTempoBpm()) + " bpm");
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void setTextFields()}</pre></p> 
	 * Triggers the animation for toggling the info bar.
	 * @param state = new visibility state of the info bar
	 */
	public void setVisibleAnimation(boolean state)
	{
		visible = state;
		//If state = visible
		if(state == true)
			this.setVisible(state);
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public boolean isInAnimation()}</pre></p> 
	 * Determines if the info bar is in the middle of its animation.
	 * @return Whether the info bar is in its animation
	 */
	public boolean isInAnimation()
	{
		//If info bar is completely opaque or invisible
		if(opacity == 0 || opacity == 255)
			return false;
		else
			return true;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void paintComponent(Graphics g)}</pre></p> 
	 * Draws the info bar when the repaint() method is triggered.
	 * @param g = component of the JPanel used to create visual elements
	 */
	public void paintComponent(Graphics g) 
	{
		Graphics2D g2D = (Graphics2D) g;
		drawInfoBar(g2D);
		
		//If a sequence has been created
		if(MIDISong.getSequence() != null)
			setTextFields();
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void drawInfoBar(Graphics2D g)}</pre></p> 
	 * Draws the components of the info bar.
	 * @param g = component of the JPanel used to create visual elements
	 */
	public void drawInfoBar(Graphics2D g)
	{
		//If visible is set to false, but animation has not finished
		if(visible == false && opacity > 16)
			opacity -= 16;
		//If visible is set to false and animation has finished
		else if(visible == false)
			this.setVisible(false);
		
		//If visible is set to true, but animation has not finished
		if(visible == true && opacity < 239)
			opacity += 16;
		//If visible is set to true and animation has finished
		else if(visible == true)
			opacity = 255;
		
		//Background of info bar (effected by opacity)
		g.setColor(new Color(GUI.colours[GUI.getColourScheme()][1].getRed(), GUI.colours[GUI.getColourScheme()][1].getGreen(), GUI.colours[GUI.getColourScheme()][1].getBlue(), opacity));
		g.fillRect(0, 0, (GUI.screenWidth / 2) -20, 120);
		
		//Border of info bar (effected by opacity)
		g.setColor(GUI.colours[GUI.getColourScheme()][GUI.COLOUR_TEXT]);
		g.setStroke(GUI.bold);
		g.drawRect(0, 0, (GUI.screenWidth / 2) -20, 120);
	}
}
