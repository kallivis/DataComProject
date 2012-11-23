import java.io.*;

public class fileOp
{
    //static const char padder[] = "........................................";
    
    public static void main (String[] args)
    {
        //File current = new File(args[0]);
        File dir = new File(".");
        File[] files = dir.listFiles();
        int y = 0;
        
        for (File i : files)
        {
            System.out.format("%3d) %-40s Size: %-10s\n", y, i, printSize(i.length()));
            y++;
        }
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
