package ru.bublinoid.printer;

import ru.bublinoid.entity.TlvStructure;
import ru.bublinoid.parser.TlvParser;

import java.util.List;

/**
 * Класс для форматирования и вывода TLV-структур.
 */
public class TlvPrinter {

    /**
     * Выводит данные TLV на экран с аннотацией и уровнями вложенности.
     * @param tlvStructures список структур TLV для вывода
     */
    public void printTlv(List<TlvStructure> tlvStructures) {
        printTlv(tlvStructures, 0);
    }

    private void printTlv(List<TlvStructure> tlvStructures, int level) {
        String indent = "  ".repeat(level); // Отступ для текущего уровня

        for (TlvStructure tlv : tlvStructures) {
            String tagClass = getClassDescription(tlv.getTag());
            String tagType = getTypeDescription(tlv.getTag());
            int tagId = getTagId(tlv.getTag());

            System.out.printf("%sTag (class: %s, type: %s, id: %d) [%02X]%n",
                    indent, tagClass, tagType, tagId, tlv.getTag());
            System.out.printf("%sLength: %d%n", indent, tlv.getLength());
            System.out.printf("%sValue: %s%n", indent, bytesToHex(tlv.getValue()));

            // Если TLV конструкция (type = constructed), распарсим вложенные TLV
            if (isConstructed(tlv.getTag())) {
                TlvParser nestedParser = new TlvParser();
                List<TlvStructure> nestedTlv = nestedParser.parseTlv(tlv.getValue());
                printTlv(nestedTlv, level + 1); // Рекурсия для вложенных TLV
            }
        }
    }

    private String getClassDescription(int tag) {
        int tagClass = (tag >> 6) & 0x03;
        return switch (tagClass) {
            case 0 -> "Universal";
            case 1 -> "Application";
            case 2 -> "Context-specific";
            case 3 -> "Private";
            default -> "Unknown";
        };
    }

    private String getTypeDescription(int tag) {
        int tagType = (tag >> 5) & 0x01;
        return tagType == 0 ? "Primitive" : "Constructed";
    }

    private int getTagId(int tag) {
        return tag & 0x1F;
    }

    private boolean isConstructed(int tag) {
        int tagType = (tag >> 5) & 0x01;
        return tagType == 1;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
