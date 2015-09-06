package it.jaschke.alexandria;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import it.jaschke.alexandria.api.Callback;

public class TestActivity2 extends AppCompatActivity implements Callback, BookDetail.BookDetailCallback {

    /**
     * Used to store the last screen title. For use in
     * {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private BroadcastReceiver messageReciever;
    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";
    public static final String MESSAGE_KEY = "MESSAGE_EXTRA";
    int pos;


    //My new only needed base requirement
    private DrawerLayout drawerLayoutt;
    private ListView listView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_main);
        initialize();
        setSupportActionBar(toolbar);
        populateDrawerList();
        mTitle = getTitle();
        messageReciever = new MessageReciever();
        IntentFilter filter = new IntentFilter(MESSAGE_EVENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReciever, filter);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayoutt, toolbar,
                R.string.app_name, R.string.app_name) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                restoreActionBar();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                showGlobalContextActionBar();
            }

        };

        drawerLayoutt.setDrawerListener(actionBarDrawerToggle);

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (savedInstanceState == null) {
            selectItem(0);
        }

        String isbn = getIntent().getStringExtra(AddBook.SCAN_CONTENTS);
        String format = getIntent().getStringExtra(AddBook.SCAN_FORMAT);
        if (isbn != null && format != null) {
            Bundle args = new Bundle();
            args.putString(AddBook.SCAN_CONTENTS, isbn);
            args.putString(AddBook.SCAN_FORMAT, format);
            AddBook addBook = new AddBook();
            addBook.setArguments(args);
            goToFragment(addBook);
        }

    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReciever);
        super.onDestroy();
    }

    private void initialize() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayoutt = (DrawerLayout) findViewById(R.id.drawer_layout);
        listView = (ListView) findViewById(R.id.left_drawer);

    }

    private void populateDrawerList() {

//        ArrayList<String> navigationDrawerItems = new ArrayList<>();
//        navigationDrawerItems.add("one");
//        navigationDrawerItems.add("two");
//        navigationDrawerItems.add("three");
//        navigationDrawerItems.add("four");

        String[] items = new String[]{
                getString(R.string.books),
                getString(R.string.scan),
                getString(R.string.about),
        };

        // set a custom shadow that overlays the main content when the drawer opens
        drawerLayoutt.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items));
        listView.setOnItemClickListener(new DrawerItemClickListener());


    }

    @Override
    public void onItemSelected(String ean) {
        Bundle args = new Bundle();
        args.putString(BookDetail.EAN_KEY, ean);

        BookDetail fragment = new BookDetail();
        fragment.setArguments(args);

        int id = R.id.container;

        if (isDualPane()) {
            id = R.id.right_container;
            findViewById(R.id.emptyFrameLayoutText).setVisibility(View.GONE); // Hide TextView
            getSupportFragmentManager().beginTransaction()
                    .replace(id, fragment)
                    .commit();
        }else {
            Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
            intent.putExtra(BookDetail.EAN_KEY, ean);
            startActivity(intent);
        }
    }

    @Override
    public void onBarcodeScanned(String isbn) {

    }

    @Override
    public void onDeleteBook() {
        // do nothing
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // TODO Auto-generated method stub
            selectItem(position);
        }

    }

    private void selectItem(int position) {

        switch (position) {
            default:
            case 0:
                // if (pos != 0)
                goToFragment(new ListOfBooks());
                mTitle = getString(R.string.books);
                break;
            case 1:
                // if (pos != 1)
                goToFragment(new AddBook());
                mTitle = getString(R.string.scan);
                break;
            case 2:
                //  if (pos != 2)
                goToFragment(new About());
                mTitle = getString(R.string.about);
                break;

        }

        if (drawerLayoutt.isDrawerOpen(GravityCompat.START)) {
            drawerLayoutt.closeDrawer(GravityCompat.START);
        }

        pos = position;
    }


    private void showGlobalContextActionBar() {

        //  getSupportActionBar().setDisplayShowTitleEnabled(true);
        //  getSupportActionBar().setTitle(R.string.app_name);
        //  getSupportActionBar().setIcon(null);
        toolbar.setTitle(R.string.app_name);
     //   toolbar.setBackgroundColor(getResources().getColor(R.color.accent_material_light));
    }

    private void restoreActionBar() {
        //   getSupportActionBar().setDisplayShowTitleEnabled(true);
        //   getSupportActionBar().setTitle(mTitle);
        //  getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.ic_launcher));
        toolbar.setTitle(mTitle);
     //   toolbar.setBackgroundColor(getResources().getColor(R.color.accent_material_light));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggle
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.main, menu);

        // Only show items in the action bar relevant to this screen
        // if the drawer is not showing. Otherwise, let the drawer
        // decide what to show in the action bar.
        if (!drawerLayoutt.isDrawerOpen(GravityCompat.START)) {
            restoreActionBar();
            // return true;
        } else {
            showGlobalContextActionBar();

        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings)
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        return super.onOptionsItemSelected(item);
    }


    private class MessageReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra(MESSAGE_KEY) != null) {
                Toast.makeText(getApplicationContext(), intent.getStringExtra(MESSAGE_KEY), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void goBack(View view) {
      //  getSupportFragmentManager().popBackStack();
    }

//    private boolean isTablet() {
//        return (getApplicationContext().getResources().getConfiguration().screenLayout
//                & Configuration.SCREENLAYOUT_SIZE_MASK)
//                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
//    }
//

    private boolean isDualPane() {
        return findViewById(R.id.right_container) != null;
    }

    private void goToFragment(Fragment nextFragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, nextFragment).commit();
    }



}
