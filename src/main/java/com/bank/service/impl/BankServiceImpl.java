package com.bank.service.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import com.bank.dao.TransactionDao;
import com.bank.dao.UserDao;
import com.bank.dao.impl.TransactionDaoImpl;
import com.bank.dao.impl.TransactionDaoWithConnection;
import com.bank.dao.impl.UserDaoImpl;
import com.bank.dao.impl.UserDaoWithConnection;
import com.bank.model.Transaction;
import com.bank.model.User;
import com.bank.service.BankService;
import com.bank.util.BankException;
import com.bank.util.DatabaseUtil;

public class BankServiceImpl implements BankService{

    private final UserDao userDao;
    private final TransactionDao transactionDao;


    public BankServiceImpl() throws SQLException{
        this.userDao=new UserDaoImpl();
        this.transactionDao=new TransactionDaoImpl();
    }


    @Override
    public void createAccount(String owner, BigDecimal balance) throws BankException, SQLException {
        //先校验参数合法性
        if(owner==null || owner.trim().isEmpty()) {
            throw new BankException("用户名不能为空！");
        }
        if(owner.contains("-") || owner.matches("DEPOSIT|WITHDRAW|TRANSFER_OUT|TRANSFER_IN")) {
            throw new BankException("用户名不合法！");
        }
        if(balance==null || balance.compareTo(BigDecimal.ZERO)<0) {
            throw new BankException("初始账户余额不能为空且不能为负数！");
        }
        
        //如果账户已经存在，抛出bank异常
        if(userDao.findByOwner(owner)!=null) {
            throw new BankException("账户已经存在");
        }

        User user = new User(owner,balance);
        boolean success=userDao.insert(user);
        if(success==false) {
            throw new BankException("用户创建失败！");
        }
        System.out.println("用户创建成功！");
    }

    @Override
    public void deposit(String owner, BigDecimal amount) throws BankException, SQLException {
        //校验参数合法性
        if(owner==null || owner.trim().isEmpty()) {
            throw new BankException("用户不能为空！");
        }
        if(amount==null || amount.compareTo(BigDecimal.ZERO)<0) {
            throw new BankException("存款金额为空，或者存款金额是负数");
        }

        executeInTransaction(connection->{
            UserDao userDaoWithConn=new UserDaoWithConnection(connection);
            TransactionDao transactionDaoWithConn=new TransactionDaoWithConnection(connection);
            //查看用户是否存在
            User user = userDaoWithConn.findByOwner(owner);
            if(user==null) {
                throw new BankException("用户不存在！");
            }

            boolean success=userDaoWithConn.updateBalance(owner, user.getBalance().add(amount));
            if(success==false) {
                throw new BankException("存款失败！");
            }

            Transaction transaction=new Transaction(owner,"DEPOSIT",amount,null);
            boolean success2=transactionDaoWithConn.insert(transaction);
            if(success2==false) {
                throw new BankException("存款交易记录创建失败！");
            }

            System.out.println("存款成功！");
            return null;
        }
        );


    }

    @Override
    public User getAccount(String owner) throws BankException,SQLException {
        //校验参数
        if(owner==null || owner.trim().isEmpty()) {
            throw new BankException("用户名为空");
        }
        User user = userDao.findByOwner(owner);
        return user;
    }

    @Override
    public List<User> getAllAccounts() throws SQLException {
        List<User> all = userDao.findAll();
        return all;
    }

    @Override
    public List<Transaction> getTransactionHistory(String owner, LocalDate startDate, LocalDate endDate,
            String operationType) throws SQLException {
        List<Transaction> res = transactionDao.findByCondition(owner, startDate, endDate, operationType);
        return res;
    }

    @Override
    public void transfer(String fromOwner, String toOwner, BigDecimal amount) throws BankException, SQLException {
        //校验参数合法性
        if(fromOwner==null || fromOwner.trim().isEmpty()) {
            throw new BankException("转账方用户不能为空！");
        }
        if(toOwner==null || toOwner.trim().isEmpty()) {
            throw new BankException("被转账方用户不能为空！");
        }
        if(amount==null || amount.compareTo(BigDecimal.ZERO)<0) {
            throw new BankException("转账金额为空，或者存款金额是负数");
        }
        if(fromOwner.equals(toOwner)) {
            throw new BankException("不能向自己转账");
        }

        executeInTransaction(connection->{
            UserDao userDaoWithConn=new UserDaoWithConnection(connection);
            TransactionDao transactionDaoWithConn=new TransactionDaoWithConnection(connection);
            //查看用户是否存在
            User fromUser = userDaoWithConn.findByOwner(fromOwner);
            if(fromUser==null) {
                throw new BankException("转账方用户不存在！");
            }
            User toUser = userDaoWithConn.findByOwner(toOwner);
            if(toUser==null) {
                throw new BankException("被转账方用户不存在！");
            }
            //转账方余额是否充足
            if(amount.compareTo(fromUser.getBalance())>0) {
                throw new BankException("转账方余额不足！");
            }

            //从转账方扣款
            boolean success1=userDaoWithConn.updateBalance(fromOwner, fromUser.getBalance().subtract(amount));
            if(success1==false) {
                throw new BankException("转账方扣款失败！");
            }
            //给被转账方加款
            boolean success2=userDaoWithConn.updateBalance(toOwner, toUser.getBalance().add(amount));
            if(success2==false) {
                throw new BankException("被转账方加款失败！");
            }
            //写入转账方记录
            Transaction transaction=new Transaction(fromOwner,"TRANSFER_OUT",amount,toOwner);
            boolean success3=transactionDaoWithConn.insert(transaction);
            if(success3==false) {
                throw new BankException("转账方交易记录创建失败！");
            }
            //写入被转账方记录
            Transaction transaction2=new Transaction(toOwner,"TRANSFER_IN",amount,fromOwner);
            boolean success4=transactionDaoWithConn.insert(transaction2);
            if(success4==false) {
                throw new BankException("被转账方交易记录创建失败！");
            }

            System.out.println("转账成功！");
            return null;
        }
        );
        
    }

    @Override
    public void withdraw(String owner, BigDecimal amount) throws BankException, SQLException {
        //校验参数合法性
        if(owner==null || owner.trim().isEmpty()) {
            throw new BankException("用户不能为空！");
        }
        if(amount==null || amount.compareTo(BigDecimal.ZERO)<0) {
            throw new BankException("取款金额为空，或者取款金额是负数");
        }

        executeInTransaction(connection->{
            UserDao userDaoWithConn=new UserDaoWithConnection(connection);
            TransactionDao transactionDaoWithConn=new TransactionDaoWithConnection(connection);
            //查看用户是否存在
            User user = userDaoWithConn.findByOwner(owner);
            if(user==null) {
                throw new BankException("用户不存在！");
            }
            //取款金额吃否超过用户余额
            if(amount.compareTo(user.getBalance())>0) {
                throw new BankException("取款金额超过账户余额！");
            }

            boolean success=userDaoWithConn.updateBalance(owner, user.getBalance().subtract(amount));
            if(success==false) {
                throw new BankException("取款失败！");
            }

            Transaction transaction=new Transaction(owner,"WITHDRAW",amount,null);
            boolean success2=transactionDaoWithConn.insert(transaction);
            if(success2==false) {
                throw new BankException("取款交易记录创建失败！");
            }

            System.out.println("取款成功！");
            return null;
        }
        );
        
    }

    /**
     * 事务支持方法 - 在数据库事务中执行操作
     * 
     * 什么是事务？
     * 事务是一组数据库操作的集合，这些操作要么全部成功，要么全部失败。
     * 比如转账操作：从A账户扣钱 + 给B账户加钱，这两个操作必须同时成功或同时失败。
     * 
     * @param <T> 泛型参数，表示回调函数的返回类型
     * @param callback 回调函数，包含需要在事务中执行的具体业务逻辑
     * @return 返回回调函数执行的结果
     * @throws BankException 业务异常
     * @throws SQLException 数据库异常
     */
    private<T> T executeInTransaction(TransactionCallback<T> callback) throws BankException,SQLException
    {
        Connection connection=null;
        try{
            // 1. 获取数据库连接
            connection=DatabaseUtil.getConnection();
            
            // 2. 关闭自动提交模式，开启手动事务控制
            // 默认情况下，每条SQL语句执行后会自动提交
            // 设置为false后，需要手动调用commit()才会真正保存到数据库
            connection.setAutoCommit(false);

            // 3. 执行业务逻辑（通过回调函数）
            // 这里会执行具体的数据库操作，比如更新账户余额等
            T result=callback.execute(connection);
            
            // 4. 如果所有操作都成功，提交事务
            // commit()会将之前的所有操作真正保存到数据库
            connection.commit();
            return result;
        }catch(Exception e) {
            // 5. 如果发生任何异常，回滚事务
            if(connection!=null) {
                // rollback()会撤销之前在这个事务中的所有操作
                // 数据库会回到事务开始前的状态
                connection.rollback();
            }
            
            // 6. 重新抛出异常
            if(e instanceof BankException) {
                throw (BankException)e;  // 如果是业务异常，直接抛出
            }
            // 如果是其他异常，包装成业务异常抛出
            throw new BankException("操作失败，已撤销操作！",e);
        }finally {
            // 7. 清理资源（无论成功还是失败都会执行）
            if(connection!=null) {
                // 恢复自动提交模式，避免影响后续操作
                connection.setAutoCommit(true);
                // 关闭数据库连接，释放资源
                DatabaseUtil.closeConnection(connection);
            }
        }
    }

    /**
     * 事务回调接口
     * 
     * 这是一个函数式接口，用于定义在事务中要执行的具体操作。
     * 使用回调模式的好处：
     * 1. 将事务管理逻辑与业务逻辑分离
     * 2. 可以复用事务管理代码
     * 3. 确保事务的一致性处理
     * 
     * @param <T> 返回值类型
     */
    @FunctionalInterface
    private interface TransactionCallback<T> {
        /**
         * 在事务中执行的具体操作
         * @param connection 数据库连接对象
         * @return 执行结果
         * @throws Exception 可能抛出的异常
         */
        T execute(Connection connection) throws Exception;
    }
    
}
