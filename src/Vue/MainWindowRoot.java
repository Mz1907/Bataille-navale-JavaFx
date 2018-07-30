package Vue;

import Controller.ControlleurIteration2;
import java.util.Observable;
import java.util.Observer;
import javafx.scene.Scene;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import java.util.List;
import Model.Position;
import Model.Armee;
import Model.Bateau;
import Model.GrandBateau;
import Model.PetitBateau;
import Model.Helper;
import javafx.event.Event;
import javafx.event.EventHandler;
import static javafx.geometry.Pos.CENTER;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class MainWindowRoot extends BorderPane implements Observer {

    private final int SIZE;
    private final ControlleurIteration2 control;
    private GridPane gridPane;
    private Button buttonYes = new Button("Oui");
    private Button buttonNo = new Button("Non");

    public MainWindowRoot(Stage stage, int size, ControlleurIteration2 control) {
        this.control = control;
        SIZE = size;
        buildJFXComponents(stage);
    }

    private void buildJFXComponents(Stage stage) {
        buildSideBars(); // Construction des sidebars droites et gauches
        buildGridPane(); //Plateau central
        buildVBoxBottom();
        buildBottomButtons();
        setStage(stage);
    }

    private Label buildLabelLeft1() {
        Label lLeft1 = new Label(control.getFirstArmyName());
        lLeft1.setStyle("-fx-alignment: center; -fx-border-color: black; -fx-border-width: 1;-fx-padding:2px;-fx-text-fill: blue;-fx-border-insets: 8px;");
        lLeft1.setMinWidth(154);
        return lLeft1;
    }

    private Label buildLabelLeft2() {
        Label lLeft2 = new Label("Position   Type      Integrite (%)" + "\n" + Vue.afficherEtatArmee(control.getFirstArmy()));
        lLeft2.setStyle("-fx-alignment: center; -fx-border-width: 1;-fx-padding:10px;-fx-text-fill: blue;");
        return lLeft2;
    }

    private VBox addSecondLabelVbleft(VBox vbLeft) {
        if (!(control.isEtatManuel())) {
            Label lLeft2 = buildLabelLeft2();
            vbLeft.getChildren().addAll(lLeft2);
            return vbLeft;
        }
        return vbLeft;
    }

    private VBox buildVbLeft(VBox vbLeft) {
        vbLeft.setStyle("-fx-alignment: top-center; -fx-border-color: black; -fx-border-width: 1;-fx-margin-top:8px;-fx-font-weight : bolder;");
        vbLeft.setMinWidth(200);
        Label lLeft1 = buildLabelLeft1();
        vbLeft.getChildren().addAll(lLeft1);
        return addSecondLabelVbleft(vbLeft);
    }

    private GridPane setColumnConstraint(GridPane gp) {
        ColumnConstraints cc = new ColumnConstraints();
        cc.setPrefWidth(50);
        gp.getColumnConstraints().add(cc);
        return gp;
    }

    private GridPane setRowConstraint(GridPane gp) {
        RowConstraints rc = new RowConstraints();
        rc.setPrefHeight(50);
        gp.getRowConstraints().add(rc);
        return gp;
    }

    private GridPane addBoatViewGpLeft(int i, GridPane gpLeft) {
        if (control.getFlotille1Elem(i) == 1) {
            gpLeft.add(new BoatView(0, 0, "small-boat", "a1"), i, 0);
        } else if (control.getFlotille1Elem(i) == 2) {
            gpLeft.add(new BoatView(0, 0, "big-boat", "a1"), i, 0);
        }
        return gpLeft;
    }

    private GridPane buildGridPaneLeft() {
        GridPane gpLeft = new GridPane();
        for (int i = 0; i < control.getFlotille1Size(); ++i) {
            gpLeft = setColumnConstraint(gpLeft);
            gpLeft = setRowConstraint(gpLeft);
            addBoatViewGpLeft(i, gpLeft);
        }
        gpLeft.setAlignment(CENTER);
        return gpLeft;
    }

    private VBox buildGpLeft(VBox vbLeft) {
        if (control.isEtatManuel()) {
            GridPane gpLeft = buildGridPaneLeft();
            vbLeft.getChildren().addAll(gpLeft);
        }
        return vbLeft;
    }

    private void buildVbLeft() {
        VBox vbLeft = new VBox();
        vbLeft = buildVbLeft(vbLeft);
        buildGpLeft(vbLeft);
        setLeft(vbLeft);
    }

    private Label buildLabelRigth1() {
        Label lRight1 = new Label(control.getSecondArmyName());
        lRight1.setMinWidth(154);
        lRight1.setStyle("-fx-alignment: center; -fx-border-color: black; -fx-border-width: 1;-fx-padding:2px;-fx-text-fill: red;-fx-border-insets: 8px;");
        return lRight1;
    }

    private Label buildLabelRight2() {
        Label lRight2 = new Label("Position   Type      Integrite (%)" + "\n" + Vue.afficherEtatArmee(control.getSecondArmy()));
        lRight2.setStyle("-fx-alignment: center; -fx-border-width: 1;-fx-padding:10px;-fx-text-fill: red;");
        return lRight2;
    }

    private VBox addSecondLabelVbRight(VBox vbRight) {
        if (!(control.isEtatManuel())) {
            Label lRight2 = buildLabelRight2();
            vbRight.getChildren().addAll(lRight2);
            return vbRight;
        }
        return vbRight;
    }

    private VBox buildVbRight(VBox vbRight) {
        vbRight.setStyle("-fx-alignment: top-center; -fx-border-color: black; -fx-border-width: 1;-fx-margin-top:8px;-fx-font-weight : bolder;");
        vbRight.setMinWidth(200);
        Label lRight1 = buildLabelRigth1();
        vbRight.getChildren().addAll(lRight1);
        return addSecondLabelVbRight(vbRight);
    }

    private GridPane addBoatViewGpRight(int i, GridPane gpRight) {
        if (control.getFlotille2Elem(i) == 1) {
            gpRight.add(new BoatView(0, 0, "small-boat", "a2"), i, 0);
        } else if (control.getFlotille2Elem(i) == 2) {
            gpRight.add(new BoatView(0, 0, "big-boat", "a2"), i, 0);
        }
        return gpRight;
    }

    private GridPane buildGridPaneRight() {
        GridPane gpRight = new GridPane();
        for (int i = 0; i < control.getFlotille2Size(); ++i) {
            gpRight = setColumnConstraint(gpRight);
            gpRight = setRowConstraint(gpRight);
            addBoatViewGpRight(i, gpRight);
        }
        gpRight.setAlignment(CENTER);
        return gpRight;
    }

    private VBox buildGpRight(VBox vbRight) {
        if (control.isEtatManuel()) {
            GridPane gpRight = buildGridPaneRight();
            vbRight.getChildren().addAll(gpRight);
        }
        return vbRight;
    }

    private void buildVbRight() {
        VBox vbRight = new VBox();
        vbRight = buildVbRight(vbRight);
        buildGpRight(vbRight);
        setRight(vbRight);
    }

    private void buildSideBars() {
        buildVbLeft(); //VBOX LEFT
        buildVbRight(); //VBOXRIGHT
    }

    private void buildGridPane() {
        gridPane = new GridPane();
        gridPane.setAlignment(CENTER);
        setCenter(gridPane);
        setSizeConstraints();
    }

    private Label buildLabelBottom1() {
        Label lBottom1 = new Label("hello");
        lBottom1.setStyle("-fx-border-color: black;");
        lBottom1.setId("lBottom");
        return lBottom1;
    }

    private Button buildButtonYes(Button buttonNo) {
        buttonYes.setVisible(false); // on affiche pas de button au départ de l'application
        buttonYes.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                buttonYes.setVisible(false);
                buttonNo.setVisible(false);
                control.buttonDeplacerClicked(true);
            }
        });
        return buttonYes;
    }

    private Button buildButtonNo(Button buttonYes) {
        buttonNo.setVisible(false); // on affiche pas de button au départ de l'application
        buttonNo.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                buttonYes.setVisible(false);
                buttonNo.setVisible(false);
                control.buttonDeplacerClicked(false);
            }
        });
        return buttonNo;
    }

    private void buildBottomButtons() {
        buildButtonYes(buttonNo);
        buildButtonNo(buttonYes);
    }

    private void buildVBoxBottom() {
        VBox vbBottom = new VBox();
        vbBottom.setStyle("-fx-border-color: black;");
        //Boutons de choix déplacement de l'utilisateur
        vbBottom.getChildren().add(buildLabelBottom1());
        vbBottom.getChildren().add(buttonYes);
        vbBottom.getChildren().add(buttonNo);
        vbBottom.setAlignment(CENTER);
        setBottom(vbBottom);
    }

    private void setStage(Stage stage) {
        stage.setScene(new Scene(this, 1024, 768));
        stage.setTitle("Combat Naval");
        stage.show();
    }

    // Pour que chaque ligne et chaque colonne soit dimensionnée
    private void setSizeConstraints() {
        for (int i = 0; i <= SIZE; ++i) {
            setColConstraint();
            setRowconstraint();
        }
    }

    private void setColConstraint() {
        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(50 / SIZE);
        gridPane.getColumnConstraints().add(cc);
    }

    private void setRowconstraint() {
        RowConstraints rc = new RowConstraints();
        rc.setPercentHeight(50 / SIZE);
        gridPane.getRowConstraints().add(rc);
    }

    private void setTitreLignesColonnes(int c, int l) {
        if (c == 0 && l > 0) { //ajout des titre col
            gridPane.add(labInt(l), c, l);
            gridPane.add(new EmptyBoxView(c, l), c, l);
        } else if (l == 0 && c > 0) { //ajout des titres lignes
            gridPane.add(labChar(c), c, l);
            gridPane.add(new EmptyBoxView(c, l), c, l);
        }
    }

    /**
     * return true si on n'affiche PAS un bateau
     *
     * @param c
     * @param l
     * @return
     */
    private boolean afficherBateau(int c, int l, Position currentPos) {
//        Armee a1 = control.getMer().getFirstArmy();
//        Armee a2 = control.getMer().getSecondArmy();
        Armee a1 = control.getFirstArmy();
        Armee a2 = control.getSecondArmy();
        return afficherBateau(c, l, currentPos, a1, a2);
    }

    /**
     * //on vérifie si a1 et a2 possède un bateau sur cette position. Si oui on
     * met une image bateau
     *
     * @param c
     * @param l
     * @param currentPos
     * @param a1
     * @param a2
     * @return
     */
    private boolean afficherBateau(int c, int l, Position currentPos, Armee a1, Armee a2) {
        if (a1.getBateauByPosition(currentPos) != null) {
            gridPane.add(buildBoatView(c, l, a1.getBateauByPosition(currentPos), "a1"), c, l);
            return false;
        } else if (a2.getBateauByPosition(currentPos) != null) {
            gridPane.add(buildBoatView(c, l, a2.getBateauByPosition(currentPos), "a2"), c, l);
            return false;
        }
        return true;
    }

    private void addBoxView(int c, int l, List<Position> posLegales, Position currentPos) {
        if (posLegales.contains(currentPos)) {
            gridPane.add(new DeplacementBoxView(c, l), c, l);
        } else {
            gridPane.add(new EmptyBoxView(c, l), c, l);
        }
    }

    private void afficherCaseViolette(Object arg, int l, int c, Position currentPos) {
        boolean isNotBoatView = afficherBateau(c, l, currentPos);
        if (isNotBoatView && control.isEtatBuildMerVide() == false) { //ici on arrive dans des modes different d'un affichage basic des bateaux
            if (arg instanceof List) { // List est une List<Position> pour affichage des déplacement possibles
                List<Position> posLegales = (List) arg;
                addBoxView(c, l, posLegales, currentPos);
            } else { //ni BoatView ni Case violette donc affichage case Vide
                gridPane.add(new EmptyBoxView(c, l), c, l);
            }
        } else if (control.isEtatBuildMerVide()) { //affichage normal
            gridPane.add(new EmptyBoxView(c, l), c, l); //TODO corriger car on utilise
        }
    }

    private void afficherButtonDeplacer(int c, int l, Object arg) {
        if (!control.isEndGame() && (c == SIZE - 1) && (l == SIZE - 1)) { // on a afficher la dernière "case" du plateau.
            if (arg instanceof Integer) { //on est dans le mode déplacer
                if (((int) arg) == 2) {                     //on se trouve dans l'état afficher déplacement
                    changeButtonVisibilty(); // il faut afficher les button pour permettre à l'utilisateur de choisir
                }
            }
        }
    }

    private void updateView(Object arg, int l, int c) {
        //Position dans laquelle on se trouve dans la boucle
        Position currentPos = new Position((byte) (l), Helper.toChar((byte) (c)));
        setTitreLignesColonnes(c, l); //affichage des titres colonnes et des lignes
        /* affichage des bateaux et des cases vides */
        afficherCaseViolette(arg, l, c, currentPos); //affichage cases violettes mode déplacement
        afficherButtonDeplacer(c, l, arg); // affiche les bouttons oui et non si dernière case
    }

    private void updateView(Object arg) {
        buildSideBars();
        for (int c = 0; c <= SIZE; ++c) {
            for (int l = 0; l <= SIZE; ++l) {
                updateView(arg, l, c);
            }
        }
        afficheMessageBottom(); //!affichage du message bottom à chaque update de la vue!
    }

    @Override
    public void update(Observable o, Object arg) {
        gridPane.getChildren().clear();
        updateView(arg);
    }

    /**
     * renvoi true si on afficha aucun message
     *
     * @return
     */
    private boolean afficherMessageBottomTirer() {
        if (control.isEtatTirer1()) {
            setBottomMessage("Armee 1 à vous de tirer");
            return false;
        } else if (control.isEtatTirer2()) {
            setBottomMessage("Armee 2 à vous de tirer");
            return false;
        }
        return true;
    }

    private boolean afficherMessageBottomDemanderDeplacer() {
        if (control.isEtatDemanderDeplacer1()) {
            setBottomMessage("Armee 1 souhaitez-vous déplacer un bateau ? ");
            return false;
        } else if (control.isEtatDemanderDeplacer2()) {
            setBottomMessage("Armee 2 souhaitez-vous déplacer un bateau ? ");
            return false;
        }
        return true;
    }

    private boolean afficherMessageBottomEtatDeplacer() {
        if (control.isEtatDeplacer1()) {
            setBottomMessage("Armee 1 choisissez bateau à déplacer");
            return false;
        } else if (control.isEtatDeplacer2()) {
            setBottomMessage("Armee 2 choisissez bateau à déplacer");
            return false;
        }
        return true;
    }

    private boolean afficherMessageBottomPlacementManuel() {
        if (control.isEtatPlacementManuel1()) {
            setBottomMessage("Armee 1 choisissez bateau à placer manuelement");
            return false;
        } else if (control.isEtatPlacementManuel2()) {
            setBottomMessage("Armee 2 choisissez bateau à placer manuelement");
            return false;
        }
        return true;
    }

    private void afficheMessageBottom() {
        if (control.isEndGame()) {
            String messageGagnant = control.getFirstArmySize() > 0 ? "Joueur 1 vous avez gagné" : "Joueur 2 vous avez gagné";
            setBottomMessage(messageGagnant);
        } else {
            afficherMessageBottomTirer();
            afficherMessageBottomDemanderDeplacer();
            afficherMessageBottomEtatDeplacer();
            afficherMessageBottomPlacementManuel();
        }
    }

    private Label labInt(int i) {
        Label label = new Label(" " + i);
        setFont(label);
        return label;
    }

    private Label labChar(int i) {
        Label label = new Label(" " + Helper.toChar((byte) (i)));
        setFont(label);
        return label;
    }

    private void setFont(Label label) {
        label.setFont(Font.font("Helvetica", 12));
    }

    private BoxView buildBoatView(int x, int y, Bateau bat, String cssArmy) {
        BoxView boxView = null;
        if (bat instanceof PetitBateau) {
            boxView = new BoatView(x, y, "small-boat", cssArmy);
        } else if (bat instanceof GrandBateau) {
            boxView = new BoatView(x, y, "big-boat", cssArmy);
        }
        return boxView;
    }

    // La vue d'une "case"
    private abstract class BoxView extends Pane {

        public BoxView() {
            getStylesheets().add("Vue/BoxView.css");
        }
    }

    private class BoatView extends BoxView {

        public String boatType; // "small-boat" ou "big-boat"

        public BoatView(int x, int y, String cssType, String cssArmy) {
            boatType = cssType; // défini s'il s'agit d'un grand ou d'un petit.
            getStyleClass().addAll(cssType, cssArmy);
            setOnMouseClicked((e) -> {
                control.boatClicked(x, y, cssType, cssArmy);
            });
        }
    }

    // La vue d'une "case" vide
    private class EmptyBoxView extends BoxView {

        public EmptyBoxView(int x, int y) {
            getStyleClass().add("empty");
            setOnMouseClicked(e -> {
                if (x != 0 && y != 0) {
                    control.emptyBoxClicked(x, y);
                }
            });
        }
    }

    private class DeplacementBoxView extends BoxView {

        public DeplacementBoxView(int x, int y) {
            getStyleClass().add("violet");
            setOnMouseClicked(e -> {
                control.deplacementBoxClicked(x, y);
            });
        }
    }

    /**
     * Affiche un message dans la zone bottom du BorderPane
     *
     * @param message
     * @return
     */
    private void setBottomMessage(String message) {
        Label lBottom = createBottomMessage(message);
        VBox vbBottom = (VBox) getBottom();
        for (Node node : vbBottom.getChildren()) {
            if (node instanceof Label) {
                if (node.getId().equals("lBottom")) {
                    ((Label) node).setText(message);
                }
            }
        }
    }

    private Label createBottomMessage(String message) {
        Label l = new Label(message);
        return l;
    }

    private void changeButtonVisibilty() {
        VBox vbBottom = (VBox) getBottom();
        for (Node node : vbBottom.getChildren()) {
            if (node instanceof Button) {
                Button currentButton = (Button) node;
                currentButton.setVisible(!currentButton.isVisible());
            }
        }
    }

}
