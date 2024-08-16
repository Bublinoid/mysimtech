package ru.bublinoid.printer;

import ru.bublinoid.entity.TlvStructure;
import ru.bublinoid.parser.TlvParser;

import java.util.List;
import java.util.logging.Logger;

/**
 * A class for formatting and printing TLV (Tag-Length-Value) structures with color output in the console.
 */
public class TlvPrinter {

    private static final Logger logger = Logger.getLogger(TlvPrinter.class.getName());

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_YELLOW = "\u001B[33m";

    /**
     * Prints the TLV data to the console with annotations, nesting levels, and color.
     *
     * @param tlvStructures The list of TLV structures to print.
     */
    public void printTlv(List<TlvStructure> tlvStructures) {
        printTlv(tlvStructures, 0, new int[20]);
    }

    /**
     * Recursively prints the TLV structures with indentation and color based on nesting level.
     *
     * @param tlvStructures The list of TLV structures to print.
     * @param level         The current level of nesting.
     * @param tlvCounter    An array to keep track of TLV numbers at each nesting level.
     */
    private void printTlv(List<TlvStructure> tlvStructures, int level, int[] tlvCounter) {
        String indent = "    ".repeat(level);

        for (TlvStructure tlv : tlvStructures) {
            String tagClass = getClassDescription(tlv.getTagClass());
            String tagType = getTypeDescription(tlv.getTagType());
            int tagId = tlv.getTagNumber();

            // Используем цветной вывод
            logger.info(String.format("%s%sTLV #%d%s", indent, ANSI_BLUE, tlvCounter[level], ANSI_RESET));
            logger.info(String.format("%s%s Tag (class: %s, kind: %s, id: %d) [%02X]%s",
                    indent, ANSI_GREEN, tagClass.charAt(0), tagType.charAt(0), tagId, tlv.getTag(), ANSI_RESET));
            logger.info(String.format("%s%s Length: %d [%02X]%s",
                    indent, ANSI_YELLOW, tlv.getLength(), tlv.getLength(), ANSI_RESET));

            if (tlv.getLength() > 0) {
                if (tlv.getTagType() == 1) {
                    TlvParser nestedParser = new TlvParser();
                    List<TlvStructure> nestedTlv = nestedParser.parseTlv(tlv.getValue());
                    logger.info(String.format("%s%s Value: (%d TLVs)%s", indent, ANSI_RED, nestedTlv.size(), ANSI_RESET));
                    tlvCounter[level + 1] = 1;
                    printTlv(nestedTlv, level + 1, tlvCounter);
                } else {
                    logger.info(String.format("%s%s Value: [%s]%s", indent, ANSI_BLUE, bytesToHex(tlv.getValue()), ANSI_RESET));
                }
            } else {
                logger.info(String.format("%s%s Value: []%s", indent, ANSI_RED, ANSI_RESET));
            }

            tlvCounter[level]++;
        }
    }

    /**
     * Returns a description of the tag class.
     *
     * @param tagClass The class of the tag (0 for Universal, 1 for Application, etc.).
     * @return A string description of the tag class.
     */
    private String getClassDescription(int tagClass) {
        return switch (tagClass) {
            case 0 -> "Universal";
            case 1 -> "Application";
            case 2 -> "Context-specific";
            case 3 -> "Private";
            default -> "Unknown";
        };
    }

    /**
     * Returns a description of the tag type.
     *
     * @param tagType The type of the tag (0 for Primitive, 1 for Constructed).
     * @return A string description of the tag type.
     */
    private String getTypeDescription(int tagType) {
        return tagType == 0 ? "Primitive" : "Constructed";
    }

    /**
     * Converts a byte array to a hexadecimal string.
     *
     * @param bytes The byte array to convert.
     * @return A hexadecimal string representation of the byte array.
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
