# MecSim - Traffic Simulation

Micro traffic simulation with multi-agent structure based on  [Jason](http://jason.sourceforge.net) and
[GraphHopper](https://graphhopper.com) OpenStreepMap library.

![Screenshot](screen.png)

## Requirements

* [Oracle Java 1.8](http://www.java.com)
* [Maven 3.0](http://maven.apache.org/)
* [Doxygen](http://www.doxygen.org/) with [GraphViz](http://www.graphviz.org)

### tested IDEs

* [IntelliJ Community Edition](http://www.jetbrains.com/idea/)
* [Eclipse](http://www.eclipse.org/)



## Installation / Configuration

Hourly a [current developer build](https://mecdev.rz-housing.tu-clausthal.de/jenkins/job/MecSim/) can be downloaded.

The Maven package build creates a Jar file in the path ```target/MecSim-<Version>.jar```. The
program must use more memory of the Java VM on graph downloading and converting, so the Jar must be started with the
suffix ```-Xmx<Memory Size>``` (a good choice of the memory size can be 3g-5g), also the parallel garbage collector
should be used with ```-XX:+UseParallelGC```

The program can be used in two structures:

 * GUI based - you need run the Jar file directly
 * non-ui based - you need run the Jar file with the parameter ```--nogui <mecsim data file, that should be loaded> --step <number of iterations>```

Any additional startup parameter can be shown with the parameter ```--help``` and other information can be found in the documentation.
