package android.amoscxy.com.smartsearchlayout;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }

    // 已经关闭了权限后弹框处理， msg 提示信息, 是否执行 finish 操作
    protected void deniedPermissionWithoutPermission(String msg, boolean isfinish){
        permissionDialog(msg,isfinish);
    }

    // 未开启权限时的弹框
    public void permissionDialog(String msg, final boolean isfinish) { // permission 权限被拒绝或禁用
        final AlertDialog permissionDialog = new AlertDialog.Builder(this).create();//创建一个AlertDialog对象
        permissionDialog.setCancelable(false);
        View view = getLayoutInflater().inflate(R.layout.permission_dialog_confirm, null);//自定义布局
        TextView message = (TextView) view.findViewById(R.id.message);
        message.setText(msg);
        View ok = view.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionDialog.dismiss();
                if(isfinish){
                    finish();
                }
            }
        });
        permissionDialog.setView(view, 0, 0, 0, 0);//把自定义的布局设置到dialog中，注意，布局设置一定要在show之前。从第二个参数分别填充内容与边框之间左、上、右、下、的像素
        permissionDialog.show();//一定要先show出来再设置dialog的参数，不然就不会改变dialog的大小了
    }
}
