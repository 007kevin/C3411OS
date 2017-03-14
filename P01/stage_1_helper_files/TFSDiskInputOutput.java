import java.io.*;
import java.util.*;

public class TFSDiskInputOutput
{
	/*
	 * Disk I/O API
	 */
	 
	public static int tfs_dio_create(byte[] name, int nlength, int size)
	{
		return -1;
	}	
	
	public static int tfs_dio_open(byte[] name, int nlength)
	{
		return -1;
	}			
	
	public static int tfs_dio_get_size()
	{
		return -1;
	}							
	
	public static int tfs_dio_read_block(int block_no, byte[] buf)
	{
		return -1;
	}
	
	public static int tfs_dio_write_block(int block_no, byte[] buf)	
	{
		return -1;
	}
	
	public static void tfs_dio_close()		
	{
		return;
	}					
}