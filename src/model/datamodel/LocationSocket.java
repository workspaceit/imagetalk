/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.datamodel;

/**
 *
 * @author mi
 */
public class LocationSocket {
    public SocketLogin receiver;
    public SocketLogin sender;
    public Location location;
    public Team team;
    public LocationSocket(){
        this.receiver = new SocketLogin();
        this.sender = new SocketLogin();
        this.location = new Location();
        this.team = new Team();
    }
}