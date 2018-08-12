package com.apptrust.educationcriminalintent;

import android.content.Context;

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
    private LinkedHashMap<String, Crime> mCrimes;

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
        mCrimes = new LinkedHashMap<>();
    }

    /**
     * Геттер для {@link #mCrimes}
     * @return {@link #mCrimes}
     */
    public LinkedHashMap<String, Crime> getCrimes() {
        return mCrimes;
    }

    /**
     * Возвращает объект {@link Crime} из {@link #mCrimes} с заданым ключом {@link UUID}
     * @param id Ключ
     * @return Объект {@link Crime}, если тот существуется в {@link #mCrimes}, иначе - null
     */
        public Crime getCrime(UUID id) {
            return mCrimes.get(id.toString());
        }


    public int getPosition(UUID id) {
        int i = 0;
        for (Map.Entry<String, Crime> entry : mCrimes.entrySet()) {
            Crime crime = entry.getValue();
            if (crime.getId().equals(id)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public Crime getCrimeByIndex(int index) {
        Map.Entry<String, Crime> entry = (Map.Entry<String, Crime>) mCrimes.entrySet().toArray()[index];
        return entry.getValue();
    }

    public boolean isEmpty() {
        return mCrimes.isEmpty();
    }

    public void addCrime(Crime c) {
        mCrimes.put(c.getId().toString(), c);
        notifyItemAdd(c);
    }

    public void deleteCrime(UUID crimeId) { // TODO: Test it!
        Crime deletedCrime = mCrimes.get(crimeId.toString());
        int position = getPosition(crimeId);
        mCrimes.remove(crimeId.toString());
        notifyItemDelete(deletedCrime, position);
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