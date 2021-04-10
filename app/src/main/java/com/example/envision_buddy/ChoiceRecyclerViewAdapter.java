package com.example.envision_buddy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ChoiceRecyclerViewAdapter extends RecyclerView.Adapter<com.teaminversion.envisionbuddy.ChoiceRecyclerViewAdapter.RecyclerViewHolder> {

    private ArrayList<String> arrayList;
    private final Context context;

    public ChoiceRecyclerViewAdapter(ArrayList<String> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public com.teaminversion.envisionbuddy.ChoiceRecyclerViewAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_layout_choice, parent, false);
        return new com.teaminversion.envisionbuddy.ChoiceRecyclerViewAdapter.RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull com.teaminversion.envisionbuddy.ChoiceRecyclerViewAdapter.RecyclerViewHolder holder, int position) {
        holder.choiceTextView.setText(arrayList.get(position));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = arrayList.get(position);
                ProgressDialog progress = new ProgressDialog(context);
                progress.setMessage("Retrieving data");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setIndeterminate(true);
                progress.setCancelable(false);
                progress.show();
                com.teaminversion.envisionbuddy.ChoiceActivity.models.clear();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(com.teaminversion.envisionbuddy.API.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                com.teaminversion.envisionbuddy.API myApi = retrofit.create(com.teaminversion.envisionbuddy.API.class);
                Call<ArrayList<com.teaminversion.envisionbuddy.JSONProcessActivity>> call = myApi.getResult("old-smoke-4544", word);
                call.enqueue(new Callback<ArrayList<com.teaminversion.envisionbuddy.JSONProcessActivity>>() {
                    @Override
                    public void onResponse(Call<ArrayList<com.teaminversion.envisionbuddy.JSONProcessActivity>> call, Response<ArrayList<com.teaminversion.envisionbuddy.JSONProcessActivity>> response) {
                        ArrayList<com.teaminversion.envisionbuddy.JSONProcessActivity> searchResults = response.body();
                        for (int i=0; i<searchResults.size(); i++){
                            if (searchResults.get(i).getSource().equals("Poly")) {
                                Map<String, String> modelInfo = new HashMap<>();
                                modelInfo.put("name", searchResults.get(i).getName());
                                modelInfo.put("thumbnail", searchResults.get(i).getThumbnail());
                                modelInfo.put("url", searchResults.get(i).getGltfUrl());
                                com.teaminversion.envisionbuddy.ChoiceActivity.models.add(modelInfo);
                            }
                        }

                        progress.dismiss();
                        if (!com.teaminversion.envisionbuddy.ChoiceActivity.models.isEmpty()) {
                            context.startActivity(new Intent(context, com.teaminversion.envisionbuddy.ModelsActivity.class));
                        }else{
                            Snackbar snackbar = Snackbar.make(v, "No 3D models found", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<com.teaminversion.envisionbuddy.JSONProcessActivity>> call, Throwable t) {
                        progress.dismiss();
                        Snackbar snackbar = Snackbar.make(v, "Couldn't fetch data", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView choiceTextView;
        private CardView cardView;

        public RecyclerViewHolder(@NonNull View view) {
            super(view);
            choiceTextView = view.findViewById(R.id.choiceTextView);
            cardView = view.findViewById(R.id.cardView);
        }
    }

}