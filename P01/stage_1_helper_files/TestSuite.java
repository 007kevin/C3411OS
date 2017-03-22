import java.io.*;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.Ignore;

public class TestSuite {
  public static final String DBNAME = "TESTDB";

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
  public void createsFiles() throws IOException {
    assertEquals(TFSDiskInputOutput.tfs_dio_create(DBNAME.getBytes(), DBNAME.length(), 5),0);
    File f = new File(DBNAME);
    assertEquals(f.length(), 5*128);
  }

  @Test
  public void openFiles() throws IOException {
    TFSDiskInputOutput.tfs_dio_create(DBNAME.getBytes(), DBNAME.length(), 5);
    TFSDiskInputOutput.tfs_dio_open(DBNAME.getBytes(), DBNAME.length());
    assertEquals(TFSDiskInputOutput.is_open(), true);
  }

  @Test
  public void getSize() throws IOException {
    TFSDiskInputOutput.tfs_dio_create(DBNAME.getBytes(), DBNAME.length(), 5);
    TFSDiskInputOutput.tfs_dio_open(DBNAME.getBytes(), DBNAME.length());
    assertEquals(TFSDiskInputOutput.tfs_dio_get_size(), 5);
  }

  @Test
  public void writeAndReadBlock() throws IOException {
    TFSDiskInputOutput.tfs_dio_create(DBNAME.getBytes(), DBNAME.length(), 5);
    TFSDiskInputOutput.tfs_dio_open(DBNAME.getBytes(), DBNAME.length());
    byte[] testdata = "WRITE THIS TO BLOCK".getBytes();
    TFSDiskInputOutput.tfs_dio_write_block(0,testdata);
    byte[] buffer   = new byte[128];
    TFSDiskInputOutput.tfs_dio_read_block(0,buffer);
    assertEquals(new String(buffer,0,testdata.length), new String(testdata));
  }

  @Test
  public void closeFile() throws IOException {
    TFSDiskInputOutput.tfs_dio_create(DBNAME.getBytes(), DBNAME.length(), 5);
    TFSDiskInputOutput.tfs_dio_open(DBNAME.getBytes(), DBNAME.length());
    assertEquals(TFSDiskInputOutput.is_open(), true);
    TFSDiskInputOutput.tfs_dio_close();
    assertEquals(TFSDiskInputOutput.is_open(), false);    
  }

  /********************************************
   * TFSFileSystem
   ********************************************/
  @Ignore
  public void tfs_mkfs() throws IOException {
    
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
