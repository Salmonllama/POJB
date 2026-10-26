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

### Contributing Pre-requisites

**JDK21** In order to write or compile anything you need JDK21

#### Editor Specific Settings
- VSCode, Codium, Code Server: Language Support for Java extension by Red Hat. It's the bare minimum, but I really recommend the Extension Pack for Java extension pack by vscjava. 
- Eclipse: Nothing special, just open the repo
- IntelliJ: Repo still needs .idea/module.xml set up with src/ and tools/ as source folders. I don't have access to IntelliJ at the moment so that'll get resolved eventually.

### How to use
This project uses its own "build tool" (if you can call it that).

| Command                   | What it does                                                           |
|---------------------------|------------------------------------------------------------------------|
| `java tools/Build.java compile` | Compiles all source files in `src/` into `out/classes`                 |
| `java tools/Build.java jar`     | Compile all source files **and** package them into a `.jar`            |
| `java tools/Build.java test`    | Currently stubbed to `System.exit(0)` until test harnesses are written |
| `java tools/Build.java help`    | Prints the help menu                                                   |

### How to run
`java tools/Build.java jar` -> Compiles source and packages the jar

`java -jar out/libs/modbot.jar` -> Runs the bot. Run with --version to see app version
