package ubell.and.com.cardstalker.readSMS;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/*
public class ReadSMS {

    private static ReadSMS readSMSInstance;
   */
/* private ArrayList<MessageDTO> messageDTOArrayList;*//*
*/
/**//*

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm");

    private ReadSMS() {
        messageDTOArrayList = new ArrayList<MessageDTO>();
    }

    public static ReadSMS getInstance(){
        if(readSMSInstance==null)
            return readSMSInstance= new ReadSMS();
        return  readSMSInstance;
    }

    //getContentResolver()는 context 안에 있음... 왜 이런걸 안알려주는것이야?
    public void readSMSMessage(Context context){
        Uri allMessage = Uri.parse("content://sms");
        //콘텐츠 프로바이더는 일종의 앱의 데이터에 일부 접근하게 해주는 통로와 같다.(정보를 오픈하는 앱에서 프로바이더를 설정해줘야함)

        //getContentResolver를 통해 콘텐츠프로바이더로 접근해서 프로바이더의 문을 열고, 쿼리문을 이용해 값을 가져온다. 그걸 cursor 변수에 담아서 그 변수를 이용하자.
        Cursor cursor = context.getContentResolver().query(allMessage,new String[] {"_id", "thread_id", "address", "person", "date", "body"},null,null,"DATE desc");

        while (cursor.moveToNext()){ //moveToNext값이 false가 될때까지 돌린다.

            long messageId = cursor.getLong(0);
            String address = cursor.getString(2);
            Date date = new Date(cursor.getLong(4));
            Log.i("timeformat",date.toString());
            String body = cursor.getString(5);

            messageDTOArrayList.add(new MessageDTO(messageId,body,address,date));

        }

    }

    public ArrayList<MessageDTO> getMessageDTOArrayList() {
        return messageDTOArrayList;
    }
}
*/
