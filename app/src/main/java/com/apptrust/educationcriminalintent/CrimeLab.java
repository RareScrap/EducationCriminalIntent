package com.apptrust.educationcriminalintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
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
    private List<Crime> mCrimes;

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
        mCrimes = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Crime crime = new Crime();
            crime.setTitle("Crime #" + i);
            crime.setSolved(i % 2 == 0); // Для каждого второго объекта
            mCrimes.add(crime);
        }
    }

    /**
     * Геттер для {@link #mCrimes}
     * @return {@link #mCrimes}
     */
    public List<Crime> getCrimes() {
        return mCrimes;
    }

    /**
     * Возвращает объект {@link CrimeLab} из {@link #mCrimes} с заданым ключом {@link UUID}
     * @param id Ключ
     * @return Объект {@link Crime}, если тот существуется в {@link #mCrimes}, иначе - null
     */
    public Crime getCrime(UUID id) {
        for (Crime crime : mCrimes) {
            if (crime.getId().equals(id)) {
                return crime;
            }
        }
        return null;
    }

    public int getPosition(UUID id) {
        for (int i = 0; i < mCrimes.size(); i++) {
            Crime crime = mCrimes.get(i);
            if (crime.getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }
}