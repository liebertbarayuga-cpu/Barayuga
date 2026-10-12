package com.example.barayuga;

import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final String GREEN = "#1F3D1F";
    private static final String ORANGE = "#F2A93B";
    private static final String CHIP_TEXT = "#463F3A";

    private CardView[] chips;
    private CardView[] navItems;
    private final String[] navSelectedText = {"🏠 Home", "🙏 Prayer", "📅 Events", "👤 Profile"};
    private final String[] navIdleText = {"🏠", "🙏", "📅", "👤"};

    private View cardWorship, cardYouth, cardPrayerNight, weekScroll;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        // Keep the top bar / bottom pill clear of the status & navigation bars.
        View root = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        cardWorship = findViewById(R.id.cardWorship);
        cardYouth = findViewById(R.id.cardYouth);
        cardPrayerNight = findViewById(R.id.cardPrayerNight);
        weekScroll = findViewById(R.id.weekScroll);
        tvEmpty = findViewById(R.id.tvEmpty);

        setupTopBarAndHero();
        setupChips();
        setupEventCards();
        setupQuickLinks();
        setupBottomNav();
    }

    // ---------------------------------------------------------------- top bar + hero
    private void setupTopBarAndHero() {
        findViewById(R.id.btnSearch).setOnClickListener(v -> {
            final EditText input = new EditText(this);
            input.setHint("Search devotions, events, groups");
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setSingleLine(true);
            new AlertDialog.Builder(this)
                    .setTitle("Search")
                    .setView(wrap(input))
                    .setPositiveButton("Search", (d, w) -> {
                        String q = input.getText().toString().trim();
                        toast(q.isEmpty() ? "Type something to search" : "Searching for \"" + q + "\"…");
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        findViewById(R.id.btnStartDevotion).setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Today's Devotion")
                        .setMessage("Take five quiet minutes today.\n\n"
                                + "1. Breathe and be still.\n"
                                + "2. Read a short passage.\n"
                                + "3. Pray for one person by name.")
                        .setPositiveButton("Done", (d, w) -> toast("Devotion complete. Well done!"))
                        .setNegativeButton("Later", null)
                        .show());
    }

    // ---------------------------------------------------------------- filter chips
    private void setupChips() {
        chips = new CardView[]{
                findViewById(R.id.chipAll),
                findViewById(R.id.chipDevotions),
                findViewById(R.id.chipEvents),
                findViewById(R.id.chipGiving)
        };
        for (int i = 0; i < chips.length; i++) {
            final int index = i;
            chips[i].setOnClickListener(v -> selectChip(index));
        }
        selectChip(0);
    }

    private void selectChip(int selected) {
        for (int i = 0; i < chips.length; i++) {
            boolean on = (i == selected);
            chips[i].setCardBackgroundColor(Color.parseColor(on ? GREEN : "#FFFFFF"));
            TextView label = (TextView) chips[i].getChildAt(0);
            label.setTextColor(Color.parseColor(on ? "#FFFFFF" : CHIP_TEXT));
        }

        boolean showWorship = false, showYouth = false, showPrayer = false;
        switch (selected) {
            case 0: // All
                showWorship = showYouth = showPrayer = true;
                break;
            case 1: // Devotions
                showWorship = true;
                showPrayer = true;
                break;
            case 2: // Events
                showYouth = true;
                break;
            case 3: // Giving -> nothing scheduled this week
            default:
                break;
        }
        cardWorship.setVisibility(showWorship ? View.VISIBLE : View.GONE);
        cardYouth.setVisibility(showYouth ? View.VISIBLE : View.GONE);
        cardPrayerNight.setVisibility(showPrayer ? View.VISIBLE : View.GONE);

        boolean empty = !(showWorship || showYouth || showPrayer);
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        weekScroll.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    // ---------------------------------------------------------------- "This Week" cards
    private void setupEventCards() {
        cardWorship.setOnClickListener(v ->
                showEvent("Morning Worship", "Sunday · 9:00 AM\nMain Hall", "Remind me"));
        cardYouth.setOnClickListener(v ->
                showEvent("Youth Night", "Wednesday · 6:30 PM\nFellowship Room", "Remind me"));
        cardPrayerNight.setOnClickListener(v ->
                showEvent("Prayer Night", "Friday · 7:00 PM\nChapel", "Remind me"));
    }

    private void showEvent(String title, String details, String actionLabel) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(details)
                .setPositiveButton(actionLabel, (d, w) -> toast("Reminder set for " + title))
                .setNegativeButton("Close", null)
                .show();
    }

    // ---------------------------------------------------------------- quick links
    private void setupQuickLinks() {
        findViewById(R.id.rowPrayer).setOnClickListener(v -> showPrayerRequestDialog());

        findViewById(R.id.rowGroups).setOnClickListener(v -> {
            final String[] groups = {"Young Adults", "Families", "Prayer Circle", "Bible Study"};
            new AlertDialog.Builder(this)
                    .setTitle("Find a Small Group")
                    .setItems(groups, (d, which) -> toast("Request sent to join " + groups[which]))
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        findViewById(R.id.rowVolunteer).setOnClickListener(v -> {
            final String[] teams = {"Welcome Team", "Music Team", "Kids Ministry", "Media Team"};
            new AlertDialog.Builder(this)
                    .setTitle("Sign up to serve")
                    .setItems(teams, (d, which) -> toast("Thank you! Signed up for " + teams[which]))
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        findViewById(R.id.rowGive).setOnClickListener(v -> {
            final String[] funds = {"Tithe", "Offering", "Mission Fund", "Building Fund"};
            new AlertDialog.Builder(this)
                    .setTitle("Give Online")
                    .setItems(funds, (d, which) -> toast("Giving to " + funds[which]))
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void showPrayerRequestDialog() {
        final EditText input = new EditText(this);
        input.setHint("Write your prayer request");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setMinLines(3);
        new AlertDialog.Builder(this)
                .setTitle("Prayer Request")
                .setView(wrap(input))
                .setPositiveButton("Send", (d, w) -> {
                    String text = input.getText().toString().trim();
                    toast(text.isEmpty() ? "Please write a request first" : "Your request was shared. 🙏");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------------------------------------------------------- bottom nav
    private void setupBottomNav() {
        navItems = new CardView[]{
                findViewById(R.id.navHome),
                findViewById(R.id.navPrayer),
                findViewById(R.id.navEvents),
                findViewById(R.id.navProfile)
        };
        for (int i = 0; i < navItems.length; i++) {
            final int index = i;
            navItems[i].setOnClickListener(v -> {
                selectNav(index);
                if (index == 1) {
                    showPrayerRequestDialog();
                } else if (index == 2) {
                    selectChip(2);
                    toast("Showing this week's events");
                } else if (index == 3) {
                    toast("Profile coming soon");
                } else {
                    selectChip(0);
                }
            });
        }
        selectNav(0);
    }

    private void selectNav(int selected) {
        for (int i = 0; i < navItems.length; i++) {
            boolean on = (i == selected);
            navItems[i].setCardBackgroundColor(on ? Color.parseColor(ORANGE) : Color.TRANSPARENT);
            TextView label = (TextView) navItems[i].getChildAt(0);
            label.setText(on ? navSelectedText[i] : navIdleText[i]);
            label.setTextSize(on ? 14 : 20);
            label.setTextColor(Color.parseColor(on ? GREEN : "#FFFFFF"));
        }
    }

    // ---------------------------------------------------------------- helpers
    private FrameLayout wrap(View child) {
        // Adds side padding around an EditText inside a dialog.
        FrameLayout container = new FrameLayout(this);
        int pad = (int) (20 * getResources().getDisplayMetrics().density);
        container.setPadding(pad, pad / 2, pad, 0);
        container.addView(child);
        return container;
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}