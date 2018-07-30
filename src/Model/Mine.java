package Model;

import java.util.Objects;

public abstract class Mine implements Comparable<Mine> {
    private Position pos;
    
    public Mine(Position pos){
        this.pos = pos;
    }
    
    @Override
    public int compareTo(Mine other){
        return pos.compareTo(other.getPos());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.pos);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Mine){
            Mine other = (Mine) obj;
            return other.pos.equals(pos);
        }
        return false;
    }

    public Position getPos() {
        return pos;
    }

    @Override
    public String toString() {
        return "Mine{" + "pos=" + pos + '}';
    }
    
}
