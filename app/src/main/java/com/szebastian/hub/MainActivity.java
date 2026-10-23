package com.szebastian.hub;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends Activity {

    // package name (applicationId in each app's build.gradle), name, category,
    // and the web version to open if the app isn't installed
    private static final String[][] APPS = {
        {"com.hora.clock", "Hora", "Clock", "https://szebastian31.github.io/hora/"},
        {"com.kalendae.money", "Kalendae", "Budgeting", "https://szebastian31.github.io/kalendae/"},
        {"com.consensus.weather", "Consensus", "Weather", "https://szebastian31.github.io/Consensus_Weather/"},
        {"com.augur.stars", "Augur", "Fate", "https://szebastian31.github.io/augur/"},
        {"com.caelum.sky", "Caelum", "Sky", "https://szebastian31.github.io/caelum/"},
    };

    // same palette as the "Five apps for looking ahead" poster
    private static final int SKY_TOP = 0xFFC9CCF3;
    private static final int SKY_BOTTOM = 0xFFF8CBA9;
    private static final int CARD = 0xF2FFFFFF;
    private static final int BLUE = 0xFF2F3FA8;
    private static final int INK = 0xFF2A2A38;
    private static final int MUTED = 0xFF8C889A;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new SkyDrawable());
    }

    // rebuilt every time the hub comes back, so a newly installed or removed app shows up straight away
    @Override
    protected void onResume() {
        super.onResume();
        setContentView(buildScreen());
    }

    private View buildScreen() {
        LinearLayout column = new LinearLayout(this);
        column.setOrientation(LinearLayout.VERTICAL);
        column.setPadding(dp(20), dp(52), dp(20), dp(28));

        TextView kicker = new TextView(this);
        kicker.setText("HUB  ·  0" + APPS.length + " APPS");
        kicker.setTextColor(MUTED);
        kicker.setTextSize(11);
        kicker.setLetterSpacing(0.18f);
        kicker.setPadding(dp(4), 0, 0, dp(8));
        column.addView(kicker);

        TextView title = new TextView(this);
        title.setText("Five apps for\nlooking ahead.");
        title.setTextColor(BLUE);
        title.setTextSize(32);
        title.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));
        title.setLineSpacing(0, 1.05f);
        title.setPadding(dp(4), 0, 0, dp(22));
        column.addView(title);

        for (int i = 0; i < APPS.length; i++) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = dp(12);
            column.addView(buildRow(APPS[i]), lp);
        }

        TextView footer = new TextView(this);
        footer.setText("MADE TO BE LOOKED UP AT");
        footer.setTextColor(MUTED);
        footer.setTextSize(10);
        footer.setLetterSpacing(0.18f);
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(0, dp(14), 0, 0);
        column.addView(footer);

        ScrollView scroll = new ScrollView(this);
        scroll.addView(column);
        return scroll;
    }

    private View buildRow(String[] app) {
        String pkg = app[0], label = app[1], category = app[2], web = app[3];
        PackageManager pm = getPackageManager();
        Intent launch = pm.getLaunchIntentForPackage(pkg);
        boolean installed = launch != null;

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(16), dp(14), dp(16), dp(14));
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(CARD);
        shape.setCornerRadius(dp(22));
        row.setBackground(new RippleDrawable(ColorStateList.valueOf(0x222F3FA8), shape, null));
        row.setElevation(dp(1));
        if (!installed) row.setAlpha(0.6f);

        LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(dp(56), dp(56));
        iconLp.rightMargin = dp(14);
        row.addView(buildIcon(pm, pkg, label, installed), iconLp);

        LinearLayout text = new LinearLayout(this);
        text.setOrientation(LinearLayout.VERTICAL);

        TextView name = new TextView(this);
        name.setText(label);
        name.setTextColor(INK);
        name.setTextSize(19);
        name.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));
        text.addView(name);

        TextView cat = new TextView(this);
        cat.setText(category);
        cat.setTextColor(BLUE);
        cat.setTextSize(13);
        cat.setTypeface(Typeface.create(Typeface.SERIF, Typeface.ITALIC));
        text.addView(cat);

        if (!installed) {
            TextView note = new TextView(this);
            note.setText("Not installed · opens the web version");
            note.setTextColor(MUTED);
            note.setTextSize(12);
            note.setPadding(0, dp(3), 0, 0);
            text.addView(note);
        }

        row.addView(text, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView chevron = new TextView(this);
        chevron.setText("›");
        chevron.setTextColor(BLUE);
        chevron.setTextSize(26);
        chevron.setPadding(dp(8), 0, 0, dp(2));
        row.addView(chevron);

        row.setContentDescription(label + ", " + category);
        row.setOnClickListener(v -> {
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
        return row;
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
        letter.setTextSize(24);
        letter.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));
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

    // purple-blue to peach, with faint star dots and two thin orbit lines, like the poster
    private class SkyDrawable extends Drawable {
        private final Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint star = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint orbit = new Paint(Paint.ANTI_ALIAS_FLAG);

        SkyDrawable() {
            star.setColor(0xCCFFFFFF);
            orbit.setStyle(Paint.Style.STROKE);
            orbit.setStrokeWidth(dp(1));
            orbit.setColor(0x33FFFFFF);
        }

        @Override
        public void draw(Canvas c) {
            int w = getBounds().width(), h = getBounds().height();
            fill.setShader(new LinearGradient(0, 0, 0, h, SKY_TOP, SKY_BOTTOM, Shader.TileMode.CLAMP));
            c.drawRect(0, 0, w, h, fill);
            c.drawOval(new RectF(-w * 0.4f, h * 0.05f, w * 1.3f, h * 0.55f), orbit);
            c.drawOval(new RectF(-w * 0.2f, h * 0.45f, w * 1.5f, h * 1.1f), orbit);
            Random r = new Random(31); // fixed seed, so the stars don't move between visits
            for (int i = 0; i < 60; i++) {
                c.drawCircle(r.nextFloat() * w, r.nextFloat() * h, dp(1) * (0.5f + r.nextFloat()), star);
            }
        }

        @Override public void setAlpha(int alpha) { }
        @Override public void setColorFilter(ColorFilter cf) { }
        @Override public int getOpacity() { return PixelFormat.OPAQUE; }
    }
}
