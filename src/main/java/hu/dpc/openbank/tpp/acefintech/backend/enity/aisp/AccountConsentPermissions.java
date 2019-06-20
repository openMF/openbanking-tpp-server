package hu.dpc.openbank.tpp.acefintech.backend.enity.aisp;

import java.util.Arrays;
import java.util.List;

public class AccountConsentPermissions {
    public static final List<String> PERMISSIONS = Arrays.asList("ReadAccountsDetail",
            "ReadBalances",
            "ReadBeneficiariesDetail",
            "ReadDirectDebits",
            "ReadProducts",
            "ReadStandingOrdersDetail",
            "ReadTransactionsCredits",
            "ReadTransactionsDebits",
            "ReadTransactionsDetail",
            "ReadOffers",
            "ReadPAN",
            "ReadParty",
            "ReadPartyPSU",
            "ReadScheduledPaymentsDetail",
            "ReadStatementsDetail");

}
