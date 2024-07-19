package com.ruoyi.main.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.main.vo.UrlAndPathVo;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system/zip")
public class FileUploadController {

    @Value("${ruoyi.profile}") // 从配置文件中读取上传目录
    private String UPLOAD_DIR;

    @PostMapping("/uploads")
    public AjaxResult uploadFiles(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return AjaxResult.error("Please select a file to upload");
        }
        try {
            // Save the uploaded file to the upload directory
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Extract files from the archive (RAR or ZIP)
            List<UrlAndPathVo> voList = new ArrayList<>();
            List<String> extractedFiles = extractArchive(filePath.toFile());
            extractedFiles.stream().forEach(a->{
                UrlAndPathVo urlAndPathVo = new UrlAndPathVo();
                urlAndPathVo.setSvsPath(a);
                String result = a.substring(UPLOAD_DIR.length());
                System.out.println("result = " + result);
                urlAndPathVo.setSvs(picUrl(result));
                System.out.println("urlAndPathVo = " + urlAndPathVo.getSvs());
                voList.add(urlAndPathVo);
            });
            Files.deleteIfExists(filePath);
            return AjaxResult.success("Files uploaded and extracted successfully", voList);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("Failed to upload and extract files");
        }
    }
    public String picUrl(String json){
        String save = "http://192.168.0.98:8091/profile/"+json;
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
            String upload_dir = UPLOAD_DIR+"/"+System.currentTimeMillis();
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
