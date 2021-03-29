package space.fedosenko.parkingtimer.GsonClassesForLocation;

import java.util.Arrays;

public class Location {
    private String status;

    private String[] origin_addresses;
    private String[] destination_addresses;
    private Row[] rows;

    public Location(String status,String[] origin_addresses, String[] destination_addresses,  Row[] row) {
        this.status = status;
        this.destination_addresses = destination_addresses;
        this.origin_addresses = origin_addresses;
        this.rows = row;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String[] getOrigin_addresses() {
        return origin_addresses;
    }

    public void setOrigin_addresses(String[] origin_addresses) {
        this.origin_addresses = origin_addresses;
    }

    public String[] getDestination_addresses() {
        return destination_addresses;
    }

    public void setDestination_addresses(String[] destination_addresses) {
        this.destination_addresses = destination_addresses;
    }

    public Row[] getRows() {
        return rows;
    }

    public void setRows(Row[] rows) {
        this.rows = rows;
    }
    public String getDistance() {
        Element[] element = rows[0].getElements();
        return element[0].getDuration().toString();

    }
    public long getTimeToReturn(){
        Element[] element = rows[0].getElements();
        return element[0].getDuration().getValue();
    }
    @Override
    public String toString() {
        return "Location{" +
                "status='" + status + '\'' +
                ", destination_addresses='" + Arrays.toString(destination_addresses) + '\'' +
                ", origin_addresses='" + Arrays.toString(origin_addresses) + '\'' +
                ", row=" + Arrays.toString(rows) +
                '}';
    }
}
