import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.BitSet;

public class test2
{
    public static void main(String[] args)
    {
        byte[] array = new byte[500];
        array = "hello".getBytes();
        BitSet bs = BitSet.valueOf(toBinary(array).);        
        System.out.println(toBinary(array));
   } 

    public static String toBinary(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
        for( int i = 0; i < Byte.SIZE * bytes.length; i++ )
            sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
        return sb.toString();
    }
}
