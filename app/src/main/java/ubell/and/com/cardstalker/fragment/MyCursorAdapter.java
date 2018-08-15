package ubell.and.com.cardstalker.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import ubell.and.com.cardstalker.R;
import ubell.and.com.cardstalker.database.DBHelper;

public class MyCursorAdapter extends CursorAdapter {

    Cursor mCursor;
    Context mContext;
    String _id;
    boolean isSucess;
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
                deletedata(_id);
        }
    });

    public MyCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        this.mContext = context;
        this.mCursor = c;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.item_bank, parent, false);

        return v;
    }

    //생성한 뷰가 view 매개변수로 받아진다. 넘겨받은 select 값이 들어있는 cursor를 이용한다.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView bankName = (TextView) view.findViewById(R.id.textView_bankName);
        bankName.setText(cursor.getString(cursor.getColumnIndex("bank")));
        _id = cursor.getString(cursor.getColumnIndex("_id"));
        bankName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                // 리턴값이 있다
                // 이메서드에서 이벤트에대한 처리를 끝냈음
                //    그래서 다른데서는 처리할 필요없음 true
                // 여기서 이벤트 처리를 못했을 경우는 false
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(mCursor.getString(mCursor.getColumnIndex("bank")))
                        .setMessage("등록번호 "+mCursor.getString(mCursor.getColumnIndex("number"))+" 를 삭제하시겠습니까?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                thread.start();
                                Toast.makeText(mContext,"삭제되었습니다.",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();


                return false;
            }
        });


    }

    public void deletedata(String id){

        DBHelper dbHelper = DBHelper.getInstance(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String Query = "DELETE from tb_bank where _id ="+id;
        db.execSQL(Query);
        db.close();

    }

    public boolean isSucess() {
        return isSucess;
    }
}
