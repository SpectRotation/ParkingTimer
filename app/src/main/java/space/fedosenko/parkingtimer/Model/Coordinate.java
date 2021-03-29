package space.fedosenko.parkingtimer.Model;

public class Coordinate {
    private String latitude,longitude;

    public Coordinate(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public Coordinate(double latitude, double longitude) {
        this.latitude = ""+latitude;
        this.longitude = ""+longitude;
    }
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return latitude+"," +longitude;
    }
}
