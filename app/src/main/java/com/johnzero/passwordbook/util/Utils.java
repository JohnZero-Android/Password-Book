package com.johnzero.passwordbook.util;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.johnzero.passwordbook.entity.PasswordInfo;


import org.litepal.LitePal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TimeZone;

import static android.service.notification.NotificationListenerService.requestRebind;

/**
 * @author: JohnZero
 * @date: 2020-09-07
 **/
public class Utils {
    private static final String TAG = "江湖";
    public static int mode=Context.MODE_MULTI_PROCESS;
    public static float minMove = 250;         //最小滑动距离
    public static float maxMove = 100;           //最大滑动距离
    public static float minVelocity = 2500;    //最小滑动速度

    public static void log(String str) {
        Log.d(TAG, str);
    }

    public static void toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public void toastSync(Context context, String msg) {
        Looper.prepare();
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

    public static String prefix(int num){
        return num<10?"0"+num:""+num;
    }

    public static String parseString(Calendar calendar){
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        return  year+"-"+prefix(month)+"-"+prefix(day)+" "+prefix(hour)+":"+prefix(minute)+":"+prefix(second);
    }

    public static void passwordIn(Context context){
        FileInputStream in = null;
        BufferedReader reader = null;
        try {
            //in = context.openFileInput("Password Book");
            in=new FileInputStream(new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),"Password Book.txt"));//文件不存在会自动创建
            reader = new BufferedReader(new InputStreamReader(in));
            LitePal.deleteAll(PasswordInfo.class);
            String line = "";
            int count=0;
            PasswordInfo info=null;
            while ((line = reader.readLine()) != null) {
                switch (count){
                    case 0:
                        info=new PasswordInfo();
                        info.setTitle(CipherUtil.Decrypt(line.replace("\n","")));
                        break;
                    case 1:
                        info.setPassword(CipherUtil.Decrypt(line.replace("\n","")));
                        break;
                    case 2:
                        info.setNote(CipherUtil.Decrypt(line.replace("\n","")));
                        break;
                    case 3:
                        info.setAccessTime(CipherUtil.Decrypt(line.replace("\n","")));
                        break;
                    case 4:
                        info.setModifyTime(CipherUtil.Decrypt(line.replace("\n","")));
                        info.save();
                        count=-1;
                        break;
                }
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void passwordOut(Context context) throws Exception {
        List<PasswordInfo>infos= LitePal.findAll(PasswordInfo.class);
        String data="";
        for(PasswordInfo info:infos){
            data+=CipherUtil.Encrypt(info.getTitle())+"\n";
            data+=CipherUtil.Encrypt(info.getPassword())+"\n";
            data+=CipherUtil.Encrypt(info.getNote())+"\n";
            data+=CipherUtil.Encrypt(info.getAccessTime())+"\n";
            data+=CipherUtil.Encrypt(info.getModifyTime())+"\n";
        }
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            //out = context.openFileOutput("Password Book", Context.MODE_PRIVATE); //内部存储
            out=new FileOutputStream(new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),"Password Book.txt")); //主外部存储；卸载软件时该文件也会被删除；覆盖模式
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
