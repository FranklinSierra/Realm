package com.example.franklinsierra.realm.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.franklinsierra.realm.R;
import com.example.franklinsierra.realm.adapters.BoardAdapter;
import com.example.franklinsierra.realm.models.Board;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;

//LE HAGO implements PARA QUE SE PUEDA PASAR EL ID DE LA BOARD DE ESTE ACTIVITY AL NOTE ACTIVITY

public class BoardActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, RealmChangeListener<RealmResults<Board>> {

    private FloatingActionButton fab;
    //Objeto base de datos
    private Realm realm;
    private ListView listView;
    private RealmResults<Board> boards;
    private BoardAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        //instancio la base de datos por defecto
        realm = Realm.getDefaultInstance();


        //consulta en la base de datos sobre todos los registros de la tabla Board
        boards = realm.where(Board.class).findAll();


        //Notifica a el adaptador cuando se crea o se elimina un registro
        boards.addChangeListener(this);

        adapter = new BoardAdapter(boards, this, R.layout.list_view_board_item);
        listView = (ListView) findViewById(R.id.listViewBoard);
        listView.setAdapter(adapter);
        //cuando hagan click en el item para pasar al otro activity
        listView.setOnItemClickListener(this);
        fab = (FloatingActionButton) findViewById(R.id.FABaddBoard);

        //invoco la alerta
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //invoco el dialogo
                showAlertCreatingBoard("Add a new board", "Type a name for your new board");
            }
        });

        //sirve para mostrar el popup cuando se prolongue el click
        registerForContextMenu(listView);

    }


    // ** DIALOGOS ** //
    private void showAlertCreatingBoard(String title, String message) {

        //Se crea un popup con un msj que pregunte por el titulo de la nueva pizarra
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        //verifico que el titulo y el msj no vengan vacios
        if (title != null) alertDialog.setTitle(title);
        if (message != null) alertDialog.setMessage(message);

        //inflo la vista
        View vista = LayoutInflater.from(this).inflate(R.layout.dialog_create_board, null);

        //le paso la vista a el dialogo
        alertDialog.setView(vista);

        //ubico lo que escriben en el edit text del popup
        //Pongo final para poder acceder al input desde cualquier metodo
        final EditText input = (EditText) vista.findViewById(R.id.editTextCreateBoard);

        //config accion del boton
        alertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //creo la variable que tomara el titulo del editText
                //El trim elimina los espacios en blanco que puedan ir al comienzo o final del editText
                String boardName = input.getText().toString().trim();

                //logica si escribe algun titulo
                if (boardName.length() > 0) {

                    //metodo que crea un board
                    createNewBoard(boardName);

                } else {

                    //quiere decir que no se ingreso nada
                    Toast.makeText(getApplicationContext(), "title is required to create a new board", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //creo el pop (ya tengo todos los parametros ahora lo creo)
        AlertDialog dialog = alertDialog.create();
        //lo muestro
        dialog.show();
    }

    // ** para editar boards **//
    private void showAlertEditingBoard(String title, String message, final Board board) {

        //Se crea un popup con un msj que pregunte por el titulo de la nueva pizarra
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        //verifico que el titulo y el msj no vengan vacios
        if (title != null) alertDialog.setTitle(title);
        if (message != null) alertDialog.setMessage(message);

        //inflo la vista
        View vista = LayoutInflater.from(this).inflate(R.layout.dialog_create_board, null);

        //le paso la vista a el dialogo
        alertDialog.setView(vista);

        //ubico lo que escriben en el edit text del popup
        //Pongo final para poder acceder al input desde cualquier metodo
        final EditText input = (EditText) vista.findViewById(R.id.editTextCreateBoard);
        //para que aparezca en el edit text el nombre que antes tenia el board
        input.setText(board.getTitle());

        //config accion del boton
        alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //creo la variable que tomara el titulo del editText
                //El trim elimina los espacios en blanco que puedan ir al comienzo o final del editText
                String boardName = input.getText().toString().trim();

                if(boardName.length()==0){
                    Toast.makeText(getApplicationContext(), "title is required to edit a board", Toast.LENGTH_SHORT).show();
                }else{
                    if(boardName.equals(board.getTitle())){
                        Toast.makeText(getApplicationContext(), "title is the same that it was beforre", Toast.LENGTH_SHORT).show();
                    }else{
                        editBoard(boardName,board);
                    }
                }
            }
        });

        //creo el pop (ya tengo todos los parametros ahora lo creo)
        AlertDialog dialog = alertDialog.create();
        //lo muestro
        dialog.show();
    }


    // ** CREO EL MENU EN EL ACTION BAR para borrar todos los tableros** //


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_board_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteAll:
                deleteAll();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // ** creo la opcion para borrar cada item pulsado prolongadamente ** //


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //buscar la info del menu
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        //ponemos por titulo el nombre dlel board
        menu.setHeaderTitle(boards.get(info.position).getTitle());
        getMenuInflater().inflate(R.menu.context_menu_board_activity, menu);
    }


    // ** los eventos de borrar o editar una pizarra ** //
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {

            case R.id.delete_board:
                deleteBoard(boards.get(info.position));
                return true;
            case R.id.edit_board:
                showAlertEditingBoard("Edit board","Change the name of the board",boards.get(info.position));
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    // ** CRUD ** //
    private void createNewBoard(String boardName) {
        //cominzo la transaccion a la base de datos
        realm.beginTransaction();
        Board newBoard = new Board(boardName);
        //copio el nuevo Board a base de datos realm
        realm.copyToRealm(newBoard);
        //cierro la transaccion
        realm.commitTransaction();
    }
    private void deleteBoard(Board board){
        realm.beginTransaction();
        //lo elimino de la base de datos
        board.deleteFromRealm();
        realm.commitTransaction();
    }
    private void editBoard(String newName,Board board){
        realm.beginTransaction();
        board.setTitle(newName);
        //importante para actualizar en la base de datos
        realm.copyToRealmOrUpdate(board);
        realm.commitTransaction();
    }
    private void deleteAll(){
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
    }
    //Metodo para pasar de este activity al noteActivity

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent(BoardActivity.this, NoteActivity.class);
        intent.putExtra("id", boards.get(position).getId());
        startActivity(intent);
    }


    @Override
    public void onChange(RealmResults<Board> boards) {
        adapter.notifyDataSetChanged();
    }
}
