import java.io.*;

/*
 * Program Name:    FileOperator.java
 * Author(s):       Jeremy Wheaton, 100105823
 *                  Cody McCarthy,  100097829 
 * Version:         3.0 - Nov 30, 2012
 * Purpose:
 * This class is used to return directory and file information.
 */
public class FileOperator
{
  //This method return all the files and directories in the server's directory.
  public static String getRootFiles(){

    File dir = new File(".");
    File[] files = dir.listFiles();
    int y = 0;
    String output = ""; 
    for (File i : files)
    {
      output = output.concat(String.format("%3d) %-40s Size: %-10s\n", 
            y, i, printSize(i.length())));
      y++;
    }
    return output;
  }

  //This method returns all the files and directories in the given's  
  //users directory.
  public static String getUserFiles(String user){
    File dir = new File(user);
    File[] files = dir.listFiles();
    int y = 0;
    String output = ""; 
    for (File i : files)
    {
      output = output.concat(String.format("%3d) %-40s Size: %-10s\n", 
            y, i, printSize(i.length())));
      y++;
    }
    return output;
  }

  //changes the size to a more readable size.
  public static String printSize(long size)
  {
    if (size < 1000)
      return String.valueOf(size) + " B";
    else if (size < 1048576)
      return String.valueOf(size / 1024) + " KB";
    else
      return String.valueOf(size / 1048576) + " MB";

  }
}
