package jsm.security.security_01.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("app/security")
public class MySecurityController {

    @GetMapping("/greetings")
    public ResponseEntity<String> greetings() {
        return ResponseEntity.ok("Welcome!!");
    }

    @GetMapping("/protected01")
    public ResponseEntity<String> protected01() {
        return ResponseEntity.ok("Protected resource: 01!!");
    }

    @GetMapping("/protected02")
    public ResponseEntity<String> protected02() {
        return ResponseEntity.ok("Protected resource: 02!!");
    }
}
