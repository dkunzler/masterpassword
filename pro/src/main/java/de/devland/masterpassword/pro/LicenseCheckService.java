package de.devland.masterpassword.pro;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;

import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.ServerManagedPolicy;

import de.devland.masterpassword.shared.util.Intents;

public class LicenseCheckService extends Service implements LicenseCheckerCallback {

    private static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlS+ieEhEqoilomfeVVc5TEThaool3voySeFZMnXI8FYHH4RfHn+ocIMEpTEEYIfi3vCTx/J3sJ/l7yesvhumEQHrncjXhlqUU6Y5+7HgrFO28WZHFs2eZOBy+5k0TgvNp46MGsApZVZYM2yFLGGn2/iUzPlLU3ZEmTEd/v2Dfds1Ycjyvyhz1p88wlSIq2iTQYV5XEQ0v6aR7Xv/mtV8K5a9uTiHhGXWsdCypV1/IfMub/UK8E8spwbv4L2O13KSB1Wu6AwHbNgIfYpiLrYuLfSa7L8lkTusupr0BJJ446l/sME3xrrKtl/Ml3P728/8A37GLijK9pFgeMcORci9HQIDAQAB";
    private static final byte[] SALT = new byte[]{
            -47, 85, 42, -39, 63, -111, 33, -36, 93, 64, 122, 31, 99, -32, 15, 40, 113, 71, -64, 30
    };

    protected LicenseChecker checker;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String deviceId = Settings.Secure
                .getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        checker = new LicenseChecker(this,
                new ServerManagedPolicy(this, new AESObfuscator(SALT, getPackageName(), deviceId)),
                PUBLIC_KEY);

        checker.checkAccess(this);


        return Service.START_STICKY;
    }

    private Intent getAnswerLicenseIntent() {
        Intent broadcast = new Intent();
        broadcast.setAction(Intents.ACTION_ANSERLICENSECHECK);
        return broadcast;
    }

    @Override
    public void allow(int reason) {
        Intent broadcast = getAnswerLicenseIntent();
        broadcast.putExtra(Intents.EXTRA_LICENSE, true);
        this.sendBroadcast(broadcast);
        this.stopSelf();
    }

    @Override
    public void dontAllow(int reason) {
        Intent broadcast = getAnswerLicenseIntent();
        broadcast.putExtra(Intents.EXTRA_LICENSE, false);
        this.sendBroadcast(broadcast);
        this.stopSelf();
    }

    @Override
    public void applicationError(int errorCode) {
        Intent broadcast = getAnswerLicenseIntent();
        broadcast.putExtra(Intents.EXTRA_LICENSE, false);
        this.sendBroadcast(broadcast);
        this.stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
