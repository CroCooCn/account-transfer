package com.bank.dao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import com.bank.model.User;

public interface UserDao {

    //我们写一下参数说明吧
    /**
     * 功能：创建用户
     * @param user 用户信息
     * @return boolean-是否成功
     * @throws SQLException SQL异常
     */
    boolean insert(User user) throws SQLException;

    /**
     * 功能：查找用户
     * @param owner 用户名
     * @return User-找到的用户
     * @throws SQLException SQL异常
     */
    User findByOwner(String owner) throws SQLException;

    /**
     * 功能：更新用户余额
     * @param owner 用户名
     * @param newBalance 更新后余额
     * @return boolean-是否成功
     * @throws SQLException SQL异常
     */
    boolean updateBalance(String owner,BigDecimal newBalance) throws SQLException;

    /**
     * 功能：查找所有用户
     * @return 用户的列表
     * @throws SQLException SQL异常
     */
    List<User> findAll() throws SQLException;
} 
