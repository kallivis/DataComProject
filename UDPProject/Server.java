import java.io.*;
import java.net.*;
import java.nio.*;

public class Server {

  public static void main(String[] args) throws Exception {

    byte[] buff = new byte[200]; //was 64...
    DatagramPacket packet = new DatagramPacket(buff, buff.length);
    DatagramSocket socket = new DatagramSocket(3031);

    System.out.println("Server started at 3031 ...");

    while (true) {
      socket.receive(packet);
      new Server.ThreadVideo(socket, packet).run();

    }
  }

  public static class ThreadVideo implements Runnable {

    private DatagramPacket packet;
    private DatagramSocket socket;

    public ThreadVideo(DatagramSocket socket, DatagramPacket packet) {
      this.packet = packet;
      this.socket = socket;
    }

    public void run() {
      String cmd = new String(packet.getData(), 0, packet.getLength());
      if (cmd.equals("FILE_REQUEST")) {
        System.out.println("FILE REQUESTED");
        try {
          read_and_send_video(this.packet.getAddress());
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else {
        System.out.println("Socket Exiting ....");
        System.exit(0);
      }
    }


    private void read_and_send_video(InetAddress address)
      throws IOException {

      File file = new File("test.txt");
      FileInputStream fis = new FileInputStream(file);
      DatagramPacket pack;

      int size = 0;
      byte[] fileBuffer = new byte[(int) file.length()];
      byte[] buffer = new byte[512];
     // ByteBuffer bb = ByteBuffer.allocate(2);
    //  bb.order(ByteOrder.BIG_ENDIAN);

      while (true) {
        size = fis.read(buffer);
        System.out.println("Size = " + size);
        byte[] sizeBuff = new byte[size]; 
        sizeBuff = fileBuffer;
        pack = new DatagramPacket(sizeBuff, size, address,
            packet.getPort());
        socket.send(pack);
        if (size < buffer.length) {
          break;
        }
        
      }

      String cmd = "END_REQUEST";
      pack = new DatagramPacket(cmd.getBytes(), cmd.getBytes().length,
          address, packet.getPort());
      socket.send(pack);

    }

  }
}
