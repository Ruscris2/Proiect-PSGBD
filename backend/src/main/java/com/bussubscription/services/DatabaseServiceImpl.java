package com.bussubscription.services;

import com.bussubscription.models.ClientListResponse;
import com.bussubscription.models.GenericResponse;
import com.bussubscription.models.UserinfoResponse;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.AbstractList;
import java.util.LinkedList;
import java.util.List;

@Service
public class DatabaseServiceImpl implements DatabaseService {

    private Connection connection;

    public DatabaseServiceImpl(){

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "proiect", "proiect");

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
        Statement stmt = null;
        ResultSet queryResult = null;
        try {
            stmt = connection.createStatement();
            queryResult = stmt.executeQuery("SELECT password FROM angajati WHERE username = '" + prepareStrForQuery(username) + "'");

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
        finally {
            try {
                if (queryResult != null)
                    queryResult.close();
                if (stmt != null)
                    stmt.close();
            }
            catch (Exception e){
                System.err.println("ERROR: Closing statement/result query!");
            }

        }
        return false;
    }

    @Override
    public UserinfoResponse getUserInfo(String username){
        Statement stmt = null;
        ResultSet queryResult = null;
        try {
            stmt = connection.createStatement();
            queryResult = stmt.executeQuery("SELECT nume, prenume, email, cnp FROM angajati WHERE username = '" + prepareStrForQuery(username) + "'");

            queryResult.next();

            UserinfoResponse output = new UserinfoResponse();
            output.nume = queryResult.getString(1);
            output.prenume = queryResult.getString(2);
            output.email = queryResult.getString(3);
            output.cnp = queryResult.getString(4);
            return output;
        }
        catch (SQLException e){
            System.err.println("Unforeseen exception: " + e.getMessage());
        }
        finally {
            try {
                if (queryResult != null)
                    queryResult.close();
                if (stmt != null)
                    stmt.close();
            }
            catch (Exception e){
                System.err.println("ERROR: Closing statement/result query!");
            }

        }
        return null;
    }

    @Override
    public GenericResponse insertIntoClients(String firstname, String lastname, String cnp, String email){
        GenericResponse response = new GenericResponse();
        response.msg = "Unexpected backend error!";

        Statement stmt = null;
        ResultSet queryResult = null;
        try{
            stmt = connection.createStatement();

            // Get an id
            queryResult = stmt.executeQuery("SELECT COUNT(*) FROM clienti");
            queryResult.next();

            int id = queryResult.getInt(1);

            // Insert into table
            stmt.executeUpdate("INSERT INTO clienti (id, nume, prenume, cnp, abonament_activ, email) VALUES " +
                    "(" + Integer.toString(id+1) + ",'" + prepareStrForQuery(firstname) + "','" +
                    prepareStrForQuery(lastname) + "','" + prepareStrForQuery(cnp) + "',-1,'" +
                    prepareStrForQuery(email) + "')");
            response.msg = "Client inserted!";
        }
        catch (SQLException e){
            if(e.getMessage().contains("ORA-00001")){
                response.msg = "Client already exists in database!";
            }
            else{
                System.err.println("Unforeseen exception: " + e.getMessage());
                response.msg = "Unexpected backend error!";
            }
        }
        finally {
            try {
                if (queryResult != null)
                    queryResult.close();
                if (stmt != null)
                    stmt.close();
            }
            catch (Exception e){
                System.err.println("ERROR: Closing statement/result query!");
            }
        }

        return response;
    }

    @Override
    public List<ClientListResponse> getClientListPage(int pagenumber, int pagesize, String filter){
        List<ClientListResponse> list = new LinkedList<>();

        Statement stmt = null;
        ResultSet queryResult = null;
        try{
            stmt = connection.createStatement();

            // Make the query
            if(filter.length() == 0) {
                queryResult = stmt.executeQuery("SELECT id, nume, prenume, cnp, tip, " +
                        "TO_CHAR(inceput_abonament, 'DD-MON-YYYY'), TO_CHAR(sfarsit_abonament, 'DD-MON-YYYY'), email" +
                        " FROM (SELECT c.*, a.tip, ROWNUM r FROM clienti c JOIN abonamente a ON c.abonament_activ=a.id)" +
                        " WHERE r > " + Integer.toString(pagenumber - 1) + "*" + Integer.toString(pagesize) + " AND r <= " +
                        Integer.toString(pagenumber) + "*" + Integer.toString(pagesize));
            }
            else
            {
                queryResult = stmt.executeQuery("SELECT id, nume, prenume, cnp, tip, " +
                        "TO_CHAR(inceput_abonament, 'DD-MON-YYYY'), TO_CHAR(sfarsit_abonament, 'DD-MON-YYYY'), email" +
                        " FROM (SELECT c.*, a.tip, ROWNUM r FROM clienti c JOIN abonamente a ON c.abonament_activ=a.id" +
                        " WHERE c.nume LIKE '%" + filter + "%')" +
                        " WHERE r > " + Integer.toString(pagenumber - 1) + "*" + Integer.toString(pagesize) + " AND r <= " +
                        Integer.toString(pagenumber) + "*" + Integer.toString(pagesize));
            }

            while (queryResult.next()){
                ClientListResponse response = new ClientListResponse();
                response.id = queryResult.getInt(1);
                response.firstname = queryResult.getString(2);
                response.lastname = queryResult.getString(3);
                response.cnp = queryResult.getString(4);
                response.abonament = queryResult.getString(5);
                response.start = queryResult.getString(6);
                response.end = queryResult.getString(7);
                response.email = queryResult.getString(8);

                list.add(response);
            }
        }
        catch (SQLException e){
            System.err.println("Unforeseen exception: " + e.getMessage());
        }
        finally {
            try {
                if (queryResult != null)
                    queryResult.close();
                if (stmt != null)
                    stmt.close();
            }
            catch (Exception e){
                System.err.println("ERROR: Closing statement/result query!");
            }
        }

        return list;
    }

    private String prepareStrForQuery(String str){
        String output = "";
        for(int i = 0; i < str.length(); i++){
            if(str.charAt(i) == '\''){
                output += "\'\'";
            }
            else
                output += str.charAt(i);
        }

        return output;
    }

    @Override
    public void finalize(){
        try {
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
