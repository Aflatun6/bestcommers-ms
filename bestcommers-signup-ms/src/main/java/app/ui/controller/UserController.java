package app.ui.controller;

import app.service.UserService;
import app.ui.model.UserResponse;
import app.ui.model.UserSignup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final UserService service;
    private final Environment env;

    @Autowired
    public UserController(UserService service, Environment env) {
        this.service = service;
        this.env = env;
    }

    @GetMapping("/status/check")
    public String status() {
        return String.format("working on the port: %s", env.getProperty("local.server.port"));
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signUp(@RequestBody UserSignup user) {
        UserResponse responseUser = service.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

}
