import java.io.*;
import java.util.*;

public class TFSFileSystem
{
  /*
   * TFS Constructor
   */
	 
  public void TFSFileSystem()
  {
  }
	
	
  /*
   * TFS API
   */
	 
  public static int tfs_mkfs()
  {
    return -1;
  }						

  public static int tfs_mount()
  {
    return -1;
  }					

  public static int tfs_umount()
  {
    return -1;
  }						

  public static int tfs_sync()	
  {
    return -1;
  }						

  public static String tfs_prrfs()	
  {
    return null;
  }					

  public static String tfs_prmfs()
  {
    return null;
  }

  public static int tfs_open(byte[] name, int nlength)
  {
    return -1;
  }			

  public static int tfs_read(int file_id, byte[] buf, int blength)	
  {
    return -1;
  }

  public static int tfs_write(int file_id, byte[] buf, int blength)
  {
    return -1;
  }	

  public static int tfs_seek(int file_id, int position)
  {
    return -1;
  }	

  public static void tfs_close(int file_id)
  {
    return;
  }			

  public static int tfs_create(byte[] name, int nlength)
  {
    return -1;
  }		

  public static int tfs_delete(byte[] name, int nlength)		
  {
    return -1;
  }	

  public static int tfs_create_dir(byte[] name, int nlength)	
  {
    return -1;
  }	

  public static int tfs_delete_dir(byte[] name, int nlength)	
  {
    return -1;
  }	
	
	
  /*
   * TFS private methods to handle in-memory structures
   */
	 
  private static int _tfs_read_block(int block_no, byte buf[])
  {
    return -1;
  }
 	
  private static int _tfs_write_block(int block_no, byte buf[])
  {
    return -1;
  }
 	
  private static int _tfs_open_fd(byte name[], int nlength)
  {
    return -1;
  }
 	
  private static int _tfs_seek_fd(int fd, int offset)
  {
    return -1;
  }
 	
  private static void _tfs_close_fd(int fd)
  {
    return;
  }
 	
  private static int _tfs_get_block_no_fd(int fd, int offset)
  {
    return -1;
  }
}
