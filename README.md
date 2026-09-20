# GraalVM Native Image with Spring Boot â€” Hands-on POC



A hands-on exploration of **GraalVM Native Image with Spring Boot**, focusing on how native compilation affects startup time, memory usage, application compatibility, reflection, database access, and JPA/Hibernate.



This POC was built by taking a working Spring Boot application and progressively testing it with GraalVM Native Image rather than relying only on theoretical comparisons.



## What I Explored



* JVM vs GraalVM Native Image

* Ahead-of-Time (AOT) compilation

* Native Image build and runtime behavior

* Runtime reflection

* Reflection configuration with `reflect-config.json`

* MySQL integration with JDBC

* Spring Data JPA + Hibernate

* JVM vs Native startup and memory measurements

* Native executable size and build-time trade-offs

* Native Image troubleshooting and reachability metadata



## POC Journey



1\. Establish a JVM baseline

2\. Build the application as a GraalVM Native Image

3\. Investigate and resolve the first native runtime failure

4\. Test runtime reflection

5\. Add Spring Data JPA + Hibernate

6\. Compare JVM and Native results

7\. Document observations, limitations, and trade-offs



## Key Findings



| Area            | Observation                                                  |

| --------------- | ------------------------------------------------------------ |

| Startup         | Native startup was significantly faster in the experiments   |

| Runtime memory  | Native process used less memory in the measured scenarios    |

| Build time      | Native compilation took considerably longer                  |

| Artifact size   | Native executables were substantially larger than JARs       |

| Reflection      | Dynamically accessed classes may require reflection metadata |

| JDBC + MySQL    | Successfully worked in the native executable                 |

| JPA + Hibernate | Successfully worked in the native executable                 |

| Compatibility   | Framework and GraalVM version compatibility was important    |



> The measurements in this POC are environment-specific observations, not universal benchmarks.



## Documentation



Detailed experiments and findings are documented separately:



* \[JVM Baseline](docs/01-baseline-jvm.md)

* \[GraalVM Native Image](docs/02-graalvm-native-image.md)

* \[First Native Failure](docs/03-first-native-failure.md)

* \[Reflection Experiment](docs/04-reflection-experiment.md)

* \[JPA + Hibernate](docs/05-jpa-hibernate.md)

* \[Results \& Comparison](docs/06-results-and-comparison.md)

* \[Learnings \& Trade-offs](docs/07-learnings-and-tradeoffs.md)



## Tech Stack



* Java

* Spring Boot 4.1.1

* GraalVM Native Image 25.0.4

* Maven Wrapper 3.9.16

* MySQL 8.0.32

* Spring JDBC

* Spring Data JPA

* Hibernate ORM 7.4.5



## Why This POC?



The goal was not just to see whether a Spring Boot application could be compiled into a native executable.



The goal was to understand **what changes when the application moves from the JVM to a native executable**, what breaks, why it breaks, how to troubleshoot it, and what trade-offs come with the approach.



\---



**Author:** Anisha Rathore




