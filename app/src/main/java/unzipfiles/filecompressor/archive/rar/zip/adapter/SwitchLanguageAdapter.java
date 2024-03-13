package unzipfiles.filecompressor.archive.rar.zip.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

import unzipfiles.filecompressor.archive.rar.zip.R;
import unzipfiles.filecompressor.archive.rar.zip.model.SelectLanguageModel;


public class SwitchLanguageAdapter extends RecyclerView.Adapter<SwitchLanguageAdapter.ViewHolder> {

    private Context context;
    private ArrayList<SelectLanguageModel> languageModels;

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private GestureDetector mGestureDetector;

    public SwitchLanguageAdapter(Context context, ArrayList<SelectLanguageModel> languageModels, OnItemClickListener mListener) {
        this.context = context;
        this.languageModels = languageModels;
        this.mListener = mListener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_select_country, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.ivFlag.setImageResource(languageModels.get(position).getFlag());
        holder.tvName.setText(languageModels.get(position).getName());

        if (languageModels.get(position).getSelected()) {
            holder.rootLayout.setBackground(context.getDrawable(R.drawable.bg_item_language_selected));
        } else {
            holder.rootLayout.setBackground(context.getDrawable(R.drawable.bg_item_language_unselected));
        }

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < languageModels.size(); i++) {
                    languageModels.get(i).setSelected(false);
                }
                languageModels.get(position).setSelected(true);
                setSelected_position(position);
                mListener.onItemClick(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return languageModels.size();
    }

    public boolean isSelectedFont(){
        int count = 0;
        for (int i = 0; i < languageModels.size(); i++){
            if (languageModels.get(i).getSelected()){
                count++;
            }
        }
        return count != 0;
    }

    public void setSelected_position(int position){
        for (int i = 0; i < languageModels.size(); i++) {
            languageModels.get(i).setSelected(false);
        }
        languageModels.get(position).setSelected(true);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        ImageView ivFlag;
        RelativeLayout rootLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            ivFlag = itemView.findViewById(R.id.ivFlag);
            rootLayout = itemView.findViewById(R.id.rootLayout);

        }
    }
}
