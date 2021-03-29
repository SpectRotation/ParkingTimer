package space.fedosenko.parkingtimer.GsonClassesForLocation;

import space.fedosenko.parkingtimer.GsonClassesForLocation.Distance;
import space.fedosenko.parkingtimer.GsonClassesForLocation.Duration;

public class Element {
private String status;
private Duration duration;
private Distance distance;

    public Element(String status, Duration duration, Distance distance) {
        this.status = status;
        this.duration = duration;
        this.distance = distance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "Element{" +
                "status='" + status + '\'' +
                ", duration=" + duration +
                ", distance=" + distance +
                '}';
    }
}
