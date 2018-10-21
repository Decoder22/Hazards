package edu.purdue.bjohncox.hazards;

import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CameraFragment extends Fragment {
    ImageView imageView;
    Camera camera;
    ShowCamera showCamera;
    String urls = "";
    int urlCount = 0;

    @BindView(R.id.gallery)
    Button galleryButton;

    @BindView(R.id.capture)
    Button captureButton;

    @BindView(R.id.frameLayout)
    FrameLayout frameLayout;

    @OnClick(R.id.gallery)
    public void transitionToCollectionActivity(){
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frameLayout, new ImageCollectionFragment())
                .commit();
    }

    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            System.out.println("I took a picture");
            FirestorageUtil.uploadImageToStorage(bytes, getContext(), new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    System.out.println("We finished it with: "+task.getResult());
                    Toast.makeText(getContext(),"Image Uploaded", Toast.LENGTH_SHORT).show();
                    showCamera.restartCamera();
                    captureButton.setEnabled(true);
                    urlCount++;
                    if(urlCount > 1){
                        urls+=",";
                    }
                    urls+=task.getResult();
                    galleryButton.setText("Pictures("+urlCount+")");
                }
            });
        }
    };

    @OnClick(R.id.capture)
    public void captureImage(View v){
        captureButton.setEnabled(false);
        if(camera!=null){
            camera.takePicture(null,null, mPictureCallback);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_camera, container, false);
        ButterKnife.bind(this, myView);
        camera = Camera.open();
        showCamera = new ShowCamera(myView.getContext(),camera);
        frameLayout = getActivity().findViewById(R.id.frameLayout);
        frameLayout.addView(showCamera);
        return myView;

    }

    /*
            captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImage(view);
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                transitionToCollectionActivity();
            }
        });
     */
}
