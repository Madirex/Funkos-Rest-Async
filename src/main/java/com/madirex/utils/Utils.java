package com.madirex.utils;

/**
 * Clase Utils que contiene métodos útiles para la aplicación
 */
public class Utils {

    private static Utils utilsInstance;

    /**
     * Constructor privado de la clase Utils
     */
    private Utils() {
    }

    /**
     * Devuelve una instancia de la clase Utils
     *
     * @return Instancia de la clase Utils
     */
    public static Utils getInstance() {
        if (utilsInstance == null) {
            utilsInstance = new Utils();
        }
        return utilsInstance;
    }

    /**
     * Devuelve un String con el formato de moneda de España
     *
     * @param dbl cantidad de tipo double
     * @return Moneda con formato de España
     */
    public String doubleToESLocal(double dbl) {
        return String.format("%,.2f", dbl).replace(".", ",");
    }
}
