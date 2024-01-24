package com.example.dtalk;

import static com.example.dtalk.retrofit.RetrofitClient.BASE_URL;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dtalk.retrofit.JWTCheckResponse;
import com.example.dtalk.retrofit.RetrofitClient;
import com.example.dtalk.retrofit.ServerApi;
import com.example.dtalk.retrofit.editProfileResponse;
import com.example.dtalk.retrofit.userInformationSearchResponse;
import com.google.android.gms.common.util.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class edit_profile extends AppCompatActivity {
    private String userId;
    private SharedPreferences preferences;
    private ServerApi service;
    private ImageView profile_image;
    private EditText input_nick;
    private EditText input_status_message;
    private Button confirm_btn;
    private Button cancel_btn;
    private String[] item;
    private AlertDialog btn_dialog;
    private AlertDialog.Builder builder;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private Uri userProfileImgPath;
    private String cSep;
    private Uri uri = null;
    private File photoFile;
    private String photoFilePath ;


    private ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //jwt (MODE_PRIVATE (이 앱에서만 사용가능))
        preferences = getSharedPreferences("JWT", MODE_PRIVATE);
        //레트로핏 api 객체 생성
        service = RetrofitClient.getClient(preferences).create(ServerApi.class);

        profile_image = edit_profile.this.findViewById(R.id.profile_image); //프로필 이미지
        input_nick = edit_profile.this.findViewById(R.id.input_nick);//닉네임 인풋
        input_status_message = edit_profile.this.findViewById(R.id.input_status_message);//상태메시지 인풋
        confirm_btn = edit_profile.this.findViewById(R.id.confirm_btn); //프로필 변경 버튼
        cancel_btn = edit_profile.this.findViewById(R.id.cancel_btn);//프로필 변경 취소 버튼



        //쉐어드에서 JWT 가져오기
        String Token = preferences.getString("JWT", ""); // 토큰값 가져오기 없으면 ""
        if (!(Token.equals(""))) { //토큰이 존재하면
            //JWT 검증
            service.JWTCheck().enqueue(new Callback<JWTCheckResponse>() {
                @Override
                public void onResponse(Call<JWTCheckResponse> call, Response<JWTCheckResponse> response) {
                    JWTCheckResponse JWTCheckResponse = response.body();

                    //엑세스토큰이 존재하고 유효할경우 혹은 만료되서 리프레시 토큰으로 재발급 받았을경우
                    if (JWTCheckResponse.getStatus().equals("certification_valid")) {
                        Toast.makeText(edit_profile.this, "엑세스 토큰 유효", Toast.LENGTH_SHORT).show();
                        userId = JWTCheckResponse.getUserId(); //유저아이디 넣기

                    } else if (JWTCheckResponse.getStatus().equals("hacked")) { //비정상적인 접근시
                        Toast.makeText(edit_profile.this, JWTCheckResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        //액티비티 테스크를 전부 지우고 로그인화면으로 이동
                        Intent intent = new Intent(edit_profile.this, login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //기존 플래그 삭제하고 새로운 플래그달음
                        startActivity(intent);
                    } else if (JWTCheckResponse.getStatus().equals("error")) { //에러시
                        Toast.makeText(edit_profile.this, JWTCheckResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } else if (JWTCheckResponse.getStatus().equals("reissued")) { //엑세스토큰 만료되서 재발급시
                        String JWT = JWTCheckResponse.getAccessToken();//JWT
                        //쉐어드에 저장
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("JWT", JWT);
                        editor.commit();

                        Toast.makeText(edit_profile.this, "리프레시 토큰 유효", Toast.LENGTH_SHORT).show();
                        userId = JWTCheckResponse.getUserId(); //유저아이디 넣기

                        Toast.makeText(edit_profile.this, "엑세스토큰 만료되서 재발급", Toast.LENGTH_SHORT).show();

                    } else if (JWTCheckResponse.getStatus().equals("expired")) { //리프레시토큰 만료시
                        Toast.makeText(edit_profile.this, JWTCheckResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    Log.d("TAG", "JWT토큰 onResponse: " + JWTCheckResponse.getUserId());


                }

                @Override
                public void onFailure(Call<JWTCheckResponse> call, Throwable t) {

                }
            });
        }

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId"); //사용자 아이디 받아옴

        // 사진변경 런쳐
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) { //이동한 액티비티에서 RESULT_OK사인이오면
                            Intent intent = result.getData(); // 넘어온 사진데이터를 인텐트로 받고
                            uri = intent.getData(); // uri타입 변수에 다시넣어줌 // 해당이미지파일의 경로 즉 uri정보를 담아줌

                            // Glide를 사용하여 이미지 로딩
                            Glide.with(edit_profile.this)
                                    .load(uri) // friend.getImageUrl()는 이미지의 URL 주소
                                    .override(130, 130)//크기 조절
                                    .into(profile_image);
                            //override : 이미지 가로 세로크기 설정 (없어도됨)
                            //into : 화면에 보여줄 이미지뷰 객체
                            //load : 선택 애미지 정보

                            userProfileImgPath = Uri.parse(getRealPathFromURI(uri)); //이미지 경로 담아줌(변경버튼 눌렀을때 서버에 넘겨주기위해)
                            cSep = "changeProfileImg"; //이미지 변경이라고 구분자로 서버에 알려줌
                            Log.d("TAG11", "onActivityResult: uri : "+uri+" cSep : "+cSep);
                        }

                    }
                });

        // 카메라 런쳐
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) { //이동한 액티비티에서 RESULT_OK사인이오면
                            // 카메라 앱에서 이미지를 촬영한 후, 파일을 FileProvider를 통해 Uri로 변환
                            Uri fileUri = FileProvider.getUriForFile(edit_profile.this, "com.example.dtalk.fileprovider", photoFile);

//                            Intent intent = result.getData(); // 넘어온 사진데이터를 인텐트로 받고
//                            uri = intent.getData(); // uri타입 변수에 다시넣어줌 // 해당이미지파일의 경로 즉 uri정보를 담아줌
//                            Log.d("TAG", "onActivityResult확인: " + result.getData());

                            //uri 얻어오기

                            // Glide를 사용하여 이미지 로딩
                            Glide.with(edit_profile.this)
                                    .load(fileUri) // friend.getImageUrl()는 이미지의 URL 주소
                                    .override(130, 130)//크기 조절
                                    .into(profile_image);
                            //override : 이미지 가로 세로크기 설정 (없어도됨)
                            //into : 화면에 보여줄 이미지뷰 객체
                            //load : 선택 애미지 정보

//                            userProfileImgPath = fileUri; //이미지 경로 담아줌(변경버튼 눌렀을때 서버에 넘겨주기위해)
                            userProfileImgPath = Uri.parse(getCameraRealPathFromUri(edit_profile.this,fileUri));

                            cSep = "changeProfileImg"; //이미지 변경이라고 구분자로 서버에 알려줌
                            Log.d("TAG11", "onActivityResult: uri : "+userProfileImgPath+" cSep : "+cSep);
                        }

                    }
                });


        service.userInformationSearch(userId, "myInfo").enqueue(new Callback<userInformationSearchResponse>() {//친구정보 불러오기
            @Override
            public void onResponse(Call<userInformationSearchResponse> call, Response<userInformationSearchResponse> response) {
                userInformationSearchResponse result = response.body();

                //이미지 변경시 캐시된 이전 이미지를 사용하는 경우 방지하기위해 랜덤 쿼리 매개변수를 먹여줌
                String imageUrl = BASE_URL + result.getUserProfileImg();
                String imageUrlWithRandomQuery = imageUrl + "?timestamp=" + System.currentTimeMillis();

                Uri uri = Uri.parse(imageUrl);

                // Glide를 사용하여 이미지 로딩
                Glide.with(edit_profile.this)
                        .load(imageUrlWithRandomQuery) // friend.getImageUrl()는 이미지의 URL 주소
                        .into(profile_image);

//                userProfileImgPath = Uri.parse(getRealPathFromURI(uri)); //이미지 경로 담아줌(변경버튼 눌렀을때 서버에 넘겨주기위해)

                input_nick.setText(result.getUserNick().toString());
                input_status_message.setText(result.getUserStatusMsg());


            }

            @Override
            public void onFailure(Call<userInformationSearchResponse> call, Throwable t) {

            }
        });
        profile_image.setOnClickListener(new View.OnClickListener() { //프로필 이미지 클릭시
            @Override
            public void onClick(View v) {
                showDialog();//메뉴 다이얼로그 출력

            }
        });

        confirm_btn.setOnClickListener(new View.OnClickListener() { //프로필 변경 적용버튼 클릭시
            @Override
            public void onClick(View v) {
                //메인으로 이동하는데 만약에 채팅방에서 들어온거일수도 있으니까
                //나중에 이프문 만들어서 어디로 들어왔냐에따라 구분해서 인텐트 바꿔야할듯?

                // 서버로 전송할 닉네임과 상태 메시지
                RequestBody userNick = RequestBody.create(MediaType.parse("text/plain"), input_nick.getText().toString());
                RequestBody userStatus = RequestBody.create(MediaType.parse("text/plain"), input_status_message.getText().toString());
                RequestBody getUserId = RequestBody.create(MediaType.parse("text/plain"), userId);


                if (input_nick.equals("")) {//유저가 닉네임을 아무것도 안쓰고 저장했을때
                    Toast.makeText(edit_profile.this, "닉네임은 반드시 입력하셔야 합니다.", Toast.LENGTH_SHORT).show();

                } else {
                    //서버통신
                    if (cSep == null) { //사용자가 이미지를 변경하지 않았으면

                        RequestBody sep = RequestBody.create(MediaType.parse("text/plain"), "noChangesToImg");

//                        File Img = new File(userProfileImgPath.toString());
//                        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(userProfileImgPath.toString());
//                        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
//                        RequestBody requestBody = RequestBody.create(MediaType.parse(mimeType), Img);
//                        MultipartBody.Part userProfileImg = MultipartBody.Part.createFormData("userProfileImg", userId + "_Profile_Image." + fileExtension, requestBody);
                        MultipartBody.Part userProfileImg = MultipartBody.Part.createFormData("userProfileImg", userId + "_Profile_Image.");

                        service.editProfile(userProfileImg, userNick, userStatus, sep, getUserId).enqueue(new Callback<editProfileResponse>() {
                            @Override
                            public void onResponse(Call<editProfileResponse> call, Response<editProfileResponse> response) {
                                editProfileResponse editProfileResponse = response.body();
                                if (editProfileResponse.getStatus().equals("success")) { //변경 성공시
                                    Toast.makeText(edit_profile.this, editProfileResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(edit_profile.this, my_profile.class);
                                    intent.putExtra("userId", userId);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //기존 플래그 삭제하고 새로운 플래그달음
                                    startActivity(intent); //내프로필 화면으로 넘어감


                                } else if (editProfileResponse.getStatus().equals("error")) { //이미지가 아닌파일로 시도했을때
                                    Toast.makeText(edit_profile.this, editProfileResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<editProfileResponse> call, Throwable t) {

                            }
                        });

                    } else if (cSep.equals("changeProfileImg")) {//이미지를 변경하고 저장을 눌렀으면
                        File Img = new File(userProfileImgPath.toString());
                        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(userProfileImgPath.toString());
                        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
                        RequestBody requestBody = RequestBody.create(MediaType.parse(mimeType), Img);
                        MultipartBody.Part userProfileImg = MultipartBody.Part.createFormData("userProfileImg", userId + "_Profile_Image." + fileExtension, requestBody);

                        RequestBody sep = RequestBody.create(MediaType.parse("text/plain"), cSep); //상태

                        service.editProfile(userProfileImg, userNick, userStatus, sep, getUserId).enqueue(new Callback<editProfileResponse>() {
                            @Override
                            public void onResponse(Call<editProfileResponse> call, Response<editProfileResponse> response) {
                                editProfileResponse editProfileResponse = response.body();
                                if (editProfileResponse.getStatus().equals("success")) { //변경 성공시
                                    Toast.makeText(edit_profile.this, editProfileResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(edit_profile.this, my_profile.class);
                                    intent.putExtra("userId", userId);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //기존 플래그 삭제하고 새로운 플래그달음
                                    startActivity(intent); //내프로필 화면으로 넘어감


                                } else if (editProfileResponse.getStatus().equals("error")) { //이미지가 아닌파일로 시도했을때
                                    Toast.makeText(edit_profile.this, editProfileResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<editProfileResponse> call, Throwable t) {
                            }
                        });
                    } else if (cSep.equals("defaultProfileImg")) { //기본이미지로 변경 눌렀을시

                        RequestBody sep = RequestBody.create(MediaType.parse("text/plain"), "defaultProfileImg");
                        MultipartBody.Part userProfileImg = MultipartBody.Part.createFormData("userProfileImg", userId + "_Profile_Image.");

                        service.editProfile(userProfileImg, userNick, userStatus, sep, getUserId).enqueue(new Callback<editProfileResponse>() {
                            @Override
                            public void onResponse(Call<editProfileResponse> call, Response<editProfileResponse> response) {
                                editProfileResponse editProfileResponse = response.body();
                                if (editProfileResponse.getStatus().equals("success")) { //변경 성공시
                                    Toast.makeText(edit_profile.this, editProfileResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(edit_profile.this, my_profile.class);
                                    intent.putExtra("userId", userId);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //기존 플래그 삭제하고 새로운 플래그달음
                                    startActivity(intent); //내프로필 화면으로 넘어감


                                } else if (editProfileResponse.getStatus().equals("error")) { //이미지가 아닌파일로 시도했을때
                                    Toast.makeText(edit_profile.this, editProfileResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<editProfileResponse> call, Throwable t) {

                            }
                        });

                    }
                }
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {//프로필 변경 취소버튼 클릭시
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void showDialog() { //다이얼로그 실행 함수
        item = getResources().getStringArray(R.array.edit_profile_image);
        builder = new AlertDialog.Builder(edit_profile.this);
        builder.setTitle("프로필 사진 변경");

        //다이얼로그에 리스트 담기
        builder.setItems(item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case 0:
                        //프로필 사진 변경 클릭시 앨범출력


                        openGallery();

                        break;
                    case 1:
                        //기본이미지로 변경 클릭시 프사 기본이미지로 변경
                        // Glide를 사용하여 이미지 로딩
                        Glide.with(edit_profile.this)
                                .load(BASE_URL + "/DTalk/img/default.jpg") // 기본프사
                                .into(profile_image);


                        cSep = "defaultProfileImg"; //기본이미지로 변경했다고 서버에 알려주는용도


                        break;

                    case 2:
                        //직접 촬영한 이미지로 변경
                        openCamera();

                }

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    // 앨범 열기 메서드
    private void openGallery() {
//        galleryLauncher.launch("image/*"); // 이미지 선택할 수 있는 앨범 열기
        Intent intent = new Intent(); //암시적인텐트
        intent.setType("image/*"); // 가져올 컨텐츠 타입
        intent.setAction(Intent.ACTION_GET_CONTENT); //앨범호출액션
        galleryLauncher.launch(intent);
    }

    private void openCamera() { //카메라여는 메소드
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 카메라로 찍은 이미지를 저장할 임시 파일 생성
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (photoFile != null) {
            Uri photoUri = FileProvider.getUriForFile(this, "com.example.dtalk.fileprovider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            cameraLauncher.launch(intent);
        }

    }
    private File createImageFile() throws IOException {
        // 이미지 파일 이름 생성
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // 이미지 파일이 저장될 디렉토리 경로 설정 (이 부분은 앱에 따라 다를 수 있음)
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // 임시 파일 생성
        File image = File.createTempFile(
                imageFileName,  /* 파일 이름 */
                ".jpg",         /* 파일 확장자 */
                storageDir      /* 저장될 디렉토리 */
        );

        // 임시 파일의 경로를 photoFilePath 변수에 저장
        photoFilePath = image.getAbsolutePath();

        return image;
    }


    public String getRealPathFromURI(Uri contentUri) { //content:// 형식의 로컬주소 실제주소로 변경해주는 메소드
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor == null) {
            return null;
        }
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }
    public String getCameraRealPathFromUri(Context context, Uri uri) { //content:// 형식의 로컬 주소 실제 주소로 변경해주는 메소드
        Uri returnUri = uri;
        Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);
        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        File file = new File(context.getFilesDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();

            //int bufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            Log.e("File Size", "Size " + file.length());
            inputStream.close();
            outputStream.close();
            Log.e("File Path", "Path " + file.getPath());
            Log.e("File Size", "Size " + file.length());
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return file.getPath();
    }

}
