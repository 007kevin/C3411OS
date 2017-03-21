import java.io.*;
import java.util.*;

public class TFSDiskInputOutput
{
  /*
   * Disk I/O API
   */
  private static final int BLOCK_SIZE = 128;
  private static final RandomAccessFile RAF = null;
	 
  public static int tfs_dio_create(byte[] name, int nlength, int size) throws IOException
  {
    String fname = new String(name, 0, nlength);
    // Create the TFSDiskFile
    RandomAccessFile raf = new RandomAccessFile(fname, "rw");
    // Set the desired file length in BLOCK_SIZE units
    raf.setLength(BLOCK_SIZE*size);
    return 0;
  }	
	
  public static int tfs_dio_open(byte[] name, int nlength) throws IOException
  {
    return -1;
  }			
	
  public static int tfs_dio_get_size()
  {
    return -1;
  }							
	
  public static int tfs_dio_read_block(int block_no, byte[] buf)
  {
    return -1;
  }
	
  public static int tfs_dio_write_block(int block_no, byte[] buf)	
  {
    return -1;
  }
	
  public static void tfs_dio_close()		
  {
    return;
  }
}
