package ru.bublinoid.parser;

import ru.bublinoid.entity.TlvStructure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * A class for parsing TLV (Tag-Length-Value) encoded data.
 */
public class TlvParser {

    private static final Logger logger = Logger.getLogger(String.valueOf(TlvParser.class));

    /**
     * Reads a hex-encoded file and converts it to a byte array.
     *
     * @param filePath
     * @return A byte array
     * @throws IOException
     */
    public byte[] readHexFile(String filePath) throws IOException {
        String hexString = Files.readString(Path.of(filePath))
                .replaceAll("\\s+", ""); // Remove whitespace
        return hexStringToByteArray(hexString);
    }

    /**
     * Converts a hexadecimal string to a byte array.
     *
     * @param s
     * @return A byte array representing the hexadecimal string.
     * @throws IllegalArgumentException
     */
    public byte[] hexStringToByteArray(String s) {
        int len = s.length();
        if (len % 2 != 0) {
            throw new IllegalArgumentException("Invalid hex string: uneven length.");
        }

        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * Parses a TLV (Tag-Length-Value) encoded byte array into a list of {@link TlvStructure} objects.
     *
     * @param data The byte array containing TLV encoded data.
     * @return A list of {@link TlvStructure} objects parsed from the byte array.
     * @throws RuntimeException If the length of a TLV value exceeds the available data.
     */
    public List<TlvStructure> parseTlv(byte[] data) {
        List<TlvStructure> tlvStructures = new ArrayList<>();
        int index = 0;

        while (index < data.length) {
            try {


                int[] tagInfo = parseTag(data, index);
                int tag = tagInfo[0];
                int tagLength = tagInfo[1];
                index += tagLength;

                int[] lengthInfo = parseLength(data, index);
                int length = lengthInfo[0];
                int lengthLength = lengthInfo[1];
                index += lengthLength;

                if (index + length > data.length) {
                    throw new RuntimeException("Length of TLV value exceeds available data.");
                }

                byte[] value = new byte[length];
                System.arraycopy(data, index, value, 0, length);
                index += length;

                tlvStructures.add(new TlvStructure(tag, length, value));
            } catch (IllegalArgumentException e) {
                logger.severe("Error parsing TLV at index " + index + ": " + e.getMessage());
                break;
            }

        }

        return tlvStructures;
    }

    /**
     * Parses the tag from a byte array starting at the specified index.
     *
     * @param data
     * @param index
     * @return An array
     */
    private int[] parseTag(byte[] data, int index) {
        if (index >= data.length) {
            throw new IllegalArgumentException("Invalid index: exceeds data length");
        }

        int tag = data[index] & 0xFF;
        int tagNumber = tag & 0x1F;
        int tagLength = 1;

        if (tagNumber == 0x1F) {  // Multi-byte tag
            tagNumber = 0;
            do {
                if (index + tagLength >= data.length) {
                    throw new IllegalArgumentException("Invalid multi-byte tag at index " + index);
                }
                tagLength++;
                tag = data[index + tagLength - 1] & 0xFF;
                tagNumber = (tagNumber << 7) | (tag & 0x7F);
            } while ((tag & 0x80) != 0);
        }

        return new int[]{tag, tagLength};
    }

    /**
     * Parses the length field from a byte array starting at the specified index.
     *
     * @param data The byte array containing TLV encoded data.
     * @param index The starting index for parsing the length field.
     * @return An array containing the length value and the number of bytes used to encode the length.
     */
    private int[] parseLength(byte[] data, int index) {
        int length = data[index] & 0xFF;

        if (length <= 0x7F) {
            return new int[]{length, 1}; // Short form
        } else {
            int lengthOfLength = length & 0x7F;
            length = 0;
            for (int i = 0; i < lengthOfLength; i++) {
                length = (length << 8) | (data[index + i + 1] & 0xFF);
            }
            return new int[]{length, lengthOfLength + 1}; // Long form
        }
    }
}
