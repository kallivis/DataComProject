public interface Settings {
  public final static int PACKET_SIZE = 264;
  public final static int MAX_SEQ = 7; 
  public final static int INT_SIZE = 4;
  public final static int WINDOW_SIZE = (MAX_SEQ) / 2;
  public final static int CHECKSUM_SIZE = 4;
  public final static int SAC_SIZE = INT_SIZE+CHECKSUM_SIZE;
  public final static int THREAD_TIME = 80;
  public final static int DATA_SIZE = PACKET_SIZE-(INT_SIZE * 2);
}


