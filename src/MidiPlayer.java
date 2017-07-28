import java.util.Collection;
import java.util.HashMap;

import javax.sound.midi.*;

public class MidiPlayer {

	private Synthesizer synth;
	private MidiChannel[] channels;
	private Instrument[] instruments;
	private Instrument currInstrument;
	
	//Arbitrary initial value for bpm
	private int bpm = 120;
	
	//how long each note value lasts based on bpm
	private long note1Time;
	private long note2Time;
	private long note4Time;
	private long note8Time;
	private long note16Time;
	private long note32Time;
	
	//standard tuned strings, from string 1 to 6
	private String[] guitarStrings = {"E4", "B3", "G3", "D3", "A2", "E2"};
	//Base midi number of notes
	private HashMap<String, Integer> noteNumbers = new HashMap<String, Integer>();
	
	//Initialize
	public void init()
	{
		noteNumbers.put("C", 0);
		noteNumbers.put("C#", 1);
		noteNumbers.put("D", 2);
		noteNumbers.put("D#", 3);
		noteNumbers.put("E", 4);
		noteNumbers.put("F", 5);
		noteNumbers.put("F#", 6);
		noteNumbers.put("G", 7);
		noteNumbers.put("G#", 8);
		noteNumbers.put("A", 9);
		noteNumbers.put("A#", 10);
		noteNumbers.put("B", 11);
		
		//use bpm to set note length in milliseconds
		updateNoteTiming();
		
		//Open midi synth
		try
		{
			synth = MidiSystem.getSynthesizer();
		    channels = synth.getChannels();
		    instruments = synth.getDefaultSoundbank().getInstruments();
		    
		    synth.open();
		    
		    //set initial instrument
		    currInstrument = instruments[Guitar.Clean.getIndex()];
		    //change instrument for 6 channels to represent each guitar string
		    for(int i = 1; i < 7; i++)
		    {
		    	channels[i].programChange(currInstrument.getPatch().getProgram());
		    }
		} 
		catch(MidiUnavailableException e)
		{
			e.printStackTrace();
		}
		
		//System.out.println("MidiPlayer Init Done.");
	}
	
	//called when a new documentmodel replaces the old one
	//reset strings, bpm, and note timing
	public void reInit()
	{
		guitarStrings[0] = "E4";
		guitarStrings[1] = "B3";
		guitarStrings[2] = "G3";
		guitarStrings[3] = "D3";
		guitarStrings[4] = "A2";
		guitarStrings[5] = "E2";
		bpm = 120;
		updateNoteTiming();
	}
	
	//plays all notes in a measure position for the position's note value length
	public void playPosition(Collection<Note> notes, NoteValue noteValue)
	{
		for(Note note : notes)
		{
			playNote(note.getString(), note.getFret());
		}
		
		long duration = 0;
		
		switch(noteValue)
		{
			case Whole:
				duration = note1Time;
				break;
			case Half:
				duration = note2Time;
				break;
			case Quarter:
				duration = note4Time;
				break;
			case Eighth:
				duration = note8Time;
				break;
			case Sixteenth:
				duration = note16Time;
				break;
			case ThirtySecond:
				duration = note32Time;
				break;
		}
		
		try { Thread.sleep(duration);
        } catch( InterruptedException e ) { }
	}
	
	//plays single note
	private void playNote(int string, int fret)
	{
		//get midi num of open string note and then add frets (half steps)
		int noteNum = noteToMidiNum(guitarStrings[string-1]) + fret;
		
		//stop previous sounds on a guitar string before next sound
		channels[string].allSoundOff();
		channels[string].noteOn(noteNum, 100);
	    
	}
	
	//Determine Midi note number from note and octave
	//Assumes note name is correct and octave is not negative
	public int noteToMidiNum(String note)
	{
		//last character of string is the octave number
		int octave = Integer.parseInt(note.substring(note.length() - 1));
		//rest is the note letter and maybe sharp
		String noteName = note.substring(0, note.length() - 1);
		
		//midi number at octave -1
		int baseNumber = noteNumbers.get(noteName);
		//difference in octaves from base of -1 times number of notes in an octave
		int difference = (octave + 1) * 12;
		
		return baseNumber + difference;
	}
	
	public void onClose()
	{
		synth.close();
	}
	
	//Change a strings note
	public void tuneString(int string, String note)
	{
		guitarStrings[string-1] = note;
	}
	
	//Change type of guitar sound
	public void changeInstrument(Guitar instrument)
	{
		currInstrument = instruments[instrument.getIndex()];
		for(int i = 1; i < 7; i++)
		{
			channels[i].programChange(currInstrument.getPatch().getProgram());
		}
	}
	
	public void setBPM(int bpm)
	{
		this.bpm = bpm;
		updateNoteTiming();
	}
	
	private void updateNoteTiming()
	{
		//60 secs / bpm = time for each beat
		note4Time = 60000 / bpm;
		
		note1Time = 4 * note4Time;
		note2Time = 2 * note4Time;
		note8Time = note4Time / 2;
		note16Time = note4Time / 4;
		note32Time = note4Time / 8;
	}
	
	public void stopSounds()
	{
		for(int i = 1; i < 7; i++)
		{
			channels[i].allSoundOff();
		}
	}
}
