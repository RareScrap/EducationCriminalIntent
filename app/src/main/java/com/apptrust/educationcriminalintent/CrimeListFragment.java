package com.apptrust.educationcriminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * @author RareScrap
 */
public class CrimeListFragment extends Fragment
        implements CrimeLab.OnItemDeleteListener {
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private static final int UPDATE_ITEM_REQUEST_CODE = 1;

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private boolean mSubtitleVisible;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        CrimeLab.get(getActivity()).addItemDeleteListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        initUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initUI();
    }

    /**
     * Инициализирует пользовательский интерфейс {@link CrimeListFragment}
     */
    private void initUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        LinkedHashMap<String, Crime> crimes = crimeLab.getCrimes();

        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }

        updateSubtitle();
    }

    @Override
    public void onItemDelete(Crime crime, int position) {
        mAdapter.notifyItemRemoved(position);
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mImageView;

        // TODO: а можно его и не сохранять
        private Crime mCrime;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
            mImageView = itemView.findViewById(R.id.crime_solved);
        }

        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());

            // Используем SimpleDateFormat со своим шаблоном
            SimpleDateFormat dateFormat = (SimpleDateFormat) DateFormat.getDateInstance();
            dateFormat.applyPattern("EEEE, MMM d, yyyy");

            // Начинаем день недели с большой буквы
            /*StringBuffer stringBuffer = new StringBuffer();
            dateFormat.applyPattern("EEEE, ");
            dateFormat.format(mCrime.getDate(), stringBuffer, new FieldPosition(0));
            stringBuffer.replace(0, 1, String.valueOf(stringBuffer.toString().charAt(0)).toUpperCase());
            dateFormat.applyPattern("MMM d, yyyy");
            dateFormat.format(mCrime.getDate(), stringBuffer, new FieldPosition(0));
            mDateTextView.setText(stringBuffer.toString());*/

            // Встроенные в DateFormat шаблон
            //DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL);

            mDateTextView.setText(dateFormat.format(mCrime.getDate()));
            mImageView.setVisibility(mCrime.isSolved() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View view) {
            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
            startActivityForResult(intent, UPDATE_ITEM_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_ITEM_REQUEST_CODE && data != null) {
            UUID crimeId = CrimeFragment.getChagedItemId(data);
            int position = CrimeLab.get(getContext()).getPosition(crimeId);
            if (position != -1) {
                mAdapter.notifyItemChanged(position);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);

                mAdapter.notifyItemChanged(CrimeLab.get(getActivity()).getPosition(crime.getId())); // TODO: не работает?

                Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
                startActivity(intent);
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                updateSubtitle();
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getString(R.string.subtitle_format, crimeCount);

        if (!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private LinkedHashMap<String, Crime> mCrimes;

        public CrimeAdapter(LinkedHashMap<String, Crime> crimes) {
            mCrimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CrimeHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Crime crime = CrimeLab.get(getContext()).getCrimeByIndex(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }
}
