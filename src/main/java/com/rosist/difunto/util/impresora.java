package com.rosist.difunto.util;

import java.io.*;
//import javax.print.PrintService;
//import javax.print.PrintServiceLookup;

/**
* Class declaration
*
*
* @author
* @version 1.10, 08/04/00
*/
public class impresora {
    //Variables de  acceso   al dispositivo
    private FileWriter fw;
    private BufferedWriter bw;
    private PrintWriter pw;
    private String dispositivo="";
    /** Esta funcion inicia el  dispositivo donde se va a imprimir */
    public  void setDispositivo( String texto ) {
        dispositivo=texto;
//        if(texto.trim().length()<=0){//Si el    dispositivo viene en  blanco el  sistema tratara de definirlo
//            PrintService service = PrintServiceLookup.lookupDefaultPrintService();
//            System.out.println("Tu impresora por default es: " + service.getName());
//            System.out.println("Tu impresora por default es: " + service.toString());

//            Session misession=new Session();
//            dispositivo=misession.impresora_tiquets();
            if(dispositivo.trim().length()<=0){
//                if(misession.isWindows()){
                  dispositivo="LPT1";//Esto si  es windows
//                  dispositivo="LPT1:";//Esto si  es windows
//                }else{
//                    dispositivo="/dev/lp0";//Esto si  es linux
//                }
            }
//        }
        try{
            fw = new FileWriter(dispositivo);
            bw = new BufferedWriter (fw);
            pw = new PrintWriter (bw);
        }catch(Exception e){
            System.out.print(e);
        }
    }
    
    public void escribir( String texto ) {
        try{
            pw.print(texto);
        }catch(Exception e){
            System.out.print(e);
        }
    }
    
    public void padr(String texto, int esp, String caracter) {
//        System.out.println("texto.-=> " + texto);
        int lEspacios = esp-texto.length();
        try{
            if (lEspacios<=0){
                pw.print(texto);
            } else {
                pw.print(texto);
                int i=0;
                for(i=1;i<=lEspacios;i++){
                    pw.print(caracter);
                }
            }
        }catch(Exception e){
            System.out.print(e);
        }
    }
    
    public void padl(String texto, int esp, String caracter) {
        int lEspacios = esp-texto.length();
        try{
            if (lEspacios<=0){
                pw.print(texto);
            } else {
                int i=0;
                for(i=1;i<=lEspacios;i++){
                    pw.print(caracter);
                }
                pw.print(texto);
            }
        }catch(Exception e){
            System.out.print(e);
        }
    }

    public void padc(String texto, int esp, String caracter) {
        int lEspacios = esp-texto.length();
        try{
            if (lEspacios<=0){
                pw.print(texto);
            } else {
                int i=0;
                for(i=1;i<=lEspacios/2;i++){
                    pw.print(caracter);
                }
                pw.print(texto);
                for(i=1;i<=lEspacios/2;i++){
                    pw.print(caracter);
                }
            }
        }catch(Exception e){
            System.out.print(e);
        }
    }
    
    public void left(String texto, int esp) {
//        int lEspacios = esp-texto.length();
        try{
            if (texto.length()<=esp){
                pw.print(texto);
            } else {
                pw.print(texto.substring(0,esp));
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }
    
    public void substr(String texto, int ini, int lon) {
        int lTexto = texto.length();
        int ni = ini+1 + lon;
        
        try{
            if (lTexto<ni){
                if (lTexto<ini){
                    pw.print(texto);
                } else {
                    pw.print(texto.substring(ini,texto.length()));
                }
            } else {
                pw.print(texto.substring(ini,lon));
            }
        }catch(Exception e){
            System.out.print(e);
        }
    }
    
    public void repetir(String texto, int lon){
        String tRepetido = "";
        for (int i=0;i<lon;i++){
            tRepetido+=texto;
        }
        pw.print(tRepetido);
    }

    public  void escribirLn( String texto ) {
        try{
            pw.println(texto);
        }catch(Exception e){
            System.out.print(e);
        }
    }
    public  void escribirLn( String texto, int esp ) {
        int lEspacios = esp-texto.length();
        try{
            if (lEspacios<=0){
                pw.print(texto);
            } else {
                pw.print(texto);
                int i=0;
                for(i=1;i<=lEspacios;i++){
                    pw.print("x");
                }
                pw.println("");
            }
        }catch(Exception e){
            System.out.print(e);
        }
    }
    
    public  void cortar() {
        try{
            char[] ESC_CUT_PAPER = new char[] { 0x1B, 'm'};
            if(!this.dispositivo.trim().equals("pantalla.txt")){
                pw.write(ESC_CUT_PAPER);
            }
        }catch(Exception e){
            System.out.print(e);
        }
    }

    public void avanza_pagina( ) {
        try{
            if(!this.dispositivo.trim().equals("pantalla.txt")){
                pw.write(0x0C);
            }
        }catch(Exception e){
            System.out.print(e);
        }
    }

    public  void setRojo( ) {
        try{
            char[] ESC_CUT_PAPER = new char[] { 0x1B, 'r',1};
            if(!this.dispositivo.trim().equals("pantalla.txt")){
                pw.write(ESC_CUT_PAPER);
            }
        }catch(Exception e){
            System.out.print(e);
        }
    }

//    public  void setNegro( ) {
//        try{
//            char[] ESC_CUT_PAPER = new char[] { 0x1B, 'r',0x00};
//            if(!this.dispositivo.trim().equals("pantalla.txt")){
//                pw.write(ESC_CUT_PAPER);
//            }
//        }catch(Exception e){
//            System.out.print("ocurio un error con el negro");
//            System.out.print(e);
//        }
//    }
    
    // original
    public  void setNegro( ) {
        try{
            char[] ESC_CUT_PAPER = new char[] { 0x1B, 'r',0};
            if(!this.dispositivo.trim().equals("pantalla.txt")){
                pw.write(ESC_CUT_PAPER);
            }
        }catch(Exception e){
            System.out.print(e);
        }
    }

    public  void set20cpi( ) {
        try{
            char[] ESC_CUT_PAPER = new char[] { 0x1B, '!',5};
            if(!this.dispositivo.trim().equals("pantalla.txt")){
                pw.write(ESC_CUT_PAPER);
            }
        }catch(Exception e){
            System.out.print(e);
        }
    }
    
    public  void setRoman( ) {
        try{
            char[] ESC_CUT_PAPER = new char[] { 0x1B, '(',0};
            if(!this.dispositivo.trim().equals("pantalla.txt")){
                pw.write(ESC_CUT_PAPER);
            }
        }catch(Exception e){
            System.out.print(e);
        }
    }

    public  void setTipoCaracterLatino( ) {
        try{
            char[] ESC_CUT_PAPER = new char[] { 0x1B, 'R',18};
            if(!this.dispositivo.trim().equals("pantalla.txt")){
                pw.write(ESC_CUT_PAPER);
            }
        }catch(Exception e){
            System.out.print(e);
        }
    }

    public  void setFormato(int formato ) {
        try{
            char[] ESC_CUT_PAPER = new char[] { 0x1B, '!',(char)formato};
            if(!this.dispositivo.trim().equals("pantalla.txt")){
                pw.write(ESC_CUT_PAPER);
            }
        }catch(Exception e){
            System.out.print(e);
        }
    }

    public  void correr(int fin){
        try{
            int i=0;
            for(i=1;i<=fin;i++){
                this.salto();
            }
        }catch(Exception e){
            System.out.print(e);
        }
    }

    public  void espacios(int esp){
        try{
            int i=0;
            for(i=1;i<=esp;i++){
                pw.print(" ");
            }
        }catch(Exception e){
            System.out.print(e);
        }
    }

    public  void salto() {
        try{
            pw.println("");
        }catch(Exception e){
            System.out.print(e);
        }
    }

    public void dividir(){
        escribir("------------------------");
//        escribir("—————————————");
    }

    public  void cerrarDispositivo(  ){
        try{
            pw.close();
            if(this.dispositivo.trim().equals("pantalla.txt")){
                java.io.File archivo=new java.io.File(dispositivo);
                java.awt.Desktop.getDesktop().open(archivo);
//                java.io.File archivo=new java.io.File("pantalla.txt");
//                java.awt.Desktop.getDesktop().open(archivo);
            }
        }catch(Exception e){
            System.out.print(e);
        }
    }

//    public static void main(String args[]) {
//        impresora p=new impresora();
//        p.setDispositivo("");
//        p.escribir((char)27+"m");
//        p.setTipoCaracterLatino();
//        p.setRojo();
//        p.escribir("Esto es una prueba");
//        p.setNegro();
//        p.escribir("esto es negro"+(char)130);
//        p.setFormato(24);
//        p.escribir("esto es negro con formato");
//        p.setFormato(1);
//        p.escribir("esto es negro con formato");
//        p.correr(10);
//        p.cortar();
//        p.cerrarDispositivo();
//    }
}