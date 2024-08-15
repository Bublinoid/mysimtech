package ru.bublinoid.utls;

import java.util.Arrays;

public class HashUtils {

    /**
     * Генерирует хэш-код для массива байтов.
     *
     * @param tag    идентификатор тега
     * @param length длина значения
     * @param value  значение в виде массива байтов
     * @return хэш-код
     */
    public static int generateHash(int tag, int length, byte[] value) {
        int result = Integer.hashCode(tag);
        result = 31 * result + Integer.hashCode(length);
        result = 31 * result + Arrays.hashCode(value);
        return result;
    }
}
