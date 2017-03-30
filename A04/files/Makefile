all: out
clean:
	@rm -f *.class
.PHONY: all clean

JAVAC		= javac
JAVA		= java

out: clean
	@$(JAVAC) AlgorithmSimulationFCFS.java
	@$(JAVAC) AlgorithmSimulationSJF.java
	@$(JAVAC) AlgorithmSimulationPriority.java
	@$(JAVAC) AlgorithmSimulationRR.java
	@$(JAVAC) SampleGenerator.java

fcfs: out
	@$(JAVA) AlgorithmSimulationFCFS

sjf: out
	@$(JAVA) AlgorithmSimulationSJF

priority: out
	@$(JAVA) AlgorithmSimulationPriority

rr: out
	@$(JAVA) AlgorithmSimulationRR

output:
	@$(JAVA) AlgorithmSimulationFCFS > OUTPUT_FCFS.txt
	@$(JAVA) AlgorithmSimulationSJF > OUTPUT_SJF.txt
	@$(JAVA) AlgorithmSimulationPriority > OUTPUT_PRIORITY.txt
	@$(JAVA) AlgorithmSimulationRR > OUTPUT_RR.txt

sample: out
	@$(JAVA) SampleGenerator
