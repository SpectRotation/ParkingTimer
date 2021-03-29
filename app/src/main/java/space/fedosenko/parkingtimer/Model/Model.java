package space.fedosenko.parkingtimer.Model;

public class Model {
    private static Model INSTANCE;
    private String info = "Initial info class";
    private Coordinate parkingCoordinate, currentCoordinate;

    private Model() {
    }

    public synchronized static Model getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new Model();
        }
        return INSTANCE;
    }

    public Coordinate getParkingCoordinate() {
        return parkingCoordinate;
    }

    public void setParkingCoordinate(Coordinate parkingCoordinate) {
        this.parkingCoordinate = parkingCoordinate;
    }

    public Coordinate getCurrentCoordinate() {
        return currentCoordinate;
    }

    public void setCurrentCoordinate(Coordinate currentCoordinate) {
        this.currentCoordinate = currentCoordinate;
    }
}

