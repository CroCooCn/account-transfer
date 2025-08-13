package com.bank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    private Long id;        //数据库中的主键id
    private String owner;
    //操作类型：DEPOSIT/WITHDRAW/TRANSFER_OUT/TRANSFER_IN
    private String operationType;
    private BigDecimal amount;
    private String theOtherOwner;
    private LocalDateTime createdTime;

    public Transaction(String owner,String operationType,BigDecimal amount,String theOtherOwner) {
        this.owner=owner;
        this.operationType=operationType;
        this.amount=amount;
        this.theOtherOwner=theOtherOwner;
    }

}
