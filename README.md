# POJB
## The Plain Old Java Bot
This is a project that I'm working on for my own fulfillment and entertainments. 
The goal is to make a Discord moderation bot using only the Java Standard Library (JDK). I am not
even allowing myself the use of a build tool (Gods, how I miss Gradle) or testing dependencies. So
everything will be built by hand.

If you're interested in running this for yourself for some reason, a) you're as crazy as I am, and b)
here's what you need to know:

### Pre-requisites

**JDK21.** That's literally it. You will not need to install any dependencies, or frameworks, or use
any build tools. See below.

### How to use
This project uses its own "build tool" (if you can call it that).

| Command                   | What it does                                                           |
|---------------------------|------------------------------------------------------------------------|
| `java Build.java compile` | Compiles all source files in `src/` into `out/classes`                 |
| `java Build.java jar`     | Compile all source files **and** package them into a `.jar`            |
| `java Build.java test`    | Currently stubbed to `System.exit(0)` until test harnesses are written |
| `java Build.java help`    | Prints the help menu                                                   |

### How to run
`java Build.java jar` -> Compiles source and packages the jar

`java -jar out/libs/modbot.jar` -> Runs the bot. Run with --version to see app version
