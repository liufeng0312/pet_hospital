package com.pethospital.controller;

import com.pethospital.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Api(tags = "文件上传")
@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Value("${file.upload-path:uploads}")
    private String uploadPath;

    @ApiOperation("上传图片")
    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        String newFilename = UUID.randomUUID().toString() + suffix;

        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            File dest = new File(dir.getAbsolutePath() + File.separator + newFilename);
            file.transferTo(dest);
            log.info("文件上传成功: {}", dest.getAbsolutePath());
            return Result.success("/uploads/" + newFilename);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    @ApiOperation("上传头像")
    @PostMapping("/avatar")
    public Result<java.util.Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("只能上传图片文件");
        }

        // 验证文件大小 (2MB)
        if (file.getSize() > 2 * 1024 * 1024) {
            return Result.error("图片大小不能超过2MB");
        }

        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        String newFilename = "avatar_" + UUID.randomUUID().toString() + suffix;

        File dir = new File(uploadPath + File.separator + "avatars");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            File dest = new File(dir.getAbsolutePath() + File.separator + newFilename);
            file.transferTo(dest);
            log.info("头像上传成功: {}", dest.getAbsolutePath());
            
            // 返回包含url的Map
            java.util.Map<String, String> data = new java.util.HashMap<>();
            data.put("url", "/uploads/avatars/" + newFilename);
            return Result.success(data);
        } catch (IOException e) {
            log.error("头像上传失败", e);
            return Result.error("头像上传失败: " + e.getMessage());
        }
    }
}
