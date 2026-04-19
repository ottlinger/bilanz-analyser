package de.aikiit.bilanzanalyser.upload;

import de.aikiit.bilanzanalyser.entity.BilanzRow;
import de.aikiit.bilanzanalyser.entity.database.BilanzRowEntity;
import de.aikiit.bilanzanalyser.entity.database.CategoryEntity;
import de.aikiit.bilanzanalyser.entity.database.PaymentEntity;
import de.aikiit.bilanzanalyser.entity.database.ShopEntity;
import de.aikiit.bilanzanalyser.entity.database.SourceEntity;
import de.aikiit.bilanzanalyser.entity.database.repository.BilanzRowRepository;
import de.aikiit.bilanzanalyser.entity.database.repository.CategoryRepository;
import de.aikiit.bilanzanalyser.entity.database.repository.PaymentRepository;
import de.aikiit.bilanzanalyser.entity.database.repository.ShopRepository;
import de.aikiit.bilanzanalyser.entity.database.repository.SourceRepository;
import de.aikiit.bilanzanalyser.reader.BilanzRowParserResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UploadAnalysisServiceTest {

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
    void getOrCreatePayment_shouldHandleConcurrentInsert() {
        when(paymentRepository.findByName("Card")).thenReturn(Optional.empty())  // first call
                .thenReturn(Optional.of(new PaymentEntity())); // retry

        when(paymentRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

        PaymentEntity result = service.getOrCreatePayment("Card");

        assertThat(result).isNotNull();
        verify(paymentRepository).findByName("Card");
    }

    @Test
    void replaceIfEmpty_shouldReplaceNullAndBlank() {
        assertThat(service.replaceIfEmpty(null)).isEqualTo("<empty>");
        assertThat(service.replaceIfEmpty(" ")).isEqualTo("<empty>");
        assertThat(service.replaceIfEmpty("   value  ")).isEqualTo("value");
    }

    @Test
    void flushDataIntoDatabase_shouldSaveRows() {
        BilanzRow row = mock(BilanzRow.class);
        when(row.getDate()).thenReturn(LocalDate.now());
        when(row.getAmount()).thenReturn(BigDecimal.TEN);
        when(row.getDescription()).thenReturn("desc");
        when(row.getShop()).thenReturn("shop");
        when(row.getPayment()).thenReturn("cash");
        when(row.getCategory()).thenReturn("food");
        when(row.getSource()).thenReturn("app");

        BilanzRowParserResult result = mock(BilanzRowParserResult.class);
        when(result.rows()).thenReturn(List.of(row));

        when(shopRepository.findByName(any())).thenReturn(Optional.of(new ShopEntity()));
        when(paymentRepository.findByName(any())).thenReturn(Optional.of(new PaymentEntity()));
        when(categoryRepository.findByName(any())).thenReturn(Optional.of(new CategoryEntity()));
        when(sourceRepository.findByName(any())).thenReturn(Optional.of(new SourceEntity()));

        service.flushDataIntoDatabase(result);

        verify(bilanzRowRepository).save(any(BilanzRowEntity.class));
    }
}