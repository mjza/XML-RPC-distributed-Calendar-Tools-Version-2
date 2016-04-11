package hpn.console.file;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileIO {
	  private String filePath;
	  final static Charset ENCODING = StandardCharsets.UTF_8;
	 
	  public FileIO(String filePath) 
	  {
		super();
		this.filePath = filePath;
	  }

	  public List<String> readFile() throws IOException 
	  {
		  if(this.filePath== null)
			  throw new IllegalStateException("The file path has not assigned yet.");
		    Path path = Paths.get(this.filePath);
		    if(Files.exists(path))
		    	return Files.readAllLines(path, ENCODING);
		    else
		    {
		    	Files.createFile(path);
		    	return null;
		    }
	  }
		  
	  public void writeFile(List<String> allLines) throws IOException 
	  {
		    Path path = Paths.get(this.filePath);
		    Files.write(path, allLines, ENCODING);
	  }

}
