package com.example.sara__000.placeinfo;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by sara__000 on 5/20/2017.
 */

public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    Realm realmTodo;
    // we can manage here the realm stuff
    //it is an application class
    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        //then you add the name to the manifestfile


    }

    //create the realm and the changes for the table in db
    public void openRealm() {
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        realmTodo = Realm.getInstance(config);
    }


    public void closeRealm(){
        realmTodo.close();
    }

    public Realm getRealm(){
        return realmTodo;
    }

    //do not use onTerminate, it is used only in Emulator not in real devices
}

