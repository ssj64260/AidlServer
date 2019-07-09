package android.com.aidlserver;

import android.os.Binder;

public abstract class PayBinder extends Binder {

    public abstract void onSuccess();

    public abstract void onCancel();

}
