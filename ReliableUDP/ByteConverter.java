import java.nio.ByteBuffer;

/*
 * Program Name:    ByteConverter.java
 * Author(s):       Jeremy Wheaton, 100105823
 *                  Cody McCarthy,  100097829 
 * Version:         3.0 - Nov 30, 2012
 * Purpose:
 * This class is used to convert bytes to ints and ints to bytes.
 * Also has some support for long.
 */


public class ByteConverter {
  //This method converts ints into bytes.
  static byte[] toBytes(int i)
  {
    //The byte  array to return 

    byte[] result = new byte[4];

    //Manually puts the int into it's byte posisitions
    result[0] = (byte) (i >> 24);
    result[1] = (byte) (i >> 16);
    result[2] = (byte) (i >> 8);
    result[3] = (byte) (i /*>> 0*/);

    return result;
  }
  //This method covertes by to ints 
  static int toInt(byte[] bytes, int offset) {
    int ret = 0;
    for (int i=0; i<4 && i+offset<bytes.length; i++) {
      ret <<= 8;
      ret |= (int)bytes[i] & 0xFF;
    }
    return ret;

  }
  //This method converts long sto bytes.
  static byte[] longToBytes(long value){
    ByteBuffer bb = ByteBuffer.allocate(8);
    return bb.putLong(value).array();

  }

}
