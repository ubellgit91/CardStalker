package ubell.and.com.cardstalker.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class PermissionCheck extends AppCompatActivity {

    private int mPermissionCheck;
    private boolean mSmsReceivePermission;
    private boolean mSmsReadPermssion;
    private boolean mLocationPermssion;

    public void permissionChecker(Context context, Activity activity) {

        //안드로이드 버전 6.0 이상부터 바뀐 권한설정...이것땜에 많이 헤맴.
        //두번째 매개변수로 넘겨지는 권한이 있는지 없는지 체크하는 메소드.
        mPermissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS);
        //앱에 권한이 있는 경우 이 메서드는 PackageManager.PERMISSION_GRANTED (0 임)를 반환하고, 앱이 작업을 계속 진행할 수 있습니다.
        //앱에 권한이 없는 경우 이 메서드는 PERMISSION_DENIED (-1임)를 반환하고, 앱이 사용자에게 명시적으로 권한을 요청해야 합니다.

        //매개변수 intent에는 액션에 대한 종류의 정보와 필드에는 추가정보 들이 들어있음.
        Log.i("PermissionCheck", String.valueOf(mPermissionCheck));

        //권한을 체크해서 권한이 있으면 true
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED) {
            mSmsReceivePermission = true;
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            mSmsReadPermssion = true;
        }
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mLocationPermssion = true;
        }
        //권한이 없으면 권한요청을 띄운다.
        if (!mSmsReceivePermission || !mSmsReadPermssion ||!mLocationPermssion) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS , Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }

        //////////////////////

    }


    //퍼미션요청 결과를 확인하는 메소드. 퍼미션 요청시 보냈던 requestCode로 요청이 맞는지를 판단하고, 요청결과는 grantResults에 담겨서 오는데
    //요청이 다수였으면 배열로 담겨서 옴. 퍼미션 결과가 어땟는지 if문으로 확인하기 가능.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 200 && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mSmsReceivePermission = true;
            }
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                mSmsReadPermssion = true;
            }
            if(grantResults[2] == PackageManager.PERMISSION_GRANTED){
                mLocationPermssion = true;
            }

        }

    }
}
