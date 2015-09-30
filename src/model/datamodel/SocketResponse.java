/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.datamodel;

/**
 *
 * @author mi
 */
public class SocketResponse {
    public SocketResponseStat responseStat;
    public Object responseData;

    public SocketResponse(){
        this.responseStat = new SocketResponseStat();
        this.responseData = new Object();
    }
}