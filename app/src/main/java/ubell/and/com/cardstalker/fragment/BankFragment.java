package ubell.and.com.cardstalker.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;

import ubell.and.com.cardstalker.MainActivity;
import ubell.and.com.cardstalker.R;
import ubell.and.com.cardstalker.database.BankDTO;
import ubell.and.com.cardstalker.database.DBHelper;

public class BankFragment extends Fragment {

    private Cursor cursor;
    private Button btn_add;

    @SuppressLint("ValidFragment")
    private BankFragment(){

    }
    public static BankFragment newInstance() {

        Bundle args = new Bundle();

        BankFragment fragment = new BankFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bank, container, false);
        //
        getData(); //데이터 불러오기.
        //
        MyCursorAdapter myCursorAdapter = new MyCursorAdapter(getActivity(),cursor,true);
        ListView listView = (ListView)view.findViewById(R.id.fragment_bank_listView);
        listView.setAdapter(myCursorAdapter);

        btn_add = (Button) view.findViewById(R.id.fragment_bank_btn);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BankAddDialog bankAddDialog = new BankAddDialog(getContext());
                bankAddDialog.buildAlert();

            }
        });

        //어뎁터에서 리스트가 하나 삭제되면 리프레쉬.  에효 씨발 여기서 refresh 넣어둔거 때문에 계속 액티비티 소환돼서 지랄났었나보다... 으흑흑
      /*  if(myCursorAdapter.isSuccess()){
            refresh();
        }
*/
        return view;
    }

    //DB값 불러와서 ArrayList에 적재
   public void getData(){

        DBHelper dbHelper = DBHelper.getInstance(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        cursor =  db.rawQuery("Select * from tb_bank order by _id desc", null);

    }

    //새로고침
    public void refresh(){
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        getActivity().finish();
    }

    ////////////////////////////////////////
    private class MyCursorAdapter extends CursorAdapter {

        private Cursor mCursor;
        private Context mContext;
        private String _id;
        private boolean isSuccess=false;
        private Handler handler = new Handler();//MainThread에서 생성되는 변수에 핸들러를 생성해 담았으니, 이 핸들러는 메인쓰레드의 메세지큐와 루퍼에 자동연결된다.

        //생성한 쓰레드는 MainThread와 작동을 달리한다.
        //때문에 핸들러를 만들어서 핸들러를 통해 쓰레드의 작업을 MainThread로 보낼 수 있다.
        //기본 생성자를 통해 Handler를 생성하면, 생성되는 Handler는 해당 Handler를 호출한 스레드의 MessageQueue와 Looper에 자동 연결된다.
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                deletedata(_id);
                if(isSuccess==true) {
                    handler.post(new Runnable() { //MainLooper에 연결된 핸들러를 통해 Runnable할 작업을 post(전송)한다.
                        @Override
                        public void run() {
                            //핸들러를 통해 MainThread에서 실행되어야 할 작업을 보내줌. MainThread의 메세지큐로 이 작업할 것이 들어간다. ㅇㅇ;
                            refresh();
                        }
                    });
                }
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
            isSuccess=true;
            db.close();


        }


    }


    //////////////////////////////////////////////////////////

    private class BankAddDialog extends Dialog {

        private EditText bank;
        private EditText number;
        private View alertLayout;
        private long result;
        private String bank_name;
        private String bank_number;


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                result = insertDB(bank_name, bank_number);
                Message message = handler.obtainMessage((int) result);
                handler.sendMessage(message);
            }
        });

        //MainThread의 looper를 갖는 핸들러를 만듦.(Looper.getMainLooper)
        // 즉, MianThread의 looper와 연결되어 있다고 보면 된다. 즉 다른 쓰레드에서 보낸 메세지를 이 핸들러에서 받아서 MainLooper의 메세지큐로 보낸다.
        //해당 핸들러로 들어오는 Message가 어떠냐에 따라 MainThraed에서 실행될 것들을 담는다.
        private Handler handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == result) {
                    if(msg.what==0){
                        Toast.makeText(getContext(),"공백이 있습니다. 확인해 주세요", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(),"등록되었습니다.",Toast.LENGTH_SHORT).show();
                        dismiss();
                        refresh(); //새로고침.
                    }

                }
            }
        };

        public long insertDB(String name, String number){


            DBHelper dbHelper = DBHelper.getInstance(getContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            if(name.equals("")||number.equals("")){
                return 0;
            } else{
                ContentValues values = new ContentValues();
                values.put("bank", name);
                values.put("number",number);
                long result = db.insert("tb_bank",null,values);
                return result;
            }


        }


        public BankAddDialog(@NonNull Context context) {
            super(context);
            LayoutInflater inflater = getLayoutInflater();
            alertLayout = inflater.inflate(R.layout.custom_dialog, null);
            bank = (EditText) alertLayout.findViewById(R.id.edit_name);
            number = (EditText) alertLayout.findViewById(R.id.edit_number);
        }


        public void buildAlert() {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("추적할 체크카드를 저장");
            builder.setView(alertLayout)
                    .setPositiveButton("추가하기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            bank_name =String.valueOf(bank.getText());
                            bank_number = String.valueOf(number.getText());
                            thread.start();//Insert 쓰레드

                        }
                    })
                    .setNegativeButton("취소하기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancel();
                        }
                    })
                    .show();

        }


    }

}
