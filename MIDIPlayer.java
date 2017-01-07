import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.midi.*;

/**
 * <b>[Date: Novemeber 2, 2016]</b>
 * <p>
 * This class contains all of the objects that are used to
 * play midi files and produce noises. It sets the channels
 * and the sequencer for playing the midi files.
 * </p>
 */
public class MIDIPlayer implements MetaEventListener
{
	public static final int END_OF_TRACK_MESSAGE = 0x2F;	//the status message for the END_OF_TRACK_MESSAGE
	
	private Synthesizer synth;								//The synthesizer of the player
	private static Sequencer sequencer;						//The sequencer of the player
	private static MidiChannel chan[];						//The channels the program has access too
	private boolean loop;									//Determines whether a played song should loop
	private static boolean play = false;					//Determines if song is being pause
	private boolean noteOn = false;							//if a note is playing in the synthesizer
	private byte[] noteData = {0,0};						//The volume and tone of the note playing
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public MIDIPlayer()}</pre></p> 
	 * The constructor method. Synthesizer and sequencer are initialized.</p>
	 */
	public MIDIPlayer()
	{
		try {
			synth = MidiSystem.getSynthesizer();
			sequencer = MidiSystem.getSequencer(false);
			synth.open();
			sequencer.open();
			//Connects the sequencer to the synthesizer
			sequencer.getTransmitter().setReceiver(synth.getReceiver());
			sequencer.addMetaEventListener(this);
		} catch (Exception e1) {e1.printStackTrace();}
		chan = synth.getChannels();
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void stop()}</pre></p> 
	 * Plays the current song / sequence.</p>
	 * @param loop = whether the song should loop
	 */
	public void play(boolean loop)
	{
		//If all variables are in place
		if(sequencer != null && MIDISong.getSequence() != null && sequencer.isOpen())
		{
			try{
				//If the song is playing, it turns off
				if(play)
				{
					sequencer.stop();
					play = false;
				}
				//Else it plays the song
				else
				{
					sequencer.setSequence(MIDISong.getSequence());
					sequencer.start();
					this.loop = loop;
					play = true;
				}
			}catch (Exception ex){NotifyAnimation.sendMessage("Error", "Sequence could not be played.");}
		}
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void stop()}</pre></p> 
	 * Stops the player from playing the song.</p>
	 */
	public void stop()
	{
		sequencer.stop();
		play = false;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void NoteOn(byte tone, byte volume)}</pre></p> 
	 * Plays a sound using the variables sent.</p>
	 * @param tone = tone of the sound
	 * @param volume = volume of the sound
	 */
	public void noteOn(byte tone, byte volume)
	{
		//If a sound isn't already playing
		if(noteOn == false)
		{
			noteData[0] = tone;
			noteData[1] = volume;
			noteOn = true;
			chan[MIDIMain.getTrackMenu()].noteOn(noteData[0], noteData[1]);
		}
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void NoteOff()}</pre></p> 
	 * Turns off the sound being played.</p>
	 */
	public void noteOff()
	{
		//If a sound is playing
		if(noteOn == true)
		{
			noteOn = false;
			//Uses noteData to determine what note to turn off
			chan[MIDIMain.getTrackMenu()].noteOff(noteData[0], noteData[1]);
		}
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void muteTrack(boolean state, byte channel)}</pre></p> 
	 * Mutes a specific channel.</p>
	 * @param state = state of the track
	 * @param channel = channel being muted
	 */
	public void muteTrack(boolean state, byte channel)
	{
		chan[channel].setMute(state);
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void setSoundBank(Soundbank soundbank)}</pre></p> 
	 * Sets the soundbank for the song.</p>
	 * @param soundbank = file that stores the soundbank
	 */
	public void setSoundBank(Soundbank soundbank)
	{
		try {
			//Trys to load the soundbank
			synth.loadAllInstruments(soundbank);
			chan = synth.getChannels();
		} catch (Exception ex) {
			//Loads default soundbank when it fails
			synth.loadAllInstruments(synth.getDefaultSoundbank());
			chan = synth.getChannels();
			NotifyAnimation.sendMessage("Error", "Soundbank could not be loaded into the program.");
		}
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void setSoundBankDefault()}</pre></p> 
	 * Sets the soundbank for the song to the default soundbank of the computer.</p>
	 */
	public void setSoundBankDefault()
	{
		synth.loadAllInstruments(synth.getDefaultSoundbank());
		chan = synth.getChannels();
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void setInstrument(byte instr, byte channel)}</pre></p> 
	 * Sets the instrument of a channel.</p>
	 * @param instr = instrument of the channel
	 * @param chan = channel being changed
	 */
	public void setInstrument(byte instr, byte channel)
	{
		chan[channel].programChange(instr);
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void setAllInstruments()}</pre></p> 
	 * Sets the instrument of every channel.</p>
	 */
	public void setAllInstruments()
	{
		//Sets every channel
		for(byte t = 0; t < MIDISong.getTracksLength(); t++)
		{
			chan[t].programChange(MIDISong.getTracks(t).getInstrument());
		}
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void setTickPosition(long t)}</pre></p> 
	 * Sets the tick position of the song being played.</p>
	 * @param t = new tick position
	 */
	public void setTickPosition(long t)
	{
		sequencer.setTickPosition(t);
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public void setVolume(byte channel, byte volume)}</pre></p> 
	 * Sets the volume of a channel.</p>
	 * @param channel = channel being muted
	 * @param volume = volume that the channel is set to
	 */
	public static void setVolume(byte channel, byte volume)
	{
		chan[channel].controlChange(7, volume);
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public Sequence getSequence(File file)}</pre></p> 
	 * Reads the sequence from file.</p>
	 * @param file = file that contains sequence
	 * @return The sequence from file
	 */
	public Sequence getSequence(File file)
	{
		try
		{
			//File is converted to read data
			InputStream is = new FileInputStream(file);
			//is file supported
			if(!is.markSupported()){
				is = new BufferedInputStream(is);
			}
			//Sequence is read
			Sequence s = MidiSystem.getSequence(is);
			is.close();
			return s;
		}catch (IOException ex){NotifyAnimation.sendMessage("Error", "Sequence could not be read.");
		}catch (InvalidMidiDataException ex){NotifyAnimation.sendMessage("Error", "Sequence could not be read.");}
		return null;
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static long getTickPosition()}</pre></p> 
	 * Returns the tick position of the song being played.</p>
	 * @return The tick position of the player
	 */
	public static long getTickPosition()
	{
		return sequencer.getTickPosition();
	}
	
	/**
	 * <blockquote>
	 * <p><pre>{@code public static boolean isPlaying()}</pre></p> 
	 * Returns the current state of the player.</p>
	 * @return Whether the song is playing
	 */
	public static boolean isPlaying()
	{
		return play;
	}

	/**
	 * <blockquote>
	 * <p><pre>{@code public void meta(MetaMessage meta)}</pre></p> 
	 * Responds to any MetaMessage produced from the midi being played.</p>
	 * @param meta = event message data
	 */
	public void meta(MetaMessage meta) 
	{
		//If event is the END_OF_TRACK message
		if(meta.getType() == END_OF_TRACK_MESSAGE){
			//If song is suppose to loop
			if(loop){
				sequencer.setTickPosition(sequencer.getLoopStartPoint());;
				sequencer.start();
			}
		}
	}
}	
