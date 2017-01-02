/**
 * This class reads, opens and creates MIDI files for the program.
 * It enables the user interaction with MIDIs.
 */

//libraries
import java.io.*;
import javax.sound.midi.*;

public class MIDIReader {

	//variables
	private static File midiFile = null;
	private boolean named = false;
	
	/*
	 * readFile(String fileName) returns sequence of the opened file
	 * String fileName = the name of the file
	 */
	public Sequence readFile(File file){
	
		named = true;
		
		if(MIDIFilter.getExtension(file).equals(MIDIFilter.mid[0]) || MIDIFilter.getExtension(file).equals(MIDIFilter.mid[1]))
		{
			midiFile = file;
				
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
	
	/*
	 * setFileName(String s) sets the name of the file
	 * String s = new name of file
	 */
	public static void setFileName(String s)
	{
		if(!s.endsWith(MIDIFilter.mid[0]) && !s.endsWith(MIDIFilter.mid[1]))
			s += ".mid";
		midiFile = new File(midiFile.getPath().substring(0, midiFile.getPath().lastIndexOf('/') + 1) + s);
	}
	
	/*
	 * public sequence createFile() returns the sequence of a new file
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
	
	/*
	 * public void saveFile saves the MIDI file
	 * Sequence s = the sequence that is being saved
	 */
	public void saveFile(Sequence s, File file)
	{
		if(!file.getName().endsWith("."+MIDIFilter.mid[0]) && !file.getName().endsWith("."+MIDIFilter.mid[1]))
		{
			if(file.getName().contains("."))
				file = new File(file.getPath().substring(0, file.getPath().lastIndexOf('.')) + "." + MIDIFilter.mid[0]);
			else
				file = new File(file.getPath() + "." + MIDIFilter.mid[0]);
		}
		midiFile = file;
		try
		{
			MidiSystem.write(s, 1, midiFile);
		}
		catch(IOException ex){ex.printStackTrace();}
		
		named = true;
	}
	
	public void saveConfig(int w, int h, int x, int y, byte c)
	{
		try {
			PrintWriter write = new PrintWriter(new BufferedWriter(new FileWriter("windowSetup/Config.txt", false)));
			write.println(w + "x" + h);
			write.println(x + "x" + y);
			write.println(c);
			write.close();
		} catch (IOException e) {
			//Won't ever be seen though
			NotifyAnimation.sendMessage("Error", "Configuration failed to save properly");
		}
	}
	
	public short[] getConfig()
	{
		short[] v = {720, 480, 0, 0, 0};
		try {
			BufferedReader read = new BufferedReader(new FileReader("windowSetup/Config.txt"));

			String s = read.readLine();
			v[0] = Short.parseShort(s.substring(0, s.indexOf('x')));
			v[1] = Short.parseShort(s.substring(s.indexOf('x') + 1));
			s = read.readLine();
			v[2] = Short.parseShort(s.substring(0, s.indexOf('x')));
			v[3] = Short.parseShort(s.substring(s.indexOf('x') + 1));
			v[4] = Short.parseShort(read.readLine());
			
			read.close();
		} catch (FileNotFoundException e) {NotifyAnimation.sendMessage("Error", "Configuration could not be read.");
		} catch (IOException e) {NotifyAnimation.sendMessage("Error", "Configuration could not be read.");}
		
		return v;
	}
	
	/*
	 * getSoundbank(File file) returns the soundbank that was read
	 * File file = file being read
	 */
	public Soundbank getSoundbank(File file)
	{
		Soundbank sound = null;
		try {
			sound = MidiSystem.getSoundbank(file);
		} catch (Exception ex) {NotifyAnimation.sendMessage("Error", "Soundbank could not be opened.");}
		return sound;
	}
	
	/*
	 * getInstrumentList() returns a list that is read from the document Instrument_List
	 */
	public static String[] getInstrumentList()
	{
		String[] s = new String[0];
		BufferedReader read;
		int lines = 0;
		try {
			read = new BufferedReader(new FileReader("windowSetup/Instrument_List.txt"));
			
			while (!read.readLine().equals("END"))
			{
				lines++;
			}
			
			s = new String[lines];
			read.close();
			
			read = new BufferedReader(new FileReader("windowSetup/Instrument_List.txt"));
			
			for(int i = 0; i < lines; i++)
			{
				s[i] = read.readLine();
			}
			
			read.close();
			
		} catch (FileNotFoundException e) {NotifyAnimation.sendMessage("Error", "Instrument list could not be read.");
		} catch (IOException e) {NotifyAnimation.sendMessage("Error", "Instrument list could not be read.");}
		
		return s;
	}
	
	/*
	 * getFileName() returns the String of the file name without its extension
	 */
	public static String getFileName(int limit)
	{
		if(limit != 0 && midiFile.getName().length() > limit)
		{
			return midiFile.getName().replaceAll("."+MIDIFilter.mid[0], "").substring(0, limit -2)+"...";
		}
		else
			return midiFile.getName().replaceAll("."+MIDIFilter.mid[0], "");
	}
	
	/*
	 * getFile() returns the file that contains the midi
	 */
	public static File getFile()
	{
		return midiFile;
	}
	
	/*
	 * isFileNamed() returns whether the file has been saved before
	 */
	public boolean isFileNamed()
	{
		return named;
	}
}
