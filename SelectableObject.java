
public class SelectableObject 
{
	/**
	 * Date: December 5th, 2016
	 * 
	 * This class is an extension for objects to allow them to be selectable.
	 * Selectable objects allow the user to control what he/she wants to edit.
	 */
	
	private static boolean allUnselected = true;	//true if all notes are not selected
	private boolean selected = false;	//whether the note is selected
	
	//selection(boolean state) sets the selected value of the note
	//boolean state = state of note selection
	public void selection(boolean state)
	{
		//If a note is selected
		if(state == true)
			allUnselected = false;
			
		selected = state;
	}
	
	//isSelected() returns whether the note is selected
	public boolean isSelected()
	{
		return selected;
	}
	
	//isAllUnSelected() returns whether all notes are unselected
	public static boolean isAllUnSelected()
	{
		return allUnselected;
	}
	
	//unSelectAll() changes the selection of every note in the track to false
	public static void unSelectAll()
	{
		//If notes are not already unselected
		if(allUnselected == false)
		{
			for(int i = 0; i < Notes.getNumNotes(); i++)
			{
				MIDISong.getNotes(MIDIMain.getTrackMenu(), i).selection(false);
			}
			for(byte i = 0; i < MIDISong.getTracksLength(); i++)
			{
				MIDISong.getTracks(i).selection(false);
			}
			allUnselected = true;
		}
	}
}
