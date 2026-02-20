package com.develhack.ddiff;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;

public interface Reporter {

    String getFormat();

    void report(Path originalRoot, Path revisedRoot, List<Diff> diffs, OutputStream os) throws IOException;
}
