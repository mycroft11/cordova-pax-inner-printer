package com.example;

import android.graphics.Bitmap;
import android.util.Log;
import com.pax.dal.IDAL;
import com.pax.dal.IPrinter;
import com.pax.dal.exceptions.PrinterDevException;
import com.pax.neptunelite.api.NeptuneLiteUser;
import android.content.Context;

import android.util.Base64;
import com.example.NeptunePrinterBuilder;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
/**
 * this is an Printer for test neptune api
 *
 * @author liwenhao
 */
public class NeptunePrinter {

    private static final String TAG = NeptunePrinter.class.getName();


    private static IPrinter iPrinter;

    private static  NeptunePrinter neptunePrinter ;

    private static final int STATUS_PRINTER_NORMAL = 0;

    private static final String DEFAULT_CHARSET = "UTF-8";

    private static final int STATUS_PRINTER_NOT_INITIATED = -100;
  	private static Context context;

    private NeptunePrinter(Context context) {
        try {
            this.context = context;
            IDAL idal = NeptuneLiteUser.getInstance().getDal(this.context );
            iPrinter = idal.getPrinter();
            initPinter();
            NeptunePrinterBuilder builder = NeptunePrinterBuilder.build();
            //配置IPrinter的相关参数 字号 行间距 字间距等等
            configPrinter(builder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initPinter() {
        try {
            iPrinter.init();
            iPrinter.setGray(3);
        } catch (PrinterDevException e) {
            e.printStackTrace();
        }

    }

    /**
     * reconfig the IPrinter params
     *
     * @param builder
     */
    public void configPrinter(NeptunePrinterBuilder builder) {
        if (iPrinter != null) {
            try {
                iPrinter.fontSet(builder.eFontTypeAscii, builder.eFontTypeExtCode);
                iPrinter.spaceSet(builder.wordSpaec, builder.lineSpace);
                iPrinter.leftIndent(builder.leftIndent);
                iPrinter.step(builder.printerStep);
            } catch (PrinterDevException e) {
                e.printStackTrace();
            }
        }
    }

    public static NeptunePrinter getInstance(Context context) {

            // return INSTANCE;
            if(neptunePrinter == null){
              synchronized (NeptunePrinter.class){
                if(neptunePrinter == null){

                  neptunePrinter = new NeptunePrinter(context);
                }
              }
            }
                neptunePrinter.context = context;
            return neptunePrinter;

    }


  public void paxPrint(String text){
        if (iPrinter != null) {
          try {

              if(text.contains("/nnn/")){
              Log.e(TAG,text);
                  for (String lineStr : text.split("/nnn/")) {
                    // Log.e(TAG,lineStr);
                      String line[] = lineStr.split("word_break");
                      String func = line[0].trim();

                      if(func.equals("printText")){

                          String para1 = line[1];
                          // Log.e(TAG,func);
                          // Log.e(TAG,para1);
                        iPrinter.printStr("\n"+para1, DEFAULT_CHARSET);
                      }     else if(func.equals("lineBreak")){

                            iPrinter.printStr("\n", DEFAULT_CHARSET);
                          }
                      else if(func.equals("printImage")){
                        String para1 = line[1].trim();
                        Log.e(TAG,func);
                        Log.e(TAG,para1);
                          byte[] decodedString = Base64.decode(para1, Base64.DEFAULT);
                          Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                          iPrinter.printBitmap(decodedByte);
                        }
                      }
                    }
                  } catch (PrinterDevException e) {
                      e.printStackTrace();
                    }
                  }
        }

    public void printText(String textContent) {
        if (iPrinter != null) {
            try {
                iPrinter.printStr(textContent, DEFAULT_CHARSET);
            } catch (PrinterDevException e) {
                e.printStackTrace();
            }
        }
    }


    public void printBitmap(Bitmap bmp) {
        if (iPrinter != null) {
            try {
                iPrinter.printBitmap(bmp);
            } catch (PrinterDevException e) {
                e.printStackTrace();
            }
        }
    }


    public void printBitmap(Bitmap bmp, IPrinter.IPinterListener listener) {
        if (iPrinter != null) {
            iPrinter.print(bmp, listener);
        }
    }

    public void step(int length) {
        if (iPrinter != null) {
            try {
                iPrinter.step(length);
            } catch (PrinterDevException e) {
                e.printStackTrace();
            }
        }
    }


    public int getPrinterStatus() {
        if (iPrinter != null) {
            try {
                return iPrinter.getStatus();
            } catch (PrinterDevException e) {
                e.printStackTrace();
            }
        }
        return STATUS_PRINTER_NOT_INITIATED;
    }


    public boolean isBusy() {
        return getPrinterStatus() != STATUS_PRINTER_NORMAL;
    }


    public void start() {
        try {
            if (iPrinter != null && iPrinter.getStatus() == STATUS_PRINTER_NORMAL) {
                iPrinter.start();
            }
        } catch (PrinterDevException e) {
            Log.e(TAG, "printer start failed: device was occur an error!");
            e.printStackTrace();
        }
    }


}
