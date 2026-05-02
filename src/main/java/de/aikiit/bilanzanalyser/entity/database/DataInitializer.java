package de.aikiit.bilanzanalyser.entity.database;

import de.aikiit.bilanzanalyser.entity.database.repository.PaymentRepository;
import de.aikiit.bilanzanalyser.entity.database.repository.SourceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration // must not be final
public class DataInitializer {

    @Bean
    CommandLineRunner initData(final PaymentRepository paymentRepository, final SourceRepository sourceRepository) {
        return args -> {

            // payment categories
            for (String category : List.of("KK", "Bar", "EC", "Überwiesen")) {
                paymentRepository.findByName(category).orElseGet(() -> {
                    PaymentEntity payment = new PaymentEntity();
                    payment.setName(category);
                    return paymentRepository.save(payment);
                });
            }

            // sources that can be used during upload
            for (String source : List.of("Ausgaben", "Einnahmen")) {
                sourceRepository.findByName(source).orElseGet(() -> {
                    SourceEntity sourceEntity = new SourceEntity();
                    sourceEntity.setName(source);
                    return sourceRepository.save(sourceEntity);
                });
            }
        };
    }
}
