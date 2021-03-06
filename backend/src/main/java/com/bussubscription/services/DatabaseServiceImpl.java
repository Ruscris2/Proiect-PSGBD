package com.bussubscription.services;

import com.bussubscription.models.*;
import org.springframework.stereotype.Service;

import java.sql.*;
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
            queryResult = stmt.executeQuery("SELECT nume, prenume, email, cnp, id FROM angajati WHERE username = '" +
                    prepareStrForQuery(username) + "'");

            queryResult.next();

            UserinfoResponse output = new UserinfoResponse();
            output.nume = queryResult.getString(1);
            output.prenume = queryResult.getString(2);
            output.email = queryResult.getString(3);
            output.cnp = queryResult.getString(4);
            output.id = queryResult.getInt(5);
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

    @Override
    public ClientListResponse getClient(String cnp){

        Statement stmt = null;
        ResultSet queryResult = null;
        try{
            ClientListResponse response = new ClientListResponse();

            stmt = connection.createStatement();
            queryResult = stmt.executeQuery("SELECT c.id, c.nume, c.prenume, c.cnp, a.tip, " +
                    "TO_CHAR(c.inceput_abonament, 'DD-MON-YYYY'), TO_CHAR(c.sfarsit_abonament, 'DD-MON-YYYY'), c.email " +
                    "FROM clienti c JOIN abonamente a ON c.abonament_activ=a.id WHERE c.cnp = '" + prepareStrForQuery(cnp) + "'");

            if(!queryResult.next())
                return null;

            response.id = queryResult.getInt(1);
            response.firstname = queryResult.getString(2);
            response.lastname = queryResult.getString(3);
            response.cnp = queryResult.getString(4);
            response.abonament = queryResult.getString(5);
            response.start = queryResult.getString(6);
            response.end = queryResult.getString(7);
            response.email = queryResult.getString(8);

            return response;
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
    public boolean removeClient(String cnp){
        CallableStatement stmt = null;
        try{
            stmt = connection.prepareCall("{ call client_pack.remove_client(?) }");

            stmt.setString(1, cnp);
            stmt.execute();
            return true;
        }
        catch (SQLException e){
            System.err.println("Unforeseen exception: " + e.getMessage());
        }
        finally {
            try {
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
    public boolean updateClient(String firstname, String lastname, String cnp, String email){
        CallableStatement stmt = null;
        try{
            stmt = connection.prepareCall("{ call client_pack.update_client(?, ?, ?, ?) }");

            stmt.setString(1, firstname);
            stmt.setString(2, lastname);
            stmt.setString(3, cnp);
            stmt.setString(4, email);

            stmt.execute();
            return true;
        }
        catch (SQLException e){
            System.err.println("Unforeseen exception: " + e.getMessage());
        }
        finally {
            try {
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
    public List<TPointListResponse> getTPointList(){
        List<TPointListResponse> list = new LinkedList<>();

        Statement stmt = null;
        ResultSet queryResult = null;
        try {
            stmt = connection.createStatement();

            queryResult = stmt.executeQuery("SELECT id, adresa, oras, project_functions.best_client(id) FROM ghisee");
            while (queryResult.next()) {
                TPointListResponse response = new TPointListResponse();
                response.id = queryResult.getInt(1);
                response.address = queryResult.getString(2);
                response.city = queryResult.getString(3);
                response.bestclient = queryResult.getString(4);
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

    @Override
    public List<SubTypeResponse> getSubTypeList(){
        List<SubTypeResponse> list = new LinkedList<>();

        Statement stmt = null;
        ResultSet queryResult = null;
        try{
            stmt = connection.createStatement();

            queryResult = stmt.executeQuery("SELECT id, tip, pret FROM abonamente");
            while(queryResult.next()){
                SubTypeResponse response = new SubTypeResponse();

                response.id = queryResult.getInt(1);
                response.name = queryResult.getString(2);
                response.price = queryResult.getInt(3);
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

    @Override
    public GenericResponse makeTransaction(String cnp, int idUser, int idSub){
        GenericResponse response = new GenericResponse();
        response.msg = "Unknown database error";

        CallableStatement stmt = null;
        try{
            stmt = connection.prepareCall("{ call transaction_pack.make_transaction(?, ?, ?) }");

            stmt.setString(1, cnp);
            stmt.setInt(2, idUser);
            stmt.setInt(3, idSub);

            stmt.execute();
            response.msg = "Transaction was successful!";
        }
        catch (SQLException e){
            if(e.getMessage().contains("ORA-01403")){
                response.msg = "CNP doesn't exist in the database!";
            }
            else {
                System.err.println("Unforeseen exception: " + e.getMessage());
            }
        }
        finally {
            try {
                if (stmt != null)
                    stmt.close();
            }
            catch (Exception e){
                System.err.println("ERROR: Closing statement/result query!");
            }
        }

        return response;
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
