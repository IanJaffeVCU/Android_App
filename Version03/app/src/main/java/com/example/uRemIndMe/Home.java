package com.example.uRemIndMe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class Home extends AppCompatActivity {
    private class ViewPagerAdapter extends FragmentPagerAdapter{

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:return new FragmentHome();
                case 1: return new FragmentReminders();
                case 2: return new FragmentPolls();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    ViewPager vp;
    BottomNavigationView bottomNavigationView;
    MenuItem prevMenu;
    ListView lv;
    DrawerLayout drawerLayout;
    Button logout,aboutus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //Setting up Toolbard
        Toolbar toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);

        //Refrencing
        vp = findViewById(R.id.home_viewpager);
        bottomNavigationView = findViewById(R.id.home_bottom_nav);
        prevMenu = bottomNavigationView.getMenu().getItem(0);
        drawerLayout = findViewById(R.id.home_drawer);
        lv = findViewById(R.id.home_drawer_list);
        logout = findViewById(R.id.home_logout);
        aboutus = findViewById(R.id.home_aboutus);

        //Getting the user info


        vp.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                prevMenu.setChecked(false);
                prevMenu = bottomNavigationView.getMenu().getItem(position);
                prevMenu.setChecked(true);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                menuItem.setChecked(true);
                switch (menuItem.getItemId()){
                    case R.id.home_bottom_nav_home : vp.setCurrentItem(0); break;
                    case R.id.home_bottom_nav_reminders: vp.setCurrentItem(1); break;
                    case R.id.home_bottom_nav_notificaion: vp.setCurrentItem(2);break;
                }

                return false;
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 2 : startActivity(new Intent(Home.this,calendar_p.class));break;
                    case 3 : startActivity(new Intent(Home.this,Attendance.class));break;
                    case 4: startActivity(new Intent(Home.this,Books.class));break;
                    case 0: startActivity(new Intent(Home.this,Profile.class));break;
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(Global.webClientId)
                        .requestEmail()
                        .build();
                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(Home.this, gso);
                mGoogleSignInClient.signOut();
                startActivity(new Intent(Home.this,HomeActivity.class));

                finish();
            }
        });

        aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogAboutUs dialog = new DialogAboutUs();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                dialog.show(ft,"hello");
            }
        });




    }




    @Override
    public boolean onSupportNavigateUp() {
        drawerLayout.openDrawer(Gravity.LEFT);
        return super.onSupportNavigateUp();
    }
}
