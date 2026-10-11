package model;

public class User {
    private int id;
    private String name ;
    private String email;
    private int ecologicalPoints;

    public User(){
        this.id = 0;
        this.name = "";
        this.email = "";
        this.ecologicalPoints = 0 ;
    }
    public User(int id , String name,String email ){
        this.id = 0;
        this.name = "";
        this.email = "";
        this.ecologicalPoints = 0 ;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public int getEcologicalPoints(){
        return ecologicalPoints;
    }

    public void setEcologicalPoints(int ecologicalPoints) {
        this.ecologicalPoints = ecologicalPoints;
    }
    public void addPoints(int points){
        if (points > 0){
            this.ecologicalPoints += points;
        }

    }
    @Override
    public String toString() {
        return "Utilizador: " + name + " (ID: " + id + ") | Pontos Ecológicos: " + ecologicalPoints;
    }
}
