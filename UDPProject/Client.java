import java.io.*;
import java.net.*;
public class Client {
  public static void main(String[] args)
  {
//    String filename = args[0];
 //   String message = filename; 
 String   message = "FILE_REQUEST";
    try {

      File file = new File("local_test.txt");
      FileOutputStream fos = new FileOutputStream(file);

      InetAddress address = InetAddress.getByName("localhost");
    //byte[] data = message.getBytes();
    //byte[] buffer = new byte[64000];
      byte[] sdata = new byte[512]; 
      byte[] rData = new byte[512];
      sdata = message.getBytes();

      DatagramSocket socket = new DatagramSocket();
      socket.setSoTimeout(5000);

      DatagramPacket packet = new DatagramPacket(sdata, sdata.length,
          InetAddress.getByName("localhost"), 3031);

      socket.send(packet);


      DatagramPacket rpacket = new DatagramPacket(rData, rData.length);

      while (true) {
        socket.receive(rpacket);
        System.out.println(rpacket.getData().length);
        fos.write(rpacket.getData(), 0, rpacket.getLength());
        if (rpacket.getLength() < 512) {
            System.out.println("File transferred");
            break;
        }
      }

      System.out.println("File length - " + file.length());

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (UnknownHostException e) {

      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}
