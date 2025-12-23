package com.skiing.demo.service;

import com.skiing.demo.DTOs.ApiResponse;
import com.skiing.demo.DTOs.db.BackupInfo;
import com.skiing.demo.DTOs.db.CreateBackupResponse;
import com.skiing.demo.service.interfaces.IBackupService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
                    .sorted(Comparator.comparing(BackupInfo::createdAt).reversed()) // новые сверху
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

            ProcessBuilder pb = new ProcessBuilder(
                    pgTool("pg_dump"),
                    "-Fc",
                    "-U", dbUser,
                    "-f", filePath,
                    extractDbName(dbUrl)
            );
            pb.environment().put("PGPASSWORD", dbPassword);

            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException("Backup failed");
            }

            long size = Files.size(Path.of(filePath)) / 1024;
            return new ApiResponse<>(true, "Backup created", new CreateBackupResponse(filename, backupDir , size));

        } catch (Exception e) {
            return new ApiResponse<>(false, "Error creating backup: " + e.getMessage(), null);
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

            // Проверяем существование файла
            if (!Files.exists(filePath)) {
                return new ApiResponse<>(false, "Backup file not found: " + filename, null);
            }

            // Проверяем расширение файла
            if (!filename.endsWith(".dump")) {
                return new ApiResponse<>(false, "Invalid backup file format", null);
            }

            // Извлекаем имя базы данных из URL
            String dbName = extractDbName(dbUrl);

            // Используем pg_restore для восстановления
            ProcessBuilder pb = new ProcessBuilder(
                    pgTool("pg_restore"),
                    "-Fc",                    // Формат custom
                    "-d", dbName,             // База данных
                    "-U", dbUser,             // Пользователь
                    "-c",                     // Очистить БД перед восстановлением
                    "--if-exists",            // Игнорировать ошибки если объекты не существуют
                    filePath.toString()       // Путь к файлу бекапа
            );

            // Устанавливаем переменную окружения для пароля
            pb.environment().put("PGPASSWORD", dbPassword);

            // Запускаем процесс
            Process process = pb.start();

            // Читаем ошибки если они есть
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

    private String pgTool(String tool) {
        return Path.of(postgresBinPath, tool + ".exe").toString();
    }
}
