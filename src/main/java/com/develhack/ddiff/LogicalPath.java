package com.develhack.ddiff;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static java.util.stream.Collectors.joining;

public class LogicalPath {

    public static class LogicalPathElement {
        public final Path name;
        public final Path renamed;

        LogicalPathElement(Path name, Path renamed) {
            this.name = name;
            this.renamed = renamed;
        }

        @Override
        public String toString() {
            if (renamed == null) {
                return name.toString();
            }
            return String.format("(%s|%s)", name, renamed);
        }
    }

    public final List<LogicalPathElement> logicalPathElements;
    public final Path originalPhysicalPath;
    public final Path revisedPhysicalPath;

    public static LogicalPath root(Path originalPhysicalPath, Path revisedPhysicalPath) {
        return new LogicalPath(Collections.emptyList(), originalPhysicalPath, revisedPhysicalPath);
    }

    public LogicalPath(List<LogicalPathElement> logicalPath, Path originalPhysicalPath, Path revisedPhysicalPath) {
        this.logicalPathElements = Collections.unmodifiableList(logicalPath);
        this.originalPhysicalPath = originalPhysicalPath;
        this.revisedPhysicalPath = revisedPhysicalPath;
    }

    public LogicalPath resolve(Path name) {
        List<LogicalPathElement> logicalPath = new ArrayList<>(this.logicalPathElements);
        logicalPath.add(new LogicalPathElement(name, null));
        return new LogicalPath(Collections.unmodifiableList(logicalPath), originalPhysicalPath.resolve(name),
                revisedPhysicalPath.resolve(name));
    }

    public LogicalPath resolve(Path name, Path renamed) {
        List<LogicalPathElement> logicalPath = new ArrayList<>(this.logicalPathElements);
        logicalPath.add(new LogicalPathElement(name, renamed));
        return new LogicalPath(Collections.unmodifiableList(logicalPath), originalPhysicalPath.resolve(name),
                revisedPhysicalPath.resolve(renamed));

    }

    @Override
    public String toString() {
        return logicalPathElements.stream().map(LogicalPathElement::toString).collect(joining("/"));
    }
}
