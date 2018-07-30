package Model.SeaBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Port {

    private List<Integer> flotille1 = new ArrayList<>();
    private List<Integer> flotille2 = new ArrayList<>();

    public Port() {
        flotille1.add(1);
        flotille1.add(1);
        flotille1.add(2);
        flotille2.add(1);
        flotille2.add(1);
        flotille2.add(2);
    }

    public List<Integer> getFlotille1() {
        return flotille1;
    }

    public List<Integer> getFlotille2() {
        return flotille2;
    }

    public int getFlotille1Elem(int index) {
        return flotille1.get(index);
    }

    public int getFlotille2Elem(int index) {
        return flotille2.get(index);
    }

    public void removeFlotille1Elem(Integer elem) {
        flotille1.remove(elem);
    }

    public void removeFlotille2Elem(Integer elem) {
        flotille2.remove(elem);
    }

    public boolean flotille1Contains(Integer elem) {
        return flotille1.contains(elem);
    }

    public boolean flotille2Contains(Integer elem) {
        return flotille2.contains(elem);
    }

}
