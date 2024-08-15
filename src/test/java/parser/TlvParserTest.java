package parser;

import org.junit.jupiter.api.Test;
import ru.bublinoid.entity.TlvStructure;
import ru.bublinoid.parser.TlvParser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TlvParserTest {

    @Test
    void testParseSingleTlv() {
        TlvParser parser = new TlvParser();
        byte[] data = {0x30, 0x07, (byte) 0xA0, 0x05, 0x30, 0x03, (byte) 0x80, 0x01, 0x00};
        List<TlvStructure> tlvStructures = parser.parseTlv(data);
        assertEquals(1, tlvStructures.size());
        // Add more assertions for tags, lengths, and values
    }

    @Test
    void testParseInvalidTlv() {
        TlvParser parser = new TlvParser();
        byte[] data = {0x30, 0x07};  // Incomplete TLV
        Exception exception = assertThrows(RuntimeException.class, () -> {
            parser.parseTlv(data);
        });
        assertTrue(exception.getMessage().contains("Error"));
    }
}
