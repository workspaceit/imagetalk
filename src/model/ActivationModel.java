package model;

import java.sql.SQLException;

/**
 * Created by mi on 10/1/15.
 */
public class ActivationModel extends ImageTalkBaseModel{
    int id;
    String phone_number;
    String activation_code;
    String created_date;
    public ActivationModel(){
        super();
        super.tableName = "activation";
    }
    public boolean isPhoneNumberExist(){
        String query ="select * from " + super.tableName+" where phone_number='"+this.phone_number+"' limit 1 ";

        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                this.id  = this.resultSet.getInt("id");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }

        return false;
    }
    public boolean isTokenValid(){
        String query ="select * from " + super.tableName+" where phone_number='"+this.phone_number+"' and activation_code='" +this.activation_code+"' limit 1";

        this.setQuery(query);
        this.getData();
        try {
            while (this.resultSet.next()) {
                this.id  = this.resultSet.getInt("id");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }

        return false;
    }
    public boolean assignToken(){
        int rand = (int)(Math.random()*9000)+1000;
        this.activation_code = String.valueOf(rand);

        /*=====| For Testing purpose |==*/
        /*=====/ Remove after sms api integration /===========*/
            this.activation_code = "1234";
        /*===================ENDS HERE======================*/

        String query = "";
        if(this.isPhoneNumberExist()){
            query = "UPDATE " +super.tableName+" SET "+
            " phone_number='"+this.phone_number+"',activation_code='" +this.activation_code+"'"+
            " where id ="+this.id;
            if(!this.updateData(query)){
                return false;
            }

        }else {
            query = "INSERT INTO " + this.tableName + " " +
                    "( phone_number," +
                    " activation_code) " +
                    " VALUES (" +
                    "'" + this.phone_number + "'," +
                    "'" + this.activation_code + "'" +
                    ")";

            this.id = this.insertData(query);
            if (this.id == 0) {
                return false;
            }
        }
        return true;
    }
    public boolean setPhoneNumber(String phone_number){
        this.phone_number =phone_number;
        return true;
    }
    public boolean setActivationCode(String activation_code){
        this.activation_code =activation_code;
        return true;
    }

    public String getPhoneNumber(){
        return this.phone_number;
    }
    public String getActivationCode(){
        return this.activation_code;
    }
    public int getId(){
        return this.id;
    }
}
