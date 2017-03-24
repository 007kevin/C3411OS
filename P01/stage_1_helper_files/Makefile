all: out
clean:
	@rm -f *.class
.PHONY: all clean

JAVAC		= javac
JAVA		= java
FILE		= TestSuite.java
CLASSPATH	= .:/home/local/projects/C3411OS/lib/junit-4.12.jar:/home/local/projects/C3411OS/lib/hamcrest-core-1.3.jar

out: clean
	@$(JAVAC) -cp $(CLASSPATH) $(FILE)

test: out
	@$(JAVA) -cp $(CLASSPATH) org.junit.runner.JUnitCore $(basename $(FILE))

shell:
	@$(JAVAC) TFSShell.java
	@$(JAVA) TFSMain

