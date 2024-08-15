v1.0

Write a program in Java to do the following:
 
 1) read in an ASCII-hex encoded file as binary data
    (may contain whitespaces like ' ', '\t', '\r' and '\n')
    
 2) the file is assumed to contain a sequence of TLV-encoded structures
    (see encoding.txt)

 3) build internal binary representation of the TLV structures
 
 4) analyze the binary data and dump them to stdout (see examples):
  - raw hex values for tags and length must be annotated with parsed info 
  - nesting levels must be shown by indentation (2 spaces per level)
  (see the provided examples; NOTE - it is not required to replicate the output format exactly as in
  examples)
 
 5) error handling (invalid input data, bad file names, etc.) is expected  

NOTES: 
 - a build system is expected to be used (preferably, Maven or Gradle)
 - Javadoc comments are required


encoding.txt -> v1.0

Data stream is a sequence of Tag-Length-Value (TLV) encoded objects.

A tag is coded on one or several bytes:

1) if the identifier is in [0, 30] range, it is encoded as follows:
 - bits 7-8 encode the class: 
        0 = universal, 1 = application, 2 = context-specific, 3 = private
 - bit 6 encodes the type: 
        0 = primitive (value part is a BLOB)
        1 = constructed (value part is a sequence of sub-TLVs)
 - bits 1-5 encode the tag number
 
2) if the identifier is >= 31, the tag is coded on multiple byte, as follows:
 - 1st (leading) byte is encoded as in 1), with bits 1-5 being equal to 1111b (31)
 - for all subsequent bytes:
        bit 8 is set to 1 for all bytes except the last, for the last one it is set to 0
        bits 1-7 of all such bytes are concatenated together to form the identifier 
            (MSB-first, big-endian format)
        bits 1-7 of the 1st byte following the leading byte are not all zeroes

A length is coded in two ways: indefinite form and definite form. For primitive TLVs only
the definite form is allowed. For constructed ones, either variant is allowed.

1) the definite form is coded on 1 (short) or more (long) bytes. Short form can only code
lengths in [0, 127] range. 
 - short form: 1 byte
        bit 8 is set to 0
        bits 1-7 code the length (0..127)
 - long form: >= 2 bytes
        first byte denotes the number of bytes in the length field:
            bit 8 is set to 1
            bits 1-7 code the number of length bytes (127 is reserved)
        subsequent bytes (the total number is specified in the 1st one) code the 
        length (MSB-first, big-endian format)       

2) the indefinite form is coded on 1 byte:
 - bit 8 is set to 1
 - bits 1-7 are set to 0
 
If the indefinite form is used, the sub-TLV sequence is ended with a terminator TLV:
 - tag byte: 00
 - length byte: 00
