package Controller;

import Model.SeaBuilder.Mer;
import Vue.MainWindowRoot;
import Vue.SplashWindowRoot;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.stage.Stage;
import Model.Position;
import Model.Armee;
import Model.Bateau;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import Model.Helper;
import Model.SeaBuilder.Port;
import Model.SeaBuilder.SeaBuilder;

public class ControlleurIteration2 extends Application {
    private Stage stage;
    private Mer mer;
    private SeaBuilder sb;
    private Bateau bateauADeplacer;
    private boolean etatDeplacementBateau;
    private boolean etatTirer1; //au tour du joueur 1 de tirer
    private boolean etatTirer2;
    private boolean etatDeplacer1; // au tour de joueur 1 de déplacer 1 bateau
    private boolean etatDeplacer2;
    private boolean etatDemanderDeplacer1; //si le joueur souhaite déplacer un bateau après avoir tirer
    private boolean etatDemanderDeplacer2;
    private boolean etatManuel;
    private boolean etatPlacementManuel1;
    private boolean etatPlacementManuel2;
    private boolean etatBuildMerVide;
    private String tempBoatViewCssType; //big-boat ou small-boat
    private String tempBoatViewArmyType; //a1 ou a2
    private boolean endGame;

    public ControlleurIteration2() {
        etatTirer1 = true;
        endGame = false;
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        new SplashWindowRoot(stage, this); // Fenêtre initiale (saisie taille)
    }

    public static void main(String[] args) {
        final String[] s = {"aaa", "bbb"};
        //Mer mer = new Mer(s); // TODO utiliser l'autre constructeur de Mer
        launch(args);
    }

    //Fait apparaître la fenêtre principale de l'application
    public void switchToMainWindow(int size, String nom1, String nom2, boolean isAleatoire) {
        etatManuel = !isAleatoire;
        sb = new SeaBuilder(size, nom1, nom2, etatManuel);
        MainWindowRoot mainWindow = new MainWindowRoot(stage, size, this);
        initialiserMer(sb, mainWindow);
    }

    private void initialiserMer(SeaBuilder sb, MainWindowRoot mainWindow) {
        initialiserMer();
        addObserver(sb, mainWindow);
        setEtatBuildMerVide(false);
    }

    private void initialiserMer() {
        if (etatManuel) {
            setEtatBuildMerVide(true);
            setEtatPlacementManuel1(true);
        } else {
            mer = sb.build();
        }
    }

    private void addObserver(SeaBuilder sb, MainWindowRoot mainWindow) {
        sb.addObserver(mainWindow);
        sb.setChangedAndNotify();
    }

    private void placerBateau(boolean placementReussi, SeaBuilder sb, Integer currentElement, int armyTurn, int x, int y) {
        if (placementReussi) {
            if (armyTurn == 1) {
                sb.removeFlotille1Element(currentElement);
            } else if ((armyTurn == 2)) {
                sb.removeFlotille2Element(currentElement);
            }
            swapEtatPlacementManuel();
            sb.setChangedAndNotify();
        }
    }

    private void placerBateauFirstArmy(int x, int y) {
        Integer currentElement = new Integer(tempBoatViewCssType.equals("big-boat") ? 2 : 1);
        if (sb.flotille1Contains(currentElement)) {
            boolean placementReussi = sb.placerBateauManuel(tempBoatViewCssType, 1, x, y);
            placerBateau(placementReussi, sb, currentElement, 1, x, y);
        }
    }

    private void placerBateauSecondArmy(int x, int y) {
        Integer currentElement = new Integer(tempBoatViewCssType.equals("big-boat") ? 2 : 1);
        if (sb.flotille2Contains(currentElement)) {
            boolean placementReussi = sb.placerBateauManuel(tempBoatViewCssType, 2, x, y);
            placerBateau(placementReussi, sb, currentElement, 2, x, y);
        }
    }

    private void placerBateauxBothArmee(int x, int y) {
        //l'attribut tempBoatViewCssType nous permet de connaitre le type de bateau 
        //(big-boat ou small-boat)
        //Maintenant, nous récupérons les coordonnées de l'EmptyBox clicked (x, y)
        //Nous allons demander au SeaBuilder de demander à la mer d'ajouter un bateau
        if (isEtatPlacementManuel1() && tempBoatViewArmyType.equals("a1")) {
            placerBateauFirstArmy(x, y);
        } else if (isEtatPlacementManuel2() && tempBoatViewArmyType.equals("a2")) {
            placerBateauSecondArmy(x, y);
        }
        if (sb.getFirstArmySize() == 3 && sb.getSecondArmySize() == 3) {
            initaliserPartie();
        }
    }

    /**
     * La mer est construite, le jeu peut commencer *
     */
    private void initaliserPartie() {
        setEtatPlacementManuel1(false);
        setEtatPlacementManuel2(false);
        mer = sb.build(); // le controlleur reçoit une mer construite
        mer.setChangedAndNotify(); //todo
        setEtatTirer1(true);
    }

    /**
     * Place manuelement les bateau et initalise la partie
     */
    private void placerManuelInitialiser(int x, int y) {
        placerBateauxBothArmee(x, y);
        if (sb.getFirstArmySize() == 3 && sb.getSecondArmySize() == 3) {
            initaliserPartie();
        }
        sb.setChangedAndNotify();
    }

    private boolean isConditionPlacementManeul() {
        if (tempBoatViewArmyType != null && isEtatPlacementManuel()) {
            if (sb.getSecondArmySize() < 3) {
                return true;
            }
        }
        return false;
    }

    // Quand l'utilisateur clique sur une case vide
    public void emptyBoxClicked(int x, int y) {
        if (etatDeplacementBateau && (!etatPlacementManuel1 && !etatPlacementManuel2)) {
            //mer.setXY(x, y); // Déplace le bateau
            etatDeplacementBateau = false;
        } else if (isConditionPlacementManeul()) {
            placerManuelInitialiser(x, y);
        }
    }

    private boolean isConditionPlacementManuel() {
        if (!endGame) {
            if (etatPlacementManuel1 || etatPlacementManuel2) {
                return true;
            }
        }
        return false;
    }

    private void executeBoatClicked(int x, int y) {
        Armee armee = defineArmyTurn().get(0);
        Bateau batClicked = getBatClicked(x, y);
        boatClicked(x, y, armee, batClicked);
    }

    private void setTempBoatAttributes(String cssType, String cssArmy) {
        tempBoatViewCssType = cssType; //big-boat ou small-boat
        tempBoatViewArmyType = cssArmy;
    }

    /**
     * Quand l'utilisateur clique sur un bateau (soit lors du placement initiale
     * des bateau, soit lorsque le jeu à débuté)
     */
    public void boatClicked(int x, int y, String cssType, String cssArmy) {
        if (!endGame && (!isEtatPlacementManuel())) {
            executeBoatClicked(x, y);
        } else if (isConditionPlacementManuel()) {
            // L'utilisateur vient de cliquer sur un bateau à placer manuelement dans la mer
            // Nous avons changer l'etat etatPlacementManuel1 ou 2 et nous assignons le type css dans un attribut du controleur
            setTempBoatAttributes(cssType, cssArmy);
            // Nous devons attendre que le user clique sur une emptybox. La suite du programme se passera dans emptyBoxclicked          
        }
    }

    private void boatClicked(int x, int y, Armee armee, Bateau batClicked) {
        if (armee.getBateaux().contains(batClicked)) { //on agit que si le joueur clique sur un bateau appartenant à son armée
            if (etatTirer1 || etatTirer2) {
                boatClicked(batClicked);
            } else if ((isEtatDeplacer1() || isEtatDeplacer2()) && armee.getBateaux().contains(batClicked)) {
                etatDeplacerExecution(x, y);
            }
        }
    }

    private void boatClicked(Bateau batClicked) {
        byte portee = batClicked.tirer();
        //ici il faudra afficher la portee
        if (portee > 0) {
            List<Armee> l = mer.findArmeeToucheeAndTireuse(batClicked); // l = armee touchee et armee tireuse
            Armee armeeTouchee = mer.tirerAction(l.get(0), l.get(1), portee, batClicked.getPos());
        }
        boatClicked();
    }

    private void boatClicked() {
        swapEtatDemanderDeplacer(); //on a tirer, il faut demander s'il veut déplacer 1 bateau
        checkEndGame();
        mer.setChangedAndNotify(new Integer(2)); // code 2 = on est dans létat demander déplacer
    }

    /**
     * Action a faire une fois que l'utilisateur a cliqué sur le bouton oui ou
     * non
     *
     * @param deplacer : choix de l'utilisateur (veut-il déplacer ?)
     */
    public void buttonDeplacerClicked(boolean deplacer) {
        if (deplacer) { // si le joueur veut déplacer son bateau, on change met etatDeplacer  a true
            swapEtatDeplacer();
            mer.setChangedAndNotify(); // le message bottom doit être mis à jour
        } else { // sinon on met etatTirer à true
            swapEtatTirer();
            mer.setChangedAndNotify(); // le message bottom doit être mis à jour
        }
    }

    /**
     * Trouve les positions de déplacement possible pour un bateau et appel la
     * méthode update de mainWindowRoot
     *
     * @param x
     * @param y
     */
    private void etatDeplacerExecution(int x, int y) {
        Bateau batClicked = getBatClicked(x, y);
        setBateauADeplacer(batClicked);
        Set<Position> posPossible = mer.posPossibles(batClicked, mer.getFirstArmy(), mer.getSecondArmy());
        Set<Position> posPossibleCroix = mer.posPossiblesCroix(batClicked, posPossible);
        etatDeplacerExecution(posPossibleCroix);
    }

    private void etatDeplacerExecution(Set<Position> posPossibleCroix) {
        List<Position> listPos = new ArrayList<>();
        listPos.addAll(posPossibleCroix);
        mer.setChangedAndNotify(listPos);
    }

    /**
     * Définit l'armée ayant la main du jeu (à qui est-ce le tour de jouer ?)
     * L'armee qui a la main se trouvera en 1er indice de arrayList<>()
     *
     * @return
     */
    private List<Armee> defineArmyTurn() {
        Armee a1, a2;
        if ((this.isEtatDeplacer1() || this.isEtatTirer1() || this.isEtatDemanderDeplacer1())) {
            a1 = mer.getFirstArmy();
            a2 = mer.getSecondArmy();
        } else {
            a1 = mer.getSecondArmy();
            a2 = mer.getFirstArmy();
        }
        return defineArmyTurn(a1, a2);
    }

    private List<Armee> defineArmyTurn(Armee a1, Armee a2) {
        List<Armee> l = new ArrayList<>();
        l.add(a1);
        l.add(a2);
        return l;
    }

    /**
     * Retourne le bateau sur lequel on clique soit pour tirer soit pour le
     * séléctionné avant déplacement
     *
     * @param x
     * @param y
     * @return
     */
    private Bateau getBatClicked(int x, int y) {
        Armee a1 = defineArmyTurn().get(0); //peut être joueur 1 ou joueur 2 -> dépend de qui à la main
        Position posClicked = new Position((byte) (y), Helper.toChar((byte) (x)));
        return a1.getBateauByPosition(posClicked);
    }

    /**
     * Méthode appelée quand user clique sur une case violette pour déplacer son
     * bateau pré-séléctionné
     *
     * @param x
     * @param y
     */
    public void deplacementBoxClicked(int x, int y) {
        Position posClicked = new Position((byte) (y), Helper.toChar((byte) (x)));
        deplacerBateau(posClicked);
        swapEtatTirer();
        mer.setChangedAndNotify();
    }

    /**
     * appel les méthode necessaire de la classe Mer et de la classe Armee pour
     * déplacer un bateau
     *
     * @param posClicked : la position de la case violette innocupée à atteindre
     * par un bateau
     */
    private void deplacerBateau(Position posClicked) {
        Bateau newBateau = bateauADeplacer.copy(posClicked, bateauADeplacer.getIntegrite());
        mer.replace(bateauADeplacer, newBateau);
    }

    /**
     * Met l'état tirer necessaire à true
     */
    private void swapEtatTirer() {
        if (isEtatTirer1() || isEtatDeplacer2() || isEtatDemanderDeplacer2()) {
            swapEtatTirer1();
        } else if (isEtatTirer2() || isEtatDeplacer1() || isEtatDemanderDeplacer1()) {
            swapEtatTirer2();
        }
    }

    private void swapEtatTirer1() {
        setEtatTirer1(true);
        setEtatTirer2(false);
        setEtatDeplacer2(false);
        setEtatDemanderDeplacer2(false);
    }

    private void swapEtatTirer2() {
        setEtatTirer1(false);
        setEtatTirer2(true);
        setEtatDeplacer1(false);
        setEtatDemanderDeplacer1(false);
    }

    private void swapEtatDemanderDeplacer() {
        if (isEtatTirer1()) {
            setEtatDemanderDeplacer1(true);
            setEtatTirer1(false);
        } else if (isEtatTirer2()) {
            setEtatDemanderDeplacer2(true);
            setEtatTirer2(false);
        }
    }

    private void swapEtatDeplacer() {
        if (isEtatDemanderDeplacer1()) {
            setEtatDeplacer1(true);
            setEtatDemanderDeplacer1(false);
        } else if (isEtatDemanderDeplacer2()) {
            setEtatDeplacer2(true);
            setEtatDemanderDeplacer2(false);
        }
    }

    private void swapEtatPlacementManuel() {
        if (isEtatPlacementManuel1()) {
            setEtatPlacementManuel1(false);
            setEtatPlacementManuel2(true);
        } else if (isEtatPlacementManuel2()) {
            setEtatPlacementManuel1(true);
            setEtatPlacementManuel2(false);
        }
    }

    private void checkEndGame() {
        if (!mer.conditionFin()) { //TODO changer le nom de cette méthode
            setEndGame(true);
        }
    }

    private void setBateauADeplacer(Bateau bateauADeplacer) {
        this.bateauADeplacer = bateauADeplacer;
    }

    public boolean isEtatTirer1() {
        return etatTirer1;
    }

    private void setEtatTirer1(boolean etatTirer1) {
        this.etatTirer1 = etatTirer1;
    }

    public boolean isEtatTirer2() {
        return etatTirer2;
    }

    private void setEtatTirer2(boolean etatTirer2) {
        this.etatTirer2 = etatTirer2;
    }


    public boolean isEtatDeplacer1() {
        return etatDeplacer1;
    }

    private void setEtatDeplacer1(boolean etatDeplacer1) {
        this.etatDeplacer1 = etatDeplacer1;
    }

    public boolean isEtatDeplacer2() {
        return etatDeplacer2;
    }

    private void setEtatDeplacer2(boolean etatDeplacer2) {
        this.etatDeplacer2 = etatDeplacer2;
    }

    public boolean isEtatDemanderDeplacer1() {
        return etatDemanderDeplacer1;
    }

    private void setEtatDemanderDeplacer1(boolean etatDemanderDeplacer1) {
        this.etatDemanderDeplacer1 = etatDemanderDeplacer1;
    }

    public boolean isEtatDemanderDeplacer2() {
        return etatDemanderDeplacer2;
    }

    private void setEtatDemanderDeplacer2(boolean etatDemanderDeplacer2) {
        this.etatDemanderDeplacer2 = etatDemanderDeplacer2;
    }

    public boolean isEtatPlacementManuel1() {
        return etatPlacementManuel1;
    }

    private void setEtatPlacementManuel1(boolean etatPlacementManuel1) {
        this.etatPlacementManuel1 = etatPlacementManuel1;
    }

    public boolean isEtatPlacementManuel2() {
        return etatPlacementManuel2;
    }

    private void setEtatPlacementManuel2(boolean etatPlacementManuel2) {
        this.etatPlacementManuel2 = etatPlacementManuel2;
    }

    public boolean isEndGame() {
        return endGame;
    }

    private void setEndGame(boolean endGame) {
        this.endGame = endGame;
    }

    public Mer getMer() {
        return mer;
    }

    public Armee getFirstArmy() {
        return sb.getFirstArmy();
    }

    public Armee getSecondArmy() {
        return sb.getSecondArmy();
    }

    public boolean isEtatBuildMerVide() {
        return etatBuildMerVide;
    }

    private void setEtatBuildMerVide(boolean etatBuildMerVide) {
        this.etatBuildMerVide = etatBuildMerVide;
    }

    public int getFlotille1Size() {
        return sb.getFlotille1Size();
    }

    public int getFlotille2Size() {
        return sb.getFlotille2Size();
    }

    public int getFlotille1Elem(int index) {
        return sb.getFlotille1Elem(index);
    }

    public int getFlotille2Elem(int index) {
        return sb.getFlotille2Elem(index);
    }

    public int getFirstArmySize() {
        return mer.getFirstArmySize();
    }

    public int getSecondArmySize() {
        return mer.getSecondArmySize();
    }

    public boolean isEtatManuel() {
        return etatManuel;
    }

    public String getFirstArmyName() {
        return sb.getFirstArmyName();
    }

    public String getSecondArmyName() {
        return sb.getSecondArmyName();
    }

    private boolean isEtatPlacementManuel() {
        return (isEtatPlacementManuel1() || etatPlacementManuel2);
    }

}
