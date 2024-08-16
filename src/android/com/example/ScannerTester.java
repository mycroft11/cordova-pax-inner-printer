package com.example;

import android.os.Handler;
import android.os.Message;

import com.pax.dal.IScanner;
import com.pax.dal.IScanner.IScanListener;
import com.pax.dal.entity.EScannerType;
import android.content.Context;
import com.pax.dal.IDAL;
import com.pax.neptunelite.api.NeptuneLiteUser;
import android.util.Log;

public class ScannerTester {
    private static ScannerTester cameraTester;

    private static EScannerType scannerType;

    private IScanner scanner;
  	private static Context context;


    private ScannerTester(Context context,EScannerType type) {
        try {
        ScannerTester.scannerType = type;
        this.context = context;
        IDAL idal = NeptuneLiteUser.getInstance().getDal(this.context );
        scanner =idal.getScanner(scannerType);
      } catch (Exception e) {
          e.printStackTrace();
      }
    }

    public static ScannerTester getInstance(Context context,EScannerType type) {
        if (cameraTester == null || type != scannerType) {
            cameraTester = new ScannerTester(context,type);
        }
        return cameraTester;
    }

    public void scan(final Handler handler,int timeout) {
        scanner.open();

        setTimeOut(timeout);
        scanner.setContinuousTimes(1);
        scanner.setContinuousInterval(1000);
        scanner.start(new IScanListener() {

            @Override
            public void onRead(String arg0) {
                Log.d("Pax","scan result in ScannerTester "+arg0);
                Message message = Message.obtain();
                message.what = 0;
                message.obj = arg0;
                handler.sendMessage(message);
            }

            @Override
            public void onFinish() {
                close();
            }

            @Override
            public void onCancel() {

                close();
            }
        });

    }

    public void close() {
        scanner.close();

    }

    public void setTimeOut(int timeout){
        scanner.setTimeOut(timeout);

    }

}
