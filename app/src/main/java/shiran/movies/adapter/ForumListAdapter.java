package shiran.movies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import shiran.movies.R;
import shiran.movies.U;
import shiran.movies.mainApp.model.Message;

public class ForumListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> messages;
    private LayoutInflater inflater;
    private Context c;
    private String userID = U.userId;
    //final MyCallback cb = new CloseDialog();
    private FirebaseStorage storage = FirebaseStorage.getInstance();



    public ForumListAdapter(List<Message> messages, Context c) {
        inflater = LayoutInflater.from(c);
        this.messages = messages;
        this.c = c;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = inflater.inflate(R.layout.fragment_forum_msg_in, parent, false);
                return new MessageHolderIn(view);
            case 1:
                view = inflater.inflate(R.layout.fragment_forum_msg_out, parent, false);
                return new MessageHolderIn(view);

        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int i) {
        final Message m = messages.get(i);
        ((MessageHolderIn) holder).msg.setText(m.getMessage());

        switch (holder.getItemViewType()) {
            case 0:
                StorageReference imagesRef = storage.getReference().child("images");
                StorageReference face0 = imagesRef.child(messages.get(i).getSender() +  ".jpg");
                Glide.with(c)
                        .using(new FirebaseImageLoader())
                        .load(face0)
                        .into(((MessageHolderIn) holder).img);


//                face0.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                    @Override
//                    public void onSuccess(byte[] bytes) {
//                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                        ((MessageHolderIn) holder).img.setImageBitmap(bitmap);
//                        ((MessageHolderIn) holder).img.setVisibility(View.VISIBLE);
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        // Handle any errors
//                        ((MessageHolderIn) holder).img.setImageResource(R.drawable.no_image);
//                    }
//                });


                //((MessageHolderIn) holder).userName.setText(messages.get(i).getSender());
                break;
            case 1:
                break;

        }


    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    private class MessageHolderIn extends RecyclerView.ViewHolder {
        private TextView msg;
        private ImageView img;

        MessageHolderIn(View row) {
            super(row);
            msg = (TextView) row.findViewById(R.id.lbl_msg);
            img = (ImageView) row.findViewById(R.id.img_forum_pic);
//            root = row.findViewById(R.id.row_root);
        }
    }

//    class MessageHolderOut extends RecyclerView.ViewHolder {
//        private TextView msg;
//
//        public MessageHolderOut(View row) {
//            super(row);
//            msg = (TextView) row.findViewById(R.id.lbl_msg);
////            root = row.findViewById(R.id.row_root);
//        }
//    }


    @Override
    public int getItemViewType(int i) {
        if (!userID.equals(messages.get(i).getSender()))
            return 0;
        else
            return 1;
    }


//    public class CloseDialog implements MyCallback {
//        final ImageButton btnF;
//        public CloseDialog(ImageButton btnF) {
//            this.btnF = btnF;
//        }
//
//        @Override
//        public void onBack(Object movie) {
//            db.setFavoriteValueInDB((Message)movie,ForumListAdapter.this);
//            setFavoriteImg(btnF,((Message)movie).isFavorite());
//            Toast.makeText(c, "favo " + db.getMessages().size(), Toast.LENGTH_SHORT).show();
//        }
//    }


}
