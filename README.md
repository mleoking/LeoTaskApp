# LeoTaskApp

[LeoTask](http://github.com/mleoking/LeoTask) is a lightweight parallel task running and results aggregation framework. This project, LeoTaskApp, includes applications based on the LeoTask framework.

# Applications

The [Instruction](#Instruction) section explains how to run an application.

Name  | Description | Links
----- |-------------|----------
Hybrid HIV infection model [1] | A HIV infection model that incorporates both cell-free and cell-to-cell modes of HIV infection. The model is able to reproduce the whole course of HIV infection that includes three disctinctive phrases. The model can also be useful for evaluating existing and future HIV treatments. | [Code](leotaskapp/src/org/leores/task/app/ModelHIV.java) [Executable](https://github.com/mleoking/LeoTaskApp/releases/download/v1.0.0/modelhiv.zip)

# Instruction

Before running an applcation please install:

* [Java](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html) and include the the directory of the command _java_ in the system's _PATH_ environment variable.
* [Gnuplot](http://sourceforge.net/projects/gnuplot/files/gnuplot/4.6.5/) and include the the directory of the command _gnuplot_ in the system's _PATH_ environment variable.

Steps to run an application:

1. Download the executable program through links in the next section.
2. Unzip the downloaded zip package and enter the extracted directory.
3. For MS Windows users, simple execute (double click) _run.bat_, for other system users, run the following command:

    java -jar leotaskapp.jar

If the application runs fine, you can then use any text editor to modify its configuration in tasks.xml or tasks#.xml, and rerun (the 3rd step) the application using new configurations.

# References

[1] Changwang Zhang, Shi Zhou, Elisabetta Groppelli, Pierre Pellegrino, Ian Williams, Persephone Borrow, Clare Jolly, Benjamin M. Chain, Hybrid spreading mechanisms and T cell activation shape the dynamics of HIV-1 infection, 2015.
