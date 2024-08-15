package ru.bublinoid;

import ru.bublinoid.parser.TlvParser;
import ru.bublinoid.entity.TlvStructure;
import ru.bublinoid.printer.TlvPrinter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Main class for processing .hex files in a specified directory.
 * This class handles file reading, TLV parsing, and printing the results.
 */
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    /**
     * Main entry point of the application.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        String dataDirectory = "src/main/resources/data";
        TlvParser parser = new TlvParser();
        TlvPrinter printer = new TlvPrinter();
        processFilesInDirectory(dataDirectory, parser, printer);
    }

    /**
     * Processes all .hex files in the specified directory.
     *
     * @param directory the directory containing .hex files
     * @param parser    the TlvParser instance for parsing files
     * @param printer   the TlvPrinter instance for printing results
     */
    private static void processFilesInDirectory(String directory, TlvParser parser, TlvPrinter printer) {
        Path dirPath = Paths.get(directory);
        try (var stream = Files.list(dirPath)) {
            stream
                    .filter(path -> path.toString().endsWith(".hex"))
                    .forEach(path -> processFile(path, parser, printer));
        } catch (IOException e) {
            logger.error("Error accessing files in directory: {} - {}", directory, e.getMessage());
        }
    }

    /**
     * Processes a single .hex file.
     *
     * @param path    the path to the .hex file
     * @param parser  the TlvParser instance for parsing the file
     * @param printer the TlvPrinter instance for printing the results
     */
    private static void processFile(Path path, TlvParser parser, TlvPrinter printer) {
        logger.info("Processing file: {}", path.getFileName());
        try {
            byte[] data = parser.readHexFile(path.toString());
            List<TlvStructure> tlvStructures = parser.parseTlv(data);
            printer.printTlv(tlvStructures);
        } catch (IOException e) {
            logger.error("Error reading file {}: {}", path.getFileName(), e.getMessage());
        }
    }
}
