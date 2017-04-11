package com.bussubscription.controllers;

import com.bussubscription.models.LoginRequest;
import com.bussubscription.models.LoginResponse;
import com.bussubscription.models.UserinfoRequest;
import com.bussubscription.models.UserinfoResponse;
import com.bussubscription.services.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class HTTPController {

    @Autowired
    private DatabaseService databaseService;

    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){

        boolean result = databaseService.validateLogin(request.username, request.password);

        LoginResponse response = new LoginResponse();
        response.authorized = result;

        System.out.println("------ /login - " + request.username + " - " + Boolean.toString(result) + " ------");
        return new ResponseEntity<LoginResponse>(response, HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping(value = "/userinfo", method = RequestMethod.POST)
    public ResponseEntity<UserinfoResponse> getUserInfo(@RequestBody UserinfoRequest request){

        UserinfoResponse response = databaseService.getUserInfo(request.username);
        if(response == null){
            System.out.println("------ /userinfo - " + request.username + " - NO MATCH ------");
            return  new ResponseEntity<UserinfoResponse>(HttpStatus.NO_CONTENT);
        }

        System.out.println("------ /userinfo - " + request.username + " ------");
        return new ResponseEntity<UserinfoResponse>(databaseService.getUserInfo(request.username), HttpStatus.OK);
    }
}
