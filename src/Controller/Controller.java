package Controller;

import Model.Armee;
import Vue.Vue;
import Model.SeaBuilder.Mer;
import Model.Position;

public class Controller {

    public static void main(String[] args) {
        Controller ctrl = new Controller();
        Vue vue = new Vue();
        Mer mer = ctrl.buildMer(vue);
        ctrl.lancer(vue, mer);
    }

    private Mer buildMer(Vue vue) {
        Mer mer = new Mer(vue.nomArmeeSaisi(), false); //on assigne les noms et on va positionner les bateaux
        vue.setMer(mer);
        mer.addObserver(vue);
        addArmeeObserver(mer, vue);
        mer.setChangedAndNotify();
        return mer;
    }

    private void addArmeeObserver(Mer mer, Vue vue) {
        mer.addFirstArmeeObserver(vue);
        mer.addSecondArmeeObserver(vue);
    }

    private Armee tirer(Mer mer, Vue vue, Armee a1, Armee a2) {
        Position posTireur = vue.saisirBateau(a1.getNom() + ", à vous de tirer. Selectionnez la position du bateau tireur svp([A-Ea-e][1-5])", a1); //on demande à user de choisir un bateau pour tirer et on en déduis sa position
        byte portee = a1.getBateau(posTireur).tirer();
        System.out.println("Portée est de " + portee);
        return mer.tirerAction(a1, a2, portee, posTireur);
    }

    private void deplacer(Mer mer, Vue vue, Armee a1, Armee a2) {
        if (vue.saisirDeplacerBateau()) { //s'il veut bien se déplacer
            Position posBateauDeplacer = vue.saisirBateau(a1.getNom() + ", sélectionnez bateau à déplacer ([A-Ea-e] [1-5])", a1); //on récupère la position du bateau désiré
            Position newPos = vue.saisirPositionDeplacement(mer, a1, a2, posBateauDeplacer);
            //ici on veut une variable de type Position.
            a1 = mer.deplacerBateau(newPos, a1, a2, posBateauDeplacer); //armee déclenche setChanged() & notifyObservers()
            //si un bateau est déplacé, la mer à changé
            mer.setChangedAndNotify(mer);
        }
    }

    private void jouer(Mer mer, Vue vue, Armee a1, Armee a2) {
        do {
            //JOUEUR 1 tir
            a2 = tirer(mer, vue, a1, a2);
            if (a2.getBateaux().size() <= 0) {
                break; //si armée1 a perdu, on ne demande pas à armée2 de jouer son tour
            }
            deplacer(mer, vue, a1, a2);  //le joueur a tiré, il faut saisir un bateau à déplacer.

            //JOUEUR 2 tir
            a1 = tirer(mer, vue, a2, a1); //INVERSION ORDRE DES PARAMS !, ICI A2 AVANT A1  
            deplacer(mer, vue, a2, a1); //INVERSION ORDRE DES PARAMS !, ICI A2 AVANT A1  

        } while (mer.conditionFin());
    }

    private void lancer(Vue vue, Mer mer) {
        Armee a1 = mer.getFirstArmy();
        Armee a2 = mer.getSecondArmy();
        jouer(mer, vue, a1, a2);
        mer.setChangedAndNotify(mer);
        vue.afficherGagnant();
    }

}
