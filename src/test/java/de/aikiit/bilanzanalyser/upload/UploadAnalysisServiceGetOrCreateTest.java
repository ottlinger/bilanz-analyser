package de.aikiit.bilanzanalyser.upload;

import de.aikiit.bilanzanalyser.entity.database.PaymentEntity;
import de.aikiit.bilanzanalyser.entity.database.ShopEntity;
import de.aikiit.bilanzanalyser.entity.database.repository.BilanzRowRepository;
import de.aikiit.bilanzanalyser.entity.database.repository.CategoryRepository;
import de.aikiit.bilanzanalyser.entity.database.repository.PaymentRepository;
import de.aikiit.bilanzanalyser.entity.database.repository.ShopRepository;
import de.aikiit.bilanzanalyser.entity.database.repository.SourceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UploadAnalysisServiceGetOrCreateTest {
    @Mock
    private BilanzRowRepository bilanzRowRepository;
    @Mock
    private ShopRepository shopRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private SourceRepository sourceRepository;

    @InjectMocks
    private UploadAnalysisService service;

    @Test
    void getOrCreatePayment_shouldReturnExisting() {
        PaymentEntity existing = new PaymentEntity();
        existing.setName("Cash");

        when(paymentRepository.findByName("Cash")).thenReturn(Optional.of(existing));

        PaymentEntity result = service.getOrCreatePayment("Cash");

        assertSame(existing, result);
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void getOrCreatePayment_shouldCreateNew() {
        when(paymentRepository.findByName("Card")).thenReturn(Optional.empty());

        PaymentEntity saved = new PaymentEntity();
        saved.setName("Card");

        when(paymentRepository.save(any())).thenReturn(saved);

        PaymentEntity result = service.getOrCreatePayment("Card");

        assertThat(result.getName()).isEqualTo("Card");
        verify(paymentRepository).save(any());
    }

    @Test
    void getOrCreateShop_shouldReturnExisting() {
        ShopEntity existing = new ShopEntity();
        existing.setName("MyShop");

        when(shopRepository.findByName("MyShop")).thenReturn(Optional.of(existing));

        ShopEntity result = service.getOrCreateShop("MyShop");

        assertSame(existing, result);
        verify(shopRepository, never()).save(any());
    }

    @Test
    void getOrCreateShop_shouldCreateNew() {
        when(shopRepository.findByName("MyNewShop")).thenReturn(Optional.empty());

        ShopEntity saved = new ShopEntity();
        saved.setName("MyNewShop");

        when(shopRepository.save(any())).thenReturn(saved);

        ShopEntity result = service.getOrCreateShop("MyNewShop");

        assertThat(result.getName()).isEqualTo("MyNewShop");
        verify(shopRepository).save(any());
    }

}
