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

public class FileUploadUtil {

    /**
     * Lưu file upload vào thư mục vật lý (physical path), trả về đường dẫn
     * public để truy cập từ web.
     *
     * @param part Part nhận được từ request.getPart(...)
     * @param folderPath Đường dẫn vật lý đến thư mục lưu file (ví dụ:
     * C:/Project/LightHouseCourse/src/main/webapp/assets/imgs/courses)
     * @param urlPrefix Đường dẫn public truy cập file (ví dụ:
     * /assets/imgs/courses)
     * @return Đường dẫn public truy cập file (để lưu vào DB)
     * @throws IOException
     */
    public static String saveFile(Part part, String folderPath, String urlPrefix) throws IOException {
        String fileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
        String fileExt = "";
        int dot = fileName.lastIndexOf(".");
        if (dot >= 0) {
            fileExt = fileName.substring(dot);
        }
        // Đổi tên file để tránh trùng (thêm timestamp)
        String newName = System.currentTimeMillis() + "_" + Math.abs(fileName.hashCode()) + fileExt;
        File uploadFolder = new File(folderPath);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }
        String filePath = folderPath + File.separator + newName;
        part.write(filePath);

        // Trả về đường dẫn public (dùng khi show ra web hoặc lưu DB)
        return urlPrefix + "/" + newName;
    }
}
