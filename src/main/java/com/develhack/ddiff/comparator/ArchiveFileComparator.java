package com.develhack.ddiff.comparator;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import com.develhack.ddiff.Comparator;
import com.develhack.ddiff.Console;
import com.develhack.ddiff.DeepDiff;
import com.develhack.ddiff.Diff;
import com.develhack.ddiff.LogicalPath;

public class ArchiveFileComparator implements Comparator {

    @Override
    public List<Diff> compareIfSupported(LogicalPath path, DeepDiff deepDiff) throws IOException {

        try (
                ArchiveInputStream ois = openInputStreamIfArchiveFile(path.originalPhysicalPath);
                ArchiveInputStream ris = openInputStreamIfArchiveFile(path.revisedPhysicalPath)) {

            if (ois == null) {
                if (ris == null) {
                    return null;
                }
                return Arrays.asList(Diff.uncomparable(path));
            }

            if (ris == null) {
                return Arrays.asList(Diff.uncomparable(path));
            }

            Path originalPhysicalPath = extract(path.originalPhysicalPath, ois);
            Path revisedPhysicalPath = extract(path.revisedPhysicalPath, ris);

            return deepDiff
                    .compare(new LogicalPath(path.logicalPathElements, originalPhysicalPath, revisedPhysicalPath));
        }
    }

    ArchiveInputStream openInputStreamIfArchiveFile(Path path) throws IOException {

        BufferedInputStream bis = null;
        InputStream maybeDecompressed = null;

        try {

            bis = new BufferedInputStream(Files.newInputStream(path));
            try {
                maybeDecompressed = CompressorStreamFactory.getSingleton().createCompressorInputStream(bis);
            } catch (CompressorException ce) {
                maybeDecompressed = bis;
            }

            return ArchiveStreamFactory.DEFAULT.createArchiveInputStream(new BufferedInputStream(maybeDecompressed));

        } catch (ArchiveException e) {
            if (maybeDecompressed != null) {
                maybeDecompressed.close();
            }
            if (bis != null) {
                bis.close();
            }
        }

        return null;
    }

    Path extract(Path srcPath, ArchiveInputStream ais) throws IOException {

        Path dir = Files.createTempDirectory("dd4j-");
        Console.vv("extract %s to %s", srcPath, dir);

        ArchiveEntry entry;
        while ((entry = ais.getNextEntry()) != null) {
            if (!ais.canReadEntryData(entry)) {
                Console.log("skip unreadable entry: %s", entry.getName());
                continue;
            }
            Path entryPath = dir.resolve(entry.getName());
            if (Files.exists(entryPath)) {
                Console.log("skip duplicate entry: %s", entry.getName());
                continue;
            }
            if (entry.isDirectory()) {
                Files.createDirectories(entryPath);
            } else {
                Files.createDirectories(entryPath.getParent());
                Files.copy(ais, entryPath);
            }
        }

        return dir;
    }
}
