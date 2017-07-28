import java.io.File;

import javax.swing.filechooser.FileFilter;

//filter for filechooser to only display txt files and directories
public class TxtFilter extends FileFilter{

	@Override
	public boolean accept(File file) {
		
		//allows user to navigate directories in filechooser
		if(file.isDirectory())
		{
			return true;
		}
		
		String fileName = file.getName();
		String fileExtension = null; 
		int i = fileName.lastIndexOf(".");
		
		//not hidden file (starts with ".") and extension is present
		if(i > 0 && i < fileName.length() - 1)
		{
			fileExtension = fileName.substring(i+1).toLowerCase();
			if(fileExtension.equals("txt"))
			{
				return true;
			}
		}
		
		return false;
	}

	@Override
	public String getDescription() {
		return "Text files (*.txt)";
	}

}
