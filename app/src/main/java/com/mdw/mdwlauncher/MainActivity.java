package com.mdw.mdwlauncher;
/*
* Source from https://www.itread01.com/content/1547926752.html
*
* */
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<ResolveInfo> mApps;
    private GridView mGrid;

    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
            //事件處理
            ResolveInfo info = mApps.get(position);

            //該應用的包名
            String pkg = info.activityInfo.packageName;
            //應用的主activity類
            String cls = info.activityInfo.name;

            ComponentName componet = new ComponentName(pkg, cls);

            Intent i = new Intent();
            i.setComponent(componet);
            startActivity(i);
        }
    };

    public static void resetPreferredLauncherAndOpenChooser(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ComponentName componentName = new ComponentName(context, com.mdw.mdwlauncher.MainActivity.class);
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        Intent selector = new Intent(Intent.ACTION_MAIN);
        selector.addCategory(Intent.CATEGORY_HOME);
        selector.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(selector);

        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Process p;
        try {
             p = Runtime.getRuntime().exec("su");
            resetPreferredLauncherAndOpenChooser(getApplicationContext());
        }catch (IOException e) {

        }
        loadApps();
        setContentView(R.layout.activity_main);
        mGrid = (GridView) findViewById(R.id.apps_list);
        mGrid.setAdapter(new AppsAdapter());

        mGrid.setOnItemClickListener(listener);

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_MAIN.equals(intent.getAction())) {
            final boolean alreadyOnHome =
                    ((intent.getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
                            != Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            if (alreadyOnHome) {
                Log.d("whatever", "Home pressed");
            }
        }
    }

    private void loadApps() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        mApps = getPackageManager().queryIntentActivities(mainIntent, 0);
    }

    public class AppsAdapter extends BaseAdapter {
        public AppsAdapter() {
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i;


            if (convertView == null) {
                i = new ImageView(MainActivity.this);
                i.setScaleType(ImageView.ScaleType.FIT_CENTER);
                i.setLayoutParams(new GridView.LayoutParams(50, 50));
            } else {
                i = (ImageView) convertView;
            }

            ResolveInfo info = mApps.get(position);
            i.setImageDrawable(info.activityInfo.loadIcon(getPackageManager()));

            return i;
        }

        public final int getCount() {
            return mApps.size();
        }

        public final Object getItem(int position) {
            return mApps.get(position);
        }

        public final long getItemId(int position) {
            return position;
        }
    }
}
