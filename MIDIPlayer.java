import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.midi.*;

public class MIDIPlayer implements MetaEventListener
{
	/**
	 * Novemeber 2, 2016
	 * 
	 * This class contains all of the objects that are used to
	 * play midi files and produce noises.
	 */
	
	public static final int END_OF_TRACK_MESSAGE = 0x2F;	//the status message for the END_OF_TRACK_MESSAGE
	
	private Synthesizer synth;								//The synthesizer of the player
	private Sequencer sequencer;							//The sequencer of the player
	private MidiChannel chan[];								//The channels the program has access too
	//private Soundbank sound;								//The soundbank for the instruments
	private boolean loop;									//Determines whether a played song should loop
	//private boolean paused;								//Determines if song is being paused

	private boolean noteOn = false;							//if a note is playing in the synthesizer
	private byte[] noteData = {0,0};						//The volume and tone of the note
	
	//initial method, sets the synthesizer and sequencer of the player
	public MIDIPlayer()
	{
		try {
			synth = MidiSystem.getSynthesizer();
			sequencer = MidiSystem.getSequencer(false);
			synth.open();
			sequencer.open();
			
			sequencer.getTransmitter().setReceiver(synth.getReceiver());
			sequencer.addMetaEventListener(this);
		} catch (Exception e1) {e1.printStackTrace();}
		chan = synth.getChannels();
		//sound = synth.getDefaultSoundbank();
	}
	
	//setSoundBank(File soundbank) sets the soundbank for the song
	//File soundbank = file that stores the soundbank
	public void setSoundBank(Soundbank soundbank)
	{
		try {
			synth.loadAllInstruments(soundbank);
			chan = synth.getChannels();
		} catch (Exception ex) {
			synth.loadAllInstruments(synth.getDefaultSoundbank());
			chan = synth.getChannels();
		}
	}
	
	//setSoundBankDefault() sets the soundbank for the song to the default soundbank of the computer
	public void setSoundBankDefault()
	{
		synth.loadAllInstruments(synth.getDefaultSoundbank());
		chan = synth.getChannels();
	}
	
	//setInstrument(byte instr, byte chan) sets the program of the channel to a specified instrument
	//byte instr = instrument
	//byte chan = channel being changed
	public void setInstrument(byte instr, byte channel)
	{
		chan[channel].programChange(instr);
	}
	
	public void setAllInstruments()
	{
		for(byte t = 0; t < MIDISong.getTracksLength(); t++)
		{
			chan[t].programChange(MIDISong.getTracks(t).getInstrument());
		}
	}
	
	//setTickPosition() sets the tick position of the song being played
	public void setTickPosition(long t)
	{
		sequencer.setTickPosition(t);
	}
	
	//getSequence(File file) returns the sequence from file
	//File file = file that contains sequence *temporally
	public Sequence getSequence(File file)
	{
		try
		{
			InputStream is = new FileInputStream(file);
			//is file supported
			if(!is.markSupported()){
				is = new BufferedInputStream(is);
			}
			Sequence s = MidiSystem.getSequence(is);
			is.close();
			return s;
		}catch (IOException ex){
			ex.printStackTrace();
			return null;
		}catch (InvalidMidiDataException ex){
			ex.printStackTrace();
			return null;
		}
	}
	
	//getTickPosition() returns the tick position of the song being played
	public long getTickPosition()
	{
		return sequencer.getTickPosition();
	}
	
	public void NoteOn(byte tone, byte volume)
	{
		if(noteOn == false)
		{
			noteData[0] = tone;
			noteData[1] = volume;
			noteOn = true;
			chan[MIDIMain.getTrackMenu()].noteOn(noteData[0], noteData[1]);
		}
	}
	
	public void NoteOff()
	{
		if(noteOn == true)
		{
			noteOn = false;
			chan[MIDIMain.getTrackMenu()].noteOff(noteData[0], noteData[1]);
		}
	}
	
	//play(Sequence seq, boolean loop) plays the the song
	//boolean loop = whether the song should loop
	public void play(boolean loop)
	{
		if(sequencer != null && MIDISong.getSequence() != null && sequencer.isOpen())
		{
			try{
				sequencer.setSequence(MIDISong.getSequence());
				sequencer.start();
				this.loop = loop;
			}catch (Exception ex){ex.printStackTrace();}
		}
	}
	
	//stop() stops the song currently being played
	public void stop()
	{
		if(sequencer != null && sequencer.isOpen())
		{
			try{
				sequencer.stop();
			}catch (Exception ex){ex.printStackTrace();}
		}
	}

	//meta(MetaMessage meta) responds to any MetaMessage produced from the midi being played
	public void meta(MetaMessage meta) 
	{
		if(meta.getType() == END_OF_TRACK_MESSAGE){
			if(loop){
				sequencer.setTickPosition(sequencer.getLoopStartPoint());;
				sequencer.start();
			}
		}
	}
}	
