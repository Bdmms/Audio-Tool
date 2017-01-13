import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * <b>[Date: October 17, 2016]</b>
 * <p>
 * This class registers and records keyboard inputs.
 * </p>
 */
public class KeyboardListener implements KeyListener
{
	private boolean space, ctrl, delete, enter;		//The special keys
	private boolean[] letters = new boolean[26];	//The letter keys on the keyboard

	/**
	 * <blockquote>
	 * <p><pre>{@code public void keyTyped(KeyEvent e)}</pre></p> 
	 * Mandatory method for the implementation of KeyListener.</p> 
	 * @param e = the detected event value from the keyboard
	 */
	public void keyTyped(KeyEvent e) {
	}

	/**
	 * <blockquote>
	 * <p><pre>{@code public void keyPressed(KeyEvent e)}</pre></p> 
	 * Checks every key used in the program when it is pressed. When the key is pressed its boolean value is set to true.</p> 
	 * @param e = the detected event value from the keyboard
	 */
	public void keyPressed(KeyEvent e) 
	{
		//Checks every letter
		for(byte c = 0; c<=25; c++)
		{
			//determines if control and the 'c' key are being pressed simultaneously 
			if(e.getKeyCode() == KeyEvent.VK_C && (e.getModifiers() | KeyEvent.CTRL_DOWN_MASK) != 0)
			{
				letters [2] = true;
				ctrl = true;
			}
			
			//determines if control and the 'v' key are being pressed simultaneously 
			if(e.getKeyCode() == KeyEvent.VK_V && (e.getModifiers() | KeyEvent.CTRL_DOWN_MASK) != 0)
			{
				letters [21] = true;
				ctrl = true;
			}
			
			//determines if control and the 'x' key are being pressed simultaneously 
			if(e.getKeyCode() == KeyEvent.VK_X && (e.getModifiers() | KeyEvent.CTRL_DOWN_MASK) != 0)
			{
				letters [23] = true;
				ctrl = true;
			}
			
			//sets the pressed letter's boolean value to true in the array of letters
			else if(e.getKeyChar() - 97 == c)
			{
				letters [c] = true;
			}
		}
		
		//determines if the delete key is pressed
		if(e.getKeyCode() == KeyEvent.VK_DELETE)
		{
			delete = true;
		}
		
		//determines if the space key is pressed
		if(e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			space = true;
		}
		//determines if the enter key is pressed
		if(e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			space = true;
		}
	}

	/**
	 * <blockquote>
	 * <p><pre>{@code public void keyReleased(KeyEvent e)}</pre></p> 
	 * Responds to when a key is released.</p> 
	 * @param e = the detected event value from the keyboard
	 */
	public void keyReleased(KeyEvent e) 
	{
		//determines if the control key is released
		if(e.getKeyCode() == KeyEvent.VK_CONTROL)
		{
			ctrl = false;
		}
		
		//determines if the delete key is released
		if(e.getKeyCode() == KeyEvent.VK_DELETE)
		{
			delete = false;
		}
		
		//determines if the space key is released
		if(e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			space = false;
		}
		
		//determines if the enter key is released
		if(e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			space = true;
		}
		
		for(byte c = 0; c<=25; c++)
		{
			//sets the released letter's boolean value to false in the array of letters
			if(e.getKeyChar() -97 == c)
			{
				letters[c] = false;
			}
		}
	}
  
	/**
	 * <blockquote>
	 * <p><pre>{@code public void setLetter(boolean state, byte letter)}</pre></p> 
	 * Sets the boolean value of a specific letter.</p> 
	 * @param state = the boolean value being set to the letter
	 * @param letter = the index of the letter in the array
	 */
	public void setLetter(boolean state, byte letter)
	{
		letters[letter] = state;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void setEnter(boolean state)}</pre></p> 
	 * Sets the boolean value of the enter key.</p> 
	 * @param state = the boolean value of the enter key
	 */
	public void setEnter(boolean state)
	{
		enter = state;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void setDelete(boolean state)}</pre></p> 
	 * Sets the boolean value of the delete key.</p> 
	 * @param state = the boolean value of the delete key
	 */
	public void setDelete(boolean state)
	{
		delete = state;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void setSpace(boolean state)}</pre></p> 
	 * Sets the boolean value of the backspace key.</p> 
	 * @param state = the boolean value of the backspace key
	 */
	public void setSpace(boolean state)
	{
		space = state;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void setControl(boolean state)}</pre></p> 
	 * Sets the boolean value of the control key.</p> 
	 * @param state = the boolean value of the control key
	 */
	public void setControl(boolean state)
	{
		ctrl = state;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public boolean[] getLetters()}</pre></p> 
	 * Returns the boolean value of every letter on the keyboard.</p> 
	 * @return The array of key inputs for letter keys
	 */
	public boolean[] getLetters()
	{
		return letters;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public boolean getEnter()}</pre></p> 
	 * Returns the boolean value of the enter key.</p> 
	 * @return The value of the enter key
	 */
	public boolean getEnter()
	{
		return enter;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public boolean getDelete()}</pre></p> 
	 * Returns the boolean value of the delete key.</p> 
	 * @return The boolean value of the delete key
	 */
	public boolean getDelete()
	{
		return delete;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public boolean getSpace()}</pre></p> 
	 * Returns the boolean value of the backspace key.</p> 
	 * @return The boolean value of the backspace key
	 */
	public boolean getSpace()
	{
		return space;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public boolean getControl()}</pre></p> 
	 * Returns the boolean value of the control key.</p> 
	 * @return The boolean value of the control key
	 */
	public boolean getControl()
	{
		return ctrl;
	}
}
