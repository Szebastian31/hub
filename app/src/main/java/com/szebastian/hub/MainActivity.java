package com.szebastian.hub;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    // package name (applicationId in each app's build.gradle), name on the tile,
    // and the web version to open if the app isn't installed
    private static final String[][] APPS = {
        {"com.hora.clock", "Hora", "https://szebastian31.github.io/hora/"},
        {"com.kalendae.money", "Kalendae", "https://szebastian31.github.io/kalendae/"},
        {"com.consensus.weather", "Consensus Weather", "https://szebastian31.github.io/Consensus_Weather/"},
        {"com.augur.stars", "Augur", "https://szebastian31.github.io/augur/"},
        {"com.caelum.sky", "Caelum", "https://szebastian31.github.io/caelum/"},
    };

    private static final int BG = 0xFF0E1116;
    private static final int TILE = 0xFF1A1F27;
    private static final int TEXT = 0xFFF2F4F7;
    private static final int MUTED = 0xFF8A94A6;

    // rebuilt every time the hub comes back, so a newly installed or removed app shows up straight away
    @Override
    protected void onResume() {
        super.onResume();
        setContentView(buildScreen());
    }

    private View buildScreen() {
        LinearLayout column = new LinearLayout(this);
        column.setOrientation(LinearLayout.VERTICAL);
        column.setPadding(dp(18), dp(56), dp(18), dp(24));

        TextView title = new TextView(this);
        title.setText("Apps");
        title.setTextColor(TEXT);
        title.setTextSize(30);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setPadding(dp(6), 0, 0, dp(18));
        column.addView(title);

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(2);
        for (int i = 0; i < APPS.length; i++) {
            // with an odd number of apps, the last tile spans both columns
            boolean wide = i == APPS.length - 1 && APPS.length % 2 == 1;
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams(
                GridLayout.spec(GridLayout.UNDEFINED),
                GridLayout.spec(GridLayout.UNDEFINED, wide ? 2 : 1, 1f));
            lp.width = 0;
            lp.height = dp(160);
            lp.setMargins(dp(6), dp(6), dp(6), dp(6));
            grid.addView(buildTile(APPS[i]), lp);
        }
        column.addView(grid);

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(BG);
        scroll.addView(column);
        return scroll;
    }

    private View buildTile(String[] app) {
        String pkg = app[0], label = app[1], web = app[2];
        PackageManager pm = getPackageManager();
        Intent launch = pm.getLaunchIntentForPackage(pkg);
        boolean installed = launch != null;

        LinearLayout tile = new LinearLayout(this);
        tile.setOrientation(LinearLayout.VERTICAL);
        tile.setGravity(Gravity.CENTER);
        tile.setPadding(dp(12), dp(12), dp(12), dp(12));
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(TILE);
        shape.setCornerRadius(dp(26));
        tile.setBackground(new RippleDrawable(ColorStateList.valueOf(0x33FFFFFF), shape, null));

        tile.addView(buildIcon(pm, pkg, label, installed), new LinearLayout.LayoutParams(dp(64), dp(64)));

        TextView name = new TextView(this);
        name.setText(label);
        name.setTextColor(installed ? TEXT : MUTED);
        name.setTextSize(16);
        name.setTypeface(Typeface.DEFAULT_BOLD);
        name.setGravity(Gravity.CENTER);
        name.setPadding(0, dp(12), 0, 0);
        tile.addView(name);

        if (!installed) {
            TextView note = new TextView(this);
            note.setText("Not installed · opens the web version");
            note.setTextColor(MUTED);
            note.setTextSize(11);
            note.setGravity(Gravity.CENTER);
            note.setPadding(0, dp(4), 0, 0);
            tile.addView(note);
        }

        tile.setContentDescription(label);
        tile.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            try {
                if (installed) {
                    launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(launch);
                } else {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(web)));
                }
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "Couldn't open " + label, Toast.LENGTH_SHORT).show();
            }
        });
        return tile;
    }

    // the app's own launcher icon when it's installed, otherwise its first letter in a circle
    private View buildIcon(PackageManager pm, String pkg, String label, boolean installed) {
        if (installed) {
            try {
                Drawable d = pm.getApplicationIcon(pkg);
                ImageView icon = new ImageView(this);
                icon.setImageDrawable(d);
                return icon;
            } catch (PackageManager.NameNotFoundException ignored) {
                // fall through to the letter
            }
        }
        TextView letter = new TextView(this);
        letter.setText(label.substring(0, 1));
        letter.setTextColor(MUTED);
        letter.setTextSize(26);
        letter.setTypeface(Typeface.DEFAULT_BOLD);
        letter.setGravity(Gravity.CENTER);
        GradientDrawable circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);
        circle.setStroke(dp(2), MUTED);
        letter.setBackground(circle);
        return letter;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
