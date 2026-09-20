# 06 - Results & Comparison

After going through all the experiments, I finally had enough data to step back and compare the JVM and GraalVM Native Image properly.

The goal wasn't to prove that one is universally better than the other.

I wanted to understand:

> **What did I actually gain by using Native Image, and what did I give up?**

---

## Experiment 1 — Basic Spring Boot Application

I started with a very small Spring Boot application containing just a simple controller.

| Metric | JVM | Native Image |
|---|---:|---:|
| Java | 25.0.4 | 25.0.4 |
| Spring Boot | 4.1.1 | 4.1.1 |
| Startup | 1.722 sec | **0.102 sec** |
| Memory | 144.06 MB | **60.27 MB** |
| Artifact size | ~19 MB JAR | ~96 MB `.exe` |
| Native build | — | 5m 12s |

The difference was pretty obvious.

Native Image started much faster and used less memory in my measurement.

But the executable was much larger, and producing it took considerably more time.

---

## Experiment 2 — JPA + Hibernate + MySQL

Then I made the application more realistic by introducing JPA, Hibernate and MySQL.

| Metric | JVM | Native Image |
|---|---:|---:|
| Java | 25.0.4 | 25.0.4 |
| Spring Boot | 4.1.1 | 4.1.1 |
| Startup | 4.611 sec | **0.451 sec** |
| Memory | — | **116.16 MB** |
| Native build | — | 8m 47s |
| Artifact size | — | **162.78 MB `.exe`** |
| JPA + Hibernate | ✅ | ✅ |
| MySQL | ✅ | ✅ |
| `/users-jpa` | ✅ | ✅ |

I didn't include a JVM memory value here because I didn't capture a clean JPA-specific measurement that I could fairly compare with the native result.

---

# What Stood Out?

### 🚀 Startup Time

This was probably the biggest difference I observed.

My basic application went from:

**1.722 sec → 0.102 sec**

And even after adding JPA + Hibernate:

**4.611 sec → 0.451 sec**

So the startup improvement wasn't limited to my tiny Hello World-style application.

---

### 💾 Memory

In my basic application experiment:

**144.06 MB → 60.27 MB**

That's roughly a **58% reduction** in the observed process memory.

Again, these are measurements from my local Windows environment, not universal benchmarks.

---

### 📦 Artifact Size

This was the opposite direction.

My original JAR was around:

**19 MB**

while the native executable was around:

**96 MB**

And with JPA + Hibernate, the native executable grew to:

**162.78 MB**

So Native Image definitely isn't about producing a smaller artifact.

---

### ⏳ Build Time

This was probably the biggest downside I experienced.

My simple native build took around:

**5m 12s**

With JPA + Hibernate, the native image generation took:

**8m 47s**

and the complete Maven build took:

**9m 33s**

Compared to a normal JVM workflow, that's a very different development experience.

My CPU definitely knew I was experimenting with GraalVM. 😂

---

# The Trade-Off

After all these experiments, I started seeing Native Image less as a replacement for the JVM and more as a **different runtime strategy**.

```text
                 JVM
                  │
       More work at runtime
                  │
                  ▼
        Faster development/build
                  │
                  ▼
        More runtime flexibility


            Native Image
                  │
       More work at build time
                  │
                  ▼
        Longer native builds
                  │
                  ▼
       Faster startup / lower
       observed runtime memory
````

So the trade-off is not simply:

> **JVM = bad, Native = good**

It's more like:

> **Where do you want to pay the cost — during the build or during runtime?**

---

# What About the Problems I Faced?

This experiment wasn't just about collecting performance numbers.

I also ran into a few problems along the way.

### Native Image runtime failure

My first native build successfully produced an executable but crashed at runtime with a `ClassNotFoundException`.

That taught me that:

> **A successful native build doesn't necessarily mean the application will run successfully.**

I eventually traced that experiment back to a GraalVM version compatibility issue and moved to GraalVM 25.

---

### Reflection

My reflection experiment initially failed in Native Image because the class was being accessed dynamically at runtime.

Adding the required reflection metadata fixed it.

That taught me about the difference between:

**Build-time knowledge vs runtime dynamism.**

---

### JPA + Hibernate

JPA initially made me wonder whether a more complex framework would work with Native Image.

It did.

Hibernate initialized successfully, connected to MySQL, and the JPA endpoint worked in the native executable.

---

# So... What Did I Actually Gain?

From this POC, the biggest benefits I personally observed were:

* ⚡ Much faster application startup
* 💾 Lower observed runtime memory in my basic experiment
* 📦 A self-contained native executable
* 🚀 Potentially interesting characteristics for applications where startup time matters

But there were also clear costs:

* ⏳ Much longer native builds
* 🧠 Higher resource usage during native compilation
* 📦 Larger executable size
* 🔍 More attention required for reflection and build-time configuration
* 🛠️ More things to consider when frameworks or dynamic behavior are involved

---

# My Final Takeaway

When I started this experiment, my thought was basically:

> **"Let's replace the JVM."**

After actually building, breaking, debugging, measuring, and rebuilding the application, I wouldn't describe it that way anymore.

Native Image isn't simply a "better JVM."

It's a different way of running Java applications with a different set of trade-offs.

The part I found most valuable wasn't even the final startup number.

It was understanding **why** those numbers changed.

I got to see firsthand how:

```text
Build-time analysis
        ↓
AOT processing
        ↓
Native compilation
        ↓
Less runtime work
        ↓
Faster startup
```

And I also learned that the benefits come with real costs during development and build time.

That was probably the biggest takeaway from this POC:

> **Don't just read that a technology is faster. Build it, break it, measure it, and understand why.** 😄

---

## Final Numbers at a Glance

| Metric       |  Basic JVM |  Basic Native |   JPA JVM |    JPA Native |
| ------------ | ---------: | ------------: | --------: | ------------: |
| Startup      |  1.722 sec | **0.102 sec** | 4.611 sec | **0.451 sec** |
| Memory       |  144.06 MB |  **60.27 MB** |         — |     116.16 MB |
| Artifact     | ~19 MB JAR |    ~96 MB EXE |         — | 162.78 MB EXE |
| Native build |          — |        5m 12s |         — |        8m 47s |

> **Note:** These are observations from my local Windows environment and should not be treated as universal benchmarks. Different machines, workloads, configurations, and application sizes can produce very different results.

---

## What's Next?

I started this POC wanting to understand:

> **"What exactly is GraalVM Native Image?"**

I ended up learning about:

* AOT compilation
* Spring AOT
* Native Image build-time analysis
* Reachability metadata
* Reflection configuration
* JPA and Hibernate
* Runtime vs build-time trade-offs
* And, most importantly, debugging something that initially looked like a completely different problem. 😂

The next step is to document the practical lessons and trade-offs from the entire experiment.


