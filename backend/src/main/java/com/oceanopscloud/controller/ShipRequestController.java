package com.oceanopscloud.controller;

import com.oceanopscloud.enums.ShipRequestStatus;
import com.oceanopscloud.model.ShipRequest;
import com.oceanopscloud.model.ShipRequestAttachment;
import com.oceanopscloud.service.AiService;
import com.oceanopscloud.service.FileService;
import com.oceanopscloud.service.ShipRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/ship-requests")
@RequiredArgsConstructor
public class ShipRequestController {

    private final ShipRequestService service;
    private final FileService fileService; // <-- ADD THIS
    private final AiService aiService;

    @PostMapping("/create")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ShipRequest create(@RequestBody ShipRequest req) {
        return service.create(req);
    }

    @GetMapping("/all")
    public List<ShipRequest> getAll() {
        return service.getAll();
    }

    @PutMapping("/{id}/status/{status}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ShipRequest updateStatus(@PathVariable Long id, @PathVariable ShipRequestStatus status) {
        return service.updateStatus(id, status);
    }

    @GetMapping(value = "/{id}/ai", produces = "application/json")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> aiAnalyze(@PathVariable Long id) {
        ShipRequest req = service.getById(id);
        Map<String, Object> result = aiService.analyzeRequest(req);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/upload")
    public ResponseEntity<String> uploadFile(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            ShipRequest req = service.getById(id);

            String path = fileService.saveFile(file);

            ShipRequestAttachment att = new ShipRequestAttachment();
            att.setShipRequest(req);
            att.setFileName(file.getOriginalFilename());
            att.setFileType(file.getContentType());
            att.setFilePath(path);

            if (req.getAttachments() == null) {
                req.setAttachments(new ArrayList<>());
            }

            req.getAttachments().add(att);
            service.save(req);

            return ResponseEntity.ok("Uploaded: " + path);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<ShipRequest> getById(@PathVariable Long id) {

        ShipRequest request = service.getById(id);

        return ResponseEntity.ok(request);
    }
    @GetMapping("/download/{attachmentId}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable Long attachmentId) {

        try {

            ShipRequestAttachment attachment =
                    service.getAttachment(attachmentId);

            Path file = Paths.get(attachment.getFilePath());

            Resource resource = new UrlResource(file.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + attachment.getFileName() + "\""
                    )
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    }

