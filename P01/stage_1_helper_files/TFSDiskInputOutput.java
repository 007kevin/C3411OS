import java.io.*;
import java.util.*;

public class TFSDiskInputOutput
{
  /*
   * Disk I/O API
   */
  private static final int BLOCK_SIZE = 128;
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
    if (RAF != null) throw new TFSException("FS already open");
    String fname = new String(name, 0, nlength);
    RAF = new RandomAccessFile(fname, "rw");
    return 0;
  }

  public static int tfs_dio_get_size() throws IOException
  {
    if (RAF == null) throw new TFSException("Cannot retrieve size. FS not open");
    return (int) RAF.length()/BLOCK_SIZE;

  }

  public static int tfs_dio_read_block(int block_no, byte[] buf) throws IOException
  {
    if (RAF == null) throw new TFSException("Cannot read block. FS not open");
    if (buf.length > BLOCK_SIZE) throw new TFSException("Buffer greater than block size");    
    RAF.seek(block_no * BLOCK_SIZE);
    RAF.read(buf);
    return 0;
  }

  public static int tfs_dio_write_block(int block_no, byte[] buf) throws IOException
  {
    if (RAF == null) throw new TFSException("Cannot write block. FS not open");
    if (buf.length > BLOCK_SIZE) throw new TFSException("Buffer greater than block size");
    RAF.seek(block_no * BLOCK_SIZE);
    RAF.write(buf);
    return 0;
  }

  public static void tfs_dio_close() throws IOException
  {
    if (RAF == null) throw new TFSException("Cannot close block. FS not open");
    RAF.close();
    RAF = null;
  }

  public static boolean is_open() {
    return RAF != null;
  }
}
