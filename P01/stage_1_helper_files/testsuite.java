import java.io.*;
import java.util.*;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class testsuite {
  
  @Test
  public void createsFiles() throws IOException {
    TFSDiskInputOutput IO = new TFSDiskInputOutput();
    String name = "TESTDB";
    assertEquals(IO.tfs_dio_create(name.getBytes(), name.length(), 5),0);
    File f = new File(name);
    assertEquals(f.length(), 5*128);
    f.delete();
  }
  
}
