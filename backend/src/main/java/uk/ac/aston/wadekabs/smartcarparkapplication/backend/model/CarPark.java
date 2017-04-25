package uk.ac.aston.wadekabs.smartcarparkapplication.backend.model;

public class CarPark {

    private String systemCodeNumber;

    public CarPark() {
    }

    public CarPark(BirminghamDataFactoryCarPark birminghamDataFactoryCarPark) {
        systemCodeNumber = birminghamDataFactoryCarPark.getSystemCodeNumber();
    }

    public String getSystemCodeNumber() {
        return systemCodeNumber;
    }

    public void setSystemCodeNumber(String systemCodeNumber) {
        this.systemCodeNumber = systemCodeNumber;
    }

    @Override
    public String toString() {
        return "Car Park:\t[ System code number:\t " + systemCodeNumber + " ]";
    }
}
