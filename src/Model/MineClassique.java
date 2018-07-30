package Model;

public final class MineClassique extends Mine{
    private static byte degatsIntegrite = 1; // une mine atomique fait perdre 1 points d'integrite

    public MineClassique(Position pos) {
        super(pos);
    }  
}
