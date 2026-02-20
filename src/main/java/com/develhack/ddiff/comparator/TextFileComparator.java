package com.develhack.ddiff.comparator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.CharacterCodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import com.develhack.ddiff.Comparator;
import com.develhack.ddiff.Console;
import com.develhack.ddiff.DeepDiff;
import com.develhack.ddiff.Diff;
import com.develhack.ddiff.LogicalPath;

public class TextFileComparator implements Comparator {

    @Override
    public List<Diff> compareIfSupported(LogicalPath path, DeepDiff deepDiff) throws IOException {
        if (!isTextFile(path.originalPhysicalPath)) {
            if (!isTextFile(path.revisedPhysicalPath)) {
                return null;
            }
            return Arrays.asList(Diff.uncomparable(path));
        }

        if (!isTextFile(path.revisedPhysicalPath)) {
            return Arrays.asList(Diff.uncomparable(path));
        }

        try {
            return deepDiff.compareText(path, Files.readAllLines(path.originalPhysicalPath),
                    Files.readAllLines(path.revisedPhysicalPath));
        } catch (CharacterCodingException e) {
            Console.log("%s: %s", path, e.getMessage());
            return Arrays.asList(Diff.uncomparable(path));
        }
    }

    boolean isTextFile(Path path) throws IOException {

        try (InputStream is = Files.newInputStream(path)) {

            byte[] buff = new byte[8000];
            int readed = is.read(buff);
            for (int i = 0; i < readed; ++i) {
                if (buff[i] == 0) {
                    return false;
                }
            }

            return true;
        }
    }
}
