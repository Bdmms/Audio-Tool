import java.awt.Color;
import java.awt.Graphics2D;

public class ToolBarExtension 
{
	private VolumeSlider slider = new VolumeSlider((short)0, (short)0, false);
	private short y = -42;
	
	public void drawExtension(Graphics2D g)
	{
		//If in Note Editor
		if(MIDIMain.getMode() == 2)
		{
			//If notes have not been selected
			if(SelectableObject.isAllUnSelected())
				y = -42;
			else if(y < 0)
				y += 2;
				
			//Volume Slider
			slider.setBounds((short)(GUI.screenWidth*5/8 + 10), (short)(GUI.toolBarHeight + 5 + y), (short)150, (short)30);
			
			//Background
			g.setColor(Color.WHITE);
			g.fillRoundRect(GUI.screenWidth*5/8, GUI.toolBarHeight + y, 250, 40, 20, 20);
			//Text
			g.setColor(Color.BLACK);
			g.setFont(GUI.defaultFont);
			g.drawString(slider.getPercent()+"%", GUI.screenWidth*5/8 + 190, GUI.toolBarHeight + y + 25);
			//Border
			g.setStroke(GUI.bold);
			g.drawRect(GUI.screenWidth*5/8 + 180, GUI.toolBarHeight + y + 10, 50, 20);
			g.drawRoundRect(GUI.screenWidth*5/8, GUI.toolBarHeight + y, 250, 40, 20, 20);
			
			slider.drawVolumeSlider(g);
			g.setStroke(GUI.basic);
		}
	}
	
	public VolumeSlider getSlider()
	{
		return slider;
	}
}
