package com.ruoyi.main.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.main.vo.UrlAndPathVo;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/zip/zip")
public class ZipUploadController {

    @Value("${ruoyi.profile}") // 从配置文件中读取上传目录
    private String UPLOAD_DIR;

    // 线程池，根据需求调整线程数量
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    @PostMapping("/uploads")
    public CompletableFuture<AjaxResult> uploadFilesAsync(@RequestParam("file") MultipartFile file) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (file.isEmpty()) {
                    return AjaxResult.error("Please select a file to upload");
                }

                // Save the uploaded file to the upload directory
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(file.getOriginalFilename());
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Start asynchronous extraction
                CompletableFuture<List<UrlAndPathVo>> extractionFuture = CompletableFuture.supplyAsync(() -> {
                    try {
                        return extractArchive(filePath.toFile());
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to extract files", e);
                    }
                }, executorService).thenApplyAsync(this::processExtractedFiles, executorService);

                // Delete the uploaded file after extraction completes
                extractionFuture.thenAcceptAsync(voList -> {
                    try {
                        Files.deleteIfExists(filePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }, executorService);

                // Return success response with async extraction result
                return AjaxResult.success("File uploaded. Extraction in progress.", extractionFuture.get());
            } catch (Exception e) {
                e.printStackTrace();
                return AjaxResult.error("Failed to upload and extract files");
            }
        }, executorService);
    }


    private List<UrlAndPathVo> processExtractedFiles(List<String> extractedFiles) {
        List<UrlAndPathVo> voList = new ArrayList<>();
        extractedFiles.forEach(a -> {
            UrlAndPathVo urlAndPathVo = new UrlAndPathVo();
            urlAndPathVo.setSvsPath(a);
            String result = a.substring(UPLOAD_DIR.length());
            urlAndPathVo.setSvs(picUrl(result));
            voList.add(urlAndPathVo);
        });
        return voList;
    }

    public String picUrl(String json) {
        String save = "http://192.168.0.98:8091/profile/" + json;
        return save;
    }

    private String cleanFileName(String fileName) {
        // Replace any invalid characters with underscore '_'
        return fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
    }

    private List<String> extractArchive(File archiveFile) throws IOException {
        List<String> extractedFiles = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(archiveFile);
             BufferedInputStream bis = new BufferedInputStream(fis);
             ArchiveInputStream ais = new ArchiveStreamFactory().createArchiveInputStream(bis)) {

            ArchiveEntry entry;
            String upload_dir = UPLOAD_DIR + "/" + System.currentTimeMillis();
            while ((entry = ais.getNextEntry()) != null) {
                if (!ais.canReadEntryData(entry)) {
                    continue;
                }

                String entryFileName = cleanFileName(entry.getName());
                Path entryPath = Paths.get(upload_dir, entryFileName);

                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Path parent = entryPath.getParent();
                    if (parent != null && !Files.exists(parent)) {
                        Files.createDirectories(parent);
                    }
                    try (OutputStream os = Files.newOutputStream(entryPath)) {
                        IOUtils.copy(ais, os);
                    }
                }

                // Add the extracted file path to the list
                extractedFiles.add(entryPath.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Error extracting archive", e);
        }

        return extractedFiles;
    }

}

