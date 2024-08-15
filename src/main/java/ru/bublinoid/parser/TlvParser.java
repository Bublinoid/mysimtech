package ru.bublinoid.parser;



import ru.bublinoid.entity.TlvStructure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для обработки и парсинга TLV-структур.
 */
public class TlvParser {

    /**
     * Читает файл, закодированный в ASCII-шестнадцатеричном формате, и преобразует его в массив байтов.
     * @param filePath путь к файлу
     * @return массив байтов, содержащий данные из файла
     * @throws IOException если файл не удается прочитать
     */
    public byte[] readHexFile(String filePath) throws IOException {
        String hexString = Files.readString(Path.of(filePath))
                .replaceAll("\\s+", "");
        return hexStringToByteArray(hexString);
    }

    /**
     * Преобразует строку в шестнадцатеричном формате в массив байтов.
     * @param s строка в шестнадцатеричном формате
     * @return массив байтов
     */
    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    /**
     * Парсит данные в формате TLV.
     * @param data массив байтов с данными
     * @return список структур TLV
     */
    public List<TlvStructure> parseTlv(byte[] data) {
        List<TlvStructure> tlvStructures = new ArrayList<>();
        int index = 0;

        while (index < data.length) {
            // Парсим тег
            int[] tagInfo = parseTag(data, index);
            int tag = tagInfo[0];
            int tagLength = tagInfo[1];
            index += tagLength;

            // Парсим длину
            int[] lengthInfo = parseLength(data, index);
            int length = lengthInfo[0];
            int lengthLength = lengthInfo[1];
            index += lengthLength;

            // Извлечение значения
            byte[] value = new byte[length];
            System.arraycopy(data, index, value, 0, length);
            index += length;

            // Добавление структуры в список
            tlvStructures.add(new TlvStructure(tag, length, value));
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
            length = 0;
            for (int i = 0; i < lengthOfLength; i++) {
                length = (length << 8) | (data[index + i + 1] & 0xFF);
            }
            return new int[]{length, lengthOfLength + 1}; // Длинная форма
        }
    }

    /**
     * Выводит данные TLV на экран.
     * @param tlvStructures список структур TLV для вывода
     */
    public void printTlv(List<TlvStructure> tlvStructures) {
        for (TlvStructure tlv : tlvStructures) {
            System.out.println(tlv);
        }
    }
}
