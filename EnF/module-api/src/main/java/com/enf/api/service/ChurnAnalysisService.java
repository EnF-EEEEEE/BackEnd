package com.enf.api.service;

import java.time.LocalDate;
import java.util.List;

public interface ChurnAnalysisService {
    List<Integer> getMenteesWithLetterWithin24Hours(LocalDate startDate, int weekCount);
    int countMenteesWithLetterWithin24Hours(LocalDate startDate, LocalDate endDate);
    List<Integer> getDefaultWeeklyMenteeCounts();
}
