import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.midi.*;

public class MIDIPlayer implements MetaEventListener
{
	public static final int END_OF_TRACK_MESSAGE = 0x2F;	//the status message for the END_OF_TRACK_MESSAGE
	
	Synthesizer synth;						//The synthesizer of the player
	Sequencer sequencer;					//The sequencer of the player
	MidiChannel chan[];						//The channels the program has access too
	private boolean loop;					//Determines whether a played song should loop
	//private boolean paused;					//Determines if song is being paused
	
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
	}
	
	//setSoundBank(File soundbank) sets the soundbank for the song
	//File soundbank = file that stores the soundbank
	public void setSoundBank(File soundbank)
	{
		try {
			Soundbank sound = MidiSystem.getSoundbank(soundbank);
			synth.loadAllInstruments(sound);
			chan = synth.getChannels();
		} catch (Exception ex) {
			synth.loadAllInstruments(synth.getDefaultSoundbank());
			chan = synth.getChannels();
			ex.printStackTrace();
		}
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
	
	
	//setTickPosition() sets the tick position of the song being played
	public void setTickPosition(long t)
	{
		sequencer.setTickPosition(t);
	}
	
	//getTickPosition() returns the tick position of the song being played
	public long getTickPosition()
	{
		return sequencer.getTickPosition();
	}
	
	//play(Sequence seq, boolean loop) plays the the song
	//boolean loop = whether the song should loop
	public void play(boolean loop)
	{
		if(sequencer != null && MIDISong.getSequence() != null && sequencer.isOpen())
		{
			try{
				if(sequencer.getSequence() == null)
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
