import java.io.*;
import java.util.*;
import java.lang.reflect.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.Ignore;

public class TestSuite {
  public static final String DBNAME = "TFSDiskFile";

  @After
  public void cleanup() throws IOException {
    /*
     * DISK CLEANUP ROUTINE
     */
    TFSFileSystem.tfs_exit();
    File f = new File(DBNAME);
    if (f.exists())
      f.delete();
  }

  /********************************************
   * TFSDiskInputOutput
   ********************************************/
  @Test
  public void test_disk_creation() throws IOException {
    assertEquals(TFSDiskInputOutput.tfs_dio_create(DBNAME.getBytes(), DBNAME.length(), 5),0);
    File f = new File(DBNAME);
    assertEquals(f.length(), 5*128);
  }

  @Test
  public void test_disk_open() throws IOException {
    TFSDiskInputOutput.tfs_dio_create(DBNAME.getBytes(), DBNAME.length(), 5);
    TFSDiskInputOutput.tfs_dio_open(DBNAME.getBytes(), DBNAME.length());
    assertEquals(TFSDiskInputOutput.is_open(), true);
  }

  @Test
  public void test_getting_disk_size() throws IOException {
    TFSDiskInputOutput.tfs_dio_create(DBNAME.getBytes(), DBNAME.length(), 5);
    TFSDiskInputOutput.tfs_dio_open(DBNAME.getBytes(), DBNAME.length());
    assertEquals(TFSDiskInputOutput.tfs_dio_get_size(), 5);
  }

  @Test
  public void test_reading_and_writing_block_in_disk() throws IOException {
    TFSDiskInputOutput.tfs_dio_create(DBNAME.getBytes(), DBNAME.length(), 5);
    TFSDiskInputOutput.tfs_dio_open(DBNAME.getBytes(), DBNAME.length());
    byte[] testdata = "WRITE THIS TO BLOCK".getBytes();
    TFSDiskInputOutput.tfs_dio_write_block(0,testdata);
    byte[] buffer   = new byte[128];
    TFSDiskInputOutput.tfs_dio_read_block(0,buffer);
    assertEquals(new String(buffer,0,testdata.length), new String(testdata));
  }

  @Test
  public void test_closing_disk() throws IOException {
    TFSDiskInputOutput.tfs_dio_create(DBNAME.getBytes(), DBNAME.length(), 5);
    TFSDiskInputOutput.tfs_dio_open(DBNAME.getBytes(), DBNAME.length());
    assertEquals(TFSDiskInputOutput.is_open(), true);
    TFSDiskInputOutput.tfs_dio_close();
    assertEquals(TFSDiskInputOutput.is_open(), false);
  }

  /********************************************
   * TFSFileSystem
   ********************************************/
  @Test
  public void tfs_mkfs() throws IOException {
    TFSFileSystem.tfs_mkfs();
    File f = new File(DBNAME);
    assertEquals(f.exists(),true);
    RandomAccessFile r = new RandomAccessFile(f,"rw");

    // assert the default values for PCB are correctly written
    // default values:
    int pcb_root            = 0;
    int pcb_size            = 1;
    int pcb_fs_size         = 1+32+1024;
    int pcb_fat_root        = 1;
    int pcb_fat_size        = 32;
    int pcb_data_block_root = pcb_size+pcb_fat_size;
    int pcb_data_block_size = 1024;
    int free_ptr = 1;

    int[] values = new int[8];
    values[0] = pcb_root;
    values[1] = pcb_size;
    values[2] = pcb_fs_size;
    values[3] = pcb_fat_root;
    values[4] = pcb_fat_size;
    values[5] = pcb_data_block_root;
    values[6] = pcb_data_block_size;
    values[7] = free_ptr;

    // assert default values for PCB are written correcly
    r.seek(pcb_root);
    for (int i = 0; i < 8; ++i){
      assertEquals(r.readInt(), values[i]);
    }

    // assert the default values for FAT are correctly written
    r.seek(pcb_fat_root*TFSDiskInputOutput.BLOCK_SIZE);
    assertEquals(0,r.readInt()); // index 0 of fat always points to block 0
    for (int i = 2; i < pcb_data_block_size; ++i){
      assertEquals(i,r.readInt()); // values in fat equal i
    }
    // assert free space list ends in null
    assertEquals(-1,r.readInt());

    // assert the default values for directory are written correctly
    r.seek(pcb_data_block_root*TFSDiskInputOutput.BLOCK_SIZE);
    byte[] name = new byte[16];
    byte[] padding = new byte[2];
    assertEquals(0,r.readInt());
    assertEquals((byte) 1, r.readByte());
    r.readFully(name);
    assertEquals(true,Arrays.equals(name, new byte[16]));
    assertEquals(0, r.readInt());
    assertEquals(0, r.readInt());
    r.readFully(padding);
    assertEquals(true,Arrays.equals(padding, new byte[2]));
  }

  @Test
  public void test_mount_and_umount_of_disk() throws IOException {
    TFSFileSystem.tfs_mkfs();
    TFSFileSystem.tfs_mount();
    assertEquals(true, TFSDiskInputOutput.is_open());
    TFSFileSystem.tfs_umount();
    assertEquals(false, TFSDiskInputOutput.is_open());
  }

  @Ignore
  public void tfs_prrfs() throws IOException {
    TFSFileSystem.tfs_mkfs();
    TFSFileSystem.tfs_mount();
    System.out.println(TFSFileSystem.tfs_prrfs());
  }

  @Ignore
  public void tfs_prmfs() throws IOException {
    TFSFileSystem.tfs_mkfs();
    TFSFileSystem.tfs_mount();
    System.out.println(TFSFileSystem.tfs_prmfs());
  }


  @Ignore
  public void tfs_exit() throws IOException {

  }

  /********************************************
   * TFSShell
   ********************************************/
  @Ignore
  public void mkfs() throws IOException {

  }

  @Ignore
  public void prrfs() throws IOException {

  }

  @Ignore
  public void exit() throws IOException {

  }

}
