package util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import util.DBConnection;


public class ObservableListCreator {
    private ObservableList<String> list = FXCollections.observableArrayList();
    private final String DEFAULT_CHOICE_BOX_VALUE = "All";
    
    
    
    
    
    public ObservableList<String> getObservableList(ResultSet res, String alias)throws SQLException{
            try {
                Vector<Vector<String>> vector = DBConnection.loadDBinVector(res, alias);
                list.add(DEFAULT_CHOICE_BOX_VALUE);
                for(int i = 0; i < vector.size(); i++){
                    list.add(
                            vector.elementAt(i).elementAt(0)
                    );
            }
        return list;
            //DBConnection.closeConnection();
        } catch (SQLException ex) {
            Logger.getLogger(ObservableListCreator.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }
    
    
    public ObservableList<String> getObservableList(String[] array){
        try{
        for(int i = 0; i < array.length; i++)
            list.add(array[i]);
        return list;
        }
        catch(Exception ex){
            System.out.println("What i thought + " + ex.getLocalizedMessage());
            return null;
        }
    }
    
}