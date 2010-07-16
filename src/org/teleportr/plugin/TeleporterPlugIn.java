package org.teleportr.plugin;

import java.util.ArrayList;
import java.util.Date;

import org.teleportr.model.Place;
import org.teleportr.model.Ride;

import android.content.Intent;


public class TeleporterPlugIn implements ITeleporterPlugIn {

    @Override
    public ArrayList<Ride> find(Place o, Place d, Date time) {

        ArrayList<Ride> rides = new ArrayList<Ride>();
        
        Ride r = new Ride();
      
        r = new Ride();
        r.orig = o;
        r.dest = d;
        r.dep = new Date(System.currentTimeMillis());
        r.arr = new Date(System.currentTimeMillis());
        r.mode = Ride.MODE_TELEPORTER;
        
        r.fun = 5;
        r.eco = 5;
        r.fast = 5;
        r.social = 1;
        r.green = 3;
        
        r.intent = new Intent("BEAM");
        
        rides.add(r);
        
        return rides;
    }

}
