# 04 - Reflection Experiment

## Why Did I Choose Reflection?

At the end of the previous experiment, I had a question:

> *"If Native Image has to know so much at build time, what happens when my application does something dynamically at runtime?"*

That question led me to **Reflection**.

Normally, many of the classes and relationships an application uses can be determined during compilation and build time.

But Java also gives us the ability to inspect and interact with classes dynamically at runtime.

That's what **Reflection** allows us to do.

With reflection, Java code can dynamically:

* Load classes
* Inspect methods and fields
* Access constructors
* Create objects
* Invoke methods

A simple example is:

```java
Class.forName("some.class.Name");
```

Instead of directly writing:

```java
new SomeClass();
```

we can provide the class name dynamically and let Java find the class at runtime.

---

## But Where Would This Actually Be Useful?

I wanted to understand this through a real-world scenario rather than just calling `Class.forName()` for the sake of the experiment.

Imagine a payment system.

Suppose an application supports multiple payment methods:

```text
CreditCardPayment
UPIPayment
PayPalPayment
```

What if the application doesn't know beforehand which payment method the user is going to select?

The application could receive something like:

```text
paymentMethod=UPI
```

at runtime and then dynamically determine which implementation needs to be used.

This is where runtime discovery can become useful.

The important part is not that Java *cannot know* these classes at all.

The interesting part is:

> **We don't know which class will actually be accessed until runtime.**

And that is exactly the kind of situation I wanted to test with Native Image.

---

## The Experiment

So I decided to create a very small reflection-based experiment.

I created a class and an endpoint that would receive the **class name through an HTTP request**.

The endpoint looked something like:

```text
http://localhost:8080/reflection?className=com.graalvm.poc.User
```

The important part was that the class name was not hardcoded inside the reflection logic.

The application received it at runtime.

The core of the experiment was:

```java
Class<?> clazz = Class.forName(className);
Constructor<?> constructor = clazz.getConstructor();
Object object = constructor.newInstance();
```

So the flow was:

```text
HTTP Request
     ↓
className received at runtime
     ↓
Class.forName(className)
     ↓
Find the class
     ↓
Get its constructor
     ↓
Create object dynamically
```

Now I had exactly the kind of runtime-dynamic behavior I wanted to test.

---

## First Test — JVM

As usual, I first tested everything on the traditional JVM.

I started the Spring Boot application and called:

```text
/reflection?className=com.graalvm.poc.User
```

And it worked.

The class was found and the object was created successfully.

```text
JVM + Reflection
        ↓
Class.forName()
        ↓
Class found ✅
        ↓
Object created ✅
```

No surprises here.

The JVM has the flexibility to load classes dynamically at runtime, so this was exactly what I expected.

---

## Now Let's Try the Native Executable

The real experiment started here.

I built the application as a Native Image and generated `poc.exe`.

The JVM version had worked perfectly, so naturally I expected the native executable to behave the same way.

I started:

```cmd
target\poc.exe
```

and called the exact same endpoint.

And...

💥

It got blasted.

Instead of successfully creating the object, the native executable threw:

```text
java.lang.ClassNotFoundException
```

The class I was trying to load dynamically could not be found.

That was confusing at first.

The class existed.

The exact same code worked on the JVM.

So why was Native Image saying:

> **"ClassNotFoundException"**

---

## What Was Actually Different?

This is where the previous experiment about **build-time analysis** started making sense.

Native Image performs **closed-world analysis** during the build.

In simplified terms, it tries to determine ahead of time:

> **"What classes, methods, fields, resources, and other pieces of the application will be needed at runtime?"**

This works very well when the application uses things that can be discovered during the build.

But in my experiment, I was doing this:

```java
Class.forName(className);
```

And `className` was coming from an HTTP request.

So during the native build, there was no way for Native Image to know:

```text
What class will the user send in the HTTP request?
```

For example, it could be:

```text
com.graalvm.poc.User
```

or some other class name.

The class name only becomes known **at runtime**.

So the situation was:

```text
Class exists in source code
        ↓
JVM can discover it at runtime ✅

But...

Native Image build
        ↓
Doesn't know which class will be requested dynamically
        ↓
Class may not be included for reflective access ❌
```

And that's exactly what I was running into.

---

## So... How Do We Fix This?

Obviously, I couldn't just tell Native Image:

> **"Bro, trust me. This class will be needed later."** 😂

I needed a way to explicitly provide that information during the native build.

This is where **Reflection Metadata** came into the picture.

Native Image allows us to provide configuration describing classes and members that will be accessed reflectively at runtime.

So I created:

```text
src/main/resources/
└── META-INF/
    └── native-image/
        └── reflect-config.json
```

And added metadata for the class used in my experiment:

```json
[
  {
    "name": "com.graalvm.poc.User",
    "allPublicConstructors": true
  }
]
```

The idea was essentially:

> **"This class is going to be accessed through reflection at runtime, so make sure the native executable has the information required for that reflective access."**

---

## Rebuild. Run. Test Again.

After adding the reflection metadata, I cleaned and rebuilt the native executable.

Then I started `poc.exe` again and called the same endpoint:

```text
/reflection?className=com.graalvm.poc.User
```

And this time...

🎉 **It worked.**

The native executable was able to find the class and create the object through reflection.

```text
Native Image
     ↓
Reflection metadata
     ↓
Class available for reflective access
     ↓
Class.forName()
     ↓
Object created ✅
```

---

## What Did I Actually Learn?

My first takeaway was:

> **"Native Image doesn't support reflection."**

But that turned out to be the wrong conclusion.

Native Image **does support reflection**.

The important difference is that Native Image performs extensive analysis at build time.

If something is accessed dynamically at runtime and cannot be discovered during that analysis, Native Image may need additional metadata telling it about that reflective access.

In my case:

```text
JVM

Class.forName(className)
        ↓
Class discovered at runtime
        ↓
Works


Native Image

Class.forName(className)
        ↓
Class name unknown during build
        ↓
Need reflection metadata
        ↓
Works
```

That was a much better understanding of the problem.

---

## The Bigger Lesson

This experiment made one concept much clearer to me:

> **"The class exists" and "Native Image knows that the class will be needed at runtime" are two different things.**

With the JVM, runtime dynamism is much more natural.

With Native Image, more information needs to be known during the build because the application is being compiled ahead of time.

And this is where things like **reflection metadata, reachability metadata, and Spring AOT** become important.

I started this experiment thinking I was simply testing reflection.

Instead, I ended up understanding another important difference between the JVM and Native Image:

> **Build-time knowledge vs. runtime dynamism.**

And now I had another question.

Reflection worked after adding the required metadata.

But what happens when I introduce something much more real-world into the application?

Something that every backend developer deals with sooner or later...

**A database.** 😈
