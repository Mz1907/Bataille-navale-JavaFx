package Model.SeaBuilder;

import Model.Armee;
import Model.Bateau;
import Model.GrandBateau;
import Model.Helper;
import Model.Mine;
import Model.MineAtomique;
import Model.MineClassique;
import Model.PetitBateau;
import Model.Position;
import java.util.HashSet;
import java.util.Observable;
import java.util.Set;
import java.util.TreeSet;
//import Vue.Vue;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import Vue.Vue;

public final class Mer extends Observable {

    private final Set<Position> posContamined = new HashSet<>(); //les positions radioactives
    private final Set<Mine> posMinees = new TreeSet<>(); //les positions minées (peut être atomique ou normale)
    private final List<Armee> armees = new ArrayList<>();
    private Port port;
    private final Random rand = new Random();
    private final int SIZE;
    private int x, y;

    public Mer(int size, String nom1, String nom2, boolean isAleatoire) {
        SIZE = size;
        //On construit les bateaux et on leur assigne un nom
        construireArmees(new String[]{nom1, nom2}, isAleatoire);
        construireMer(size);
        if (isAleatoire) {
            port = new Port();
        }
    }

    //Constructeur CONSOLE
    public Mer(String[] nomArmees, boolean isAleatoire) {
        SIZE = 5; // constructeur par default en mode console
        ajouterLesArmees(nomArmees, isAleatoire);
        placerBateaux(); //placement des bateaux par chaque armée respective
        positionnerMines(); //placement des mines
    }

    public boolean placerBateauManuel(String tempBoatViewCssType, int armyTurn, int x, int y) {
        if (armyTurn == 1) { // l'armée 1 ajoute le bateau
            return getFirstArmy().placerBateauxManuel(tempBoatViewCssType, x, y, getSecondArmyFlotille());
        } else if (armyTurn == 2) { // l'armée 2 ajoute le bateau
            return getSecondArmy().placerBateauxManuel(tempBoatViewCssType, x, y, getFirstArmyFlotille());
        }
        return false;
    }



    public void setChangedAndNotify() {
        setChanged();
        notifyObservers();
    }

    private boolean isValid(int x, int y) {
        return x >= 0 && x < SIZE && y >= 0 && y < SIZE;
    }

    private void construireMer(int size) {
        positionnerMines(); //placement des mines
    }

    private void construireArmees(String[] nomArmees, boolean isAleatoire) {
        ajouterLesArmees(nomArmees, isAleatoire);
        placerBateaux(); //placement des bateaux par chaque armée respective
    }

    public int getSIZE() {
        return SIZE;
    }

    private void ajouterLesArmees(String[] nomArmees, boolean isAleatoire) {
        armees.add(new Armee(nomArmees[0], 1, isAleatoire));
        armees.add(new Armee(nomArmees[1], 2, isAleatoire));
    }

    private void positionnerMines() {
        for (int i = 1; i <= SIZE; ++i) {
            for (int j = 1; j <= SIZE; ++j) {
                Position currentPos = new Position((byte) i, Helper.toChar((byte) j));
                positionnerMines(currentPos);
            }
        }
        setChangedAndNotify(this);
    }

    private void positionnerMines(Position currentPos) {
        //1. determiner si bateau non-present
        if (isPosOccuped(currentPos)) {
            placerLaMine10(currentPos); // une chance sur 10
        }
    }

    /**
     * Une chance sur 10 que l'on place une mine
     *
     * @param currentPos
     */
    private void placerLaMine10(Position currentPos) {
        if (uneChanceSur10()) {
            placerLaMine2(currentPos);
        }
    }

    /**
     * Une chance sur 2 que l'on place une mine
     *
     * @param currentPos
     */
    private void placerLaMine2(Position currentPos) {
        if (uneChanceSur2()) {
            posMinees.add(new MineAtomique(currentPos));
        } else {
            posMinees.add(new MineClassique(currentPos));
        }
    }

    private boolean isPosOccuped(Position currentPos) {
        Armee a1 = armees.get(0);
        Armee a2 = armees.get(1);
        return !a1.getBateauxPositions().contains(currentPos) && !a2.getBateauxPositions().contains(currentPos);
    }

    private boolean uneChanceSur10() {
        return Helper.rand((byte) 10) == 4;
    }

    private boolean uneChanceSur2() {
        return (Helper.rand((byte) 2)) % 2 == 0;
    }

    //renvoi un objet mine si la newPos equals une positions minees. Sinon renvoi null
    private Mine getMineByPositon(Position pos) {
        for (Mine m : posMinees) {
            if (m.getPos().equals(pos)) {
                return m;
            }
        }
        return null;
    }

    //supprime une mine (non-nucléaire) ayant explosé
    private boolean removeMine(Mine m) {
        return posMinees.remove(m);
    }

    /**
     *
     * @param posTireur : la position du bateau tireur
     * @param portee : la puissance de portée du tir
     * @return Position : la position "supérieur gauche"
     */
    private Position limiteSuperieure(Position posTireur, byte portee) {
        byte co = definirColonneSup(posTireur, portee); // 1. Définir la colonne dans laquelle se trouvera la limite supérieur
        byte li = definirLigneSup(posTireur, portee); //2. Définir la ligne dans laquelle se trouvera la limite supérieur
        return new Position(li, Helper.toChar(co));
    }

    private byte definirColonneSup(Position posTireur, byte portee) {
        byte coTireur = Helper.toByte(posTireur.getCo()); // conversion lettre de la colonne en byte
        byte co = definirColonneSup(coTireur, portee);
        return co;
    }

    private byte definirColonneSup(byte coTireur, byte portee) {
        byte co = 0;
        if (coTireur - portee >= 1) {
            co = (byte) (coTireur - portee);
        } else {
            co = (byte) (SIZE - portee + coTireur);
        }
        return co;
    }

    private byte definirLigneSup(Position posTireur, byte portee) {
        byte li = 0;
        if (posTireur.getLi() - portee >= 1) {
            li = (byte) (posTireur.getLi() - portee);
        } else {
            li = (byte) (SIZE - portee + posTireur.getLi());;
        }
        return li;
    }

    /**
     *
     * @param posTireur
     * @param portee
     * @return
     */
    private Position limiteInferieure(Position posTireur, byte portee) {
        byte co = definirColonneInf(posTireur, portee); // 1. Définir la colonne dans laquelle se trouvera la limite supérieur
        byte li = definirLigneInf(posTireur, portee); //2. Définir la ligne dans laquelle se trouvera la limite supérieur
        return new Position(li, Helper.toChar(co));
    }

    private byte definirColonneInf(Position posTireur, byte portee) {
        byte coTireur = Helper.toByte(posTireur.getCo()); // conversion lettre de la colonne en byte
        byte co = definirColonneInf(coTireur, portee);
        return co;
    }

    private byte definirColonneInf(byte coTireur, byte portee) {
        if (coTireur + portee <= SIZE) {
            return (byte) (coTireur + portee);
        } else {
            return (byte) (coTireur + portee - SIZE);
        }
    }

    private byte definirLigneInf(Position posTireur, byte portee) {
        byte li = 0;
        if (posTireur.getLi() + portee <= SIZE) {
            li = (byte) (posTireur.getLi() + portee);
        } else {
            li = (byte) (posTireur.getLi() + portee - SIZE);;
        }
        return li;
    }

    /**
     * Est utilisé dans la fonction posImpactees
     *
     * @param limiteInferieure
     * @param limiteSuperieure
     * @param liSup
     * @param liInf
     * @param coSup
     * @param coInf
     * @return
     */
    private Set<Position> limiteSupLower(Position limiteInferieure, Position limiteSuperieure, byte liSup, byte liInf, byte coSup, byte coInf) {
        Set<Position> posImpactees = new TreeSet<>();
        boolean tourNordSudCOmplet = false;
        coSup = definirCoSup(coSup, coInf, liSup, liInf, posImpactees, limiteSuperieure, limiteInferieure);
        return posImpactees;
    }

    private byte definirCoSup(byte coSup, byte coInf, byte liSup, Set<Position> posImpactees, Position limiteSuperieure, Position limiteInferieure) {
        if (limiteSuperieure.getCo() < limiteInferieure.getCo()) {
            coSup = addPosImpactees(coSup, coInf, liSup, posImpactees, limiteSuperieure);
        } else {
            coSup = addPosImpactees(false, coSup, coInf, liSup, posImpactees, limiteSuperieure);
        }
        return coSup;
    }

    private byte definirCoSup(byte coSup, byte coInf, byte liSup, byte liInf, Set<Position> posImpactees, Position limiteSuperieure, Position limiteInferieure) {
        for (; liSup <= liInf; ++liSup) {
            coSup = definirCoSup(coSup, coInf, liSup, posImpactees, limiteSuperieure, limiteInferieure);
        }
        return coSup;
    }

    private byte addPosImpactees(byte coSup, byte coInf, byte liSup, Set<Position> posImpactees, Position limiteSuperieure) {
        for (; coSup <= coInf; ++coSup) {
            posImpactees.add(new Position(liSup, Helper.toChar(coSup)));
        }
        return Helper.toByte(limiteSuperieure.getCo());
    }

    private byte addPosImpactees(boolean tourEstOuestComplet, byte coSup, byte coInf, byte liSup, Set<Position> posImpactees, Position limiteSuperieure) {
        while (isPosImpactAndTourEnded(coSup, coInf, tourEstOuestComplet)) {
            tourEstOuestComplet = isTourEstOuestComplet(tourEstOuestComplet, coSup, coInf);
            posImpactees.add(new Position(liSup, Helper.toChar(coSup)));
            ++coSup;
            coSup = setCoSup(coSup);
        }
        return Helper.toByte(limiteSuperieure.getCo());
    }

    private boolean isTourEstOuestComplet(boolean tourEstOuestComplet, byte coSup, byte coInf) {
        if (coSup == coInf) {
            tourEstOuestComplet = true;
        }
        return tourEstOuestComplet;
    }

    private byte setCoSup(byte coSup) {
        if (coSup > SIZE) {
            coSup = 1;
        }
        return coSup;
    }

    private byte setLiSup(byte liSup) {
        if (liSup > SIZE) {
            liSup = 1;
        }
        return liSup;
    }

    private boolean isPosImpactAndTourEnded(byte coSup, byte coInf, boolean tourEstOuestComplet) {
        return (coSup <= SIZE || (coSup >= coInf)) && !tourEstOuestComplet;
    }

    /**
     * Est utilisé dans la fonction posImpactees
     *
     * @param limiteInferieure
     * @param limiteSuperieure
     * @param liSup
     * @param liInf
     * @param coSup
     * @param coInf
     * @return
     */
    private Set<Position> limiteSupGreater(Position limiteInferieure, Position limiteSuperieure, byte liSup, byte liInf, byte coSup, byte coInf) {
        Set<Position> posImpactees = new TreeSet<>();
        boolean tourNordSudCOmplet = false;
        addPosImpactee(posImpactees, limiteInferieure, limiteSuperieure, tourNordSudCOmplet, coSup, coInf, liSup, liInf);
        return posImpactees;
    }

    private void addPosImpactee(Set<Position> posImpactees, Position limiteInferieure, Position limiteSuperieure, boolean tourNordSudCOmplet, byte coSup, byte coInf, byte liSup, byte liInf) {
        while (isPosImpactAndtourEnded(liSup, liInf, tourNordSudCOmplet)) {
            tourNordSudCOmplet = isTourNordSudCOmplet(tourNordSudCOmplet, liSup, liInf);
            coSup = addPosImpactee(posImpactees, limiteInferieure, limiteSuperieure, coSup, liSup, coInf);
            ++liSup;
            liSup = setLiSup(liSup);
        }
    }

    private boolean isPosImpactAndtourEnded(byte liSup, byte liInf, boolean tourNordSudCOmplet) {
        return (liSup >= liInf || liSup != (byte) (liInf + 1)) && !tourNordSudCOmplet;
    }

    private byte addPosImpactee(Set<Position> posImpactees, Position limiteInferieure, Position limiteSuperieure, byte coSup, byte liSup, byte coInf) {
        if (limiteSuperieure.getCo() < limiteInferieure.getCo()) {
            coSup = addPosImpactees(coSup, coInf, liSup, posImpactees, limiteSuperieure);
        } else {
            coSup = addPosImpactees(false, coSup, coInf, liSup, posImpactees, limiteSuperieure);
        }
        return coSup;
    }

    private boolean isTourNordSudCOmplet(boolean tourNordSudComplet, byte liSup, byte liInf) {
        if (liSup == liInf) {
            tourNordSudComplet = true;
        }
        return tourNordSudComplet;
    }

    /**
     * Concatène l'utilisaton de plusieurs fonctions pour trouver les positons
     * impactees
     *
     * @param posTireur
     * @param portee
     * @return
     */
    private Set<Position> positionsImpactees(Position posTireur, byte portee) {
        Position limiteInferieure = limiteInferieure(posTireur, portee);
        Position limiteSuperieure = limiteSuperieure(posTireur, portee);
        return positionsImpactees(limiteInferieure, limiteSuperieure, posTireur);
    }

    /**
     * Détermine les positions impactées par un tir.
     *
     * @param limiteInferieure
     * @param limiteSuperieure
     * @param posTireur
     * @return
     */
    private Set<Position> positionsImpactees(Position limiteInferieure, Position limiteSuperieure, Position posTireur) {
        Set<Position> posImpactees = PosImpactees(limiteInferieure, limiteSuperieure);
        posImpactees.remove(posTireur); // la position du tireur ne doit pas être impactable
        return posImpactees;
    }

    private Set<Position> PosImpactees(Position limiteInferieure, Position limiteSuperieure) {
        byte liSup = limiteSuperieure.getLi();
        byte liInf = limiteInferieure.getLi();
        byte coSup = Helper.toByte(limiteSuperieure.getCo());
        byte coInf = Helper.toByte(limiteInferieure.getCo());
        return PosImpactees(limiteInferieure, limiteSuperieure, coSup, coInf, liSup, liInf);
    }

    private Set<Position> PosImpactees(Position limiteInferieure, Position limiteSuperieure, byte coSup, byte coInf, byte liSup, byte liInf) {
        Set<Position> posImpactees = new TreeSet<>();
        if (limiteSuperieure.getLi() < limiteInferieure.getLi()) {
            posImpactees = limiteSupLower(limiteInferieure, limiteSuperieure, liSup, liInf, coSup, coInf);
        } else if (limiteSuperieure.getLi() > limiteInferieure.getLi()) {
            posImpactees = limiteSupGreater(limiteInferieure, limiteSuperieure, liSup, liInf, coSup, coInf);
        }
        return posImpactees;
    }

    /**
     * Renvois les positions possibles de déplacement.
     *
     * @param bat
     * @param a1
     * @param a2
     */
    public Set<Position> posPossibles(Bateau bat, Armee a1, Armee a2) {
        byte porteeDeplacement = (byte) (bat instanceof GrandBateau ? 1 : 2);
        Position limiteSuperieure = limiteSuperieure(bat.getPos(), porteeDeplacement);
        Position limiteInferieure = limiteInferieure(bat.getPos(), porteeDeplacement);
        Set<Position> positionsPossiblesCarre = positionsImpactees(limiteInferieure, limiteSuperieure, bat.getPos()); //positions sur lesquels un bateau peut se poser en théorie. 
        //Vérication si position est occupé par un autre bateau
        removeAll(positionsPossiblesCarre, a1, a2);
        return posPossiblesCroix(bat, positionsPossiblesCarre);
    }

    /**
     * Supprime das posPossible des position occupées par des bateaux
     *
     * @param positionsPossiblesCarre
     * @param a1
     * @param a2
     * @return
     */
    private void removeAll(Set<Position> positionsPossiblesCarre, Armee a1, Armee a2) {
        positionsPossiblesCarre.removeAll(a1.getBateauxPositions());
        positionsPossiblesCarre.removeAll(a2.getBateauxPositions());
        positionsPossiblesCarre.removeAll(posContamined); //Vérification si position est contamonée
    }

    /**
     * Reçoit des un "carré" de positions. Renvoit une "croix" de positions.
     *
     * @param bat
     * @return
     */
    public Set<Position> posPossiblesCroix(Bateau bat, Set<Position> positionsPossiblesCarre) {
        byte ligneBateau = bat.getPos().getLi();
        char colonneBateau = bat.getPos().getCo();
        return posPossiblesCroix(positionsPossiblesCarre, ligneBateau, colonneBateau);
    }

    private Set<Position> posPossiblesCroix(Set<Position> positionsPossiblesCarre, byte ligneBateau, char colonneBateau) {
        Set<Position> positionsPossiblesCroix = new TreeSet<>();
        for (Position pos : positionsPossiblesCarre) {
            if (pos.getCo() == colonneBateau || pos.getLi() == ligneBateau) {
                positionsPossiblesCroix.add(pos);
            }
        }
        return positionsPossiblesCroix;
    }

//    public void afficherPosDispo(Bateau bat, Armee a1, Armee a2) {
//        afficherMerPositionPossibles(this.posPossibles(bat, a1, a2));
//    }
//    public void ajouterObserver(Vue vue) {
//        for (Armee armee : armees) {
//            armee.addObserver(vue);
//        }
//    }
    private void placerBateaux() {
        Set<Position> positionsTheoriquesA1 = armees.get(0).definirPositions((byte) SIZE, new HashSet<Position>());
        armees.get(0).placerBateaux(positionsTheoriquesA1);

        Set<Position> positionsTheoriquesA2 = armees.get(1).definirPositions((byte) SIZE, positionsTheoriquesA1);
        armees.get(1).placerBateaux(positionsTheoriquesA2);
    }

    /**
     * Saisis le bateau tireur, effectue le tir, et calcul les dégatsArmee1
     *
     * @param vue
     * @param mer
     * @param a1
     * @param a2
     */
    public Armee tirerAction(Armee a1, Armee a2, byte portee, Position posTireur) {
        Set<Position> positionsImpactees = positionsImpactees(posTireur, portee); //On calcul les positions impactees par ce tir
        a2 = a2.traitementTorpille(positionsImpactees, a1, posTireur); //On interroge les bateaux adverses pour savoir s'ils sont impactés
        return a2;
    }

    public Armee deplacerBateau(Position newPos, Armee a1, Armee a2, Position posBateauDeplacer) {
        return deplacerBateau(a1, newPos, a1.getBateauByPosition(posBateauDeplacer));
    }

    private void traitementMines(Armee a1, Position newPos, Bateau bateau) {
        Mine m = getMineByPositon(newPos);
        if (m != null && m instanceof MineAtomique) { //la position est minée, il faut faire une action differente selon le type de mine
            traitementMineAtomique(a1, m, newPos, bateau);
        } else if (m != null && m instanceof MineClassique) {
            traitementMineClassique(a1, m, newPos, bateau);
        }
    }

    private Armee deplacerBateau(Armee a1, Position newPos, Bateau bateau) {
        if (posContamined.contains(newPos)) { // si newPos != une position contaminée
            traitementMines(a1, newPos, bateau);
        } else { // newPos n'est pas une position minée, on peut mettre à jour la position du bateau
            return updatePosition(a1, newPos, bateau);
        }
        return a1;
    }

    private Armee updatePosition(Armee a1, Position newPos, Bateau bateau) {
        a1.updatePosition(newPos, bateau);
        return a1;
    }

    /**
     * Applique les dégats d'une mine atomique aux bateaux concernés de la
     * flotille
     *
     * @param m
     * @param newPos
     * @param bateau
     */
    private void traitementMineAtomique(Armee a1, Mine m, Position newPos, Bateau bateau) {
        a1.getBateaux().remove(bateau);//1. couler le bateau
        posContamined.add(newPos); //2. ajouter la position de la mine à la liste des positions contaminées
        removeMine(m);//3. faire disparaitre mine
    }

    /**
     * Applique les dégats d'une mine classique aux bateaux concernés de la
     * flotille Une mine classique se comporte comme un tir adverse.
     *
     * @param m
     * @param newPos
     * @param bateau
     */
    private void traitementMineClassique(Armee a1, Mine m, Position newPos, Bateau bateau) {
        if (bateau instanceof PetitBateau) {
            a1.getBateaux().remove(bateau);
        } else {
            traitementMineClassique(a1, m, bateau);
        }
        removeMine(m); //supprime la mine de la liste des mines du jeu
    }

    private void traitementMineClassique(Armee a1, Mine m, Bateau bateau) {
        if (bateau.getIntegrite() - 50 <= 0) {
            a1.getBateaux().remove(bateau);
        } else {
            bateau.perdrePointsVie((byte) 50);
        }
    }

    public boolean conditionFin() {
        Armee a1 = armees.get(0);
        Armee a2 = armees.get(1);
        return (a1.getBateauxSize() > 0) && (a2.getBateauxSize() > 0);
    }

    /**
     * renvoie une list dont le premier element est l'arméee du bateau tireur et
     * le second l'armée du bateau touché
     *
     * @param bat
     * @return
     */
    public List<Armee> findArmeeToucheeAndTireuse(Bateau bat) {
        List<Armee> l = new ArrayList<>();
        if (armees.get(0).getBateaux().contains(bat)) { //armees.get(0) contient le bateau tireur
            l.add(0, armees.get(0));
            l.add(1, armees.get(1));
        } else {
            l.add(0, armees.get(1));
            l.add(1, armees.get(0));
        }
        return l;
    }

    /**
     * va chercher dans les 2 armées l'occurence d'un bateau et la remplace par
     * un nouveau bateau
     *
     * @param oldBateau
     * @param newBateau
     * @return
     */
    public boolean replace(Bateau oldBateau, Bateau newBateau) {
        boolean done = armees.get(0).findAndReplace(oldBateau, newBateau);
        if (done == false) {
            done = armees.get(1).findAndReplace(oldBateau, newBateau);
        }
        return done;
    }

    public Armee getFirstArmy() {
        return armees.get(0);
    }

    public Armee getSecondArmy() {
        return armees.get(1);
    }

    public void setChangedAndNotify(Object obj) {
        setChanged();
        notifyObservers(obj);
    }

    public int getFirstArmySize() {
        if (null == armees.get(0)) {
            return 0;
        }
        return armees.get(0).getNbBateaux();
    }

    public int getSecondArmySize() {
        if (null == armees.get(1)) {
            return 0;
        }
        return armees.get(1).getNbBateaux();
    }

    public Port getPort() {
        return port;
    }

    public void setPort(Port port) {
        this.port = port;
    }

    public int getFlotille1Size() {
        return port.getFlotille1().size();
    }

    public int getFlotille2Size() {
        return port.getFlotille2().size();
    }

    public int getFlotille1Elem(int index) {
        return this.port.getFlotille1Elem(index);
    }

    public int getFlotille2Elem(int index) {
        return this.port.getFlotille2Elem(index);
    }

    public void removeFlotille1Element(Integer elem) {
        port.removeFlotille1Elem(elem);
    }

    public void removeFlotille2Element(Integer elem) {
        port.removeFlotille2Elem(elem);
    }

    public boolean flotille1Contains(Integer elem) {
        return port.flotille1Contains(elem);
    }

    public boolean flotille2Contains(Integer elem) {
        return port.flotille2Contains(elem);
    }

    public Set<Bateau> getFirstArmyFlotille() {
        return getFirstArmy().getBateaux();
    }

    public Set<Bateau> getSecondArmyFlotille() {
        return getSecondArmy().getBateaux();
    }

    public String getFirstArmyName() {
        return armees.get(0).getNom();
    }

    public String getSecondArmyName() {
        return armees.get(1).getNom();
    }

    public void addFirstArmeeObserver(Vue vue) {
        armees.get(0).addObserver(vue);
    }

    public void addSecondArmeeObserver(Vue vue) {
        armees.get(1).addObserver(vue);
    }
}
