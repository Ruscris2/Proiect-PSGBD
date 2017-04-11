package com.bussubscription.controllers;

import com.bussubscription.models.LoginRequest;
import com.bussubscription.models.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class HTTPController {

    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        LoginResponse response = new LoginResponse();
        response.authorized = true;
        return new ResponseEntity<LoginResponse>(response, HttpStatus.OK);
    }
}
