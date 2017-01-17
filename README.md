CZSuperAdapter
===================================
A super adapter for recyclerview,you can combine function part like refresh,loadmore,head,foot,multitype free as well as you want(适用于RecyclerView的万能Adapter，方便控制刷新、加载更多、头、脚、多视图。)
## Download
first,edit your application build.gradle
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}

second,edit your module build.gradle
dependencies {
    ...
    compile 'com.github.XinYiWorld:CZSuperAdapters:1.0'
}
