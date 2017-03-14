import java.util.*;
import java.io.*;

public class SampleGenerator
{
	public static void main(String args[])
	{
		Random r = new Random();
		
		System.out.print("Minimum Priority?  ");
		int MIN_PRIORITY = SavitchIn.readLineInt();
		System.out.print("Maximum CPU Burst?  ");
		int MAX_BURST = SavitchIn.readLineInt();
		System.out.print("Simulation Time?  ");
		int SIM_TIME = SavitchIn.readLineInt();
		System.out.print("Average Arrival Time?  ");
		int AVG_ARRIVAL_TIME = SavitchIn.readLineInt();
//		System.out.print("Time Slice?  ");
//		int SLICE_TIME = SavitchIn.readLineInt();
		System.out.print("Output File?  ");
		String OUTPUT_FILE = SavitchIn.readLineWord();
		
		int process = 0;
		int arrival, priority, burst;
		int time = 0;
		int max_burst = MAX_BURST;
		boolean shrinking = true;

		try {
			File f = new File(OUTPUT_FILE);
			f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			DataOutputStream dos = new DataOutputStream(bos);
		
			while(time <= SIM_TIME) {
				arrival = time + r.nextInt(2 * AVG_ARRIVAL_TIME + 1);
				priority = r.nextInt(MIN_PRIORITY + 1);
				burst = r.nextInt(max_burst) + 1;
				if (arrival + burst > SIM_TIME)
					break;
				System.out.println(process + "  " + arrival + "  " + priority + "  " + burst);
				dos.writeInt(process);
//				dos.writeBytes("  ");
				dos.writeInt(arrival);
//				dos.writeBytes("  ");
				dos.writeInt(priority);
//				dos.writeBytes("  ");
				dos.writeInt(burst);
//				dos.writeBytes("  ");
				process++;
				time = arrival;
				if (shrinking) {
					max_burst--;
					if (max_burst <= MAX_BURST/2) {
						shrinking = false;
					}
				}
				else {
					max_burst++;
					if (max_burst == MAX_BURST) {
						shrinking = true;
					}
				}
			}
			
			dos.close();
			
		} catch (IOException ie) {
			System.out.println("Something for writing");
		}
		
	}
}