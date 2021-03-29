package space.fedosenko.parkingtimer.GsonClassesForLocation;

public class Distance {
    private long value;
    private String text;

    public Distance(long value, String text) {
        this.value = value;
        this.text = text;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Distance{" +
                "value=" + value +
                ", text='" + text + '\'' +
                '}';
    }
}
