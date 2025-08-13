ALTER TABLE transactions
ADD COLUMN the_other_owner VARCHAR(50) COMMENT '另一账户（转账时使用）';
