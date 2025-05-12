package com.savitsky.bankingtransactions.job;

import com.savitsky.bankingtransactions.model.Account;
import com.savitsky.bankingtransactions.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.math.BigDecimal;
import java.util.List;

import static com.savitsky.bankingtransactions.utils.PaginationUtils.processInBatch;

@RequiredArgsConstructor
public class BalanceGrowthJob implements Job {

    private static final double MAX_INCREASING_COEFFICIENT = 2.07;
    private static final double INCREASING_COEFFICIENT = 1.10;
    private static final int BATCH_SIZE = 100;

    private final AccountRepository accountRepository;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        processInBatch(
                accountRepository::findAll,
                this::processAccountsBatch,
                BATCH_SIZE
        );
    }

    private void processAccountsBatch(final List<Account> accounts) {
        for (Account account : accounts) {
            var startBalance = account.getStartBalance();
            var currentBalance = account.getBalance();

            var maxBalance = startBalance.multiply(BigDecimal.valueOf(MAX_INCREASING_COEFFICIENT));

            if (currentBalance.compareTo(maxBalance) >= 0) {
                continue;
            }

            var increasedBalance = currentBalance.multiply(BigDecimal.valueOf(INCREASING_COEFFICIENT));

            if (increasedBalance.compareTo(maxBalance) > 0) {
                increasedBalance = maxBalance;
            }

            account.setBalance(increasedBalance);
        }
        accountRepository.saveAll(accounts);
    }
}
