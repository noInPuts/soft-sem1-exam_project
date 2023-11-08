package cphbusiness.noInPuts.accountService.controller;

import cphbusiness.noInPuts.accountService.dto.UserDTO;
import cphbusiness.noInPuts.accountService.service.UserService;
import cphbusiness.noInPuts.accountService.service.JwtService;
import cphbusiness.noInPuts.accountService.service.RabbitMessagePublisher;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(maxAge = 3600)
@RestController
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    // TODO: Change to constructor injection
    @Autowired
    public RabbitMessagePublisher rabbitMessagePublisher;

    @Autowired
    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    // Endpoint for creating a user account
    @PostMapping(value = "/user/create", produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    // TODO: Swagger documentation
    // TODO: Spam check
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody UserDTO POSTuserDTO) {
        UserDTO userDTO = userService.createAccount(POSTuserDTO);
        String jwtToken = jwtService.generateToken(userDTO);

        rabbitMessagePublisher.createdUserEvent("User created: " + userDTO.getUsername());
        // TODO: Fix this redundant code
        Map<String, Object> response = new HashMap<>();
        response.put("user", userDTO);
        response.put("jwtToken", jwtToken);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Endpoint for logging in to a user account
    // TODO: Create tests for this
    @PostMapping(value = "/user/login", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserDTO POSTuserDTO) {
        UserDTO userDTO = userService.login(POSTuserDTO);
        String jwtToken = jwtService.generateToken(userDTO);

        // TODO: Fix this redundant code
        Map<String,Object> response = new HashMap<>();
        response.put("user", userDTO);
        response.put("jwtToken", jwtToken);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}