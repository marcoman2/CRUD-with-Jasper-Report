
package crud;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;


public class controller implements Initializable{
    
    @FXML
    private TextField id;

    @FXML
    private TextField surname;

    @FXML
    private TextField given;

    @FXML
    private ComboBox<?> gender;

    @FXML
    private Label file_path;

    @FXML
    private Button insert;

    @FXML
    private Button update;

    @FXML
    private Button clear;

    @FXML
    private Button delete;

    @FXML
    private Button print;

    @FXML
    private ImageView image_view;

    @FXML
    private Button insert_image;

    @FXML
    private TableView<Data> table_view;

    @FXML
    private TableColumn<Data, Integer> col_id;

    @FXML
    private TableColumn<Data, String> col_surname;

    @FXML
    private TableColumn<Data, String> col_given;

    @FXML
    private TableColumn<Data, String> col_gender;

    @FXML
    private TableColumn<Data, String> col_picture;
    
    @FXML
    private AnchorPane left_main;
    
    private String[] comboGender = {"Male", "Female", "Others"};
    
    public void comboBox(){
        
        List<String> list = new ArrayList<>();
        
        for(String data: comboGender){
            
            list.add(data);
            
        }
        
        ObservableList dataList = FXCollections.observableArrayList(list);
        
        gender.setItems(dataList);
        
    }
    
    private Connection connect;
    private PreparedStatement prepare;
    private Statement statement;
    private ResultSet result;
    
    public Connection connectDb(){
        
        try{
            
            Class.forName("com.mysql.jdbc.Driver");
            
            connect = DriverManager.getConnection("jdbc:mysql://localhost/admin", "root" , "");
            
            return connect;
            
        }catch(Exception e){}
        
        return null;
        
    }
    
    public ObservableList<Data> dataList(){
        
        connect = connectDb();
        
        ObservableList<Data> dataList = FXCollections.observableArrayList();
        
        String sql = "SELECT * FROM account";
        
        try{
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();
            
            Data data;
            
            while(result.next()){
                
                data = new Data(result.getInt("id"), result.getString("surname"),
                        result.getString("given"), result.getString("gender"),
                        result.getString("picture"));
                
                dataList.add(data);
                
            }
            
        }catch(Exception e){}
        
        return dataList;
        
    }
    
    public void showData(){
        ObservableList<Data> showList = dataList();
        
        col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        col_surname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        col_given.setCellValueFactory(new PropertyValueFactory<>("given"));
        col_gender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        col_picture.setCellValueFactory(new PropertyValueFactory<>("picture"));
        
        table_view.setItems(showList);
        
    }
    
    public void insertImage(){
        
        FileChooser open = new FileChooser();
        
        Stage stage = (Stage)left_main.getScene().getWindow();
        
        File file = open.showOpenDialog(stage);
        
        if(file != null){
            
            String path = file.getAbsolutePath();
            
            path = path.replace("\\", "\\\\");
            
            file_path.setText(path);

            Image image = new Image(file.toURI().toString(), 110, 110, false, true);
            
            image_view.setImage(image);
            
        }else{
            
            System.out.println("NO DATA EXIST!");
            
        }
    }
    
    public void insert(){
        
        connect = connectDb();
//        I HAVE 5 COLUMNS
        String sql = "INSERT INTO account VALUES (?,?,?,?,?)";
        
        try{
            
            if(id.getText().isEmpty() | surname.getText().isEmpty()
                    | given.getText().isEmpty() 
                    | gender.getSelectionModel().isEmpty()
                    | image_view.getImage() == null){
                
                Alert alert = new Alert(AlertType.ERROR);
                
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Enter all blank fields!");
                alert.showAndWait();
                
            }else{
                prepare = connect.prepareStatement(sql);
                prepare.setString(1, id.getText());
                prepare.setString(2, surname.getText());
                prepare.setString(3, given.getText());
                prepare.setString(4, (String)gender.getSelectionModel().getSelectedItem());
                prepare.setString(5, file_path.getText());
                prepare.executeUpdate();
            
                showData();
                clear();
            }
        }catch(Exception e){}
        
    }
    
    public void update(){
        
        connect = connectDb();
        
        String path = file_path.getText();
        
        path = path.replace("\\", "\\\\");
        
        String sql = "UPDATE account SET `surname` = '" 
                + surname.getText() + "', `given` = '" 
                + given.getText() + "', `gender` = '" 
                + gender.getSelectionModel().getSelectedItem() 
                + "', `picture` = '" + path 
                + "' WHERE id = '" + id.getText() + "'";
        
        try{
            
            if(id.getText().isEmpty() | surname.getText().isEmpty()
                    | given.getText().isEmpty() 
                    | gender.getSelectionModel().isEmpty()
                    | image_view.getImage() == null){
                
                Alert alert = new Alert(AlertType.ERROR);
                
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Enter all blank fields!");
                alert.showAndWait();
                
            }else{
            
                statement = connect.createStatement();
                statement.executeUpdate(sql);

                Alert alert = new Alert(AlertType.INFORMATION);

                alert.setTitle("MarcoMan Message");
                alert.setHeaderText(null);
                alert.setContentText("Successfully Update the data!");
                alert.showAndWait();

                showData();
                clear();
                
            }
        }catch(Exception e){}
        
    }
    
    public void delete(){
        
        String sql = "DELETE from account WHERE `id` = '" + id.getText() + "'";
        
        connect = connectDb();
        
        try{
            
            Alert alert = new Alert(AlertType.CONFIRMATION);
            
            alert.setTitle("Confirmation Message");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure that you want to delete it?");
            
            Optional<ButtonType> buttonType = alert.showAndWait();
            
            if(buttonType.get() == ButtonType.OK){
            
            statement = connect.createStatement();
            statement.executeUpdate(sql);
                
            }else{
                
                return;
                
            }
            
            showData();
            clear();
            
        }catch(Exception e){}
        
    }

    public void print(){
        
        connect = connectDb();
        
        try{
            JasperDesign jDesign = JRXmlLoader.load("F:\\ajava\\6 NUMBER\\CRUD\\src\\crud\\report.jrxml");
        
            JasperReport jReport = JasperCompileManager.compileReport(jDesign);
            
            JasperPrint jPrint = JasperFillManager.fillReport(jReport, null, connect);
            
            JasperViewer viewer = new JasperViewer(jPrint, false);
            
            viewer.setTitle("MarcoMan Report");
            viewer.show();
            
        }catch(Exception e){}
    }
    
    public void selectData(){
        
        Data data = table_view.getSelectionModel().getSelectedItem();
        
        int num = table_view.getSelectionModel().getSelectedIndex();
        
        if((num-1) < -1)
            return;
        
        id.setText(String.valueOf(data.getId()));
        surname.setText(data.getSurname());
        given.setText(data.getGiven());
        gender.getSelectionModel().clearSelection();
        
        String picture ="file:" +  data.getPicture();
        
        Image image = new Image(picture, 110, 110, false, true);
        
        image_view.setImage(image);
        
        String path = data.getPicture();
        
        file_path.setText(path);
        
    }
    
    public void clear(){
        
        id.setText("");
        surname.setText("");
        given.setText("");
        gender.getSelectionModel().clearSelection();
        image_view.setImage(null);
        
    }
    
    public void textfieldDesign(){
        
        if(id.isFocused()){
            
            id.setStyle("-fx-border-width:2px; -fx-background-color:#fff");
            surname.setStyle("-fx-border-width:1px; -fx-background-color:transparent");
            given.setStyle("-fx-border-width:1px; -fx-background-color:transparent");
            gender.setStyle("-fx-border-width:1px; -fx-background-color:transparent");
            
        }else if(surname.isFocused()){
            
            id.setStyle("-fx-border-width:1px; -fx-background-color:transparent");
            surname.setStyle("-fx-border-width:2px; -fx-background-color:#fff");
            given.setStyle("-fx-border-width:1px; -fx-background-color:transparent");
            gender.setStyle("-fx-border-width:1px; -fx-background-color:transparent");
            
        }else if(given.isFocused()){
            
            id.setStyle("-fx-border-width:1px; -fx-background-color:transparent");
            surname.setStyle("-fx-border-width:1px; -fx-background-color:transparent");
            given.setStyle("-fx-border-width:2px; -fx-background-color:#fff");
            gender.setStyle("-fx-border-width:1px; -fx-background-color:transparent");
            
        }else if(gender.isFocused()){
            
            id.setStyle("-fx-border-width:1px; -fx-background-color:transparent");
            surname.setStyle("-fx-border-width:1px; -fx-background-color:transparent");
            given.setStyle("-fx-border-width:1px; -fx-background-color:transparent");
            gender.setStyle("-fx-border-width:2px; -fx-background-color:#fff");
            
        }
        
    }
    
    public void defaultId(){
        
        id.setStyle("-fx-border-width:2px; -fx-background-color:#fff");
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resource){
        
        comboBox();
        
        defaultId();
        
        showData();
        
    }
    
}
