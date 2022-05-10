package sk.stuba.fei.uim.vsa.pr1.domain;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;

@Entity
public class CarParkFloor implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @EmbeddedId
    private CarParkEmbeddedId embeddedId;
    
    @MapsId("carParkId")
    @ManyToOne
    private CarPark carPark;
    
    @OneToMany(mappedBy = "carParkFloor")
    private final List<ParkingSpot> parkingSpots = new ArrayList<>();

    public CarParkEmbeddedId getEmbeddedId() {
        return embeddedId;
    }

    public void setEmbeddedId(CarParkEmbeddedId embeddedId) {
        this.embeddedId = embeddedId;
    }

    public CarPark getCarPark() {
        return carPark;
    }

    public void setCarPark(CarPark carPark) {
        this.carPark = carPark;
    }

    public List<ParkingSpot> getParkingSpots() {
        return parkingSpots;
    }
    
    public CarParkFloor addParkingSpot(ParkingSpot spot)
    {
        if (! this.parkingSpots.contains(spot)) {
            spot.setCarParkFloor(this);
            this.parkingSpots.add(spot);
        }
        return this;
    }
}
