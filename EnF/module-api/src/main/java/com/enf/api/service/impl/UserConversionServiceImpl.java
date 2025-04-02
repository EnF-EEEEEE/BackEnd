package com.enf.api.service.impl;

import com.enf.domain.repository.querydsl.UserConversionQueryRepository;
import com.enf.domain.repository.querydsl.UserConversionQueryRepository.BirdConversionRate;
import com.enf.domain.repository.querydsl.UserConversionQueryRepository.MonthlyConversionRate;
import com.enf.domain.repository.querydsl.UserConversionQueryRepository.ProviderConversionRate;
import com.enf.api.service.UserConversionService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserConversionServiceImpl implements UserConversionService {

    private final UserConversionQueryRepository userConversionQueryRepository;

    @Override
    public double getOverallConversionRate() {
        return userConversionQueryRepository.calculateOverallConversionRate();
    }

    @Override
    public double getConversionRateForPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return userConversionQueryRepository.calculateConversionRateForPeriod(startDate, endDate);
    }

    @Override
    public List<ProviderConversionRate> getConversionRateByProvider() {
        return userConversionQueryRepository.calculateConversionRateByProvider();
    }

    @Override
    public List<BirdConversionRate> getConversionRateByBird() {
        return userConversionQueryRepository.calculateConversionRateByBird();
    }

    @Override
    public List<MonthlyConversionRate> getMonthlyConversionRates(int months) {
        return userConversionQueryRepository.calculateMonthlyConversionRates(months);
    }
}
