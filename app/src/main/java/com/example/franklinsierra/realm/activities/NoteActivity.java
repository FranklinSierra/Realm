package com.example.franklinsierra.realm.activities;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.franklinsierra.realm.R;
import com.example.franklinsierra.realm.adapters.NoteAdapter;
import com.example.franklinsierra.realm.models.Board;
import com.example.franklinsierra.realm.models.Note;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;

//le implemento RealmChangeListener<Board> por que es el board el que va a cambiar

public class NoteActivity extends AppCompatActivity implements RealmChangeListener<Board> {

    //propiedades basicas

    private ListView listView;
    private NoteAdapter adapter;
    private RealmList<Note> notes;
    private Realm realm;
    private FloatingActionButton fab;
    private int boardId;
    private Board board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        //le doy la config por defecto a la base de datos
        realm = Realm.getDefaultInstance();

        //tomo el id del BoardActivity
        if (getIntent().getExtras() != null) {
            boardId = getIntent().getExtras().getInt("id");
            //consulto el board que tiene asociado el id
            board = realm.where(Board.class).equalTo("id", boardId).findFirst();
            //para que pueda ir refrescarse el adaptador
            board.addChangeListener(this);
            //obtengo las notas de esa board
            notes = board.getNotes();

            //ponemos el titulo en el accion bar del activity (opcional)
            this.setTitle(board.getTitle());

            //ubicamos por id lo basico para desplegar
            fab = (FloatingActionButton) findViewById(R.id.FABaddNote);
            listView = (ListView) findViewById(R.id.listNote);
            adapter = new NoteAdapter(this, notes, R.layout.list_view_note_item);

            listView.setAdapter(adapter);

            //defino el comportamiento del fab
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAlertCreatingNote("Add a new note", "Type a new note for " + board.getTitle() + ".");
                }
            });
        }

        registerForContextMenu(listView);

    }


    private void showAlertCreatingNote(String title, String message) {

        //Se crea un popup con un msj que pregunte por el titulo de la nueva pizarra
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        //verifico que el titulo y el msj no vengan vacios
        if (title != null) alertDialog.setTitle(title);
        if (message != null) alertDialog.setMessage(message);

        //inflo la vista
        View vista = LayoutInflater.from(this).inflate(R.layout.create_note, null);

        //le paso la vista a el dialogo
        alertDialog.setView(vista);

        //ubico lo que escriben en el edit text del popup
        //Pongo final para poder acceder al input desde cualquier metodo
        final EditText input = (EditText) vista.findViewById(R.id.editTextCreateNote);

        //config accion del boton
        alertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //creo la variable que tomara el titulo del editText
                //El trim elimina los espacios en blanco que puedan ir al comienzo o final del editText
                String note = input.getText().toString().trim();

                //logica si escribe algun titulo
                if (note.length() > 0) {

                    //metodo que crea un board
                    createNewNote(note);

                } else {

                    //quiere decir que no se ingreso nada
                    Toast.makeText(getApplicationContext(), "The note can't be empty", Toast.LENGTH_LONG).show();
                }
            }


        });

        //creo el popup
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    private void showAlertEditingNote(String title, String message, final Note note) {

        //Se crea un popup con un msj que pregunte por el titulo de la nueva pizarra
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        //verifico que el titulo y el msj no vengan vacios
        if (title != null) alertDialog.setTitle(title);
        if (message != null) alertDialog.setMessage(message);

        //inflo la vista
        View vista = LayoutInflater.from(this).inflate(R.layout.create_note, null);

        //le paso la vista a el dialogo
        alertDialog.setView(vista);

        //ubico lo que escriben en el edit text del popup
        //Pongo final para poder acceder al input desde cualquier metodo
        final EditText input = (EditText) vista.findViewById(R.id.editTextCreateNote);
        input.setText(note.getDescription());

        //config accion del boton
        alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //creo la variable que tomara el titulo del editText
                //El trim elimina los espacios en blanco que puedan ir al comienzo o final del editText
                String noteContent = input.getText().toString().trim();
                if (noteContent.length() == 0) {
                    Toast.makeText(getApplicationContext(), "The note can't be empty", Toast.LENGTH_LONG).show();
                } else {
                    if (noteContent.equals(note.getDescription())) {
                        Toast.makeText(getApplicationContext(), "The note is the same it was before", Toast.LENGTH_LONG).show();
                    } else {
                        editNote(noteContent, note);
                    }
                }

            }


        });

        //creo el popup
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }


    // ** CRUD ** //
    private void createNewNote(String note) {
        //comienzo la transaccion
        realm.beginTransaction();
        Note _note = new Note(note);
        realm.copyToRealm(_note);
        //hago que se relacione la nota con la pizarra
        board.getNotes().add(_note);
        //cierro la transaccion
        realm.commitTransaction();
    }

    private void editNote(String newNoteDescription, Note note) {
        realm.beginTransaction();
        note.setDescription(newNoteDescription);
        realm.copyToRealmOrUpdate(note);
        realm.commitTransaction();
    }

    private void deleteNote(Note note) {
        realm.beginTransaction();
        note.deleteFromRealm();
        realm.commitTransaction();
    }

    private void deleteAllNotes() {
        realm.beginTransaction();
        board.getNotes().deleteAllFromRealm();
        realm.commitTransaction();
    }



    // ** opcion de eliminar todas las notas de la pizarra **//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delteAllNotes:
                deleteAllNotes();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //cuando se hace click sostenido sobre una nota
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.context_menu_note_activity, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete_note:
                deleteNote(notes.get(info.position));
                return true;
            case R.id.edit_note:
                showAlertEditingNote("Edit your note","Type your new note",notes.get(info.position));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onChange(Board board) {
        adapter.notifyDataSetChanged();
    }
}


