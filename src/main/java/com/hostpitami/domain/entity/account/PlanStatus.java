package com.hostpitami.domain.entity.account;

public enum PlanStatus {
    NONE,      // appena registrato: non ha scelto piano
    TRIAL,     // trial attivo
    ACTIVE,    // piano attivo
    PAST_DUE,  // futuro: pagamento fallito
    CANCELLED  // futuro: disdetto
}
