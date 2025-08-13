package com.bank.dao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import com.bank.model.Transaction;

public interface TransactionDao {

    /**
     * 创建交易记录
     *  @param transaction交易记录
     * @return 是否成功
     */
    boolean insert(Transaction transaction) throws SQLException;

    
    /**
     * 根据：用户名。查找交易记录
     * @param owner 用户
     * @return  交易记录列表
     * @throws SQLException
     */
    //List<Transaction> findByOwner(String owner) throws SQLException;

    /**
     * 根据：用户名，交易时间范围。查找交易记录
     * @param owner 
     * @param sDate
     * @param eDate
     * @return
     * @throws SQLException
     */
    //List<Transaction> findByOwnerAndDateRange(String owner,LocalDate startDate,LocalDate endDate) throws SQLException;

    /**
     * 根据：用户名，交易类型。查找交易记录
     * @param owner
     * @param operationType
     * @return
     * @throws SQLException
     */
    //List<Transaction> findByOwnerAndOperationType(String owner,String operationType) throws SQLException;

    /**
     * 根据可选参数：用户名，交易时间范围，交易类型。动态查找交易记录
     * @param owner
     * @param starDate
     * @param endDate
     * @param operationType
     * @return
     * @throws SQLException
     */
    List<Transaction> findByCondition(String owner,LocalDate starDate,LocalDate endDate,String operationType) throws SQLException;

    /**
     * 查找所有交易记录
     * @return
     * @throws SQLException
     */
    //List<Transaction> findAll() throws SQLException;

}