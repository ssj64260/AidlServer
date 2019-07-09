package android.com.aidlserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

public class PayService extends Service {

    public static final String ACTION_BIND_PAY_SERVICE = "action_bind_pay_service";

    private IPayStatusInterface mListener;

    /**
     * 用于服务端支付界面回调支付情况
     */
    private final PayBinder mBinder = new PayBinder() {
        @Override
        public void onSuccess() {
            if (mListener != null) {
                try {
                    mListener.onPaySuccess();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onCancel() {
            if (mListener != null) {
                try {
                    mListener.onPayCancel();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * 用于客户端调起服务端的相关功能
     */
    private final IPayInterface.Stub mStub = new IPayInterface.Stub() {
        @Override
        public void startPay(String name, String amount) throws RemoteException {
            MainActivity.show(PayService.this, name, amount);
        }

        @Override
        public void setPayStatusListener(IBinder listener) throws RemoteException {
            mListener = IPayStatusInterface.Stub.asInterface(listener);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        final String action = intent.getAction();
        if (ACTION_BIND_PAY_SERVICE.equals(action)) {
            return mBinder;
        }

        return mStub;
    }
}
