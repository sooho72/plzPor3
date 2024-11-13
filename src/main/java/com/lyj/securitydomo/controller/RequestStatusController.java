
package com.lyj.securitydomo.controller;

import com.lyj.securitydomo.domain.RequestStatus;
import com.lyj.securitydomo.service.RequestStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/request-status")
@RequiredArgsConstructor
public class RequestStatusController {
    private final RequestStatusService requestStatusService;
    // 모든 상태 조회
    @GetMapping
    public ResponseEntity<List<RequestStatus>> getAllStatuses() {
        List<RequestStatus> statuses = requestStatusService.findAll();
        return ResponseEntity.ok(statuses);
    }

    // 특정 상태 조회
    @GetMapping("/{id}")
    public ResponseEntity<RequestStatus> getStatusById(@PathVariable Long id) {
        return requestStatusService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 상태 생성
    @PostMapping
    public ResponseEntity<RequestStatus> createStatus(@RequestBody RequestStatus requestStatus) {
        RequestStatus createdStatus = requestStatusService.create(requestStatus);
        return ResponseEntity.ok(createdStatus);
    }

    // 상태 업데이트
    @PutMapping("/{id}")
    public ResponseEntity<RequestStatus> updateStatus(@PathVariable Long id, @RequestBody RequestStatus requestStatus) {
        RequestStatus updatedStatus = requestStatusService.update(id, requestStatus);
        return ResponseEntity.ok(updatedStatus);
    }

    // 상태 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatus(@PathVariable Long id) {
        requestStatusService.delete(id);
        return ResponseEntity.noContent().build();
    }
}