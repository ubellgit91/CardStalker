package ubell.and.com.cardstalker.fragment;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ubell.and.com.cardstalker.R;
import ubell.and.com.cardstalker.database.DBHelper;
import ubell.and.com.cardstalker.database.MessageDTO;
import ubell.and.com.cardstalker.dialog.BottomSheetDialog;

public class UsedListFragment extends Fragment {


    private UsedListFragmentRecyclerViewAdapter adapter;

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
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.UsedListFragment_recyclerView);
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
        private int mCurrentItemSize;
        private String _id, mLat, mLon, address, message;
        private int mPosition;
        private Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                DBHelper dbHelperp = DBHelper.getInstance(getActivity());
                SQLiteDatabase db = dbHelperp.getReadableDatabase();
                Cursor cursor = db.rawQuery("select * from tb_mapdb order by _id desc", null);

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
            }
        });


        public UsedListFragmentRecyclerViewAdapter() {
            thread.start();


           /* //흠이게 아닌데... 굳이 메세지 목록까지 불러올 필요가 있나?
            messageDTOList = new ArrayLis t<>();
            //여기 안에다가 DTO값을 불러와야 할거 같은데 흠...
            ReadSMS readSMS = ReadSMS.getInstance();
            readSMS.readSMSMessage(getActivity()); //Fragment에서는 getActivity하면 Context를 얻어올 수 있다.

            //이 값을 불러오는게 아니라 DB에 있는 데이터를 불러오도록 수정하자.
            messageDTOList = readSMS.getMessageDTOArrayList();
*/

        }

        public void updateData() {
            messageDTOList.clear();

            //DataSet에 변경이 일어났을 때 notify*수정*시킨다. 어댑터 자체를..
            adapter.notifyDataSetChanged();

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
            //해당 ViewHolder를 매개변수로 보냄.
        }

        //ViewHolder의 데이터를 담는 변수와 값을 바인딩(묶기) 해주는 것.
        //onCreateViewHolder에서 return해주는 뷰홀더가 매개변수 holder로 들어오는 거 같다.
        //항목을 구성하기 위해서 호출된다.
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            MessageDTO messageDTO = messageDTOList.get(position);
            CustomViewHolder customViewHolder = (CustomViewHolder) holder;
            customViewHolder.textView_address.setText(messageDTO.getAddress());
            customViewHolder.textView_message.setText(messageDTO.getMessage());
            customViewHolder.textView_date.setText(messageDTO.getTime().toString());
            _id = String.valueOf(messageDTO.get_id());
            customViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    BottomSheetDialog bottomSheetDialog = BottomSheetDialog.getInstance();
                    bottomSheetDialog.setDb_id(_id);
                    bottomSheetDialog.show(getFragmentManager(), "bottomSheet");

                }
            });


            /*    customViewHolder.textView_message;
             *//*          .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(getContext(), position+"번째 흔적", Toast.LENGTH_SHORT).show();






                    *//**//* AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    builder.setMessage("사용 지역을 보시겠습니까?")
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    //Intent(context, 이동할 activity)
                                    Intent intent = new Intent(getActivity(), MapsActivity.class);
                                    intent.putExtra("lat", messageDTO.getLat());
                                    intent.putExtra("lon", messageDTO.getLon());
                                    startActivity(intent);

                                }
                            });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();*//**//*

                }
            });
*/

        }

        //항목의 개수
        @Override
        public int getItemCount() {

            return messageDTOList.size();
        }


    }


    //필요한 뷰를 생성해서 홀더(이를테면 보관함)에 보관한다.
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

            itemView.setOnClickListener(this); //뷰에다가 OnClickListener를 셋팅해줌..

            linearLayout = (LinearLayout) itemView.findViewById(R.id.usedlist_layout);
            textView_address = (TextView) itemView.findViewById(R.id.usedlist_textView_add);
            textView_message = (TextView) itemView.findViewById(R.id.uselist_message);
            textView_date = (TextView) itemView.findViewById(R.id.usedlist_date);

        }

        @Override
        public void onClick(View v) {


        }
    }


}
