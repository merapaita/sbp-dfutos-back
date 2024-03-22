/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rosist.difunto.modelSbp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Sucursal {
    private int tiping;
    private String descri;
    private String direccion;
    private String telefono;
    private String fax;
    private String email;
    private Ubigeo ubigeo;    
}
