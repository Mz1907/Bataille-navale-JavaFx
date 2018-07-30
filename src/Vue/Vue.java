package Vue;

import Controller.Controller;
import java.util.Scanner;
import Model.Position;
import Model.Armee;
import Model.Bateau;
import Model.GrandBateau;
import Model.Helper;
import Model.SeaBuilder.Mer;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

public class Vue implements Observer {

    private final static Scanner SCANNER = new Scanner(System.in);
    private Mer mer;

    public Vue() {
        this.mer = mer;
    }

    public static StringBuilder afficherEtatArmee(Armee armee) {
        Set<Bateau> bateaux = armee.getBateaux();
        StringBuilder sb = new StringBuilder();
        for (Bateau bat : bateaux) {
            sb.append(" " + bat.getPos()).append("             ");
            if (bat instanceof GrandBateau) {
                sb.append("BIG      ");
            } else {
                sb.append("SMALL");
            }
            sb.append("  " + bat.getIntegrite());
            sb.append(System.getProperty("line.separator"));
        }
        return sb;
    }

    public void afficherEtat(Armee a1, Armee a2) {
        String espaceAjouter = Helper.calculerEspace(a1.getNom(), a2.getNom());
        StringBuilder sb = new StringBuilder();
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("Etat des armées");
        sb.append(System.getProperty("line.separator"));
        sb.append("Position   Armée").append(espaceAjouter).append("  Type  Integrite (%)");
        System.out.println(sb);
        afficherArmee(a1, espaceAjouter, (a1.getNom().length() < a2.getNom().length()));
        afficherArmee(a2, espaceAjouter, (a2.getNom().length() < a1.getNom().length()));
    }

    public boolean saisirDeplacerBateau() {
        String entree;
        boolean res = false;
        do {
            System.out.println("Déplacement bateau : voulez déplacer un bateau ?  y/n ");
            entree = SCANNER.next().toLowerCase();
            if (entree.equals("n")) {
                res = false;
            } else if (entree.equals("y")) {
                res = true;
            }
        } while (!entree.equals("n") && !entree.equals("y"));
        return res;
    }

    public Position saisirNouvellePosition(String message, Armee armee) {
        boolean validLi = false, validCo = false;
        byte li = 0;
        char co = '\0';
        String entree, liEntree, coEntree;
        Position p;

        do {
            System.out.println(message);
            entree = SCANNER.next();

            if (entree.length() == 2) {
                coEntree = entree.substring(0, 1);
                liEntree = entree.substring(1);

                //test si li est un byte
                if (Helper.isByte(liEntree)) {
                    li = Byte.parseByte(liEntree);
                    //test si li est compatible avec les dimension du plateau
                    if (Helper.isLignePresent(li, 5)) {
                        validLi = true;
                    }
                }
                if (validLi == true) { // on ne valide co que si la validation de li est TRUE
                    if (Helper.isLetter(coEntree)) {
                        coEntree = coEntree.toLowerCase();
                        co = coEntree.charAt(0);
                        if (Helper.isColonnePresent(co + "", 5)) { //cast de co en string via concatenation
                            validCo = true;
                        }

                    }
                }
            }
        } while (validLi == false || validCo == false);

        return new Position(li, co);
    }

    public Position saisirBateau(String message, Armee armee) {
        boolean validLi = false, validCo = false, bateauExist = false; //bateau existe = bateau présent dans Set<Bateau> bateaux de armee
        byte li = 0;
        char co = '\0';
        String entree, liEntree, coEntree;
        Position p;

        do {
            System.out.println(message);
            entree = SCANNER.next();

            if (entree.length() == 2) {
                coEntree = entree.substring(0, 1);
                liEntree = entree.substring(1);

                //test si li est un byte
                if (Helper.isByte(liEntree)) {
                    li = Byte.parseByte(liEntree);
                    //test si li est compatible avec les dimension du plateau
                    if (Helper.isLignePresent(li, 5)) {
                        validLi = true;
                    }
                }
                if (validLi == true) { // on ne valide co que si la validation de li est TRUE
                    if (Helper.isLetter(coEntree)) {
                        coEntree = coEntree.toLowerCase();
                        co = coEntree.charAt(0);
                        if (Helper.isColonnePresent(co + "", 5)) { //cast de co en string via concatenation
                            validCo = true;
                            p = new Position(li, co);
                            Set<Position> bateauxArmee = armee.getBateauxPositions();
                            bateauExist = bateauxArmee.contains(p);
                        }

                    }
                }
            }
        } while (validLi == false || validCo == false || bateauExist == false);

        return new Position(li, co);
    }

    public String[] nomArmeeSaisi() {
        String nomsArmee[] = {null, null};
        String string;
        for (int i = 1; i < 3; i++) {
            Scanner SCANNER = new Scanner(System.in);
            boolean validNom = false;
            do {
                afficherMessage("Nom de l'armee n° " + i + " (entre 1 et 10 charactères) : ");
                string = SCANNER.next();
                if ((string.length() >= 1) && (string.length() <= 10)) {
                    if (i == 2) { //on vérifie que user n'entre pas 2 noms identiques
                        if (!nomsArmee[0].equals(string)) {
                            validNom = true;
                        } else {
                            System.out.println("Veuillez choisir un nom different de armee 1");
                        }

                    } else {
                        validNom = true;
                    }
                    nomsArmee[i - 1] = string;
                }
            } while (validNom == false);
        }
        return nomsArmee;
    }

    public void afficherMessage(String msg) {
        System.out.print(msg);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof Mer) {
            Mer mer = (Mer) o;
            afficherMer((byte) mer.getSIZE());
        } else if (o instanceof Armee) {
            afficherEtat(mer.getFirstArmy(), mer.getSecondArmy());
        }
    }

    public void afficherGagnant() {
        if (mer.getFirstArmy().getBateaux().size() == 0) {
            System.out.println("Bravo " + mer.getSecondArmy().getNom() + " Vous avez gagné");
        } else {
            System.out.println("Bravo " + mer.getFirstArmy().getNom() + " Vous avez gagné");
        }
    }

    public void afficherMer(byte sizeMer) {
        Map<Position, Bateau> positionflotilleA1 = mer.getFirstArmy().getflottillePositions();
        Map<Position, Bateau> positionflotilleA2 = mer.getSecondArmy().getflottillePositions();

        for (int i = 0; i <= sizeMer; i++) {
            for (int j = 0; j <= sizeMer; j++) {
                if (i == 0 && j < sizeMer) {
                    if (j == 0) {
                        System.out.print("  ");
                    }
                    //on affiche les titres de colonnes
                    System.out.print(" ");
                    System.out.print(("" + Helper.toChar((byte) (j + 1))).toUpperCase());
                } else if (i > 0) {
                    if (j == 0) {
                        System.out.println("  ");
                        System.out.print(i); // on affiche le titre des lignes
                        System.out.print(" ");
                    } else {
                        System.out.print("|");
                        Position currentPos = new Position((byte) i, Helper.toChar((byte) j));
                        if (positionflotilleA1.containsKey(currentPos)) {
                            System.out.print(mer.getFirstArmy().getNum() == 1 ? "" + Couleur.CYAN : "" + Couleur.RED);
                            System.out.print(positionflotilleA1.get(currentPos));
                            System.out.print("" + Couleur.RESET);
                        } else if (positionflotilleA2.containsKey(currentPos)) {
                            System.out.print(mer.getSecondArmy().getNum() == 1 ? "" + Couleur.CYAN : "" + Couleur.RED);
                            System.out.print(positionflotilleA2.get(currentPos));
                            System.out.print("" + Couleur.RESET);
                        } else {
                            System.out.print(" ");
                        }
                        if (j == sizeMer) {
                            System.out.print("|");
                        }

                    }
                }
            }
        }
        System.out.println("");
    }

    /**
     *
     * @param positionsPossibles
     */
    public void afficherMerPositionPossibles(Set<Position> positionsPossibles, byte size) {
        for (int i = 0; i <= size; i++) {
            for (int j = 0; j <= size; j++) {
                if (i == 0 && j < size) {
                    if (j == 0) {
                        System.out.print("  ");
                    }
                    //on affiche les titres de colonnes
                    System.out.print(" ");
                    System.out.print(("" + Helper.toChar((byte) (j + 1))).toUpperCase());
                } else if (i > 0) {
                    if (j == 0) {
                        System.out.println("  ");
                        System.out.print(i);
                        System.out.print(" ");
                    } else {
                        System.out.print("|");
                        Position currentPos = new Position((byte) i, Helper.toChar((byte) j));
                        if (positionsPossibles.contains(currentPos)) {
                            System.out.print(Couleur.GREEN + "x" + Couleur.RESET);
                        } else {
                            System.out.print(" ");
                        }
                        if (j == size) {
                            System.out.print("|");
                        }
                    }
                }
            }
        }
        System.out.println("");
    }

    public void afficherArmee(Armee armee, String espaceAjouter, boolean doitAjouter) {
        Set<Bateau> bateaux = armee.getBateaux();
        StringBuilder sb = new StringBuilder();
        for (Bateau bat : bateaux) {
            sb.append(armee.getNum() == 1 ? "" + Couleur.CYAN : "" + Couleur.RED);
            sb.append(bat.getPos()).append("         ");
            if (doitAjouter) {
                sb.append(armee.getNom()).append(" ").append(espaceAjouter);
            } else {
                sb.append(armee.getNom()).append(" ");
            }
            sb.append(bat instanceof GrandBateau ? "BIG   " : "SMALL ");
            sb.append(bat.getIntegrite());
            sb.append("").append(Couleur.RESET);
            sb.append(System.getProperty("line.separator"));
        }
        System.out.println(sb.toString());
    }

    public Position saisirPositionDeplacement(Mer mer, Armee a1, Armee a2, Position posBateauDeplacer) {
        afficherPosDispo(a1.getBateauByPosition(posBateauDeplacer), mer); //on affiche les positions disponibles pour ce bateau
        boolean newPosValidation = false; //on va récupérer la nouvelle position désirée et on la valide
        Position newPos;
        do {
            newPos = saisirNouvellePosition("Sélectionnez positions disponibles pour déplacement (contenant une croix)", a2);
            newPosValidation = validerPrositionUser(newPos, a1.getBateauByPosition(posBateauDeplacer), a1, a2, mer);
            if (!newPosValidation) {
                System.out.println("Position non-valide");
            }
        } while (!newPosValidation);
        //mer.setChangedAndNotify(mer);
        return newPos;
    }

    public void afficherPosDispo(Bateau bat, Mer mer) {
        afficherMerPositionPossibles(mer.posPossibles(bat, mer.getFirstArmy(), mer.getSecondArmy()), (byte) mer.getSIZE());
    }

    public boolean validerPrositionUser(Position propositionUser, Bateau bat, Armee a1, Armee a2, Mer mer) {
        Set<Position> posPossibles = mer.posPossibles(bat, a1, a2);
        return posPossibles.contains(propositionUser);
    }

    public void setMer(Mer mer) {
        this.mer = mer;
    }

}
