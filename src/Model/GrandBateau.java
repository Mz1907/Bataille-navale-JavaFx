package Model;

import java.util.Objects;

public final class GrandBateau extends Bateau{
    public GrandBateau(Position pos, byte integrite) {
        super(pos, integrite);
    }

    public String toString() {
        return "B";
    }

    /**
     * Renvoi 0 ou 1 ou 2
     *
     * @return
     */
    public byte tirer() {
        int rand = Helper.rand((byte) 10);
        byte portee = 0;

        if (rand < 6 && rand > 2) {
            portee = 1;
        } else if (rand <= 10 && rand > 5) {
            portee = 2;
        }
        return portee;
    }
    
        @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.pos);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof PetitBateau || obj instanceof GrandBateau){
            Bateau other = (Bateau) obj;
            return (other.pos.equals(pos));
        }
        return false;
    }
    
    public GrandBateau copy(Position pos, byte integrite){
        return new GrandBateau(pos, integrite);
    }
}
