import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.jar.Attributes.Name;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.Stream;

class Build {
    private static final String OUT_DIR = "out/";
    private static final Path OUT_PATH = Path.of(OUT_DIR);
    private static final String CLASS_DIR = "classes/";
    private static final Path CLASSES_PATH = Path.of(OUT_DIR, CLASS_DIR);
    private static final String SOURCE_DIR = "src/";
    private static final String JAR_DIR = "libs/";
    private static final String MAIN_CLASS_NAME = "modbot.app.Main";
    private static final String MANIFEST_VERSION = "1.0";
    private static final String JAR_NAME = "modbot.jar";

    public static void main(String[] args) {
        if (args.length != 0) {
            switch (args[0]) {
                case "compile" -> System.exit(cleanCompile());
                case "test" -> System.exit(test());
                case "jar" -> System.exit(cleanCompileJar());
                case "help" -> System.exit(usage());
                default -> {
                    System.err.println("Unknown build option: " + args[0]);
                    System.exit(1);
                }
            }
        } else {
            usage();
            System.exit(1);
        }
    }

    private static int usage() {
        String help = """
            Build.java currently takes the following options:
            compile - compile all .java files in the source directory (src/)
            test - Stubbed to pass until test harnesses are implemented
            jar - compile and package all source files into a Java Archive (jar) found in out/libs/
            help - print this help text
            """;

        System.out.println(help);
        return 0;
    }

    public static int clean() {
        if (Files.exists(OUT_PATH)) {
            try (Stream<Path> walk = Files.walk(OUT_PATH)) {
                walk.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            throw new UncheckedIOException("Failed to delete file: " + path, e);
                        }
                    });
            } catch (Exception e) {
                System.err.println("Failure in cleaning the build path: " + e.getMessage());
                e.printStackTrace();
                return 1;
            }
        }

        return 0;
    }

    public static int compile() {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            System.err.println("A JDK is required in order to use SystemJavaCompiler.");
            return 1;
        }

        Path sourceDir = Path.of(SOURCE_DIR);

        DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();

        try (StandardJavaFileManager fileMan = compiler.getStandardFileManager(diagnosticCollector, null, null)) {
            List<Path> javaSourcePaths;

            try (Stream<Path> walk = Files.walk(sourceDir)) {
                javaSourcePaths = walk
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".java"))
                    .toList();
            }

            if (javaSourcePaths.isEmpty()) {
                System.err.println("No Java files found to compile");
                return 1;
            }

            Iterable<? extends JavaFileObject> compilationCandidates = fileMan.getJavaFileObjectsFromPaths(javaSourcePaths);

            Iterable<String> options = Arrays.asList("--release", "21", "-Xlint:all", "-Werror", "-d", CLASSES_PATH.toString());

            JavaCompiler.CompilationTask task = compiler.getTask(
                null,
                fileMan,
                diagnosticCollector,
                options,
                null,
                compilationCandidates
            );

            boolean success = task.call();

            if (success) {
                System.out.println("Successfully compiled " + javaSourcePaths.size() + " files!");
                return 0;
            } else {
                System.err.println("Compilation failed");
                for (Diagnostic<? extends JavaFileObject> diag : diagnosticCollector.getDiagnostics()) {
                    Kind errorKind = diag.getKind();
                    JavaFileObject source = diag.getSource();
                    long ln = diag.getLineNumber();
                    String msg = diag.getMessage(null);

                    if (source == null) {
                        // Error message without a source
                        System.err.println(errorKind + ": on line " + ln + ": " + msg);
                    } else {
                        // Error message with a source
                        System.err.println(errorKind + ": in " + source.getName() + ":" + ln + " " + msg);
                    }
                }
                return 1;
            }
        } catch (Exception e) {
            System.err.println("A Runtime exception occurred while compiling: " + e.getMessage());
            e.printStackTrace();
            return 1;
        }
    }

    public static int test() {
        // Stubbed until test harnesses are implemented.
        return 0;
    }

    public static int jar() {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Name.MANIFEST_VERSION, MANIFEST_VERSION);
        manifest.getMainAttributes().put(Name.MAIN_CLASS, MAIN_CLASS_NAME);

        Path outputPath = Path.of(OUT_DIR, JAR_DIR);
        Path outputJarPath = outputPath.resolve(JAR_NAME);

        if (outputJarPath.getParent() != null) {
            try {
                Files.createDirectories(outputJarPath.getParent());
            } catch (Exception e) {
                System.err.println("Failed to create folder on output path: " + outputJarPath.getParent());
                return 1;
            }
        }

        try (JarOutputStream target = new JarOutputStream(new FileOutputStream(outputJarPath.toFile()), manifest)) {
            try (Stream<Path> walk = Files.walk(CLASSES_PATH)) {
                walk
                    .filter(path -> !Files.isDirectory(path))
                    .filter(path -> path.getFileName().toString().endsWith(".class"))
                    .forEach(path -> {
                        Path relativePath = CLASSES_PATH.relativize(path);

                        try {
                            JarEntry entry = new JarEntry(relativePath.toString());
                            target.putNextEntry(entry);
                            Files.copy(path, target);
                            target.closeEntry();
                        } catch (IOException e) {
                            throw new UncheckedIOException("Failed to pack file: " + path, e);
                        }
                    });
            }
        } catch (Exception e) {
            System.err.println("An error has occurred while packaging the jar file: " + e.getMessage());
            e.printStackTrace();
            return 1;
        }

        System.out.println("Successfully packaged all source files!");
        return 0;
    }

    private static int cleanCompile() {
        if (clean() == 1) {
            return 1;
        }

        if (compile() == 1) {
            return 1;
        }

        return 0;
    }

    private static int cleanCompileJar() {
        int result = cleanCompile();
        return result != 0 ? result : jar();
    }
}
