package ru.bublinoid.printer;

import ru.bublinoid.entity.TlvStructure;
import ru.bublinoid.parser.TlvParser;

import java.util.List;
import java.util.logging.Logger;

/**
 * A class for formatting and printing TLV (Tag-Length-Value) structures.
 */
public class TlvPrinter {

    private static final Logger logger = Logger.getLogger(TlvPrinter.class.getName());

    /**
     * Prints the TLV data to the console with annotations and nesting levels.
     *
     * @param tlvStructures The list of TLV structures to print.
     */
    public void printTlv(List<TlvStructure> tlvStructures) {
        printTlv(tlvStructures, 0);
    }

    /**
     * Recursively prints the TLV structures with indentation based on nesting level.
     *
     * @param tlvStructures The list of TLV structures to print.
     * @param level The current level of nesting.
     */
    private void printTlv(List<TlvStructure> tlvStructures, int level) {
        String indent = "  ".repeat(level); // Indentation for the current level

        for (TlvStructure tlv : tlvStructures) {
            String tagClass = getClassDescription(tlv.getTagClass());
            String tagType = getTypeDescription(tlv.getTagType());
            int tagId = tlv.getTagNumber();

            logger.info(String.format("%sTag (class: %s, type: %s, id: %d) [%02X]",
                    indent, tagClass, tagType, tagId, tlv.getTag()));
            logger.info(String.format("%sLength: %d",
                    indent, tlv.getLength()));
            logger.info(String.format("%sValue: %s",
                    indent, bytesToHex(tlv.getValue())));

            // If TLV is a constructed type, parse nested TLV structures
            if (tlv.getTagType() == 1) {
                TlvParser nestedParser = new TlvParser();
                List<TlvStructure> nestedTlv = nestedParser.parseTlv(tlv.getValue());
                printTlv(nestedTlv, level + 1); // Recursively print nested TLV
            }
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
