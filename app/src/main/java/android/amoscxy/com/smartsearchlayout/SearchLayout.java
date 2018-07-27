package android.amoscxy.com.smartsearchlayout;

import android.amoscxy.com.smartsearchlayout.ormlite.Cache;
import android.amoscxy.com.smartsearchlayout.ormlite.CacheDao;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by cxy on 2018/7/4.
 */

public class SearchLayout extends RelativeLayout implements View.OnClickListener,TextView.OnEditorActionListener,TextWatcher,MyDialogFragment.DialogFragmentListener{

    private Activity mActivity;
    private Context mContext;
    private EditText searchEdit;
    private ImageView mCleanImage;
    private CacheDao cacheDao;
    private List<Cache> list;
    private RecyclerView mRecyclerviewFlow;
    private FlowAdapter flowAdapter;
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    private MyDialogFragment myDialogFragment;
    private View mFooterView;
    private TextView v;
    private View spinner;
    private Long lastSearchTime;
    // 语音听写对象
    private SpeechRecognizer mIat;
    // 语音听写UI
    private RecognizerDialog mIatDialog;
    private SharedPreferences mSharedPreferences;
    private RelativeLayout mRelativeLayoutRoot;
    private View view;
    private OnVoicePermissionListener onVoicePermissionListener;
    private LinearLayout mContent;

    public void setClassz(Class classz) {
        cacheDao = new CacheDao(mContext, classz);
        list = cacheDao.queryAll();
        listVisible(mContent, list);
        mRecyclerviewFlow.setAdapter(flowAdapter = new FlowAdapter(list));
    }

    public void setOnVoicePermissionListener(OnVoicePermissionListener onVoicePermissionListener) {
        this.onVoicePermissionListener = onVoicePermissionListener;
    }

    public SearchLayout(Context context) {
        super(context);
        initView(context);
    }

    public SearchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SearchLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SearchLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(final Context context) {
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.layout_search,this);
        mActivity = (Activity)getContext();
        findViewById(R.id.button_back).setOnClickListener(this);
        findViewById(R.id.search_image).setOnClickListener(this);
        searchEdit = (EditText)findViewById(R.id.search_edit);
        searchEdit.setOnEditorActionListener(this);
        searchEdit.addTextChangedListener(this);
        mCleanImage = (ImageView) findViewById(R.id.clean_image);
        mCleanImage.setOnClickListener(this);
        findViewById(R.id.image_recycle_bin).setOnClickListener(this);
        findViewById(R.id.linearlayout_voice).setOnClickListener(this);

        mRecyclerviewFlow = (RecyclerView) findViewById(R.id.rececleview_flow);
        FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
        mRecyclerviewFlow.addItemDecoration(new SpaceItemDecoration(dp2px(10)));
        mRecyclerviewFlow.setLayoutManager(flowLayoutManager);

        mFooterView = LayoutInflater.from(context).inflate(R.layout.ptr_footer, null);
        v = (TextView) mFooterView.findViewById(R.id.ptr_id_text);
        spinner = mFooterView.findViewById(R.id.ptr_id_spinner);
        // 语音输入
        SpeechUtility.createUtility(mContext, SpeechConstant.APPID
                + "=" + getResources().getString(R.string.app_id));
        mIat = SpeechRecognizer.createRecognizer(mContext,
                mInitListener);
        initIat(mIat);
        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
        mIatDialog = new RecognizerDialog(mContext, mInitListener);
        mSharedPreferences = mContext.getSharedPreferences(IatSettings.PREFER_NAME, Activity.MODE_PRIVATE);
        mRelativeLayoutRoot = (RelativeLayout) findViewById(R.id.relativeLayout_root);
        mRelativeLayoutRoot.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            //当键盘弹出隐藏的时候会调用此方法。
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                //获取当前界面可视部分
                mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                //获取屏幕的高度
                int screenHeight = mActivity.getWindow().getDecorView().getRootView().getHeight();
                //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
                int heightDifference = screenHeight - r.bottom - getSoftButtonsBarHeight(mActivity);
                Log.d("Keyboard Size", "Size: " + heightDifference);
                showAViewOverKeyBoard(heightDifference);
            }
        });
        mContent = (LinearLayout) findViewById(R.id.content);
    }

    /**
     * 底部虚拟按键栏的高度
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static int getSoftButtonsBarHeight(Activity context) {
        DisplayMetrics metrics = new DisplayMetrics();
        //这个方法获取可能不是真实屏幕的高度
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        //获取当前屏幕的真实高度
        context.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.button_back){
            mActivity.finish();
        }else if(id == R.id.search_image){
            String keywords = searchEdit.getText().toString().trim();
            //insert data
            cacheDao.insert(new Cache(keywords));
            flowAdapter.insertData(new Cache(keywords));
            listVisible(mContent,list);
            if(onVoicePermissionListener != null){
                onVoicePermissionListener.clickListener(keywords);
            }
        }else if(id == R.id.clean_image){
            searchEdit.setText("");
            mCleanImage.setVisibility(View.GONE);
        }else if(id == R.id.image_recycle_bin){
            showDialogFragment(0,true,null);
        }else if(id == R.id.linearlayout_voice){
            //回调接口申请语音权限
            if(onVoicePermissionListener != null){
                onVoicePermissionListener.voicePermission();
            }
        }
    }

    private void listVisible(LinearLayout mContent, List<Cache> list) {
        if(list!=null && list.size() != 0){
            mContent.setVisibility(VISIBLE);
        }else{
            mContent.setVisibility(GONE);
        }
    }

    public void initIat(SpeechRecognizer mIat) {
        try {
            if (mIat == null) {
                return;
            }
            mIat.setParameter(SpeechConstant.SAMPLE_RATE, "16000");
            mIat.setParameter(SpeechConstant.DOMAIN, "iat");
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
            mIat.setParameter(SpeechConstant.KEY_SPEECH_TIMEOUT, "30000");
            mIat.setParameter(SpeechConstant.VAD_BOS, "3000");
            mIat.setParameter(SpeechConstant.VAD_EOS, "3000");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void determine(int position, Boolean isDeleteAll, Cache cache) {
        //确定
        if(isDeleteAll){
            flowAdapter.removeAllData();
            cacheDao.deleteAllCache();
        }else{
            flowAdapter.removeData(position);
            cacheDao.deleteByCache(cache);
        }
        if(myDialogFragment!=null && !mActivity.isFinishing()){
            myDialogFragment.dismiss();
            myDialogFragment = null;
        }
        listVisible(mContent,list);
    }

    public void showDialogFragment(int position, Boolean isDeleteAll, Cache cache){
        myDialogFragment = new MyDialogFragment(mActivity,position,isDeleteAll,cache);
        myDialogFragment.setmDialogFragmentListener(this);
        myDialogFragment.show(mActivity.getFragmentManager(),"myDialogFragment");
    }

    // 获取权限后要执行的事件
    public void voiceEvent(){
        mIatResults.clear();
        boolean isShowDialog = mSharedPreferences.getBoolean(mContext.getString(R.string.pref_key_iat_show), true);
        if (isShowDialog) {
            // 显示听写对话框
            mIatDialog.setListener(mRecognizerDialogListener);
            mIatDialog.show();
            showTip(mContext.getString(R.string.text_begin));
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String str = s.toString();
        if(TextUtils.isEmpty(str)){
            mCleanImage.setVisibility(View.GONE);
        }else{
            mCleanImage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    //点击软键盘确定
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        int id = v.getId();
        if(id == R.id.search_edit){
            if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                if(lastSearchTime != null){
                    long temp = System.currentTimeMillis() - lastSearchTime;
                    if(temp < 1000){
                        return true;
                    }
                }
                lastSearchTime = System.currentTimeMillis();

                String keywords = searchEdit.getText().toString().trim();
                //insert data
                cacheDao.insert(new Cache(keywords));
                flowAdapter.insertData(new Cache(keywords));
                listVisible(mContent,list);
                if(onVoicePermissionListener != null){
                    onVoicePermissionListener.clickListener(keywords);
                }
                return true;
            }
        }
        return false;
    }

    private int dp2px(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d("TAG", "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败，错误码：" + code);
            }
        }
    };

    private void showTip(final String str) {
        Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
    }
    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results, isLast);
        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {

        }

    };
    private void printResult(RecognizerResult results, boolean isLast) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(mIatResults.containsKey(sn)){
            return;
        }
        mIatResults.put(sn, text);
        if(text != null && text.length() > 0){
            String[] s = text.split("。");
            for(String ele: s){
                if(!TextUtils.isEmpty(ele)){
                    searchEdit.append(ele);
                }
            }
        }
    }

    private void showAViewOverKeyBoard(int heightDifference) {
        if (heightDifference > 0) {//显示
            if (view == null) {//第一次显示的时候创建  只创建一次
                view = View.inflate(mContext, R.layout.layout_voice, null);
                RelativeLayout.LayoutParams loginlayoutParams = new RelativeLayout.LayoutParams(-1, -2);
                loginlayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                loginlayoutParams.bottomMargin = heightDifference + 10;
                mRelativeLayoutRoot.addView(view, loginlayoutParams);
            }
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //获取语音权限
                    if(onVoicePermissionListener != null){
                        onVoicePermissionListener.voicePermission();
                    }
                }
            });
        } else {//隐藏
            if (view != null) {
                view.setVisibility(View.GONE);
            }
        }
    }

    class FlowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<Cache> list;

        public FlowAdapter(List<Cache> list) {
            this.list = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyHolder(View.inflate(mContext, R.layout.flow_item, null));
        }

        //删除positon位置的数据
        public void removeData(int position) {
            list.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, list.size() - position);
        }

        //删除全部数据
        public void removeAllData() {
            list.clear();
            notifyItemRangeRemoved(0,list.size());
            notifyDataSetChanged();
        }

        //在0位置添加数据
        public void insertData(Cache cache){
            if(cache!=null && !TextUtils.isEmpty(cache.getCache())){
                if(list.size() == 0){
                    list.add(cache);
                    notifyItemInserted(0);
                    notifyItemRangeChanged(0,list.size());
                }else{
                    if(list.size() > 100){
                        return;
                    }
                    for(Cache ele:list){
                        if(cache.getCache().equals(ele.getCache())){
                            return;
                        }
                    }
                    list.add(cache);
                    notifyItemInserted(0);
                    notifyItemRangeChanged(0,list.size());
                }
            }
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            final TextView textView = ((MyHolder) holder).text;
            textView.setText(list.get(position).getCache());
            textView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            //长按
                            showDialogFragment(position,false,list.get(position));
                            return true;
                        }
                    });
                    holder.itemView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //短按
                            if(onVoicePermissionListener != null){
                                onVoicePermissionListener.clickListener(textView.getText().toString());
                            }
                        }
                    });
                }
            });

        }

        @Override
        public int getItemCount() {
            if(list != null && list.size() != 0){
                return list.size();
            }
            return 0;
        }

        class MyHolder extends RecyclerView.ViewHolder {

            private TextView text;

            public MyHolder(View itemView) {
                super(itemView);
                text = (TextView) itemView.findViewById(R.id.flow_text);
            }
        }
    }

    public interface OnVoicePermissionListener{
        void voicePermission();
        void clickListener(String str);
    }
}
