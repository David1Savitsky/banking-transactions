package com.savitsky.bankingtransactions;

import org.springframework.boot.SpringApplication;

public class TestBankingTransactionsApplication {

    public static void main(String[] args) {
        SpringApplication.from(BankingTransactionsApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
