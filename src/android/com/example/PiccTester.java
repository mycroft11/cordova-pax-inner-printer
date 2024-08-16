package com.example;


// import com.pax.demo.base.DemoApp;
// import com.pax.demo.util.BaseTester;
import android.util.Log;
import android.content.Context;
import com.pax.dal.IDAL;
import com.pax.neptunelite.api.NeptuneLiteUser;

import com.pax.dal.IPicc;
import com.pax.dal.entity.ApduSendInfo;
import com.pax.dal.entity.EBeepMode;
import com.pax.dal.entity.EDetectMode;
import com.pax.dal.entity.EM1KeyType;
import com.pax.dal.entity.EPiccRemoveMode;
import com.pax.dal.entity.EPiccType;
import com.pax.dal.entity.PiccCardInfo;
import com.pax.dal.entity.PiccPara;
import com.pax.dal.exceptions.EPiccDevException;
import com.pax.dal.exceptions.PiccDevException;

import com.example.Convert;
import com.example.Convert.EPaddingPosition;


public class PiccTester  {

    private static PiccTester piccTester;
    private IPicc picc;
    private static EPiccType piccType = EPiccType.INTERNAL;
    private String TAG= "PICC TESTER";
    private static Context context;
    private  int intCount = 0;

    private PiccTester(Context context) {
        try {
          this.context = context;
          IDAL idal = NeptuneLiteUser.getInstance().getDal(this.context );
          picc = idal.getPicc(piccType);
        }catch(Exception e ){
          Log.i(TAG, e.toString());
        }

    }

    public static PiccTester getInstance(Context context) {
        if(piccTester == null){
          synchronized (PiccTester.class){
            if(piccTester == null){

              piccTester = new PiccTester(context);
            }
          }
        }
        piccTester.context = context;
        return piccTester;
    }
    public void close() {
          intCount = 0;
        try {
            picc.close();

        } catch (PiccDevException e) {
            e.printStackTrace();

        }
    }
    public String detectM() {
        // byte[] pwd = { (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff };
        PiccCardInfo cardInfo = null;
        if (null != (cardInfo = detect(EDetectMode.ONLY_M))) {

              Log.i(TAG,"cardtype:"
                    + cardInfo.getCardType()
                    + " SerialInfo:"
                    + Convert.getInstance().bcdToStr(
                            (cardInfo.getSerialInfo() == null) ? "".getBytes() : cardInfo.getSerialInfo())
                    + " cid:"
                    + cardInfo.getCID()
                    + " Other:"
                    + Convert.getInstance().bcdToStr(
                            (cardInfo.getOther() == null) ? "".getBytes() : cardInfo.getOther()));

              return    Convert.getInstance().bcdToStr(cardInfo.getSerialInfo());
        } else {
              Log.i(TAG,"can't find card !");
                return "";
            // Message.obtain(handler, 0, "can't find card !").sendToTarget();
        }

    }

    public String detectMbsCard() {
        // byte[] pwd = { (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff };
        PiccCardInfo cardInfo = null;
        if (null != (cardInfo = detect(EDetectMode.ONLY_M))) {

              Log.i(TAG,"cardtype:"
                    + cardInfo.getCardType()
                    + " SerialInfo:"
                    + Convert.getInstance().bcdUSASCiiToStr(
                            (cardInfo.getSerialInfo() == null) ? "".getBytes() : cardInfo.getSerialInfo())
                    + " cid:"
                    + cardInfo.getCID()
                    + " Other:"
                    + Convert.getInstance().bcdUSASCiiToStr(
                            (cardInfo.getOther() == null) ? "".getBytes() : cardInfo.getOther()));

                    byte[] value = null;
                    String str = "";
                    try {
                      byte type =   (byte)65;
                      int blockNum = 1;
                      byte[] password =   Convert.getInstance().strToBcd("FFFFFFFFFFFF", EPaddingPosition.PADDING_RIGHT);
                      Log.i("Test", "keyType:"+ " blockNum:" + blockNum + " password:");
                      picc.m1Auth(EM1KeyType.TYPE_A, (byte) blockNum, password, cardInfo.getSerialInfo());
                      value = picc.m1Read((byte) blockNum);
                    } catch (PiccDevException e) {
                        e.printStackTrace();
                        str = "error";
                        close();
                          return str;
                    }
                    if (value != null) {
                        str += "name"+(Convert.getInstance().bcdUSASCiiToStr(value));
                    } else {
                        str = "error";
                        close();
                        return str;
                    }

                    try {
                      byte type =   (byte)65;
                      int blockNum = 4;
                      byte[] password =   Convert.getInstance().strToBcd("FFFFFFFFFFFF", EPaddingPosition.PADDING_RIGHT);
                      Log.i("Test", "keyType:"+ " blockNum:" + blockNum + " password:");
                      picc.m1Auth(EM1KeyType.TYPE_A, (byte) blockNum, password, cardInfo.getSerialInfo());
                      value = picc.m1Read((byte) blockNum);
                    } catch (PiccDevException e) {
                        e.printStackTrace();
                        str = "error";
                        close();
                          return str;
                    }
                    if (value != null) {
                        str += "number"+(Convert.getInstance().bcdUSASCiiToStr(value));
                    } else {
                        str = "error";
                          close();
                          return str;
                    }
                    close();
                    return str;

                    // String cardString = "";// 卡片的信息
                    // cardString += ("cardType:" + new String(new byte[] { cardInfo.getCardType() }) + "\n");
                    // cardString += ("block " + blockNum + " read message:" + str);


              // return    Convert.getInstance().bcdUSASCiiToStr(cardInfo.getSerialInfo());
        } else if(intCount<100) {
          intCount = intCount+1;
              Log.i(TAG,"can't find card !");
                return "";
            // Message.obtain(handler, 0, "can't find card !").sendToTarget();
        }else{
            Log.i("intCount",String.valueOf(intCount));
              close();
              return "errorTimeOut";
        }

    }


    public PiccCardInfo detect(EDetectMode mode) {
        try {
            picc.open();
            PiccCardInfo cardInfo = picc.detect(mode);
            Log.i(TAG,"Detected");
            return cardInfo;
        } catch (PiccDevException e) {
            e.printStackTrace();
            Log.i(TAG,"detect error");
            return null;
        }
    }

}
