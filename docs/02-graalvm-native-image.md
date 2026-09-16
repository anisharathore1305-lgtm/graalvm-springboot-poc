# 02 - GraalVM Native Image

## Moving Beyond the JVM

So far, the JVM was working perfectly fine for me.

As someone who works with Java, the JVM had always been the obvious part of the picture. Write Java code, compile it into bytecode, package it as a JAR, and let the JVM take care of running it.

But now I had come across something that seemed to challenge that assumption.

**GraalVM Native Image.**

After recording the startup time, memory usage, and artifact size of my application running on the JVM, I was curious to see what would happen if I tried running the same application as a native executable.

And honestly, there was a tiny part of me thinking:

> "Wait... if Java needs the JVM to run, how are we suddenly running a Java application without it?"

So I decided to find out.

---

## What is a Native Executable?

The term **native executable** kept coming up while reading about GraalVM.

In simple terms, a native executable is a platform-specific executable containing the application compiled into native machine code, along with the runtime components required by the application.

The important difference is what happens at runtime.

With the traditional JVM approach:

```text
Java Source
    ↓
Java Compiler
    ↓
Bytecode / JAR
    ↓
JVM
    ↓
Interpretation + JIT Compilation
    ↓
Native Machine Code
    ↓
Application
````

The JVM is therefore an essential part of the runtime environment.

With Native Image, the approach is different:

```text
Java Source
    ↓
AOT Processing + Native Image Build
    ↓
Native Machine Code
    ↓
Native Executable
    ↓
Operating System
    ↓
Application
```

A separate JVM does not need to be started at application runtime.

This is one of the key reasons Native Image can achieve very fast startup times.

---

## What is AOT Compilation?

Another term I kept seeing was **AOT**, which stands for **Ahead-of-Time compilation**.

The basic idea is to move as much work as possible from runtime to build time.

In a traditional JVM application, some work happens when the application is actually running. The JVM can interpret bytecode and use JIT compilation to optimize frequently executed code during runtime.

With Native Image, a significant amount of this work is performed while building the executable.

So the simplified idea is:

```text
Traditional JVM

More work during runtime
        ↓
Application starts
        ↓
JVM performs runtime work and JIT compilation


Native Image

More work during build time
        ↓
Native executable is produced
        ↓
Application starts with much of that work already completed
```

The trade-off is that the native build itself becomes considerably more expensive.

---

## Spring AOT

While exploring Native Image, I also came across another important concept: **Spring AOT**.

Spring Boot applications rely heavily on things such as dependency injection, configuration, reflection, proxies, and runtime discovery.

These patterns are very natural for a JVM-based application, where the JVM and framework can perform a lot of work at runtime.

Native Image has a different execution model, so Spring provides an AOT processing phase to prepare the application for native compilation.

Spring AOT can generate things such as:

* optimized application initialization code
* generated source code and classes
* metadata required by the native build

The basic idea I understood was:

> **Spring AOT prepares the Spring application so that more of the work can be known and performed before runtime.**

This becomes particularly important later when dealing with things that are only known dynamically at runtime.

---

## Setting Up GraalVM

To start the POC, I installed GraalVM from the official GraalVM website.

The environment I eventually used for the successful native build was:

| Component        | Version |
| ---------------- | ------- |
| GraalVM          | 25.0.4  |
| Java             | 25.0.4  |
| Spring Boot      | 4.1.1   |
| Maven            | 3.9.16  |
| Operating System | Windows |

I also configured `JAVA_HOME` and the system `PATH` so that the GraalVM installation was being used.

I verified the setup using commands such as:

```cmd
java -version
native-image --version
mvnw.cmd -version
```

The important thing at this stage was making sure that both Java and the Native Image tooling were available from the command line.

---

## Building the Native Image

Once the environment was ready, I used the Spring Boot native build support provided through Maven.

The command was:

```cmd
mvnw.cmd -Pnative native:compile
```

Unlike a normal Maven build, this process took several minutes.

That made sense after understanding what Native Image was doing during the build. Instead of leaving a significant amount of work for the JVM to perform at runtime, the native build performs extensive analysis and compilation ahead of time.

The build successfully generated:

```text
target\poc.exe
```

The successful native build took approximately:

**5 minutes 12 seconds**

and the generated executable was approximately:

**96 MB**

---

## First Successful Native Run

I then ran the generated executable directly:

```cmd
target\poc.exe
```

The application started successfully and reported:

```text
Starting AOT-processed PocApplication using Java 25.0.4
Tomcat initialized with port 8080
Tomcat started on port 8080
Started PocApplication in 0.102 seconds
```

And this was the moment that got my attention.

The application that previously took around **1.7 seconds** to start on the JVM was now starting in:

**0.102 seconds**

That was significantly faster than what I had seen with the JVM.

---

## Native Image Measurements

The measurements from the native executable were:

* **Startup time:** 0.102 sec
* **Runtime memory:** 60.27 MB
* **Executable size:** ~96 MB
* **Native build time:** 5m 12s

For a quick reference:

| Metric         |        JVM |     Native |
| -------------- | ---------: | ---------: |
| Startup time   |  1.722 sec |  0.102 sec |
| Runtime memory |  144.06 MB |   60.27 MB |
| Artifact size  | ~19 MB JAR | ~96 MB EXE |
| Build time     |          — |     5m 12s |

The JVM build time was not captured as precisely as the native build time, so I am not treating that as a direct comparison here.

---

## What surprised me?

The startup time was the first thing that surprised me.

**0.102 seconds** felt almost unreal compared with the roughly 1.7 seconds I had seen earlier.

The memory usage was also significantly lower in this particular experiment.

But there was another thing I noticed during the native build itself.

The native compilation process made heavy use of my system's CPU cores and RAM. While the build was running, my system became noticeably slower.

So Native Image introduced an interesting trade-off:

> **Less work at application startup came with significantly more work during the build process.**

This was my first real look at the trade-offs involved instead of just reading about the benefits.

---

## What I Learned From This Experiment

At this point, I had a working native executable and some actual numbers to compare with my JVM baseline.

The initial results were promising:

* Startup time was much lower.
* The measured runtime memory was lower.
* The native executable was considerably larger than the JAR.
* Native compilation took significantly longer.
* The build process itself was resource-intensive.

But there was one important question still left.

**Was the native executable actually compatible with everything inside my application?**

The fact that the application could be compiled successfully did not necessarily mean that everything would work correctly at runtime.

That question became much more interesting in the next stage of the experiment.

```

Available next action: :contentReference[oaicite:0]{index=0}
```
