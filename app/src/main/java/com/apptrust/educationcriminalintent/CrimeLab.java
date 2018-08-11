package com.apptrust.educationcriminalintent;

import android.content.Context;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Синглтон-класс для хранения обьектов {@link Crime} (преступлений)
 *
 * @author RareScrap
 */
public class CrimeLab {
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

    public void addCrime(Crime c) {
        mCrimes.put(c.getId().toString(), c);
    }
}