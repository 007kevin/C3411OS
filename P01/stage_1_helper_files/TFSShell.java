import java.io.*;
import java.util.*;

public class TFSShell extends Thread  
{
  String wdir = ""; // working directory
  public TFSShell()
  {
  }
	
  public void run()
  {
    readCmdLine();
  }
	
  /*
   * User interface routine
   */
	 
  void readCmdLine()
  {
    String line, cmd, arg1, arg2, arg3, arg4;
    StringTokenizer stokenizer;
    Scanner scanner = new Scanner(System.in);

    System.out.println("Hal: Good morning, Dave!\n");
		
    while(true) {
      try {
        System.out.print("ush " + wdir + "> ");
			
        line = scanner.nextLine();
        line = line.trim();
        stokenizer = new StringTokenizer(line);
        if (stokenizer.hasMoreTokens()) {
          cmd = stokenizer.nextToken();
				
          if (cmd.equals("mkfs"))
            mkfs();
          else if (cmd.equals("mount"))
            mount();
          else if (cmd.equals("sync"))
            sync();
          else if (cmd.equals("prrfs"))
            prrfs();
          else if (cmd.equals("prmfs"))
            prmfs();

					
          else if (cmd.equals("mkdir")) {
            if (stokenizer.hasMoreTokens()) {
              arg1 = stokenizer.nextToken();
              if (arg1.charAt(0) != '/')
                arg1 = wdir + "/" +arg1;
              mkdir(arg1);					
            }
            else
              System.out.println("Usage: mkdir directory");
          }
          else if (cmd.equals("rmdir")) {
            if (stokenizer.hasMoreTokens()) {
              arg1 = stokenizer.nextToken();
              rmdir(arg1);					
            }
            else
              System.out.println("Usage: rmdir directory");
          }
          else if (cmd.equals("ls")) {
            if (stokenizer.hasMoreTokens()) {
              arg1 = stokenizer.nextToken();
              ls(arg1);
            }
            else
              ls(wdir);
          }
          else if (cmd.equals("cd")) {
            if (stokenizer.hasMoreTokens()) {
              arg1 = stokenizer.nextToken();
              cd(arg1);
            }
            else
              System.out.println("Usage: change current directory");
          }
          else if (cmd.equals("create")) {
            if (stokenizer.hasMoreTokens()) {
              arg1 = stokenizer.nextToken();
              create(arg1);					
            }
            else
              System.out.println("Usage: create file");
          }
          else if (cmd.equals("rm")) {
            if (stokenizer.hasMoreTokens()) {
              arg1 = stokenizer.nextToken();
              rm(arg1);					
            }
            else
              System.out.println("Usage: rm file");
          }
          else if (cmd.equals("print")) {
            if (stokenizer.hasMoreTokens())
              arg1 = stokenizer.nextToken();
            else {
              System.out.println("Usage: print file position number");
              continue;
            }
            if (stokenizer.hasMoreTokens())
              arg2 = stokenizer.nextToken();
            else {
              System.out.println("Usage: print file position number");
              continue;
            }					
            if (stokenizer.hasMoreTokens())
              arg3 = stokenizer.nextToken();
            else {
              System.out.println("Usage: print file position number");
              continue;
            }	
            try {
              print(arg1, Integer.parseInt(arg2), Integer.parseInt(arg3));
            } catch (NumberFormatException nfe) {
              System.out.println("Usage: print file position number");
            }			
          }
          else if (cmd.equals("append")) {
            if (stokenizer.hasMoreTokens())
              arg1 = stokenizer.nextToken();
            else {
              System.out.println("Usage: append file number");
              continue;
            }
            if (stokenizer.hasMoreTokens())
              arg2 = stokenizer.nextToken();
            else {
              System.out.println("Usage: append file number");
              continue;
            }					
            try {
              append(arg1, Integer.parseInt(arg2));
            } catch (NumberFormatException nfe) {
              System.out.println("Usage: append file number");
            }			
          }
          else if (cmd.equals("cp")) {
            if (stokenizer.hasMoreTokens())
              arg1 = stokenizer.nextToken();
            else {
              System.out.println("Usage: cp file directory");
              continue;
            }
            if (stokenizer.hasMoreTokens())
              arg2 = stokenizer.nextToken();
            else {
              System.out.println("Usage: cp file directory");
              continue;
            }					
            cp(arg1, arg2);
          }
          else if (cmd.equals("rename")) {
            if (stokenizer.hasMoreTokens())
              arg1 = stokenizer.nextToken();
            else {
              System.out.println("Usage: rename src_file dest_file");
              continue;
            }
            if (stokenizer.hasMoreTokens())
              arg2 = stokenizer.nextToken();
            else {
              System.out.println("Usage: rename src_file dest_file");
              continue;
            }					
            rename(arg1, arg2);
          }
					
          else if (cmd.equals("exit")) {
            exit();
            System.out.println("\nHal: Good bye, Dave!\n");
            break;
          }
				
          else
            System.out.println("-ush: " + cmd + ": command not found");
        }
      } catch (IOException e){
        // System.out.println(e.getMessage());
        e.printStackTrace();
      }
    }
  }


  /*
   * You need to implement these commands
   */
  String absolutify(String directory){
    if (directory.charAt(0) == '/')
      return directory;
    return wdir + (wdir.equals("/")?"":"/") + directory;
  }

  void cd(String directory) throws IOException {
    if (directory.equals("..")){
      if (!wdir.equals("/"))
        wdir = new File(wdir).getParent();
      return;
    }
    directory = absolutify(directory);
    // check directory exists
    TFSFileSystem.read_path_names(directory);
    wdir = directory;
    return;
  }
 	
  void mkfs() throws IOException
  {
    TFSFileSystem.tfs_mkfs();
    return;
  }
	
  void mount() throws IOException
  {
    TFSFileSystem.tfs_mount();
    wdir = "/";
    return;
  }
	
  void sync() throws IOException
  {
    TFSFileSystem.tfs_sync();
    return;
  }
	
  void prrfs() throws IOException
  {
    System.out.println(TFSFileSystem.tfs_prrfs());
    return;
  }

  void prmfs() throws IOException
  {
    System.out.println(TFSFileSystem.tfs_prmfs());
    return;
  }
	
  void mkdir(String directory) throws IOException
  {
    TFSFileSystem.mkdir(directory);
    return;
  }
	
  void rmdir(String directory) throws IOException
  {
    return;
  }
	
  void ls(String directory) throws IOException
  {
    String output = "";
    String names[] = TFSFileSystem.read_path_names(directory);
    for (int i = 0; i < names.length; ++i)
      output += names[i] + " ";
    if (output.length() != 0)
      System.out.println(output);
    return;
  }
	
  void create(String file) throws IOException
  {
    return;
  }
	
  void rm(String file) throws IOException
  {
    return;
  }
	
  void print(String file, int position, int number) throws IOException
  {
    return;
  }
	
  void append(String file, int number) throws IOException
  {
    return;
  }
	
  void cp(String file, String directory) throws IOException
  {
    return;
  }
	
  void rename(String source_file, String destination_file) throws IOException
  {
    return;
  }
	
  void exit() throws IOException
  {
    return;
  }
}


/*
 * main method
 */

class TFSMain
{
  public static void main(String argv[]) throws InterruptedException
  {
    TFSShell shell = new TFSShell();
		
    shell.start();
    shell.join();
  }
}
