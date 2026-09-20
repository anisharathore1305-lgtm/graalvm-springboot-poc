
# 05 - JPA + Hibernate with GraalVM Native Image

## Why JPA?

After the reflection experiment, I wanted to test something more realistic.

I decided to add **JPA + Hibernate + MySQL** to the application and see how it behaves with GraalVM Native Image.

The main question was:

> **"Can a Spring Boot application using JPA and Hibernate actually run as a native executable?"**

---

## Setting Up JPA

I added Spring Data JPA:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
````

I already had a MySQL database with a simple `users` table containing a few records.

I then created a JPA entity:

```java
@Entity
@Table(name = "users")
public class JpaUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String email;
}
```

And a repository:

```java
public interface JpaUserRepository
        extends JpaRepository<JpaUser, Integer> {
}
```

Finally, I exposed a simple endpoint:

```text
GET /users-jpa
```

which simply called:

```java
repository.findAll();
```

---

## First Test — JVM

Before trying Native Image, I tested the application normally on the JVM.

JPA and Hibernate initialized successfully.

Hibernate connected to MySQL and executed the query to fetch the users.

The endpoint worked:

```text
/users-jpa → ✅
```

One thing I immediately noticed was the startup time.

My original small Spring Boot application started in around:

**1.722 sec**

With JPA + Hibernate, it increased to:

**4.611 sec**

That made sense because there was now considerably more initialization happening.

---

## Now Let's Try Native Image

The JVM version worked.

So I rebuilt the application using GraalVM Native Image.

```cmd
mvnw.cmd clean
mvnw.cmd -Pnative native:compile
```

This build took considerably longer than my earlier Native Image build.

The native image generation took:

**8m 47s**

and the complete Maven build took:

**9m 33s**

My CPU had another workout. 😂

---

## And... Did It Work?

I ran the generated executable:

```cmd
target\poc.exe
```

And this time, I watched the logs carefully.

Hibernate initialized successfully.

HikariCP started.

The application connected to MySQL.

The `EntityManagerFactory` was created.

And most importantly:

```text
/users-jpa → ✅
```

The native executable successfully fetched the users from MySQL.

So:

* JPA → ✅
* Hibernate → ✅
* MySQL → ✅
* Native Image → ✅

That was actually a pretty satisfying result. 😄

---

## What About Startup and Memory?

The native application started in:

**0.451 sec**

Compared to:

**4.611 sec** on the JVM.

For memory, the native process was:

* **110.48 MB** before calling `/users-jpa`
* **116.16 MB** after calling `/users-jpa`

The generated native executable was:

**162.78 MB**

---

## JVM vs Native

| Metric          |       JVM |         Native Image |
| --------------- | --------: | -------------------: |
| Java            |    25.0.4 |               25.0.4 |
| Spring Boot     |     4.1.1 |                4.1.1 |
| Startup         | 4.611 sec |        **0.451 sec** |
| Memory          |         — |        **116.16 MB** |
| Native build    |         — |           **8m 47s** |
| Artifact        |         — | **162.78 MB `.exe`** |
| JPA + Hibernate |         ✅ |                    ✅ |
| MySQL           |         ✅ |                    ✅ |
| `/users-jpa`    |         ✅ |                    ✅ |

I haven't included JVM memory here because I didn't capture a clean JPA-specific JVM memory measurement.

---

## What Did I Learn?

This experiment gave me a much better understanding of how Native Image behaves with a more realistic backend stack.

### JPA + Hibernate can work with Native Image

This was the main thing I wanted to verify.

My native executable successfully initialized Hibernate, connected to MySQL, and served the JPA endpoint.

### More complexity means more work during the build

Compared to my earlier Native Image build, adding JPA + Hibernate made the native build significantly heavier.

The executable also became much larger.

### But startup was still fast

Even with JPA, Hibernate, HikariCP and MySQL involved:

**4.611 sec → 0.451 sec**

That was the most interesting part of this experiment.

---

## The Bigger Lesson

At this point, I was starting to see the actual trade-off with Native Image.

```text
More work during build
        ↓
Larger / more expensive native build
        ↓
Less work during startup
        ↓
Much faster application startup
```

So Native Image wasn't simply:

> **"Make Java faster."**

It was more like:

> **"Move more work from runtime to build time."**

