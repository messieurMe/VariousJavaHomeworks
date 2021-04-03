package ru.ifmo.rain.kuliev.crawler;

import org.junit.Before;
import org.junit.Test;

import java.text.Collator;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;


public class TextStatisticsTest {

    @Before
    public void main() {}


    void println(String s){
        System.out.println("\n============\n");
        System.out.println(s);
        System.out.println("\n============\n");

    }

    @Test
    public void enToRu() {
        TextStatistics.lInput = Locale.forLanguageTag("en-US");
        TextStatistics.lOutput = Locale.forLanguageTag("ru-RU");
        Locale.setDefault(TextStatistics.lOutput);
        TextStatistics.dsSep1 = String.valueOf(
                DecimalFormatSymbols.getInstance(TextStatistics.lInput).getDecimalSeparator());
        TextStatistics.dsSep2 = String.valueOf(
                DecimalFormatSymbols.getInstance(TextStatistics.lInput).getGroupingSeparator());
        TextStatistics.collator = Collator.getInstance(TextStatistics.lInput);
        TextStatistics.bundle = ResourceBundle.getBundle("StatRB", TextStatistics.lInput);
        TextStatistics.source =
                "Hello, my name is Username. Today's 1/2/2020, and" + "\n it's nice to have $10.50 at 3 a.m.. Thanks!";

        String sent = TextStatistics.getHtmsStatisticsSentences().trim();
        String line = TextStatistics.getHtmlStatisticsLines().trim();
        String word = TextStatistics.getHtmlStatisticsWords().trim();
        String mone = TextStatistics.getHtmlStatisticsCurrency().trim();
        String numb = TextStatistics.getHtmlStatisticsNumbers().trim();
        String dates = TextStatistics.getHtmlStatisticsDates().trim();

        println(TextStatistics.source);
        println(sent);
        println(line);
        println(word);
        println(mone);
        println(numb);
        println(dates);

        assertEquals("<h3>Статистика по предложениям</h3>\n" + "<p>Число предложений: 3 (3 уникальных)</p> \n" +
                     "<p>Минимальное предложение: Hello, my name is Username.</p>\n" +
                     "<p>Максимальное предложение: Today's 1/2/2020, and\n" +
                     " it's nice to have $10.50 at 3 a.m..</p>\n" +
                     "<p>Минимальная длина предложения: 7 (Thanks!)</p>\n" +
                     "<p>Максимальная длина предложения: 58 (Today's 1/2/2020, and\n" +
                     " it's nice to have $10.50 at 3 a.m..)</p>\n" + "<p>Средняя длина предложения: 30</p>\n".trim(),
                     sent);
        assertEquals("<h3>Статистика по строкам</h3>\n" + "<p>Число строк: 2 (2 уникальных)</p> \n" +
                     "<p>Минимальная строка: Hello, my name is Username. Today's 1/2/2020, and</p>\n" +
                     "<p>Максимальная строка:  it's nice to have $10.50 at 3 a.m.. Thanks!</p>\n" +
                     "<p>Минимальная длина строки: 44 ( it's nice to have $10.50 at 3 a.m.. Thanks!)</p>\n" +
                     "<p>Максимальная длина строки: 49 (Hello, my name is Username. Today's 1/2/2020, and)</p>\n" +
                     "<p>Средняя длина строки: 46</p>".trim(), line);
        assertEquals("<h3>Статистика по словам</h3>\n" + "<p>Число слов: 27 (23 уникальных)</p> \n" +
                     "<p>Минимальное слово: ,</p>\n" + "<p>Максимальное слово: Username</p>\n" +
                     "<p>Минимальная длина слова: 1 (!, ,, ., /, 1, 2, 3)</p>\n" +
                     "<p>Максимальная длина слова: 8 (Username)</p>\n" + "<p>Средняя длина слова: 2</p>".trim(), word);
        assertEquals("<h3>Статистика по числам</h3>\n" + "<p>Число чисел: 5 (5 уникальных)</p> \n" +
                     "<p>Минимальное число: 1</p>\n" + "<p>Максимальное число: 2020</p>\n" +
                     "<p>Минимальная длина числа: 1 (1, 2, 3)</p>\n" + "<p>Максимальная длина числа: 5 (10.50)</p>\n" +
                     "<p>Среднее значение числа: 407</p>".trim(), numb);
        assertEquals("<h3>Статистика по деньгам</h3>\n" + "<p>Число денег: 1 (1 уникальных)</p> \n" +
                     "<p>Минимальная сумма: $10.50</p>\n" + "<p>Максимальная сумма: $10.50</p>\n" +
                     "<p>Минимальная длина суммы: 6 ($10.50)</p>\n" + "<p>Максимальная длина суммы: 6 ($10.50)</p>\n" +
                     "<p>Среднее значение суммы: 10</p>\n".trim(), mone);
        assertEquals("<h3>Статистика по датам</h3>\n" + "<p>Число дат: 1 (1 уникальных)</p> \n" +
                     "<p>Минимальная дата: 1/2/2020</p>\n" + "<p>Максимальная дата: 1/2/2020</p>\n" +
                     "<p>Минимальная длина даты: 8 (1/2/2020)</p>\n" +
                     "<p>Максимальная длина даты: 8 (1/2/2020)</p>\n" +
                     "<p>Среднее значение даты: 02.01.2020</p>".trim(), dates);

    }


    @Test
    public void arToRu() {
        TextStatistics.lInput = Locale.forLanguageTag("ar");
        TextStatistics.lOutput = Locale.forLanguageTag("ru_RU");
        TextStatistics.source = "مرحبا اسمي الاسم. اليوم ٪s أو ٪s ، ومن الجيد أن يكون لديك ٪s في الخميس، ١ يناير ١٩٧٠ صباحًا. شكرًا!";
        Locale.setDefault(TextStatistics.lOutput);
        TextStatistics.dsSep1 = String.valueOf(
                DecimalFormatSymbols.getInstance(TextStatistics.lInput).getDecimalSeparator());
        TextStatistics.dsSep2 = String.valueOf(
                DecimalFormatSymbols.getInstance(TextStatistics.lInput).getGroupingSeparator());
        TextStatistics.collator = Collator.getInstance(TextStatistics.lInput);
        TextStatistics.bundle = ResourceBundle.getBundle("StatRB", Locale.forLanguageTag("ru"));
        String sent = TextStatistics.getHtmsStatisticsSentences().trim();
        String line = TextStatistics.getHtmlStatisticsLines().trim();
        String word = TextStatistics.getHtmlStatisticsWords().trim();
        String mone = TextStatistics.getHtmlStatisticsCurrency().trim();
        String numb = TextStatistics.getHtmlStatisticsNumbers().trim();
        String dates = TextStatistics.getHtmlStatisticsDates().trim();

        println(TextStatistics.source);
        println(sent);
        println(line);
        println(word);
        println(mone);
        println(numb);
        println(dates);

        assertEquals("<h3>Статистика по предложениям</h3>\n" + "<p>Число предложений: 3 (3 уникальных)</p> \n" +
                     "<p>Минимальное предложение: اليوم ٪s أو ٪s ، ومن الجيد أن يكون لديك ٪s في الخميس، ١ يناير ١٩٧٠ صباحًا.</p>\n" +
                     "<p>Максимальное предложение: مرحبا اسمي الاسم.</p>\n" +
                     "<p>Минимальная длина предложения: 6 (شكرًا!)</p>\n" +
                     "<p>Максимальная длина предложения: 74 (اليوم ٪s أو ٪s ، ومن الجيد أن يكون لديك ٪s في الخميس، ١ يناير ١٩٧٠ صباحًا.)</p>\n" +
                     "<p>Средняя длина предложения: 32</p>\n".trim(), sent);
        assertEquals("<h3>Статистика по строкам</h3>\n" + "<p>Число строк: 1 (1 уникальных)</p> \n" +
                     "<p>Минимальная строка: مرحبا اسمي الاسم. اليوم ٪s أو ٪s ، ومن الجيد أن يكون لديك ٪s في الخميس، ١ يناير ١٩٧٠ صباحًا. شكرًا!</p>\n" +
                     "<p>Максимальная строка: مرحبا اسمي الاسم. اليوم ٪s أو ٪s ، ومن الجيد أن يكون لديك ٪s في الخميس، ١ يناير ١٩٧٠ صباحًا. شكرًا!</p>\n" +
                     "<p>Минимальная длина строки: 99 (مرحبا اسمي الاسم. اليوم ٪s أو ٪s ، ومن الجيد أن يكون لديك ٪s في الخميس، ١ يناير ١٩٧٠ صباحًا. شكرًا!)</p>\n" +
                     "<p>Максимальная длина строки: 99 (مرحبا اسمي الاسم. اليوم ٪s أو ٪s ، ومن الجيد أن يكون لديك ٪s في الخميس، ١ يناير ١٩٧٠ صباحًا. شكرًا!)</p>\n" +
                     "<p>Средняя длина строки: 99</p>\n".trim(), line);
        assertEquals("<h3>Статистика по словам</h3>\n" + "<p>Число слов: 28 (22 уникальных)</p> \n" +
                     "<p>Минимальное слово: !</p>\n" + "<p>Максимальное слово: يناير</p>\n" +
                     "<p>Минимальная длина слова: 1 (١, !, ٪, ،, ., s)</p>\n" +
                     "<p>Максимальная длина слова: 6 (الخميس, صباحًا)</p>\n" + "<p>Средняя длина слова: 2</p>\n".trim(),
                     word);
        assertEquals("<h3>Статистика по числам</h3>\n" + "<p>Число чисел: 2 (2 уникальных)</p> \n" +
                     "<p>Минимальное число: ١</p>\n" + "<p>Максимальное число: ١٩٧٠</p>\n" +
                     "<p>Минимальная длина числа: 1 (١)</p>\n" + "<p>Максимальная длина числа: 4 (١٩٧٠)</p>\n" +
                     "<p>Среднее значение числа: 985</p>".trim(), numb);
        assertEquals("<h3>Статистика по деньгам</h3>\n" + "<p>Число денег: 0 (0 уникальных)</p> \n" +
                     "<p>Минимальная сумма: </p>\n" + "<p>Максимальная сумма: </p>\n" +
                     "<p>Минимальная длина суммы: 0 </p>\n" + "<p>Максимальная длина суммы: 0 </p>\n" +
                     "<p>Среднее значение суммы: 0</p>".trim(), mone);
        assertEquals("<h3>Статистика по датам</h3>\n" + "<p>Число дат: 1 (1 уникальных)</p> \n" +
                     "<p>Минимальная дата: الخميس، ١ يناير ١٩٧٠</p>\n" +
                     "<p>Максимальная дата: الخميس، ١ يناير ١٩٧٠</p>\n" +
                     "<p>Минимальная длина даты: 20 (الخميس، ١ يناير ١٩٧٠)</p>\n" +
                     "<p>Максимальная длина даты: 20 (الخميس، ١ يناير ١٩٧٠)</p>\n" +
                     "<p>Среднее значение даты: 1970-01-01</p>\n".trim(), dates);
    }


    @Test
    public void frToRu() {
        TextStatistics.lInput = Locale.forLanguageTag("fr");
        TextStatistics.lOutput = Locale.US;
        TextStatistics.source = "Bonjour, je m'appelle Nom. Aujourd'hui jeudi 1 janvier 1970 ou 01/01/1970, et c'est bien d'avoir 1 234,57 ¤ à 4 du matin. Merci!";
        Locale.setDefault(TextStatistics.lOutput);
        TextStatistics.dsSep1 = String.valueOf(
                DecimalFormatSymbols.getInstance(TextStatistics.lInput).getDecimalSeparator());
        TextStatistics.dsSep2 = String.valueOf(
                DecimalFormatSymbols.getInstance(TextStatistics.lInput).getGroupingSeparator());
        TextStatistics.collator = Collator.getInstance(TextStatistics.lInput);
        TextStatistics.bundle = ResourceBundle.getBundle("StatRB", Locale.forLanguageTag("ru"));

        String sent = TextStatistics.getHtmsStatisticsSentences().trim();
        String line = TextStatistics.getHtmlStatisticsLines().trim();
        String word = TextStatistics.getHtmlStatisticsWords().trim();
        String mone = TextStatistics.getHtmlStatisticsCurrency().trim();
        String numb = TextStatistics.getHtmlStatisticsNumbers().trim();
        String dates = TextStatistics.getHtmlStatisticsDates().trim();

        println(TextStatistics.source);
        println(sent);
        println(line);
        println(word);
        println(mone);
        println(numb);
        println(dates);

        assertEquals("<h3>Статистика по предложениям</h3>\n" + "<p>Число предложений: 3 (3 уникальных)</p> \n" +
                     "<p>Минимальное предложение: Aujourd'hui jeudi 1 janvier 1970 ou 01/01/1970, et c'est bien d'avoir 1 234,57 ¤ à 4 du matin.</p>\n" +
                     "<p>Максимальное предложение: Merci!</p>\n" +
                     "<p>Минимальная длина предложения: 6 (Merci!)</p>\n" +
                     "<p>Максимальная длина предложения: 94 (Aujourd'hui jeudi 1 janvier 1970 ou 01/01/1970, et c'est bien d'avoir 1 234,57 ¤ à 4 du matin.)</p>\n" +
                     "<p>Средняя длина предложения: 42</p>".trim(), sent);
        assertEquals("<h3>Статистика по строкам</h3>\n" + "<p>Число строк: 1 (1 уникальных)</p> \n" +
                     "<p>Минимальная строка: Bonjour, je m'appelle Nom. Aujourd'hui jeudi 1 janvier 1970 ou 01/01/1970, et c'est bien d'avoir 1 234,57 ¤ à 4 du matin. Merci!</p>\n" +
                     "<p>Максимальная строка: Bonjour, je m'appelle Nom. Aujourd'hui jeudi 1 janvier 1970 ou 01/01/1970, et c'est bien d'avoir 1 234,57 ¤ à 4 du matin. Merci!</p>\n" +
                     "<p>Минимальная длина строки: 128 (Bonjour, je m'appelle Nom. Aujourd'hui jeudi 1 janvier 1970 ou 01/01/1970, et c'est bien d'avoir 1 234,57 ¤ à 4 du matin. Merci!)</p>\n" +
                     "<p>Максимальная длина строки: 128 (Bonjour, je m'appelle Nom. Aujourd'hui jeudi 1 janvier 1970 ou 01/01/1970, et c'est bien d'avoir 1 234,57 ¤ à 4 du matin. Merci!)</p>\n" +
                     "<p>Средняя длина строки: 128</p>\n".trim(), line);
        assertEquals("<h3>Статистика по словам</h3>\n" + "<p>Число слов: 34 (28 уникальных)</p> \n" +
                     "<p>Минимальное слово:  </p>\n" + "<p>Максимальное слово:  </p>\n" +
                     "<p>Минимальная длина слова: 1 ( , à, !, ¤, ,, ., /,  , 1, 4)</p>\n" +
                     "<p>Максимальная длина слова: 11 (Aujourd'hui)</p>\n" + "<p>Средняя длина слова: 3</p>\n".trim(),
                     word);
        assertEquals("<h3>Статистика по числам</h3>\n" + "<p>Число чисел: 7 (5 уникальных)</p> \n" +
                     "<p>Минимальное число: 1</p>\n" + "<p>Максимальное число: 1970</p>\n" +
                     "<p>Минимальная длина числа: 1 (1, 4)</p>\n" + "<p>Максимальная длина числа: 8 (1 234,57)</p>\n" +
                     "<p>Среднее значение числа: 740</p>\n".trim(), numb);
        assertEquals("<h3>Статистика по деньгам</h3>\n" + "<p>Число денег: 1 (1 уникальных)</p> \n" +
                     "<p>Минимальная сумма: 1 234,57 ¤</p>\n" + "<p>Максимальная сумма: 1 234,57 ¤</p>\n" +
                     "<p>Минимальная длина суммы: 10 (1 234,57 ¤)</p>\n" +
                     "<p>Максимальная длина суммы: 10 (1 234,57 ¤)</p>\n" +
                     "<p>Среднее значение суммы: 1234</p>\n".trim(), mone);
        assertEquals("<h3>Статистика по датам</h3>\n" + "<p>Число дат: 2 (2 уникальных)</p> \n" +
                     "<p>Минимальная дата: jeudi 1 janvier 1970</p>\n" +
                     "<p>Максимальная дата: jeudi 1 janvier 1970</p>\n" +
                     "<p>Минимальная длина даты: 10 (01/01/1970)</p>\n" +
                     "<p>Максимальная длина даты: 20 (jeudi 1 janvier 1970)</p>\n" +
                     "<p>Среднее значение даты: 1/1/70</p>\n".trim(), dates);
    }

    @Test
    public void ivToRu() {
        TextStatistics.lInput = Locale.forLanguageTag("iw_IL");
        TextStatistics.lOutput = Locale.US;
        TextStatistics.source = "שלום, שמי שם. 1970 Jan 1, Thu או 1970-01-01 של היום, ונחמד שיהיה ¤ 1,234.57 ב- 4 .. תודה!";
        Locale.setDefault(TextStatistics.lOutput);
        TextStatistics.dsSep1 = String.valueOf(
                DecimalFormatSymbols.getInstance(TextStatistics.lInput).getDecimalSeparator());
        TextStatistics.dsSep2 = String.valueOf(
                DecimalFormatSymbols.getInstance(TextStatistics.lInput).getGroupingSeparator());
        TextStatistics.collator = Collator.getInstance(TextStatistics.lInput);
        TextStatistics.bundle = ResourceBundle.getBundle("StatRB", Locale.forLanguageTag("ru"));

        String sent = TextStatistics.getHtmsStatisticsSentences().trim();
        String line = TextStatistics.getHtmlStatisticsLines().trim();
        String word = TextStatistics.getHtmlStatisticsWords().trim();
        String mone = TextStatistics.getHtmlStatisticsCurrency().trim();
        String numb = TextStatistics.getHtmlStatisticsNumbers().trim();
        String dates = TextStatistics.getHtmlStatisticsDates().trim();

        println(TextStatistics.source);
        println(sent);
        println(line);
        println(word);
        println(mone);
        println(numb);
        println(dates);

        assertEquals("<h3>Статистика по предложениям</h3>\n" + "<p>Число предложений: 2 (2 уникальных)</p> \n" +
                     "<p>Минимальное предложение: שלום, שמי שם. 1970 Jan 1, Thu או 1970-01-01 של היום, ונחמד שיהיה ¤ 1,234.57 ב- 4 ..</p>\n" +
                     "<p>Максимальное предложение: תודה!</p>\n" + "<p>Минимальная длина предложения: 5 (תודה!)</p>\n" +
                     "<p>Максимальная длина предложения: 83 (שלום, שמי שם. 1970 Jan 1, Thu או 1970-01-01 של היום, ונחמד שיהיה ¤ 1,234.57 ב- 4 ..)</p>\n" +
                     "<p>Средняя длина предложения: 44</p>\n".trim(), sent);
        assertEquals("<h3>Статистика по строкам</h3>\n" + "<p>Число строк: 1 (1 уникальных)</p> \n" +
                     "<p>Минимальная строка: שלום, שמי שם. 1970 Jan 1, Thu או 1970-01-01 של היום, ונחמד שיהיה ¤ 1,234.57 ב- 4 .. תודה!</p>\n" +
                     "<p>Максимальная строка: שלום, שמי שם. 1970 Jan 1, Thu או 1970-01-01 של היום, ונחמד שיהיה ¤ 1,234.57 ב- 4 .. תודה!</p>\n" +
                     "<p>Минимальная длина строки: 89 (שלום, שמי שם. 1970 Jan 1, Thu או 1970-01-01 של היום, ונחמד שיהיה ¤ 1,234.57 ב- 4 .. תודה!)</p>\n" +
                     "<p>Максимальная длина строки: 89 (שלום, שמי שם. 1970 Jan 1, Thu או 1970-01-01 של היום, ונחמד שיהיה ¤ 1,234.57 ב- 4 .. תודה!)</p>\n" +
                     "<p>Средняя длина строки: 89</p>\n".trim(), line);
        assertEquals("<h3>Статистика по словам</h3>\n" + "<p>Число слов: 31 (23 уникальных)</p> \n" +
                     "<p>Минимальное слово:  </p>\n" + "<p>Максимальное слово: תודה</p>\n" +
                     "<p>Минимальная длина слова: 1 ( , !, ¤, ,, -, ., 1, ב, 4)</p>\n" +
                     "<p>Максимальная длина слова: 8 (1,234.57)</p>\n" + "<p>Средняя длина слова: 2</p>\n".trim(),
                     word);
        assertEquals("<h3>Статистика по числам</h3>\n" + "<p>Число чисел: 7 (5 уникальных)</p> \n" +
                     "<p>Минимальное число: 1</p>\n" + "<p>Максимальное число: 1970</p>\n" +
                     "<p>Минимальная длина числа: 1 (1, 4)</p>\n" + "<p>Максимальная длина числа: 8 (1,234.57)</p>\n" +
                     "<p>Среднее значение числа: 740</p>\n".trim(), numb);
        assertEquals("<h3>Статистика по деньгам</h3>\n" + "<p>Число денег: 1 (1 уникальных)</p> \n" +
                     "<p>Минимальная сумма: ¤ 1,234.57</p>\n" + "<p>Максимальная сумма: ¤ 1,234.57</p>\n" +
                     "<p>Минимальная длина суммы: 10 (¤ 1,234.57)</p>\n" +
                     "<p>Максимальная длина суммы: 10 (¤ 1,234.57)</p>\n" +
                     "<p>Среднее значение суммы: 1234</p>\n".trim(), mone);
        assertEquals("<h3>Статистика по датам</h3>\n" + "<p>Число дат: 2 (2 уникальных)</p> \n" +
                     "<p>Минимальная дата: 1970 Jan 1, Thu</p>\n" + "<p>Максимальная дата: 1970 Jan 1, Thu</p>\n" +
                     "<p>Минимальная длина даты: 10 (1970-01-01)</p>\n" +
                     "<p>Максимальная длина даты: 15 (1970 Jan 1, Thu)</p>\n" +
                     "<p>Среднее значение даты: 1/1/70</p>".trim(), dates);

    }

    @Test
    public void getHtmsStatisticsSentences() {
    }
}