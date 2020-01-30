package com.apptrust.educationcriminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.apptrust.educationcriminalintent.database.CrimeBaseHelper;
import com.apptrust.educationcriminalintent.database.CrimeCursorWrapper;
import com.apptrust.educationcriminalintent.database.CrimeDbSchema.CrimeTable;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Синглтон-класс для хранения обьектов {@link Crime} (преступлений)
 *
 * @author RareScrap
 */
public class CrimeLab {
    public interface OnItemAddListener {
        void onItemAdd(Crime crime);
    }
    public interface OnItemChangeListener {
        void onItemChange(Crime crime);
    }
    public interface OnItemDeleteListener {
        void onItemDelete(Crime crime, int position);
    }

    private List<OnItemAddListener> itemAddListeners = new ArrayList<>();
    private List<OnItemChangeListener> itemChangeListeners = new ArrayList<>();
    private List<OnItemDeleteListener> itemDeleteListeners = new ArrayList<>();

    /** Сслыка на свой экземпляр. Необходим для паттерна "Синглтон" */
    private static CrimeLab sCrimeLab;
    /** Список преступлений (данные) */
    private Context mContext; // TODO: имхо, сохранять ссылку на контекст - хуевая идея. Он же запросто может стать неактуальным
    private SQLiteDatabase mDatabase;

    /**
     * Геттер для {@link #sCrimeLab}. Если {@link #sCrimeLab} - null, то создается новый
     * экземпляр {@link CrimeLab}.
     *
     * @param context TODO
     * @return Ссылка на единственный экземпляр класса {@link CrimeLab}
     */
    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    /**
     * Приватный конструктор, необходимый для паттерна "Синглтон"
     *
     * @param context TODO
     */
    private CrimeLab(Context context) {
        // вместо контекста активити пполучаем контекст приложения, т.к. тот более живучий
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext)
                .getWritableDatabase();
    }

    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());
        return values;
    }

    public LinkedHashMap<String, Crime> getCrimes()  {
        LinkedHashMap<String, Crime> crimes = new LinkedHashMap<>();
        CrimeCursorWrapper cursor = queryCrimes(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Crime crime = cursor.getCrime();
                crimes.put(crime.getId().toString(), crime);
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return crimes;
    }

    public Crime getCrime(UUID id) {
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
    }


    public int getPosition(UUID id) {
        int i = 0;
        for (Map.Entry<String, Crime> entry : getCrimes().entrySet()) {
            Crime crime = entry.getValue();
            if (crime.getId().equals(id)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public Crime getCrimeByIndex(int index) {
        Map.Entry<String, Crime> entry = (Map.Entry<String, Crime>) getCrimes().entrySet().toArray()[index];
        return entry.getValue();
    }

    public boolean isEmpty() {
        return getCrimes().isEmpty();
    }

    public void addCrime(Crime c) {
        ContentValues values = getContentValues(c);
        mDatabase.insert(CrimeTable.NAME, null, values);
        notifyItemAdd(c);
    }

    public File getPhotoFile(Crime crime) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, crime.getPhotoFilename());
    }

    public void updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);
        mDatabase.update(CrimeTable.NAME, values,
                CrimeTable.Cols.UUID + " = ?", // ? предотвращает SQL инъекции
                new String[] { uuidString });
        notifyItemChange(crime);
    }

    public void deleteCrime(UUID crimeId) { // TODO: Test it!
        Crime crime = getCrime(crimeId);
        mDatabase.delete(CrimeTable.NAME,
                CrimeTable.Cols.UUID + " = ?",
                new String[] { crimeId.toString() });

        notifyItemDelete(crime, getPosition(crimeId));
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null, // columns - с null выбираются все столбцы
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new CrimeCursorWrapper(cursor);
    }

    private void notifyItemAdd(Crime crime) {
        for (OnItemAddListener itemAddListener : itemAddListeners) {
            itemAddListener.onItemAdd(crime);
        }
    }

    public void notifyItemChange(Crime crime) {
        for (OnItemChangeListener itemChangeListener : itemChangeListeners) {
            itemChangeListener.onItemChange(crime);
        }
    }

    private void notifyItemDelete(Crime crime, int position) {
        for (OnItemDeleteListener itemDeleteListener : itemDeleteListeners) {
            itemDeleteListener.onItemDelete(crime, position);
        }
    }

    // TODO: Как избежать этого boiler-plate кода?
    public void addItemAddListener(OnItemAddListener itemAddListener) {
        if (!itemAddListeners.contains(itemAddListener))
            itemAddListeners.add(itemAddListener);
    }

    public void removeItemAddListener(OnItemAddListener itemAddListener) {
        itemAddListeners.remove(itemAddListener);
    }

    public void addItemChangeListener(OnItemChangeListener itemChangeListener) {
        if (!itemChangeListeners.contains(itemChangeListener))
            itemChangeListeners.add(itemChangeListener);
    }

    public void removeItemChangeListener(OnItemChangeListener itemChangeListener) {
        itemChangeListeners.remove(itemChangeListener);
    }

    public void addItemDeleteListener(OnItemDeleteListener itemDeleteListener) {
        if (!itemDeleteListeners.contains(itemDeleteListener))
            itemDeleteListeners.add(itemDeleteListener);
    }

    public void removeItemChangeListener(OnItemDeleteListener itemDeleteListener) {
        itemDeleteListeners.remove(itemDeleteListener);
    }
}