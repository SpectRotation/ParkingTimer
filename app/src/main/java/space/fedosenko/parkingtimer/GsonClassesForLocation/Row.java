package space.fedosenko.parkingtimer.GsonClassesForLocation;

import java.util.Arrays;

import space.fedosenko.parkingtimer.GsonClassesForLocation.Element;

public class Row {
    private Element[] elements;

    public Row(Element[] elements) {
        this.elements = elements;
    }

    public Element[] getElements() {
        return elements;
    }

    public void setElements(Element[] elements) {
        this.elements = elements;
    }

    @Override
    public String toString() {
        return "Row{" +
                "elements=" + Arrays.toString(elements) +
                '}';
    }
}
