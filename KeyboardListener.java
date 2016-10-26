/**
 * This class is responsible for all of the keyboard shortcuts present within the audio tool
 * Shortcuts include copy, paste, cut, undo and redo
 * Date: October 17, 2016
 * 
 */

//libraries

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardListener implements KeyListener{

	//variables
	private boolean space, ctrl, enter;
	
	private boolean[] letters = new boolean[26];
	private boolean[] numbers = new boolean[10];

	/*
	 * getLetters() returns the boolean value of every letter on the keyboard
	 */
	public boolean[] getLetters(){
		
		return letters;
	}
	
	/*
	 * setLetter(boolean state, byte letter) sets the boolean value of a specific letter
	 * boolean state = the boolean value being set to the letter
	 * byte letter = the index of the letter in the array
	 */
	public void setLetter (boolean state, byte letter){
		
		letters[letter] = state;
	}
	/*
	 * getNumbers() returns the boolean value of every number on the keyboard
	 */
	public boolean [] getNumbers(){
		
		return numbers;
	}
	
	/*
	 * setNumbers(boolean state, byte number) sets the boolean value of a specific number
	 * boolean state = the boolean value being set to the letter
	 * byte number = the index of the number in the array
	 */
	public void setNumbers(boolean state, byte number){
		
		numbers[number] = state;
	}
	
	/*
	 * getEnter() returns the boolean value of the enter key
	 */
	public boolean getEnter(){
		
		return enter;
	}
	
	/*
	 * setEnter(boolean state) sets the boolean value of the enter key
	 * boolean state = the boolean value of the enter key
	 */
	public void setEnter(boolean state){
		
		enter = state;
	}
	
	/*
	 * getBackspace() returns the boolean value of the backsapce key
	 */
	public boolean getSpace(){
		
		return space;
	}
	
	/*
	 * setBackspapce(boolean state) sets the boolean value of the backspace key
	 * boolean state = the boolean value of the backsapce key
	 */
	public void setSpace(boolean state){
		
		space = state;
	}
	
	/*
	 * getControl() returns the boolean value of the control key
	 */
	public boolean getControl(){
		
		return ctrl;
	}
	
	/*
	 * setControl(boolean state) sets the boolean value of the control key
	 * boolean state = the boolean value of the control key
	 */
	public void setControl(boolean state){
		
		ctrl = state;
	}
	
	/*
	 * mandatory method for the implementation of KeyListener
	 */
	public void keyTyped(KeyEvent e) {
		
		
	}

	/*
	 * keyPressed(KeyEvent e) checks every key used in the program when it is pressed. When the key is pressed its boolean value is set to true
	 * KeyEvent e = the detected event value from the keyboard
	 * 
	 */
	public void keyPressed(KeyEvent e) {
				
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
			
			//determines if control and the 'y' key are being pressed simultaneously 
			if(e.getKeyCode() == KeyEvent.VK_Y && (e.getModifiers() | KeyEvent.CTRL_DOWN_MASK) != 0)
			{
				letters [24] = true;
				ctrl = true;
			}
			
			//determines if control and the 'z' key are being pressed simultaneously 
			if(e.getKeyCode() == KeyEvent.VK_Z && (e.getModifiers() | KeyEvent.CTRL_DOWN_MASK) != 0)
			{
				letters [25] = true;
				ctrl = true;
			}
			
			//sets the pressed letter's boolean value to true in the array of letters
			else if(e.getKeyChar() - 97 == c)
			{
				letters [c] = true;
				System.out.println((char)(c+97));
			}
		}
		
		for(byte n = 0; n<=9; n++)
		{
			//sets the pressed number's boolean value to true in the array of numbers
			if(e.getKeyChar() -48 == n)
			{
				numbers [n] = true;
				System.out.println(n);
			}
		}
		
		//determines if the enter key is pressed
		if(e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			enter = true;
		}
		
		//determines if the space key is pressed
		if(e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			space = true;
		}
		
	}

	/*
	 * keyPressed(KeyEvent e) checks every key used in the program when it is pressed. When the key is released its boolean value is set to false
	 * KeyEvent e = the detected event value from the keyboard
	 */
	public void keyReleased(KeyEvent e) {
		
		//determines if the control key is released
		if(e.getKeyCode() == KeyEvent.VK_CONTROL)
		{
			ctrl = false;
		}
		
		//determines if the enter key is released
		if(e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			enter = false;
		}
		
		//determines if the space key is released
		if(e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			space = false;
		}
		
		for(byte c = 0; c<=25; c++)
		{
			//sets the released letter's boolean value to false in the array of letters
			if(e.getKeyChar() -97 == c)
			{
				letters [c] = false;
			}
		}
		
		for(byte n = 0; n<=9; n++)
		{
			//sets the released number's boolean value to false in the array of numbers
			if(e.getKeyChar() -48 == n)
			{
				numbers [n] = false;
			}
		}
	}

	
}
