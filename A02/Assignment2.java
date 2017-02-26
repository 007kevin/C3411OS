import java.io.*;

class Assignment2 {
  public static void insertEntry(DataOutputStream d, int n, String s) throws IOException {
    // Right pad each entry so record is 36 bytes
    d.writeInt(n);
    d.writeBytes(String.format("%-32s",s));
  }
  
  public static void main(String[] args){
    try {
      File f = new File("student_record.txt");
      f.createNewFile();
      FileOutputStream fos = new FileOutputStream(f);
      BufferedOutputStream bos = new BufferedOutputStream(fos);
      DataOutputStream d = new DataOutputStream(bos);
      
      insertEntry(d,72,"James");
      insertEntry(d,56,"Mark");
      insertEntry(d,87,"John");
      insertEntry(d,30,"Phillip");
      insertEntry(d,44,"Andrew");      
      d.close();

      RandomAccessFile r = new RandomAccessFile(f,"rw");
      
      System.out.println("\nStudent Records (before sorting)");
      for (int i = 0; i < r.length(); i+=36){
        r.seek(i);
        System.out.print(r.readInt());
        byte[] bytes = new byte[32];
        r.read(bytes);
        System.out.println("\t" + new String(bytes,"UTF-8"));
      }

      for (int i = 0; i < r.length(); i+=36){
        r.seek(i);
        int a = r.readInt();
        int p = i;
        for (int j = i+36; j < r.length(); j+=36){
          r.seek(j);
          int b = r.readInt();
          if (a > b){
            a = b;
            p = j;
          }
        }
        byte[] e1 = new byte[36];
        byte[] e2 = new byte[36];
        r.seek(i);
        r.read(e1);
        r.seek(p);
        r.read(e2);
        r.seek(p);
        r.write(e1);
        r.seek(i);
        r.write(e2);
      }

      System.out.println("\nStudent Records (after sorting)");
      for (int i = 0; i < r.length(); i+=36){
        r.seek(i);
        System.out.print(r.readInt());
        byte[] bytes = new byte[32];
        r.read(bytes);
        System.out.println("\t" + new String(bytes,"UTF-8"));
      }
      
      r.close();
    }
    catch (IOException e) {
      System.out.println("Error: " + e.getMessage());
    }
  }
}
