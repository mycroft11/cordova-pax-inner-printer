
package com.example;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;


import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import java.util.Iterator;

import android.graphics.Typeface;

import android.os.Build;


import android.widget.Toast;
import android.content.Context;
import java.util.concurrent.TimeUnit;
import java.lang.reflect.Method;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;


import com.example.NeptunePrinter;
import com.example.ScannerTester;
import com.example.MagTester;
import com.example.PedTester;
import com.pax.dal.entity.EScannerType;

import com.pax.dal.entity.TrackData;
import android.os.SystemClock;

import com.pax.dal.entity.EPedType;


public class PaxPrinter extends CordovaPlugin {

    private static final String TAG = "Pax";

    CallbackContext callbackContext = null;
    Context context = null;
    private NeptunePrinter neptunePrinter;
    private ScannerTester scannerTester;
    private static Handler scanEventHandler;

    private static Handler magHandler;

    static MagReadThread magReadThread;

    private static Handler cardHandler;

    static IccDectedThread iccDectedThread;

    static PiccDectedThread piccDectedThread;
    static MbsCardReadThread mbsCardReadThread;
    private static Handler nfcCardHandler;

    private static Handler mbsCardReadHandler;
    public static boolean b = false;
    private   int intCount = 0;

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }
    private int getTimeout() {

        return Integer.parseInt("0");
    }
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        Log.d(TAG,action);
        context = cordova.getActivity().getApplicationContext();

        if (action.equals("print")) {
          cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                  try{
                      neptunePrinter = NeptunePrinter.getInstance(context);
                      neptunePrinter.initPinter();
                      String text = args.getString(0);
                      neptunePrinter.printText(text);
                      neptunePrinter.start();
                  }catch (Exception e) {
                      e.printStackTrace();

                      callbackContext.error("Printe Error in catch");
                  }

                    }
                });
                return true;
        }else if (action.equals("printImage")) {
            cordova.getThreadPool().execute(new Runnable() {
              @Override
              public void run() {
                    try{
                        neptunePrinter = NeptunePrinter.getInstance(context);
                        neptunePrinter.initPinter();
                        String text = args.getString(0);

                        neptunePrinter.printBitmap(base64ToImage(text));
                        neptunePrinter.start();
                    }catch (Exception e) {
                        e.printStackTrace();

                        callbackContext.error("Printe Error in catch");
                    }

                      }
                  });
                  return true;
          }else if (action.equals("paxPrint")) {
              cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                      try{
                          neptunePrinter = NeptunePrinter.getInstance(context);
                          neptunePrinter.initPinter();
                          String text = args.getString(0);

                          neptunePrinter.paxPrint(text);
                          neptunePrinter.start();
                      }catch (Exception e) {
                          e.printStackTrace();

                          callbackContext.error("Printe Error in catch");
                      }

                        }
                    });
                    return true;
            }else if (action.equals("scanQR")){

              scanEventHandler = new Handler() {
              public void handleMessage(android.os.Message msg) {
                  switch (msg.what) {
                    case 0:
                      Log.d("Pax","scan result "+msg.obj.toString());
                      callbackContext.success(msg.obj.toString());
                      break;
                    default:
                      break;
                    }
                  };
                };


              cordova.getThreadPool().execute(new Runnable() {
                  @Override
                  public void run() {

                      context = cordova.getActivity().getApplicationContext();
                      scannerTester =   ScannerTester.getInstance(context,EScannerType.REAR);
                      scannerTester.scan(scanEventHandler,getTimeout());

              }
              });
              return true;
        }else if (action.equals("readCard")){

          magHandler = new Handler() {
          public void handleMessage(android.os.Message msg) {
              switch (msg.what) {
                case 0:
                  MagTester.getInstance(context).close();
                  intCount = 0;
                  Log.d("Pax","read result "+msg.obj.toString());
                  callbackContext.success(msg.obj.toString());
                  break;
                default:
                  break;
                }
              };
            };
            cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                  if (magReadThread != null){
                      magReadThread = null;
                  }
                  if (magReadThread == null) {
                    // magReadThread = new MagReadThread();
                    context = cordova.getActivity().getApplicationContext();
                    MagTester.getInstance(context).open();
                    MagTester.getInstance(context).reset();
                    // magReadThread.start();

                    while (!Thread.interrupted()) {

                          intCount = intCount+1;
                          Log.i("intCount Mag",String.valueOf(intCount));
                          if(intCount >100){
                               intCount = 0;
                            Message.obtain(magHandler, 0, "mag_card_timeout").sendToTarget();
                            break;
                          }
                        if (MagTester.getInstance(context).isSwiped()) {
                            TrackData trackData = MagTester.getInstance(context).read();
                            if (trackData != null) {
                                String resStr = "";
                                if (trackData.getResultCode() == 0) {
                                    resStr = "mag_card_error";
                                     intCount = 0;
                                    Message.obtain(magHandler, 0, "mag_card_error").sendToTarget();
                                    continue;
                                }
                                if ((trackData.getResultCode() & 0x01) == 0x01) {
                                    resStr += "mag_track1_data" + trackData.getTrack1();
                                  //  Message.obtain(magHandler, 0, resStr).sendToTarget();
                                }
                                if ((trackData.getResultCode() & 0x02) == 0x02) {
                                    resStr += "mag_track2_data"+ trackData.getTrack2();
                                       intCount = 0;
                                    Message.obtain(magHandler, 0, resStr).sendToTarget();
                                      break;
                                }
                                if ((trackData.getResultCode() & 0x04) == 0x04) {
                                    resStr += "mag_track3_data"+ trackData.getTrack3();
                                  //  Message.obtain(magHandler, 0, resStr).sendToTarget();
                                }
                                break;
                            }

                        }
                        SystemClock.sleep(100);
                    }
            }

            }
            });
            return true;
        }else if (action.equals("cardDetails")) {

              cardHandler = new Handler() {
              public void handleMessage(android.os.Message msg) {
                  switch (msg.what) {
                    case 0:
                      Log.d("Pax","read result "+msg.obj.toString());
                      callbackContext.success(msg.obj.toString());
                      break;
                    default:
                      break;
                    }
                  };
                };
                cordova.getThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                      if (iccDectedThread != null){
                          iccDectedThread = null;
                      }
                      if (iccDectedThread == null) {
                        iccDectedThread = new IccDectedThread();
                        // context = cordova.getActivity().getApplicationContext();
                        // I.getInstance(context).open();
                        // IccDectedThread.getInstance(context).reset();
                        iccDectedThread.start();
                }

                }
                });
                return true;
              }else if (action.equals("readNfc")) {

                      nfcCardHandler = new Handler() {
                      public void handleMessage(android.os.Message msg) {
                          switch (msg.what) {
                            case 0:
                              Log.d("Pax","nfc read result "+msg.obj.toString());
                              callbackContext.success(msg.obj.toString());
                              break;
                            default:
                              break;
                            }
                          };
                        };
                        cordova.getThreadPool().execute(new Runnable() {
                            @Override
                            public void run() {
                              if (piccDectedThread != null){
                                  piccDectedThread = null;
                              }
                              if (piccDectedThread == null) {
                                piccDectedThread = new PiccDectedThread();
                                // context = cordova.getActivity().getApplicationContext();
                                // I.getInstance(context).open();
                                // IccDectedThread.getInstance(context).reset();
                                piccDectedThread.start();
                        }

                        }
                        });
                        return true;
                  } else if (action.equals("mbsCardRead")) {

                          mbsCardReadHandler = new Handler() {
                          public void handleMessage(android.os.Message msg) {
                              switch (msg.what) {
                                case 0:
                                  Log.d("Pax","mbs read result "+msg.obj.toString());
                                  callbackContext.success(msg.obj.toString());
                                  break;
                                default:
                                  break;
                                }
                              };
                            };
                            cordova.getThreadPool().execute(new Runnable() {
                                @Override
                                public void run() {
                                  // if (mbsCardReadThread != null){
                                  //     mbsCardReadThread = null;
                                  // }
                                  // if (mbsCardReadThread == null) {
                                  //   mbsCardReadThread = new MbsCardReadThread();
                                  //   mbsCardReadThread.start();
                                  // }
                                  while (!Thread.interrupted()) {

                                          String  res = PiccTester.getInstance(context).detectMbsCard();
                                          if (res != "") {
                                              res= "nfc_mode"+res;
                                              Message.obtain(mbsCardReadHandler, 0, res).sendToTarget();
                                              SystemClock.sleep(1000);
                                              break;

                                          } else {
                                              Log.i(TAG,"nfc read else !");

                                          }
                                      SystemClock.sleep(100);
                                  }

                            }
                            });
                            return true;
                      }else if (action.equals("getSN")){
                    cordova.getThreadPool().execute(new Runnable() {
                      @Override
                      public void run() {

                        context = cordova.getActivity().getApplicationContext();
                        String sn = PedTester.getInstance(context).getSerialNumber();
                        Log.d("Pax","Serial Number "+sn);
                        callbackContext.success(sn);
                      }
                  });
        }
                  return true;

      }
    public Bitmap base64ToImage(String base64Image){

      byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
      Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
     return decodedByte;
    }


      public class PiccDectedThread extends Thread {
          @Override
          public void run() {
              super.run();
              String resString = "";
              while (!Thread.interrupted()) {
                  String  res = PiccTester.getInstance(context).detectM();
                  if (res != "") {

                      Message.obtain(nfcCardHandler, 0, res).sendToTarget();
                      SystemClock.sleep(2000);
                      break;

                  } else {
                      SystemClock.sleep(2000);
                  }
              }
          }
      }
      public class IccDectedThread extends Thread {
          @Override
          public void run() {
              super.run();
              String resString = "";
              IccTester.getInstance(context).light(true);
              while (!Thread.interrupted()) {
                  b = IccTester.getInstance(context).detect((byte) 0);
                  if (b) {
                      // resString = getResources().getString(R.string.icc_detect_havecard);
                      byte[] res = IccTester.getInstance(context).init((byte) 0);
                      if (res == null) {
                          Log.i("Test", "init ic card,but no response");
                          return;
                      }

                      IccTester.getInstance(context).close((byte) 0);
                      IccTester.getInstance(context).light(false);
                      Message.obtain(cardHandler, 0, "card_detetcted").sendToTarget();
                      SystemClock.sleep(2000);
                      break;

                  } else {
                      SystemClock.sleep(2000);
                  }
              }
          }
      }
      class MagReadThread extends Thread {
          @Override
          public void run() {
              super.run();
              while (!Thread.interrupted()) {
                  if (MagTester.getInstance(context).isSwiped()) {
                      TrackData trackData = MagTester.getInstance(context).read();
                      if (trackData != null) {
                          String resStr = "";
                          if (trackData.getResultCode() == 0) {
                              resStr = "mag_card_error";
                              Message.obtain(magHandler, 0, "mag_card_error").sendToTarget();
                              continue;
                          }
                          if ((trackData.getResultCode() & 0x01) == 0x01) {
                              resStr += "mag_track1_data" + trackData.getTrack1();
                            //  Message.obtain(magHandler, 0, resStr).sendToTarget();
                          }
                          if ((trackData.getResultCode() & 0x02) == 0x02) {
                              resStr += "mag_track2_data"+ trackData.getTrack2();
                              Message.obtain(magHandler, 0, resStr).sendToTarget();
                          }
                          if ((trackData.getResultCode() & 0x04) == 0x04) {
                              resStr += "mag_track3_data"+ trackData.getTrack3();
                            //  Message.obtain(magHandler, 0, resStr).sendToTarget();
                          }
                          break;
                      }

                  }
                  SystemClock.sleep(100);
              }
          }
}

// class MbsCardReadThread extends Thread {
//     @Override
//     public void run() {
//         super.run();
//         while (!Thread.interrupted()) {
//             if (MagTester.getInstance(context).isSwiped()) {
//                 TrackData trackData = MagTester.getInstance(context).read();
//                 if (trackData != null) {
//                     String resStr = "";
//                     if (trackData.getResultCode() == 0) {
//                         resStr = "mag_card_error";
//                         Message.obtain(mbsCardReadHandler, 0, "mag_card_error").sendToTarget();
//                         continue;
//                     }
//                     if ((trackData.getResultCode() & 0x01) == 0x01) {
//                         resStr += "mag_track1_data" + trackData.getTrack1();
//                       //  Message.obtain(magHandler, 0, resStr).sendToTarget();
//                     }
//                     if ((trackData.getResultCode() & 0x02) == 0x02) {
//                         resStr += "mag_track2_data"+ trackData.getTrack2();
//                         Message.obtain(mbsCardReadHandler, 0, resStr).sendToTarget();
//                     }
//                     if ((trackData.getResultCode() & 0x04) == 0x04) {
//                         resStr += "mag_track3_data"+ trackData.getTrack3();
//                       //  Message.obtain(magHandler, 0, resStr).sendToTarget();
//                     }
//                     break;
//                 }
//
//             }
//             // else {
//             //   String  res = PiccTester.getInstance(context).detectMbsCard();
//             //   if (res != "") {
//             //       res= "nfc_mode"+res;
//             //       Message.obtain(mbsCardReadHandler, 0, res).sendToTarget();
//             //       SystemClock.sleep(1000);
//             //       break;
//             //
//             //   } else {
//             //                   Log.i(TAG,"nfc read else !");
//             //       // SystemClock.sleep(1000);
//             //   }
//             // }
//     SystemClock.sleep(100);
//           }
//
//         }
//     }
class MbsCardReadThread extends Thread {
  @Override
  public void run() {
      super.run();
      while (!Thread.interrupted()) {

              String  res = PiccTester.getInstance(context).detectMbsCard();
              if (res != "") {
                  res= "nfc_mode"+res;
                  Message.obtain(mbsCardReadHandler, 0, res).sendToTarget();
                  SystemClock.sleep(1000);
                  break;

              } else {
                              Log.i(TAG,"nfc read else !");

              }
          SystemClock.sleep(100);
      }
  }
}
    }
