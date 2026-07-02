package com.ftip.ftip.controller;
import com.ftip.ftip.dto.WebhookPayloadRequest;
import com.ftip.ftip.service.WebhookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor

public class WebhookController {
    private final WebhookService webhookService;
    @PostMapping("/ci")
    public ResponseEntity<String>receivedCiresults(@Valid @RequestBody WebhookPayloadRequest payload)
    {
        webhookService.processWebhook(payload);
        return ResponseEntity.ok("Test results processed successfully");
    }
}
