package org.example.proektnupraktikum.Controller;

import lombok.RequiredArgsConstructor;
import org.example.proektnupraktikum.Entity.Resume;
import org.example.proektnupraktikum.Service.ResumeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getByStudent(@RequestParam Long studentId) {
        Optional<Resume> found = resumeService.findByStudentId(studentId);
        if (found.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Resume r = found.get();
        Map<String, Object> body = new HashMap<>();
        body.put("id", r.getId());
        body.put("fileUrl", r.getFileUrl() != null ? r.getFileUrl() : "");
        return ResponseEntity.ok(body);
    }

    @PostMapping
    public ResponseEntity<Resume> save(
            @RequestParam Long studentId,
            @RequestParam String fileUrl
    ) {
        return ResponseEntity.ok(resumeService.save(studentId, fileUrl));
    }
}