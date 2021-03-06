package sk.stuba.fei.uim.vsa.pr1.domain;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "CAR")
public class Car implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    private String brand;
    private String model;
    private String colour;

    @Column(unique = true)
    private String vrp;

    @ManyToOne
    private User user;

    public Car() {
    }

    public Car(String brand, String model, String colour, String vrp) {
        this.brand = brand;
        this.model = model;
        this.colour = colour;
        this.vrp = vrp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getVrp() {
        return vrp;
    }

    public void setVrp(String vrp) {
        this.vrp = vrp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
