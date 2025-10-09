package org.agrosoft.funkogui.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FunkoDto {

    private String linea;
    private String descripcion;
    private int numero;
    private String tamanio;
    private String categoria;
    private String tipo;
    private String comentarios;
}
