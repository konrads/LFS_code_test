Fixed Width 2 Delimited parser
==============================
This code does following:
* reads a spec from [spec.json](src/resources/spec.json), describing fixed width and delimited file structures
* loads the fixed width file according to `FixedWidthEncoding` encoding
  * splits the file into rows, defined by spec's `ColumnNames` and `Offsets`. Note, `Offsets` are used within encoded strings, not on bytes!
* saves the delimited file with `DelimitedEncoding` encoding, following `IncludeHeader` setting  


Usage
-----
```
sbt "run data/sample_input sample_output"
```
