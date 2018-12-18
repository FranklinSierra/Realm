package com.example.franklinsierra.realm.models;

import com.example.franklinsierra.realm.app.MyAplication;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Board extends RealmObject {

    //CADA CLASE SE TRATA COMO SI FUERA UNA TABLA SI SE EXTIENDE DE REALMOBJECT

    //se crean los campos y se agregan los atributos segun sql
    @PrimaryKey
    private int id;
    @Required
    private String title;
    @Required
    private Date dateCreated;

    //hacemos las relaciones entre tablas
    //PREGUNTAR COMO SE MANEJAN LAS RELACIONES ENTRE LAS TABLAS (CLAVES FORANEAS)

    //como una pizarra y las notas tienen relacion 1:m, creo la lista de las notas
    private RealmList<Note> notes;

    //Creo los constructores
    public Board(){}

    public Board (String title){
        //hacemos uso de la conf de id de MyAplication
        this.id=MyAplication.BoardId.incrementAndGet();
        this.title=title;
        this.dateCreated=new Date();
        this.notes=new RealmList<Note>();
    }

    public int getId() {
        return id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public RealmList<Note> getNotes() {
        return notes;
    }

}
