import java.util.*;

class ProcessEval {
  public int No, TT, WT, completeTime;
  public ProcessEval(int No, int TT, int WT, int completeTime) {
    this.No = No;
    this.TT = TT;
    this.WT = WT;
    this.completeTime = completeTime;
  }
}

public class AlgorithmSimulationRR {
  
  public static void main(String[] args) {
    LinkedList<ProcessControlBlock> readyQ = new LinkedList<ProcessControlBlock>();
    SampleReader sr;
    String sampleFile = "OUTPUT";
    int process = -1;
    int arrival = -1;
    int priority = -1;
    int burst = -1;

    // time in milliseconds
    int MINUTE = 60000;
    int TEN_MINUTES = 10*MINUTE;
    int HOUR = 60*MINUTE;
    int SIM_TIME = HOUR;

    // Time Quanta
    int QUANTUM = 50;

    // System.out.print("Sample File?  ");
    // sampleFile = SavitchIn.readLineWord();
    sr = new SampleReader(sampleFile);
    while(true) {
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
      ProcessControlBlock PCB = new ProcessControlBlock(process, arrival, priority, burst);
      readyQ.add(PCB);
    }
    /************************************************
     * ALGORITHM EVALUATION: RR without premption 
     ************************************************/
    // Keep track of done processes
    LinkedList<ProcessEval> Eval = new LinkedList<ProcessEval>();

    // Assume head of LinkedList is being processed by CPU
    LinkedList<ProcessControlBlock> Q = new LinkedList<ProcessControlBlock>();
    
    for (int ms = 0; ms <= SIM_TIME || Q.size() != 0; ++ms){
      // Print average times every minute
      if (ms%MINUTE == 0){

        // Remove completed times older than 10 minutes
        while(Eval.size() > 0 && Eval.peek().completeTime < ms-TEN_MINUTES){
          Eval.removeFirst();
        }

        double TurnTime = 0;
        double WaitTime = 0;
        
        for (ProcessEval P : Eval){
          TurnTime+=P.TT;
          WaitTime+=P.WT;
        }
        
        if (Eval.size() != 0){
          System.out.printf("%.10f\t%.10f\n", TurnTime/Eval.size(), WaitTime/Eval.size());
        }
        
      }
      
      // Queue any arrived processes
      while (readyQ.size() > 0 && readyQ.peek().getArrivalTime() == ms){
        Q.add(readyQ.poll());
      }

      // Remove any done processes from head of Queue
      if (Q.size() > 0 && Q.peek().getRuntime() >= Q.peek().getCpuBurstTime()){
        ProcessControlBlock PCB = Q.poll();
        int TT = ms - PCB.getArrivalTime();
        int WT = TT - PCB.getCpuBurstTime();
        Eval.add(new ProcessEval(PCB.getProcessNo(), TT, WT, ms));
      }
      
      // Place process with used up quanta at end of Queue
      if (Q.size() > 0 &&
          Q.peek().getRuntime() != 0 &&
          Q.peek().getRuntime()%QUANTUM == 0){
        Q.add(Q.poll());
      }

      // Increment CPU running time of process if exist
      if (Q.size() > 0){
        Q.peek().incRuntime();
      }
    }

  }
}
