package com.jonathan.vanhouteghem.android_tchat.assets;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jonathan.vanhouteghem.android_tchat.R;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by sca on 02/06/15.
 */
public class MessagesAdapter extends BaseAdapter {

    private final Context context;

    public MessagesAdapter(Context ctx){
        this.context = ctx;
    }

    List<Message> messages = new LinkedList<>();

    public void addMessage(List<Message> messages){
        this.messages = messages;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(messages == null){
            return 0;
        }
        return messages.size();
    }

    @Override
    public Message getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // On utilise le ViewHolder pour améliorer les performances et ne pas recharger les mêmes messages quand scroll en bas ou en haut
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity)context).getLayoutInflater(); // layout inflater est un objet qui permet d'instancier une vue
            convertView = inflater.inflate(R.layout.item_message, parent, false); // R c'est le résultat quand compile.
            vh = new ViewHolder();
            vh.username = (TextView) convertView.findViewById(R.id.msg_user);
            vh.message = (TextView) convertView.findViewById(R.id.msg_message);
            vh.date = (TextView) convertView.findViewById(R.id.msg_date);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.username.setText(messages.get(position).getUsername());
        vh.message.setText(messages.get(position).getMsg());
        try {
            vh.date.setText(DateHelper.getFormattedDate(messages.get(position).getDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    private class ViewHolder{
        TextView username;
        TextView message;
        TextView date;
    }
}
