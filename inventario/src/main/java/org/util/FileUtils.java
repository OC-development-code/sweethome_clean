package org.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileUtils {

    private static final String BASE_DIR = "SweetHome";
    private static final String REPORTS_DIR = "reportes";

    public static Path getBasePath() {
        return Paths.get(System.getProperty("user.home"), "Documents", BASE_DIR);
    }

    public static Path getCategoryPath(String categoria) {
        return getBasePath().resolve(Paths.get(REPORTS_DIR, categoria.toLowerCase()));
    }

    public static Path getExcelPath(String categoria) {
        return getCategoryPath(categoria).resolve("excel");
    }

    public static Path getPdfPath(String categoria) {
        return getCategoryPath(categoria).resolve("pdf");
    }

    public static void crearDirectorios() throws IOException {
        String[] categorias = {"ventas", "inventario", "costos", "stock"};
        for (String cat : categorias) {
            Files.createDirectories(getExcelPath(cat));
            Files.createDirectories(getPdfPath(cat));
        }
    }

    public static Path asegurarDirectorio(Path path) throws IOException {
        if (!Files.exists(path)) Files.createDirectories(path);
        return path;
    }

    public static String generarNombreArchivo(String categoria, String extension) {
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return categoria.toLowerCase() + "_" + fecha + extension;
    }

    public static String obtenerFechaActual() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}