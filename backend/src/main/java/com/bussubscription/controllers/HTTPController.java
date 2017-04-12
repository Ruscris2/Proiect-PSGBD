package com.bussubscription.controllers;

import com.bussubscription.models.*;
import com.bussubscription.services.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@RestController
public class HTTPController {

    @Autowired
    private DatabaseService databaseService;

    // ---------------------------------------- /LOGIN -------------------------------------------------
    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){

        boolean result = databaseService.validateLogin(request.username, request.password);

        LoginResponse response = new LoginResponse();
        response.authorized = result;

        System.out.println("------ /login - " + request.username + " - " + Boolean.toString(result) + " ------");
        return new ResponseEntity<LoginResponse>(response, HttpStatus.OK);
    }

    // ---------------------------------------- /USERINFO -------------------------------------------------
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

    // ---------------------------------------- /ADDCLIENT -------------------------------------------------
    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping(value = "/addclient", method = RequestMethod.POST)
    public ResponseEntity<GenericResponse> addClient(@RequestBody AddClientRequest request){

        GenericResponse response = databaseService.insertIntoClients(request.firstname, request.lastname,
                                    request.cnp, request.email);

        System.out.println("------ /addclient ------");
        return new ResponseEntity<GenericResponse>(response, HttpStatus.OK);
    }

    // ---------------------------------------- /CLIENTLIST -------------------------------------------------
    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping(value = "/clientlist", method = RequestMethod.POST)
    public ResponseEntity<List<ClientListResponse>> getClientList(@RequestBody ClientListRequest request){

        List<ClientListResponse> response = databaseService.getClientListPage(request.pagenumber, request.pagesize, request.filter);

        System.out.println("------ /clientlist ------");
        return new ResponseEntity<List<ClientListResponse>>(response, HttpStatus.OK);
    }
}
