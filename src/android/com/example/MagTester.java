package com.example;

import com.pax.dal.IMag;
import com.pax.dal.entity.TrackData;
import com.pax.dal.exceptions.MagDevException;
import com.pax.neptunelite.api.NeptuneLiteUser;
import android.content.Context;
import com.pax.dal.IDAL;

public class MagTester {

    private static MagTester magTester;

    private IMag iMag;

    private static Context context;

    private MagTester(Context context) {
      try{

        this.context = context;
        IDAL idal = NeptuneLiteUser.getInstance().getDal(this.context );
        iMag = idal.getMag();

      }catch(Exception e){
        e.printStackTrace();
      }

    }

    public static MagTester getInstance(Context context) {
        // if (magTester == null) {
        //     magTester = new MagTester();
        //
        // }
        // return magTester;


      if(magTester == null){
        synchronized (MagTester.class){
          if(magTester == null){

            magTester = new MagTester(context);
          }
        }
      }
          magTester.context = context;
      return magTester;
    }

    public void open() {
        try {
            iMag.open();

        } catch (MagDevException e) {
            e.printStackTrace();

        }
    }

    public void close() {
        try {
            iMag.close();

        } catch (MagDevException e) {
            e.printStackTrace();

        }
    }

    // Reset magnetic stripe card reader, and clear buffer of magnetic stripe card.
    public void reset() {
        try {
            iMag.reset();

        } catch (MagDevException e) {
            e.printStackTrace();

        }
    }

    // Check whether a card is swiped
    public boolean isSwiped() {
        boolean b = false;
        try {
            b = iMag.isSwiped();

        } catch (MagDevException e) {
            e.printStackTrace();

        }
        return b;
    }

    public TrackData read() {
        try {
            TrackData trackData = iMag.read();

            return trackData;
        } catch (MagDevException e) {
            e.printStackTrace();

            return null;
        }
    }

}
