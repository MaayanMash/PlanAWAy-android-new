package com.example.maayanmash.finalproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.example.maayanmash.finalproject.Model.Model;
import com.example.maayanmash.finalproject.Model.ModelFirebase;
import com.example.maayanmash.finalproject.Model.entities.Destination;
import com.example.maayanmash.finalproject.Model.entities.SubTask;
import com.example.maayanmash.finalproject.Model.entities.TaskRow;

import java.util.ArrayList;
import java.util.List;


public class MyTaskListFragment extends Fragment {
    private ListAdapter adapter = new ListAdapter();
    ListView list;
    //TaskListViewModel dataModel;
    String uid;
    boolean isManager;

    public MyTaskListFragment() {
        // Required empty public constructor
    }

    public static MyTaskListFragment newInstance(String uid, boolean isManager) {

        MyTaskListFragment myFragment = new MyTaskListFragment();
        Bundle args = new Bundle();
        args.putString("uid", uid);
        args.putString("isMamager", String.valueOf(isManager));
        myFragment.setArguments(args);
        return myFragment;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle =this.getArguments();
        uid = bundle.getString("uid");
        isManager= Boolean.parseBoolean(bundle.getString("isMamager"));

        View view = inflater.inflate(R.layout.fragment_my_task_list, container, false);
        ListView list = view.findViewById(R.id.tasks_list);

        if(isManager){
            Model.instance.getDestinationsBymID(uid, new MainActivity.GetDestinationsForUserIDCallback() {
                @Override
                public void onDestination(ArrayList<Destination> destinations, String taskID, List<TaskRow> taskRowList) {
                    for (TaskRow tr:taskRowList){
                        adapter.data.add(tr);
                    }
                    adapter.notifyDataSetChanged();
                }
            });
        }
        else {
            Model.instance.getMyDestinationsByID(uid, new MainActivity.GetDestinationsForUserIDCallback() {
                @Override
                public void onDestination(ArrayList<Destination> destinations, String taskID, List<TaskRow> taskRowList) {
                    for (TaskRow tr : taskRowList) {
                        adapter.data.add(tr);
                    }
                    adapter.notifyDataSetChanged();
                }
            });
        }

        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Log.d("TAG","item in row " + i + "was selected");
            }
        });

        return view;
    }


    public interface OnTodayMySubTasksCallback{
        void  getSubTasks(List<SubTask> list);
    }

    class ListAdapter extends BaseAdapter {
        public List<TaskRow> data = new ArrayList<>();

        @Override
        public int getCount() {
            return data.size();
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
                view =LayoutInflater.from(getActivity()).inflate(R.layout.subtask_row,null);
                CheckBox cb = view.findViewById(R.id.subtask_row_checkbox_id);
                cb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int index = (int) view.getTag();
                        TaskRow tr = data.get(index);
                        Boolean isDone=!tr.isDone();
                        tr.setDone(isDone);
                        Model.instance.updateDestinationArrivalForTask(tr.getDid(),isDone);
                    }
                });
            }
            TaskRow tr = data.get(i);
            CheckBox cb = view.findViewById(R.id.subtask_row_checkbox_id);
            cb.setTag(i);
            cb.setChecked(tr.isDone());
            TextView name = view.findViewById(R.id.subtask_row_name_id);
            TextView address = view.findViewById(R.id.subtask_row_address_id);
            name.setText(tr.getName());
            address.setText(tr.getAddress());
            return view;
        }
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
                Fragment fragment;
                if(isManager){
                    fragment= new ManagerFragment();
                }
                else {
                    fragment = new MapFragment();
                }
                Bundle bundle = new Bundle();
                bundle.putString("uid", uid);
                fragment.setArguments(bundle);
                tran.replace(R.id.main_container, fragment);
                tran.addToBackStack("tag");
                tran.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




}