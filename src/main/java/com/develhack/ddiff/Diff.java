package com.develhack.ddiff;

import java.util.List;

import com.github.difflib.patch.Patch;

public class Diff {

    public enum Status {
        CHANGED, ADDED, DELETED, UNCHANGED, UNCOMPARABLE
    }

    private final LogicalPath path;
    private final Status status;
    private final Patch<String> patch;
    private final List<String> originalLines;
    private final List<String> revisedLines;

    private Diff(LogicalPath path, Status status, Patch<String> patch, List<String> originalLines, List<String> revisedLines) {
        this.path = path;
        this.status = status;
        this.patch = patch;
        this.originalLines = originalLines;
        this.revisedLines = revisedLines;
    }

    public LogicalPath getPath() {
        return path;
    }

    public Status getStatus() {
        return status;
    }

    public Patch<String> getPatch() {
        return patch;
    }

    public List<String> getOriginalLines() {
        return originalLines;
    }

    public List<String> getRevisedLines() {
        return revisedLines;
    }

    @Override
    public String toString() {
        return String.format("path: %s, status: %s", path, status);
    }

    public static Diff changed(LogicalPath path, Patch<String> patch, List<String> originalLines, List<String> revisedLines) {
        Console.v("%s: %s", path, Diff.Status.CHANGED);
        return new Diff(path, Status.CHANGED, patch, originalLines, revisedLines);
    }
    
    public static Diff added(LogicalPath path) {
        Console.v("%s: %s", path, Diff.Status.ADDED);
        return new Diff(path, Status.ADDED, null, null, null);
    }
    
    public static Diff deleted(LogicalPath path) {
        Console.v("%s: %s", path, Diff.Status.DELETED);
        return new Diff(path, Status.DELETED, null, null, null);
    }
    
    public static Diff unchanged(LogicalPath path) {
        Console.v("%s: %s", path, Diff.Status.UNCHANGED);
        return new Diff(path, Status.UNCHANGED, null, null, null);
    }
    
    public static Diff uncomparable(LogicalPath path) {
        Console.v("%s: %s", path, Diff.Status.UNCOMPARABLE);
        return new Diff(path, Status.UNCOMPARABLE, null, null, null);
    }
}
