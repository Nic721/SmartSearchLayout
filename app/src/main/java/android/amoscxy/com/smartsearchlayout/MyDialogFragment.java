package android.amoscxy.com.smartsearchlayout;

import android.amoscxy.com.smartsearchlayout.ormlite.Cache;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

/**
 * Created by cxy on 2018/7/2.
 */

@SuppressLint("ValidFragment")
public class MyDialogFragment extends DialogFragment implements View.OnClickListener{
    private DialogFragmentListener mDialogFragmentListener;
    private int position;
    private Activity activity;
    private Boolean isDeleteAll;
    private Cache cache;

    public void setmDialogFragmentListener(DialogFragmentListener mDialogFragmentListener) {
        this.mDialogFragmentListener = mDialogFragmentListener;
    }

    @SuppressLint("ValidFragment")
    public MyDialogFragment(Activity activity, int position, Boolean isDeleteAll, Cache cache) {
        this.activity = activity;
        this.position = position;
        this.isDeleteAll = isDeleteAll;
        this.cache = cache;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialogfragment_my,container);
        initView(view);
        return view;
    }

    private void initView(View view) {
        (view.findViewById(R.id.cancel_button)).setOnClickListener(this);
        (view.findViewById(R.id.determine_button)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.cancel_button) {
            if(!activity.isFinishing()){
                dismiss();
            }
        }else if(i == R.id.determine_button){
            if(mDialogFragmentListener != null){
                mDialogFragmentListener.determine(position,isDeleteAll,cache);
            }
        }
    }

    public interface DialogFragmentListener{
        void determine(int position, Boolean isDeleteAll, Cache cache);
    }
}
