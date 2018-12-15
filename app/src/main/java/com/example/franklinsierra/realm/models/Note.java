package com.example.franklinsierra.realm.models;

import com.example.franklinsierra.realm.app.MyAplication;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Note extends RealmObject {

    //se crean los campos y se agregan los atributos segun sql
    @PrimaryKey
    private int id;
    @Required
    private String description;
    @Required
    private Date dateCreated;

    //constructor ES NECESARIO UNO VACIO Y OTRO NO

    public Note(){}

    public Note(String desciption){
        this.id=MyAplication.NoteId.incrementAndGet();
        this.description=desciption;
        //automaticamente toma la fecha de cuando se dio click en la opcion de creacion
        this.dateCreated=new Date();
    }

    public int getId() {
        return id;
    }


    //no se puede modificar el id por que es autogenerado
   /* public void setId(int id) {
        this.id = id;
    }
*/
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    //no se puede modificar la fecha por que es autogenerado

   /* public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }*/
}
