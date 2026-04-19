package de.aikiit.bilanzanalyser.upload;

import de.aikiit.bilanzanalyser.entity.database.repository.SourceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SourceServiceTest {

    @Mock
    private SourceRepository sourceRepository;

    @InjectMocks
    private SourceService sourceService;

    @Test
    void getSources_shouldReturnSortedNames() {
        // given
        List<String> mockResult = List.of("Alpha", "Beta", "Gamma");
        when(sourceRepository.findAllNamesOrdered()).thenReturn(mockResult);

        // when
        List<String> result = sourceService.getSources();

        // then
        assertEquals(mockResult, result);
        verify(sourceRepository).findAllNamesOrdered();
    }
}