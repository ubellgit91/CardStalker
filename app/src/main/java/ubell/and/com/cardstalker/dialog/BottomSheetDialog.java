package ubell.and.com.cardstalker.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import ubell.and.com.cardstalker.MainActivity;
import ubell.and.com.cardstalker.MapsActivity;
import ubell.and.com.cardstalker.R;
import ubell.and.com.cardstalker.database.DBHelper;
import ubell.and.com.cardstalker.fragment.UsedListFragment;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    private static BottomSheetDialog instance;
    private LinearLayout mTrace;
    private LinearLayout mEraser;
    private DBHelper dbHelper;
    private String lat;
    private String lon;
    private String position;
    private String db_id;
    private UsedListFragment.UsedListFragmentRecyclerViewAdapter adapter;
    private String title = "발생한 흔적";
    private String snippet;
    private Handler handler;

    @SuppressLint("ValidFragment")
    private BottomSheetDialog() {
        dbHelper = DBHelper.getInstance(getContext());
        handler = new Handler();
    }

    public static BottomSheetDialog getInstance() {
        if (instance == null) {

            instance = new BottomSheetDialog();
        }

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottomsheet_dialog, container, false);
        mTrace = (LinearLayout) view.findViewById(R.id.trace_map);
        mEraser = (LinearLayout) view.findViewById(R.id.eraser_trace);


        mTrace.setOnClickListener(new View.OnClickListener() {
            //구글 맵 띄우기.
            @Override
            public void onClick(View v) {


                selectData(db_id);
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra("lat", lat);
                intent.putExtra("lon", lon);
                intent.putExtra("title", title);
                intent.putExtra("snippet", snippet);
                startActivity(intent);

            }
        });

        mEraser.setOnClickListener(new View.OnClickListener() {

            //DB에서 데이터 가져와서 지우기.
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle("흔적 지우기");
                alertDialog.setMessage(position + "번째 흔적 입니다. 지우시겠습니까?");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //witch는 dialog 목록이 여러개일 때 쓰는 position과 같은 역할인가봄.
                        deleteData(db_id);

                    }
                })

                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.cancel();

                            }
                        })
                        .show();

            }
        });


        return view;
    }

    public void setDb_id(String db_id) {
        this.db_id = db_id;
    }

    public void deleteData(String id) {

        String deleteSQL = "DELETE FROM tb_mapdb WHERE _id =" + id;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(deleteSQL);
        db.close();
        adapter.notifyDataSetChanged();

        //새로고침 하는 것. 그냥 인텐트를 새로 하나 만들어서 기존 페이지를 꺼버리고 페이지를 새로 열어줌
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        getActivity().finish();
    }

    public void selectData(String id){

        String selectSQL = "select * from tb_mapdb where _id ="+id;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectSQL,null);
        while (cursor.moveToNext()){
         lat= cursor.getString(cursor.getColumnIndex("lat"));
         lon= cursor.getString(cursor.getColumnIndex("lon"));
         snippet = cursor.getString(cursor.getColumnIndex("message"));
        }
        db.close();

    }


}
