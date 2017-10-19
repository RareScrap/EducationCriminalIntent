package com.apptrust.educationcriminalintent;

import android.support.v4.app.Fragment;

/**
 * @author RareScrap
 */
public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
