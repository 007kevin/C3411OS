import java.util.*;

public class TestJava {

  public static void main(String[] args){
    LinkedList<ProcessControlBlock> Q = new LinkedList<ProcessControlBlock>();
    Q.add(new ProcessControlBlock(0,0,0,0));
    ProcessControlBlock PCB = Q.peek();
    PCB.incRuntime();
    PCB.incRuntime();
    PCB.incRuntime();
    System.out.println(Q.peek().getRuntime());
  }
}
