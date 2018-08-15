package ubell.and.com.cardstalker.location;


import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import java.util.List;

//위치정보를 얻어오기 위한 클래스
//Fused Location API를 이용할 예정임.
public class GetUsedLocation extends Service implements LocationListener{

    private Context mContext;

    //현재 GPS 사용유무
    boolean isGPSEnabled = false;

    //네트워크 사용유무. 네트워크를 통해서도 위치정보 받아올 수 있음
    boolean isNetworkEnabled = false;
    //GPS상태값
    boolean isGetLocation = false;

    Location location;

    double lat; //위도
    double lon; //경도


    //최소 GPS 정보 업데이트 거리 10m. 이 거리는 지나야 GPS가 업데이트됨.
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;
    //최소 GPS 정보 업데이트 시간. 밀리세컨
    private static final long MIN_TIME_BW_UPDATES = 100;


    //실질적으로 위치정보를 알아오는 클래스
    LocationManager locationManager;

    private static GetUsedLocation INSTANCE;

    private GetUsedLocation(Context context){
        this.mContext = context;
        getLocation();
    }

    public static GetUsedLocation getINSTANCE(Context context){
        if(INSTANCE==null){
            INSTANCE = new GetUsedLocation(context);
        }
        return INSTANCE;
    }



    public Location getLocation(){

      try {
          if (Build.VERSION.SDK_INT >= 23 &&
                  ContextCompat.checkSelfPermission(
                          mContext, android.Manifest.permission.ACCESS_FINE_LOCATION)
                          != PackageManager.PERMISSION_GRANTED &&
                  ContextCompat.checkSelfPermission(
                          mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                          != PackageManager.PERMISSION_GRANTED) {

              return null;
          }


          locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

          //GPS정보 가져오기
          isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

          //현재 네트워크 상태 값 알아오기
          isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

          if (!isGPSEnabled && !isNetworkEnabled) {
              //GPS와 네트워크 사용이 가능하지 않을 때
              showSettingAlert();

          } else {
              this.isGetLocation = true;
              // 네트워크 정보로부터 위치값 가져오기
              if (isNetworkEnabled) {
                  //location 매니저의 함수 requestLocationUpdates를 통해 위치정보를 요청한다.

                  if (locationManager != null) {                  locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                      //만약 locationManager가 null이 아니라면 마지막 위치정보를 불러와서 location 변수에 담는다.
                      location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                      if (location != null) {
                          //location변수에 담겨져있는 위도 경도를 불러와 위도 경도 변수에 담는다. 저장
                          lat = location.getLatitude();
                          lon = location.getLongitude();

                      }
                  }
              }

              if (isGPSEnabled) {
                  if (location == null) {
                      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                      if (locationManager != null) {
                          location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                          if (location != null) {

                              lat = location.getLatitude();
                              lon = location.getLongitude();
                          }
                      }
                  }
              }
          }
      }catch(Exception e){
          e.printStackTrace();
      }
        return location;
    }

    /*
    * GPS 종료
    * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GetUsedLocation.this);
        }

    }

    //위도값
    public double getLatitude(){
        if(location !=null){
            lat = location.getLatitude();
        }
        return lat;
    }

    //경도값
    public double getLongitude(){
        if(location !=null){
            lon = location.getLongitude();
        }
        return lon;

    }

    public boolean isGetLocation(){
        return this.isGetLocation;
    }


    /*GPS정보를 가져오지 못핼을 때 설정값으로 갈지 물어보는 Alert창 생성*/

    public void showSettingAlert(){
        AlertDialog.Builder alerDialog = new AlertDialog.Builder(mContext);

        alerDialog.setTitle("GPS 사용유무")
                .setMessage("GPS 셋팅이 되지 않았을수도 있음. \n 설정창으로 가시겠습니까?")
                .setPositiveButton("Setting", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();

    }



    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /*위치정보 얻어오기.

    LocationManager 클래스에서 제공하는 함수를 이용함.

            LocationManager manger = (LocationManager) getSystemService(LOCATION_SERVICE);


    List<String> provider = manger.getAllProviders(); 모든 위치정보 제공자 가져옴.(GPS, NetWork, Wifi, Passive)


    List<String> enabledProviders = manger.getProviders(true); //사용가능한 위치정보제공자 가져옴.

    위치정보 획득하기

    Locaton location = manger.getLastKnownLocation(provider) ; // 위치정보 제공자를 넘겨서 이 함수가 위치값을 얻지못하면 null을 반환하고, 위치값을 잘 가져오면
    위치와 관련된 다양한 정보를 Location에 담아 전달한다.

            location.getAccuracy() 정확도
location.getAltitude() 고도
location.getLongtitude() 경도
location.getLatitude() 위도

            등등등

location.getProvider() 위치 정보 제공자

    위치 정보를 얻어오는 함수는 getLastKnownLocation()인데 이 함수는 필요한 순간 한 번만 이용할 수 있음.

    일정 시간마다 반복해서 위치정보를 얻어와야 하면 LocationListener 인터페이스를 구현하면 됨

    LocationListener listener = new LocationLisnenter() {

        onStatusChanged( ) //provider의 상태가 변경될 때마다 호출

        onProviderEnabled // provider가 사용 가능한 상태가 되는 순간 호출

                onProviderDisabled // provider가 사용 불가능 상황이 되는 슨간 호출

        onLocationChanged // 위치정보 전달 목적으로 호출
    }

manager.requestLocationUpdates(bestProvider, 10000, 10, listener);
    매개변수로 리스너를 등록해서 쓴다. 이용할 위치정보 제공자, 리스너 호출주기, 변경위치거리, 리스너 이렇게 등록해서 씀.*/
}