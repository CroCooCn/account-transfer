package com.bank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;        //数据库中的主键id
    private String owner;
    private BigDecimal balance;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    public User(String owner,BigDecimal balance) {
        this.owner=owner;
        this.balance=balance;
    }
}
