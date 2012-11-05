/*
 * Program Name:    Server.java
 * Author(s):       Jeremy Wheaton, 100105823
 *                  Cody McCarthy,  100097829 
 * Version:         2.0 - Nov 2, 2012
 * Purpose:
 * Can send any requested file (using UDP) from the directory that Server.java is run from
 * to the directory Client.java is run from.
 */


import java.io.*;
import java.net.*;
import java.nio.*;

public class Server {

    public static void main(String[] args) throws Exception
    {

        byte[] buff = new byte[200]; //need??
        DatagramPacket packet = new DatagramPacket(buff, buff.length);
        DatagramSocket socket = new DatagramSocket(3031);

        System.out.println("Server started at 3031 ...");

        while (true) {
            socket.receive(packet);
            new Server.TransferThread(socket, packet).run();

        }
    }

    public static class TransferThread implements Runnable
    {

        private DatagramPacket packet;
        private DatagramSocket socket;

        public TransferThread(DatagramSocket socket, DatagramPacket packet) 
        {
            this.packet = packet;
            this.socket = socket;
        }

        public void run()
        {
            String rfile = new String(packet.getData(), 0, 
                packet.getLength());
            if (rfile.equals("EXIT"))
            {
                System.out.println("Server Closing.");
                System.exit(0);
            }
            else
            {
                System.out.println("Requested: " + rfile);
                try
                {
                    SendStream(this.packet.getAddress(), rfile);
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            }
        }


        private void SendStream(InetAddress address, String 
            fileName)
            throws IOException
            {

            File file = new File(fileName);
            FileInputStream fis = new FileInputStream(file);
            DatagramPacket pack;

            int size = 0;
            byte[] fileBuffer = new byte[(int) file.length()];
            byte[] buffer = new byte[512];
            System.out.println("Transfer started...");
            while (true)
            {
                size = fis.read(buffer);
                byte[] sizeBuff = new byte[size]; 
                sizeBuff = buffer;
                pack = new DatagramPacket(sizeBuff, size, address,
                        packet.getPort());
                socket.send(pack);
                if (size < buffer.length)
                {
                    System.out.println("Transfer finished.");
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
