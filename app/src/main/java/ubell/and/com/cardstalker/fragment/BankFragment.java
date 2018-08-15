package ubell.and.com.cardstalker.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


import java.util.ArrayList;

import ubell.and.com.cardstalker.MainActivity;
import ubell.and.com.cardstalker.R;
import ubell.and.com.cardstalker.database.BankDTO;
import ubell.and.com.cardstalker.database.DBHelper;
import ubell.and.com.cardstalker.dialog.BankAddDialog;

public class BankFragment extends Fragment {

    private Cursor cursor;
    private Button btn_add;
    private Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            getData();
        }
    });

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
        thread.start();
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

               //dialog를 통해 값이 하나 추가되면 리프레쉬.



            }
        });

        //어뎁터에서 리스트가 하나 삭제되면 리프레쉬.  에효 씨발 여기서 refresh 넣어둔거 때문에 계속 액티비티 소환돼서 지랄났었나보다... 으흑흑

        return view;
    }

    //DB값 불러와서 ArrayList에 적재
   public void getData(){

        DBHelper dbHelper = DBHelper.getInstance(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        cursor =  db.rawQuery("Select * from tb_bank order by _id desc", null);

    }

    public void refresh(){
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        getActivity().finish();
    }


}
