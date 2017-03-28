all: out
clean:
	@rm -f *.class
.PHONY: all clean

JAVAC		= javac
JAVA		= java

out: clean
	@$(JAVAC) AlgorithmSimulation.java
	@$(JAVAC) SampleGenerator.java

run: out
	@$(JAVA) AlgorithmSimulation

sample: out
	@$(JAVA) SampleGenerator
