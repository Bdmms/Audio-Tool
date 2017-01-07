import java.awt.Graphics2D;
import java.util.ArrayList;

/**
 * <b>[Date: October 24, 2016]</b>
 * <p>
 * This class controls the animation and text of the message
 * panel that slide onto screen.
 * </p>
 */
public class NotifyAnimation 
{
	private static final byte characterLimit = 58; 						//Sets minimum limit to character space in notification window
	private static boolean trigger = false; 							//Determines whether the window should appear
	private static short anmTimer = 0; 									//The current time of the animation
	private static short y = 0; 										//The y location of the notification window
	private static short value = 0;										//The animation value for the window
	private static ArrayList<String> message = new ArrayList<String>();	//The message contained in the window
	private static String header;										//The header of the message
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static void drawNoteWindow(Graphics2D g)}</pre></p> 
	 * Draws the notification window.</p> 
	 * @param g = component of the JPanel used to create visual elements
	 */
	public static void drawNoteWindow(Graphics2D g)
	{
		//If a message has been sent
		if(trigger == true)
		{
			y = GUI.screenHeight;
			//50 = 500 milliseconds
			
			//for 250 milliseconds
			if(anmTimer < 25)
			{
				value -= 4;
			}
			//for 2500 milliseconds
			else if(anmTimer < 275)
			{
				
			}
			//for 250 milliseconds
			else if(anmTimer < 300)
			{
				value += 4;
			}
			//end of animation
			else
			{
				trigger = false;
			}
			
			anmTimer++;
			
			//Text Box
			g.setStroke(GUI.bold);
			g.setColor(GUI.colours[GUI.getColourScheme()][5]);
			g.fillRect(GUI.screenWidth - 330, y +value, 320, 100);
			g.setColor(GUI.colours[GUI.getColourScheme()][6]);
			g.drawRect(GUI.screenWidth - 330, y +value, 320, 100);
			
			g.setStroke(GUI.basic);
			
			//Message
			g.setFont(GUI.defaultFont);
			g.drawString("- "+header+" -", GUI.screenWidth - 320, y + value + 20);
			g.setFont(GUI.romanBaseline);
			for(byte i = 0; i < message.size(); i++)
			{
				g.drawString(message.get(i), GUI.screenWidth - 325, y +value + 40 + i*20);
			}
		}
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static void sendMessage(String head, String s)}</pre></p> 
	 * Receives a sent message.</p> 
	 * @param head = header to message
	 * @param s = message
	 * 
	 */
	public static void sendMessage(String head, String s){
		header = head;
		message.clear();
		byte a = 0;
		
		//While String is larger than limit
		while(s.length() > characterLimit + 1)
		{
			//If line limit is reached
			if(a > 1 && s.length() > characterLimit)
			{
				message.add(s.substring(0,characterLimit)+"...");
				s = " ";
			}
			//Checks if line in message has spaces
			else if(s.substring(0, characterLimit).contains(" "))
			{
				message.add(s.substring(0, s.lastIndexOf(' ', characterLimit)));
				
				//checks if line is long enough to remove space
				if(s.length() > characterLimit+1)
					s = s.substring(s.lastIndexOf(' ', characterLimit));
			}
			else
			{
				s = s.substring(0, characterLimit)+"-"+s.substring(characterLimit);
				message.add(s.substring(0, characterLimit + 1));
				s = s.substring(characterLimit);
			}
			
			//If line starts with space
			while(message.get(a).startsWith(" "))
			{
				String temp = message.get(a).substring(1);
				message.remove(a);
				message.add(temp);
			}
			a++;
		}
		message.add(s);
		
		//Checks if any of the lines are empty
		for(byte i = 0; i < message.size(); i++)
		{
			//If a line of the message equals null
			if(message.get(i) == null)
				message.remove(i);
		}
		
		value = 0;					//Reset location
		anmTimer = 0;				//Reset timer
		trigger = true;				//Turn on animation
	}
}
