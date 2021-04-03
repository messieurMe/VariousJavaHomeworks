package ru.ifmo.rain.kuliev.crawler;

import info.kgeorgiy.java.advanced.crawler.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

public class WebCrawler implements Crawler {
    private final Downloader downloader;
    private final ExecutorService extractService;
    private final ExecutorService downloadService;

    public WebCrawler(Downloader downloader, int downloaders, int extractors, int perHost) {
        this.downloader = downloader;
        this.downloadService = Executors.newFixedThreadPool(downloaders);
        this.extractService = Executors.newFixedThreadPool(extractors);
    }

    @Override
    public Result download(String url, int depth) {
        Set<String> visited = ConcurrentHashMap.newKeySet();
        Map<String, IOException> exceptions = new ConcurrentHashMap<>();
        Phaser phaser = new Phaser();
        phaser.register();
        downloader(url, depth, visited, exceptions, phaser);
        phaser.arriveAndAwaitAdvance();
        visited.removeAll(exceptions.keySet());
        return new Result(new ArrayList<>(visited), exceptions);
    }

    private void downloader(String url, int depth, Set<String> visited, Map<String, IOException> exceptions, Phaser phaser) {
        phaser.register();
        downloadService.execute(() -> {
            try {
                if (!visited.add(url)) return;
                Document document = downloader.download(url);
                if (depth > 1) {
                    phaser.register();
                    extractService.execute(() -> {
                        try {
                            for (var i : document.extractLinks()) {
                                downloader(i, depth - 1, visited, exceptions, phaser);
                            }
                        } catch (IOException e) {
                            exceptions.put(url, e);
                        } finally {
                            phaser.arrive();
                        }
                    });
                }
            } catch (IOException e) {
                exceptions.put(url, e);
            } finally {
                phaser.arrive();
            }
        });
    }

    @Override
    public void close() {
        downloadService.shutdown();
        extractService.shutdown();
    }

    public static void main(String[] args) {
        if (args == null || args.length < 1 || args.length > 5) {
            System.err.println("Wrong input");
            return;
        }
        String url = args[0];
        int depth = args.length >= 2 ? Integer.parseInt(args[1]) : 1;
        int downloaders = args.length >= 3 ? Integer.parseInt(args[2]) : 1;
        int extractors = args.length >= 4 ? Integer.parseInt(args[3]) : 1;

        try (Crawler crawler = new WebCrawler(new CachingDownloader(), downloaders, extractors, -1)) {
            Result result = crawler.download(url, depth);
            System.out.println("Answers: ");
            result.getDownloaded().forEach(System.out::println);
        } catch (IOException | NumberFormatException e) {
            System.out.println(e.getMessage());
        }
    }
}
