package ubell.and.com.cardstalker.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ubell.and.com.cardstalker.MainActivity;
import ubell.and.com.cardstalker.MapsActivity;
import ubell.and.com.cardstalker.R;
import ubell.and.com.cardstalker.database.BankDTO;
import ubell.and.com.cardstalker.database.DBHelper;
import ubell.and.com.cardstalker.database.MessageDTO;
import ubell.and.com.cardstalker.dialog.BottomSheetDialog;

public class UsedListFragment extends Fragment {


    private UsedListFragmentRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    ArrayList<Integer> ids = new ArrayList<Integer>();


    @SuppressLint("ValidFragment")
    private UsedListFragment() {

    }

    /*객체 생성 메소드.*/
    public static UsedListFragment newInstance() {

        Bundle args = new Bundle();
        UsedListFragment fragment = new UsedListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //가장 기본이 되는 레이아웃xml을 인플레이트(팽창)시킨다. xml설정파일을 view객체로 만드는 과정임
        View view = inflater.inflate(R.layout.fragment_usedlist, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.UsedListFragment_recyclerView);
        //LayoutManager는 항목의 배치를 위해 쓰인다.
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        //Adapter는 RecyclerView에 들어갈 항목을 구성하는 것.
        //Adapter를
        adapter = new UsedListFragmentRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);


        return view;

    }


    //뷰어댑터 이너클래스.
    public class UsedListFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


        private List<MessageDTO> messageDTOList = new ArrayList<MessageDTO>();
        private List<BankDTO> bankDTOList = new ArrayList<>();


        private Thread startThread = new Thread(new Runnable() {
            @Override
            public void run() {
                DBHelper dbHelperp = DBHelper.getInstance(getActivity());
                SQLiteDatabase db = dbHelperp.getReadableDatabase();
                Cursor cursor = db.rawQuery("select * from tb_mapdb order by _id asc", null);

                while (cursor.moveToNext()) {
                    int _id = cursor.getInt(0);
                    String address = cursor.getString(1);
                    String message = cursor.getString(2);
                    String time = cursor.getString(3);
                    String lat = cursor.getString(4);
                    String lon = cursor.getString(5);

                    //db에서 불러온 값들을 DTO객체를 만들어서 DTO객체를 보관하는 arrayList에 담는다.
                    messageDTOList.add(new MessageDTO(_id, address, message, time, lat, lon));
                }

                cursor = db.rawQuery("select * from tb_bank", null);
                while(cursor.moveToNext()){
                    int _id = cursor.getInt(0);
                    String bank = cursor.getString(1);
                    String number = cursor.getString(2);

                    bankDTOList.add(new BankDTO(_id,bank,number));

                }
                db.close();


            }
        });


        public UsedListFragmentRecyclerViewAdapter() {
            startThread.start();

        }


        //항목 구성을 위한 레이아웃 xml 파일 inflater를 위해 onCreateViewHolder()함수가 먼저 호출됨.
        //이곳의 리턴값은 inflate된 view의 계층구조에서
        //view를 findViewById 할 ViewHolder를 리턴함.
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            //이런 레이아웃을 갖는 뷰를 만들겠다
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usedlist, parent, false);
            //그런 뷰 형태를 갖는 뷰홀더를 만들어라.
            //이 inflate된 곳에서 뷰를 findViewById 하기 위한 ViewHolder를 생성.. CustomViewHolder로 이동.

            CustomViewHolder customViewHolder = new CustomViewHolder(view);

            return customViewHolder; //반환된 ViewHolder를 Adapter 내부에서 메모리에 유지하다가 각 항목을 구성하기 위해 호출되는 onBindViewHolder()를 호출하면서

            //해당 ViewHolder를 아래의 onBindViewHolder()의 매개변수 holder로 보냄.
        }

        //ViewHolder의 데이터를 담는 변수와 값을 바인딩(묶기) 해주는 것.
        //onCreateViewHolder에서 return해주는 뷰홀더가 매개변수 holder로 들어오는 거 같다.
        //항목을 구성하기 위해서 호출된다.
        //중요! RecyclerView에서는 각각의 요소를 클릭했을 때를 처리하는 OnItemClickListener가 없다.
        //그래서 따로 구현을 해주거나 해야한다고 한다..
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            //messageDTOList라는 arrayList에는 MessageDTO 객체가 db의 수만큼 들어가있다.

            MessageDTO messageDTO = messageDTOList.get(position); //position 0번부터 갖고있는 size까지 돌면서 List를 구축함..
            CustomViewHolder customViewHolder = (CustomViewHolder) holder;

            for(BankDTO bankDTO : bankDTOList){
                if(messageDTO.getAddress().equals(bankDTO.getNumber())){
                 customViewHolder.textView_address.setText(bankDTO.getBank());
                }
            }
            Log.i("address",messageDTO.getAddress());
            customViewHolder.textView_message.setText(messageDTO.getMessage());
            customViewHolder.textView_date.setText(messageDTO.getTime().toString());
            //항목을 구성하면서 한꺼번에 출력되는구나...
            //배열이나 리스트를 만들어서 거기에 한꺼번에 담아버리게 끔 해버리고
            //그 리스트를 BottomSheet객체에 넘겨줘서 position과 함께 쓰도록해버리자.
            ids.add(messageDTO.get_id());

           /* Log.i("get_id", String.valueOf(messageDTO.get_id())); //각각 포지션이 가지는 id를 넣는 수는 없을까...*/

            //_id = String.valueOf(messageDTO.get_id()); //id는 20. 가장 나중 것이 들어가는거 같음. // id값이랑 position값을 어떻게 각각 집어넣지


        }

        //항목의 개수. 이게 위의 onBindViewHolder의 position값으로 들어가는 것 같다.
        @Override
        public int getItemCount() {

            return messageDTOList.size();
        }


    }


    //필요한 뷰(시각적으로 보이는 요소들)를 생성해서 홀더(이를테면 보관함)에 보관한다.
    //RecyclerView는 Adapter에 ViewHolder의 적용이 강제되어있음. 따라서 ViewHolder 클래스를 먼저 정의해줘야함
    //ViewHolder의 역할은 항목을 구성하기 위한 뷰들을 findViewById 해주는 역할이며, ViewHolder객체를 Adapter내부에서 메모리에 유지해줌으로써
    //ViewHolder에 의해 최초에 한번만!! findViewById 하기 위해서임. 뷰 홀더에서는 항목을 구성하기 위한 뷰를 한번만 findViewById 할 수 있도록 알고리즘을 짜면 된다.

    private class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textView_address;
        public TextView textView_message;
        public TextView textView_date;
        public LinearLayout linearLayout;

        public CustomViewHolder(View itemView) {
            super(itemView);

            /*itemView.setOnClickListener(this); //뷰에다가 OnClickListener를 셋팅해줌..*/

            linearLayout = (LinearLayout) itemView.findViewById(R.id.usedlist_layout);
            textView_address = (TextView) itemView.findViewById(R.id.usedlist_textView_add);
            textView_message = (TextView) itemView.findViewById(R.id.uselist_message);
            textView_date = (TextView) itemView.findViewById(R.id.usedlist_date);
            linearLayout.setOnClickListener(this);

            textView_message.setSingleLine(true);
            textView_message.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            textView_message.setSelected(true);

        }

        @Override
        public void onClick(View v) {
            int itemPosition = getAdapterPosition(); //0번부터 안나오게 하기 위함..
            BottomSheetDialog bottomSheetDialog = BottomSheetDialog.getInstance();
            bottomSheetDialog.setPosition(itemPosition);//몇번 째 포지션인지.
            bottomSheetDialog.set_ids(ids); //클래스가 다르기때문에 전체클래스를 아우르는 멤버변수 ids로 선언해줬다.
            bottomSheetDialog.show(getFragmentManager(), "bottomSheet");

        }
    }


}
