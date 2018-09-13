package com.example.hp.photofilterapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST=1;
    private static final int RESULT_LOAD_IMAGE=0;

    Button b_load,b_save,b_share,b_filter;
    ImageView imageView,imageFilter;

    String currentImage = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSION_REQUEST);
            }else{
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSION_REQUEST);
            }
        }else   {
            //do nothing
        }
         imageView=(ImageView) findViewById(R.id.imageView);
        imageFilter=(ImageView) findViewById(R.id.imageFilter);

        b_load=(Button) findViewById(R.id.b_load);
        b_save=(Button) findViewById(R.id.b_save);
        b_filter=(Button) findViewById(R.id.b_filter);
        b_share=(Button) findViewById(R.id.b_share);

        b_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
               startActivityForResult(intent,RESULT_LOAD_IMAGE);
            }
        });

        b_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View content=findViewById(R.id.lay);
                Bitmap bitmap= getScreenShot(content);
                currentImage="image" + System.currentTimeMillis() + ".png";
                store(bitmap,currentImage);
                b_share.setEnabled(true);
            }
        });

        b_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageFilter.setImageResource(R.drawable.ic_filter);
                b_save.setEnabled(true);
            }
        });

        b_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            shareImage(currentImage);
            }
        });

    }

    private static Bitmap getScreenShot(View view)
    {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap=Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    private void store(Bitmap bm,String fileName){
        String dirPath= Environment.getExternalStorageDirectory().getAbsolutePath() + "EDITEDIMAGES";
        File dir=new File(dirPath);
        if (!dir.exists()){
            dir.mkdirs();
        }
        File file=new File(dirPath,fileName);
        try {
            FileOutputStream fileOutputStream=new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            Toast.makeText(this, "SAVED!!", Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void shareImage(String fileName){
        String dirPath=Environment.getExternalStorageDirectory().getAbsolutePath() + "EDITEDIMAGES";
        Uri uri=Uri.fromFile(new File(dirPath,fileName));
        Intent intent= new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(Intent.EXTRA_SUBJECT,"");
        intent.putExtra(Intent.EXTRA_TEXT,"");
        intent.putExtra(Intent.EXTRA_STREAM,uri);

        try{
            startActivity(Intent.createChooser(intent,"Share via"));
        }catch (Exception e){
            Toast.makeText(this, "No Sharing app found", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data){}
        Uri selectedImage=data.getData();
        String [] filePathColumn={MediaStore.Images.Media.DATA};
        Cursor cursor=getContentResolver().query(selectedImage,filePathColumn,null,null,null);
        cursor.moveToFirst();
        int columnIndex=cursor.getColumnIndex(filePathColumn[0]);
        String picturePath=cursor.getString(columnIndex);
        cursor.close();
        imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        b_filter.setEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSION_REQUEST:{
                if (grantResults.length>0 &&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                        //do nothing
                    }
                }else {
                    Toast.makeText(this, "No Permission granted", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }
}
