import java.io.*;
import java.nio.*;
import java.util.*;

/****************************************************

  Layout of TFS on the hard disk

  0         1...      n..                                 m
  +---------+---------+-----------------------------------+
  |         |         |                                   |
  |   PCB   |   FAT   |   Data Blocks                     |
  |         |         |                                   |
  +---------+---------+-----------------------------------+

  PCB will be in block 0. Holds the following information:

    pcb_root            - first block of the PCB
    pcb_size            - number of blocks allocated to PCB
    pcb_fs_size         - total number of blocks in the TFS Disk
    pcb_fat_root        - first block of the FAT
    pcb_fat_size        - the number of index blocks in the FAT table. values will range  
                          from [0, m -n]. Index value 0 can be used as null pointer since  
                          it will hold the root directory by default (We will assume we   
                          never want to delete the root directory because if we did, there
                          would be no entry point for the file system when mounting).     
    pcb_data_block_root - first block of the data block
    pcb_data_block_size - number of blocks allocated to data blocks


  FAT will be in block 1. Additional blocks may be allocated depending on the size
  of the table. Data structure to hold block indices will be a hash table (i.e fixed
  length array) where values are keys to other block indices thus forming a linked
  list for sequential block access. Key-value pairs in the FAT will need to be offset
  by data_block_root when requesting disk for the specific block.

  Free-space allocation:
  Since the FAT will have to be read from memory anyways, the free-space list
  can be built when mounting the fs with no additonal cost. As the
  FAT is read into memory, any null valued entries (i.e 0) can be added to the free-space
  list.

  Data Blocks will hold the files. Directory structure is also included here since
  directories can be regarded as files. The first block (i.e block n) will be the root
  (e.g "/") directory to form a tree structure.

  Each entry in the directory structure will either be a file or directory:

  int      parent_block - index of FAT into the parent directory
  byte     directory    - 1 if is directory else 0
  byte[16] name         - name of the entry, limited to 16 characters as per requirement
  byte     nlength      - length of the name. Max number of chars is 16 which is suitable
                          for the 8 bits allocated to the byte. If we has used int, then
                          3 of the 4 bytes allocated would be wasted
  int      block        - index into the first block in FAT
  int      size         - size of the file in bytes. NOT BLOCKS.
  byte[2]  padding      - padding to ensure each directory entry is 32 bytes.
                          This makes writing/reading data from blocks easier
                          since exactly 4 entries can fit per block. Don't need to
                          write complicated algorithm to read/write data entries that
                          have been split into multiple data blocks

  in total, 4+1+16+1+4+4+2 = 32 bytes per entry, thus allowing exactly 4 entries per block

  Note:
  The file system is designed so exceptions are handled by the caller all the
  way up to the TFSShell class so any thrown exceptions can be handled in one place.
  Also, the code is more readable without the try-catch clauses

 ****************************************************/

public class TFSFileSystem
{
  private static final String TFSDiskFile = "TFSDiskFile";

  /***************************
   * Partition Control Block *
   ***************************/
  private static int pcb_root;            // 0
  private static int pcb_size;            // 1

  // pcb(1) + fat(32) + data blocks(1024)
  // Require 32 blocks for fat because there are 1024 data blocks.
  // Given value are int, 4 bytes will consist of one fat entry
  // thus (32*128)/4 = 1024
  private static int pcb_fs_size;         // 1+32+1024
  private static int pcb_fat_root;        // 1  
  private static int pcb_fat_size;        // 32
  private static int pcb_data_block_root; // pcb_size+pcb_fat_size
  private static int pcb_data_block_size; // 1024
  
  /***************************
   * File Allocation Table   *
   ***************************/
  // integer arrays default to 0 as per Java Language Specification.
  // 0 indicates a null pointer since block 0 is the PCB
  private static int[] fat;               // new int[pcb_fat_size*BLOCK_SIZE]

  /***************************
   * Flags                   *
   ***************************/
  private static boolean fs_mounted  = false;


  /*
   * TFS API
   */
  public static int tfs_mkfs() throws IOException
  {
    // Do not allow file system creation while a file is mounted
    if (fs_mounted) throw new TFSException("Cannot create file system while mounted");

    // Assign default values. Must be care when making modifications,
    // especially the # of entries in FAT must match # of data blocks
    pcb_root        = 0;
    pcb_size        = 1;
    pcb_fs_size     = 1+32+1024;
    pcb_fat_root    = 1;
    pcb_fat_size    = 32;
    pcb_data_block_root = pcb_size+pcb_fat_size;
    pcb_data_block_size = 1024;
                      // default initializes to 0 by Java Lang. Spec
    fat             = new int[pcb_fat_size*TFSDiskInputOutput.BLOCK_SIZE];

    // Create disk file with default values
    TFSDiskInputOutput.tfs_dio_create(TFSDiskFile.getBytes(),
                                      TFSDiskFile.length(),
                                      pcb_fs_size);

    // Open disk file to read/write
    TFSDiskInputOutput.tfs_dio_open(TFSDiskFile.getBytes(),TFSDiskFile.length());
    // set mount flag not utility functions can write to disk
    fs_mounted = true;
    // Write pcb to disk
    _tfs_write_pcb();
    // Write fat to disk
    _tfs_write_fat();

    // Write directory entry into first data block
    ByteBuffer buffer = ByteBuffer.allocate(TFSDiskInputOutput.BLOCK_SIZE);    
    buffer.putInt(0);              // int      parent_block - root is parent to itself
    buffer.put((byte) 1);          // byte     directory    - root is directory
    buffer.put(new byte[16]);      // byte[16] name         - root has no name
    buffer.put((byte) 0);          // byte     nlength      - root's name is length 0
    buffer.put((byte) 0);          // int      block        - null pointer
    buffer.put((byte) 0);          // int      size         - root directory is initially empty
    buffer.put(new byte[2]);       // byte[2]  padding  
    
    TFSDiskInputOutput.tfs_dio_close();    
    fs_mounted = false;
    return 0;
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

  /*
   * PCB related utilities
   */

  private static void _tfs_write_pcb() throws IOException {
    if (!fs_mounted)
      throw new TFSException("Cannot write to PCB. Disk not mounted");
    if (!TFSDiskInputOutput.is_open())
      throw new TFSException("Cannot write to PCB. Disk not open");    
    
    ByteBuffer buffer = ByteBuffer.allocate(TFSDiskInputOutput.BLOCK_SIZE);
    // Write pcb to disk
    buffer.putInt(pcb_root);
    buffer.putInt(pcb_size);
    buffer.putInt(pcb_fs_size);
    buffer.putInt(pcb_fat_root);    
    buffer.putInt(pcb_fat_size);
    buffer.putInt(pcb_data_block_root);
    buffer.putInt(pcb_data_block_size);    
    TFSDiskInputOutput.tfs_dio_write_block(pcb_root,buffer.array());
  }

  /*
   * FAT related utilities
   */

  private static void _tfs_write_fat() throws IOException {
    if (!fs_mounted)
      throw new TFSException("Cannot write to PCB. Disk not mounted");
    if (!TFSDiskInputOutput.is_open())
      throw new TFSException("Cannot write to PCB. Disk not open");    

    ByteBuffer buffer = ByteBuffer.allocate(TFSDiskInputOutput.BLOCK_SIZE);
    // Write fat to disk
    // A 128 byte block can hold 32 entries (i.e 128/4)
    int num_entries_per_block = TFSDiskInputOutput.BLOCK_SIZE/4;
    for (int i = 0; i < pcb_fat_size; ++i){
      buffer.clear();
      for (int j = 0; j < num_entries_per_block; ++j){
        buffer.putInt(fat[i*num_entries_per_block+j]); // add value, 0 for null pointer
      }
      TFSDiskInputOutput.tfs_dio_write_block(pcb_fat_root+i,buffer.array());
    }
  }
}
