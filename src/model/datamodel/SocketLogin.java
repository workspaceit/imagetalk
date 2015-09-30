/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.datamodel;

/**
 *
 * @author mi
 */

public class SocketLogin extends Login{
    
    public Location location;
    
    public SocketLogin(){
        this.location = new Location();
    }
}