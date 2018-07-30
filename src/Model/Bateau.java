package Model;

public abstract class Bateau {
    protected Position pos;
    protected byte integrite;

    public Bateau(Position pos, byte integrite) {
        this.pos = pos;
        this.integrite = integrite;
    }
    
    public void perdrePointsVie(byte aPerdre){
        integrite -= aPerdre;
    }
    
    public Position getPos() {
        return pos;
    }    

    public byte getIntegrite() {
        return integrite;
    }

    public void setIntegrite(byte integrite) {
        this.integrite = integrite;
    }
    
    public abstract byte tirer();
    
    public abstract Bateau copy(Position pos, byte integrite);
}
