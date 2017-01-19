CZSuperAdapter
===================================
A super adapter for recyclerview,you can combine function part like refresh,loadmore,head,foot,multitype free as well as you want(适用于RecyclerView的万能Adapter，方便控制刷新、加载更多、头、脚、多视图。)
## Download
1. first,edit your application build.gradle<br />
```Groovy
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```
<br />
2. second,edit your module build.gradle<br />
```Groovy
dependencies {
    ...
    compile 'com.github.XinYiWorld:CZSuperAdapters:1.0'
}
```

## Use
1. define your adapter<br />
`Warning:`you need to pass RecyclerView for the constructor of CZSuperAdapter,because I need to bind touch event with RecylerView in CZSuperAdapter to achive refresh and load more functions.
```Java
 final CZSuperAdapter mAdapter = new CZSuperAdapter(mContext, recyclerView, new MultiTypeMaker<String>() {
            @Override
            public int getType(int position) {
                Log.i(TAG, "getType: position = " + position);
                //if you has more than one type view,you need to return different type.
                if (position % 2 == 0) {
                    return 0;
                } else {
                    return 1;
                }
            }

            @Override
            public int getLayoutId(int viewType) {
                return android.R.layout.simple_list_item_1;
                //you can return different layout for the different viewtype.
            }

            @Override
            public void bindViewHolder(CommonViewHolder holder, String data, int viewType, int position) {
                //bind your data with your view here
                TextView tv = holder.findViewById(android.R.id.text1);
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
        
         recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        //simulate some data for adapter
        List<String> temp = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            temp.add("我是第" + (i + 1) + "个条目");
        }
        mAdapter.addAll(temp);
        recyclerView.setAdapter(mAdapter);
        recyclerView.requestLayout();
```
<br />
2. extra functions
    <br />
   * enable refresh
   ```Java
   refreshController = new RefreshController.Builder().setOnRefreshListener(new RefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshController.finishRefresh();
                    }
                }, 1000);
            }

            @Override
            public void onPull(int distance) {

            }
        })
        //custom  refresh style
                .setRefreshIndicatorStyle(AVLoadingIndicatorView.Pacman)
                .setRefreshIndicatorColor(Color.RED)
                .setPullArrowImage(android.R.drawable.arrow_up_float)
                .build().create();

        mAdapter.setRefreshController(refreshController);       //call this method to enable refresh
   ```
   * enable loadmore
   ```Java
     loadController = new LoadController.Builder().setOnLoaderListener(new LoaderListener() {
            @Override
            public void onLoadMore() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mAdapter.size() > 30) {
                            loadController.finishLoadMore(false);
                        } else {
                            mAdapter.add("我是加载更多的数据");

                            recyclerView.requestLayout();
                            loadController.finishLoadMore(true);
                        }
                    }
                }, 2000);
            }
        })
        //custom  loadmore style
                .setLoadIndicatorStyle(AVLoadingIndicatorView.Pacman)
                .setLoadIndicatorColor(Color.GREEN)
                .setLoadMode(LoadMode.CLICK_TO_LOAD)            //the default mode is SCROLL_BOTTOM_TO_LOAD
                .build().create();

        mAdapter.setLoadController(loadController);             //call this method to enable loadmore

   ```
   * add header
   ```Java
    MultiTypeMaker header1 = new MultiTypeMaker<String>() {
            @Override
            public int getType(int position) {
                return 0;
            }

            @Override
            public int getLayoutId(int viewType) {
                return android.R.layout.simple_list_item_1;
            }

            @Override
            public void bindViewHolder(CommonViewHolder holder, String data, int viewType, int position) {
                TextView text1 = holder.findViewById(android.R.id.text1);
                text1.setText(data);
            }
        };
        header1.setData("head view 1");
        mAdapter.addHeader(header1);            //call this method to add a header,of course,you can add more headers as you want
        //mAdapter.remove(header1);             //remove header
   ```
   * add footer
   ```Java
     MultiTypeMaker footerTypeMaker = new MultiTypeMaker<String>() {
            @Override
            public int getType(int position) {
                return 0;
            }

            @Override
            public int getLayoutId(int viewType) {
                return android.R.layout.simple_list_item_1;
            }

            @Override
            public void bindViewHolder(CommonViewHolder holder, String data, int viewType, int position) {
                TextView text1 = holder.findViewById(android.R.id.text1);
                text1.setText(data);
            }

        };
        footerTypeMaker.setData("foot view 1");  
        mAdapter.addFooter(footerTypeMaker);    //call this method to add a footer,of course,you can add more footers as you want
        mAdapter.remove(footerTypeMaker);       //remove footer
   ```
     
