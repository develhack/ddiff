# Deep Diff

Diff tool for recursively comparing directories and archives.

Outputs a report in HTML format by default.

## Features

- Recursively compares files contained within the directory.
- Recursively compares files contained within the archive using [Apache Commons Compress](https://commons.apache.org/proper/commons-compress/).
- Map and compare files with different names.
- Can add comparable files using the [plugin](#comparators).
- Can add report format using the [plugin](#reporters).

## Usage

```
Usage: ddiff [-hVv] [-f=<format>] [-R=<renameFile>] [-r=<original=revised>]...
             ORIGINAL REVISED
Recursively compares archives or directories.
      ORIGINAL            The original file or directory to compare.
      REVISED             The revised file or directory to compare.
  -f, --format=<format>   Report format.
                          Can be added via plugins.
                            Default: html
  -h, --help              Show this help message and exit.
  -r, --rename=<original=revised>
                          Specify a mapping of file names before and after
                            renaming.
                          e.g., -r myfile_v1.0.zip=myfile_v1.1.zip
  -R, --renamefile=<renameFile>
                          Specify a file that contains the mappings of file
                            names before and after renaming.
                          The file must be in property file format.
  -v, --verbose           Print verbose output to stderr.
                          Specify multiple -v options up to three times to
                            increase verbosity.
  -V, --version           Print version info and exit.
```

> [!NOTE]
> Report is output to standard output.  
> Please redirect it to a file if necessary. 

## Prerequisites

- Java Runtime Environment 8 or later

## Installation

TODO

## Known plugins

### Comparators

- **Java Cass File** - [ddiff-comparator-javaclass](https://github.com/develhack/ddiff-comparator-javaclass)

### Reporters

- **Microsoft Excel** - [ddiff-reporter-excel](https://github.com/develhack/ddiff-reporter-excel)
