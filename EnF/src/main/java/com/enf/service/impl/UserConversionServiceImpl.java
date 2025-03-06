package com.enf.service.impl;

import com.enf.repository.querydsl.UserConversionQueryRepository;
import com.enf.repository.querydsl.UserConversionQueryRepository.BirdConversionRate;
import com.enf.repository.querydsl.UserConversionQueryRepository.MonthlyConversionRate;
import com.enf.repository.querydsl.UserConversionQueryRepository.ProviderConversionRate;
import com.enf.service.UserConversionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
