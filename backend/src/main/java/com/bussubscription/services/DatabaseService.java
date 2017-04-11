package com.bussubscription.services;

import com.bussubscription.models.UserinfoResponse;

public interface DatabaseService {
    boolean validateLogin(String username, String password);
    UserinfoResponse getUserInfo(String username);
}
