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
    if (TFSDiskInputOutput.is_open())
      TFSDiskInputOutput.tfs_dio_close();
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

    int[] values = new int[7];
    values[0] = pcb_root;
    values[1] = pcb_size;
    values[2] = pcb_fs_size;
    values[3] = pcb_fat_root;
    values[4] = pcb_fat_size;
    values[5] = pcb_data_block_root;
    values[6] = pcb_data_block_size;

    r.seek(pcb_root);
    for (int i = 0; i < 7; ++i){
      assertEquals(r.readInt(), values[i]);
    }

    // assert the default values for FAT are correctly written
    r.seek(pcb_fat_root);
    for (int i = 0; i < pcb_data_block_size; ++i){
      int val = r.readInt();
      // assertEquals(r.readInt(),(int)0); // values in fat should be 0
      System.out.println(val);
    }


  }

  @Ignore
  public void tfs_prrfs() throws IOException {

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
