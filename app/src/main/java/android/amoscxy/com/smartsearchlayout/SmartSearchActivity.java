package android.amoscxy.com.smartsearchlayout;

import android.Manifest;
import android.amoscxy.com.smartsearchlayout.ormlite.CacheAll;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

public class SmartSearchActivity extends BaseActivity implements SearchLayout.OnVoicePermissionListener {
	private static final long VIBRATE_DURATION = 200L;
	private SearchLayout mSearchlayout;
	private static final int PERMISSION_VOICE = 0;
	private Vibrator vibrator;
	private SoundPool soundPool;
	private int soundId;
	private String cacheParameter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_smart_search);
		initView();
		initLayoutEvent();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void initView() {
		mSearchlayout = (SearchLayout) findViewById(R.id.searchlayout);
		mSearchlayout.setOnVoicePermissionListener(this);
		// 音乐回放即媒体音量
        SmartSearchActivity.this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        soundId = soundPool.load(this, R.raw.beep, 1);
	}

	private void initLayoutEvent() {
		cacheParameter = getIntent().getStringExtra("cacheParameter");
		if("cacheAll".equals(cacheParameter)){
			mSearchlayout.setClassz(CacheAll.class);
		}
	}

	@Override
	public void voicePermission() {
		if (ContextCompat.checkSelfPermission(SmartSearchActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(SmartSearchActivity.this, new String[]{ Manifest.permission. RECORD_AUDIO }, 1);
		} else {
			// 获取权限后要执行的事件
			mSearchlayout.voiceEvent();
		}
	}

	//点击历史记录
	@Override
	public void clickListener(String cacheString) {
		Intent intent = new Intent();
		intent.putExtra("cacheString",cacheString);
		setResult(RESULT_OK,intent);
		finish();
	}

	//检测到还没有获取到权限时执行，申请获取权限
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case PERMISSION_VOICE:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// 获取权限后要执行的事件
					mSearchlayout.voiceEvent();
				} else {
					// 权限未开启时弹框处理
					deniedPermissionWithoutPermission("无法获取声音数据，请检查是否已经打开录制音声权限",false);
				}
				break;
			default:
		}
	}

	// 失败时震动
	public void playVibrate() {
		if (vibrator == null)
			vibrator = (Vibrator)this.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(VIBRATE_DURATION);
	}
	// 成功音效
	public void playBeepSound() {
		if (soundPool != null) {
			soundPool.play(soundId, 1, 1, 1, 0, 1);
		}
	}
}
