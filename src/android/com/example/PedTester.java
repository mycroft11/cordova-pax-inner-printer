package com.example;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import android.util.Log;

import com.pax.dal.IPed;
import com.pax.dal.entity.DUKPTResult;
import com.pax.dal.entity.EAesCheckMode;
import com.pax.dal.entity.ECheckMode;
import com.pax.dal.entity.ECryptOperate;
import com.pax.dal.entity.ECryptOpt;
import com.pax.dal.entity.EDUKPTDesMode;
import com.pax.dal.entity.EDUKPTMacMode;
import com.pax.dal.entity.EDUKPTPinMode;
import com.pax.dal.entity.EFuncKeyMode;
import com.pax.dal.entity.EPedDesMode;
import com.pax.dal.entity.EPedKeyType;
import com.pax.dal.entity.EPedMacMode;
import com.pax.dal.entity.EPedType;
import com.pax.dal.entity.EPinBlockMode;
import com.pax.dal.entity.EUartPort;
import com.pax.dal.entity.RSAKeyInfo;
import com.pax.dal.entity.RSARecoverInfo;
import com.pax.dal.entity.SM2KeyPair;
import com.pax.dal.exceptions.PedDevException;
import android.content.Context;
import com.pax.dal.IDAL;
import com.pax.neptunelite.api.NeptuneLiteUser;


public class PedTester{
    	private static Context context;
    private static PedTester pedTester;
    private static EPedType pedType;
    public static int PEDMODE = 0; //全局标志位
    private static int pedMode = 0; //本地变量
    private IPed ped;
    private byte[] byte_test = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
    private KeyPair kp = null;
    public static byte[] modulus1 = null;
    public static boolean isGenRsaKey = false;

    private PedTester(Context context) {
    try {
        this.context = context;
        IDAL idal = NeptuneLiteUser.getInstance().getDal(this.context);
        ped = idal.getPed(EPedType.EXTERNAL_TYPEA);
      } catch (Exception e) {
          e.printStackTrace();
      }
    }

    public static PedTester getInstance(Context context) {
        if (pedTester == null) {
            pedTester = new PedTester(context);
        }
        return pedTester;
    }

    public static String getSerialNumber() {
        String serialNumber;

        try {
            serialNumber = android.os.Build.SERIAL;
            // Class<?> c = Class.forName("android.os.SystemProperties");
            // Method get = c.getMethod("get", String.class);
            //
            // serialNumber = (String) get.invoke(c, "gsm.sn1");
            // if (serialNumber.equals(""))
            //     serialNumber = (String) get.invoke(c, "ril.serialnumber");
            // if (serialNumber.equals(""))
            //     serialNumber = (String) get.invoke(c, "ro.serialno");
            // if (serialNumber.equals(""))
            //     serialNumber = (String) get.invoke(c, "sys.serialnumber");
            // if (serialNumber.equals(""))
            //     serialNumber = Build.SERIAL;
            //
            // // If none of the methods above worked
            // if (serialNumber.equals(""))
            //     serialNumber = null;
        } catch (Exception e) {
            e.printStackTrace();
            serialNumber = null;
        }

        return serialNumber;
    }


    public String getSN() {
        String sn = null;
        try {
            sn = ped.getSN();
        } catch (PedDevException e) {
            e.printStackTrace();
        }
        return sn == null ? "null" : sn;
    }



}
