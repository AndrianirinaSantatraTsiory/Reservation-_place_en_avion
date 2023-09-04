package Reservation;


import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Jospin
 */
public class avionDB {
    public static Connection ConnectDB(){
        try {
            Class.forName("org.sqlite.JDBC");//charger le driver du fichier jar
            Connection conn=DriverManager.getConnection("jdbc:sqlite:gestionAvionBD");
            System.out.println("OK");
            return  conn;
        } catch (Exception e) {
            System.out.println(" not OK");
            Logger.getLogger(avionDB.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        
    }
}
