public class ProcessControlBlock
{
  int processNo, arrivalTime, priority, cpuBurstTime, runtime;
	
  public ProcessControlBlock(int processNo, int arrivalTime, int priority, int cpuBurstTime)
  {
    this.processNo = processNo;
    this.arrivalTime = arrivalTime;
    this.priority = priority;
    this.cpuBurstTime = cpuBurstTime;
  }
	
  public int getProcessNo()
  {
    return processNo;
  }
	
  public int getArrivalTime()
  {
    return arrivalTime;
  }
	
  public int getPriority()
  {
    return priority;
  }
	
  public int getCpuBurstTime()
  {
    return cpuBurstTime;
  }

  public int getRuntime()
  {
    return runtime;
  }

  public void incRuntime()
  {
    runtime++;
  }
  
}
