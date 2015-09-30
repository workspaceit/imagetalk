package model;

import model.datamodel.Country;
import model.datamodel.Login;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by mi on 9/20/15.
 */
public class CountryModel extends ImageTalkBaseModel{

    public  CountryModel(){
        super();
        this.tableName = "country";
    }
    public ArrayList<Country> getAll() {
        ArrayList<Country> countryList = new ArrayList<>();
        String sql = "select * from "+this.tableName+" where status = 1";
        this.getData(sql);

        try {
            while (this.resultSet.next()) {
                Country country = new Country();
                country.id = this.resultSet.getInt("id");
                country.name =  this.resultSet.getString("name");
                country.isoCode2  =this.resultSet.getString("iso_code_2");
                country.isoCode2  =this.resultSet.getString("iso_code_3");

                Byte typeByte = this.resultSet.getByte("status");
                country.status =(typeByte.intValue()==1)?true:false;
                countryList.add(country);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            this.closeConnection();
        }
        return countryList;
    }
}
