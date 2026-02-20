package com.develhack.ddiff;

import static java.util.stream.Collectors.toList;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.Patch;

public class DeepDiff {

    private final Map<Path, Path> renameMap;
    private final List<Comparator> comparators;

    public DeepDiff(Map<Path, Path> renameMap, List<Comparator> comparators) {
        this.renameMap = renameMap;
        this.comparators = comparators;
    }

    public List<Diff> compare(LogicalPath path) throws IOException {

        Console.vvv("compare %s vs %s", path.originalPhysicalPath, path.revisedPhysicalPath);
        if (!Files.exists(path.originalPhysicalPath)) {
            return Arrays.asList(Diff.added(path));
        }
        if (!Files.exists(path.revisedPhysicalPath)) {
            return Arrays.asList(Diff.deleted(path));
        }

        List<Diff> result = null;

        result = compareIfDirectories(path);
        if (result != null) {
            return result;
        }

        if (contentEquals(path)) {
            return Arrays.asList(Diff.unchanged(path));
        }

        for (Comparator comparator : comparators) {
            result = comparator.compareIfSupported(path, this);
            if (result != null) {
                return result;
            }
        }

        Console.v("%s: no supported comparator found", path);
        return Arrays.asList(Diff.changed(path, null, null, null));
    }

    List<Diff> compareIfDirectories(LogicalPath path) throws IOException {

        if (!Files.isDirectory(path.originalPhysicalPath)) {
            if (!Files.isDirectory(path.revisedPhysicalPath)) {
                return null;
            }
            return Arrays.asList(Diff.uncomparable(path));
        }

        if (!Files.isDirectory(path.revisedPhysicalPath)) {
            return Arrays.asList(Diff.uncomparable(path));
        }

        Set<Path> originalItems = new HashSet<>();
        try (Stream<Path> stream = Files.list(path.originalPhysicalPath)) {
            originalItems.addAll(stream.map(path.originalPhysicalPath::relativize).collect(toList()));
        }

        Set<Path> revisedItems = new HashSet<>();
        try (Stream<Path> stream = Files.list(path.revisedPhysicalPath)) {
            revisedItems.addAll(stream.map(path.revisedPhysicalPath::relativize).collect(toList()));
        }

        for (Path originalItem : originalItems) {
            Path renamedItem = renameMap.get(originalItem);
            if (renamedItem != null) {
                revisedItems.remove(renamedItem);
            }
        }

        Set<Path> allItems = new TreeSet<>(originalItems);
        allItems.addAll(revisedItems);

        List<Diff> result = new ArrayList<>();
        for (Path item : allItems) {
            LogicalPath subpath;
            Path renamedItem = renameMap.get(item);
            if (renamedItem == null) {
                subpath = path.resolve(item);
            } else {
                subpath = path.resolve(item, renamedItem);
            }
            result.addAll(compare(subpath));
        }

        return result;
    }

    public List<Diff> compareText(LogicalPath path, List<String> original, List<String> revised) {

        Patch<String> patch = DiffUtils.diff(original, revised);
        if (patch.getDeltas().isEmpty()) {
            return Arrays.asList(Diff.unchanged(path));
        }

        return Arrays.asList(Diff.changed(path, patch, original, revised));
    }

    public boolean contentEquals(LogicalPath path) throws IOException {

        if (Files.size(path.originalPhysicalPath) != Files.size(path.revisedPhysicalPath)) {
            return false;
        }

        try (
                BufferedInputStream bis1 = new BufferedInputStream(Files.newInputStream(path.originalPhysicalPath));
                BufferedInputStream bis2 = new BufferedInputStream(Files.newInputStream(path.revisedPhysicalPath))) {

            int i1;
            while ((i1 = bis1.read()) != -1) {
                int i2 = bis2.read();
                if (i1 != i2) {
                    return false;
                }
            }
        }

        return true;
    }
}
