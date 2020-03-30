package com.apptrust.educationcriminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.LinkedHashMap;
import java.util.UUID;


/**
 * @author RareScrap
 */
public class CrimePagerActivity extends AppCompatActivity
    implements CrimeFragment.Callbacks {
    private static final String EXTRA_CRIME_ID =
            "com.apptrust.educationcriminalintent.crime_id";

    private ViewPager mViewPager;
    private LinkedHashMap<String, Crime> mCrimes;

    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        mCrimes = CrimeLab.get(this).getCrimes();

        mViewPager = (ViewPager) findViewById(R.id.crime_view_pager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                if (mMenu != null) processMenuChanges();
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = CrimeLab.get(CrimePagerActivity.this).getCrimeByIndex(position);
                return CrimeFragment.newInstance(crime.getId());
            }
            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        mViewPager.setCurrentItem(CrimeLab.get(this).getPosition(crimeId));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.crime_pager_activity_menu, menu);
        this.mMenu = menu;

        processMenuChanges();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.to_start:
                mViewPager.setCurrentItem(0);
                return true;
            case R.id.to_end:
                mViewPager.setCurrentItem(mCrimes.size()-1);
                return true;
            case R.id.delete:
                finish();
                return false;
            // И хотя кажется, что onOptionsItemSelected не будет вызван у фрагментов - это не
            // совсем так. Дело в том, что при любом возвращенном булене в активити onOptionsItemSelected
            // у фрагментов все равно вызовется. Но если несолько фрагментов обрабатывают нажатие одного
            // элемента меню, то возврат true в одном из них привет к тому, что onOptionsItemSelected
            // у другого фрагмента не вызовется
            // https://stackoverflow.com/a/33680520/6698055
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    private void processMenuChanges() {
        if (mViewPager.getCurrentItem() == 0) {
            mMenu.findItem(R.id.to_start).setVisible(false);
            mMenu.findItem(R.id.to_start).setEnabled(false);
            mMenu.findItem(R.id.to_end).setVisible(true);
            mMenu.findItem(R.id.to_end).setEnabled(true);
        } else if (mViewPager.getCurrentItem() == mCrimes.size()-1) {
            mMenu.findItem(R.id.to_start).setVisible(true);
            mMenu.findItem(R.id.to_start).setEnabled(true);
            mMenu.findItem(R.id.to_end).setVisible(false);
            mMenu.findItem(R.id.to_end).setEnabled(false);
        } else {
            mMenu.findItem(R.id.to_start).setVisible(true);
            mMenu.findItem(R.id.to_start).setEnabled(true);
            mMenu.findItem(R.id.to_end).setVisible(true);
            mMenu.findItem(R.id.to_end).setEnabled(true);
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {}
}
