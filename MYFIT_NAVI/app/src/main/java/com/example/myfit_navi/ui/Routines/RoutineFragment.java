package com.example.myfit_navi.ui.Routines;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfit_navi.DB.CreateRoutine.Routine;
import com.example.myfit_navi.DB.CreateRoutine.RoutineCreateDialogF;
import com.example.myfit_navi.DB.CreateRoutine.RoutineCreateListener;
import com.example.myfit_navi.DB.QueryClass;
import com.example.myfit_navi.DB.ShowRoutine.RoutineViewAdapter;
import com.example.myfit_navi.R;
import com.example.myfit_navi.cfg.Config;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;


public class RoutineFragment extends Fragment implements RoutineCreateListener {

    private QueryClass databaseQueryClass;

    private List<Routine> routineList = new ArrayList<>();

    private TextView routineListEmptyTextView;
    private RecyclerView recyclerView;
    private RoutineViewAdapter routineListRecyclerViewAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        databaseQueryClass = new QueryClass(getActivity());
        return inflater.inflate(R.layout.fragment_routine_list, container, false);
    }
    @Override
    public void onStart() {
        super.onStart();
        Logger.addLogAdapter(new AndroidLogAdapter());

        recyclerView = (RecyclerView) getView().findViewById(R.id.RoutineRecyclerView);
        routineListEmptyTextView = (TextView) getView().findViewById(R.id.emptyRoutineListTextView);

        routineList.addAll(databaseQueryClass.getAllRoutine());

        routineListRecyclerViewAdapter = new RoutineViewAdapter(getActivity(), routineList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(routineListRecyclerViewAdapter);

        viewVisibility();

        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRoutineCreateDialog();
            }
        });

        FloatingActionButton del = (FloatingActionButton) getView().findViewById(R.id.del);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                all_delete();
            }
        });
    }

    public void all_delete(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Are you sure, You wanted to delete all routines?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        boolean isAllDeleted = databaseQueryClass.deleteAllRoutines();
                        if(isAllDeleted){
                            routineList.clear();
                            routineListRecyclerViewAdapter.notifyDataSetChanged();
                            viewVisibility();
                        }
                    }
                });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.create(); // 만들고
        alertDialogBuilder.show(); // 보여준다.
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // 메뉴바 사용할때.

        if(item.getItemId()==R.id.action_delete){

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setMessage("Are you sure, You wanted to delete all routines?");
            alertDialogBuilder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            boolean isAllDeleted = databaseQueryClass.deleteAllRoutines();
                            if(isAllDeleted){
                                routineList.clear();
                                routineListRecyclerViewAdapter.notifyDataSetChanged();
                                viewVisibility();
                            }
                        }
                    });

            alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void viewVisibility() {
        if(routineList.isEmpty())
            routineListEmptyTextView.setVisibility(View.VISIBLE);
        else
            routineListEmptyTextView.setVisibility(View.GONE);
    }

    private void openRoutineCreateDialog() {
        RoutineCreateDialogF routineCreateDialogFragment = RoutineCreateDialogF.newInstance("Create routine", this::onRoutineCreated);
        routineCreateDialogFragment.show(getActivity().getSupportFragmentManager(), Config.CREATE_Routine);
    }

    @Override
    public void onRoutineCreated(Routine routine) {
        routineList.add(routine);
        routineListRecyclerViewAdapter.notifyDataSetChanged();
        viewVisibility();
        Logger.d(routine.getName());
    }
}
