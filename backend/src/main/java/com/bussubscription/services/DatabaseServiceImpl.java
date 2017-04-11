package com.bussubscription.services;

import org.springframework.stereotype.Service;

import java.sql.*;

@Service
public class DatabaseServiceImpl implements DatabaseService {

    private Connection connection;
    private Statement stmt;
    private ResultSet queryResult;

    public DatabaseServiceImpl(){

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "proiect", "proiect");

            stmt = connection.createStatement();

            System.out.println("================================");
            System.out.println("  CONNECTED TO ORACLE DATABASE  ");
            System.out.println("================================");
        }
        catch (ClassNotFoundException e){
            System.err.println("FATAL ERROR: " + e.getMessage());
        }
        catch (SQLException e){
            System.err.println("DATABASE ERROR: " + e.getMessage());
        }
    }

    @Override
    public boolean validateLogin(String username, String password) {
        try {
            queryResult = stmt.executeQuery("SELECT password FROM angajati WHERE username = '" + username + "'");

            if(!queryResult.next())
                return false;

            if(password.equals(queryResult.getString(1)))
                return true;
            else
                return false;
        }
        catch (SQLException e){
            System.err.println("Unforeseen exception: " + e.getMessage());
        }
        return false;
    }

    @Override
    public void finalize(){
        try {
            queryResult.close();
            stmt.close();
            connection.close();

            System.out.println("================================");
            System.out.println("  DISCONNECTED FROM ORACLE DB   ");
            System.out.println("================================");
        }
        catch (Exception e){
            System.err.println("Error shutting down database!");
        }
    }
}
