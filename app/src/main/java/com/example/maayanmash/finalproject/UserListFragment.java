package com.example.maayanmash.finalproject;


import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.maayanmash.finalproject.Model.Model;
import com.example.maayanmash.finalproject.Model.entities.User;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserListFragment extends Fragment {
    ListView list;
    MyAdapter myAdapter = new MyAdapter();;
    UserListViewModel dataModel;

    public static UserListFragment newInstance() {
        UserListFragment fragment = new UserListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataModel = ViewModelProviders.of(this).get(UserListViewModel.class);
        dataModel.getData().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                myAdapter.notifyDataSetChanged();
                Log.d("TAG","notifyDataSetChanged" + users.size());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Model.instance.cancellGetAllUsers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);

        list = view.findViewById(R.id.driversList_list);
        list.setAdapter(myAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("TAG","item selected:" + i);
            }
        });
        return view;
    }



    @Override
    public void onDetach() {
        super.onDetach();
    }

    //Menu
    @SuppressLint("ResourceType")
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        getActivity().getMenuInflater().inflate(R.menu.back_manu, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        FragmentTransaction tran = getActivity().getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.back:
                ManagerFragment fragment= new ManagerFragment();
                Bundle bundle = new Bundle();
                MainActivity activity= (MainActivity) getActivity();
                bundle.putString("uid", activity.getuID());
                fragment.setArguments(bundle);

                tran.replace(R.id.main_container, fragment);
                tran.addToBackStack("tag");
                tran.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    class MyAdapter extends BaseAdapter {
        public MyAdapter(){
        }

        @Override
        public int getCount() {
            return dataModel.getData().getValue().size();

        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            if (view == null){
                view = LayoutInflater.from(getActivity()).inflate(R.layout.driver_list_item,null);
            }

            final User user = dataModel.getData().getValue().get(i);

            TextView nameTv = view.findViewById(R.id.ListItem_name_tv);
            TextView addressTv = view.findViewById(R.id.ListItem_address_tv);
            TextView phoneTv = view.findViewById(R.id.ListItem_phone_tv);
            final ImageView avatarView = view.findViewById(R.id.ListItem_avatar);

            nameTv.setText(user.name);
            if (user.address!=null)
                addressTv.setText(user.address);
            if (user.phone!=null)
                phoneTv.setText(user.phone);
            avatarView.setImageResource(R.drawable.driver);
            avatarView.setTag(user.uid);
            if (user.getImage() != null){
                Model.instance.getImage(user.getImage(), new Model.GetImageListener() {
                    @Override
                    public void onDone(Bitmap imageBitmap) {
                        if (user.uid.equals(avatarView.getTag()) && imageBitmap != null) {
                            avatarView.setImageBitmap(imageBitmap);
                        }
                    }
                });
            }
            return view;
        }
    }

}
