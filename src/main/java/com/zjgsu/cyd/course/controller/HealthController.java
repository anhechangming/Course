//package com.zjgsu.cyd.course.controller;
//




//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/health")
//public class HealthController {
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    @GetMapping("/db")
//    public ResponseEntity<String> checkDbHealth(@RequestParam(required = false, defaultValue = "false") boolean fail) {
//        try {
//            if (fail) {
//                // 模拟 SQL 错误（比 throw 更贴近真实情况）
//                jdbcTemplate.queryForObject("SELECT * FROM nonexistent_table", Integer.class);
//            } else {
//                jdbcTemplate.queryForObject("SELECT 1", Integer.class);
//            }
//            return ResponseEntity.ok("Database health check passed (DB: H2)");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
//                    .body("Database health check failed: " + e.getMessage());
//        }
//    }
//}


