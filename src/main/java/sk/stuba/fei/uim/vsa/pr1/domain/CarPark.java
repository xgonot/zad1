package sk.stuba.fei.uim.vsa.pr1.domain;

import javax.persistence.*;

@Entity
@Table(name = "CARPARK")
public class CarPark {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String name;

    private String address;

    private Double price;

    public CarPark() {
    }

    public CarPark(String name, String address, Double price) {
        this.name = name;
        this.address = address;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setPrice(Integer price) {
        this.price = price.doubleValue();
    }
}
