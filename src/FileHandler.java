import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;


public class FileHandler {

	public static void saveFile(File file)
	{
		Path filePath = null;
		
		try
		{
			filePath = file.toPath();			
		}
		catch(InvalidPathException e)
		{
			System.err.format("InvalidPathException: %s%n", e);
			JOptionPane.showMessageDialog(Main.frame, "Invalid file name");
			return;
		}
		
		Charset charset = Charset.forName("UTF-8");
		try (BufferedWriter writer = Files.newBufferedWriter(filePath, charset)) 
		{
			//Write header info: bpm, time sig, guitar strings, numMeasures
			String s = "bpm = " + Main.tabModel.getBPM();
		    writer.write(s, 0, s.length());
		    writer.newLine();
		    
		    s = "Time Signature: " + Main.tabModel.getTimeSigTop() + "/" + Main.tabModel.getTimeSigBot().getValue();
		    writer.write(s, 0, s.length());
		    writer.newLine();
		    
		    s = "Tuning: ";
		    String[] guitarStrings = Main.tabModel.getStrings();
		    for(int i = 0; i < guitarStrings.length; i++)
		    {
		    	s += guitarStrings[i] + " ";
		    }
		    writer.write(s, 0, s.length());
		    writer.newLine();
		    
		    s = "Number of Measures: ";
		    ArrayList<Measure> measures = Main.tabModel.getMeasures();
		    s += measures.size();
		    writer.write(s, 0, s.length());
		    writer.newLine();
		    
		    //write measures
		    for(int i = 0; i < measures.size(); i++)
		    {
		    	writer.newLine();
		    	s = "Measure " + (i+1);
		    	writer.write(s, 0, s.length());
			    writer.newLine();
			    
			    s = measures.get(i).toString();
			    writer.write(s, 0, s.length());
		    }
		} 
		catch (IOException e) 
		{
		    System.err.format("IOException: %s%n", e);
		    JOptionPane.showMessageDialog(Main.frame, e);
		}
	}
	
	public static void openFile(File file)
	{
		Path filePath = file.toPath();
		DocumentModel newModel = new DocumentModel();
		
		Charset charset = Charset.forName("UTF-8");
		String[] lines = new String[9]; //will read 9 lines at a time for measures
		try (BufferedReader reader = Files.newBufferedReader(filePath, charset)) 
		{
			//Header: bpm, time sig, tuning, numMeasures
		    for(int line = 0; line < 4; line++)
		    {
		    	lines[line] = reader.readLine();
		    }
		    
		    boolean validHeader = validateHeader(newModel, lines);
		    if(!validHeader)
		    {
		    	JOptionPane.showMessageDialog(Main.frame, "Incompatible file.");
	    		return;
		    }
			
		    String[] parts = lines[3].split(" ");
		    int numMeasures = Integer.parseInt(parts[3]);
		    
			//measures
		    for(int measure = 0; measure < numMeasures; measure++)
		    {
		    	if(measure != 0)
		    	{
		    		newModel.addMeasure();
		    	}
		    	
		    	for(int line = 0; line < 9; line++)
		    	{
		    		lines[line] = reader.readLine();
		    	}
		    	
		    	boolean validMeasure = validateMeasure(newModel, lines);
		    	if(!validMeasure)
		    	{
		    		JOptionPane.showMessageDialog(Main.frame, "Incompatible file.");
		    		return;
		    	}
		    }
		    
		    Main.tabModel = newModel;
		    Main.tabModel.rewind();
		    Main.mainPane.viewPane.redraw();
		} 
		catch (IOException e) 
		{
		    System.err.format("IOException: %s%n", e);
		}
	}
	
	//returns if header info is properly formed. if true, changes model with info read in
	private static boolean validateHeader(DocumentModel model, String[] lines)
	{
		boolean validHeader = true;
		
		for(int i = 0; i < 4; i++)
		{
			if(lines[i] == null)
			{
				validHeader = false;
			}
		}
		
		if(validHeader)
		{
			//line in form of bpm = a number from 1-999
			boolean valid1 = Pattern.matches("bpm = [1-9][0-9]?[0-9]?", lines[0]);
			boolean valid2 = lines[1].equals("Time Signature: 4/4");
			//line in form of Tuning: 6 sharp notes or regular notes plus their octaves
			boolean valid3 = Pattern.matches("Tuning: ((([ACDFG]#?)|[ABCDEFG])[0-8] ){6,6}", lines[2]);
			//line in form of Number of Measures: any number greater than zero
			boolean valid4 = Pattern.matches("Number of Measures: [1-9][0-9]*", lines[3]);
			
			if(valid1 && valid2 && valid3 && valid4)
			{
				//bpm
				String[] parts = lines[0].split(" ");
				model.setBPM(Integer.parseInt(parts[2]));
				Main.midiPlayer.setBPM(Integer.parseInt(parts[2]));
				
				//tuning
				parts = lines[2].split(" ");
				for(int i = 1; i < parts.length; i++)
				{
					model.tuneString(i, parts[i]);
				}
			}
			else
			{
				validHeader = false;
			}
		}
		
		return validHeader;
	}
	
	//returns if written measure is properly formed. if true, adds measure to model
	private static boolean validateMeasure(DocumentModel model, String[] lines)
	{
		boolean validMeasure = true;
		
		for(int i = 0; i < 9; i++)
		{
			if(lines[i] == null)
			{
				validMeasure = false;
			}
		}
		
		if(validMeasure)
		{
			boolean valid1 = lines[0].equals("");
			//line in form of Measure: number greater than zero
			boolean valid2 = Pattern.matches("Measure [1-9][0-9]*", lines[1]);
			//line in form of 32 positions consisting of one space padding, and note position (a space and
			//	the letter of the note value). One final space padding is after all positions
			//TODO regex does not handle proper number of spaces after a particular note value
			//	   (e.g. an empty position after a sixteenth note)
			boolean valid3 = Pattern.matches("(  [ WHQEST]){32,32} ", lines[8]);
			boolean valid4 = true;
			
			//similar to regex for the note values. padding is a hyphen, note positions are either empty (--) or
			//	a fret number from 0-22.
			//TODO also does not check proper number of empty positions after a particular note value
			Pattern p = Pattern.compile("(-(--|-[0-9]|1[0-9]|2[0-2])){32,32}-");
			
			//check all six guitar strings
			for(int i = 2; i < 8; i++)
			{
				Matcher m = p.matcher(lines[i]);
				if(!m.matches())
				{
					valid4 = false;
				}
			}
			
			if(valid1 && valid2 && valid3 && valid4)
			{
				readBeatLine(model, lines[8]);
				
				for(int i = 2; i < 8; i++)
				{
					readNoteLine(model, lines[i], i-1);
				}
			}
			else
			{
				validMeasure = false;
			}
		}
		
		return validMeasure;
	}
	
	//adds measure positions to the current measure with note values that are read in
	private static void readBeatLine(DocumentModel model, String line)
	{
		int index = 2; //0 is padding, 1 is empty, 2 is first letter location
		String noteValue = line.substring(index, index+1);
		int toSkip = 0; //measure positions skipped for 4/4 time
		
		while(!noteValue.equals(" "))
		{
			switch(noteValue)
			{
				case "W":
					//first position is already added, set its note value instead
					if(index == 2) { model.setPositionBeat(NoteValue.Whole); }
					else { model.addPosition(NoteValue.Whole); }
					toSkip = 31;
					break;
				case "H":
					if(index == 2) { model.setPositionBeat(NoteValue.Half); }
					else { model.addPosition(NoteValue.Half); }
					toSkip = 15;
					break;
				case "Q":
					if(index == 2) { model.setPositionBeat(NoteValue.Quarter); }
					else { model.addPosition(NoteValue.Quarter); }
					toSkip = 7;
					break;
				case "E":
					if(index == 2) { model.setPositionBeat(NoteValue.Eighth); }
					else { model.addPosition(NoteValue.Eighth); }
					toSkip = 3;
					break;
				case "S":
					if(index == 2) { model.setPositionBeat(NoteValue.Sixteenth); }
					else { model.addPosition(NoteValue.Sixteenth); }
					toSkip = 1;
					break;
				case "T":
					if(index == 2) { model.setPositionBeat(NoteValue.ThirtySecond); }
					else { model.addPosition(NoteValue.ThirtySecond); }
					toSkip = 0;
					break;
				default:
					break;
			}
			
			//skip one padding + 2 spots for a position, plus another position to get to next beat
			index += toSkip * 3 + 3;
			
			//read in next note value position if not at end
			if(index < line.length())
			{
				noteValue = line.substring(index, index+1);
			}
			else
			{
				break;
			}
		}
	}
	
	//adds notes to current string of measure positions
	private static void readNoteLine(DocumentModel model, String line, int string)
	{
		Measure currMeasure = model.getCurrMeasure();
		currMeasure.moveToStart(); //rewind measure
		ArrayList<MeasurePosition> positions = currMeasure.getPositions();
		
		int index = 1; //0 is padding, 1 and 2 are the note
		int toSkip = 0; //measure positions skipped for 4/4 time
		
		for(int position = 0; position < positions.size(); position++)
		{
			String fret = line.substring(index, index + 2);
			
			//not empty position
			if(!fret.equals("--"))
			{
				//single digit fret
				if(fret.substring(0, 1).equals("-"))
				{
					fret = fret.substring(1, 2);
				}
				
				int fretNum = Integer.parseInt(fret);
				model.placeNote(new Note(string, fretNum));
			}
			
			NoteValue noteValue = positions.get(position).getNoteValue();
			
			switch(noteValue)
			{
				case Whole:
					toSkip = 31;
					break;
				case Half:
					toSkip = 15;
					break;
				case Quarter:
					toSkip = 7;
					break;
				case Eighth:
					toSkip = 3;
					break;
				case Sixteenth:
					toSkip = 1;
					break;
				case ThirtySecond:
					toSkip = 0;
					break;
			}
			
			//skip one padding + 2 spots for a position, plus another position to get to next beat
			index += toSkip * 3 + 3;
			
			currMeasure.moveRightPosition();
		}
		
	}
}
