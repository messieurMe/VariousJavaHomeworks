package ru.ifmo.rain.kuliev.crawler;

public class StatRB_en {
    private final Object[][] CONTENTS = {
            {"File", "Input file"},
            {"Uniq", "unique"},

            {"SentStat", "Statistics by sentences"},
            {"SentTotal", "Number of sentences"},
            {"SentMinV", "Minimal sentence"},
            {"SentMaxV", "Maximal sentence"},
            {"SentMinL", "Minimal length of sentence"},
            {"SentMaxL", "Maximal length of sentence"},
            {"SentMiddle", "Middle length of sentence"},

            {"LineStat", "Statistics by lines"},
            {"LineTotal", "Number of lines"},
            {"LineMinV", "Minimal line"},
            {"LineMaxV", "Maximal line"},
            {"LineMinL", "Minimal length of line"},
            {"LineMaxL", "Maximal length of line"},
            {"LineMiddle", "Midldle length of line"},

            {"WordStat", "Statistics by words"},
            {"WordTotal", "Number of words"},
            {"WordMinV", "Minimal word"},
            {"WordMaxV", "Maximal word"},
            {"WordMinL", "Minimal length of word"},
            {"WordMaxL", "Maximal length of wor"},
            {"WordMiddle", "Middle length of word"},

            {"NumStat", "Statistics by numbers"},
            {"NumTotal", "Number of numbers"},
            {"NumMinV", "Minimal number"},
            {"NumMaxV", "Maximal number"},
            {"NumMinL", "Minimal length of number"},
            {"NumMaxL", "Maximal length of number"},
            {"NumMiddle", "Middle number"},

            {"CurStat", "Statistics by money"},
            {"CurTotal", "Amount of money"},
            {"CurMinV", "Minimal amount"},
            {"CurMaxV", "Maximal amount"},
            {"CurMinL", "Minimal length of amount"},
            {"CurMaxL", "Maximal length of amount"},
            {"CurMiddle", "Middle amount"},

            {"DateStat", "Statistics by dates"},
            {"DateTotal", "Number of dates"},
            {"DateMinV", "Minimal date"},
            {"DateMaxV", "Maximal date"},
            {"DateMinL", "Minimal length of date"},
            {"DateMaxL", "Maximal length of date"},
            {"DateMiddle", "Middle date"},
    };
    protected Object[][] getContents() {
        return CONTENTS;
    }
}
