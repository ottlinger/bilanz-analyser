package de.aikiit.bilanzanalyser.api;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api")
public class DataController {

    @GetMapping("/dataitems")
    public List<Pair<String, Integer>> getDataItems() {
        // TODO replace with real data from database if done
        return List.of(Pair.of("Apple", new Random().nextInt(100)), Pair.of("Banana", 234), Pair.of("Tea", new Random().nextInt(200)));
    }
}