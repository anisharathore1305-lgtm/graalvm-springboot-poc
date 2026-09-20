# 07 - Learnings & Trade-Offs

After completing the POC, I had a much clearer idea of what GraalVM Native Image actually brings to a Spring Boot application.

Here are the main things I learned.

---

## What Worked Well

### ⚡ Faster Startup

This was the biggest difference I observed.

My basic application went from:

**1.722 sec → 0.102 sec**

And with JPA + Hibernate:

**4.611 sec → 0.451 sec**

That's a pretty noticeable improvement.

---

### 💾 Lower Runtime Memory

In my basic experiment:

**144.06 MB → 60.27 MB**

So the native application used considerably less observed process memory in my test.

---

### 🧩 Real Frameworks Still Worked

I wasn't only testing a `Hello World` application.

I was able to run:

- Spring Boot
- JPA
- Hibernate
- HikariCP
- MySQL
- Reflection

inside the native application.

That was probably the most reassuring part of the experiment.

---

## The Trade-Offs

### ⏳ Longer Build Times

This is where Native Image definitely made me wait. 😂

The basic native build took around **5 minutes**, while the JPA version took around **9 minutes** for the complete Maven build.

So the trade-off is basically:

> **More work during build time → less work during startup.**

---

### 📦 Larger Executable

My original JAR was around **19 MB**, while the basic native executable was around **96 MB**.

With JPA + Hibernate, it grew to around **163 MB**.

So Native Image doesn't necessarily mean a smaller artifact.

---

### 🔍 More Attention to Dynamic Features

The reflection experiment taught me that things which are discovered dynamically at runtime may need additional information during the native build.

That's where things like reflection metadata and Spring AOT become important.

---

## What Would I Take Away From This?

When I started this POC, my thought was:

> **"Let's replace the JVM."**

After actually building, breaking, debugging and measuring the application, I don't see Native Image as a simple replacement for the JVM anymore.

It's a different approach with different trade-offs.

```text
Native Image

More work at build time
          ↓
Faster startup
          ↓
Different runtime characteristics
````

But whether those trade-offs are worth it depends on the application.

---

## My Biggest Learning

Honestly, the most valuable part of this POC wasn't just seeing:

**0.102 seconds startup.**

It was the process of:

> **Build → Break → Investigate → Fix → Measure → Understand**

I started this experiment because I wanted to know **what GraalVM Native Image actually does**.

Now I can say I didn't just read about it.

**I actually built it, broke it, and figured out why.** 😄

