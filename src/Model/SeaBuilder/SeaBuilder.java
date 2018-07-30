package Model.SeaBuilder;

import Model.Bateau;
import Model.GrandBateau;
import Model.PetitBateau;
import Vue.MainWindowRoot;
import Model.Armee;

public class SeaBuilder {

    private Mer mer;

    public SeaBuilder(int size, String nom1, String nom2, boolean isEtatManuel) {
        if (isEtatManuel) {
            mer = buildMerTemp(size, nom1, nom2, isEtatManuel);
            mer.setChangedAndNotify();
        } else {
            mer = new Mer(size, nom1, nom2, false);
        }
    }

    public Mer buildMerTemp(int size, String nom1, String nom2, boolean isEtatManuel) {
        return new Mer(size, nom1, nom2, isEtatManuel);
    }

    public Mer build() {
        return mer;
    }

    public int getFirstArmySize() {
        return mer.getFirstArmySize();
    }

    public int getSecondArmySize() {
        return mer.getSecondArmySize();
    }

    public boolean placerBateauManuel(String tempBoatViewCssType, int armyTurn, int x, int y) {
        return mer.placerBateauManuel(tempBoatViewCssType, armyTurn, x, y);
    }

    public void setChangedAndNotify() {
        mer.setChangedAndNotify();
    }

    public void addObserver(MainWindowRoot mainWindow) {
        mer.addObserver(mainWindow);
    }

    public Armee getFirstArmy() {
        return mer.getFirstArmy();
    }

    public Armee getSecondArmy() {
        return mer.getSecondArmy();
    }

    public Port getPort() {
        return mer.getPort();
    }

    public int getFlotille1Size() {
        return mer.getFlotille1Size();
    }

    public int getFlotille2Size() {
        return mer.getFlotille2Size();
    }

    public int getFlotille1Elem(int index) {
        return mer.getFlotille1Elem(index);
    }

    public int getFlotille2Elem(int index) {
        return mer.getFlotille2Elem(index);
    }

    public void removeFlotille1Element(Integer elem) {
        mer.removeFlotille1Element(elem);
    }

    public void removeFlotille2Element(Integer elem) {
        mer.removeFlotille2Element(elem);
    }

    public Port buildPort() {
        return mer.getPort();
    }

    public boolean flotille1Contains(Integer elem) {
        return mer.flotille1Contains(elem);
    }

    public boolean flotille2Contains(Integer elem) {
        return mer.flotille2Contains(elem);
    }

    public String getFirstArmyName() {
        return mer.getFirstArmyName();
    }

    public String getSecondArmyName() {
        return mer.getSecondArmyName();
    }

}
