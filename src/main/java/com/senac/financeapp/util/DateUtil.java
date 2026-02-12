package com.senac.financeapp.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Formata um LocalDate para uma string no formato "dd/MM/yyyy".
     * @param date A data a ser formatada.
     * @return A string formatada.
     */
    public static String format(LocalDate date) {
        if (date == null) {
            return null;
        }
        return DATE_FORMATTER.format(date);
    }

    /**
     * Converte uma string no formato "dd/MM/yyyy" para LocalDate.
     * @param dateString A string da data.
     * @return O LocalDate correspondente.
     * @throws DateTimeParseException Se a string não puder ser parseada.
     */
    public static LocalDate parse(String dateString) {
        try {
            return LocalDate.parse(dateString, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return null; // Ou lançar uma exceção mais específica
        }
    }

    /**
     * Valida se uma string está no formato "dd/MM/yyyy" e representa uma data válida.
     * @param dateString A string a ser validada.
     * @return true se for uma data válida, false caso contrário.
     */
    public static boolean isValidDate(String dateString) {
        return DateUtil.parse(dateString) != null;
    }
}
