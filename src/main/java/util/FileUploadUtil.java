/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author Pham Quoc Tu - CE181513
 */
package util;

import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public class FileUploadUtil {

    /**
     * Lưu file upload vào folder vật lý, trả về path public để lưu DB (không
     * dấu / đầu)
     *
     * @param part Part từ request (file upload)
     * @param folderPath Đường dẫn vật lý đến folder lưu file (ví dụ:
     * /app/web/assets/videos)
     * @param urlPrefix Đường dẫn public tới file (ví dụ: assets/videos)
     * @return Đường dẫn public tới file, KHÔNG có dấu '/' đầu
     * @throws IOException
     */
    public static String saveFile(Part part, String folderPath, String urlPrefix) throws IOException {
        String fileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
        String fileExt = "";
        int dot = fileName.lastIndexOf(".");
        if (dot >= 0) {
            fileExt = fileName.substring(dot);
        }
        // Đổi tên để tránh trùng (timestamp + hash)
        String newName = System.currentTimeMillis() + "_" + Math.abs(fileName.hashCode()) + fileExt;
        File uploadFolder = new File(folderPath);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }
        String filePath = folderPath + File.separator + newName;
        part.write(filePath);

        // Đảm bảo không trả về dấu / đầu
        String publicPath = urlPrefix.endsWith("/") ? urlPrefix + newName : urlPrefix + "/" + newName;
        if (publicPath.startsWith("/")) {
            publicPath = publicPath.substring(1);
        }
        return publicPath;
    }

    /**
     * Kiểm tra & upload file duy nhất, trả về path public (nếu lỗi trả về null,
     * ghi lỗi vào map errors)
     *
     * @param part
     * @param folderPath
     * @param urlPrefix
     * @param maxSize
     * @param acceptExtensions
     * @param errors
     * @param errorKey
     * @param required
     * @return
     * @throws java.io.IOException
     */
    public static String handleUpload(
            Part part,
            String folderPath,
            String urlPrefix,
            long maxSize,
            String[] acceptExtensions,
            Map<String, String> errors,
            String errorKey,
            boolean required
    ) throws IOException {
        if (part == null || part.getSize() == 0) {
            if (required) {
                errors.put(errorKey, "File is required.");
            }
            return null;
        }
        String fileName = part.getSubmittedFileName().toLowerCase();
        boolean validExt = false;
        for (String ext : acceptExtensions) {
            if (fileName.endsWith(ext)) {
                validExt = true;
                break;
            }
        }
        if (!validExt) {
            errors.put(errorKey, "File must be " + String.join(", ", acceptExtensions));
            return null;
        }
        if (part.getSize() > maxSize) {
            errors.put(errorKey, "File is too large (max " + (maxSize / (1024 * 1024)) + "MB).");
            return null;
        }
        // Lưu file, trả về path public KHÔNG có dấu / đầu
        return saveFile(part, folderPath, urlPrefix);
    }
}
