package com.bank.dao.impl;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.bank.dao.TransactionDao;
import com.bank.model.Transaction;

public class TransactionDaoWithConnection implements TransactionDao{

    private Connection connection;
    public TransactionDaoWithConnection(Connection connection) {
        this.connection=connection;
    }


    /*@Override
    public List<Transaction> findAll() throws SQLException {
        String sql="SELECT * FROM transactions ORDER BY created_time DESC";
        try(Connection connection=DatabaseUtil.getConnection();
            PreparedStatement statement=connection.prepareStatement(sql);
            ResultSet resultSet=statement.executeQuery()) {
            
            return mapResultSetToTransactionList(resultSet);
        }
        
    }*/

    @Override
    public List<Transaction> findByCondition(String owner, LocalDate startDate, LocalDate endDate, String operationType)
            throws SQLException {
        StringBuilder sql=new StringBuilder("SELECT * FROM transactions WHERE 1=1 ");
        List<Object> paras=new ArrayList<>();

        if(owner!=null && !owner.trim().isEmpty()) {
            sql.append("AND owner=?");
            paras.add(owner);
        }

        if(startDate!=null && endDate!=null) {
            sql.append(" AND DATE(created_time) BETWEEN ? AND ? ");
            paras.add(Date.valueOf(startDate));
            paras.add(Date.valueOf(endDate));
        }

        if(operationType!=null && !operationType.trim().isEmpty()) {
            sql.append(" AND operation_type=? ");
            paras.add(operationType);
        }

        sql.append(" ORDER BY created_time DESC ");


        try(
            PreparedStatement statement=connection.prepareStatement(sql.toString())) 
        {
            //传入参数
            for(int i=0;i<paras.size();i++) {
                statement.setObject(i+1, paras.get(i));
            }
            try(ResultSet resultSet=statement.executeQuery()) {
                return mapResultSetToTransactionList(resultSet);
            }
        }
    }

   /*  @Override
    public List<Transaction> findByOwner(String owner) throws SQLException {
        String sql="SELECT * FROM transactions WHERE owner = ? ORDER BY created_time DESC";
        try(Connection connection=DatabaseUtil.getConnection();
            PreparedStatement statement=connection.prepareStatement(sql)) {
            statement.setString(1, owner);

            //写try，不论是否抛出异常，resultSet会自动关闭
            try(ResultSet resultSet=statement.executeQuery()) {
                return mapResultSetToTransactionList(resultSet);
            }
        }
    }*/

    /*@Override
    public List<Transaction> findByOwnerAndDateRange(String owner, LocalDate startDate, LocalDate endDate)
            throws SQLException {
        
        return null;
    }*/

    /*@Override
    public List<Transaction> findByOwnerAndOperationType(String owner, String operationType) throws SQLException {
        
        return null;
    }*/

    @Override
    public boolean insert(Transaction transaction) throws SQLException {
        String sql="INSERT INTO transactions (owner,operation_type,amount,the_other_owner) VALUES (?,?,?,?)";
        //让connection和statement自动关闭
        try(
            PreparedStatement statement=connection.prepareStatement(sql)) {
            statement.setString(1, transaction.getOwner());
            statement.setString(2, transaction.getOperationType());
            statement.setBigDecimal(3, transaction.getAmount());
            statement.setString(4, transaction.getTheOtherOwner());

            int result=statement.executeUpdate();
            return result>0;
        }


    }

    private List<Transaction> mapResultSetToTransactionList(ResultSet resultSet) throws SQLException{
        List<Transaction> transactions=new ArrayList<>();
        while(resultSet.next()) {
            Transaction transaction=new Transaction();
            transaction.setId(resultSet.getLong("id"));
            transaction.setOwner(resultSet.getString("owner"));
            transaction.setAmount(resultSet.getBigDecimal("amount"));
            transaction.setOperationType(resultSet.getString("operation_type"));

            Timestamp createTimestamp=resultSet.getTimestamp("created_time");
            if(createTimestamp!=null) {
                transaction.setCreatedTime(createTimestamp.toLocalDateTime());
            }

            String theOtherOwner=resultSet.getString("the_other_owner");
            if(theOtherOwner!=null && !theOtherOwner.trim().isEmpty()) {
                transaction.setTheOtherOwner(theOtherOwner);
            }

            transactions.add(transaction);
                        
        }
        return transactions;
    }

   

    
    
}
