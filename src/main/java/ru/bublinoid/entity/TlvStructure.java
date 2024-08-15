package ru.bublinoid.entity;

import lombok.Data;

/**
 * Class representing a TLV (Tag-Length-Value) structure.
 * This class encapsulates the tag, length, and value components of a TLV structure
 * and provides methods for formatting and extracting tag information.
 */
@Data
public class TlvStructure {

    private final int tag;
    private final int length;
    private final byte[] value;

    /**
     * Returns a string representation of the TLV structure.
     * The format includes the tag in hexadecimal, length as a decimal,
     * and the value as a hexadecimal string.
     *
     * @return a string representation of the TLV structure
     */
    @Override
    public String toString() {
        return String.format("Tag: %02X, Length: %d, Value: %s",
                tag, length, bytesToHex(value));
    }

    /**
     * Converts an array of bytes to a hexadecimal string representation.
     *
     * @param bytes the array of bytes to convert
     * @return a hexadecimal string representing the byte array
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    /**
     * Extracts the tag class from the tag field.
     * The tag class is determined by the two most significant bits of the tag.
     *
     * @return the tag class (0, 1, 2, or 3)
     */
    public int getTagClass() {
        return (tag >> 6) & 0x03;
    }

    /**
     * Extracts the tag type from the tag field.
     * The tag type is determined by the next most significant bit of the tag.
     *
     * @return the tag type (0 or 1)
     */
    public int getTagType() {
        return (tag >> 5) & 0x01;
    }

    /**
     * Extracts the tag number from the tag field.
     * The tag number is determined by the least significant 5 bits of the tag.
     *
     * @return the tag number (0 to 31)
     */
    public int getTagNumber() {
        return tag & 0x1F;
    }
}
