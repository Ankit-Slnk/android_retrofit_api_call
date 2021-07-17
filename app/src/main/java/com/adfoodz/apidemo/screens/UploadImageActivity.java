package com.adfoodz.apidemo.screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.adfoodz.apidemo.R;
import com.adfoodz.apidemo.api.APIClient;
import com.adfoodz.apidemo.api.APIInterface;
import com.adfoodz.apidemo.models.CommonResponse;
import com.adfoodz.apidemo.utility.ImageCompressionAsyncTask;
import com.adfoodz.apidemo.utility.Utility;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadImageActivity extends AppCompatActivity {

    ImageView imgImage;
    File selectedImage;
//    LinearLayout llProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        imgImage = findViewById(R.id.imgImage);

        imgImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(UploadImageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(UploadImageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(UploadImageActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    PopupMenu popup = new PopupMenu(UploadImageActivity.this, v);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.nav_gallery:
                                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(pickPhoto, 1);
                                    return true;
                                case R.id.nav_camera:
                                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    startActivityForResult(takePicture, 2);
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    popup.inflate(R.menu.gallery_camera_menu);
                    popup.show();
                } else {
                    // Request permission from the user
                    ActivityCompat.requestPermissions(UploadImageActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 1: // gallery
                if (resultCode == RESULT_OK) {
                    try {
                        final String path = Utility.getPathFromURI(UploadImageActivity.this, imageReturnedIntent.getData());
                        if (path != null) {
                            imgImage.setImageURI(imageReturnedIntent.getData());
                            ImageCompressionAsyncTask imageCompression = new ImageCompressionAsyncTask() {
                                @Override
                                protected void onPostExecute(File file) {
                                    if (file != null) {
                                        selectedImage = file;
                                    } else {
                                        Toast.makeText(UploadImageActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            };
                            imageCompression.execute(path);
                        }
                    } catch (Exception e) {
                        selectedImage = null;
                        e.printStackTrace();
                    }
                }
                break;
            case 2: //camera
                if (resultCode == RESULT_OK) {
                    Bitmap imageBitmap = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    imgImage.setImageBitmap(imageBitmap);

                    ImageCompressionAsyncTask imageCompression = new ImageCompressionAsyncTask() {
                        @Override
                        protected void onPostExecute(File file) {
                            if (file != null) {
                                selectedImage = file;
                            } else {
                                Toast.makeText(UploadImageActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    };
                    imageCompression.execute(Utility.bitmapToFile(imageBitmap).getPath());

                }
                break;
        }
    }

    public void onImageUpload(View view) {
        if (selectedImage == null) {
            Toast.makeText(this, "Please select image", Toast.LENGTH_SHORT).show();
        } else {
            uploadProfilePhoto();
        }
    }

    void uploadProfilePhoto() {
        Map<String, RequestBody> map = new HashMap<>();
//        llProgressBar.setVisibility(View.VISIBLE);

        if (selectedImage != null) {
            map.put("profile_pic\"; filename=\"" + selectedImage.getName() + "\"", RequestBody.create(MediaType.parse("*/*"), selectedImage));
        }

        APIClient
                .getClient(UploadImageActivity.this)
                .create(APIInterface.class)
                .uploadProfilePhoto(map)
                .enqueue(new Callback<CommonResponse>() {
                    @Override
                    public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
//                        llProgressBar.setVisibility(View.GONE);
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                CommonResponse apiResponse = response.body();
                                Toast.makeText(UploadImageActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();

                                if (!apiResponse.isStatus()) {
                                    // success
                                }
                            }
                        } else {
                            Utility.showError(UploadImageActivity.this, response.errorBody());
                        }
                    }

                    @Override
                    public void onFailure(Call<CommonResponse> call, Throwable t) {
//                        llProgressBar.setVisibility(View.GONE);
                        Log.e("onFailure", t.toString() + "");
                        if (t instanceof com.adfoodz.partner.api.NoConnectivityException) {
                            Toast.makeText(UploadImageActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        call.cancel();
                    }
                });
    }
}