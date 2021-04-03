package ru.ifmo.rain.kuliev.crawler;

import java.util.ListResourceBundle;

public class StatRB_ru extends ListResourceBundle {
    private final Object[][] CONTENTS = {
            {"File", "Анализируемый файл"},
            {"Uniq", "уникальных"},

            {"SentStat", "Статистика по предложениям"},
            {"SentTotal", "Число предложений"},
            {"SentMinV", "Минимальное предложение"},
            {"SentMaxV", "Максимальное предложение"},
            {"SentMinL", "Минимальная длина предложения"},
            {"SentMaxL", "Максимальная длина предложения"},
            {"SentMiddle", "Средняя длина предложения"},

            {"LineStat", "Статистика по строкам"},
            {"LineTotal", "Число строк"},
            {"LineMinV", "Минимальная строка"},
            {"LineMaxV", "Максимальная строка"},
            {"LineMinL", "Минимальная длина строки"},
            {"LineMaxL", "Максимальная длина строки"},
            {"LineMiddle", "Средняя длина строки"},

            {"WordStat", "Статистика по словам"},
            {"WordTotal", "Число слов"},
            {"WordMinV", "Минимальное слово"},
            {"WordMaxV", "Максимальное слово"},
            {"WordMinL", "Минимальная длина слова"},
            {"WordMaxL", "Максимальная длина слова"},
            {"WordMiddle", "Средняя длина слова"},

            {"NumStat", "Статистика по числам"},
            {"NumTotal", "Число чисел"},
            {"NumMinV", "Минимальное число"},
            {"NumMaxV", "Максимальное число"},
            {"NumMinL", "Минимальная длина числа"},
            {"NumMaxL", "Максимальная длина числа"},
            {"NumMiddle", "Среднее значение числа"},

            {"CurStat", "Статистика по деньгам"},
            {"CurTotal", "Число денег"},
            {"CurMinV", "Минимальная сумма"},
            {"CurMaxV", "Максимальная сумма"},
            {"CurMinL", "Минимальная длина суммы"},
            {"CurMaxL", "Максимальная длина суммы"},
            {"CurMiddle", "Среднее значение суммы"},

            {"DateStat", "Статистика по датам"},
            {"DateTotal", "Число дат"},
            {"DateMinV", "Минимальная дата"},
            {"DateMaxV", "Максимальная дата"},
            {"DateMinL", "Минимальная длина даты"},
            {"DateMaxL", "Максимальная длина даты"},
            {"DateMiddle", "Среднее значение даты"},
    };
    protected Object[][] getContents() {
        return CONTENTS;
    }
}
