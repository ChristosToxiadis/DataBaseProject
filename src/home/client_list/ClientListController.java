package home.client_list;

import data_base_interface.DBConnection;
import home.car_inventory.CarInventoryController;
import home.car_inventory.CarInventoryModel;
import home.create_observable_list.ObservableListCreator;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;




public class ClientListController implements Initializable{
    @FXML
    private TableView clientTable;
    
    @FXML
    private TableColumn<ClientListModel, Integer> idColumn;
    @FXML
    private TableColumn<ClientListModel, String> nameColumn; 
    @FXML
    private TableColumn<ClientListModel, String> surnameColumn;
    @FXML
    private TableColumn<ClientListModel, String> phoneColumn;
    @FXML
    private TableColumn<ClientListModel, String> countryColumn;
    
    
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField LastnameTextField;
    
    @FXML
    private ChoiceBox<String> countryBox;
    @FXML
    private ChoiceBox<String> genderBox;
    
    private ObservableList<ClientListModel> list = FXCollections.observableArrayList();
    
    private String[][] array = new String[2][2];
    private final String DEFAULT_CHOICE_BOX_VALUE = "All";
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try{
            initializeArray(array);
            
            ObservableListCreator list_creator1 = new ObservableListCreator();
            ObservableListCreator list_creator2 = new ObservableListCreator();
            
            ObservableList<String> list1;
            ObservableList<String> list2;
            
            
            ResultSet res = DBConnection.getStatement().executeQuery("select getTable2() as getTable");
            Vector<Vector<String>> vector = DBConnection.loadDBinVector(res, "getTable");
            for(int i = 0; i < vector.size(); i++){
                int j = 0;
                list.add(new ClientListModel(
                        vector.elementAt(i).elementAt(j++),
                        vector.elementAt(i).elementAt(j++),
                        vector.elementAt(i).elementAt(j++),
                        vector.elementAt(i).elementAt(j++),
                        vector.elementAt(i).elementAt(j++)
                ));
            }
            
            DBConnection.closeConnection();
            
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
            phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
            countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
            clientTable.setItems(list);
            
            
            
             //Create Choice box for Country
            list1 = list_creator1.getObserverList(DBConnection.getStatement().executeQuery("select getCountries() as Country"), "Country");
            DBConnection.closeConnection();
            countryBox.setItems(list1);
            
            //Create Choice box for gender
            list2 = list_creator2.getObserverList(DBConnection.getStatement().executeQuery("select getGenders() as Genders"), "Genders");
            DBConnection.closeConnection();
            genderBox.setItems(list2);
            
            
            //give default values in choice boxes
            countryBox.getSelectionModel().selectFirst();
            genderBox.getSelectionModel().selectFirst();
            
            
            // add Listeners for Choice Boxes
            setChoiceBoxListeners();
            
        }
        catch (ClassNotFoundException ex) {
            Logger.getLogger(CarInventoryController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(CarInventoryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
     //Set listners for Choice boxes
    private void setChoiceBoxListeners(){
        countryBox.getSelectionModel().selectedItemProperty().addListener((ev,oldValue, newValue) -> createsSQLQuery(newValue,0));
        genderBox.getSelectionModel().selectedItemProperty().addListener((ev,oldValue, newValue) -> createsSQLQuery(newValue,1));        
    }
    
    
    //This method uses the sql query from getSQLQuery in order to retrieve data from data base and then shows it on car Inventory table
    private void createsSQLQuery(String value, int position){
        try {
            //Delete elements from car table
            while(list.size() > 0 )
                list.remove(0);
            clientTable.setItems(list);
            
            //Retrieve data from data base
            String sql_query = getSQLQuery(value,position);
            ResultSet res = DBConnection.getStatement().executeQuery(sql_query);
            Vector<Vector<String>> vector = DBConnection.loadDBinVector(res, "select_country");
            
            //Shows teh results in table
            for(int i = 0; i < vector.size(); i++){
                int j = 0;
                list.add(new ClientListModel(
                        vector.elementAt(i).elementAt(j++),
                        vector.elementAt(i).elementAt(j++),
                        vector.elementAt(i).elementAt(j++),
                        vector.elementAt(i).elementAt(j++),
                        vector.elementAt(i).elementAt(j++)
                ));
            }
            clientTable.setItems(list);
            DBConnection.closeConnection();
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CarInventoryController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(CarInventoryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    //This method craetes dynamically an sql query based on the filter choices made by user
    private String getSQLQuery(String value, int position){
        if(!value.equals(DEFAULT_CHOICE_BOX_VALUE)) //The element is string so it needs ' ' 
            array[position][1] = "'" + value + "'";
        else
            array[position][1] = DEFAULT_CHOICE_BOX_VALUE;//The element is the default (All). User didn't choose a filter option
        
        StringBuilder sql_query = new StringBuilder();
        
        //craetes dynamically an sql query based on the combined filter options
        for(int i = 0; i < array.length; i++){
            if(array[i][1].equals(DEFAULT_CHOICE_BOX_VALUE))
                continue;
            for(int j = 0; j < array[i].length; j++)
                sql_query.append(array[i][j]);
            sql_query.append(") as select_country intersect ");
        }
        return sql_query.length() > 0 ? sql_query.substring(0, sql_query.length() - 10) : "select getTable2() as select_country"; // Because the word intersect contains 9 letters + 1 for the space
    }
    
    
    //This method Initializes the array that stores sql queries
    private void initializeArray(String[][] array){
        for(int i = 0; i < array.length; i++)
            for(int j = 0; j < array[i].length; j++)
                array[i][j] = j != 1 ? new String("") : new String(DEFAULT_CHOICE_BOX_VALUE);
        
        array[0][0] = "select select_country(";
        array[1][0] = "select select_gender(";
    }
    
}