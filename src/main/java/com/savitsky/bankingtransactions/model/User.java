package com.savitsky.bankingtransactions.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "\"user\"")
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_id_seq")
    private Long id;

    private String name;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    private String password;

    @OneToOne(mappedBy = "user")
    private Account account;

    @OneToMany(mappedBy = "user")
    private List<EmailData> emailData;

    @OneToMany(mappedBy = "user")
    private List<PhoneData> phoneData;
}
