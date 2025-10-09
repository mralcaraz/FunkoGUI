package org.agrosoft.funkogui.model.enums;

import lombok.Getter;

@Getter
public enum FunkoTipo implements CatalogEnum<FunkoTipo> {
    REGULAR(1, "Regular"),
    CHASE(2, "Chase"),
    FLOCKED(3, "Flocked"),
    DIAMOND(4, "Diamond"),
    GLOW_IN_THE_DARK(5, "Glow In the Dark"),
    METALLIC(6, "Metallic"),
    CHROME(7, "Chrome"),
    BLACK_LIGHT(8, "Black Light");

    private final int id;
    private final String descripcion;

    FunkoTipo(int id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }
}
