
/**
 * <b>[Date: December 5, 2016]</b>
 * <p>
 * This class is an extension for objects to allow them to be selectable.
 * Selectable objects allow the user to control what he/she wants to edit.
 * </p>
 */
public class SelectableObject 
{
	private static boolean allUnselected = true;	//true if all notes are not selected
	private boolean selected = false;				//whether the note is selected
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void selection(boolean state)}</pre></p> 
	 * Sets the selected value of an Object.</p> 
	 * @param state = state of note selection
	 */
	public void selection(boolean state)
	{
		//If a object is selected
		if(state == true)
			allUnselected = false;
			
		selected = state;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public boolean isSelected()}</pre></p> 
	 * Returns whether the object is selected.</p> 
	 * @return Whether the object is selected
	 */
	public boolean isSelected()
	{
		return selected;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static boolean isAllUnSelected()}</pre></p> 
	 * Returns whether all objects are unselected.</p> 
	 * @return Whether all objects are unselected
	 */
	public static boolean isAllUnSelected()
	{
		return allUnselected;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static void unSelectAll()}</pre></p> 
	 * Changes the selection of every note in the track to false.</p> 
	 */
	public static void unSelectAll()
	{
		//If notes are not already unselected
		if(allUnselected == false)
		{
			//Checks every note
			for(int i = 0; i < Notes.getNumNotes(); i++)
			{
				MIDISong.getNotes(MIDIMain.getTrackMenu(), i).selection(false);
			}
			//Checks every track
			for(byte i = 0; i < MIDISong.getTracksLength(); i++)
			{
				MIDISong.getTracks(i).selection(false);
			}
			allUnselected = true;
		}
	}
}
