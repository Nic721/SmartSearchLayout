package android.amoscxy.com.smartsearchlayout;

import android.content.Intent;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int SMART_SEARCH_PARAMETER = 0;
    private TextView result;
    private Button search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        result = findViewById(R.id.result);
        search = findViewById(R.id.search);
        search.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search:
                Intent intent = new Intent(MainActivity.this, SmartSearchActivity.class);
                intent.putExtra("cacheParameter","cacheAll");
                MainActivity.this.startActivityForResult(intent,SMART_SEARCH_PARAMETER);
        }
    }

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
}
