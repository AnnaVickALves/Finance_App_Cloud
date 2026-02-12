package com.senac.financeapp.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class NumberUtil {

    private static final Locale BRAZIL_LOCALE = Locale.of("pt", "BR");
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(BRAZIL_LOCALE);
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance(BRAZIL_LOCALE);

    public static String formatCurrency(BigDecimal value) {
        if (value == null) {
            return CURRENCY_FORMAT.format(BigDecimal.ZERO);
        }
        return CURRENCY_FORMAT.format(value);
    }

    public static BigDecimal parseCurrency(String text) throws ParseException {
        if (text == null || text.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        String cleanedText = text.trim();

        // 1. Tenta parsear diretamente com o formato de moeda (ex: R$ 1.000,01)
        try {
            Number number = CURRENCY_FORMAT.parse(cleanedText);
            return new BigDecimal(number.toString());
        } catch (ParseException e) {
            // Ignora e tenta a próxima abordagem
        }

        // 2. Tenta parsear com o formato de número (ex: 1.000,01 sem o R$)
        try {
            Number number = NUMBER_FORMAT.parse(cleanedText);
            return new BigDecimal(number.toString());
        } catch (ParseException e) {
            // Ignora e tenta a próxima abordagem
        }

        // 3. Abordagem mais agressiva: remove o símbolo da moeda (se presente) e tenta parsear como número.
        // Isso é útil se o usuário digitou "R$1000,01" ou "R$ 1000,01"
        String currencySymbol = CURRENCY_FORMAT.getCurrency().getSymbol(BRAZIL_LOCALE);
        if (cleanedText.startsWith(currencySymbol)) {
            cleanedText = cleanedText.substring(currencySymbol.length()).trim();
            try {
                Number number = NUMBER_FORMAT.parse(cleanedText);
                return new BigDecimal(number.toString());
            } catch (ParseException e) {
                // Ignora e tenta a próxima abordagem
            }
        }

        // 4. Última tentativa: padroniza a string para o formato de BigDecimal (ponto como decimal)
        // Isso é para casos onde o NumberFormat falha por alguma razão, mas a string é numericamente válida.
        // Ex: "1.000,01" -> "1000.01"
        // Ex: "1000,01" -> "1000.01"
        // Ex: "1000.01" -> "1000.01"
        String standardizedText = cleanedText;
        if (standardizedText.contains(",") && standardizedText.contains(".")) {
            // Se tem ambos, assume que o ponto é separador de milhar e a vírgula é decimal (padrão BR)
            standardizedText = standardizedText.replace(".", ""); // Remove separador de milhar
            standardizedText = standardizedText.replace(",", "."); // Troca vírgula por ponto decimal
        } else if (standardizedText.contains(",")) {
            // Se tem só vírgula, assume que é decimal
            standardizedText = standardizedText.replace(",", ".");
        }
        // Se tem só ponto, assume que é decimal (padrão EUA/BigDecimal) ou milhar (padrão BR sem decimal)
        // Deixa como está, BigDecimal(String) lida bem com "1000.01" ou "1000"

        try {
            return new BigDecimal(standardizedText);
        } catch (NumberFormatException e) {
            // Se tudo falhar, lança a exceção original ou uma nova
            throw new ParseException("Não foi possível converter o texto '" + text + "' para um valor numérico válido. Erro: " + e.getMessage(), 0);
        }
    }
}


