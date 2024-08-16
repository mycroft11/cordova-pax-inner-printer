package com.example;

import com.pax.dal.IIcc;
import com.pax.dal.exceptions.IccDevException;
// import com.pax.demo.base.DemoApp;
// import com.pax.demo.util.BaseTester;
import android.util.Log;
import android.content.Context;
import com.pax.dal.IDAL;
import com.pax.neptunelite.api.NeptuneLiteUser;

public class IccTester  {

    private static IccTester iccTester;

    private IIcc icc;
    private static Context context;

    private IccTester(Context context) {
        // icc = DemoApp.getDal().getIcc();
        try {
          this.context = context;
          IDAL idal = NeptuneLiteUser.getInstance().getDal(this.context );
          icc = idal.getIcc();
        }catch(Exception e ){
                    Log.i("init", e.toString());
        }

    }

    public static IccTester getInstance(Context context) {
        // if (iccTester == null) {
        //     iccTester = new IccTester();
        // }
        // return iccTester;

        if(iccTester == null){
          synchronized (IccTester.class){
            if(iccTester == null){

              iccTester = new IccTester(context);
            }
          }
        }
            iccTester.context = context;
        return iccTester;
    }

    public byte[] init(byte slot) {
        byte[] initRes = null;
        try {
            initRes = icc.init(slot);
              Log.i("IccTester", "Init");
            // logTrue("init");
            return initRes;
        } catch (IccDevException e) {
            e.printStackTrace();
            Log.i("init", e.toString());
            // logErr("init", e.toString());
            return null;
        }
    }

    public boolean detect(byte slot) {
        boolean res = false;
        try {
            res = icc.detect(slot);
              Log.i("IccTester", "card detetcted "+slot);

            // logTrue("detect");
            return res;
        } catch (IccDevException e) {
            e.printStackTrace();
            Log.i("detect", e.toString());
            // logErr("detect", e.toString());
            return res;
        }
    }

    public void close(byte slot) {
        try {
            icc.close(slot);
           Log.i("IccTester", "close");
        } catch (IccDevException e) {
            e.printStackTrace();
               Log.i("IccTester", e.toString());
        }
    }

    public void autoResp(byte slot, boolean autoresp) {
        try {
            icc.autoResp(slot, autoresp);
     Log.i("IccTester", "autoResp");
        } catch (IccDevException e) {
            e.printStackTrace();
           Log.i("autoResp", e.toString());
        }
    }

    public byte[] isoCommand(byte slot, byte[] send) {
        try {
            byte[] resp = icc.isoCommand(slot, send);
            // logTrue("isoCommand");
                 Log.i("IccTester", "isoCommand");
            return resp;
        } catch (IccDevException e) {
            e.printStackTrace();
            Log.i("isoCommand",  e.toString());
            // logErr("isoCommand", e.toString());
            return null;
        }
    }

    public void light(boolean flag){
        try {
            icc.light(flag);
                Log.i("IccTester", "light");
            // logTrue("light");
        } catch (IccDevException e) {
            e.printStackTrace();
            Log.i("light", e.toString());
        }
    }
}
