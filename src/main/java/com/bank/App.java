package com.bank;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import com.bank.model.Transaction;
import com.bank.model.User;
import com.bank.service.BankService;
import com.bank.service.impl.BankServiceImpl;
import com.bank.util.BankException;
import com.bank.util.DatabaseUtil;

/**
 * Hello JAVA World!
 *
 */
public class App 
{
    private static BankService bankService;
    private static Scanner scanner;
    private static final DateTimeFormatter DATE_FORMATTER=DateTimeFormatter.ofPattern("yyyy/MM/dd");

    public static void main(String[] args) {
        System.out.println("欢迎使用银行转账系统");
        System.out.println("版本：0.0.1 更新：2025/08/12");

        try {
            bankService=new BankServiceImpl();
            scanner= new Scanner(System.in);

            Connection testConnection=DatabaseUtil.getConnection();
            DatabaseUtil.closeConnection(testConnection);

            while(true) {
                System.out.print("\n>>>");
                String input=scanner.nextLine().trim();
                if(input.isEmpty()) continue;
                if(input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit"))
                {
                    break;
                }

                processCommand(input);
            }
        }catch(Exception e) {
            System.out.println("系统初始化失败"+e.getMessage());
            e.printStackTrace();
        }finally{
            if(scanner!=null) {
                scanner.close();
            }
        }
    }

    private static void processCommand(String input) {
        try {
            String[] parts=input.split("\\s+");
            String type=parts[0].toLowerCase();
            switch (type) {
                case "create":
                    handleCreateAccount(parts);
                    break;
                case "save":
                    handleDeposit(parts);
                    break;
                case "take":
                    handleWithdraw(parts);
                    break;
                case "trans":
                    handleTransfer(parts);  
                    break; 
                case "find":
                    handleFind(parts);   
                    break;   
                case "history":
                    handleHistory(parts);   
                    break;         
                default:
                    System.out.println("未知命令！");
            }
        }catch(Exception e) {
            System.out.println("命令执行失败！"+e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleCreateAccount(String[] parts) throws BankException,SQLException {
        if(parts.length!=3) {
            System.out.println("创建帐户命令格式错误！");
            return ;
        }

        String owner=parts[1];
        BigDecimal balance;

        try {
            balance=new BigDecimal(parts[2]).setScale(2,RoundingMode.HALF_UP);
        }catch(NumberFormatException e) {
            System.out.println("初始余额格式错误！");
            return;
        }

        bankService.createAccount(owner, balance);
    }

    private static void handleDeposit(String[] parts) throws BankException,SQLException{
        if(parts.length!=3) {
            System.out.println("存款命令格式错误！");
            return ;
        }
        String owner=parts[1];
        BigDecimal amount;

        try {
            amount=new BigDecimal(parts[2]).setScale(2,RoundingMode.HALF_UP);
        }catch (NumberFormatException e) {
            System.out.println("存款金额格式错误！");
            return ;
        }
        bankService.deposit(owner, amount);
    }

    private static void handleWithdraw(String[] parts) throws BankException,SQLException{
        if(parts.length!=3) {
            System.out.println("取款命令格式错误！");
            return ;
        }

        String owner=parts[1];
        BigDecimal amount;
        try {
            amount=new BigDecimal(parts[2]).setScale(2,RoundingMode.HALF_UP);
        }catch (NumberFormatException e) {
            System.out.println("取款金额格式错误！");
            return ;
        }

        bankService.withdraw(owner, amount);
    }

    private static void handleTransfer(String[] parts) throws BankException,SQLException{
        if(parts.length!=4) {
            System.out.println("转账命令格式错误！");
            return ;
        }
        String fromOwner=parts[1],toOwner=parts[2];
        BigDecimal amount;
        try {
            amount=new BigDecimal(parts[3]).setScale(2,RoundingMode.HALF_UP);
        }catch(NumberFormatException e) {
            System.out.println("转账金额格式错误！");
            return ;
        }

        bankService.transfer(fromOwner, toOwner, amount);
    }

    private static void handleFind(String[] parts) throws BankException,SQLException{
        if(parts.length==1) {
            List<User> users=bankService.getAllAccounts();
            if(users.isEmpty()) {
                System.out.println("暂无账户");
                return ;
            }
            System.out.printf("%-15s %-20s %-20s %-20s\n","Owner","Balance","Balance Updated Time","Created Time");
            
            for(User user: users) {
                System.out.printf("%-15s %-20s %-20s %-20s\n",
                    user.getOwner(),
                    user.getBalance(),
                    user.getUpdatedTime()!=null?user.getUpdatedTime():"NULL",
                    user.getCreatedTime()!=null?user.getCreatedTime():"NULL"
                );
            }
            
        }else if(parts.length==2) {
            String owner=parts[1];
            User user=bankService.getAccount(owner);
            if(user==null) {
                System.out.println("查不到该用户！");
                return;
            }
            System.out.printf("%-15s %-20s %-20s %-20s\n","Owner","Balance","Balance Updated Time","Created Time");
            System.out.printf("%-15s %-20s %-20s %-20s\n",
                user.getOwner(),
                user.getBalance(),
                user.getUpdatedTime()!=null?user.getUpdatedTime():"NULL",
                user.getCreatedTime()!=null?user.getCreatedTime():"NULL"
            );

        }else {
            System.out.println("查找用户命令格式错误！");
            return ;
        }
    }

    //动态参数查询交易记录
    private static void handleHistory(String[] parts) throws SQLException{
        String owner=null;
        LocalDate startDate=null;
        LocalDate endDate=null;
        String operationType=null;

        

        for(int i=1;i<parts.length;i++) {
            String param=parts[i];
            if(param.contains("-")) {
                if(startDate!=null) {
                    System.out.println("日期范围重复读入");
                    return ;
                }

                String[] dateRange=param.split("-");
                if(dateRange.length==2) {
                    try {
                        startDate=LocalDate.parse(dateRange[0],DATE_FORMATTER);
                        endDate=LocalDate.parse(dateRange[1],DATE_FORMATTER);
                    }catch(DateTimeParseException e) {
                        System.out.println("日期范围格式错误");
                        return ;
                    }
                }else {
                    System.out.println("日期范围格式错误");
                    return ;
                }
            }else if(param.matches("DEPOSIT|WITHDRAW|TRANSFER_OUT|TRANSFER_IN")) {
                if(operationType!=null) {
                    System.out.println("操作类型重复读入");
                    return ;
                }
                operationType=param;
            }else {
                //是用户名
                if(owner!=null) {
                    System.out.println("用户名重复读入");
                    return ;
                }
                owner=param;
            }
        }

        List<Transaction> transactions = bankService.getTransactionHistory(owner, startDate, endDate, operationType);
        if(transactions.isEmpty()) {
            System.out.println("未找到符合条件的交易记录");
            return ;
        }

        System.out.printf("%-15s %-15s %-20s %-15s %-20s \n", 
            "Owner","Operation Type", "Amount", "Interacted Owner", "Transaction Time");
        for(Transaction transaction : transactions) {
            System.out.printf("%-15s %-15s %-20s %-15s %-20s \n", 
            transaction.getOwner(),
            transaction.getOperationType(),
            transaction.getAmount(),
            transaction.getTheOtherOwner()!=null?transaction.getTheOtherOwner():"NULL",
            transaction.getCreatedTime()!=null?transaction.getCreatedTime():"NULL"
            );
        }
    }
}
