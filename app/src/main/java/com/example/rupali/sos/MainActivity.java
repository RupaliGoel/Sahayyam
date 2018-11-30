package com.example.rupali.sos;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import androidx.annotation.IdRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.fragment.app.Fragment;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.ncapdevi.fragnav.FragNavController;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    //-----------------------------------
    Toolbar toolbar;
    Bundle SavedInstanceState;
    Boolean Islogin;
    TextView appnametv;
    //-----------------------------------

    //------------------------------code for bottom navigation tabs-----------------------------------
    private BottomBar mBottomBar;
    private FragNavController fragNavController;

    //indices to fragments
    private final int TAB_SEARCH = FragNavController.TAB1;
    private final int TAB_DECLARE = FragNavController.TAB2;
    private final int TAB_HOME = FragNavController.TAB3;

    //------------------------------code for bottom navigation tabs-----------------------------------

    String user_name,user_email,toolbarMessage,profileimageurl;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SavedInstanceState = savedInstanceState;
        setContentView(R.layout.activity_main);

        //requestsPermissions();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();
        Islogin = prefs.getBoolean("Islogin", false);
        user_email = prefs.getString("user_email","guest@guest.com");
        user_name = prefs.getString("user_name","Guest");
        toolbarMessage = prefs.getString("AppName","App");
        profileimageurl = prefs.getString("profile_picture",null);

       /* ImageView imv = (ImageView)findViewById(R.id.nav_profile_image);
        if (profileimageurl.isEmpty()) {
            imv.setImageResource(R.drawable.profile);
        } else {
            Picasso.with(MainActivity.this)
                    .load(profileimageurl).resize(150, 150)
                    .noFade().into(imv);
        }*/
        //------------------------------code for bottom navigation tabs-----------------------------------
        //FragNav
        //list of fragments
        final List<Fragment> fragments = new ArrayList<>(3);

        //add fragments to list
        fragments.add(new SearchEmergency());
        fragments.add(new InputEmergency());
        fragments.add(new HomeFragment());

        //link fragments to container
        fragNavController = new FragNavController(getSupportFragmentManager(), R.id.container, fragments);
        //End of FragNav

        //BottomBar menu
        mBottomBar = BottomBar.attach(this, savedInstanceState);
        mBottomBar.setMaxFixedTabs(2);
        mBottomBar.setItems(R.menu.bottombar_menu);
        //mBottomBar.getBar().setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.red));

        mBottomBar.setOnMenuTabClickListener(new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                //switch between tabs
                switch (menuItemId) {
                    case R.id.bottomBarItemOne:
                        fragNavController.switchTab(TAB_SEARCH);
                        break;
                    case R.id.bottomBarItemSecond:
                        fragNavController.switchTab(TAB_DECLARE);
                        break;
                    case R.id.bottomBarItemThird:
                        fragNavController.switchTab(TAB_HOME);
                        break;
                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
                if (menuItemId == R.id.bottomBarItemOne) {
                    fragNavController.switchTab(TAB_SEARCH);
                }
                if (menuItemId == R.id.bottomBarItemSecond) {
                    fragNavController.switchTab(TAB_DECLARE);
                }
                if (menuItemId == R.id.bottomBarItemThird) {
                    fragNavController.switchTab(TAB_HOME);
                }
            }
        });

        mBottomBar.mapColorForTab(0, getColor(R.color.tab1));
        mBottomBar.mapColorForTab(1, getColor(R.color.tab2));
        mBottomBar.mapColorForTab(2, getColor(R.color.tab3));

        //------------------------------code for bottom navigation tabs-----------------------------------

        //-----------------------------Code for Navigation Drawer--------------------------------------

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Islogin = prefs.getBoolean("Islogin", false);
        buildNavigationDrawer();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            prefs = PreferenceManager.getDefaultSharedPreferences(this);
            editor = prefs.edit();
            Islogin = prefs.getBoolean("Islogin", false);
            user_email = prefs.getString("user_email","guest@guest.com");
            user_name = prefs.getString("user_name","Guest");

            buildNavigationDrawer();
        }
    }

    /*@Override
    public void onBackPressed() {
        if (fragNavController.getCurrentStack().size() > 1) {
            fragNavController.pop();
        } else {
            super.onBackPressed();
        }
    }*/

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.logo)
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to close this app?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Necessary to restore the BottomBar's state, otherwise we would
        // lose the current tab on orientation change.
        mBottomBar.onSaveInstanceState(outState);
    }
    //-----------------------------Code for Navigation Drawer--------------------------------------

    public void buildNavigationDrawer() {
        if (Islogin) {   // condition true means user is logged in

            new DrawerBuilder().withActivity(this).build();

            DrawerImageLoader.init(new AbstractDrawerImageLoader() {
                @Override
                public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                    if(profileimageurl.equals(""))
                    {
                        imageView.setImageResource(R.drawable.profile);
                    }
                    else{
                        Picasso.with(MainActivity.this)
                                .load(profileimageurl).resize(150,150)
                                .noFade().into(imageView);
                    }
                }

                @Override
                public void cancel(ImageView imageView) {
                    Picasso.with(MainActivity.this).cancelRequest(imageView);
                }

    /*
    @Override
    public Drawable placeholder(Context ctx) {
        return super.placeholder(ctx);
    }

    @Override
    public Drawable placeholder(Context ctx, String tag) {
        return super.placeholder(ctx, tag);
    }
    */
            });

            //primary items
            PrimaryDrawerItem primary_item1 = new PrimaryDrawerItem()
                    .withIdentifier(2)
                    .withName(R.string.drawer_item_upload_photo)
                    .withIcon(R.drawable.ic_upload_photo);
            PrimaryDrawerItem primary_item2 = new PrimaryDrawerItem()
                    .withIdentifier(3)
                    .withName(R.string.drawer_item_my_posts)
                    .withIcon(R.drawable.ic_my_posts);
            PrimaryDrawerItem primary_item3 = new PrimaryDrawerItem()
                    .withIdentifier(4)
                    .withName(R.string.drawer_item_write_review)
                    .withIcon(R.drawable.ic_write_review);
            PrimaryDrawerItem primary_item4 = new PrimaryDrawerItem()
                    .withIdentifier(5)
                    .withName(R.string.drawer_item_glossary)
                    .withIcon(R.drawable.ic_glossary);
            //settings, help, contact items
            SecondaryDrawerItem settings = (SecondaryDrawerItem) new SecondaryDrawerItem()
                    .withIdentifier(97)
                    .withName(R.string.drawer_item_settings)
                    .withIcon(R.drawable.ic_settings);
            SecondaryDrawerItem rate = (SecondaryDrawerItem) new SecondaryDrawerItem()
                    .withIdentifier(98)
                    .withName(R.string.drawer_item_rateus)
                    .withIcon(R.drawable.ic_rate_us);
            SecondaryDrawerItem contact = (SecondaryDrawerItem) new SecondaryDrawerItem()
                    .withIdentifier(99)
                    .withName(R.string.drawer_item_contactus)
                    .withIcon(R.drawable.ic_contact_us);
            SecondaryDrawerItem signout = (SecondaryDrawerItem) new SecondaryDrawerItem()
                    .withIdentifier(100)
                    .withName(R.string.sign_out)
                    .withIcon(R.drawable.ic_sign_out);


            //-----------------------------code for Toolbar---------------------------------------------
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            appnametv = (TextView)findViewById(R.id.appname);
            appnametv.setText(toolbarMessage);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
            //-----------------------------code for Toolbar---------------------------------------------

            AccountHeader headerResult = new AccountHeaderBuilder()
                    .withActivity(this)
                    .withHeaderBackground(R.color.md_grey_600)
                    .addProfiles(
                                new ProfileDrawerItem().withName(user_name).withEmail(user_email).withIcon(profileimageurl)
                    )
                    .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                        @Override
                        public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {

                            Intent intent = new Intent(MainActivity.this, MyProfile.class);
                            MainActivity.this.startActivity(intent);

                            return false;
                        }
                    })
                    .build();

            new DrawerBuilder()
                    .withAccountHeader(headerResult)
                    .withActivity(this)
                    .withToolbar(toolbar)
                    .withActionBarDrawerToggleAnimated(true)
                    .withTranslucentStatusBar(false)
                    .withFullscreen(true)
                    .withSavedInstance(SavedInstanceState)
                    .addDrawerItems(
                            primary_item1,
                            primary_item2,
                            primary_item3,
                            primary_item4,
                            new SectionDrawerItem().withName("Others"),
                            settings,
                            rate,
                            contact,
                            signout
                    )
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            if (drawerItem != null) {
                                Intent intent = null;
                                if (drawerItem.getIdentifier() == 2) {
                                    intent = new Intent(MainActivity.this, UploadPhoto.class);
                                } else if (drawerItem.getIdentifier() == 3) {
                                    intent = new Intent(MainActivity.this, MyPosts.class);
                                } else if (drawerItem.getIdentifier() == 4) {
                                    intent = new Intent(MainActivity.this, WriteReview.class);
                                } else if (drawerItem.getIdentifier() == 5) {
                                    intent = new Intent(MainActivity.this, Glossary.class);
                                } else if (drawerItem.getIdentifier() == 97) {
                                    intent = new Intent(MainActivity.this, Settings.class);
                                } else if (drawerItem.getIdentifier() == 98) {
                                    intent = new Intent(MainActivity.this, RateUs.class);
                                } else if (drawerItem.getIdentifier() == 99) {
                                    intent = new Intent(MainActivity.this, ContactUs.class);
                                } else if (drawerItem.getIdentifier() == 100) {
                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                                    prefs.edit().putBoolean("Islogin", false).commit();
                                    prefs.edit().putString("user_email","").commit();
                                    prefs.edit().putString("user_name","Guest").commit();
                                    prefs.edit().putString("user_role1","").commit();
                                    prefs.edit().putString("user_role2","").commit();
                                    prefs.edit().putString("user_role3","").commit();
                                    prefs.edit().putString("roles","").commit();
                                    prefs.edit().putString("user_contact","").commit();
                                    prefs.edit().putString("user_address","").commit();
                                    Toast.makeText(getBaseContext(), "Successfully Signed Out", Toast.LENGTH_SHORT).show();
                                    Islogin = false;
                                    /////////////////////
                                    Intent i = new Intent(MainActivity.this, MainActivity.class);
                                    // set the new task and clear flags
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                    ////////////////////
                                    //buildNavigationDrawer();

                                }
                                if (intent != null) {
                                    MainActivity.this.startActivity(intent);
                                }
                            }

                            return false;
                        }
                    })
                    .build();
        } else {
            user_name = "Guest";
            user_email = "";
            new DrawerBuilder().withActivity(this).build();

            //primary items
            PrimaryDrawerItem primary_item4 = new PrimaryDrawerItem()
                    .withIdentifier(5)
                    .withName(R.string.drawer_item_glossary)
                    .withIcon(R.drawable.ic_glossary);
            //settings, help, contact items
            SecondaryDrawerItem settings = (SecondaryDrawerItem) new SecondaryDrawerItem()
                    .withIdentifier(97)
                    .withName(R.string.drawer_item_settings)
                    .withIcon(R.drawable.ic_settings);
            SecondaryDrawerItem rate = (SecondaryDrawerItem) new SecondaryDrawerItem()
                    .withIdentifier(98)
                    .withName(R.string.drawer_item_rateus)
                    .withIcon(R.drawable.ic_rate_us);
            SecondaryDrawerItem contact = (SecondaryDrawerItem) new SecondaryDrawerItem()
                    .withIdentifier(99)
                    .withName(R.string.drawer_item_contactus)
                    .withIcon(R.drawable.ic_contact_us);

            //-----------------------------code for Toolbar---------------------------------------------
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);


            //-----------------------------code for Toolbar---------------------------------------------

            AccountHeader headerResult = new AccountHeaderBuilder()
                    .withActivity(this)
                    .withHeaderBackground(R.drawable.header)
                    .addProfiles(
                            new ProfileDrawerItem().withName(user_name).withEmail(user_email).withIcon(getResources().getDrawable(R.drawable.profile))
                    )
                    .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                        @Override
                        public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                            return false;
                        }
                    })
                    .build();

            new DrawerBuilder()
                    .withAccountHeader(headerResult)
                    .withActivity(this)
                    .withToolbar(toolbar)
                    .withActionBarDrawerToggleAnimated(true)
                    .withTranslucentStatusBar(false)
                    .withFullscreen(true)
                    .withSavedInstance(SavedInstanceState)
                    .addDrawerItems(
                            primary_item4,
                            new SectionDrawerItem().withName("Others"),
                            settings,
                            rate,
                            contact
                    )
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            if (drawerItem != null) {
                                Intent intent = null;
                                if (drawerItem.getIdentifier() == 5) {
                                    intent = new Intent(MainActivity.this, Glossary.class);
                                } else if (drawerItem.getIdentifier() == 97) {
                                    intent = new Intent(MainActivity.this, Settings.class);
                                } else if (drawerItem.getIdentifier() == 98) {
                                    intent = new Intent(MainActivity.this, RateUs.class);
                                } else if (drawerItem.getIdentifier() == 99) {
                                    intent = new Intent(MainActivity.this, ContactUs.class);
                                }
                                if (intent != null) {
                                    MainActivity.this.startActivity(intent);
                                }
                            }

                            return false;
                        }
                    })
                    .build();
        }
    }

    public void requestsPermissions(){
        if(!CustomPermissions.Check_STORAGE(MainActivity.this))
        {
            //if not permisson granted so request permisson with request code
            CustomPermissions.Request_STORAGE(MainActivity.this,1);
        }
        if(!CustomPermissions.Check_CAMERA(MainActivity.this))
        {
            //if not permisson granted so request permisson with request code
            CustomPermissions.Request_CAMERA(MainActivity.this,2);
        }
        if(!CustomPermissions.Check_FINE_LOCATION(MainActivity.this))
        {
            //if not permisson granted so request permisson with request code
            CustomPermissions.Request_FINE_LOCATION(MainActivity.this,3);
        }
        if(!CustomPermissions.Check_COURSE_LOCATION(MainActivity.this))
        {
            //if not permisson granted so request permisson with request code
            CustomPermissions.Request_COURSE_LOCATION(MainActivity.this,4);
        }
    }

}
