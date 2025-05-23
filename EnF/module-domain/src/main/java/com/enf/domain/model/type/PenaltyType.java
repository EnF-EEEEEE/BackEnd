package com.enf.domain.model.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PenaltyType {
    FIRST(1),
    SECOND(2),
    THIRD(3),
    FOURTH(4);

    private final int penaltyCount;
}
