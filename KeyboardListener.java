/**
 * Class for keyboard inputs
 * Date: October 17, 2016
 * 
 */

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardListener implements KeyListener{

	private boolean back, ctrl, enter;
	
	private boolean[] letters = new boolean[26];
	private boolean[] numbers = new boolean[10];

	
	public boolean[] getLetters(){
		
		return letters;
	}
	
	
	public void setLetter (boolean state, byte letter){
		
		letters[letter] = state;
	}
	
	public boolean [] getNumbers(){
		
		return numbers;
	}
	
	public void setNumbers(boolean state, byte number){
		
		numbers[number] = state;
	}
	
	public boolean getEnter(){
		
		return enter;
	}
	
	public void setEnter(boolean state){
		
		enter = state;
	}
	
	public boolean getBackspace(){
		
		return back;
	}
	
	public void setBackspace(boolean state){
		
		back = state;
	}
	
	public boolean getControl(){
		
		return enter;
	}
	
	public void setControl(boolean state){
		
		ctrl = state;
	}
	
	public void keyTyped(KeyEvent e) {
		
		
	}

	public void keyPressed(KeyEvent e) {
		
		for(byte c = 97; c<=122; c++)
		{
			if(e.getKeyChar() == c)
			{
				letters [c-97] = true;
				System.out.println((char)c);
			}
		}
		
	}

	public void keyReleased(KeyEvent e) {
		
		for(byte c = 97; c<=122; c++)
		{
			if(e.getKeyChar() == 'c')
			{
				letters [c-97] = false;
			}
		}
	}

	
}
