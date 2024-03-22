/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rosist.difunto.modelSbp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Marco
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    private Parmae tipdoccli;
    private String doccli;
    private String nomcli;
    private String dircli;

}
