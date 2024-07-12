package co.jp.xeex.chat.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.UrlResource;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import co.jp.xeex.chat.common.AppConstant;

/**
 * FileUtil
 * 
 * @author q_thinh
 */
@SuppressWarnings({ "squid:S1118" })
public class FileUtil {
    private static final int BUFFER_SIZE = 1024;
    private static final String[] IMAGE_TYPES = { "png", "jpg", "jpeg", "svg" };

    /**
     * uploadFile to string path
     * 
     * @param uploadPath
     * @param file
     * @return
     * @throws IOException
     */
    public static String uploadFile(String uploadPath, MultipartFile file) throws IOException {
        return uploadFile(Paths.get(uploadPath), file);
    }

    /**
     * uploadFile to path
     * 
     * @param uploadPath
     * @param file
     * @return
     * @throws IOException
     */
    public static String uploadFile(Path uploadPath, MultipartFile file) throws IOException {
        // Create directory
        Files.createDirectories(uploadPath);

        // Generated store file name
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        String generateStoreName = UUID.randomUUID().toString().replace("-", StringUtils.EMPTY) + "."
                + fileExtension;
        Path destPath = uploadPath.resolve(Paths.get(generateStoreName)).normalize().toAbsolutePath();

        // Copy file to uploads folder
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destPath, StandardCopyOption.REPLACE_EXISTING);
        }

        return generateStoreName;
    }

    /**
     * readFileByteContent
     * 
     * @param path
     * @param storeName
     * @return
     * @throws IOException
     */
    public static byte[] readFileByteContent(String path, String storeName) throws IOException {
        return readFileByteContent(Paths.get(path), storeName);
    }

    /**
     * readFileByteContent
     * 
     * @param path
     * @param storeName
     * @return
     * @throws IOException
     */
    public static byte[] readFileByteContent(Path path, String storeName) throws IOException {
        // Get file
        Path file = path.resolve(storeName);

        UrlResource resource = new UrlResource(file.toUri());
        if (resource.exists() || resource.isReadable()) {
            return StreamUtils.copyToByteArray(resource.getInputStream());
        }

        return new byte[0];
    }

    /**
     * readFileStreamContent
     * 
     * @param path
     * @param storeName
     * @return
     * @throws IOException
     */
    public static StreamingResponseBody readFileStreamContent(String path, String storeName) throws IOException {
        return readFileStreamContent(Paths.get(path), storeName);
    }

    /**
     * readFileStreamContent
     * 
     * @param path
     * @param storeName
     * @return
     * @throws IOException
     */
    public static StreamingResponseBody readFileStreamContent(Path path, String storeName) throws IOException {
        // Get file
        Path file = path.resolve(storeName);
        UrlResource urlResource = new UrlResource(file.toUri());

        return outputStream -> {
            int bytesRead;
            byte[] buffer = new byte[BUFFER_SIZE];
            try (InputStream inputStream = urlResource.getInputStream()) {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        };
    }

    /**
     * copy temp file to chat/task path
     * 
     * @param rootUploadPath
     * @param targetPath
     * @param targetFileName
     * @param userName
     * @param isChat
     * @throws IOException
     */
    public static void saveTempFile(String rootUploadPath,
            String targetPath,
            String targetFileName,
            String userName,
            boolean isChat) throws IOException {

        // Get target file
        Path tempPath = Paths.get(rootUploadPath, AppConstant.PATH_TEMP_PREFIX, userName);
        tempPath = tempPath.resolve(Paths.get(targetFileName)).normalize().toAbsolutePath();

        // Setting destination file
        String requestPath = isChat ? AppConstant.PATH_CHAT_PREFIX : AppConstant.PATH_TASK_PREFIX;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        File destFile = getTargetPath(rootUploadPath, requestPath, targetPath, timestamp)
                .resolve(Paths.get(targetFileName))
                .normalize()
                .toAbsolutePath()
                .toFile();

        // Copy file to destination path
        copyFile(tempPath.toFile(), destFile);

        // Delete temp path
        Files.deleteIfExists(tempPath);
    }

    /**
     * copyFile
     * 
     * @param targetFile
     * @param destFile
     * @throws IOException
     */
    public static void copyFile(File targetFile, File destFile) throws IOException {
        Files.copy(targetFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Get target path file upload
     * 
     * @param rootUploadPath
     * @param fileClassify
     * @param targetPath
     * @param timestamp
     * @return
     */
    public static Path getTargetPath(String rootUploadPath, String fileClassify, String targetPath,
            Timestamp timestamp) {

        // Get target path file upload
        Path resultPath = Paths.get(rootUploadPath, fileClassify.toLowerCase(), targetPath);
        if (timestamp != null) {
            resultPath = Paths.get(rootUploadPath,
                    fileClassify.toLowerCase(),
                    targetPath,
                    String.valueOf(timestamp.toLocalDateTime().getYear()),
                    String.valueOf(timestamp.toLocalDateTime().getMonthValue()),
                    String.valueOf(timestamp.toLocalDateTime().getDayOfMonth()));
        }

        // Create directory if not exist
        if (!resultPath.toFile().exists()) {
            resultPath.toFile().mkdirs();
        }

        return resultPath;
    }

    /**
     * check image type file
     * 
     * @param file
     * @return
     */
    public static boolean isImage(MultipartFile file) {
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        return Arrays.asList(IMAGE_TYPES).contains(fileExtension.trim().toLowerCase());
    }

    /**
     * Delete path
     * 
     * @param targetPath
     * @throws IOException
     */
    public static void deletePath(Path targetPath) throws IOException {
        File targetFolderPath = targetPath.toFile();
        if (targetFolderPath.exists()) {
            File[] files = targetFolderPath.listFiles();
            for (File file : files) {
                Files.deleteIfExists(file.toPath());
            }

            // Delete temp path
            Files.deleteIfExists(targetPath);
        }
    }

    /**
     * Deletes a list of files from a target directory.
     *
     * @param targetPath the path of the target directory
     * @param storeFiles the list of file names to be deleted
     * @throws IOException if an I/O error occurs while deleting the files
     */
    public static void deleteListFile(Path targetPath, List<String> storeFiles) throws IOException {
        if (targetPath.toFile().exists()) {
            for (String storeFile : storeFiles) {
                Path filePath = targetPath.resolve(storeFile);
                Files.deleteIfExists(filePath);
            }
        }
    }

    /**
     * v_long:
     * Search files by name or path
     * 
     * @param rootFolder the root folder start to search
     * @param fileOrPath the file or path to search
     * @return the list of files found
     * @throws IOException if an I/O error occurs or file not found
     */
    public static List<Path> searchFilesByName(String rootFolder, String fileOrPath) throws IOException {
        Path startPath = Paths.get(rootFolder);
        List<Path> result;
        String fileNameOnly = Paths.get(fileOrPath).getFileName().toString();
        try (Stream<Path> stream = Files.walk(startPath)) {
            List<Path> paths = stream.collect(Collectors.toList());            
            result = paths.stream()
                    //.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().equals(fileNameOnly))
                    .collect(Collectors.toList());
        }
        if (result == null || result.isEmpty()) {
            throw new IOException("File not found");
        }
        return result;
    }
}
