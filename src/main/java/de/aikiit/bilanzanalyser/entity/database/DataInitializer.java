package de.aikiit.bilanzanalyser.entity.database;

import de.aikiit.bilanzanalyser.entity.database.repository.PaymentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(PaymentRepository paymentRepository) {
        return args -> {

            // payment categories
            for (String category : List.of("KK", "Bar", "EC", "Überwiesen")) {
                paymentRepository.findByName(category).orElseGet(() -> {
                    PaymentEntity payment = new PaymentEntity();
                    payment.setName(category);
                    return paymentRepository.save(payment);
                });
            }
        };
    }
}