import java.util.*;
import java.io.*;

public class SampleReader
{
	DataInputStream dis;
	
	public SampleReader(String sampleFile)
	{
		try {
			File f = new File(sampleFile);
			if (!f.exists()) {
				System.out.println(sampleFile + " does not exist");
				System.exit(-1);
			}
			FileInputStream fis = new FileInputStream(f);
			BufferedInputStream bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);
		} 
		catch (IOException ie) {
			System.out.println("Something wrong to open " + sampleFile);
			System.exit(-2);
		}
		
	}
	
	public int readProcess()
	{
		try {
			return dis.readInt();
		} catch (IOException ie) {
			return -1;
		}
	}
	
	public int readArrival()
	{
		try {
			return dis.readInt();
		} catch (IOException ie) {
			return -1;
		}
	}
	
	public int readPriority()
	{
		try {
			return dis.readInt();
		} catch (IOException ie) {
			return -1;
		}
	}
	
	public int readBurst()
	{
		try {
			return dis.readInt();
		} catch (IOException ie) {
			return -1;
		}
	}
	
	public static void main(String args[])
	{
		System.out.print("Sample File?  ");
		String sampleFile = SavitchIn.readLineWord();
		
		SampleReader sr = new SampleReader(sampleFile);
		
		int process, arrival, priority, burst;

		while(true) {
			process = -1;
			arrival = -1;
			priority = -1;
			burst = -1;
			process = sr.readProcess();
			if (process < 0)
				break;
			arrival = sr.readArrival();
			if (arrival < 0)
				break;
			priority = sr.readPriority();
			if (priority < 0)
				break;
			burst = sr.readBurst();
			if (burst < 0)
				break;
				
			System.out.println(process + "  " + arrival + "  " + priority + "  " + burst);
		}
	}
}