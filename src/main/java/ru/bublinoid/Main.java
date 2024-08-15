package ru.bublinoid;

import ru.bublinoid.parser.TlvParser;
import ru.bublinoid.entity.TlvStructure; // Добавьте этот импорт

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Директория с файлами .hex
        String dataDirectory = "src/main/resources/data";

        // Создание экземпляра TlvParser
        TlvParser parser = new TlvParser();

        try {
            // Получение списка всех файлов в директории data с расширением .hex
            Files.list(Paths.get(dataDirectory))
                    .filter(path -> path.toString().endsWith(".hex"))
                    .forEach(path -> processFile(path, parser));
        } catch (IOException e) {
            System.err.println("Error accessing files in directory: " + e.getMessage());
        }
    }

    /**
     * Обработка одного файла: чтение, парсинг и вывод TLV-структур.
     *
     * @param path путь к файлу .hex
     * @param parser экземпляр TlvParser для обработки файла
     */
    private static void processFile(Path path, TlvParser parser) {
        System.out.println("Processing file: " + path.getFileName());

        try {
            // Чтение файла и преобразование его содержимого в массив байтов
            byte[] data = parser.readHexFile(path.toString());

            // Парсинг TLV-структур из массива байтов
            List<TlvStructure> tlvStructures = parser.parseTlv(data);

            // Вывод распарсенных TLV-структур на экран
            parser.printTlv(tlvStructures);
        } catch (IOException e) {
            System.err.println("Error reading file " + path.getFileName() + ": " + e.getMessage());
        }
    }
}
