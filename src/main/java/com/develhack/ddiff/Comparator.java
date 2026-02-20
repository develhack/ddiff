package com.develhack.ddiff;

import java.io.IOException;
import java.util.List;

public interface Comparator {

    List<Diff> compareIfSupported(LogicalPath path, DeepDiff deepDiff) throws IOException;

}
