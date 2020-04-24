package com.amber.svoice.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amber.svoice.R;
import com.amber.svoice.utils.GPInfo;

import java.util.List;

public class GPInfoAdapter extends ArrayAdapter<GPInfo> {

    private List<GPInfo> gpInfos;
    private int resourceId;
    private SQLiteAssist sqLiteAssist;

    public GPInfoAdapter(@NonNull Context context, int resource, @NonNull List<GPInfo> objects) {
        super(context, resource, objects);
        resourceId = resource;
        gpInfos = objects;
        sqLiteAssist = new SQLiteAssist(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final GPInfo gpInfo = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView textView1 = view.findViewById(R.id.name_text);
        textView1.setText(gpInfo.getName());
        TextView textView2 = view.findViewById(R.id.code_text);
        textView2.setText(gpInfo.getCode());
        Button button1 = view.findViewById(R.id.remove_btn);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = sqLiteAssist.delete(SQLiteAssist.COLUMN_2 + "=?", new String[]{gpInfo.getCode()});
                if (count > 0) {
                    gpInfos.remove(gpInfo);
                    notifyDataSetChanged();
                }
            }
        });
        return view;
    }

}
