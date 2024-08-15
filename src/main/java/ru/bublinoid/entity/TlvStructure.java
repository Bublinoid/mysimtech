package ru.bublinoid.entity;

import lombok.Data;

/**
 * Класс для представления структуры TLV.
 */
@Data
public class TlvStructure {
    private final int tag;
    private final int length;
    private final byte[] value;

    @Override
    public String toString() {
        return String.format("Tag: %02X, Length: %d, Value: %s",
                tag, length, bytesToHex(value));
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
