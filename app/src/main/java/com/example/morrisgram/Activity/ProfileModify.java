package com.example.morrisgram.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Printer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.example.morrisgram.CameraClass.GlideApp;
import com.example.morrisgram.CameraClass.ImageResizeUtils;
import com.example.morrisgram.DTO_Classes.Firebase.Users_ProfileModify;
import com.example.morrisgram.DTO_Classes.Firebase.Users_Signup;
import com.example.morrisgram.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileModify extends AppCompatActivity {

   //데이터베이스의 주소를 지정 필수
   private DatabaseReference mdataref = FirebaseDatabase.getInstance().getReference("UserList");
   //현재 접속중인 유저UID가져오기
   private FirebaseUser uid = FirebaseAuth.getInstance().getCurrentUser();
   private StorageReference mstorageRef = FirebaseStorage.getInstance().getReference();

   private ImageButton ProfileModifyB;
   private TextView email;
   private TextView phone;
   private TextView sex;
   private ImageView profileimg;

   private EditText edname;
   private EditText edwebsite;
   private EditText edintroduce;

   private String userUID = uid.getUid();
//------------------------------카메라,앨범-----------------------------------------------------
    //이 변수는 onActivityResult 에서 requestCode 로 반환되는 값입니다
    private static final int PICK_FROM_ALBUM = 1;
    //전역변수로 File 타입의 tempFile 을 선언해 주세요. 이 tempFile 에 받아온 이미지를 저장할거에요.
    private static File tempFile;
    /*Intent 를 통해 카메라화면으로 이동할 수 있습니다.
    이때 startAcitivtyResult 에는 PICK_FROM_CAMER 를 파라미터로 넣어줍니다.*/
    private static final int PICK_FROM_CAMERA = 2;
    //카메라 퍼미션
    private final int MY_PERMISSIONS_REQUEST_CAMERA=1;
    //카메라 이미지 회전 적역변수
    private Boolean isCamera = false;

    //카메라와 앨범으로부터 얻게 되는 URI
    public Uri photoUri;
    //파일 위치 절대경로 URI
    public Uri getPhotoUri;
    //카메라 촬영,앨범에서 얻게 되는 비트맵 이미지 주소값
    public Bitmap originalBm;
//---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 화면을 portrait(세로) 화면으로 고정하고 싶은 경우
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_profile_modify);

        Log.i("파베","프로필수정 크리에이트 시작");

        final Context context= this;

        //프로필 수정완료 버튼
        ProfileModifyB = (ImageButton) findViewById(R.id.compB);

        //개인정보 표시 변수
        email = (TextView) findViewById(R.id.emailTV_profile);
        phone = (TextView) findViewById(R.id.phonTV_profile);
        sex = (TextView) findViewById(R.id.sexTV_profile);

        //프로필 수정 인풋 텍스트
        edname = (EditText) findViewById(R.id.inputname);
        edwebsite = (EditText) findViewById(R.id.inputwebsite);
        edintroduce = (EditText) findViewById(R.id.inputintro);

       profileimg = (ImageView) findViewById(R.id.ModifyIMG);

        //Glide를 통한 이미지 바인딩
        StorageReference imageRef = mstorageRef.child(userUID+"/ProfileIMG/ProfileIMG");
            Log.i("이미지","스토리지 리퍼런스 NOT NULL : "+imageRef);
            GlideApp.with(this)
                    .load(imageRef)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .dontAnimate()
                    .circleCrop()
                    .placeholder(R.drawable.noimage)
                    .into(profileimg);

        //취소버튼
        ImageButton cancelB;
        cancelB=(ImageButton)findViewById(R.id.cancelB);
        cancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(ProfileModify.this,Myinfo.class);
//                startActivity(intent);
                finish();
            }
        });

        //처음 앱을 실행하고 버튼을 눌렀을 때만 값을 읽어옴 addListenerForSingleValueEvent
        //수시로 해당 디비의 하위값들이 변화를 감지하고 그떄마다 값을 불러오려면 addValueEventListener를 사용
        mdataref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //현재 로그인된 유저 정보와 일치하는 데이터를 가져오기.
                String EmailVal = (String) dataSnapshot.child(userUID).child("UserInfo").child("Email_ID").getValue();
                String PhoneVal = (String) dataSnapshot.child(userUID).child("UserInfo").child("Phone").getValue();
                String SexVal = (String) dataSnapshot.child(userUID).child("UserInfo").child("Sex").getValue();
                String NameVal = (String) dataSnapshot.child(userUID).child("UserInfo").child("NickName").getValue();

                //유저프로필
                String WebsiteVal = (String) dataSnapshot.child(userUID).child("Profile").child("Website").getValue();
                String IntroVal = (String) dataSnapshot.child(userUID).child("Profile").child("Introduce").getValue();

               //개인정보 표시
               email.setText(EmailVal);
               phone.setText(PhoneVal);
               sex.setText(SexVal);

               //수정용 프로필 정보 표시 프로필 차일드와 중복
               edname.setText(NameVal);

               //프로필 읽고 인풋텍스트에 세팅
               edwebsite.setText(WebsiteVal);
               edintroduce.setText(IntroVal);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //프로필 사진 변경 버튼
        final ViewGroup ChangePicB = (ViewGroup) findViewById(R.id.pic_profile);
        final ViewGroup ChangePicB_be = (ViewGroup) findViewById(R.id.pic_profile_be);
        ChangePicB.setVisibility(View.VISIBLE);
        ChangePicB_be.setVisibility(View.INVISIBLE);

        ChangePicB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"사진촬영","앨범"};
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ChangePicB.setVisibility(View.INVISIBLE);
                        ChangePicB_be.setVisibility(View.VISIBLE);

                        switch (which){
                            //사진촬영
                            case 0 : takePhoto();
//                                Toast.makeText(getApplicationContext(),"사진촬영 선택",Toast.LENGTH_SHORT).show();
                                break;
                            //앨범
                            case 1 : goToAlbum();
//                                Toast.makeText(getApplicationContext(),"앨범 선택",Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
                //다이얼로그 생성
                AlertDialog alertDialog = alertDialogBuilder.create();

                //다이얼로그 보여주기
                alertDialog.show();;
            }
        });

        //이미지 변경 미리보기 버튼
        ChangePicB_be.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"사진촬영","앨범"};
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which){
                            //사진촬영
                            case 0 : takePhoto();
//                                Toast.makeText(getApplicationContext(),"사진촬영 선택",Toast.LENGTH_SHORT).show();
                                break;
                            //앨범
                            case 1 : goToAlbum();
//                                Toast.makeText(getApplicationContext(),"앨범 선택",Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
                //다이얼로그 생성
                AlertDialog alertDialog = alertDialogBuilder.create();

                //다이얼로그 보여주기
                alertDialog.show();;
            }
        });

        //          >-----------------프로필 정보 변경 최종 확인 버튼-------------<
        ProfileModifyB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    //프로필 정보
//                    String IMGuri = photoUri.toString();
                //스토리지에 저장한 파일명과 일치하게 스트링으로 작성
//                Intent intent = new Intent(ProfileModify.this,Myinfo.class);

                    String upname = edname.getText().toString();
                    String upwebsite = edwebsite.getText().toString();
                    String upintro = edintroduce.getText().toString();

                    FirebaseDatabase(true,upwebsite,upintro,upname);
//                    startActivity(intent);

                //>----------------이미지 uri 업로드 작업------------------<
                try {
                    //uri값이 null값이면 일리걸 에러 발생
                    if (photoUri != null) {
                        Log.i("파베","프로필 수정 photoUri 값 확인 : "+photoUri);
                        //이미지 이름 강제 지정
                        String UriSTR = "ProfileIMG";

                        //로컬에서 스토리지로 이미지 업로드 소스코드
                        //사진 저장 경로 지정 - userUID - ProfileIMG - IMG
                        StorageReference riversRef = mstorageRef.child(userUID).child("ProfileIMG/" + UriSTR);
                        UploadTask uploadTask = riversRef.putFile(photoUri);

                        //이미지 업로드 모니터링
                        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                final double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                System.out.println("Upload is " + progress + "% done");
                                new android.os.Handler().postDelayed(
                                        new Runnable() {
                                            public void run() {
                                                final ProgressDialog progressDialog = new ProgressDialog(ProfileModify.this);
                                                progressDialog.setIndeterminate(true);
                                                progressDialog.setMessage("읽어들이는 중...");
                                                progressDialog.setCancelable(false);
                                                progressDialog.show();
                                            }
                                        }, (long) progress);
                                //100%가 될 때까지 액티비티 전환 대기
                                if(progress == 100.0){
                                    finish();
                                }

                            }
                        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                                System.out.println("Upload is paused");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                Toast.makeText(getApplicationContext(),"이미지 업로드 실패!",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Handle successful uploads on complete
                                Log.i("파베","이미지 업로드 성공");
                            }
                        });

// Register observers to listen for when the download is done or if it fails
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            }
                        });
                        //이미지 업로드 널값
                    }else {
                        //널값이면 종료
                        finish();
                    }
                }catch (NullPointerException e){
                    e.getStackTrace();
                    Log.i("이미지","사진 촬영 phtoUri : "+photoUri);
                }
            }
        });

    }//-----------------------------------크리에이트--------------------------------------

    //파이어 베이스 업데이트 메소드 - 프로필 웹사이트,소개
    public void FirebaseDatabase(boolean add, String website, String intro, String upname){
        //해쉬맵 생성
        Map<String,Object> childUpdates = new HashMap<>();
        Map<String,Object> PostValues = null;

        if(add){
            Users_ProfileModify posting = new Users_ProfileModify(website,intro);
            PostValues = posting.toMap();
        }

        //새로운 차일드 목록 만들기
        childUpdates.put("Profile" ,PostValues);
        mdataref.child(userUID).updateChildren(childUpdates);

        //유저이름 업데이트 - UserInfo child
        mdataref.child(userUID).child("UserInfo").child("NickName").setValue(upname);
    }

//--------------------------------------카메라 메소드--------------------------------------------------------
private void takePhoto() {

    isCamera=true;

    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    try {
        tempFile = createImageFile();
    } catch (IOException e) {
        Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
        finish();
        e.printStackTrace();
    }
    if (tempFile != null) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Uri photoUri = FileProvider.getUriForFile(this,
                    "com.example.morrisgram.provider", tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, PICK_FROM_CAMERA);
        } else {

            Uri photoUri = Uri.fromFile(tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, PICK_FROM_CAMERA);
        }
    }
}
    //권한 요청을 거부했다면 예외처리 만들기
    private void goToAlbum() {
        isCamera =false;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
        /*startActivityForResult 를 통해 다른 Activity 로 이동한 후 다시 돌아오게 되면 onActivityResult 가 동작되게 됩니다.
        이때 startActivityForResult 의 두번 째 파라미터로 보낸 값 {여기서는 PICK_FROM_ALBUM 이겠죠?}이
        requestCode 로 반환되는 동작을 합니다.*/
    }

    //카메라에서 찍은 사진을 저장할 파일 만들기
    private File createImageFile() throws IOException {
        // 이미지 파일 이름 ( blackJin_{시간}_ )
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "morrisgram" + timeStamp + "_";

        // 이미지가 저장될 폴더 이름 ( blackJin )
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/morrisgram/");
        if (!storageDir.exists()) storageDir.mkdirs();

        // 빈 파일 생성
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        return image;
    }
    //갤러리에서 받아온 이미지 넣기
    private void setImage() {
        Log.i("이미지"," setImage 실행확인");
        ImageView profileimg_be = (ImageView) findViewById(R.id.ModifyIMG_be);
        /*첫 번째 파라미터: 변형시킬 tempFile 을 넣었습니다.
         두 번째 파라미터에는 변형시킨 파일을 다시 tempFile에 저장.
         세 번째 파라미터는 이미지의 긴 부분을 1280 사이즈로 리사이징 하라는 의미.
         네 번째 파라미터를 통해 카메라에서 가져온 이미지인 경우 카메라의 회전각도를 적용해 줍니다.(앨범에서 가져온 경우에는 회전각도를 적용 시킬 필요가 없겠죠?)*/

        //이미지 회전 인스턴스
        ImageResizeUtils.resizeFile(tempFile,tempFile,1280,isCamera);
        Log.i("이미지"," setImage tempFile 값 확인 : "+tempFile);
        //사진촬영 주소URI로 변경
        photoUri = Uri.fromFile(tempFile);

        BitmapFactory.Options options = new BitmapFactory.Options();
        originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);

        //비트맵을 이미지세트함.
        profileimg_be.setImageBitmap(originalBm);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("이미지"," onActivityResult 실행확인");

        //예외사항 처리 =앨범화면으로 이동 했지만 선택을 하지 않고 뒤로 간 경우 또는 카메라로 촬영한 후 저장하지 않고 뒤로 가기를 간 경우
        super.onActivityResult(requestCode, resultCode, data);
        //예외처리 분기분
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();

            if (tempFile != null) {
                if (tempFile.exists()) {
                    if (tempFile.delete()) {
                        Log.e("예외사항", tempFile.getAbsolutePath() + " 삭제 성공");
                        tempFile = null;
                    }
                }
            }
            return;
        }

        if (requestCode == PICK_FROM_ALBUM) {

            photoUri = data.getData();
            Log.i("이미지", "onActivityResult photoUri 값 확인 : " + photoUri);
            Cursor cursor = null;
            try {
                /*
                 *  Uri 스키마를
                 *  content:/// 에서 file:/// 로  변경한다.
                 */
                //절대경로 구하는중
                String[] proj = {MediaStore.Images.Media.DATA};
                assert photoUri != null;
                cursor = getContentResolver().query(photoUri, proj, null, null, null);

                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                cursor.moveToFirst();


                tempFile = new File(cursor.getString(column_index));
                Log.i("이미지"," onActivityResult tempFile 값 확인 : "+tempFile);
                getPhotoUri = Uri.fromFile(tempFile);

            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            setImage();
            //onActivityResult 분기 처리
            //onActivityResult 에서 requestCode 를 앨범에서 온 경우와 카메라에서 온 경우로 나눠서 처리해줍니다.
        } else if (requestCode == PICK_FROM_CAMERA) {
            setImage();
        }
    }
    //애니메이션 효과 지우기
    @Override
    public void onPause(){
        super.onPause();
        overridePendingTransition(0,0);

    }

    //프로필 수정 종료시 사진업로드 ... 사진 변경 없으면 널값예외처리
    @Override
    public void onDestroy(){
        super.onDestroy();
        ProgressDialog dialog;

    }
    //뒤로가기 버튼 -> Myinfo로 이동
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(ProfileModify.this, Myinfo.class);
        startActivity(intent);
        finish();
    }
}
