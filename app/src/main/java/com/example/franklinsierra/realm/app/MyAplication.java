package com.example.franklinsierra.realm.app;

import android.app.Activity;
import android.app.Application;
import android.util.Log;
import android.view.View;

import com.example.franklinsierra.realm.models.Board;
import com.example.franklinsierra.realm.models.Note;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

//LA EXTION DE APPLICATION PARA QUE SE EJECUTE ANTES DEL MAIN (BOARD ACTIVITY)

// **** ES NECESARIO ESPECIFICARLO TAMBIEN EN EL MANIFEST PONIENDO EL android:name  ****    //

public class MyAplication extends Application {

    //Metodos que autoincrementan las llaves primarias (esto se hace para tantas tablas como se tengan)
    public static AtomicInteger BoardId =new AtomicInteger();
    public static AtomicInteger NoteId=new AtomicInteger();

    //Sirve para conocer el valor maximo del id que se lleva para seguir con la secuencia
    @Override
    public void onCreate() {
        super.onCreate();

        // ** FUNCIONAMIENTO DE STETHO ** //

        //instacio Stetho
        Stetho.InitializerBuilder initializerBuilder=Stetho.newInitializerBuilder(this);

        //Trabajo con las herramientas de Chrome
        initializerBuilder.enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this));

        //Adiciono Dumpapp (AVERIGUAR QUE ES)
        initializerBuilder.enableDumpapp(Stetho.defaultDumperPluginsProvider(getApplicationContext()));

        //esta listo, se llama al metodo que lo genera
        Stetho.Initializer initializer= initializerBuilder.build();

        //se inicia
        Stetho.initialize(initializer);

        //sincronizar la red de aca hasta el navegador
       /* OkHttpClient httpClient=new OkHttpClient();
        httpClient.networkInterceptors().add(new StethoInterceptor());
        //recupera el contenido
        try {
            Response response=httpClient.newCall(new Request.Builder().url("http://httpbin.org/ip").build()).execute();
        } catch (IOException e) {
            Log.d("Stetho Tut",e.getMessage());
        }*/


        // ** TRATO DE LOS ID PARA PODER AUTOINCREMENT ** //
        Realm.init(this);
        //configuro primero la base de datos
        setUpRealmConfig();
        //instanciamos la database por defecto
        Realm realm = Realm.getDefaultInstance();
        BoardId = getIdByTable(realm, Board.class);
        NoteId = getIdByTable(realm, Note.class);
        realm.close();
    }

    //configuracion de la database en realm

    private void setUpRealmConfig(){
        RealmConfiguration config= new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();

        //Metemos en la config por defecto la config creada
        Realm.setDefaultConfiguration(config);
    }


    //  ++++    METODO PARA CONOCER EL VALOR MAXIMO QUE LLEVA CADA ID DE CUALQUIER CLASE    ++++    //
    //La T significa cualquier clase
    private <T extends RealmObject> AtomicInteger getIdByTable(Realm realm,Class<T> anyClass){

        //resultados de todos los registros de la tabla
        RealmResults<T> results=realm.where(anyClass).findAll();
        if(results.size()>0){
            //quiere decir que hay al menos un registro
            return new AtomicInteger(results.max("id").intValue());
        }else{
            //como no se le pasa nada quiere decir que es cero
            return new AtomicInteger();
        }
    }
}
