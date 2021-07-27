
package crud;

public class Data {
    
    private final Integer id;
    private final String surname;
    private final String given;
    private final String gender;
    private final String picture;
    
    public Data(Integer id, String surname, String given, String gender, String picture){
        
        this.id = id;
        this.surname = surname;
        this.given = given;
        this.gender = gender;
        this.picture = picture;
        
    }
    
    public Integer getId(){
        
        return id;
        
    }
    
    public String getSurname(){
        
        return surname;
        
    }
    
    public String getGiven(){
        
        return given;
        
    }
    
    public String getGender(){
        
        return gender;
        
    }
    
    public String getPicture(){
        
        return picture;
        
    }
    
}
