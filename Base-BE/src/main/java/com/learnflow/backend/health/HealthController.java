package com.learnflow.backend.health;

import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/health")
public class HealthController {
    @GetMapping public Map<String, String> health() { return Map.of("status", "UP", "service", "ckd-screening-backend"); }
}
