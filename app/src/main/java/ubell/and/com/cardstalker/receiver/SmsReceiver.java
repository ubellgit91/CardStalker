package ubell.and.com.cardstalker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsMessage;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ubell.and.com.cardstalker.database.BankDTO;
import ubell.and.com.cardstalker.database.DBHelper;
import ubell.and.com.cardstalker.location.GetUsedLocation;


public class SmsReceiver extends BroadcastReceiver {

    private final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private final String TAG = "SmsReceiver";
    private PushMessage pushMessage = PushMessage.getInstance();

    private static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private Context mContext;
    private String phoneNumber = "";
    private String phoneMessage = "";
    private String phoneGetTime = "";
    private DBHelper dbHelper = DBHelper.getInstance(mContext);
    private ArrayList<BankDTO> selectArray = new ArrayList<BankDTO>();
    private boolean isEqualNumber;
    private String bankName;


    @Override
    public void onReceive(Context context, Intent intent) {

        this.mContext = context;

        if (intent.getAction().equals(SMS_RECEIVED)) {
            Log.i(TAG, "onReceiver() 호출"); //Log.i는 info
            /////
            Bundle bundle = intent.getExtras();


            if (bundle != null) {

                //실제 메세지는 Object타입의 배열에 PDU형식으로 저장
                Object[] pdus = (Object[]) bundle.get("pdus"); //pdus라는 키값을 통해 문자메시지

                SmsMessage[] msg = new SmsMessage[pdus.length];

                for (int i = 0; i < pdus.length; i++) {

                    msg[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    // SmsMessage의 static메서드인 createFromPdu로 pdusObj의
                    // 데이터를 message에 담는다
                    // 이 때 pdusObj는 byte배열로 형변환을 해줘야 함

                }

                for (SmsMessage message : msg) {

                    phoneNumber += message.getOriginatingAddress();
                    phoneMessage += message.getMessageBody();

                    phoneGetTime += dateformat.format(new Date(message.getTimestampMillis())).toString();


                }

            }
            ////////////
            Thread thread1 = new Thread(new Runnable() {
                @Override
                public void run() {

                    Looper.prepare();//이벤트나 메세지를 담아놓는 Queue 생성함.(선입선출) -<일종의 메모리구조임.
                    //쓰레드마다 하나의 이벤트루프를 가질 수 있음.(1Thread = 1Looper)
                    //이 Loop는 여러개의 Handler를 가질 수 있음.
                    //Handler는 prepare()를 통해 만들어진 Queue에 들어가는 메세지 객체.
                    //이것을 이용해 다른 Thread에 작업전달가능.
                    //Handler handler = new Handler(Looper.getMainLooper()); 이렇게 하면 MainThread(UI)에 있는 Looper를 가져와서
                    //MainThread의 작업에 관여하겠다는 것임
                    //핸들러를통해 다른 쓰레드로 보낼 메세지를 가져와서 메세지 변수에 담고
                    //Message message = handler.obtainMessage(보낼메세지)
                    //그걸 핸들러의 handler.sendMessage(message)로 전달할 수 있음.

                    selectBank(); //은행정보 가져오기 쓰레드처리.

                    if (phoneMessage.contains("체크") && isEqualNumber == true) {
                        if (phoneMessage.contains("예정")) {
                            //문자에 예정~이라는 메세지가 들어있으면 거름.
                            return;
                        } else {

                            //그렇지않고 체크카드 사용이 맞으면 푸쉬 메시지 보내고, 로케이션 정보 db에 저장함.
                            insertLocation(mContext);
                            pushMessage.goPushMessage(mContext, phoneMessage, bankName);


                        }

                    }
                    Looper.loop();

                }
            });
            thread1.start();

        }


    }

    ///////////////////////////////////////////////////////
    private void insertLocation(Context context) {

        String lat = "";
        String lon = "";

        GetUsedLocation location = GetUsedLocation.getINSTANCE(context);
        if (!location.isGetLocation()) {
            location.showSettingAlert();
        } else {

            lat = String.valueOf(location.getLatitude());
            lon = String.valueOf(location.getLongitude());

        }


        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String insertSQL = "insert into tb_mapdb (address, message, time, lat, lon) values (?, ?, ?, ?, ?)";

        db.execSQL(insertSQL, new String[]{phoneNumber, phoneMessage, phoneGetTime, lat, lon});
        db.close();

        //문자를 받게되면 바로 adapter를 notifyDataSetChanged() 시켜보도록 하자. 이게맞나?
        //adapter변수를 static으로 선언했음. 실험해보자.
        //안된다. 제일 나중에 수정하도록 하자.
//            UsedListFragment.adapter.notifyDataSetChanged();

    }

    private void selectBank() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _id,bank,number from tb_bank", null);
        while (cursor.moveToNext()) {
            int _id =Integer.valueOf(cursor.getString(cursor.getColumnIndex("_id")));
            String bank = cursor.getString(cursor.getColumnIndex("bank"));
            String number = cursor.getString(cursor.getColumnIndex("number"));


            selectArray.add(new BankDTO(_id,bank,number));
            //테이블에서 number들을 불러온다. 하나씩... 그걸 어디엔가 담는다.
            //그 다음 그 값을 하나씩 가져와서 phoneNumber랑 비교를 한다.
        }
        //
        for (BankDTO dto : selectArray) {
            if (phoneNumber.equals(dto.getNumber())) {
                isEqualNumber = true;
                bankName = dto.getBank();
                return;
            }

        }

    }
    //////
}
