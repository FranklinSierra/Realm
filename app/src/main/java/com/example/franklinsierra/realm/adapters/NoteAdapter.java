package com.example.franklinsierra.realm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.franklinsierra.realm.R;
import com.example.franklinsierra.realm.models.Note;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NoteAdapter extends BaseAdapter {

    //propiedades
    private Context context;
    private List<Note> notes;
    private int layout;

    public NoteAdapter(Context context, List<Note> notes, int layout) {
        this.context = context;
        this.notes = notes;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public Note getItem(int position) {
        return notes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder vh;
        //miramos si la vista ya ha sido cargada
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(layout, null);
            vh = new ViewHolder();
            vh.description = (TextView) convertView.findViewById(R.id.textNoteContent);
            vh.date = (TextView) convertView.findViewById(R.id.textNoteDate);
            //guardo el estado del vh
            convertView.setTag(vh);
        } else {
            //quiere decir que ya se tiene un vh con datos, entonces lo traigo
            vh = (ViewHolder) convertView.getTag();
        }

        Note note = notes.get(position);

        vh.description.setText(note.getDescription());
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String date = df.format(note.getDateCreated());
        vh.date.setText(date);

        return convertView;
    }

    public class ViewHolder {
        TextView description;
        TextView date;
    }
}
