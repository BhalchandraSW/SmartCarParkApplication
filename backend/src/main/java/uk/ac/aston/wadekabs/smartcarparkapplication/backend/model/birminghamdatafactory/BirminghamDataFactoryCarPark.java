package uk.ac.aston.wadekabs.smartcarparkapplication.backend.model.birminghamdatafactory;

import net.sf.jsefa.csv.annotation.CsvDataType;
import net.sf.jsefa.csv.annotation.CsvField;

import java.math.BigDecimal;
import java.util.Date;

@CsvDataType()
public class BirminghamDataFactoryCarPark {

    @CsvField(pos = 1)
    private String systemCodeNumber;

    @CsvField(pos = 2)
    private int capacity;

    @CsvField(pos = 3)
    private int disabledCapacity;

    @CsvField(pos = 4)
    private String shortDescription;

    @CsvField(pos = 5)
    private BigDecimal northing;

    @CsvField(pos = 6)
    private BigDecimal easting;

    @CsvField(pos = 7)
    private String state;

    @CsvField(pos = 8)
    private String fault;

    @CsvField(pos = 9)
    private int occupancy;

    @CsvField(pos = 10)
    private String occupancyTrend;

    @CsvField(pos = 11)
    private BigDecimal occupancyPercentage;

    @CsvField(pos = 12)
    private BigDecimal fillRate;

    @CsvField(pos = 13)
    private BigDecimal exitRate;

    @CsvField(pos = 14)
    private BigDecimal queueTime;

    @CsvField(pos = 15, format = "yyyy-MM-dd hh:mm:ss")
    private Date lastUpdated;

    @CsvField(pos = 16)
    private String geom;

    public String getSystemCodeNumber() {
        return systemCodeNumber;
    }

    public void setSystemCodeNumber(String systemCodeNumber) {
        this.systemCodeNumber = systemCodeNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getDisabledCapacity() {
        return disabledCapacity;
    }

    public void setDisabledCapacity(int disabledCapacity) {
        this.disabledCapacity = disabledCapacity;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public BigDecimal getNorthing() {
        return northing;
    }

    public void setNorthing(BigDecimal northing) {
        this.northing = northing;
    }

    public BigDecimal getEasting() {
        return easting;
    }

    public void setEasting(BigDecimal easting) {
        this.easting = easting;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFault() {
        return fault;
    }

    public void setFault(String fault) {
        this.fault = fault;
    }

    public int getOccupancy() {
        return occupancy;
    }

    public void setOccupancy(int occupancy) {
        this.occupancy = occupancy;
    }

    public String getOccupancyTrend() {
        return occupancyTrend;
    }

    public void setOccupancyTrend(String occupancyTrend) {
        this.occupancyTrend = occupancyTrend;
    }

    public BigDecimal getOccupancyPercentage() {
        return occupancyPercentage;
    }

    public void setOccupancyPercentage(BigDecimal occupancyPercentage) {
        this.occupancyPercentage = occupancyPercentage;
    }

    public BigDecimal getFillRate() {
        return fillRate;
    }

    public void setFillRate(BigDecimal fillRate) {
        this.fillRate = fillRate;
    }

    public BigDecimal getExitRate() {
        return exitRate;
    }

    public void setExitRate(BigDecimal exitRate) {
        this.exitRate = exitRate;
    }

    public BigDecimal getQueueTime() {
        return queueTime;
    }

    public void setQueueTime(BigDecimal queueTime) {
        this.queueTime = queueTime;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getGeom() {
        return geom;
    }

    public void setGeom(String geom) {
        this.geom = geom;
    }

    @Override
    public String toString() {
        return "Birmingham Data Factory Car Park:\n" +
                "System Code Number:\t" + systemCodeNumber + "\n" +
                "Capacity:\t" + capacity + "\n" +
                "Disabled Capacity:\t" + disabledCapacity + "\n";
    }
}
