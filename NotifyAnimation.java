import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class NotifyAnimation 
{
	//Sets limit to character space in notification window
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
			if(anmTimer < 25)
			{
				value -= 4;
			}
			else if(anmTimer < 275)
			{
				
			}
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
			for(byte i = 0; i < message.length; i++)
			{
				g.drawString(message[i], 540, value + 60 + i*20);
			}
		}
	}
	
	//sendMessage(String s) receives a sent class
	//String s = full length string of message
	public static void sendMessage(String s){
		message = new String[s.length()/characterLimit+1];
		if(s.length() > characterLimit)
		{
			byte a = 0;
			while(s.length() > characterLimit)
			{
				message[a] = s.substring(0, s.indexOf(' ', characterLimit));
				while(message[a].startsWith(" "))
				{
					message[a] = message[a].substring(1);
				}
				s = s.substring(s.indexOf(' ', characterLimit));
				a++;
			}
			message[a] = s;
		}
		else
		{
			message[0] = s;
		}
		value = 480;
		anmTimer = 0;
		trigger = true;
	}
}
