package com.develhack.ddiff;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.concurrent.Callable;
import java.util.stream.StreamSupport;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.IHelpSectionRenderer;
import picocli.CommandLine.Model.UsageMessageSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "ddiff", versionProvider = VersionProvider.class, description = "Recursively compares archives or directories.", showDefaultValues = true)
public class Main implements Callable<Integer> {

    public static void main(String[] args) {

        Main main = new Main();
        CommandLine commandLine = new CommandLine(main);

        Map<String, IHelpSectionRenderer> helpSectionMap = commandLine.getHelpSectionMap();
        helpSectionMap.put(UsageMessageSpec.SECTION_KEY_FOOTER_HEADING, help -> main.getPluginsInfo());

        System.exit(commandLine.execute(args));
    }

    @Parameters(paramLabel = "ORIGINAL", index = "0", description = "The original file or directory to compare.")
    private Path path1;

    @Parameters(paramLabel = "REVISED", index = "1", description = "The revised file or directory to compare.")
    private Path path2;

    @Option(names = { "-V", "--version" }, versionHelp = true, description = "Print version info and exit.")
    boolean versionRequested;

    @Option(names = { "-h", "--help" }, usageHelp = true, description = "Show this help message and exit.")
    boolean helpRequested;

    @Option(names = { "-f", "--format" }, description = { "Report format.",
            "Can be added via plugins." }, defaultValue = "html")
    private String format;

    @Option(paramLabel = "<original=revised>", names = { "-r", "--rename" }, description = { "Specify a mapping of file names before and after renaming.",
            "e.g., -r myfile_v1.0.zip=myfile_v1.1.zip" })
    private Map<Path, Path> renameMap;

    @Option(names = { "-R", "--renamefile" }, description = {
            "Specify a file that contains the mappings of file names before and after renaming.",
            "The file must be in property file format." })
    private Path renameFile;

    @Option(names = { "-v", "--verbose" }, description = { "Print verbose output to stderr.",
            "Specify multiple -v options up to three times to increase verbosity." })
    private boolean[] verbosity;

    private final List<Comparator> comparators;

    private final Map<String, Reporter> reporters;

    Main() {

        ServiceLoader<Comparator> comparatorServiceLoader = ServiceLoader.load(Comparator.class);
        comparators = StreamSupport.stream(comparatorServiceLoader.spliterator(), false)
                .collect(toList());

        ServiceLoader<Reporter> reporterServiceLoader = ServiceLoader.load(Reporter.class);
        reporters = StreamSupport.stream(reporterServiceLoader.spliterator(), false)
                .collect(toMap(Reporter::getFormat, identity()));
    }

    @Override
    public Integer call() throws Exception {

        if (verbosity != null) {
            Console.verbosity = verbosity.length;
        }

        if (!Files.isReadable(path1)) {
            Console.log("%s cannot be read.", path1);
            return 1;
        }
        if (!Files.isReadable(path2)) {
            Console.log("%s cannot be read.", path2);
            return 1;
        }

        if (renameMap == null) {
            renameMap = new HashMap<>();
        }
        if (renameFile != null) {
            if (!Files.isReadable(renameFile)) {
                Console.log("%s cannot be read.", renameFile);
                return 1;
            }

            try (Reader reader = Files.newBufferedReader(renameFile)) {
                Properties properties = new Properties();
                properties.load(reader);
                for (final String name : properties.stringPropertyNames()) {
                    renameMap.put(Paths.get(name), Paths.get(properties.getProperty(name)));
                }
            }
        }

        Reporter reporter = reporters.get(format);
        if (reporter == null) {
            Console.log("unsupported format: %s", format);
            return 1;
        }

        DeepDiff diff = new DeepDiff(renameMap, comparators);

        List<Diff> diffs = diff.compare(LogicalPath.root(path1, path2));

        reporter.report(path1, path2, diffs, System.out);

        return 0;
    }

    String getPluginsInfo() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        pw.println();
        pw.println("The following comparators are available:");
        for (Comparator comparator : comparators) {
            pw.printf("  %s", comparator.getClass().getName());
            pw.println();
        }

        pw.println();
        pw.println("The following reporters are available:");
        for (Reporter reporter : reporters.values()) {
            pw.printf("  %s: %s", reporter.getFormat(), reporter.getClass().getName());
            pw.println();
        }

        return sw.toString();
    }
}
