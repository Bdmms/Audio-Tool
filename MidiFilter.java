import java.io.File;
import javax.swing.filechooser.*;

public class MIDIFilter extends FileFilter
{
	/**
	 * Date: October 31, 2016
	 * 
	 * This class checks files and filters only the ones
	 * with the correct extension
	 */
	
	public final static String[] mid = {"mid","midi"};  //Extensions of midi files
    public final static String soundbank = "sf2";	 	//Extension of soundbank files
	
	private boolean midiFile = true;	//Determines if .mid or .sf2 extensions should be filtered
	
	//accept(File f) determines whether file is accepted
	//File f = file being checked
	public boolean accept(File f) {
		//If file is a directory
		if (f.isDirectory()) {
		    return true;
		}
		
		String extension = getExtension(f);
		//If extension does exist
	    if (extension != null) {
	    	//If extension equals .mid
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

	//getDescription(File f) describes the extension of the file name
	//File f = file being checked
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
	
	//String getExtension(File f) returns the extension of a file name
	//File f = file being checked
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
	
	//setFilterMIDI(boolean midiExtend)
	//boolean midiExtend = the mode of filtering for the filter
	public void setFilterMIDI(boolean midiExtend)
	{
		midiFile = midiExtend;
	}

	//getDescription() is a required method, it returns null always
	public String getDescription() 
	{
		return null;
	}
}
