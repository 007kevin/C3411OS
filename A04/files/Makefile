all: out
clean:
	@rm -f *.class
.PHONY: all clean

JAVAC		= javac
JAVA		= java

out: clean
	@$(JAVAC) AlgorithmSimulationFCFS.java
	@$(JAVAC) AlgorithmSimulationSJF.java
	@$(JAVAC) SampleGenerator.java

fcfs: out
	@$(JAVA) AlgorithmSimulationFCFS

sjf: out
	@$(JAVA) AlgorithmSimulationSJF


sample: out
	@$(JAVA) SampleGenerator
