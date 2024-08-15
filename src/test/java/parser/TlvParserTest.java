package parser;

import org.junit.jupiter.api.Test;
import ru.bublinoid.entity.TlvStructure;
import ru.bublinoid.parser.TlvParser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TlvParserTest {

    @Test
    public void testParseT0Hex() {
        String fileContent = "3007A0053003800100" +
                "300FA00B300980010781010D8301098100";
        TlvParser parser = new TlvParser();

        // Чтение данных из строки (эмуляция содержимого файла)
        byte[] data = parser.hexStringToByteArray(fileContent);
        List<TlvStructure> tlvStructures = parser.parseTlv(data);

        // Проверка структуры TLV
        assertEquals(2, tlvStructures.size());

        // Проверка первого TLV
        TlvStructure tlv1 = tlvStructures.get(0);
        assertEquals(0x30, tlv1.getTag() & 0xFF);
        assertEquals(7, tlv1.getLength());

        List<TlvStructure> nestedTlv1 = parser.parseTlv(tlv1.getValue());
        assertEquals(1, nestedTlv1.size());

        TlvStructure tlv1_1 = nestedTlv1.get(0);
        assertEquals(0xA0, tlv1_1.getTag() & 0xFF);
        assertEquals(5, tlv1_1.getLength());

        List<TlvStructure> nestedTlv1_1 = parser.parseTlv(tlv1_1.getValue());
        assertEquals(1, nestedTlv1_1.size());

        TlvStructure tlv1_1_1 = nestedTlv1_1.get(0);
        assertEquals(0x30, tlv1_1_1.getTag() & 0xFF);
        assertEquals(3, tlv1_1_1.getLength());

        List<TlvStructure> nestedTlv1_1_1 = parser.parseTlv(tlv1_1_1.getValue());
        assertEquals(1, nestedTlv1_1_1.size());

        TlvStructure tlv1_1_1_1 = nestedTlv1_1_1.get(0);
        assertEquals(0x80, tlv1_1_1_1.getTag() & 0xFF);
        assertEquals(1, tlv1_1_1_1.getLength());
        assertEquals("00", bytesToHex(tlv1_1_1_1.getValue()));

        // Проверка второго TLV
        TlvStructure tlv2 = tlvStructures.get(1);
        assertEquals(0x30, tlv2.getTag() & 0xFF);
        assertEquals(15, tlv2.getLength());

        List<TlvStructure> nestedTlv2 = parser.parseTlv(tlv2.getValue());
        assertEquals(2, nestedTlv2.size());

        TlvStructure tlv2_1 = nestedTlv2.get(0);
        assertEquals(0xA0, tlv2_1.getTag() & 0xFF);
        assertEquals(11, tlv2_1.getLength());

        List<TlvStructure> nestedTlv2_1 = parser.parseTlv(tlv2_1.getValue());
        assertEquals(1, nestedTlv2_1.size());

        TlvStructure tlv2_1_1 = nestedTlv2_1.get(0);
        assertEquals(0x30, tlv2_1_1.getTag() & 0xFF);
        assertEquals(9, tlv2_1_1.getLength());

        List<TlvStructure> nestedTlv2_1_1 = parser.parseTlv(tlv2_1_1.getValue());
        assertEquals(3, nestedTlv2_1_1.size());

        TlvStructure tlv2_1_1_1 = nestedTlv2_1_1.get(0);
        assertEquals(0x80, tlv2_1_1_1.getTag() & 0xFF);
        assertEquals(1, tlv2_1_1_1.getLength());
        assertEquals("07", bytesToHex(tlv2_1_1_1.getValue()));

        TlvStructure tlv2_1_1_2 = nestedTlv2_1_1.get(1);
        assertEquals(0x81, tlv2_1_1_2.getTag() & 0xFF);
        assertEquals(1, tlv2_1_1_2.getLength());
        assertEquals("0D", bytesToHex(tlv2_1_1_2.getValue()));

        TlvStructure tlv2_1_1_3 = nestedTlv2_1_1.get(2);
        assertEquals(0x83, tlv2_1_1_3.getTag() & 0xFF);
        assertEquals(1, tlv2_1_1_3.getLength());
        assertEquals("09", bytesToHex(tlv2_1_1_3.getValue()));

        // Проверка пустого TLV
        TlvStructure tlv2_2 = nestedTlv2.get(1);
        assertEquals(0x81, tlv2_2.getTag() & 0xFF);
        assertEquals(0, tlv2_2.getLength());
        assertEquals(0, tlv2_2.getValue().length);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
