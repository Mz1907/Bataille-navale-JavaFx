package Model;

import java.util.Random;

/**
 *
 * @author zmmai
 */
public class Helper {

    //Vérifie qu'un string donné est convertible en byte
    public static boolean isByte(String s) {
        try {
            byte li = Byte.parseByte(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    //Vérifie qu'un byte donné existe dans le range (1, max_dimension) 
    public static boolean isLignePresent(byte ligne, int dimension) {
        return ligne >= 1 && ligne <= dimension;
    }

    //Vérifie qu'un string donné contient des lettres a - z
    public static boolean isLetter(String s) {
        if (s.length() == 1) {
            return s.matches(".*[A-Za-z].*");
        }
        return false;
    }

    //Vérifie que le string donnée existe dans le plateau de jeu
    public static boolean isColonnePresent(String colonne, int dimension) {
        if (colonne.length() == 1) {
            return ((colonne.getBytes()[0]) >= ("a".getBytes()[0])
                    && (colonne.getBytes()[0]) < ("a".getBytes()[0] + (byte) dimension));
        }
        return false;
    }

    /**
     * Converti un byte en char
     *
     * @param val
     * @return
     */
    public static char toChar(byte val) {
        return (char) (byte) ("a".getBytes()[0] + val - (byte) 1);
    }

    public static byte toByte(char c) {
        return (byte) (c - 'a' + 1);
    }

    /**
     * renvoi un entier positif au hasard 1(min) -> dimension(max)
     *
     * @param dimension
     * @return
     */
    public static int rand(byte dimension) {
        Random r = new Random();
        int rand = r.nextInt(dimension) + 1;
        return rand;
    }

    /**
     * renvoi un espace texte dont la longueur = la diff de taille entre s1 et
     * s2 (sert à combler la diff de taille des noms armees lors de l'affichage)
     * nom des armées
     *
     * @return
     */
    public static String calculerEspace(String s1, String s2) {
        String res = "";
        if (s1.length() > s2.length()) {
            for (int i = 0; i < (s1.length() - s2.length()); i++) {
                res += " ";
            }
        } else {
            for (int i = 0; i < (s2.length() - s1.length()); i++) {
                res += " ";
            }
        }
        return res;
    }
}
