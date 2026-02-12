package com.senac.financeapp.dao;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class BackupService {
    // O nome do seu arquivo de banco atual
    private static final String DB_NAME = "financeapp.db";

    public static void saveBackup(File destFile) throws Exception {
        File source = new File(DB_NAME);
        if (!source.exists()) throw new Exception("Banco de dados não encontrado!");
        
        // Copia o arquivo original para o destino escolhido pelo usuário
        Files.copy(source.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public static void restoreBackup(File sourceFile) throws Exception {
        File dest = new File(DB_NAME);
        // Substitui o banco atual pelo arquivo de backup
        Files.copy(sourceFile.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
}
