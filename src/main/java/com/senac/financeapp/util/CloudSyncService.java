package com.senac.financeapp.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;

public class CloudSyncService {

    private static final String DB_NAME = "financeapp.db";

    public static void sync(File cloudFolder) throws Exception {
        File localDb = new File(DB_NAME);
        File cloudDb = new File(cloudFolder, "financeapp_sync.db");

        // Se o banco local não existir (erro raro), para aqui
        if (!localDb.exists()) {
            throw new Exception("Banco de dados local não encontrado.");
        }

        // Caso 1: A nuvem está vazia (primeira sincronização)
        if (!cloudDb.exists()) {
            Files.copy(localDb.toPath(), cloudDb.toPath());
            return;
        }

        // Caso 2: Comparação de metadados (Data de Modificação)
        FileTime localTime = Files.getLastModifiedTime(localDb.toPath());
        FileTime cloudTime = Files.getLastModifiedTime(cloudDb.toPath());

        // Se o local for MAIS NOVO que a nuvem
        if (localTime.compareTo(cloudTime) > 0) {
            Files.copy(localDb.toPath(), cloudDb.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } 
        // Se a nuvem for MAIS NOVA que o local
        else if (cloudTime.compareTo(localTime) > 0) {
            Files.copy(cloudDb.toPath(), localDb.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        // Se forem iguais, não faz nada (economiza processamento)
    }
}