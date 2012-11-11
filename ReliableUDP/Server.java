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

    //This is the size of the packets being sent and recieved 
    private final static int PACKET_SIZE = 10;

    public static void main(String[] args) throws Exception
    {
        //The buffer for the initial message received from the Client
        byte[] buff = new byte[PACKET_SIZE];
        
        //Creates the Packet to receive the request from the Client
        DatagramPacket packet = new DatagramPacket(buff, buff.length);
        //Opens a Datagram Socket that the server is running on
        //Opens it on port 3031
        DatagramSocket socket = new DatagramSocket(3031);

        System.out.println("Server started at 3031 ...");

        //While server is running, Constantly listens for client requests
        while (true) {
            //Receives the request packet from the client
            socket.receive(packet);
            //starts a new thread to send the packets of file
            //requested by the client.
            new Server.TransferThread(socket, packet).run();

        }
    }
    //Thread class for Transfering Packets
    public static class TransferThread implements Runnable
    {
        //Sets up a packet and Socket
        private DatagramPacket packet;
        private DatagramSocket socket;

        //Constructer for the thread Class
        //Takes a Socket and Packet as parameters
        public TransferThread(DatagramSocket socket, DatagramPacket packet) 
        {
            this.packet = packet;
            this.socket = socket;
        }
        //The run method of the thread used to check on the request
        //and if a file is requested starts the sendStream 
        public void run()
        {
            //Uses the received packet data from the client to 
            //setup a string with the requested filename
            String filename = new String(packet.getData(), 0, 
                    packet.getLength());
            //If the requested message is "EXIT" then it shuts down the server
            if (filename.equals("EXIT"))
            {
                System.out.println("Server Closing.");
                System.exit(0);
            }
            else
            {
                System.out.println("Requested: " + filename);
                try
                {
                    //Starts the SendStream for the given filename
                    SendStream(filename);
                } 
                catch (IOException e) {
                    e.printStackTrace();
                } 
            }
        }

        //Method that takes a filename as it's param
        //and sends the requested file in packets
        //to the client that requested it. 
        private void SendStream(String fileName) throws IOException
        {
            //Opens a file and a makes a file object from the
            //requested filename for reading.
            File file = new File(fileName);
            //Creates a fileInputStream for reading in the file into a buffer
            FileInputStream fis = new FileInputStream(file);
            //Creates a DatagramPacket packet that will be used to send 
            //data to the client
            DatagramPacket pack;

            //Size that will hold the size of the buffer being sent
            int size = 0;
            // The remaining size that is left to send of the file
            int remainingSize = (int) file.length(); 
            //The buffer that the file will be read into with a max size
            //determined by the PACKET_SIZE constant
            byte[] buffer = new byte[PACKET_SIZE];
            System.out.println("Transfer started...");
            while (true)
            {
                //Reads part of the file into the buffer and sets the size of
                //the amount read
                size = fis.read(buffer);
                //Updates the remaining size by the amount read above
                remainingSize -= size;
                //Creates a new buffer the size of the amount read
                //This is in case the size is less than the PACKET_SIZE 
                //constant, so that the buffer is of exact size of the
                //data that we are sending
                byte[] sizeBuff = new byte[size]; 
                //Copies the buffer into this new buffer
                sizeBuff = buffer;
                //Creates a new packet with the above buffer of data.
                //Uses the initial client packet to get the client's 
                //address and port.
                pack = new DatagramPacket(sizeBuff, size, packet.getAddress(),
                        packet.getPort());
                //Sends the above packet to the client
                socket.send(pack);
                //If the size send was less than PACKET_SIZE then the last
                //packet was sent and Server is done transfering
                if (size < PACKET_SIZE)
                {
                    System.out.println("Transfer finished.");
                    break;
                }
                //If data was a multiple of PACKET_SIZE then the last packet
                //when remaining size is equal to 0.
                //An empty packet is then sent to the client to signal
                //that the transfer is complete.
                if (remainingSize == 0)  
                {
                    //Creates the empty packet to send to the client to signal
                    //that the transfer is complete
                    pack = new DatagramPacket(new byte[0] , 0, 
                            packet.getAddress(),packet.getPort());
                    //Sends the empty packet
                    socket.send(pack);
                    System.out.println("Transfer finished.");
                    break;

                }

            }
        }
    }
}
