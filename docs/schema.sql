-- Weibao 情侣点餐系统核心表结构（MySQL 8.0+）
-- 字符集 UTF8MB4，按需调整存储引擎

CREATE TABLE IF NOT EXISTS user (
    id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    phone         VARCHAR(32) UNIQUE,
    open_id       VARCHAR(64),
    nickname      VARCHAR(64) NOT NULL,
    avatar        VARCHAR(255),
    gender        TINYINT DEFAULT 0,
    status        TINYINT DEFAULT 1,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户';

CREATE TABLE IF NOT EXISTS family (
    id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(64) NOT NULL,
    type          VARCHAR(16) NOT NULL COMMENT 'couple/family',
    owner_id      BIGINT UNSIGNED NOT NULL,
    wallet_id     BIGINT UNSIGNED,
    status        TINYINT DEFAULT 1,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_family_owner FOREIGN KEY (owner_id) REFERENCES user (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '家庭/情侣';

CREATE TABLE IF NOT EXISTS family_member (
    id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    family_id     BIGINT UNSIGNED NOT NULL,
    user_id       BIGINT UNSIGNED NOT NULL,
    role          VARCHAR(16) NOT NULL COMMENT 'owner/partner/child',
    nickname      VARCHAR(64),
    status        TINYINT DEFAULT 1,
    joined_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_family_member UNIQUE (family_id, user_id),
    CONSTRAINT fk_family_member_family FOREIGN KEY (family_id) REFERENCES family (id),
    CONSTRAINT fk_family_member_user FOREIGN KEY (user_id) REFERENCES user (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '家庭成员';

CREATE TABLE IF NOT EXISTS invite_code (
    id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    family_id     BIGINT UNSIGNED NOT NULL,
    code          VARCHAR(16) NOT NULL UNIQUE,
    type          VARCHAR(16) NOT NULL,
    creator_id    BIGINT UNSIGNED NOT NULL,
    expire_at     DATETIME,
    status        TINYINT DEFAULT 1,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_invite_family FOREIGN KEY (family_id) REFERENCES family (id),
    CONSTRAINT fk_invite_creator FOREIGN KEY (creator_id) REFERENCES user (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '家庭邀请码';

CREATE TABLE IF NOT EXISTS category (
    id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(64) NOT NULL,
    icon          VARCHAR(32),
    description   VARCHAR(255),
    sort_order    INT DEFAULT 0,
    visible       TINYINT DEFAULT 1,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '菜品分类';

CREATE TABLE IF NOT EXISTS dish (
    id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    category_id   BIGINT UNSIGNED NOT NULL,
    name          VARCHAR(64) NOT NULL,
    cover         VARCHAR(255),
    description   TEXT,
    price         DECIMAL(10,2) NOT NULL,
    rating        DECIMAL(3,1) DEFAULT 5.0,
    tags          JSON,
    spicy_level   TINYINT,
    status        TINYINT DEFAULT 1,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_dish_category FOREIGN KEY (category_id) REFERENCES category (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '菜品';

CREATE TABLE IF NOT EXISTS dish_media (
    id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    dish_id       BIGINT UNSIGNED NOT NULL,
    url           VARCHAR(255) NOT NULL,
    media_type    VARCHAR(16) NOT NULL COMMENT 'image/video',
    is_cover      TINYINT DEFAULT 0,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_dish_media FOREIGN KEY (dish_id) REFERENCES dish (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '菜品素材';

CREATE TABLE IF NOT EXISTS dish_review (
    id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    dish_id       BIGINT UNSIGNED NOT NULL,
    family_id     BIGINT UNSIGNED NOT NULL,
    user_id       BIGINT UNSIGNED NOT NULL,
    score         TINYINT NOT NULL,
    content       TEXT,
    images        JSON,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_review_dish FOREIGN KEY (dish_id) REFERENCES dish (id),
    CONSTRAINT fk_review_family FOREIGN KEY (family_id) REFERENCES family (id),
    CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES user (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '菜品评价';

CREATE TABLE IF NOT EXISTS cart_item (
    id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    family_id     BIGINT UNSIGNED NOT NULL,
    dish_id       BIGINT UNSIGNED NOT NULL,
    quantity      INT NOT NULL,
    note          VARCHAR(255),
    last_operator BIGINT UNSIGNED,
    updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_cart UNIQUE (family_id, dish_id),
    CONSTRAINT fk_cart_family FOREIGN KEY (family_id) REFERENCES family (id),
    CONSTRAINT fk_cart_dish FOREIGN KEY (dish_id) REFERENCES dish (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '家庭购物车';

CREATE TABLE IF NOT EXISTS orders (
    id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    family_id     BIGINT UNSIGNED NOT NULL,
    order_no      VARCHAR(32) NOT NULL UNIQUE,
    total_amount  DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) DEFAULT 0,
    coin_used     INT DEFAULT 0,
    pay_amount    DECIMAL(10,2) NOT NULL,
    status        VARCHAR(16) NOT NULL,
    delivery_type VARCHAR(16) DEFAULT 'home',
    address_id    BIGINT,
    remark        VARCHAR(255),
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_family FOREIGN KEY (family_id) REFERENCES family (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '订单';

CREATE TABLE IF NOT EXISTS order_item (
    id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    order_id      BIGINT UNSIGNED NOT NULL,
    dish_id       BIGINT UNSIGNED NOT NULL,
    name          VARCHAR(64) NOT NULL,
    price         DECIMAL(10,2) NOT NULL,
    quantity      INT NOT NULL,
    note          VARCHAR(255),
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES orders (id),
    CONSTRAINT fk_order_item_dish FOREIGN KEY (dish_id) REFERENCES dish (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '订单菜品';

CREATE TABLE IF NOT EXISTS coin_wallet (
    id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    family_id     BIGINT UNSIGNED NOT NULL UNIQUE,
    balance       INT NOT NULL DEFAULT 0,
    frozen        INT NOT NULL DEFAULT 0,
    version       INT NOT NULL DEFAULT 0,
    updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_wallet_family FOREIGN KEY (family_id) REFERENCES family (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '虚拟币钱包';

CREATE TABLE IF NOT EXISTS coin_transaction (
    id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    wallet_id     BIGINT UNSIGNED NOT NULL,
    type          VARCHAR(16) NOT NULL COMMENT 'reward/pay/refund',
    amount        INT NOT NULL,
    order_id      BIGINT UNSIGNED,
    task_code     VARCHAR(32),
    remark        VARCHAR(255),
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_coin_tx_wallet FOREIGN KEY (wallet_id) REFERENCES coin_wallet (id),
    CONSTRAINT fk_coin_tx_order FOREIGN KEY (order_id) REFERENCES orders (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '虚拟币流水';

CREATE TABLE IF NOT EXISTS coin_task (
    id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code          VARCHAR(32) NOT NULL UNIQUE,
    title         VARCHAR(64) NOT NULL,
    description   VARCHAR(255),
    reward        INT NOT NULL,
    limit_type    VARCHAR(16) NOT NULL COMMENT 'daily/once',
    status        TINYINT DEFAULT 1,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '虚拟币任务';

CREATE TABLE IF NOT EXISTS coin_reward_log (
    id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    wallet_id     BIGINT UNSIGNED NOT NULL,
    task_code     VARCHAR(32) NOT NULL,
    amount        INT NOT NULL,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_reward UNIQUE (wallet_id, task_code),
    CONSTRAINT fk_reward_wallet FOREIGN KEY (wallet_id) REFERENCES coin_wallet (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '任务领取记录';


