import java.io.*;

public class FileOperator
{
    
  public static String getRootFiles(){
        File dir = new File(".");
        File[] files = dir.listFiles();
        int y = 0;
        String output = ""; 
        for (File i : files)
        {
            output = output.concat(String.format("%3d) %-40s Size: %-10s\n", y, i, printSize(i.length())));
            y++;
        }
        return output;
  }

  public static String getUserFiles(String user){
        File dir = new File(user);
        File[] files = dir.listFiles();
        int y = 0;
        String output = ""; 
        for (File i : files)
        {
            output = output.concat(String.format("%3d) %-40s Size: %-10s\n", y, i, printSize(i.length())));
            y++;
        }
        return output;
  }
        

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
