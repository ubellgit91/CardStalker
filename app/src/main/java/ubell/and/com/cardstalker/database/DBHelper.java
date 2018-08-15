package ubell.and.com.cardstalker.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*SQLiteOpenHelper을 이용해서 데이터 관리를 쉽게 해보자. SQLiteOpenHelper는 추상클래스이다.*/
/*DB접속을 해주고 테이블을 만들어주는 클래스.*/
public class DBHelper extends SQLiteOpenHelper{

    private static final int DB_VERSION = 1;
    private static DBHelper INSTANCE;


    private DBHelper(Context context){
        super(context, "mapdb",null,DB_VERSION);

    }


    public static DBHelper getInstance(Context context){
        if(INSTANCE==null){
            INSTANCE = new DBHelper(context);
        }

        return INSTANCE;
    }


    //앱이 설치된 후에 SQLiteOpenHelper가 최초로 이용되는 순간 한 번 호출됨.
    @Override
    public void onCreate(SQLiteDatabase db) {

        //따로 데이터타입을 지정 안해주면 자동으로 varchar로 지정되나봄..
        String create_map_SQL="create table tb_mapdb "+
                "(_id integer primary key autoincrement, "+ "address, "+ "message, "+"time, " +"lat, "+ "lon)";

        String create_bank_SQL="create table tb_bank (_id integer primary key autoincrement, bank, number)";


        db.execSQL(create_map_SQL);
        db.execSQL(create_bank_SQL);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion==DB_VERSION){
            db.execSQL("drop table tb_mapdb");
            onCreate(db);
        }

    }
}
