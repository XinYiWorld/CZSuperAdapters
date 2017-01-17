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
1. define your adapter
   
```Java
 final BaseSuperAdapter mAdapter = new BaseSuperAdapter(mContext, recyclerView, new MultiTypeMaker<String>() {
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
```
