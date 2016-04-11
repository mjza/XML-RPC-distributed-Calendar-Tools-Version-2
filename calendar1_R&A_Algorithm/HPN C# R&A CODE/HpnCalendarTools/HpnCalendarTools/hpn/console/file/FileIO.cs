using System;
using System.IO;
using System.Collections.Generic;
using System.Linq;

namespace hpn.console.file
{
    public class FileIO
    {
          private String filePath;
	      
	      public FileIO(String filePath) 
	      {
		    this.filePath = filePath;
	      }

	      public List<String> readFile()
	      {
		        if(this.filePath== null)
			      throw new System.NullReferenceException("The file path has not assigned yet.");

		        if(File.Exists(this.filePath))
		    	{
                    List<string> lines = File.ReadLines(this.filePath).ToList();
                    return lines;
                }
		        else
		        {
		    	    StreamWriter stream = File.CreateText(this.filePath);
                    stream.Close();
		    	    return null;
		        }
	      }
		  
	      public void writeFile(List<String> allLines) 
	      {
              File.WriteAllLines(this.filePath, allLines, System.Text.Encoding.UTF8);
	      }
    }
}
