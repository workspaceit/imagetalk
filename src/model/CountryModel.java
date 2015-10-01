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
        String query = "select * from "+this.tableName+" where status = 1";

        this.setQuery(query);
        this.getData();

        try {
            while (this.resultSet.next()) {
                Country country = new Country();
                country.id = this.resultSet.getInt("id");
                country.iso = this.resultSet.getString("iso");
                country.name =  this.resultSet.getString("name");
                country.niceName =  this.resultSet.getString("nicename");
                country.iso3 = this.resultSet.getString("iso3");
                country.phoneCode = this.resultSet.getInt("phonecode");
                country.numcode = (this.resultSet.getObject("numcode")!=null)? this.resultSet.getInt("numcode") : 0;
                country.status = (this.resultSet.getInt("status")==1)?true:false;
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
