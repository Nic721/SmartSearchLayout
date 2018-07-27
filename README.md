# SmartSearchLayout
SmartSearchLayout是一个很好用的搜索框架，包含了以下功能点：  语音输入、一键清除输入结果、搜索、历史搜索、短按搜索记录提交搜索、长按搜索记录删除搜索、历史搜索流式布局、一键清除历史搜索、多处历史搜索记录互不影响等

# Blog

## 1.1 简介
搜索是每个app都会用到的功能，因此就想一劳永逸封装个搜索框架，看了很多app搜索的效果，最后选择了封装个类淘宝搜索的框架出来。

先看效果图：

![这里写图片描述](https://github.com/amoscxy/SmartSearchLayout/blob/master/pic/1.png)

这个搜索框架包含了以下功能点：
语音输入、一键清除输入结果、搜索、历史搜索、短按搜索记录提交搜索、长按搜索记录删除搜索、历史搜索流式布局、一键清除历史搜索、多处历史搜索记录互不影响等

## 1.2 使用方法

### 1.2.1 SmartSearchLayout使用了OrmLite数据库框架，使用前要在Application中初始化数据库

1.使用搜索框架时要创建继承自Cache的数据项，如：
```
public class CacheAll extends Cache {
    public CacheAll() {
        super();
    }

    public CacheAll(String cache) {
        super(cache);
    }
}
```
2.在Application中初始化数据库
```
//初始化ormlite数据库
public void initDatabase(){
    DatabaseHelper.version = 0;
    DatabaseHelper.classes.add(CacheAll.class);
}
```
其中的CacheAll.class就是上一步创建的数据项
每次更新、新增、删除数据项，DatabaseHelper.version就要加1

### 1.2.2 跳转到搜索框架布局页SmartSearchActivity
```
private static final int SMART_SEARCH_PARAMETER = 0;
mRlsousuoView1.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(ZxingCapturePrintActivity.this, SmartSearchActivity.class);
        intent.putExtra("cacheParameter","CacheAll");
        startActivityForResult(intent,SMART_SEARCH_PARAMETER);
    }
});
```

### 1.2.3 SmartSearchActivity.java中的initLayoutEvent()方法中增加分支
```
private void initLayoutEvent() {
	cacheParameter = getIntent().getStringExtra("cacheParameter");
	if("cacheAll".equals(cacheParameter)){
		mSearchlayout.setClassz(CacheAll.class);
	}
}
```
其中CacheAll.class对应于我们上面设置的数据项

### 1.2.4 上一级页面接收搜索框架返回的搜索数据
```
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if(data == null){
        return;
    }
    if(requestCode == SMART_SEARCH_PARAMETER && resultCode == RESULT_OK){
        String resultStr = data.getStringExtra("cacheString");
        result.setText(resultStr);
    }
}
```
## 1.3 结果展示
![这里写图片描述](https://github.com/amoscxy/SmartSearchLayout/blob/master/pic/1.gif)

[点击查看对应博客](https://blog.csdn.net/amoscxy/article/details/81218050)