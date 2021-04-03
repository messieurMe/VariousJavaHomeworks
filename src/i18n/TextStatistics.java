package ru.ifmo.rain.kuliev.crawler;

import java.io.*;
import java.text.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;


public class TextStatistics {
    static Locale lInput;
    static String source;
    static String dsSep1;
    static String dsSep2;
    static Locale lOutput;
    static Collator collator;
    static ResourceBundle bundle;

    static int uniq = 0;
    static int total = 0;
    static long middle = 0;
    static String minV = null;
    static String maxV = null;
    static String minL = null;
    static String maxL = null;
    static int minDatePrev = -1;
    static int maxDatePrev = -1;
    static String finalMiddle = null;
    static String longestValues = null;
    static String shortestValues = null;

    public static void main(String[] args) {
        //args[0] TextStatustics
        //args[1] Locale text input
        //args[2] Locale text output
        //args[3] file input
        //args[4] file output
        if (args.length < 5 || args[1] == null || args[2] == null || args[3] == null || args[4] == null) {
            System.out.println("Incorrect arguments. Please, change input\n" + "        args[0]- TextStatustics\n" +
                               "        //args[1]- Locale text input\n" + "        args[2]- Locale text output\n" +
                               "        //args[3]- file input\n" + "        args[4]- file output\n");
            return;
        }
        input(args);
        try (PrintWriter out = new PrintWriter(new File(args[4]))) {
            bundle = ResourceBundle.getBundle("StatRB", Locale.forLanguageTag(args[2]));
            out.println(String.format(
                    "<html>\n <head>\n<title>Page Title</title>\n</head>\n<body>\n<h1>%s</h1> \n %s \n %s \n %s \n %s \n" +
                    "%s \n %s\n </body>\n </html>\n",
                    MessageFormat.format("{0}: {1}", bundle.getString("File"), args[3]), getHtmsStatisticsSentences(),
                    getHtmlStatisticsLines(), getHtmlStatisticsWords(), getHtmlStatisticsNumbers(),
                    getHtmlStatisticsCurrency(), getHtmlStatisticsDates()));
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write in file. Please, correct output.file");
        }
    }

    private static void input(String[] args) {
        try {
            lInput = Locale.forLanguageTag(args[1]);
            Locale.setDefault(lInput);
            source = new String(Files.readAllBytes(Paths.get(args[3])));

            lOutput = Locale.forLanguageTag(args[2]);
            dsSep1 = String.valueOf(DecimalFormatSymbols.getInstance(lInput).getDecimalSeparator());
            dsSep2 = String.valueOf(DecimalFormatSymbols.getInstance(lInput).getGroupingSeparator());
            collator = Collator.getInstance(lInput);
        } catch (IOException e) {
            System.out.println("Problems with reading file. Check the path and name");
        }
    }

    static String getHtmsStatisticsSentences() {
        parseSource(BreakIterator.getSentenceInstance(lInput));
        return getStringRepresentation("Sent");
    }

    static String getHtmlStatisticsWords() {
        parseSource(BreakIterator.getWordInstance(lInput));
        return getStringRepresentation("Word");
    }

    static String getHtmlStatisticsLines() {
        clearAll();
        String[] lines = source.split("\n");
        HashMap<String, Integer> data = new HashMap<>();
        for (String i : lines) {
            total++;
            String parse = i.replace("\r", "");
            addToData(data, parse);
            checkStats(parse);
        }
        checkNull();
        findAllEntries(data);
        finalMiddle = String.valueOf(middle / total);
        return getStringRepresentation("Line");
    }

    static String getHtmlStatisticsNumbers() {
        parseNumeric(NumberFormat.getNumberInstance(lInput));
        return getStringRepresentation("Num");
    }

    static String getHtmlStatisticsCurrency() {
        parseNumeric(NumberFormat.getCurrencyInstance(lInput));
        return getStringRepresentation("Cur");
    }

    static String getHtmlStatisticsDates() {
        clearAll();
        Map<String, Integer> data = new HashMap<>();
        ParsePosition p = new ParsePosition(0);
        BreakIterator iter = BreakIterator.getWordInstance(lInput);
        iter.setText(source);
        DateFormat[] df = new DateFormat[4];
        for (int i = 0; i < 4; i++) {
            df[i] = DateFormat.getDateInstance(i, lInput);
        }

        int start = iter.first();
        while (start != BreakIterator.DONE) {
            p.setIndex(start);
            for (int i = 0; i < 4; i++) {
                if (df[i].parse(source, p) != null) {
                    total++;
                    String result = source.substring(start, p.getIndex()).trim();
                    while (start < p.getIndex()) {
                        start = iter.next();
                    }
                    addToData(data, result);
                    checkDate(result, i, df);
                    break;
                }
            }
            start = iter.next();
        }
        findAllEntries(data);
        checkNull();
        prepareDateAnswer();
        return getStringRepresentation("Date");
    }

    static void prepareDateAnswer() {
        DateFormat toOutputFormat = DateFormat.getDateInstance(3, lOutput);
        Date middleDate = new Date(total == 0 ? 0 : middle / total);
        finalMiddle = toOutputFormat.format(middleDate);
    }

    static void parseSource(BreakIterator iter) {
        clearAll();
        HashMap<String, Integer> data = new HashMap<>();
        iter.setText(source);
        int start = iter.first();
        while (iter.next() != BreakIterator.DONE) {
            String a = source.substring(start, iter.current()).trim();
            if (checkStats(a) != null) {
                addToData(data, a);
                total++;
            }
            start = iter.current();
        }
        findAllEntries(data);
        checkNull();
        finalMiddle = String.valueOf(total == 0 ? 0 : middle / total);
    }

    static void parseNumeric(NumberFormat nf) {
        clearAll();
        HashMap<String, Integer> data = new HashMap<>();
        ParsePosition p = new ParsePosition(0);
        BreakIterator iter = BreakIterator.getCharacterInstance(lInput);
        iter.setText(source);

        int start = iter.first();
        while (start != BreakIterator.DONE) {
            p.setIndex(start);
            if (nf.parse(source, p) != null) {
                total++;
                String weParsed = checkNum(source.substring(start, p.getIndex()), dsSep1, dsSep2, nf);
                while (start < p.getIndex()) {
                    start = iter.next();
                }
                addToData(data, weParsed);
            }
            start = iter.next();
        }
        findAllEntries(data);
        finalMiddle = (total == 0 ? "0" : String.valueOf(middle / total));
        checkNull();
    }

    static void checkNull() {
        if (minV == null) minV = "";
        if (maxV == null) maxV = "";
        if (minL == null) minL = "";
        if (maxL == null) maxL = "";
        if (finalMiddle == null) finalMiddle = "";
        if (longestValues == null) longestValues = "";
        if (shortestValues == null) shortestValues = "";
    }

    static String checkStats(String a) {
        if (a.isEmpty()) return null;

        middle += a.length();
        if (minL == null || a.length() < minL.length()) minL = a;
        if (maxL == null || a.length() > maxL.length()) maxL = a;
        if (minV == null || collator.compare(minV, a) > 0) minV = a;
        if (maxV == null || collator.compare(maxV, a) < 0) maxV = a;

        return a;
    }

    static String checkNum(String weParsed, String dsep1, String dsep2, NumberFormat nf) {
        if (weParsed.endsWith(dsep2)) weParsed = weParsed.substring(dsep2.length());
        if (weParsed.startsWith(dsep1)) weParsed = weParsed.substring(dsep1.length());
        if (weParsed.endsWith(dsep1)) weParsed = weParsed.substring(0, weParsed.length() - dsep1.length());
        if (weParsed.endsWith(dsep2)) weParsed = weParsed.substring(0, weParsed.length() - dsep2.length());

        try {
            double temp = nf.parse(weParsed).doubleValue();
            middle += temp;
            if (minL == null || weParsed.length() < minL.length()) minL = weParsed;
            if (maxL == null || weParsed.length() > maxL.length()) maxL = weParsed;
            if (minV == null || temp < nf.parse(minV).doubleValue()) minV = weParsed;
            if (maxV == null || temp > nf.parse(maxV).doubleValue()) maxV = weParsed;
        } catch (ParseException ignore) {
        }

        return weParsed;
    }

    static void findAllEntries(Map<String, Integer> data) {
        shortestValues = findStats(minL, data);
        longestValues = findStats(maxL, data);
    }

    static void checkDate(String result, int df, DateFormat[] dateFormat) {
        try {
            long dateInLong = dateFormat[df].parse(result).getTime();
            middle += dateInLong;
            if (minL == null || result.length() < minL.length()) minL = result;
            if (maxL == null || result.length() > maxL.length()) maxL = result;

            if (minV == null) {
                minDatePrev = df;
                maxDatePrev = df;
            }
            if (minV == null || dateInLong < dateFormat[minDatePrev].parse(minV).getTime()) {
                minDatePrev = df;
                minV = result;
            }
            if (maxV == null || dateInLong > dateFormat[maxDatePrev].parse(maxV).getTime()) {
                maxDatePrev = df;
                maxV = result;
            }
        } catch (ParseException ignore) {
        }
    }

    static void addToData(Map<String, Integer> data, String weParsed) {
        if (!data.containsKey(weParsed)) {
            uniq++;
            data.put(weParsed, 1);
        } else {
            data.put(weParsed, data.get(weParsed) + 1);
        }
    }

    static String findStats(String minMax, Map<String, Integer> data) {
        if (minMax == null || minMax.length() == 0) {
            return "";
        }
        String result = Arrays.toString(
                data.keySet().stream().filter(i -> i.length() == minMax.length()).toArray(String[]::new));
        return String.format("(%s)", result.substring(1, result.length() - 1));
    }

    static void clearAll() {
        uniq = 0;
        total = 0;
        middle = 0;
        finalMiddle = "";
        minL = null;
        maxL = null;
        maxV = null;
        minV = null;
        longestValues = null;
        shortestValues = null;
    }

    static String getStringRepresentation(String id) {
        return MessageFormat.format("<h3>{0}</h3>\n" + //0
                                    "<p>{1}: {2, number} ({3, number} {4})</p> \n" + "<p>{5}: {6}</p>\n" + //min
                                    "<p>{7}: {8}</p>\n" + //max
                                    "<p>{9}: {10, number} {11}</p>\n" + //minL
                                    "<p>{12}: {13, number} {14}</p>\n" + //maxL
                                    "<p>{15}: {16}</p>\n", // middle
                                    bundle.getString(id + "Stat"), //0
                                    bundle.getString(id + "Total"), total, uniq, bundle.getString("Uniq"),
                                    bundle.getString(id + "MinV"), minV,//min
                                    bundle.getString(id + "MaxV"), maxV, //max
                                    bundle.getString(id + "MinL"), minL.length(), shortestValues, //minL
                                    bundle.getString(id + "MaxL"), maxL.length(), longestValues, //maxL
                                    bundle.getString(id + "Middle"), finalMiddle);
    }
}
