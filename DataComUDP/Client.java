import java.io.*;
import java.net.*;
public class Client {
  public static void main(String[] argv)
  {
    String message = "FILE_REQUEST";
    try {

      File file = new File("localTest.txt");
      FileOutputStream fos = new FileOutputStream(file);

      InetAddress address = InetAddress.getByName("localhost");
      byte[] data = message.getBytes();
      byte[] buffer = new byte[64000];

      DatagramSocket socket = new DatagramSocket();
      socket.setSoTimeout(5000);

      DatagramPacket packet = new DatagramPacket(data, data.length,
          InetAddress.getByName("localhost"), 3031);

      socket.send(packet);


      DatagramPacket rpacket = new DatagramPacket(buffer, buffer.length);

      while (true) {
        socket.receive(rpacket);
        if (rpacket.getLength() <= 9) {
          String cmd = new String(rpacket.getData(), 0,
              rpacket.getLength());
          if (cmd.equals("END_REQUEST")) {
            System.out.println("File transferred");
            break;
          }
        }
        fos.write(rpacket.getData(), 0, rpacket.getLength());
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
