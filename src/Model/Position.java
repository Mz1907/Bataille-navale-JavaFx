package Model;

public final class Position implements Comparable<Position> {
    private byte li;
    private char co;

    public Position(byte li, char co) {
        this.li = li;
        this.co = co;
    }

    public byte getLi() {
        return li;
    }

    public char getCo() {
        return co;
    }

    @Override
    public String toString() {
        return "" + co + li;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + this.li;
        hash = 41 * hash + this.co;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Position){
            Position other = (Position) obj;
            return (other.getLi() == li) && (other.getCo() == co); 
        }
        return false;
    }

    @Override
    public int compareTo(Position o) {
        if(li < o.getLi() || ((li == o.getLi()) && (co < o.getCo()))){
            return -1;
        }
        else if(li == o.getLi() && co == o.getCo()){
            return 0;
        }
        else{
            return 1;
        }
    }
    
}
