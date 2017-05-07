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
    pcb_fat_size        - the number of index blocks in the FAT table values will range
                          from [0, m -n]. Index value -1 can be used as null pointer.
                          We will assume we never want to delete the root directory
                          because if we did, there would be no entry point for the
                          file system when mounting.
    pcb_data_block_root - first block of the data block
    pcb_data_block_size - number of blocks allocated to data blocks
    free_ptr            - pointer to first free block. File system initialization
                          will set this to 1 by default


  FAT will be in block 1. Additional blocks may be allocated depending on the size
  of the table. Data structure to hold block indices will be a hash table (i.e fixed
  length array) where values are keys to other block indices thus forming a linked
  list for sequential block access. Key-value pairs in the FAT will need to be offset
  by data_block_root when requesting disk for the specific block.

  Free-space allocation:
  Free space list will be a linked list in the fat initialized during mkfs.

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

  *********************************************************************************
  FDT - File Descriptor Table

  Table of abstract file handles used for accessing files. File manipulations
  such are read/write will be done onto FDT entries. Writing to FDT will make sure
  data is written in block chunks.

  Default to maximum 100 entries per table and fdt allocation will always allocate
  the first available entry starting from index 0. Null values to FDT indicate
  free entry.

  A file entry will hold the following information about a file:
    boolean d;    // directory flag
    String name;  //file name
    int block;    // first block number of the file
    int offset;   // file pointer offset in bytes
    int size;     // size of file in bytes
  *********************************************************************************


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

  /**********************************
   * Free Space List (stored in PCB *
   **********************************/
  // free space list will point to index 1 of FAT during file system initialization.
  private static int free_ptr;





  /***************************
   * File Allocation Table   *
   ***************************/
  // integer arrays default to 0 as per Java Language Specification.
  // 0 indicates a null pointer since block 0 is the PCB
  private static int[] fat = null;               // new int[pcb_fat_size*BLOCK_SIZE]

  /***************************
   * Flags                   *
   ***************************/
  private static boolean fs_mounted  = false;

  /***************************
   * File Descriptor Table   *
   ***************************/
  private static class FileD {
    FileD p; // parent entry
    int p_offset; // offset to entry from parent
    boolean d; // directory flag
    String name; //file name
    int block;  // first block number of the file
    int offset; // file pointer offset in bytes
    int size; // size of file in bytes

    public FileD(FileD p, int p_offset, boolean d,
                 String name, int block, int size){
      this.p = p;
      this.p_offset = p_offset;
      this.d = d;
      this.name = name;
      this.block = block;
      this.offset = 0;
      this.size = size;
    }
  }

  private static FileD fdt[] = null;

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
    free_ptr            =  1;
                    // default initializes to 0 by Java Lang. Spec
    fat             = new int[(pcb_fat_size*TFSDiskInputOutput.BLOCK_SIZE)/4];

    // build free space list
    for (int i = 1; i < pcb_data_block_size-1; ++i)
      fat[i] = i+1;
    fat[pcb_data_block_size-1] = -1; // end free space list

    // Create disk file with default values
    TFSDiskInputOutput.tfs_dio_create(TFSDiskFile.getBytes(),
                                      TFSDiskFile.length(),
                                      pcb_fs_size);

    // Open disk file to read/write
    TFSDiskInputOutput.tfs_dio_open(TFSDiskFile.getBytes(),TFSDiskFile.length());
    // set mount flag not utility functions can write to disk
    fs_mounted = true;
    // Write pcb and fat to disk
    tfs_sync();

    // Write directory entry into first data block
    ByteBuffer buffer = ByteBuffer.allocate(TFSDiskInputOutput.BLOCK_SIZE);
    buffer.putInt(0);              // 0  int      parent_block - root is parent to itself
    buffer.put((byte) 1);          // 4  byte     directory    - root is directory
    buffer.put(new byte[16]);      // 5  byte[16] name         - root has no name
    buffer.put((byte) 0);          // 21 byte     nlength      - root's name is length 0
    buffer.putInt(0);              // 22 int      block        - null pointer
    buffer.putInt(0);              // 26 int      size         - root directory is initially empty
    buffer.put(new byte[2]);       // 30 byte[2]  padding
    TFSDiskInputOutput.tfs_dio_write_block(pcb_data_block_root,buffer.array());

    TFSDiskInputOutput.tfs_dio_close();
    fs_mounted = false;
    return 0;
  }

  public static int tfs_mount() throws IOException
  {
    if (fs_mounted)
      throw new TFSException("File system already mounted");
    if (TFSDiskInputOutput.is_open())
      throw new TFSException("Disk already open");

    TFSDiskInputOutput.tfs_dio_open(TFSDiskFile.getBytes(),TFSDiskFile.length());
    fs_mounted = true;

                      // default initialize java objects to null
    fdt             = new FileD[100];


    // note: PCB should always be read first since read_fat needs the values from the PCB
    _tfs_read_pcb();
    _tfs_read_fat();
    return 0;
  }

  public static int tfs_umount() throws IOException
  {
    if (!fs_mounted)
      throw new TFSException("File system already unmounted");
    if (!TFSDiskInputOutput.is_open())
      throw new TFSException("Disk is closed. Cannot write memory out to disk");
    fdt = null;
    _tfs_write_pcb();
    _tfs_write_fat();
    TFSDiskInputOutput.tfs_dio_close();
    fs_mounted = false;
    return 0;
  }

  public static int tfs_exit() throws IOException {
    if (fs_mounted){
      tfs_umount();
    }
    return 0;
  }

  public static int tfs_sync() throws IOException
  {
    _tfs_write_pcb();
    _tfs_write_fat();
    return 0;
  }

  // Print PCB and FAT in the file system (disk)
  public static String tfs_prrfs() throws IOException
  {
    if (!fs_mnounted)
      throw new TFSException("Cannot print records. File system not mounted");
    if (!TFSDiskInputOutput.is_open())
      throw new TFSException("Cannot print records. Disk not open");

    byte[] buffer = new byte[TFSDiskInputOutput.BLOCK_SIZE];
    ByteBuffer bb =  ByteBuffer.wrap(buffer);
    TFSDiskInputOutput.tfs_dio_read_block(pcb_root, buffer);
    int disk_pcb_root = bb.getInt();
    int disk_pcb_size = bb.getInt();
    int disk_pcb_fs_size = bb.getInt();
    int disk_pcb_fat_root = bb.getInt();
    int disk_pcb_fat_size = bb.getInt();
    int disk_pcb_data_block_root = bb.getInt();
    int disk_pcb_data_block_size = bb.getInt();

    String output = "";
    output+="Disk\n";
    output+="  +---------+---------+-----------------------------------+\n";
    output+="  |         |         |                                   |\n";
    output+="  |   PCB   |   FAT   |   Data Blocks                     |\n";
    output+="  |         |         |                                   |\n";
    output+="  +---------+---------+-----------------------------------+\n";
    output+="File System:\n";
    output+="  block size = " + TFSDiskInputOutput.BLOCK_SIZE + " bytes\n";
    output+="  total disk blocks = " + disk_pcb_fs_size + " \n";
    output+="PCB:\n";
    output+="  root = " + disk_pcb_root + "\n";
    output+="  size = " + disk_pcb_size + "\n";
    output+="FAT:\n";
    output+="  root = " + disk_pcb_fat_root + "\n";
    output+="  size = " + disk_pcb_fat_size + "\n";
    output+="Data Blocks:\n";
    output+="  root = " + disk_pcb_data_block_root + "\n";
    output+="  size = " + disk_pcb_data_block_size + "\n";
    output+="Free Space:\n";
    output+="  free_ptr = " + free_ptr + "\n";

    int num = 0;
    int ptr = free_ptr;
    while(ptr != -1){
      num++;
      ptr=fat[ptr];
    }
    output+="  free bytes = " + num + "\n";

    return output;
  }

  // Print PCB and FAT in memory
  public static String tfs_prmfs() throws IOException
  {
    if (!fs_mounted)
      throw new TFSException("Cannot print records. File system not mounted");
    if (!TFSDiskInputOutput.is_open())
      throw new TFSException("Cannot print records. Disk not open");

    String output = "";
    output+="Memory\n";
    output+="  +---------+---------+-----------------------------------+\n";
    output+="  |         |         |                                   |\n";
    output+="  |   PCB   |   FAT   |   Data Blocks                     |\n";
    output+="  |         |         |                                   |\n";
    output+="  +---------+---------+-----------------------------------+\n";
    output+="File System:\n";
    output+="  block size = " + TFSDiskInputOutput.BLOCK_SIZE + " bytes\n";
    output+="  total disk blocks = " + pcb_fs_size + " \n";
    output+="PCB:\n";
    output+="  root = " + pcb_root + "\n";
    output+="  size = " + pcb_size + "\n";
    output+="FAT:\n";
    output+="  root = " + pcb_fat_root + "\n";
    output+="  size = " + pcb_fat_size + "\n";
    output+="Data Blocks:\n";
    output+="  root = " + pcb_data_block_root + "\n";
    output+="  size = " + pcb_data_block_size + "\n";
    output+="Free Space:\n";
    output+="  free_ptr = " + free_ptr + "\n";
    int num = 0;
    int ptr = free_ptr;
    while(ptr != -1){
      num++;
      ptr=fat[ptr];
    }
    output+="  free bytes = " + num + "\n";

    return output;
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
    buffer.putvInt(pcb_size);
    buffer.putInt(pcb_fs_size);
    buffer.putInt(pcb_fat_root);
    buffer.putInt(pcb_fat_size);
    buffer.putInt(pcb_data_block_root);
    buffer.putInt(pcb_data_block_size);
    buffer.putInt(free_ptr);
    TFSDiskInputOutput.tfs_dio_write_block(pcb_root,buffer.array());
  }

  private static void _tfs_read_pcb() throws IOException {
    if (!fs_mounted)
      throw new TFSException("Cannot write to PCB. Disk not mounted");
    if (!TFSDiskInputOutput.is_open())
      throw new TFSException("Cannot write to PCB. Disk not open");

    // read pcb from disk
    byte[] buffer = new byte[TFSDiskInputOutput.BLOCK_SIZE];
    ByteBuffer bb = ByteBuffer.wrap(buffer);
    TFSDiskInputOutput.tfs_dio_read_block(0,buffer);
    pcb_root = bb.getInt();
    pcb_size = bb.getInt();
    pcb_fs_size = bb.getInt();
    pcb_fat_root = bb.getInt();
    pcb_fat_size = bb.getInt();
    pcb_data_block_root = bb.getInt();
    pcb_data_block_size = bb.getInt();
    free_ptr = bb.getInt();
  }

  /*
   * FAT related utilities
   */

  private static void _tfs_write_fat() throws IOException {
    if (!fs_mounted)
      throw new TFSException("Cannot write to PCB. Disk not mounted");
    if (!TFSDiskInputOutput.is_open())
      throw new TFSException("Cannot write to PCB. Disk not open");

    // Write fat to disk
    // A 128 byte block can hold 32 entries (i.e 128/4)
    int num_entries_per_block = TFSDiskInputOutput.BLOCK_SIZE/4;

    ByteBuffer buffer = ByteBuffer.allocate(TFSDiskInputOutput.BLOCK_SIZE);
    for (int i = 0; i < pcb_fat_size; ++i){
      buffer.clear();
      for (int j = 0; j < num_entries_per_block; ++j){
        buffer.putInt(fat[i*num_entries_per_block+j]); // add value, 0 for null pointer
      }
      TFSDiskInputOutput.tfs_dio_write_block(pcb_fat_root+i,buffer.array());
    }
  }

  private static void _tfs_read_fat() throws IOException {
    if (!fs_mounted)
      throw new TFSException("Cannot write to PCB. Disk not mounted");
    if (!TFSDiskInputOutput.is_open())
      throw new TFSException("Cannot write to PCB. Disk not open");

    // read fat from disk
    byte[] buffer = new byte[TFSDiskInputOutput.BLOCK_SIZE];
    ByteBuffer bb = ByteBuffer.wrap(buffer);
    int num_entries_per_block = TFSDiskInputOutput.BLOCK_SIZE/4;

    // check int array is defined
    if (fat == null)
      fat = new int[(pcb_fat_size*TFSDiskInputOutput.BLOCK_SIZE)/4];

    for (int i = 0; i < pcb_fat_size; ++i){
      TFSDiskInputOutput.tfs_dio_read_block(pcb_fat_root+i,buffer);
      bb.clear();
      for (int j = 0; j < num_entries_per_block; ++j){
        fat[i*num_entries_per_block+j] = bb.getInt();
      }
    }
  }

  /*
   * File related - private helpers
   */
  private static int get_block() throws IOException {
    if (free_ptr == -1)
      throw new TFSException("No free space available");
    int n = free_ptr;
    free_ptr = fat[free_ptr];
    return n;
  }

  private static void free_blocks(int s, int e){
    fat[e] = free_ptr;
    free_ptr = s;
  }

  private static void read_block(int block, byte buf[]) throws IOException {
    TFSDiskInputOutput.tfs_dio_read_block(block+pcb_data_block_root,buf);
  }

  private static void write_block(int block, byte buf[]) throws IOException
  {
    TFSDiskInputOutput.tfs_dio_write_block(block+pcb_data_block_root,buf);
  }

  private static void read_fd(FileD fd, byte buf[]) throws IOException {
    int BS = TFSDiskInputOutput.BLOCK_SIZE;
    int len = buf.length;

    // move to block of offset
    int block = fd.block;
    for (int i = 0; i < fd.offset/BS; ++i)
      block = fat[block];

    int ceil = (fd.offset%BS+len+BS-1)/BS;
    ByteBuffer b = ByteBuffer.allocate(ceil*BS);
    byte[] tmp = new byte[BS];
    for (int i = 0; i < ceil; ++i){
      read_block(block,tmp);
      b.put(tmp);
      block=fat[block];
    }
    ByteBuffer dst = ByteBuffer.wrap(buf);
    dst.put(b.array(),fd.offset%BS,len);
  }

  private static void write_fd(FileD fd, byte buf[]) throws IOException {
    int BS = TFSDiskInputOutput.BLOCK_SIZE;
    int len = buf.length;

    // If file has no blocks allocated
    if (fd.size == 0){
      fd.block = get_block();
      fd.size = Math.min(BS,len);
    }

    // check if new blocks must still be allocated
    // 1) watch for edge case when writing aligns with block size
    // 2) watch for edge case when size aligns with block size
    int n = ((fd.offset+len+BS-1)/BS) - ((fd.size+BS-1)/BS);
    if (n > 0){
      // move to last block
      int block = fd.block;
      for (int i = 0; i < (fd.size-1)/BS; ++i) // (a+b-1)/b - 1 => (a-1)/b
        block = fat[block];                    // find block size resides on even if size
      int c = free_ptr;                        // aligns with block size
      for (int i = 0; i < n-1 && c != -1; ++i){
        c = fat[c];
      }
      if (c == -1) throw new TFSException("No space available, write aborted.");
      fat[block] = free_ptr;
      free_ptr = fat[c];
      fat[c] = -1;
    }

    // read blocks to buffer
    int m = (fd.offset%BS+len+BS-1)/BS; // number of blocks to read
    ByteBuffer b = ByteBuffer.allocate(m*BS);
    byte tmp[] = new byte[BS];

    // move to block of offset
    int offset_block = fd.block;
    for (int i = 0; i < fd.offset/BS; ++i) // it is okay if offset aligns with block
      offset_block = fat[offset_block];

    // write data to buffer
    int block = offset_block;
    for (int i = 0; i < m; ++i){
      read_block(block,tmp);
      b.put(tmp);
      block = fat[block];
    }

    b.position(fd.offset%BS);
    b.put(buf,0,len);
    b.position(0);

    // write data back to disk
    block = offset_block;
    for (int i = 0; i < m; ++i){
      b.get(tmp,0,BS);
      write_block(block,tmp);
      block = fat[block];
    }

    // update file pointer
    fd.offset += len;

    // update size of file descriptor
    if (fd.offset > fd.size)
      fd.size = fd.offset;

    //    fd.update();
  }

  // propagate up directory tree and update file size changes
  private static void update_fd(FileD fd) throws IOException {
    if (fd.p == null){
      int BS = TFSDiskInputOutput.BLOCK_SIZE;
      ByteBuffer b = ByteBuffer.allocate(BS);
      read_block(0,b.array());
      b.position(22);
      b.putInt(fd.block);
      b.putInt(fd.size);
      write_block(0,b.array());
      return;
    }

    // set up buffer for writing
    ByteBuffer b = ByteBuffer.allocate(32);
    b.putInt(fd.p.block);
    b.put((byte) (fd.d?1:0));
    b.put(ByteBuffer.allocate(16).put(fd.name.getBytes()).array());
    b.put((byte) fd.name.length());
    b.putInt(fd.block); // block
    b.putInt(fd.size); //size

    // write into appropriate parent directory
    int orig = fd.p.offset;
    fd.p.offset = fd.p_offset;
    write_fd(fd.p,b.array());
    fd.p.offset = orig;
    update_fd(fd.p);
  }

  private static FileD root() throws IOException {
    int BS = TFSDiskInputOutput.BLOCK_SIZE;

    // read root's first block and size
    ByteBuffer b = ByteBuffer.allocate(BS);
    read_block(fat[0],b.array());
    b.position(22);
    int fblock = b.getInt();
    int fsize = b.getInt();

    // root has no parent to update thus null
    return new FileD(null,0,true,"",fblock,fsize);
  }

  private static FileD[] read_dir(FileD fd) throws IOException {
    if (!fd.d) throw new TFSException(fd.name + " is not a directory");
    int BS = TFSDiskInputOutput.BLOCK_SIZE;
    FileD entries[];

    int fblock = fd.block;
    int fsize = fd.size;
    entries = new FileD[fsize/32];

    // read blocks into buffer
    fd.offset = 0;
    ByteBuffer b = ByteBuffer.allocate(fsize);

    read_fd(fd,b.array());

    // read into entries array
    for (int i = 0; i < entries.length; ++i){
      int p = b.getInt();           // 4
      boolean d = (b.get() != 0);   // 5
      byte name[] = new byte[16];   // 21
      b.get(name);
      int nlength = b.get();        // 22
      int eblock = b.getInt();      // 26
      int esize = b.getInt();       // 30
      b.get();
      b.get();                      // 32

      entries[i] = new FileD(fd,
                             i*32,
                             d,
                             new String(name,0,nlength),
                             eblock,
                             esize);
    }
    return entries;
  }

  // retrieve entry of the file path
  private static FileD read_entry(String dir) throws IOException {
    if (dir.length() == 0 || dir.charAt(0) != '/')
      throw new TFSException("Invalid path: " + dir);
    FileD cur = root();
    String names[] = dir.split("/");
    for (int i = 1; i < names.length; ++i){
      FileD entries[] = read_dir(cur);
      int j = 0;
      for (;j < entries.length; ++j){
        if (entries[j].name.equals(names[i]))
          break;
      }
      if (j == entries.length)
        throw new IOException(dir + ": No such file or directory");
      cur = entries[j];
    }
    return cur;
  }

  /*
   * File related - public methods
   */

  public static String[] read_path_names (String dir) throws IOException {
    FileD[] entries = read_dir(read_entry(dir));
    String names[] = new String[entries.length];
    for (int i = 0; i < entries.length; ++i){
      names[i] = (entries[i].d?"\033[0;1m":"")+entries[i].name;
    }
    return names;
  }

  public static void mkdir (String directory) throws IOException {
    File f = new File(directory);
    String name = f.getName();
    FileD parent = read_entry(f.getParent());
    FileD[] entries = read_dir(parent);
    for (int i = 0; i < entries.length; ++i){
      if (entries[i].d && entries[i].name.equals(name))
        throw new TFSException(name + " already exists");
    }
    FileD child = new FileD(parent,
                            entries.length*32,
                            true,
                            name,
                            0,
                            0);
    update_fd(child); // reflect changes in parent directory so child is searchable
  }

  public static void rmdir (String directory) throws IOException {
    
  }

  /*
   * Misc.
   */

}
