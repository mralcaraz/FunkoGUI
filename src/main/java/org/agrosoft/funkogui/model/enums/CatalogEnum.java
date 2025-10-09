package org.agrosoft.funkogui.model.enums;

import java.util.Arrays;

public interface CatalogEnum<T extends Enum<T> & CatalogEnum<T>> {
    int getId();
    String getDescripcion();

    static <E extends Enum<E> & CatalogEnum<E>> E fromId(Class<E> enumClass, int id) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Id inválido: " + id +
                        "[" + enumClass.getSimpleName() + "]"));
    }

    static <E extends Enum<E> & CatalogEnum<E>> E fromDescripcion(Class<E> enumClass, String desc) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> e.getDescripcion().equalsIgnoreCase(desc))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Descripción inválida: " + desc +
                        "[" + enumClass.getSimpleName() + "]"));
    }
}
