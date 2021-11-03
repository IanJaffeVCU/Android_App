package com.example.uRemIndMe;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentPolls.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentPolls#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentPolls extends Fragment {

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.recycler_poll,parent,false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            holder.subject.setText(Global.polls.get(position).subject);
            holder.body.setText(Global.polls.get(position).body);
            holder.rejected.setText(Global.polls.get(position).reject.toString());
            holder.accepted.setText(Global.polls.get(position).accept.toString());

            if(Global.documentData.pollsreacted.contains(Global.polls.get(position).time)){
                holder.accept.setVisibility(View.GONE);
                holder.reject.setVisibility(View.GONE);
            }else{
                holder.accept.setVisibility(View.VISIBLE);
                holder.reject.setVisibility(View.VISIBLE);
            }

            holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Global.documentData.pollsreacted.add(Global.polls.get(position).time);
                    Global.polls.get(position).accept++;
                    Global.pollRef.update(Global.documentData.userInfo.batch.toString(),Global.polls);
                    Global.userRef.update("pollsreacted",Global.documentData.pollsreacted);

                    adapter.notifyDataSetChanged();



                }
            });

            holder.reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Global.documentData.pollsreacted.add(Global.polls.get(position).time);
                    Global.polls.get(position).reject++;
                    Global.pollRef.update("polls",Global.polls);
                    Global.userRef.update("pollsreacted",Global.documentData.pollsreacted);

                    adapter.notifyDataSetChanged();

                }
            });
        }


        @Override
        public int getItemCount() {
            return Global.polls.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView subject, body, accepted,rejected;
            Button accept ,reject;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                subject = itemView.findViewById(R.id.recycler_poll_subject);
                body = itemView.findViewById(R.id.recycler_poll_body);
                accepted = itemView.findViewById(R.id.recycler_poll_accepted);
                rejected = itemView.findViewById(R.id.recycler_poll_rejected);
                accept = itemView.findViewById(R.id.recycler_poll_accept);
                reject = itemView.findViewById(R.id.recycler_poll_reject);
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

    public FragmentPolls() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentPolls.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentPolls newInstance(String param1, String param2) {
        FragmentPolls fragment = new FragmentPolls();
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

    RecyclerView rv;

    static RecyclerViewAdapter adapter;
    ImageButton add;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootview = inflater.inflate(R.layout.fragment_polls, container, false);

        if(Global.polls == null){
            Global.polls = new ArrayList<>();
        }
        if(Global.documentData.pollsreacted == null){
            Global.documentData.pollsreacted = new ArrayList<>();
        }

        add =rootview.findViewById(R.id.fragment_poll_add);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogMakePoll dialog = new DialogMakePoll();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                dialog.show(ft,"hello");
            }
        });

        rv = rootview.findViewById(R.id.fragment_poll_rv);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
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
