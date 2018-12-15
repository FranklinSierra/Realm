package com.example.franklinsierra.realm.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.franklinsierra.realm.R;
import com.example.franklinsierra.realm.models.Board;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BoardAdapter extends BaseAdapter {

    //propiedades
    private List<Board> boards;
    private Context context;
    private int layout;

    public BoardAdapter(List<Board> boards, Context context, int layout) {
        this.boards = boards;
        this.context = context;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return boards.size();
    }

    @Override
    public Board getItem(int position) {
        return boards.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null){

            //quiere decir que es la primer vez que se cargan los elementos
            convertView=LayoutInflater.from(context).inflate(R.layout.list_view_board_item,null);
            vh=new ViewHolder();
            vh.title=(TextView)convertView.findViewById(R.id.textViewTitle);
            vh.notes=(TextView)convertView.findViewById(R.id.textViewNotes);
            vh.date=(TextView)convertView.findViewById(R.id.textViewDate);
            //guardo el estado del viewHolder
            convertView.setTag(vh);
        }else{
            //quiere decir que ya existia un convertView
            vh= (ViewHolder) convertView.getTag();
        }

        //tomamos el elemento en el que nos posicionamos
        Board board=boards.get(position);
        vh.title.setText(board.getTitle());

        //caso para las notas
        int numberOfNotes=board.getNotes().size();
        String textForNotes;
        if(numberOfNotes==1){
            textForNotes=numberOfNotes+"Note";
        }else{
            textForNotes=numberOfNotes+"Notes";
        }
        vh.notes.setText(textForNotes);

        //le damos un formato a la fecha
        DateFormat dateFormat=new SimpleDateFormat("dd-MM-YYYY");
        String dateString=dateFormat.format(board.getDateCreated());
        vh.date.setText(dateString);

        return convertView;

    }

    public class ViewHolder{
        TextView title;
        TextView notes;
        TextView date;
    }
}
