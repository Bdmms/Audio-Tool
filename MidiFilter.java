import java.io.File;
import javax.swing.filechooser.*;

/**
 * <b>[Date: October 31, 2016]</b>
 * <p>
 * This class checks files and filters only the ones
 * with the correct extension
 * </p>
 */
public class MIDIFilter extends FileFilter
{
	public final static String[] mid = {"mid","midi"};  //Extensions of midi files
    public final static String soundbank = "sf2";	 	//Extension of soundbank files
	
	private boolean midiFile = true;	//Determines if .mid or .sf2 extensions should be filtered
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public boolean accept(File f)}</pre></p> 
	 * Determines whether file is accepted.</p> 
	 * @param f = file being checked
	 * @return Whether the file is accepted
	 */
	public boolean accept(File f) {
		//If file is a directory
		if (f.isDirectory())
		    return true;
		
		String extension = getExtension(f);
		//If extension does exist
	    if (extension != null) {
	    	//If extension equals .mid (or .midi)
	        if((extension.equals(mid[0]) || extension.equals(mid[1])) && midiFile){
	        	return true;
	        } 
	        //If extension equals .sf2
	        else if(extension.equals(soundbank) && !midiFile){
	        	return true;
	        }
	        else 
	        {
	        	return false;
	        }
	    }
	    
		return false;
	}

	/**
	 * <blockquote>
	 * <p><pre>{@code public String getDescription(File f)}</pre></p> 
	 * Describes the extension of the file name.</p> 
	 * @param f = file being checked
	 * @return the name of the extension
	 */
	public String getDescription(File f) 
	{
		String extension = getExtension(f);
	    String type = null;

	    //If extension does exist
	    if (extension != null) {
	    	//If extension equals .mid
	        if (extension.equals(mid[0]) || (extension.equals(mid[1]))){
	            type = "Midi File";
	        }
	        //If extension equals .sf2
	        if (extension.equals(soundbank)){
	            type = "SoundBank File";
	        }
	    }
	    return type;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static String getExtension(File f)}</pre></p> 
	 * Returns the extension of the file.</p> 
	 * @param f = file being checked
	 * @return the extension name
	 */
	public static String getExtension(File f)
	{
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        //checks if i is in the String
        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void setFilterMIDI(boolean midiExtend)}</pre></p> 
	 * Sets the filter mode of the filter.</p> 
	 * @param midiExtend = the mode of filtering for the filter
	 */
	public void setFilterMIDI(boolean midiExtend)
	{
		midiFile = midiExtend;
	}

	/**
	 * <blockquote>
	 * <p><pre>{@code public String getDescription()}</pre></p> 
	 * Is a required method. Unused by the program.</p> 
	 * @return Always null
	 */
	public String getDescription() 
	{
		return null;
	}
}
