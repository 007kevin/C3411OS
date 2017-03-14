public class SampleReaderTest
{
	public static void main(String args[])
	{
		PriorityQueue readyQ = new PriorityQueue();
		ProcessControlBlock pcb;
		SampleReader sr;
		String sampleFile;
		int process, arrival, priority, burst;

		System.out.print("Sample File?  ");
		sampleFile = SavitchIn.readLineWord();
		sr = new SampleReader(sampleFile);

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

			pcb = new ProcessControlBlock(process, arrival, priority, burst);
			readyQ.putQueue(pcb, arrival);
				
//			System.out.println(process + "  " + arrival + "  " + priority + "  " + burst);
		}

		while(!readyQ.isEmpty()) {
			System.out.println(readyQ.getHighestPriority());  // print the highest priority without getting the queue item
			pcb = (ProcessControlBlock)readyQ.getQueue();
			System.out.println(pcb.getProcessNo() + "  " + pcb.getArrivalTime() + "  " + pcb.getPriority() + "  " + pcb.getCpuBurstTime());
		}
	}
}
