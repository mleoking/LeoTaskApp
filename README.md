# Applications

The [Instruction](#instruction) section explains how to run/modify/create an application. [[Discussion]](https://groups.google.com/forum/#!forum/leotaskapp)

Name  | Description | Links
----- |-------------|----------
Hybrid HIV Infection Model [1] | A HIV infection model that incorporates both cell-free and cell-to-cell modes of HIV infection. The model is able to reproduce the whole course of HIV infection that includes three disctinctive phrases. The model can also be useful for evaluating existing and future HIV treatments. | [Code](leotaskapp/src/org/leores/task/app/ModelHIV.java) [**Executable**](leotaskapp/demo/modelhiv.zip?raw=true) [Paper](http://journals.plos.org/ploscompbiol/article?id=10.1371/journal.pcbi.1004179)
Hybrid Epidemic Spreading Model [2] | A hybird epidemic spreading model incorporating both local and global spreading mechanisms. The model can be used to explore the optimal tradeoff between local and global spreading mechanisms. | [Code](leotaskapp/src/org/leores/task/app/EpiHybrid.java) [**Executable**](leotaskapp/demo/epihybrid.zip?raw=true) [Paper](http://www.nature.com/srep/2015/150429/srep09924/full/srep09924.html)

# LeoTaskApp

[LeoTask](http://github.com/mleoking/LeoTask) is a lightweight parallel task running and results aggregation framework. This project, LeoTaskApp, includes applications based on the LeoTask framework.

# Instruction

**1. Install the runtime environment:**

For Windows system users: 

* Download, unzip, and install (_install.bat_) the all-in-one package: [LeoTaskRunEnv](https://github.com/mleoking/LeoTaskApp/releases/download/v1.0.0/LeoTaskRunEnv.zip)

For other system users:

* Install [Java](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html) and include the the directory of the command _java_ in the system's _PATH_ environment variable.
* Install [Gnuplot 4.6.5](http://sourceforge.net/projects/gnuplot/files/gnuplot/4.6.5/) and include the the directory of the command _gnuplot_ in the system's _PATH_ environment variable.

**2. Run an application:**

1. Download the executable program through links in the [Applications](#applications) section.
2. Unzip the downloaded zip package and enter the extracted directory.
3. For MS Windows users, simply execute (double click) _run.bat_, for other system users, run the following command:

    java -jar leotaskapp.jar
    
* If the application runs successfully, you can use any text editor to modify its configuration in _tasks.xml_ or _tasks#.xml_, and then rerun (the 3rd step) the application.

**3. Modify/create an application:**

1. Download and install [Eclipse Java IDE](https://www.eclipse.org/downloads/packages/eclipse-ide-java-developers/keplersr2).
2. Open the Eclipse Java IDE and import the LeoTaskApp project into your workspace. [Click here for details](leotaskapp/doc/import-into-eclipse/README.md).
3. Import the [LeoTask](https://github.com/mleoking/LeoTask) project into your workspace for your reference on the Application Programming Interface of the framework. [Click here for details](leotaskapp/doc/import-into-eclipse/README.md).
4. Now you can modify an exiting application or create new applications in the LeoTaskApp project in your local Eclipse workspace.
5. You can also submit and push your new applications to the LeoTaskApp project on Github.

# References

1. C. Zhang, S. Zhou, E. Groppelli, P. Pellegrino, I. Williams, P. Borrow, B. M. Chain, and C. Jolly. Hybrid Spreading Mechanisms and T Cell Activation Shape the Dynamics of HIV-1 Infection. PLoS Computational Biology. 2015 Apr 2;11(4):e1004179. [[Web]](http://journals.plos.org/ploscompbiol/article?id=10.1371/journal.pcbi.1004179)[[Pdf]](http://www.ploscompbiol.org/article/fetchObject.action?uri=info:doi/10.1371/journal.pcbi.1004179&representation=PDF)
2. C. Zhang, S. Zhou, J. C. Miller, I. J. Cox, and B. M. Chain. Optimizing Hybrid Spreading in Metapopulations. Scientific Reports. 2015 Apr 29;5:9924. [[Web]](http://www.nature.com/srep/2015/150429/srep09924/full/srep09924.html)[[Pdf]](http://www.nature.com/srep/2015/150429/srep09924/pdf/srep09924.pdf)



[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/mleoking/leotaskapp/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

