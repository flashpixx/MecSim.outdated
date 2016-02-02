# MecSim - Micro Traffic Agent-Based Simulation

![Screenshot](screen.png)

## Features

* native Java implementation
* HTML5-based GUI with multilanguage support
* build-in user and developer documentation
* complete [OpenStreetMap](https://www.openstreetmap.org/) data import with [GraphHopper](https://graphhopper.com) routing engine
* Nagel-Schreckenberg traffic following model
* Multi-Agent system with complete [Jason-Support](http://jason.sourceforge.net/)
* Code Benchmarking with statistical analysis with [R](https://www.r-project.org/)

## Requirements

### Running

* [Oracle Java Runtime 1.8](http://www.java.com)
* online access
* at least 3 GB RAM

### Developing

* [Oracle Java Developing Kit 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Maven 3.0](http://maven.apache.org/)
* [Doxygen](http://www.doxygen.org/) with [GraphViz](http://www.graphviz.org)
* [Bower](http://bower.io/)
* [Source code documentation](http://flashpixx.github.io/MecSim/)

#### IDE Support

* [IntelliJ Community Edition](http://www.jetbrains.com/idea/)
* [Eclipse](http://www.eclipse.org/)


## Installation / Configuration

The Maven package build creates a Jar and OS dependet package files in the target-path. The Jar must use more memory
of the Java VM on graph downloading and converting, so the Jar must be started with the suffix ```-Xmx<Memory Size>```
(a good choice of the memory size can be 3g-5g), also the parallel garbage collector should be used with ```-XX:+UseParallelGC```.
OS dependent packages does not use these parameters.


The program can be used in two structures:

 * GUI based - you need run the Jar file directly
 * non-ui based - you need run the Jar file with the parameter ```--nogui <mecsim data file, that should be loaded> --step <number of iterations>```

Any additional startup parameter can be shown with the parameter ```--help``` and other information can be found in the documentation.
