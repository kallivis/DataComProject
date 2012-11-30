/*
 * Program Name:    Settings.java
 * Author(s):       Jeremy Wheaton, 100105823
 *                  Cody McCarthy,  100097829 
 * Version:         3.0 - Nov 30, 2012
 * Purpose:
 * An interface containing all the constant values used  
 * by the client and server.
 */
public interface Settings {

  public final static int PACKET_SIZE = 8092;
  public final static int MAX_SEQ = 7; 
  public final static int INT_SIZE = 4;
  public final static int WINDOW_SIZE = (MAX_SEQ) / 2;
  public final static int CHECKSUM_SIZE = 4;
  public final static int SAC_SIZE = INT_SIZE+CHECKSUM_SIZE;
  public final static int THREAD_TIME = 80;
  public final static int DATA_SIZE = PACKET_SIZE-(INT_SIZE + CHECKSUM_SIZE);

}


