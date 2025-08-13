-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS bank_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE bank_system;

-- 创建用户账户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    owner VARCHAR(50) NOT NULL UNIQUE COMMENT '账户持有人',
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '账户余额',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户账户表';

-- 创建交易流水表
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    owner VARCHAR(50) NOT NULL COMMENT '操作账户',
    operation_type VARCHAR(20) NOT NULL COMMENT '操作类型：DEPOSIT/WITHDRAW/TRANSFER_OUT/TRANSFER_IN',
    amount DECIMAL(15,2) NOT NULL COMMENT '金额',
    the_other_owner VARCHAR(50) COMMENT '另一账户（转账时使用）',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '交易时间',
    INDEX idx_owner (owner),
    INDEX idx_created_time (created_time),
    INDEX idx_operation_type (operation_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易流水表';
