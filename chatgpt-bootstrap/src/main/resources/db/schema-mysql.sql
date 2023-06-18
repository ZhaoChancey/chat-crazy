-- 创建 database
CREATE DATABASE IF NOT EXISTS chat DEFAULT CHARACTER SET utf8;

-- 进入 chat 库
USE chat;

-- 聊天室表
CREATE TABLE IF NOT EXISTS chat_room (
    id bigint NOT NULL COMMENT '主键',
    user_id int NOT NULL COMMENT '用户 id，user_type=0时表示虚拟用户的主键',
    user_type tinyint NOT NULL COMMENT '用户类型，0：未登录用户，1：已登录用户',
    ip VARCHAR(255) DEFAULT NULL COMMENT 'ip',
    conversation_id VARCHAR(255) DEFAULT NULL  COMMENT '对话 id，唯一',
    first_chat_message_id BIGINT DEFAULT NULL COMMENT '第一条消息主键',
    first_message_id varchar(255) DEFAULT NULL COMMENT '第一条消息',
    title VARCHAR(255) NOT NULL COMMENT '对话标题，从第一条消息截取',
    status tinyint NOT NULL COMMENT '聊天室状态，0：删除，1：正常存在',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT  '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `idx_first_chat_message_id`(`first_chat_message_id`),
    UNIQUE INDEX `idx_first_message_id`(`first_message_id`),
    UNIQUE INDEX `idx_conversation_id`(`conversation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='聊天室表';

-- 聊天消息表
CREATE TABLE IF NOT EXISTS chat_message (
    id BIGINT NOT NULL COMMENT '主键',
    user_id int NOT NULL COMMENT '用户 id，user_type=0时表示虚拟用户的主键',
    user_type tinyint NOT NULL COMMENT '用户类型，0：未登录用户，1：已登录用户',
    message_id VARCHAR(255) NOT NULL COMMENT '消息 id',
    parent_message_id VARCHAR(255) COMMENT '父级消息 id',
    parent_answer_message_id VARCHAR(255) COMMENT '父级回答消息 id',
    parent_question_message_id VARCHAR(255) COMMENT '父级问题消息 id',
    context_count BIGINT NOT NULL COMMENT '上下文数量',
    question_context_count BIGINT NOT NULL COMMENT '问题上下文数量',
    message_type INTEGER NOT NULL COMMENT '消息类型枚举',
    chat_room_id BIGINT NOT NULL COMMENT '聊天室 id',
    conversation_id VARCHAR(255) NULL COMMENT '对话 id',
    model_name VARCHAR(50) DEFAULT NULL COMMENT '模型名称',
    content VARCHAR(5000) NOT NULL COMMENT '消息内容',
    original_data TEXT COMMENT '消息的原始请求或响应数据',
    response_error_data TEXT COMMENT '错误的响应数据',
    prompt_tokens int COMMENT '输入消息的 tokens',
    completion_tokens int COMMENT '输出消息的 tokens',
    total_tokens int COMMENT '累计 Tokens',
    ip VARCHAR(255) NULL COMMENT 'ip',
    status INTEGER NOT NULL COMMENT '聊天记录状态',
    is_hide TINYINT NOT NULL DEFAULT 0 COMMENT '是否隐藏 0 否 1 是',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `idx_message_id`(`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT = Dynamic COMMENT='聊天消息表';

-- 敏感词表
CREATE TABLE IF NOT EXISTS sensitive_word (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    word VARCHAR(255) NOT NULL COMMENT '敏感词内容',
    status INTEGER NOT NULL COMMENT '状态 1 启用 2 停用',
    is_deleted INTEGER DEFAULT 0 COMMENT '是否删除 0 否 NULL 是',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='敏感词表';

-- 虚拟id表-体验专用
CREATE TABLE `virtual_user` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
    `vir_id` char(32) NOT NULL COMMENT '虚拟id',
    `status` int(11) NOT NULL COMMENT '状态 1 启用 0 停用',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_vir_id` (`vir_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COMMENT='虚拟id表-体验专用'

-- 用户表
CREATE TABLE IF NOT EXISTS user_info (
    id int NOT NULL AUTO_INCREMENT COMMENT '主键',
    phone CHAR(32) NOT NULL COMMENT '手机号',
    status INTEGER NOT NULL COMMENT '状态 1 启用 0 停用',
    nick_name VARCHAR(32) DEFAULT '小柴' COMMENT '昵称',
    start_time DATETIME NOT NULL  COMMENT '生效日期',
    end_time DATETIME NOT NULL  COMMENT '失效日期',
    vip_start_time DATETIME COMMENT 'vip-生效日期',
    vip_end_time DATETIME COMMENT 'vip-失效日期',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY idx_phone(phone)
    ) ENGINE=InnoDB AUTO_INCREMENT=2000 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 用户登录信息表
CREATE TABLE IF NOT EXISTS user_active_info (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id int NOT NULL COMMENT '手机号',
    ip VARCHAR(255) DEFAULT '' COMMENT 'IP地址',
    active_type INTEGER NOT NULL COMMENT '0: 登录，1：退出登录',
    active_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录/退出时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户登录信息表';

-- 订单表
CREATE TABLE IF NOT EXISTS chat_order (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id int NOT NULL COMMENT '用户 id，user_type=0时表示虚拟用户的主键',
    user_type tinyint NOT NULL COMMENT '用户类型，0：未登录用户，1：已登录用户',
    payment_type tinyint NOT NULL COMMENT '渠道类型，1：支付宝，2：微信', 
    order_id VARCHAR(255) NOT NULL COMMENT '订单id',
    transaction_id VARCHAR(255) NOT NULL COMMENT '渠道订单id',
    package_id tinyint NOT NULL COMMENT '套餐类型',
    amount decimal(5, 2) UNSIGNED NOT NULL COMMENT '金额',
    order_status int NOT NULL COMMENT '订单状态',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `idx_type_order_id`(`payment_type`, `order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT = Dynamic COMMENT='订单表';

-- 机器id表
CREATE TABLE IF NOT EXISTS chat_distribute (
    id int NOT NULL AUTO_INCREMENT COMMENT '主键',
    port CHAR(4) NOT NULL COMMENT '端口号',
    worker_id int NOT NULL COMMENT '机器id',
    datacenter_id int(4) NOT NULL COMMENT '数据中心id',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `idx_port`(`port`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT = Dynamic COMMENT='分布式id';
insert into chat_distribute(port, worker_id, datacenter_id) values("3002", 0, 0), ("3003", 1, 0), ("3004", 2, 0);