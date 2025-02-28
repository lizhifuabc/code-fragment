package io.github.lizhifuabc.minio.controller;

import io.github.lizhifuabc.minio.dto.ChunkListRequest;
import io.github.lizhifuabc.minio.dto.ChunkMergeRequest;
import io.github.lizhifuabc.minio.service.AdvancedUploadService;
import io.minio.ObjectWriteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 高级文件上传控制器
 * 支持秒传、断点续传、分片上传
 *
 * @author lizhifu
 * @since 2024/2/24
 */
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class AdvancedUploadController {
    private final AdvancedUploadService advancedUploadService;

    /**
     * 尝试秒传
     */
    @PostMapping("/tryFast")
    public ResponseEntity<Long> tryFastUpload(@RequestParam("md5") String md5,
                                            @RequestParam("fileName") String fileName) {
        Long fileMd5Id = advancedUploadService.tryFastUpload(md5, fileName);
        return ResponseEntity.ok(fileMd5Id);
    }

    /**
     * 上传分片
     */
    @PostMapping("/chunk")
    public ResponseEntity<Void> uploadChunk(@RequestParam("chunk") MultipartFile chunk,
                                          @RequestParam("chunkNumber") int chunkNumber,
                                          @RequestParam("totalChunks") int totalChunks,
                                          @RequestParam("fileMd5Id") Long fileMd5Id,
                                          @RequestParam(required = false) String bucketName) {
        advancedUploadService.uploadChunk(chunk, chunkNumber, totalChunks, fileMd5Id, bucketName);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取已上传的分片列表
     */
    @PostMapping("/chunks")
    public ResponseEntity<List<Integer>> getUploadedChunks(@RequestBody ChunkListRequest request) {
        List<Integer> chunks = advancedUploadService.getUploadedChunks(request.getFileMd5Id(), request.getTotalChunks());
        return ResponseEntity.ok(chunks);
    }

    /**
     * 合并分片
     */
    @PostMapping("/merge")
    public ResponseEntity<Void> mergeChunks(@RequestBody ChunkMergeRequest request) {
        ObjectWriteResponse response = advancedUploadService.mergeChunks(
            request.getFileMd5Id(),
            request.getFileName(),
            request.getTotalChunks(),
            request.getBucketName());
        return ResponseEntity.ok().build();
    }
}