package com.example.uRemIndMe;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentReminders.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentReminders#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentReminders extends Fragment {

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

        @NonNull
        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.recycler_reminders,parent,false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {

            Global.ModalClasses.AssignmentModal data = Global.documentData.assignment.get(position);
            holder.subname.setText(data.subject);
            Long time = data.timestamp;
            Date date  =  new Date(time);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
            holder.date.setText(dateFormat.format(date));
            holder.time.setText(timeFormat.format(date));
            holder.details.setText(data.details);

        }

        @Override
        public int getItemCount() {
            return Global.documentData.assignment.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            TextView subname,time,date,details;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                subname = itemView.findViewById(R.id.recycler_reminder_subject);
                time = itemView.findViewById(R.id.recycler_reminder_time);
                date = itemView.findViewById(R.id.recycler_reminder_date);
                details = itemView.findViewById(R.id.recycler_reminder_details);
            }
        }
    }


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentReminders() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentReminders.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentReminders newInstance(String param1, String param2) {
        FragmentReminders fragment = new FragmentReminders();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    static RecyclerViewAdapter adapter;
    RecyclerView rv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if(Global.documentData.assignment == null){
            Global.documentData.assignment = new ArrayList<>();
        }

        View rootview = inflater.inflate(R.layout.fragment_reminders, container, false);
        rv = rootview.findViewById(R.id.fragment_reminder_rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecyclerViewAdapter();
        rv.setAdapter(adapter);

        return rootview;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    } */

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
