## Huffman Encoding
Here I use Huffman encoding to compress and decompress files. The files `BufferedBitReader` and `BufferedBitWrite` were not written by me, and were provided by professor as Java library does not have classes to read and write bits. In terms of lossless file compression, Huffman encoding gives the smallest possible fixed encoding of a file. A description of how Huffman encoding works can be found online or in the class textbook.

As seen in 'Huffman.java', there are many test cases (including boundry tests) which ensure no errors are present. As for the results, `WarAndPeace.txt` is 10323 lines and was able to be compressed to 65355 lines losslessly.
