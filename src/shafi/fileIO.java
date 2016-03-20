
package shafi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author shafi
 *
 */
public class fileIO {
	
	private File inputfile;
	private File outputfile;
	private Scanner sc;
        private FileOutputStream fos;
        
        /**
         *  @param name - the file path.
         *  @param type - operation type ( in - for input, out- for output)
         *  @throws FileNotFoundException if the file cannot be opened
         *   
         */
        public fileIO(String name, String type)
        {
            if("in".equals(type))
            {
                inputfile = new File(name);
                try {
                    sc= new Scanner(inputfile);
                } catch (FileNotFoundException ex) {
                    print("File not found.");
                }
            }
            else if("out".equals(type))
            {
                outputfile = new File(name);
                
                try {
                    fos = new FileOutputStream(outputfile);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(fileIO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else
            {
                print("No match found. Use in for input file, out for output file.");
            }
        }
        
	public boolean setInputFileName(File name)
	{
		inputfile = name;
		return true;
	}
	
	public boolean setOutFileName(File name)
	{
            try {
                outputfile = name;
                fos.close();
                fos = new FileOutputStream(outputfile);
                return true;
            } catch (FileNotFoundException ex) {
                Logger.getLogger(fileIO.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(fileIO.class.getName()).log(Level.SEVERE, null, ex);
            }
            return false;
	}

	
	public int getNextInt()
	{
		int retInt=0;
		return retInt;
	}
	
	public String getNextString()
	{
		String string="";
		return string;
	}
	
	public char getNextChar()
	{
		char ch= (Character) null ;
		return ch;
	}
	
	public String getNextLine()
	{
		String line = "";
		return line;
	}
        
        public boolean writeString(String str)
        {
            
            return false;
        }
        
        public void writeInt(int value)
        {
            try {
                fos.write(value);
            } catch (IOException ex) {
                Logger.getLogger(fileIO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        public boolean writeObject(Object obj)
        {
            try {
                fos.write((byte[]) obj);
                return true;
            } catch (IOException ex) {
                Logger.getLogger(fileIO.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }

    private void print(Object obj) {
        System.out.println(obj.toString());
    }
	
}