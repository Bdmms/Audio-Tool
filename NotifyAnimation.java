import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class NotifyAnimation 
{
	//Sets minimum limit to character space in notification window
	private static byte characterLimit = 24;
	
	//Determines whether the window should appear
	private static boolean trigger = false;
	//The current time of the animation
	private static short anmTimer = 0;
	//The y location of the notification window
	private static short value = 480;
	//The message contained in the window
	private static String[] message;
	
	//drawNoteWindow(Graphics g) draws the notification window
	//Graphics g = component of the JPanel used to create visual elements
	public static void drawNoteWindow(Graphics g){
		if(trigger == true)
		{
			//50 = 500 milliseconds
			
			//250 milliseconds
			if(anmTimer < 25)
			{
				value -= 4;
			}
			//2500 milliseconds
			else if(anmTimer < 275)
			{
				
			}
			//250 milliseconds
			else if(anmTimer < 300)
			{
				value += 4;
			}
			
			anmTimer++;
			
			g.setColor(Color.WHITE);
			g.fillRect(530, value, 180, 100);
			g.setColor(Color.BLACK);
			g.drawRect(530, value, 180, 100);
			
			g.setFont(new Font("AAA",Font.BOLD, 12));
			g.drawString("- Notification -", 540, value + 20);
			g.setFont(new Font("AAA",Font.ROMAN_BASELINE, 12));
			for(byte i = 0; i < message.length; i++)
			{
				g.drawString(message[i], 535, value + 40 + i*20);
			}
		}
	}
	
	//sendMessage(String s) receives a sent class
	//String s = full length string of message
	public static void sendMessage(String s){
		
		message = new String[(s.length())/characterLimit+1];
		byte a = 0;
		
		//While String is larger than limit
		while(s.length() > characterLimit + 1)
		{
			//If line limit is reached
			if(a > 1 && s.length() > characterLimit)
			{
				message[a] = s.substring(0,characterLimit)+"...";
				s = " ";
			}
			//Checks if line in message has spaces
			else if(s.substring(characterLimit).contains(" "))
			{
				message[a] = s.substring(0, s.indexOf(' ', characterLimit));
				
				//checks if line is long enough to remove space
				if(s.length() > characterLimit+1)
					s = s.substring(s.indexOf(' ', characterLimit));
			}
			else
			{
				s = s.substring(0, characterLimit)+"-"+s.substring(characterLimit);
				message[a] = s.substring(0, characterLimit + 1);
				s = s.substring(characterLimit);
			}
			
			//If line starts with space
			if(message[a].startsWith(" "))
			{
				message[a] = message[a].substring(1);
			}
			a++;
		}
		message[a] = s;
		
		for(byte i = 0; i < message.length; i++)
		{
			//If a line of the message equals null
			if(message[i] == null)
				message[i] = " ";
		}
		
		value = 480; 	//Reset location
		anmTimer = 0;	//Reset timer
		trigger = true;	//Turn on animation
	}
}
