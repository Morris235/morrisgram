package com.example.morrisgram.Activity.BaseActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.morrisgram.Activity.Posting;
import com.example.morrisgram.Activity.ProfileModify;
import com.example.morrisgram.CameraClass.ImageResizeUtils;
import com.example.morrisgram.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

//앨범열기 - 사진 업로드
public class AddingPoster_BaseAct extends AppCompatActivity {
    //카메라 이미지 회전 적역변수
    private Boolean isCamera = false;
    //이 변수는 onActivityResult 에서 requestCode 로 반환되는 값입니다
    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;

    //카메라와 앨범으로부터 얻게 되는 URI ->>스토리지로 업로드!!
    public Uri photoUri;

    //파일 위치 절대경로 URI
    public Uri getPhotoUri;
    //카메라 촬영,앨범에서 얻게 되는 비트맵 이미지 주소값
    public Bitmap originalBm;
    //전역변수로 File 타입의 tempFile 을 선언해 주세요. 이 tempFile 에 받아온 이미지를 저장할거에요.
    private static File tempFile;

    //현재 접속중인 유저UID가져오기
    public FirebaseUser uid = FirebaseAuth.getInstance().getCurrentUser();
    public StorageReference mstorageRef = FirebaseStorage.getInstance().getReference();
    public String userUID = uid.getUid();
    public StorageReference riversRef;
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

    //갤러리에서 받아온 이미지 넣기
//    private void setImage() {
//        Log.i("이미지"," setImage 실행확인");
//        ImageView profileimg_be = (ImageView) findViewById(R.id.ModifyIMG_be);
//        /*첫 번째 파라미터: 변형시킬 tempFile 을 넣었습니다.
//         두 번째 파라미터에는 변형시킨 파일을 다시 tempFile에 저장.
//         세 번째 파라미터는 이미지의 긴 부분을 1280 사이즈로 리사이징 하라는 의미.
//         네 번째 파라미터를 통해 카메라에서 가져온 이미지인 경우 카메라의 회전각도를 적용해 줍니다.(앨범에서 가져온 경우에는 회전각도를 적용 시킬 필요가 없겠죠?)*/
//
//        //이미지 회전 인스턴스
//        ImageResizeUtils.resizeFile(tempFile,tempFile,1280,isCamera);
//        Log.i("이미지"," setImage tempFile 값 확인 : "+tempFile);
//        //사진촬영 주소URI로 변경
//        photoUri = Uri.fromFile(tempFile);
//
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
//
//        //비트맵을 이미지세트함.
//        profileimg_be.setImageBitmap(originalBm);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

//            setImage();
            //onActivityResult 분기 처리
            //onActivityResult 에서 requestCode 를 앨범에서 온 경우와 카메라에서 온 경우로 나눠서 처리해줍니다.
        } else if (requestCode == PICK_FROM_CAMERA) {
//            setImage();
        }
    }

    //선택한 이미지 URL 업로드 메소드
    //어떻게 작동시키지?
    public void upLoadImage (){
//>----------------이미지 uri 업로드 작업------------------<
        try {
            //uri값이 null값이면 일리걸 에러 발생
            if (photoUri != null) {

                //게시물 이미지 이름 강제 지정

                //로컬에서 스토리지로 이미지 업로드 소스코드
                //사진 저장 경로 지정 - PosterPicList - PosterUID - IMG
                DocumentReference mDocumentRef = null;

                //게시물 UID를 만들어서 포스팅 클래스에 인텐트로 전달한다.
                final String PosterUID = mDocumentRef.getId();
                riversRef = mstorageRef.child(PosterPicList).child(PosterUID+"/" + PosterIMGname); //게시물 UID 만들기 -> 포스팅 페이지에 전달
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
                                        final ProgressDialog progressDialog = new ProgressDialog(getApplicationContext());
                                        progressDialog.setIndeterminate(true);
                                        progressDialog.setMessage("업로드 중...");
                                        progressDialog.show();
                                    }
                                }, (long) progress);
                        //100%가 될 때까지 액티비티 전환 대기
                        if(progress == 100.0){

                            //업로드 완료되면 포스팅 화면으로 이동 - 게시물 UID 전달
                            Intent intent = new Intent(AddingPoster_BaseAct.this , Posting.class);
                            intent.putExtra("PosterUID",PosterUID);
                            startActivity(intent);

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
            }
        }catch (NullPointerException e){
            e.getStackTrace();
            Log.i("이미지","사진 촬영 phtoUri : "+photoUri);
        }
    }
}
