package Model;

import java.util.Objects;

public final class PetitBateau extends Bateau {

    public PetitBateau(Position pos, byte integrite) {
        super(pos, integrite);
    }

    public String toString() {
        return "S";
    }

    /**
     * Renvoi 0 ou 1 ou 2
     *
     * @return
     */
    public byte tirer() {
        int rand = Helper.rand((byte) 10);
        byte portee = 0;

        if (rand < 9 && rand > 5) {
            portee = 1;
        } else if (rand <= 10 && rand > 8) {
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
        if (obj instanceof PetitBateau || obj instanceof GrandBateau) {
            Bateau other = (Bateau) obj;
            return (other.getPos().equals(pos));
        }
        return false;
    }

    public PetitBateau copy(Position pos, byte integrite) {
        return new PetitBateau(pos, integrite);
    }
}
