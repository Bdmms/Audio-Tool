import java.io.*;
import javax.sound.midi.*;

/**
 * <b>[Date: October 29, 2016]</b>
 * <p>
 * This class reads, opens and creates MIDI files for the program.
 * It enables the user interaction with MIDIs.
 * </p>
 */
public class MIDIReader {

	private File midiFile = null;	//The file that contains the sequence
	private boolean named = false;	//Whether the file has been named
	private byte extUsed = 0; 		//The current extension of the file
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public Sequence readFile(File file)}</pre></p> 
	 * Reads the file to retrieve the sequence.</p> 
	 * @param file = the file being opened
	 * @return The sequence from the opened file
	 */
	public Sequence readFile(File file){
	
		named = true;
		
		if(MIDIFilter.getExtension(file).equals(MIDIFilter.mid[1]))
			extUsed = 1;
		else
			extUsed = 0;
		
		//If file contains a correct extension
		if(MIDIFilter.getExtension(file).equals(MIDIFilter.mid[extUsed]))
		{
			midiFile = file;
				
			//File is opened
			try
			{
				InputStream is = new FileInputStream(file);
				
				//if the file type is supported then create a buffered input stream for is
				if(!is.markSupported())
				{
					is = new BufferedInputStream(is);
				}
				Sequence s = MidiSystem.getSequence(is);
				is.close();
				return s;
			}
			catch (Exception ex){NotifyAnimation.sendMessage("Error", "File could not be read.");;}
		}
		return null;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public Sequence createFile()}</pre></p> 
	 * Creates a new sequence.</p> 
	 * @return The sequence that was create
	 */
	public Sequence createFile()
	{		
		try
		{
			Sequence s = new Sequence(javax.sound.midi.Sequence.PPQ,24);
			s.createTrack();
			//fileName = "Song";
			midiFile = new File("Song");
			
			//SYSEX MESSAGE		
			//byte[] b = {(byte)0xF0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, (byte)0xF7};
			//s.getTracks()[0].add(new MidiEvent(new SysexMessage(b,8), 0));

			//SET TEMPO
			//{32,0,0} (0x200000)
			byte[] c = {0x20, 0x0, 0x0};
			//{81,c,3}
			s.getTracks()[0].add(new MidiEvent(new MetaMessage(0x51, c, 3), 0));
			
			//END OF TRACK
			byte[] a = {};
			s.getTracks()[0].add(new MidiEvent(new MetaMessage(0x2F, a, 0), 16*40));

			return s;
		}
		catch(InvalidMidiDataException ex){ex.printStackTrace();}
		
		return null;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void saveFile(Sequence s, File file)}</pre></p> 
	 * Saves the sequence to a file.</p> 
	 * @param s = the sequence that is being saved
	 */
	public void saveFile(Sequence s, File file)
	{
		//If file does not have the correct extension name
		if(!file.getName().endsWith("."+MIDIFilter.mid[extUsed]))
		{
			//If file has a period (suggests it has an extension)
			if(file.getName().contains("."))
				file = new File(file.getPath().substring(0, file.getPath().lastIndexOf('.')) + "." + MIDIFilter.mid[extUsed]);
			else
				file = new File(file.getPath() + "." + MIDIFilter.mid[extUsed]);
		}
		midiFile = file;
		try
		{
			//Writes file
			MidiSystem.write(s, 1, midiFile);
		}
		catch(IOException ex){ex.printStackTrace();}
		
		named = true;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void saveConfig(int w, int h, int x, int y, byte c)}</pre></p> 
	 * Saves the config information.</p> 
	 * @param w = width of the screen
	 * @param h = height of the screen
	 * @param x = x location of the window on screen
	 * @param y = y location of the window on screen
	 * @param c = colour scheme last used
	 */
	public void saveConfig(int w, int h, int x, int y, byte c)
	{
		try {
			//Writes information
			PrintWriter write = new PrintWriter(new BufferedWriter(new FileWriter("windowSetup/Config.txt", false)));
			write.println(w + "x" + h);
			write.println(x + "x" + y);
			write.println(c);
			write.close();
		} catch (IOException e) {
			//Won't ever be seen though (program closes immediately afterwards)
			NotifyAnimation.sendMessage("Error", "Configuration failed to save properly");
		}
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static void setFileName(String s)}</pre></p> 
	 * Sets the name of the file.</p> 
	 * @param s = new name of file
	 */
	public void setFileName(String s)
	{
		//If s lacks the correct extension
		if(!s.endsWith(MIDIFilter.mid[extUsed]))
			s += ".mid";
		midiFile = new File(midiFile.getPath().substring(0, midiFile.getPath().lastIndexOf('/') + 1) + s);
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public short[] getConfig()}</pre></p> 
	 * Reads the config information.</p> 
	 * @return An array containing all config information
	 */
	public short[] getConfig()
	{
		short[] v = {720, 480, 0, 0, 0};
		try {
			BufferedReader read = new BufferedReader(new FileReader("windowSetup/Config.txt"));

			String s = read.readLine();
			//width of the screen
			v[0] = Short.parseShort(s.substring(0, s.indexOf('x')));
			//height of the screen
			v[1] = Short.parseShort(s.substring(s.indexOf('x') + 1));
			s = read.readLine();
			//x location of the window on screen
			v[2] = Short.parseShort(s.substring(0, s.indexOf('x')));
			//y location of the window on screen
			v[3] = Short.parseShort(s.substring(s.indexOf('x') + 1));
			//colour scheme
			v[4] = Short.parseShort(read.readLine());
			
			read.close();
		} catch (FileNotFoundException e) {NotifyAnimation.sendMessage("Error", "Configuration could not be read.");
		} catch (IOException e) {NotifyAnimation.sendMessage("Error", "Configuration could not be read.");}
		
		return v;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public Soundbank getSoundbank(File file)}</pre></p> 
	 * Reads a soundbank from a file.</p> 
	 * @param file = file being read
	 * @return The soundbank that was read
	 */
	public Soundbank getSoundbank(File file)
	{
		Soundbank sound = null;
		try {
			//Read soundbank file
			sound = MidiSystem.getSoundbank(file);
		} catch (Exception ex) {NotifyAnimation.sendMessage("Error", "Soundbank could not be opened.");}
		return sound;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static String getFileName(int limit)}</pre></p> 
	 * Returns the String of the file name without its extension.</p> 
	 * @param limit = how many characters of the String to return (0 = unlimited)
	 * @return The file name
	 */
	public String getFileName(int limit)
	{
		if(limit != 0 && midiFile.getName().length() > limit)
		{
			return midiFile.getName().replaceAll("."+MIDIFilter.mid[extUsed], "").substring(0, limit -2)+"...";
		}
		else
			return midiFile.getName().replaceAll("."+MIDIFilter.mid[extUsed], "");
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static File getFile()}</pre></p> 
	 * Returns the file that contains the midi.</p> 
	 * @return The file that contains the sequence
	 */
	public File getFile()
	{
		return midiFile;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public boolean isFileNamed()}</pre></p> 
	 * Determines if the file has been saved before.</p> 
	 * @return Whether the file has been named
	 */
	public boolean isFileNamed()
	{
		return named;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static String[] getInstrumentList()}</pre></p> 
	 * Reads the instrument list from the document Instrument_List.</p> 
	 * @param file = file being read
	 * @return The instrument list as a String[] array
	 */
	public static String[] getInstrumentList()
	{
		String[] s = new String[0];
		BufferedReader read;
		int lines = 0;
		try {
			read = new BufferedReader(new FileReader("windowSetup/Instrument_List.txt"));
			
			//Determine size of list (Also determines if there is an end)
			while (!read.readLine().equals("END"))
			{
				lines++;
			}
			
			s = new String[lines];
			read.close();
			
			//Reset reader
			read = new BufferedReader(new FileReader("windowSetup/Instrument_List.txt"));
			
			//Read every item of the list
			for(int i = 0; i < lines; i++)
			{
				s[i] = read.readLine();
			}
			
			read.close();
			
		} catch (FileNotFoundException e) {NotifyAnimation.sendMessage("Error", "Instrument list could not be read.");
		} catch (IOException e) {NotifyAnimation.sendMessage("Error", "Instrument list could not be read.");}
		
		return s;
	}
}
