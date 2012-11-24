public class ByteConverter {
  static byte[] toBytes(int i)
  {
    byte[] result = new byte[4];

    result[0] = (byte) (i >> 24);
    result[1] = (byte) (i >> 16);
    result[2] = (byte) (i >> 8);
    result[3] = (byte) (i /*>> 0*/);

    return result;
  }
  static int toInt(byte[] bytes, int offset) {
    int ret = 0;
    for (int i=0; i<4 && i+offset<bytes.length; i++) {
      ret <<= 8;
      ret |= (int)bytes[i] & 0xFF;
    }
    return ret;

  }

}
