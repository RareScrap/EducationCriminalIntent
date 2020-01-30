package com.apptrust.educationcriminalintent;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class PhotoViewerDialog extends DialogFragment {
    private ImageView imageView;

    public static PhotoViewerDialog newInstance(Bitmap piture) {

        Bundle args = new Bundle();
        args.putParcelable("image", piture);
        PhotoViewerDialog fragment = new PhotoViewerDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_photo, container);
        imageView = view.findViewById(R.id.photo);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        imageView.setImageBitmap(getArguments().<Bitmap>getParcelable("image"));
    }
}
