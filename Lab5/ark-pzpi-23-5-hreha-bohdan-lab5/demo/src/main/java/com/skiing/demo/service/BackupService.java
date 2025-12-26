package com.skiing.demo.service;

import com.skiing.demo.DTOs.ApiResponse;
import com.skiing.demo.DTOs.db.BackupInfo;
import com.skiing.demo.DTOs.db.CreateBackupResponse;
import com.skiing.demo.service.interfaces.IBackupService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class BackupService implements IBackupService {

    @Value("${backup.dir}")
    private String backupDir;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${postgres.bin-path}")
    private String postgresBinPath;

    @Override
    public ApiResponse<List<BackupInfo>> getAllBackups() {
        try {
            Path backupPath = Path.of(backupDir);

            if (!Files.exists(backupPath)) {
                return new ApiResponse<>(true, "No backups found", List.of());
            }

            List<BackupInfo> backups = Files.list(backupPath)
                    .filter(Files::isRegularFile) // только файлы
                    .filter(f -> f.getFileName().toString().endsWith(".dump"))
                    .map(f -> {
                        try {
                            double sizeMb = Files.size(f) / (1024.0 * 1024.0);// размер в МБ
                            String filename = f.getFileName().toString();
                            LocalDateTime createdAt = LocalDateTime.ofInstant(
                                    Files.getLastModifiedTime(f).toInstant(),
                                    ZoneId.systemDefault()
                            );
                            return new BackupInfo(filename, String.format("%.2f MB", sizeMb), createdAt);
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(BackupInfo::createdAt).reversed())
                    .toList();

            return new ApiResponse<>(true, "Backups retrieved", backups);

        } catch (Exception e) {
            return new ApiResponse<>(false, "Error reading backups: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<CreateBackupResponse> createBackup() {
        try {
            Path backupPath = Path.of(backupDir);
            if (!Files.exists(backupPath)) {
                Files.createDirectories(backupPath);
            }

            String filename = "backup_" + LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm")) + ".dump";
            String filePath = backupDir + "/" + filename;

            createBackupDocker(filePath, filename);

            long size = Files.size(Path.of(filePath)) / 1024;
            return new ApiResponse<>(true, "Backup created",
                    new CreateBackupResponse(filename, backupDir, size));

        } catch (Exception e) {
            return new ApiResponse<>(false, "Error creating backup: " + e.getMessage(), null);
        }
    }

    private void createBackupDocker(String filePath, String filename) throws Exception {
        String dbName = extractDbName(dbUrl);
        String host = "postgres";
        String port = "5432";

        ProcessBuilder pb = new ProcessBuilder(
                "pg_dump",
                "-Fc",
                "-h", host,
                "-p", port,
                "-U", dbUser,
                "-f", filePath,
                dbName
        );

        pb.environment().put("PGPASSWORD", dbPassword);

        Process process = pb.start();

        BufferedReader errorReader = new BufferedReader(
                new InputStreamReader(process.getErrorStream())
        );
        String errorLine;
        StringBuilder errorOutput = new StringBuilder();
        while ((errorLine = errorReader.readLine()) != null) {
            errorOutput.append(errorLine).append("\n");
        }

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Backup failed: " + errorOutput.toString());
        }
    }

    private void createBackupLocal(String filePath) throws Exception {
        String dbName = extractDbName(dbUrl);
        String host = extractHost(dbUrl);
        String port = extractPort(dbUrl);

        ProcessBuilder pb = new ProcessBuilder(
                "pg_dump",
                "-Fc",
                "-h", host,
                "-p", port,
                "-U", dbUser,
                "-f", filePath,
                dbName
        );
        pb.environment().put("PGPASSWORD", dbPassword);

        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Local backup failed");
        }
    }

    @Override
    public ApiResponse<BackupInfo> deleteBackup(String filename) {
        try {
            Path filePath = Path.of(backupDir, filename);

            if (!Files.exists(filePath)) {
                return new ApiResponse<>(false, "Backup file not found: " + filename, null);
            }

            // Получаем информацию о файле до удаления
            double sizeMb = Files.size(filePath) / (1024.0 * 1024.0);
            String sizeStr = String.format("%.2f MB", sizeMb);
            LocalDateTime createdAt = LocalDateTime.ofInstant(
                    Files.getLastModifiedTime(filePath).toInstant(),
                    ZoneId.systemDefault()
            );

            BackupInfo backupInfo = new BackupInfo(filename, sizeStr, createdAt);

            // Удаляем файл
            Files.delete(filePath);

            return new ApiResponse<>(true, "Backup deleted: " + filename, backupInfo);

        } catch (Exception e) {
            return new ApiResponse<>(false, "Error deleting backup: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<String> restoreBackup(String filename) {
        try {
            Path filePath = Path.of(backupDir, filename);

            if (!Files.exists(filePath)) {
                return new ApiResponse<>(false, "Backup file not found: " + filename, null);
            }

            if (!filename.endsWith(".dump")) {
                return new ApiResponse<>(false, "Invalid backup file format", null);
            }

            String dbName = extractDbName(dbUrl);

            ProcessBuilder pb = new ProcessBuilder(
                    pgTool("pg_restore"),
                    "-Fc",
                    "-d", dbName,
                    "-U", dbUser,
                    "-c",
                    "--if-exists",
                    filePath.toString()
            );

            pb.environment().put("PGPASSWORD", dbPassword);

            Process process = pb.start();

            String errorOutput = new String(process.getErrorStream().readAllBytes());

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                String errorMsg = "Restore failed with exit code: " + exitCode;
                if (!errorOutput.isEmpty()) {
                    errorMsg += "\nError: " + errorOutput;
                }
                return new ApiResponse<>(false, errorMsg, null);
            }

            return new ApiResponse<>(true, "Database restored successfully from: " + filename, filename);

        } catch (Exception e) {
            return new ApiResponse<>(false, "Error restoring backup: " + e.getMessage(), null);
        }
    }

    private String extractDbName(String dbUrl) {
        if (dbUrl == null || !dbUrl.contains("/")) {
            throw new IllegalArgumentException("Invalid database URL: " + dbUrl);
        }
        return dbUrl.substring(dbUrl.lastIndexOf("/") + 1);
    }

    private String extractHost(String jdbcUrl) {
        String url = jdbcUrl.replace("jdbc:postgresql://", "");
        return url.split(":")[0];
    }

    private String extractPort(String jdbcUrl) {

        String url = jdbcUrl.replace("jdbc:postgresql://", "");
        String[] parts = url.split(":");
        return parts.length > 1 ? parts[1].split("/")[0] : "5432";
    }

    private String pgTool(String tool) {
        return Path.of(postgresBinPath, tool + ".exe").toString();
    }
}
