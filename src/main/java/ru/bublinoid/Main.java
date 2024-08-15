package ru.bublinoid;

import ru.bublinoid.parser.TlvParser;
import ru.bublinoid.entity.TlvStructure;
import ru.bublinoid.printer.TlvPrinter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Определение директории и соответствующих расширений файлов
        String dataDirectory = "src/main/resources/data";

        // Создание экземпляров TlvParser и TlvPrinter
        TlvParser parser = new TlvParser();
        TlvPrinter printer = new TlvPrinter();

        // Обработка файлов с расширением .hex в директории data
        processFilesInDirectory(dataDirectory, parser, printer);
    }

    private static void processFilesInDirectory(String directory, TlvParser parser, TlvPrinter printer) {
        try {
            Files.list(Paths.get(directory))
                    .filter(path -> path.toString().endsWith(".hex"))
                    .forEach(path -> processFile(path, parser, printer));
        } catch (IOException e) {
            System.err.println("Error accessing files in directory: " + directory + " - " + e.getMessage());
        }
    }

    private static void processFile(Path path, TlvParser parser, TlvPrinter printer) {
        System.out.println("Processing file: " + path.getFileName());

        try {
            byte[] data = parser.readHexFile(path.toString());
            List<TlvStructure> tlvStructures = parser.parseTlv(data);
            printer.printTlv(tlvStructures);
        } catch (IOException e) {
            System.err.println("Error reading file " + path.getFileName() + ": " + e.getMessage());
        }
    }
}
