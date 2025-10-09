package org.agrosoft.funkogui.model.enums;

import lombok.Getter;

@Getter
public enum FunkoTamanio implements CatalogEnum<FunkoTamanio> {
    REGULAR_4(1, "Regular 4\""),
    SUPER_6(2, "Super 6\""),
    JUMBO_10(3, "Jumbo 10\""),
    RIDES(4, "Rides"),
    MOMENT(5, "Moment"),
    DELUXE(6, "Deluxe"),
    TWO_PACK(7, "2 pack"),
    FOUR_PACK(8, "4 pack"),
    KEYCHAIN(9, "Keychain"),
    ALBUM_DELUXE(10, "Album Deluxe"),
    ALBUM_COVER(11, "Album Cover");

    private final int id;
    private final String descripcion;

    FunkoTamanio(int id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

}
