package com.zapateria.models;

/**
 * Historia PDLS-38: Clasificación de calzado por rango de edad.
 */
public enum CategoriaEdad {
    NINO("Niño (0-12 años)", 0, 12),
    ADOLESCENTE("Adolescente (13-17 años)", 13, 17),
    ADULTO("Adulto (18+ años)", 18, 120);

    private final String descripcion;
    private final int edadMin;
    private final int edadMax;

    CategoriaEdad(String descripcion, int edadMin, int edadMax) {
        this.descripcion = descripcion;
        this.edadMin = edadMin;
        this.edadMax = edadMax;
    }

    public String getDescripcion() { return descripcion; }
    public int getEdadMin() { return edadMin; }
    public int getEdadMax() { return edadMax; }

    public static CategoriaEdad desdedad(int edad) {
        for (CategoriaEdad c : values()) {
            if (edad >= c.edadMin && edad <= c.edadMax) return c;
        }
        return ADULTO;
    }
}
