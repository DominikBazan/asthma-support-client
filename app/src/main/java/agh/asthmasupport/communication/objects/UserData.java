package agh.asthmasupport.communication.objects;

public class UserData {

    private String name, surname, sex, email, birth, height, weight, disease_start, password;

    public UserData(String name, String surname, String sex, String email, String birth,
                    String height, String weight, String disease_start) {
        this.name = name;
        this.surname = surname;
        this.sex = sex;
        this.email = email;
        this.birth = birth;
        this.height = height;
        this.weight = weight;
        this.disease_start = disease_start;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getSex() {
        return sex;
    }

    public String getEmail() {
        return email;
    }

    public String getBirth() {
        return birth;
    }

    public String getHeight() {
        return height;
    }

    public String getWeight() {
        return weight;
    }

    public String getDisease_start() {
        return disease_start;
    }
}