package android.com.aidlserver;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_PAYMENT_NAME = "key_payment_name";
    private static final String KEY_PAYMENT_AMOUNT = "key_payment_amount";

    private TextView tvContent;
    private Button btnPay;

    private Intent mServiceIntent;
    private PayBinder mBinder;

    private String mName;
    private String mAmount;

    private boolean mCanPay = false;
    private boolean mIsConnect = false;

    public static void show(Context context, String name, String amount) {
        Intent intent = new Intent();
        intent.setClass(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_PAYMENT_NAME, name);
        intent.putExtra(KEY_PAYMENT_AMOUNT, amount);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initView();

        startService(mServiceIntent);
        bindRecordService();
    }

    private void initData() {
        mName = getIntent().getStringExtra(KEY_PAYMENT_NAME);
        mAmount = getIntent().getStringExtra(KEY_PAYMENT_AMOUNT);

        mCanPay = !TextUtils.isEmpty(mName) && !TextUtils.isEmpty(mAmount);

        mServiceIntent = new Intent(this, PayService.class);
        mServiceIntent.setAction(PayService.ACTION_BIND_PAY_SERVICE);
    }

    private void initView() {
        tvContent = findViewById(R.id.tv_content);
        btnPay = findViewById(R.id.btn_pay);

        final String content = "支付项目：" + (mCanPay ? mName : "获取支付项目失败")
                + "\n支付金额：￥" + (mCanPay ? mAmount : "0.00")
                + (mCanPay ? "\n请确认以上支付的项目" : "");
        tvContent.setText(content);
        btnPay.setText(mCanPay ? "确认支付" : "返回");
        btnPay.setOnClickListener(mClick);
    }

    private void bindRecordService() {
        if (!mIsConnect && mBinder == null) {
            bindService(mServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private void unBindRecordService() {
        if (mIsConnect) {
            unbindService(mConnection);
            mBinder = null;
            mIsConnect = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unBindRecordService();
    }

    @Override
    public void onBackPressed() {
        if (mBinder != null) {
            mBinder.onCancel();
        }
        super.onBackPressed();
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service instanceof PayBinder) {
                mIsConnect = true;
                mBinder = (PayBinder) service;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsConnect = false;
        }
    };

    private final View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_pay:
                    if (mBinder != null) {
                        if (mCanPay) {
                            mBinder.onSuccess();
                        } else {
                            mBinder.onCancel();
                        }
                    }

                    finish();
                    break;
            }
        }
    };
}
