package Vue;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import Controller.ControlleurIteration2;
import javafx.scene.control.CheckBox;
import javafx.scene.input.KeyCode;

public class SplashWindowRoot extends VBox {
    private final ControlleurIteration2 control;
    private final int DIMENSION_DEFAUT = 5;

    public SplashWindowRoot(Stage stage, ControlleurIteration2 ctrl) {
        control = ctrl;
        setup();
        stage.setTitle("Choose Size");
        stage.setScene(new Scene(this, 600, 300));
        stage.show();
    }

    // Mise en place de la (racine de la) scene
    private void setup() {
        TextField user1Name = new InputString("Joueur Un");
        TextField user2Name = new InputString("Joueur Deux");
        TextField tailleFenetre = new InputNumber();
        CheckBox isAleatoireCheckBox = new CheckBox("Placement aléatoire");
        ButtonSubmit buttonSubmit = new ButtonSubmit(user1Name, user2Name, tailleFenetre, isAleatoireCheckBox);
        getChildren().addAll(user1Name, user2Name, tailleFenetre, isAleatoireCheckBox, buttonSubmit);
        setDisposition();
    }
    
    private void setDisposition() {
        setAlignment(Pos.CENTER);
        setPadding(new Insets(20));
        setSpacing(20);
    }

    private void switchToMainWindow(int tailleFenetre, String user1Name, String user2Name, boolean isAleatoireCheckBox) {
        control.switchToMainWindow(tailleFenetre, user1Name, user2Name, isAleatoireCheckBox);
    }

    private class InputString extends TextField {
        //Constructeur
        InputString(String string) {
            super(string);
            setAlignment(Pos.BOTTOM_CENTER);
            setMaxWidth(150);
            installListeners();
        }

        private void installListeners() {
            // N'accepte que les non digits
            textProperty().addListener((obs, oldValue, newValue) -> {
                if (!newValue.matches("\\D*")) {
                    setText(oldValue);
                }
            });
        }
    }
    
        // Un TextField qui n'accepte que des saisies de nombre entiers naturels
    private class InputNumber extends TextField {
        //Constructeur
        InputNumber() {
            super("" + DIMENSION_DEFAUT);
            setAlignment(Pos.CENTER);
            setMaxWidth(150);
            installListeners();
        }

        private void installListeners() {
            // N'accepte que les chiffres
            textProperty().addListener((obs, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    setText(oldValue);
                }
            });
        }
    }
    
        private class ButtonSubmit extends Button {
        //Constructeur
        ButtonSubmit(TextField user1Name, TextField user2Name, TextField tailleFenetre, CheckBox isAleatoireCheckBox) {
            this.setText("OK");
            this.setOnAction(e -> {
                if (!tailleFenetre.getText().isEmpty() && !user1Name.getText().isEmpty() && !user2Name.getText().isEmpty()) {
                    switchToMainWindow(Integer.valueOf(tailleFenetre.getText()), user1Name.getText(), user2Name.getText(), isAleatoireCheckBox.isSelected());
                } else {
                    tailleFenetre.requestFocus(); // Laisse le focus au TextField
                }
            });
        }
    }
}
