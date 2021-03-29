package space.fedosenko.parkingtimer.GsonClassesForLocation;

public class Duration {
    private long value;
    private String text;

    public Duration(long value, String text) {
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
        return "Duration{" +
                "value=" + value +
                ", text='" + text + '\'' +
                '}';
    }
}
