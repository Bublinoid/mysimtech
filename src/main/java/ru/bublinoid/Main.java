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
        String refDirectory = "src/main/resources/ref";

        // Создание экземпляров TlvParser и TlvPrinter
        TlvParser parser = new TlvParser();
        TlvPrinter printer = new TlvPrinter();

        // Обработка файлов с расширением .hex в директории data
        processFilesInDirectory(dataDirectory, ".hex", parser, printer);

        // Обработка файлов с расширением .out в директории ref
        processFilesInDirectory(refDirectory, ".out", parser, printer);
    }

    /**
     * Обрабатывает файлы в заданной директории с определенным расширением.
     *
     * @param directory Путь к директории
     * @param fileExtension Расширение файлов для обработки
     * @param parser Экземпляр TlvParser для обработки файла
     * @param printer Экземпляр TlvPrinter для вывода
     */
    private static void processFilesInDirectory(String directory, String fileExtension, TlvParser parser, TlvPrinter printer) {
        try {
            Files.list(Paths.get(directory))
                    .filter(path -> path.toString().endsWith(fileExtension))
                    .forEach(path -> processFile(path, parser, printer));
        } catch (IOException e) {
            System.err.println("Error accessing files in directory: " + directory + " - " + e.getMessage());
        }
    }

    /**
     * Обработка одного файла: чтение, парсинг и вывод TLV-структур.
     *
     * @param path Путь к файлу
     * @param parser Экземпляр TlvParser для обработки файла
     * @param printer Экземпляр TlvPrinter для вывода
     */
    private static void processFile(Path path, TlvParser parser, TlvPrinter printer) {
        System.out.println("Processing file: " + path.getFileName());

        try {
            // Чтение файла и преобразование его содержимого в массив байтов
            byte[] data = parser.readHexFile(path.toString());

            // Парсинг TLV-структур из массива байтов
            List<TlvStructure> tlvStructures = parser.parseTlv(data);

            // Вывод распарсенных TLV-структур на экран
            printer.printTlv(tlvStructures);
        } catch (IOException e) {
            System.err.println("Error reading file " + path.getFileName() + ": " + e.getMessage());
        }
    }
}
