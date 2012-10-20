import java.io.*;
import java.net.*;

public class Client {
  public static void main(String[] args) throws IOException {

    Socket socket = null;
    DataOutputStream out = null;
    DataInputStream in = null;

    try 
    {
      socket = new Socket("192.168.0.107", 4444);
      out = new PrintWriter(socket.getOutputStream(), true);
      in = new DataInputStream(socket.getInputStream());
    } 
    catch (UnknownHostException e) 
    {
      System.err.println("Don't know about host.");
      System.exit(1);
    } 
    catch (IOException e) {
      System.err.println("Couldn't get I/O for the connection to Host.");
      System.exit(1);
    }

    //Input file name
//    String filename = args[0];
 //   System.out.println(filename);

    //Write file to out.println(args[1])
 //   out.println(filename);

      String inputline;
           FileWriter fw = new FileWriter("Sheep.jpg");
              BufferedWriter bufWriter = new BufferedWriter(fw);
            
              //Step 1 read length
              int nb = in.readInt();
              System.out.println("Read Length"+ nb);
              byte[] digit = new byte[nb];
              //Step 2 read byte
               System.out.println("Writing.......");
              for(int i = 0; i < nb; i++)
                digit[i] = in.readByte();
               
               String st = new String(digit);
              bufWriter.append(st);
               bufWriter.close();
               
              //Step 1 send length
              out.writeInt(st.length());
              //Step 2 send length
              out.writeBytes(st); // UTF is a string encoding


    out.close();
    in.close();
    socket.close();

  }
}
