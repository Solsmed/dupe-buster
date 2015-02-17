# dupe-buster
DupeBuster finds duplicates of images in a set of images and reports the offenders in clean lists.

Usage (linux):
There are two steps involved: setup/indexing and search.

Index images (setup):
This operation takes a while, depending on how many images you have. It only has to be done once though.
java -cp '.:lib/lire.jar:lib/lucene-analyzers-common-4.10.2.jar:lib/lucene-core-4.10.2.jar' Indexer <path to folder of image catalogue>
for example:
java -cp '.:lib/lire.jar:lib/lucene-analyzers-common-4.10.2.jar:lib/lucene-core-4.10.2.jar' Indexer /var/images/

The Indexer will scan the argument folder and its subfolders, and add any files ending in "_original" to its index. The index itself resides in the filesystem in an automatically created folder named "index", created in the folder the Indexer was run from. The ListDupes program expects there to exist sucha a folder named "index".

Generating report on duplicates in image catalgoue:
First, make sure there dupelogs folder exists (and is clean, for your convenience).
mkdir dupelogs
Then run the ListDupes program in a similar fashion as the Indexer:
java -cp '.:lib/lire.jar:lib/lucene-analyzers-common-4.10.2.jar:lib/lucene-core-4.10.2.jar' ListDupes
This will (quickly) scan the index and any cases of dupes that are found will be output to logfiles placed in the dupelogs folder. For example, if there are 20 identically looking images of Tux, these 20 will constitute a dupe case and the canonical filenames of all 20 will be listed in one log file, named "method-0-dupecase-15.log". The numbering of the dupecase is arbitrary. The numbering of the method is always 0, as there is currently only one feature extractor used for indexing.


Compiling:
javac -cp 'lib/lire.jar:lib/lucene-analyzers-common-4.10.2.jar:lib/lucene-core-4.10.2.jar' -Xlint:deprecation Indexer.java
javac -cp 'lib/lire.jar:lib/lucene-analyzers-common-4.10.2.jar:lib/lucene-core-4.10.2.jar' -Xlint:deprecation ListDupes.java
