package com.bank.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import com.bank.model.Transaction;
import com.bank.model.User;
import com.bank.util.BankException;

//银行业务逻辑的接口
public interface BankService {
    /**
     * 创建账户
     * @param owner 用户名称
     * @param balance 用户初始余额
     * @throws BankException
     * @throws SQLException
     */
    void createAccount(String owner,BigDecimal balance) throws BankException,SQLException;

    /**
     * 进行存款业务
     * @param owner 用户名称
     * @param amount 存款金额
     * @throws BankException
     * @throws SQLException
     */
    void deposit(String owner,BigDecimal amount) throws BankException,SQLException;

    /**
     * 进行取款业务
     * @param owner 用户名称
     * @param amount 取款金额
     * @throws BankException
     * @throws SQLException
     */
    void withdraw(String owner,BigDecimal amount) throws BankException,SQLException;

    /**
     * 转账
     * @param fromOwner 
     * @param toOwner
     * @param amount
     * @throws BankException
     * @throws SQLException
     */
    void transfer(String fromOwner,String toOwner,BigDecimal amount) throws BankException,SQLException;

    /**
     * 获取某个用户
     * @param owner 用户名
     * @return 
     * @throws BankException
     * @throws SQLException
     */
    User getAccount(String owner) throws BankException,SQLException;

    /**
     * 获取所有用户
     * @return
     * @throws BankException
     * @throws SQLException
     */
    List<User> getAllAccounts() throws SQLException;

    /**
     * 获取交易历史记录（可变参）
     * @param owner
     * @param startDate
     * @param endDate
     * @param operationType
     * @return
     * @throws SQLException
     */
    List<Transaction> getTransactionHistory(String owner,LocalDate startDate,LocalDate endDate,String operationType) throws SQLException;



}
