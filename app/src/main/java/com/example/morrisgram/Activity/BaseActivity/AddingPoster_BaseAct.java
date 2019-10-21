package com.example.morrisgram.Activity.BaseActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.morrisgram.Activity.Posting;
import com.example.morrisgram.CameraClass.ImageResizeUtils;
import com.example.morrisgram.ClassesDataSet.UserPosterList_Dataset;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//앨범열기 - 사진 업로드
public class AddingPoster_BaseAct extends AppCompatActivity {
    //카메라 이미지 회전 적역변수
    public Boolean isCamera = false;
    //전역변수로 File 타입의 tempFile 을 선언해 주세요. 이 tempFile 에 받아온 이미지를 저장할거에요.
    public static File tempFile;
    /*Intent 를 통해 카메라화면으로 이동할 수 있습니다.
    이때 startAcitivtyResult 에는 PICK_FROM_CAMER 를 파라미터로 넣어줍니다.*/
    public static final int PICK_FROM_CAMERA = 2;

    //이 변수는 onActivityResult 에서 requestCode 로 반환되는 값입니다
    public static final int PICK_FROM_ALBUM = 1;
    //카메라와 앨범으로부터 얻게 되는 URI ->>스토리지로 업로드!!
    public Uri photoUri;
    //파일 위치 절대경로 URI
    public Uri getPhotoUri;
    //카메라 촬영,앨범에서 얻게 되는 비트맵 이미지 주소값

    //이미지 업로드 포스터키
    public String PosterKey;

    //현재 접속중인 유저UID가져오기
    public FirebaseUser uid = FirebaseAuth.getInstance().getCurrentUser();
    public StorageReference mstorageRef = FirebaseStorage.getInstance().getReference();
    public StorageReference riversRef;
    public String userUID = uid.getUid();

    public String PosterIMGname = "PosterIMG";
    public String PosterPicList = "PosterPicList";


    //앨범열기 메소드
    public void goToAlbum() {
        isCamera =false;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
        /*startActivityForResult 를 통해 다른 Activity 로 이동한 후 다시 돌아오게 되면 onActivityResult 가 동작되게 됩니다.
        이때 startActivityForResult 의 두번 째 파라미터로 보낸 값 {여기서는 PICK_FROM_ALBUM 이겠죠?}이
        requestCode 로 반환되는 동작을 합니다.*/
    }
    public void takePhoto() {

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
    //카메라에서 찍은 사진을 저장할 파일 만들기
    public File createImageFile() throws IOException {
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("이미지"," onActivityResult 실행확인");

        //예외사항 처리 =앨범화면으로 이동 했지만 선택을 하지 않고 뒤로 간 경우 또는 카메라로 촬영한 후 저장하지 않고 뒤로 가기를 간 경우
        super.onActivityResult(requestCode, resultCode, data);
        //예외처리 분기문
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
                Log.i("이미지"," 베이스 클래스 onActivityResult tempFile 값 확인 : "+tempFile);
                getPhotoUri = Uri.fromFile(tempFile);

            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            //이미지 회전 인스턴스
            ImageResizeUtils.resizeFile(tempFile,tempFile,1280,isCamera);

//            setImage();
            upLoadImage();
            //onActivityResult 분기 처리
            //onActivityResult 에서 requestCode 를 앨범에서 온 경우와 카메라에서 온 경우로 나눠서 처리해줍니다.
        }
    }

    //선택한 이미지 URL 업로드 메소드 - 이미지 선택과 함께 업로드 한다.
    public void upLoadImage (){
        Log.i("이미지", "이미지 업로드 메소드 실행 확인");
//>----------------이미지 uri 업로드 작업------------------<
        try {
            //uri값이 null값이면 일리걸 에러 발생
            if (photoUri != null) {
                Log.i("이미지", "베이스 클래스 photoUri 값 확인 : " + photoUri);
                //게시물 이미지 이름 강제 지정

                //로컬에서 스토리지로 이미지 업로드 소스코드
                //사진 저장 경로 지정 - PosterPicList - PosterUID - IMG

                //게시물 UID를 만들어서 포스팅 클래스에 인텐트로 전달한다.
                final DatabaseReference mdataref = FirebaseDatabase.getInstance().getReference();
                PosterKey = mdataref.push().getKey();


                //게시물 업로드 경로 => gs://morrisgram.appspot.com/PosterPicList/-LrNycKfF3c3d5Gb-_OH/PosterIMG
           //게시물 업로드 경로 수정 => gs://morrisgram.appspot.com/PosterPicList/userUID/-LrNycKfF3c3d5Gb-_OH/PosterIMG
                riversRef = mstorageRef.child(PosterPicList).child(PosterKey+ "/" + PosterIMGname); //게시물 UID 만들기 -> 포스팅 페이지에 전달
                Log.i("이미지", "베이스 클래스  riversRef 값 확인 : " +   riversRef);
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
                                        final ProgressDialog progressDialog = new ProgressDialog(AddingPoster_BaseAct.this);
                                        progressDialog.setIndeterminate(true);
                                        progressDialog.setMessage("업로드 중...");
                                        progressDialog.setCancelable(false);
                                        progressDialog.show();

                                        if(progress == 100.0){
                                            //포스터키 넘기기
                                            Intent intent = new Intent(AddingPoster_BaseAct.this , Posting.class);
                                            intent.putExtra("PosterKey",PosterKey);
                                            startActivity(intent);
                                            finish();
                                           Log.i("이미지", "베이스 클래스 progress 값 확인 : " +progress);
                                        }
                                    }
                                }, (long) progress);
                        //100%가 될 때까지 액티비티 전환 대기
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
                        Toast.makeText(getApplicationContext(),"이미지 업로드 실패!",Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(),""+exception,Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //업로드 완료되면 포스팅 화면으로 이동 - 게시물 UID 전달
                        Log.i("파베","이미지 업로드 성공");

//                        //유저 게시물 키값 저장용 메소드 - 이미지 업로드 실패시 분기 처리
//                        SavePosterKey(PosterKey);
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


            }
        }catch (NullPointerException e){
            e.getStackTrace();
            Log.i("이미지","사진 촬영 phtoUri : "+photoUri);
        }
    }
    //유저 게시물 키값 내부 저장용 메소드
    public void SavePosterKey (String PosterKey){
        SharedPreferences MY_POSTER_KEYS = getSharedPreferences("POSTER_KEYS",MODE_PRIVATE);
        SharedPreferences.Editor KEY_EDITOR = MY_POSTER_KEYS.edit();

        //강제삭제용
//        KEY_EDITOR.clear();
//        KEY_EDITOR.apply();

        //데이터 타입 설명
        Type UserPosterList_Type = new TypeToken<UserPosterList_Dataset>() {}.getType();
        //게시물 키값 DTO클래스 객체 - PosterKey ; PosterKey
        UserPosterList_Dataset MyPosterList = new UserPosterList_Dataset(PosterKey);

        //json 변환 도구 gson
        Gson gson =new GsonBuilder().create();
        JSONArray jsonArray = new JSONArray();

        //게시물 키값들 로드용
        JSONObject jsonObject;

        //MY_POSTER_KEYS에 데이터가 있다면 로드해서 쌓이게 담기 로직
        if(!MY_POSTER_KEYS.getString(userUID,"null").isEmpty()){
            String TEMP_KEYS = MY_POSTER_KEYS.getString(userUID,"null");
            try {
                jsonArray = new JSONArray(TEMP_KEYS);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String json = gson.toJson(MyPosterList,UserPosterList_Type);
        jsonArray.put(json);
        KEY_EDITOR.putString(userUID,jsonArray.toString());
        KEY_EDITOR.apply();
    }

    //유저 게시물 키값 얻는 메소드
    public ArrayList GetPosterKey(){
        SharedPreferences MY_POSTER_KEYS = getSharedPreferences("POSTER_KEYS",MODE_PRIVATE);
        String GetPosterKey = MY_POSTER_KEYS.getString(userUID,"null");

        //메모리 공간에 할당 선언 - try와 for문 스코프 회피 후 리턴
        ArrayList<String> PosterKeyArray = new ArrayList<>();
        try {
           JSONArray jsonArray = new JSONArray(GetPosterKey);

            for (int i = 0; i < jsonArray.length(); i++){
               String GetKey = jsonArray.getString(i);
               JSONObject jsonObject = new JSONObject(GetKey);
               String PosterKey = jsonObject.getString("PosterKey");
               PosterKeyArray.add(PosterKey);
//                Log.i("포스터키", "Posterkey 출력 확인 : "+jsonObject.getString("PosterKey"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return PosterKeyArray;
    }
}
