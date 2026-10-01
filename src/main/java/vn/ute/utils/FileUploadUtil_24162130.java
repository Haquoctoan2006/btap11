package vn.ute.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Part;

/**
 * Cau 5: luu anh upload (Category/Product) vao thu muc "/uploads" trong webapp.
 * Tra ve gia tri luu vao cot "images" dang "uploads/&lt;tenFileMoi&gt;" de JSP
 * phan biet duoc voi anh mau co san trong /assets/images/...
 */
public final class FileUploadUtil_24162130 {

    public static final String UPLOAD_FOLDER = "uploads";

    private FileUploadUtil_24162130() {
    }

    /**
     * @param part      Part lay tu request.getPart("images"), co the null hoac rong (khong chon file)
     * @param context   ServletContext de lay duong dan that cua webapp
     * @return duong dan tuong doi ("uploads/xxx.jpg") de luu vao DB, hoac null neu khong co file nao duoc chon
     */
    public static String saveIfPresent(Part part, ServletContext context) throws IOException {
        if (part == null || part.getSize() <= 0) {
            return null;
        }
        String originalName = extractFileName(part);
        if (originalName == null || originalName.isBlank()) {
            return null;
        }

        String ext = "";
        int dot = originalName.lastIndexOf('.');
        if (dot >= 0) {
            ext = originalName.substring(dot);
        }
        String newFileName = UUID.randomUUID() + ext;

        String realPath = context.getRealPath("/" + UPLOAD_FOLDER);
        if (realPath == null) {
            // Fallback khi realPath khong ton tai (war chua giai nen): tao canh thu muc lam viec
            realPath = UPLOAD_FOLDER;
        }
        Path uploadDir = Paths.get(realPath);
        Files.createDirectories(uploadDir);

        Path target = uploadDir.resolve(newFileName);
        try (var in = part.getInputStream()) {
            Files.copy(in, target);
        }

        return UPLOAD_FOLDER + "/" + newFileName;
    }

    private static String extractFileName(Part part) {
        String header = part.getHeader("content-disposition");
        if (header == null) {
            return null;
        }
        for (String token : header.split(";")) {
            token = token.trim();
            if (token.startsWith("filename")) {
                String fileName = token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
                // Trinh duyet cu co the gui full path (C:\...\ten.jpg) -> chi lay ten file
                int slash = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
                return slash >= 0 ? fileName.substring(slash + 1) : fileName;
            }
        }
        return null;
    }
}
