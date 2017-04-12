package com.bussubscription.services;

import com.bussubscription.models.ClientListResponse;
import com.bussubscription.models.GenericResponse;
import com.bussubscription.models.UserinfoResponse;

import java.util.List;

public interface DatabaseService {
    boolean validateLogin(String username, String password);
    UserinfoResponse getUserInfo(String username);
    GenericResponse insertIntoClients(String firstname, String lastname, String cnp, String email);
    List<ClientListResponse> getClientListPage(int pagenumber, int pagesize, String filter);
    ClientListResponse getClient(String cnp);
    boolean removeClient(String cnp);
    boolean updateClient(String firstname, String lastname, String cnp, String email);
}
