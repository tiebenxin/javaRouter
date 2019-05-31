package com.example.mychat;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.mychat.adapter.AdapterFirst;
import com.example.mychat.adapter.AdapterSecond;
import com.example.mychat.bean.FirstBean;
import com.example.mychat.multi.MultiTypeAdapter;
import com.example.mychat.springview.container.DefaultFooter;
import com.example.mychat.springview.container.DefaultHeader;
import com.example.mychat.springview.widget.SpringView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * Created by Liszt on 2019/2/25.
 */

@Route(path = "/chat/chat")
public class ChatActivity extends AppCompatActivity {

    private List<String> mDataList;
    private SpringView springView;
    private int pullPosition = 0;
    private AdapterTest adapterTest;
    private int loadPosition = 0;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        springView = findViewById(R.id.springView);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        initAdapter();

        initSpringView();
    }

    private void initAdapter() {
        adapterTest = new AdapterTest(this);
        adapterTest.setData(getDefaltData());
        recyclerView.setAdapter(adapterTest);
    }

//    private void initAdapter() {
//        AdapterFirst adapterFirst = new AdapterFirst();
//        AdapterSecond adapterSecond = new AdapterSecond();
//
//        MultiTypeAdapter multiTypeAdapter = new MultiTypeAdapter();
//        multiTypeAdapter.register(FirstBean.class, adapterFirst);
//        multiTypeAdapter.register(String.class, adapterSecond);
//
//        multiTypeAdapter.setItems(getDefaltData());
//
//        recyclerView.setAdapter(multiTypeAdapter);
//
//    }

//    private List<Object> getDefaltData() {
//        mDataList = new ArrayList<>();
//        for (int i = 0; i < 50; i++) {
//            if (i % 3 == 0) {
//                String s = "title" + i;
//                mDataList.add(s);
//            } else {
//                FirstBean firstBean = new FirstBean();
//                firstBean.setName("Name" + i);
//                firstBean.setSex(i % 2);
//                System.out.println("性别=" + i % 2 + "--- i=" + i);
//                firstBean.setAge(i);
//                mDataList.add(firstBean);
//            }
//        }
//        return mDataList;
//    }

    private List<String> getDefaltData() {
        mDataList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            String s = "这是第" + i + "个数据";
            mDataList.add(s);

        }
        return mDataList;
    }

    public void initSpringView() {
        springView.setHeader(new DefaultHeader(this));
        springView.setFooter(new DefaultFooter(this));
        springView.setListener(new SpringView.OnFreshListener() {
            @Override
            public void onRefresh() {
                //下拉刷新
                onSpringRefresh();
            }

            @Override
            public void onLoadMore() {
                onSpringLoadMore();
            }
        });

    }

    public void onSpringRefresh() {
        // pullToRefresh();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pullToRefresh();
                springView.onFinishFreshAndLoad();
            }
        }, 1000);

    }

    private void pullToRefresh() {
        pullPosition = pullPosition + 1;
        String s = "下拉刷新的第" + pullPosition + "个数据";
        mDataList.add(0, s);
        adapterTest.setData(mDataList);
    }

    private void onSpringLoadMore() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadMore();
                springView.onFinishFreshAndLoad();
            }
        }, 1000);


    }

    private void loadMore() {
        loadPosition = loadPosition + 1;
        String s = "加载更多的第" + loadPosition + "个数据";
        mDataList.add(s);
        adapterTest.setData(mDataList);
    }

    private void loadData() {


    }
}
