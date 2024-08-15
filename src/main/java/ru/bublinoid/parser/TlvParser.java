package ru.bublinoid.parser;

import ru.bublinoid.entity.TlvStructure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TlvParser {

    public byte[] readHexFile(String filePath) throws IOException {
        String hexString = Files.readString(Path.of(filePath))
                .replaceAll("\\s+", ""); // Удаление пробельных символов
        return hexStringToByteArray(hexString);
    }

    private byte[] hexStringToByteArray(String s) {
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

    public List<TlvStructure> parseTlv(byte[] data) {
        List<TlvStructure> tlvStructures = new ArrayList<>();
        int index = 0;

        try {
            while (index < data.length) {
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
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Error while parsing TLV structure: " + e.getMessage(), e);
        }

        return tlvStructures;
    }

    private int[] parseTag(byte[] data, int index) {
        int tag = data[index] & 0xFF;
        int tagClass = (tag >> 6) & 0x03;
        int tagType = (tag >> 5) & 0x01;
        int tagNumber = tag & 0x1F;

        int tagLength = 1;
        if (tagNumber == 0x1F) { // Мультибайтный тег
            tagNumber = 0;
            do {
                tagLength++;
                if (index + tagLength > data.length) {
                    throw new RuntimeException("Incomplete multi-byte tag in TLV.");
                }
                tag = data[index + tagLength - 1] & 0xFF;
                tagNumber = (tagNumber << 7) | (tag & 0x7F);
            } while ((tag & 0x80) != 0);
        }

        return new int[]{(tagClass << 24) | (tagType << 23) | tagNumber, tagLength};
    }

    private int[] parseLength(byte[] data, int index) {
        int length = data[index] & 0xFF;

        if (length <= 0x7F) {
            return new int[]{length, 1}; // Короткая форма
        } else {
            int lengthOfLength = length & 0x7F;
            if (index + lengthOfLength >= data.length) {
                throw new RuntimeException("Incomplete length field in TLV.");
            }
            length = 0;
            for (int i = 0; i < lengthOfLength; i++) {
                length = (length << 8) | (data[index + i + 1] & 0xFF);
            }
            return new int[]{length, lengthOfLength + 1}; // Длинная форма
        }
    }
}
