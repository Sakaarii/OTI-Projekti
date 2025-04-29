package oti.projekti.mokki;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// Samuel Sakari Paunonen
// 24.04.2025 v1.0
public class SQLDriver {

    // Protected variables for connection information, only accessible by the functions.
    // Connection variable format is: jdbc:mysql://localhost:{port_of_database}/{database_name}
    // Default values are placeholders.
    protected String connection = "jdbc:mysql://localhost:3306/sakila";
    // A username and password that can access the MySQL database;
    protected String name = "root";
    protected String password = "1234";

    // Initializing a driver with new database information.
    SQLDriver(String connection, String userName, String userPassword) {
        this.connection = connection;
        this.name = userName;
        this.password = userPassword;
    };

    SQLDriver() {};

    // Returns query of tables that are defined by conditions in queryTable and has a keyValue
    // , that defines how it can be accessed in the returned object map as a key.
    // Example result: {VAL=[37, BOLGER, 2006-02-15 04:34:33]}, where VAL is the key and queryValues are inside
    // accessible by an ArrayList.
    public Map<String, ArrayList<String>> tableQuery(String queryTable, String keyValue, String[] queryValues){

        Map<String, ArrayList<String>> results = new HashMap<>();

        try{

            Connection driverCon = DriverManager.getConnection(connection, name, password);

            Statement myStatement = driverCon.createStatement();

            ResultSet myResult = myStatement.executeQuery(queryTable);

            while(myResult.next()){
                ArrayList<String> res = new ArrayList<>();
                String main = myResult.getString(keyValue);
                for(String val : queryValues){
                    res.add(myResult.getString(val));
                }
                results.put(main,res);
            }

            driverCon.close();

        }
        catch (Exception e){
            System.out.println(e);
        }


        return results;
    }

    // Updates the updateTable specified, sets the specified field to a specified value in
    // setValueTo in format: "first_name = \"ADAM\" AND last_name = \"WILLIAMS\"".
    // with whereConditions you can narrow down which person you would like to update
    // in format: "first_name = \"HANK\" AND last_name = \"MORRIS\"".
    // The function doesn't return anything.
    public void updateTable(String updateTable, String setValueTo, String whereConditions){
        try{
            Connection driverCon = DriverManager.getConnection(connection, name, password);

            String query = String.format("UPDATE %s SET %s WHERE %s;",updateTable,setValueTo,whereConditions);
            PreparedStatement pst = driverCon.prepareStatement(query);
            System.out.println(pst.toString());
            pst.executeUpdate();

            pst.close();
            driverCon.close();

        }catch (Exception e){
            System.out.println(e);
        }
    }

    // a non-returning query, that can execute things inside the database. Does not return anything.
    public void nonReturningQuery(String query){
        try{
            Connection driverCon = DriverManager.getConnection(connection, name, password);

            PreparedStatement pst = driverCon.prepareStatement(query);

            pst.execute();
        }catch (Exception e){
            System.out.println(e);
        }
    }

    // return results all the columns and rows of the specified query in the format ArrayList<ArrayList<String>>
    // So an ArrayList of ArrayLists that have all the column values.
    public ArrayList<ArrayList<String>> returnAllQuery(String query){

        ArrayList<ArrayList<String>> results = new ArrayList<>();

        try{
            Connection driverCon = DriverManager.getConnection(connection, name, password);

            Statement myStatement = driverCon.createStatement();

            ResultSet myResult = myStatement.executeQuery(query);

            ResultSetMetaData metaData = myResult.getMetaData();

            int columns = metaData.getColumnCount();

            while(myResult.next()){
                ArrayList<String> res = new ArrayList<>();
                for (int i = 1; i < columns+1; i++) {
                    String value = myResult.getString(i);
                    res.add(value);
                }
                results.add(res);
            }

        }catch (Exception e){
            System.out.println(e);
        }

        return results;

    }

    public static void main(String[] args) {

        SQLDriver driver = new SQLDriver();


//        EXAMPLE updateTable:
//        driver.updateTable("customer", "first_name = \"ADAM\"", "first_name = \"BETTY\"");


//        EXAMPLE tableQuery:
//        String[] values1 = new String[]{"category_id"};
//        String main1 = "name";
//        String[] values2 = new String[]{"actor_id", "last_name", "last_update"};
//        String main2 = "first_name";
//        Map<String, ArrayList<String>> list1 = driver.tableQuery("select * from category", main1, values1);
//        Map<String, ArrayList<String>> list2 = driver.tableQuery("select * from actor", main2, values2);
//        System.out.println(list1);
//        System.out.println(list2);
//        System.out.println(list2.get("WILLIAM").get(1));


//        EXAMPLE returnAllQuery:
        ArrayList<ArrayList<String>> list = driver.returnAllQuery("select * from actor");
        System.out.println(list);
    }
}
