
# 01 - JVM Baseline

## Why did I start with a JVM baseline?

This whole experiment actually started quite randomly.

I was scrolling through Medium articles when I came across an article about **GraalVM**. The name itself caught my attention — it is not exactly a name you come across every day 😄 — so I clicked on it.

After reading about what GraalVM Native Image could potentially offer, I was impressed. But it also made me think about the Java services I work with.

Until then, my primary focus had always been on whether a service was functioning correctly and fulfilling its business requirements. I had not paid much attention to questions like:

- How much memory is the application consuming?
- How long does the application take to start?
- How large is the final application artifact?

For example, if a Spring Boot service took 2–3 seconds to start, that felt perfectly acceptable to me. I had never really stopped to question whether that could be improved.

Then I came across the performance claims around GraalVM Native Image and thought:

> What if I could make these applications start much faster and consume less memory?

My first thought was almost:

> "Let's replace the JVM!"

But replacing something blindly would not really tell me whether the change was useful.

I needed to know what the application looked like **before** the change.

If I couldn't differentiate between the before and after, how would I recognize the effort? 😄

That became the reason for establishing a JVM baseline first.

---

## What did I want to measure?

Before moving to GraalVM Native Image, I wanted to capture a few basic metrics from the same application running normally on the JVM.

The main metrics I decided to record were:

- Application startup time
- Runtime memory usage
- Application artifact size
- Build time

The idea was simple:

**First measure the JVM application → then move to Native Image → measure again → compare the results.**

---

## Initial Application

I created a very small Spring Boot application for the initial experiment.

At this stage, the application contained a simple controller that returned a `Hello` string. There was no database, external service, or significant business logic involved.

The purpose was not to build a realistic production service yet. I wanted to start with the smallest possible application so that I could clearly understand what changes when moving from the JVM to a native executable.

---

## Environment

| Component | Version |
|---|---|
| Java | 25 |
| Spring Boot | 4.1.1 |
| Maven | 3.9.16 |
| Operating System | Windows |

---

## How I measured the baseline

### Startup Time

Spring Boot prints the application startup time in its startup logs.

```text
Started PocApplication in 1.722 seconds
````

**Observed startup time:** `1.722 seconds`

### Memory Usage

I used the Windows `tasklist` command to check the memory usage of the running Java process.

**Observed memory usage:** `144.06 MB`

This is the measured process working-set memory at the time of the observation, so it should be treated as an environment-specific measurement rather than a universal memory requirement for Spring Boot applications.

### Artifact Size

After building the Spring Boot application, I checked the generated JAR inside the `target` directory.

**Observed JAR size:** `~19.0 MB`

---

## JVM Baseline Results

| Metric         |          JVM |
| -------------- | -----------: |
| Startup time   |    1.722 sec |
| Runtime memory |    144.06 MB |
| Artifact size  | ~19.0 MB JAR |

---

## What surprised me?

The result that caught my attention was the memory usage.

The application was extremely simple. It had essentially one controller with no database interaction or meaningful business logic — just a simple `Hello` response.

Yet the entire Spring Boot application took approximately **1.7 seconds to start** and used around **144 MB of process memory**.

That made me think about the applications I normally work with.

Our actual services have considerably more functionality, dependencies, configurations, requests to handle, and in some cases database or external-service interactions.

If such a small application already had this baseline, I started wondering what these numbers would look like for a more realistic application.

This was the point where the GraalVM experiment became more interesting to me.

I now had something concrete to compare against.

---

## Why this baseline matters

At this point, I was not trying to conclude whether the JVM was good or bad, or whether GraalVM Native Image was better.

I simply wanted to establish a reference point.

The next step was to take the same application and try building it as a **GraalVM Native Image**.

That would allow me to compare the two approaches using actual measurements instead of assumptions.

```
