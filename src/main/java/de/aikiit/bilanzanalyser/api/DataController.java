package de.aikiit.bilanzanalyser.api;

import de.aikiit.bilanzanalyser.entity.database.repository.BilanzRowRepository;
import de.aikiit.bilanzanalyser.entity.database.repository.CategoryRepository;
import de.aikiit.bilanzanalyser.entity.database.repository.PaymentRepository;
import de.aikiit.bilanzanalyser.entity.database.repository.ShopRepository;
import de.aikiit.bilanzanalyser.entity.database.repository.SourceRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public final class DataController {
    private final BilanzRowRepository bilanzRowRepository;
    private final CategoryRepository categoryRepository;
    private final PaymentRepository paymentRepository;
    private final ShopRepository shopRepository;
    private final SourceRepository sourceRepository;

    public DataController(final BilanzRowRepository bilanzRowRepository, final CategoryRepository categoryRepository, final PaymentRepository paymentRepository, final ShopRepository shopRepository, final SourceRepository sourceRepository) {
        this.bilanzRowRepository = bilanzRowRepository;
        this.categoryRepository = categoryRepository;
        this.paymentRepository = paymentRepository;
        this.shopRepository = shopRepository;
        this.sourceRepository = sourceRepository;
    }

    @GetMapping("/dataitems")
    public List<DataItem> getDataItems() {
        return List.of(new DataItem("Bilanz rows", bilanzRowRepository.count()), //
                new DataItem("Categories", categoryRepository.count()), //
                new DataItem("Payment", paymentRepository.count()), //
                new DataItem("Shop", shopRepository.count()), //
                new DataItem("Source", sourceRepository.count())
                //
        );
    }
}
