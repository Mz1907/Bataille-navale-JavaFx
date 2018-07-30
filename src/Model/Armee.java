package Model;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import Vue.Vue;

public final class Armee extends Observable {

    private final String nom;
    private final Set<Bateau> bateaux = new HashSet<>();
    private final int num; // définit s'il s'agit du joueur 1 ou du joueur 2

    public Armee(String nom, int num, boolean isAleatoire) {
        this.nom = nom;
        this.num = num;
        if (!isAleatoire) {
            ajouterLesBateaux();
        }
        this.setChangedAndNotify(this);
    }

    private void ajouterLesBateaux() {
        bateaux.add(new GrandBateau(new Position((byte) 1, 'a'), (byte) 100));
        bateaux.add(new PetitBateau(new Position((byte) 1, 'b'), (byte) 100));
        bateaux.add(new PetitBateau(new Position((byte) 1, 'c'), (byte) 100));
    }

    /**
     * Défini 3 positions differentes n'étant pas occupé par l'adversaire
     *
     * @return Set<Position>
     */
    public Set<Position> definirPositions(byte dimension, Set<Position> posAdversaire) {
        Set<Position> positions = new TreeSet<>();
        return addPosition(dimension, positions, posAdversaire);
    }

    private Set<Position> addPosition(byte dimension, Set<Position> positions, Set<Position> posAdversaire) {
        while (positions.size() < 3) {
            byte li = (byte) Helper.rand(dimension);
            char co = Helper.toChar((byte) Helper.rand(dimension));
            addPosition(new Position(li, co), positions, posAdversaire);
        }
        return positions;
    }

    private void addPosition(Position temp, Set<Position> positions, Set<Position> posAdversaire) {
        if (!posAdversaire.contains(temp)) {
            positions.add(temp);
        }
    }

    /**
     * Déplacer un bateau consiste à créer un nouveau bateau avec l'attribut
     * position modifié On crée un nouveau bateau pour ne pas travailler
     * directement dans le Set Assigne une position initiale en début de parti.
     *
     * @param positionsInitiales : Set renvoyé par la méthode definirPositions()
     * ci-dessus
     */
    public void placerBateaux(Set<Position> positionsInitiales) {
        int nbBateaux = bateaux.size();
        bateaux.clear();
        placerBateaux(nbBateaux, positionsInitiales);
    }

    private void placerBateaux(int nbBateaux, Set<Position> positionsInitiales) {
        for (int i = 0; i < nbBateaux; ++i) {
            if (i == 2) {
                Bateau newBateau = new GrandBateau((Position) positionsInitiales.toArray()[i], (byte) 100);
                bateaux.add(newBateau);
            } else if (i < 2) {
                Bateau newBateau = new PetitBateau((Position) positionsInitiales.toArray()[i], (byte) 100);
                bateaux.add(newBateau);
            }
        }
    }

    public boolean placerBateauxManuel(String tempBoatViewCssType, int x, int y, Set<Bateau> bateauxAdverse) {
        Position posBateau = new Position((byte) y, Helper.toChar((byte) x));
        
        if (tempBoatViewCssType.equals("big-boat")) {
            GrandBateau bat = new GrandBateau(posBateau, (byte) 100);
            if (!bateaux.contains(bat) && !bateauxAdverse.contains(bat)) {
                
                bateaux.add(bat);
                return true;
            }
        } else {
            PetitBateau bat = new PetitBateau(posBateau, (byte) 100);
            if (!bateaux.contains(bat) && !bateauxAdverse.contains(bat)) {
                bateaux.add(bat);
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param positionsImpactees
     * @param tireur : le bateau tireur
     * @param armee
     */
    public Armee traitementTorpille(Set<Position> posImpactees, Armee armeeAdverse, Position posTireur) {
        Set<Position> intersec = getBateauxPositions(); //ensemble des Positions des bateaux des 2 armées
        intersec.retainAll(posImpactees); //parmis ces positions, nous ne retenons que celles impactées par le tir.
        return updateIntegrite(intersec, armeeAdverse, posTireur);
    }

    /**
     * on va créer une liste, la secouer, et en retirer le premier élement pour
     * obtenir un bateau au hasard. On fait ceci car un seul bateau de la list
     * des bat touchés peut réelement être impacté
     *
     * @return : le bateau tiré au hasard qui sera impacté par le tir du "Petit
     * batau tireur"
     */
    private Bateau bateauHasard(List<Bateau> listeBateaux) {
        Collections.shuffle(listeBateaux);
        return listeBateaux.get(0);
    }

    /**
     * Assigne une integrite de 50 au bateau donné en param, qu'il soit grand ou
     * petit
     *
     * @param batHasard
     */
    private void traiteBateauHasardIntegrite(Bateau batHasard) {
        bateaux.remove(batHasard);
        batHasard.setIntegrite((byte) 50);
        bateaux.add(batHasard);
    }

    /**
     * Un grand bateau vient d'être touché, Contrairement à un petit bateau, on
     * ne supprime pas forcement un grand bateau Il faut vérifier son integrite
     * avant de le supprimer éventuelement
     *
     * @param batHasard
     */
    private void traiterBateauHasardGrandBateau(Bateau batHasard) {
        if (batHasard.getIntegrite() - 50 <= 0) {
            bateaux.remove(batHasard);
        } else {
            traiteBateauHasardIntegrite(batHasard);
        }
    }

    /**
     * On a sécoué et selectionné un bateau hasard suite à un tir de petit
     * bateau Un grand bateau fut selectionné pour subir des degats Nous
     * traitons ici ce grand bateau
     *
     * @param aSupprimer
     * @param bat
     */
    private void traiterBateauHasardGrandBateau(Set<Bateau> aSupprimer, Bateau bat) {
        if (bat.getIntegrite() - 50 <= 0) {
            aSupprimer.add(bat);
        } else {
            bat.perdrePointsVie((byte) 50);
        }
    }

    /**
     *
     * @param intersec : ensemble des positions impactées par un tir et sur
     * lesquelles se trouvent effectivement des bateaux adverses
     * @param bat
     * @param aSupprimer
     */
    private void executerDegatsIntersec(Set<Position> intersec, Bateau bat, Set<Bateau> aSupprimer) {
        if (intersec.contains(bat.getPos())) { //intersec est Set<Position>
            if (bat instanceof PetitBateau) { // true
                aSupprimer.add(bat);
            } else {
                traiterBateauHasardGrandBateau(aSupprimer, bat);
            }
        }
    }

    /**
     * Un petit bateau a tiré. Parmis les bateaux impactés on en selectionne 1.
     * Ce bateau selectionné va subir des dégats. Si c'est un petit on le
     * supprime Si c'est un grand bateau, on renvoi vers une fonction qui traite
     * les degats
     */
    private void traiterBateauHasardTireurPetitBat(Set<Position> intersec) {

        if (intersec.size() > 0) { //on avance que si la liste contient effectivement des Positions impactées.
            // mais il faudrait seulement prendre un bateau se trouvant sur les positions impactées.
            List<Bateau> aSupprimer = new ArrayList<>(); // liste des bateaux a supprimer
            for (Position pos : intersec) {
                if (bateaux.contains(getBateauByPosition(pos))) {
                    //BUG petit bateau
                    aSupprimer.add(getBateauByPosition(pos));
                }
            }
            Bateau batHasard = bateauHasard(aSupprimer); //Ici on prend un bateau au hasard parmis les bateaux de l'armée 
            if (batHasard instanceof PetitBateau) {
                bateaux.remove(batHasard);
            } else {
                traiterBateauHasardGrandBateau(batHasard);
            }
        }
    }

    /**
     *
     * @param intersec : ensemble des positions impactées par un tir et sur
     * lesquels se trouvent effectivement des bateaux impactés (uniquement
     * utilisé si tireur instanceof GrandBateau)
     * @param tireur : Bateau tireur
     * @param aSupprimer :
     */
    private void updateIntegrite(Set<Position> intersec, Bateau tireur, Set<Bateau> aSupprimer) {
        if (tireur instanceof PetitBateau) {
            traiterBateauHasardTireurPetitBat(intersec); //BUG
        } else { // tireur instanceof Grand bateau
            traiterBateauHasardTireurGrandBat(intersec, aSupprimer);
        }
    }

    /**
     * Le tireur est un grand bateau. Il risque d'y avoir plusieurs bateau
     * impactés
     *
     * @param intersec : ensemble des positions impactées par un tir et sur
     * lesquels se trouvent effectivement des bateaux adverse
     * @param aSupprimer
     */
    private void traiterBateauHasardTireurGrandBat(Set<Position> intersec, Set<Bateau> aSupprimer) {
        for (Bateau bat : bateaux) {
            executerDegatsIntersec(intersec, bat, aSupprimer);
        }
        bateaux.removeAll(aSupprimer);
    }

    /**
     *
     * @param intersec : ensemble des positions impactées par un tir et sur
     * lesquels se trouvent effectivement des bateaux éligibles à subir des
     * dégats
     * @param armeeAdverse : armee du bateau tireur
     * @param posTireur : position du bateau tireur
     * @return
     */
    private Armee updateIntegrite(Set<Position> intersec, Armee armeeAdverse, Position posTireur) {
        Bateau tireur = armeeAdverse.getBateauByPosition(posTireur);
        updateIntegrite(intersec, tireur, new HashSet<>());
        setChangedAndNotify(this);
        return this;
    }

    public void updatePosition(Position newPos, Bateau bateau) {
        byte integrite = bateau.getIntegrite();
        bateaux.remove(bateau);
        updatePosition(newPos, bateau, integrite);
    }

    private void updatePosition(Position newPos, Bateau bateau, byte integrite) {
        if (bateau instanceof PetitBateau) {
            bateaux.add(new PetitBateau(newPos, integrite));
        } else if (bateau instanceof GrandBateau) {
            bateaux.add(new GrandBateau(newPos, integrite));
        }
    }

    /**
     * Renvoi un map <Position, Bateau> de tout les bateaux non coulés de cette
     * arméee
     */
    public Map<Position, Bateau> getflottillePositions() {
        Map<Position, Bateau> flotillePositions = new HashMap<>();

        for (Bateau bat : bateaux) {
            if (bat.getIntegrite() > 0) {
                flotillePositions.put(bat.getPos(), bat);
            }
        }
        return flotillePositions;
    }

    public Set<Bateau> getBateaux() {
        return bateaux;
    }

    /**
     * Retourne un Set<position> contenant les positions de chaque bateaux de la
     * flotille
     *
     * @return
     */
    public Set<Position> getBateauxPositions() {
        Set<Position> bateauxPositions = new HashSet<>();
        for (Bateau bat : bateaux) {
            bateauxPositions.add(bat.getPos());
        }
        return bateauxPositions;
    }

    public int getNum() {
        return num;
    }

    public String getNom() {
        return nom;
    }

    public Bateau getBateau(Position pos) {
        for (Bateau bat : bateaux) {
            if (bat.getPos().equals(pos)) {
                return bat;
            }
        }
        return null;
    }

    public Bateau getBateauByPosition(Position pos) {
        for (Bateau bat : bateaux) {
            if (bat.getPos().equals(pos)) {
                return bat;
            }
        }
        return null;
    }

    public int getBateauxSize() {
        return bateaux.size();
    }

    private void setChangedAndNotify(Object obj) {
        setChanged();
        this.notifyObservers(obj);
    }

    /**
     * trouve un bateau et le replace
     *
     * @return
     */
    public boolean findAndReplace(Bateau oldBateau, Bateau newBateau) {
        boolean replaced = false;
        if (bateaux.contains(oldBateau)) {
            bateaux.remove(oldBateau);
            bateaux.add(newBateau);
            return true;
        }
        return replaced;
    }

    public int getNbBateaux() {
        return bateaux.size();
    }    
    
}
