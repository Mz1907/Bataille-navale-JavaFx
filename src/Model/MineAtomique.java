
package Model;

public final class MineAtomique extends Mine{
    private byte degatsIntegrite = 2; // une mine atomique fait perdre 2 points d'integrite

    public MineAtomique(Position pos) {
        super(pos);
    }
}
