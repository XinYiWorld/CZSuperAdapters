package xinyi.com.samples;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xinyi.czsuperadapter.AVLoadingIndicatorView;
import com.xinyi.czsuperadapter.interfaces.LoaderListener;
import com.xinyi.czsuperadapter.main.CZSuperAdapter;
import com.xinyi.czsuperadapter.main.CommonViewHolder;
import com.xinyi.czsuperadapter.main.LoadController;
import com.xinyi.czsuperadapter.main.MultiTypeMaker;
import com.xinyi.czsuperadapter.main.RefreshController;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private RefreshController refreshController;


    private Handler handler = new Handler();
    private LoadController loadController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rcv);

        final CZSuperAdapter mAdapter = new CZSuperAdapter(this, recyclerView, new MultiTypeMaker<String>() {
            @Override
            public int getType(int position) {
                Log.i(TAG, "getType: position = " + position);
                if (position % 2 == 0) {
                    return 0;
                } else {
                    return 1;
                }
            }

            @Override
            public int getLayoutId(int viewType) {
                return  R.layout.simple_item_view_main;
            }

            @Override
            public void bindViewHolder(CommonViewHolder holder, String data, int viewType, int position) {
                TextView tv = holder.findViewById(R.id.tv_main);
                if (viewType == 0) {
                    tv.setText(data);
                } else {
                    tv.setText(data + "222222222");
                }
                if (position % 2 == 0) {
                    tv.setTextColor(Color.RED);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
            }
        });

        mAdapter.setOnItemClickListener(new CommonViewHolder.OnItemClickListener() {
            @Override
            public void onItemClick(View convertView, int position) {
                Toast.makeText(MainActivity.this, position + "", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onItemClick: " +  position);
            }
        });

        mAdapter.setOnItemLongClickListener(new CommonViewHolder.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View convertView, int position) {
                Toast.makeText(MainActivity.this, position + "", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onItemLongClick: " + position);
                return true;
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // set divider
        DividerLine dividerLine = new DividerLine(DividerLine.VERTICAL);
        dividerLine.setSize(5);
        dividerLine.setColor(Color.parseColor("#f0eff4"));
        recyclerView.addItemDecoration(dividerLine);

        List<String> temp = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            temp.add("我是第" + (i + 1) + "个条目");
        }
        mAdapter.addAll(temp);
        recyclerView.setAdapter(mAdapter);
//        recyclerView.requestLayout();


//        refreshController = new RefreshController.Builder().setOnRefreshListener(new RefreshListener() {
//            @Override
//            public void onRefresh() {
////                handler.postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
//                        refreshController.finishRefresh();
////                    }
////                }, 1000);
//            }
//
//            @Override
//            public void onPull(int distance) {
//
//            }
//        })
//                .setRefreshIndicatorStyle(AVLoadingIndicatorView.Pacman)
//                .setRefreshIndicatorColor(Color.RED)
//                .build().create();
//
//        mAdapter.setRefreshController(refreshController);


        loadController = new LoadController.Builder().setOnLoaderListener(new LoaderListener() {
            @Override
            public void onLoadMore() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mAdapter.size() > 15) {
                            loadController.finishLoadMore(false);
                        } else {
                            mAdapter.add("我是加载更多的数据");
                            recyclerView.requestLayout();
                            loadController.finishLoadMore(true);
                        }
                    }
                }, 1000);
            }

            @Override
            public void onScroll(int detX, int detY) {
                Log.i(TAG, "onScroll: dety = " + detY);
            }

            @Override
            public void onScrollStateChanged(int newState) {

            }
        })
                .setLoadIndicatorStyle(AVLoadingIndicatorView.Pacman)
                .setLoadIndicatorColor(Color.GREEN)
//                .setLoadMode(LoadMode.CLICK_TO_LOAD)
                .build().create();

        mAdapter.setLoadController(loadController);

//
//        MultiTypeMaker header1 = new MultiTypeMaker<String>() {
//            @Override
//            public int getType(int position) {
//                return 0;
//            }
//
//            @Override
//            public int getLayoutId(int viewType) {
//                return  R.layout.simple_item_view_header;
//            }
//
//            @Override
//            public void bindViewHolder(CommonViewHolder holder, String data, int viewType, int position) {
//                TextView text1 = holder.findViewById(R.id.tv_header);
//
//                text1.setText(data);
//            }
//        };
//        header1.setData("我是头q1");
//        mAdapter.addHeader(header1);
//
//        MultiTypeMaker header2 = new MultiTypeMaker<String>() {
//            @Override
//            public int getType(int position) {
//                return 0;
//            }
//
//            @Override
//            public int getLayoutId(int viewType) {
//                return R.layout.simple_item_view_header;
//            }
//
//            @Override
//            public void bindViewHolder(CommonViewHolder holder, String data, int viewType, int position) {
//                TextView text1 = holder.findViewById(R.id.tv_header);
//                text1.setText(data);
//            }
//        };
//        header2.setData("我是头q2");
//        mAdapter.addHeader(header2);
//
//
//        MultiTypeMaker footerTypeMaker = new MultiTypeMaker<String>() {
//            @Override
//            public int getType(int position) {
//                return 0;
//            }
//
//            @Override
//            public int getLayoutId(int viewType) {
//                return  R.layout.simple_item_view_footer;
//            }
//
//            @Override
//            public void bindViewHolder(CommonViewHolder holder, String data, int viewType, int position) {
//                TextView text1 = holder.findViewById(R.id.tv_footer);
//                text1.setText(data);
//            }
//
//        };
//        footerTypeMaker.setData("我是脚1。。。");
//        mAdapter.addFooter(footerTypeMaker);
//
//        MultiTypeMaker footerTypeMaker2 = new MultiTypeMaker<String>() {
//            @Override
//            public int getType(int position) {
//                return 0;
//            }
//
//            @Override
//            public int getLayoutId(int viewType) {
//                return  R.layout.simple_item_view_footer;
//            }
//
//            @Override
//            public void bindViewHolder(CommonViewHolder holder, String data, int viewType, int position) {
//                TextView text1 = holder.findViewById(R.id.tv_footer);
//                text1.setText(data);
//            }
//
//        };
//        footerTypeMaker2.setData("我是脚2。。。");
//        mAdapter.addFooter(footerTypeMaker2);
    }
}
