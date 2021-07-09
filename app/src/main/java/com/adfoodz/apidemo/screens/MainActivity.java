package com.adfoodz.apidemo.screens;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adfoodz.apidemo.R;
import com.adfoodz.apidemo.api.APIClient;
import com.adfoodz.apidemo.api.APIInterface;
import com.adfoodz.apidemo.models.UserDetails;
import com.adfoodz.apidemo.models.UserResponse;
import com.adfoodz.apidemo.utility.GlideImageLoader;
import com.adfoodz.apidemo.utility.Utility;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    UserAdapter adapter;
    LinearLayout llProgressBar, llEmptyView;
    SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setToolbar();

        recyclerView = findViewById(R.id.recyclerView);
        llProgressBar = findViewById(R.id.llProgressBar);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        llEmptyView = findViewById(R.id.llEmptyView);

        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUsers();
            }
        });

        swipeRefresh.post(new Runnable() {
            @Override
            public void run() {
                swipeRefresh.setRefreshing(true);
                getUsers();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getUsers();
    }

    public void getUsers() {
        llProgressBar.setVisibility(View.VISIBLE);
        APIClient
                .getClient(MainActivity.this)
                .create(APIInterface.class)
                .getUsers()
                .enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        llProgressBar.setVisibility(View.GONE);
                        if (swipeRefresh.isRefreshing())
                            swipeRefresh.setRefreshing(false);
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                UserResponse apiResponse = response.body();

                                if (apiResponse.getData().size() > 0) {
                                    adapter = new UserAdapter(MainActivity.this, apiResponse.getData());
                                    recyclerView.setAdapter(adapter);
                                    if (apiResponse.getData().size() > 0) {
                                        llEmptyView.setVisibility(View.GONE);
                                    } else {
                                        llEmptyView.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    llEmptyView.setVisibility(View.VISIBLE);
                                }
                            } else {
                                llEmptyView.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Utility.showError(MainActivity.this, response.errorBody());
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        llProgressBar.setVisibility(View.GONE);
                        if (swipeRefresh.isRefreshing())
                            swipeRefresh.setRefreshing(false);
                        Log.e("onFailure", t.toString() + "");
                        if (t instanceof com.adfoodz.partner.api.NoConnectivityException) {
                            Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        call.cancel();
                    }
                });
    }

    class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

        private List<UserDetails> listData;
        Activity context;

        public UserAdapter(Activity context, List<UserDetails> listData) {
            this.context = context;
            this.listData = listData;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_view, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            UserDetails data = listData.get(position);

            new GlideImageLoader(holder.imgUser, holder.progress).load(data.getAvatar(), Utility.getGlideRequestOptions());

            holder.tvName.setText(data.getFirst_name() + " " + data.getLast_name());
            holder.tvEmail.setText(data.getEmail());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }

        @Override
        public int getItemCount() {
            return listData.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imgUser;
            TextView tvName, tvEmail;
            ProgressBar progress;

            public MyViewHolder(View view) {
                super(view);
                imgUser = view.findViewById(R.id.imgUser);
                tvName = view.findViewById(R.id.tvName);
                tvEmail = view.findViewById(R.id.tvEmail);
                progress = view.findViewById(R.id.progress);
            }
        }
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Users");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}