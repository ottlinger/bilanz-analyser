package de.aikiit.bilanzanalyser.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api")
public class DataController {

    @GetMapping("/dataitems")
    public List<DataItem> getDataItems() {
        // TODO replace with real data from database if done
        return List.of(
                new DataItem("Apple", new Random().nextInt(100)),
                new DataItem("Banana", 234),
                new DataItem("Tea", new Random().nextInt(200))
        );
    }
}