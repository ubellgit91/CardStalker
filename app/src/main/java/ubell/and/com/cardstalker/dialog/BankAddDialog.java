package ubell.and.com.cardstalker.dialog;

import android.app.AlertDialog;
import android.app.Dialog;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ubell.and.com.cardstalker.MainActivity;
import ubell.and.com.cardstalker.R;
import ubell.and.com.cardstalker.database.DBHelper;


public class BankAddDialog extends Dialog {

    private EditText bank;
    private EditText number;
    private View alertLayout;
    private long result;
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            result = insertDB(String.valueOf(bank.getText()), String.valueOf(number.getText()));
        }
    });

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
                .setPositiveButton("추가하기", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        thread.start();//Insert 쓰레드
                        try {
                            Thread.sleep(1000); //쓰레드 1초 실행
                        } catch(InterruptedException e) {
                            e.printStackTrace();
                        }
                        thread.interrupt();

                        if(result==0){
                            Toast.makeText(getContext(),"공백이 있습니다. 확인해 주세요", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(),"등록되었습니다.",Toast.LENGTH_SHORT).show();
                            dismiss();
                        }

                    }
                })
                .setNegativeButton("취소하기", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancel();
                    }
                })
                .show();

    }

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


}
