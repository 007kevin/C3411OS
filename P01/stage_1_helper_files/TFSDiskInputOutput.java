import java.io.*;
import java.util.*;

public class TFSDiskInputOutput
{
  /*
   * Disk I/O API
   */

  // Size of blocks in disk.
  public static final int BLOCK_SIZE = 128;
  // Pointer to opened file
  private static RandomAccessFile RAF = null;

  public static int tfs_dio_create(byte[] name, int nlength, int size) throws IOException
  {
    String fname = new String(name, 0, nlength);
    // Create the TFSDiskFile
    RAF = new RandomAccessFile(fname, "rw");
    // Set the desired file length in BLOCK_SIZE units
    RAF.setLength(BLOCK_SIZE*size);
    tfs_dio_close();
    return 0;
  }

  public static int tfs_dio_open(byte[] name, int nlength) throws IOException
  {

    // Ensure no files are open before attempting to open
    if (RAF != null) throw new TFSException("FS already open");
    String fname = new String(name, 0, nlength);
    RAF = new RandomAccessFile(fname, "rw");
    return 0;
  }

  public static int tfs_dio_get_size() throws IOException
  {
    // Ensure file is open before attempting to get number of blocks
    if (RAF == null) throw new TFSException("Cannot retrieve size. FS not open");
    return (int) RAF.length()/BLOCK_SIZE;

  }

  public static int tfs_dio_read_block(int block_no, byte[] buf) throws IOException
  {
    // File must be open before attempting to read
    if (RAF == null) throw new TFSException("Cannot read block. FS not open");
    // byte buffer must be less than block size
    if (buf.length > BLOCK_SIZE) throw new TFSException("Buffer greater than block size");    
    RAF.seek(block_no * BLOCK_SIZE);
    RAF.read(buf);
    return 0;
  }

  public static int tfs_dio_write_block(int block_no, byte[] buf) throws IOException
  {
    // File must be open before attempting to read    
    if (RAF == null) throw new TFSException("Cannot write block. FS not open");
    // byte buffer must be less than block size    
    if (buf.length > BLOCK_SIZE) throw new TFSException("Buffer greater than block size");
    RAF.seek(block_no * BLOCK_SIZE);
    RAF.write(buf);
    return 0;
  }

  public static void tfs_dio_close() throws IOException
  {
    // Cannot close a file that is not open
    if (RAF == null) throw new TFSException("Cannot close block. FS not open");
    RAF.close();
    RAF = null;
  }

  // Test is file is open. Used in jUnit testing
  public static boolean is_open() {
    return RAF != null;
  }
}
