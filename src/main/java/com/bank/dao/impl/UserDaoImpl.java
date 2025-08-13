package com.bank.dao.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.bank.dao.UserDao;
import com.bank.model.User;
import com.bank.util.DatabaseUtil;

public class UserDaoImpl implements UserDao{


    @Override
    public List<User> findAll() throws SQLException {
        String sql="SELECT * FROM users ORDER BY created_time";
        List<User> users=new ArrayList<>();
        try (Connection connection = DatabaseUtil.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery()) {
        
            while (resultSet.next()) {
                users.add(mapResultSetToUser(resultSet));
            }
        }
        return users;
    }

    @Override
    public User findByOwner(String owner) throws SQLException {
        String sql="SELECT * FROM users WHERE owner=?";
        try (Connection connection=DatabaseUtil.getConnection();
            PreparedStatement statement=connection.prepareStatement(sql)
        ){
            statement.setString(1, owner);
            try(ResultSet resultSet=statement.executeQuery()) {
                if(resultSet.next()) {  //查到了owner
                    return mapResultSetToUser(resultSet);
                }
                return null;
            }
        }
    }

    @Override
    public boolean insert(User user) throws SQLException {
        String sql="INSERT INTO users (owner,balance) VALUES (?,?)";
        try (
            Connection connection=DatabaseUtil.getConnection();
            PreparedStatement statement=connection.prepareStatement(sql)
        ){
            statement.setString(1, user.getOwner());
            statement.setBigDecimal(2, user.getBalance());

            int result=statement.executeUpdate();
            return result>0;
        }
    }

    @Override
    public boolean updateBalance(String owner, BigDecimal newBalance) throws SQLException {
        String sql="UPDATE users SET balance=?, updated_time=CURRENT_TIMESTAMP WHERE owner=?";
        try (
            Connection connection=DatabaseUtil.getConnection();
            PreparedStatement statement=connection.prepareStatement(sql)
        ){
            statement.setBigDecimal(1, newBalance);
            statement.setString(2, owner);

            int result=statement.executeUpdate();
            return result>0;
        }
    }
    
    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        User user=new User();
        user.setId(resultSet.getLong("id"));
        user.setOwner(resultSet.getString("owner"));
        user.setBalance(resultSet.getBigDecimal("balance"));


        Timestamp createTimestamp=resultSet.getTimestamp("created_time");
        if(createTimestamp!=null) {
            user.setCreatedTime(createTimestamp.toLocalDateTime());
        }

        Timestamp updatedTimestamp=resultSet.getTimestamp("updated_time");
        if(updatedTimestamp!=null) {
            user.setUpdatedTime(updatedTimestamp.toLocalDateTime());
        }  
        
        return user;
    }
    
}
