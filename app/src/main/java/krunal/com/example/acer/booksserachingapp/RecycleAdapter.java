package krunal.com.example.acer.booksserachingapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by acer on 21-02-2018.
 */

/**
 * RecycleAdapter class.
 */
public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.RecycleViewHolder> {

    private static final String TAB = RecycleAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<Books> mlist;
    private LayoutInflater mInflater;
    private OnItemListener mOnItemClickListener;

    RecycleAdapter(Context context,ArrayList<Books> list){
        this.context = context;
        this.mlist = list;
        this.mInflater = LayoutInflater.from(context);
    }

    void SetOnItemClickListener(OnItemListener OnClickListener){
        this.mOnItemClickListener = OnClickListener;
    }

    @Override
    public RecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_list,parent,false);
        return new RecycleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecycleViewHolder holder, int position) {
        Log.i(TAB,"onBindViewHolder call");
        Books currentitem = mlist.get(position);
        holder.mTitleTextView.setText(currentitem.getTitle());
        holder.mAuthoeTextView.setText(currentitem.getAuthorNames());
        holder.mDescriptionTextView.setText(currentitem.getDescription());
        holder.bind(mlist.get(position),mOnItemClickListener);
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    static class RecycleViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitleTextView;
        private TextView mAuthoeTextView;
        private TextView mDescriptionTextView;

        RecycleViewHolder(View itemView) {
            super(itemView);
            mTitleTextView = itemView.findViewById(R.id.book_title);
            mAuthoeTextView = itemView.findViewById(R.id.book_authors);
            mDescriptionTextView = itemView.findViewById(R.id.book_description);

        }

        void bind(Books books,OnItemListener OnItemListener){
            itemView.setOnClickListener(view->{
                OnItemListener.OnItemClick(books);
            });
        }
    }
}
