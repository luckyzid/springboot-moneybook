USE db_local;

CREATE TABLE IF NOT EXISTS mb_user
(
    idx           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_key      VARCHAR(100)    NOT NULL COMMENT '사용자 키',
    name          VARCHAR(30)              DEFAULT NULL COMMENT '이름',
    email         VARCHAR(255)             DEFAULT NULL COMMENT '이메일',
    password      VARCHAR(255)             DEFAULT NULL COMMENT '비밀번호',
    profile       TEXT                     DEFAULT NULL COMMENT '프로필',
    provider      VARCHAR(50)     NOT NULL COMMENT '프로바이더',
    unique_key    VARCHAR(100)    NOT NULL COMMENT '고유 키',
    role          VARCHAR(20)     NOT NULL DEFAULT 'USER' COMMENT '역할 (USER, ADMIN 등)',
    is_blocked    CHAR(1)         NOT NULL DEFAULT 'N' COMMENT '차단 여부 (Y/N)',
    register_user VARCHAR(100)    NOT NULL COMMENT '등록한 사용자',
    register_date DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    update_user   VARCHAR(100)    NOT NULL COMMENT '수정한 사용자',
    update_date   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (idx),
    UNIQUE KEY idx_mb_user_unique (provider, unique_key),
    UNIQUE KEY idx_mb_user_unique_key (user_key),
    KEY idx_mb_user_provider_email (provider, email),
    KEY idx_mb_user_update_date (update_date)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS mb_user_token
(
    idx             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_key        VARCHAR(100)    NOT NULL COMMENT '사용자 키 (User 참조)',
    token           VARCHAR(512)    NOT NULL COMMENT '토큰',
    is_active       TINYINT(1)      NOT NULL DEFAULT 1 COMMENT '활성 여부 (1: 활성, 0: 비활성)',
    device_info     TEXT                     DEFAULT NULL COMMENT '디바이스 정보',
    ip_address      VARCHAR(45)              DEFAULT NULL COMMENT 'IP 주소',
    expiration_time DATETIME        NOT NULL COMMENT '만료 시간',
    register_user   VARCHAR(100)    NOT NULL COMMENT '등록한 사용자',
    register_date   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    update_user     VARCHAR(100)    NOT NULL COMMENT '수정한 사용자',
    update_date     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (idx),
    FOREIGN KEY (user_key) REFERENCES mb_user (user_key) ON DELETE CASCADE,
    KEY idx_mb_user_token_user_key (user_key),
    KEY idx_mb_user_token_token (token),
    KEY idx_mb_user_token_user_key_is_active (user_key, is_active),
    KEY idx_mb_user_token_update_date (update_date)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS mb_account
(
    idx           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_key      VARCHAR(100)    NOT NULL COMMENT '사용자 키',
    name          VARCHAR(50)     NOT NULL COMMENT '계좌 이름',
    type          VARCHAR(20)     NOT NULL COMMENT '계좌 타입 (BANK, CARD 등)',
    register_user VARCHAR(100)    NOT NULL COMMENT '등록한 사용자',
    register_date DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    update_user   VARCHAR(100)    NOT NULL COMMENT '수정한 사용자',
    update_date   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (idx),
    UNIQUE KEY idx_mb_account_unique (user_key, name),
    FOREIGN KEY (user_key) REFERENCES mb_user (user_key) ON DELETE CASCADE,
    KEY idx_mb_account_user_key (user_key),
    KEY idx_mb_account_update_date (update_date)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS mb_category
(
    idx           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_key      VARCHAR(100)    NOT NULL COMMENT '사용자 키',
    name          VARCHAR(50)     NOT NULL COMMENT '카테고리 이름',
    parent_idx    BIGINT UNSIGNED          DEFAULT NULL COMMENT '부모 카테고리 인덱스',
    depth         INT             NOT NULL DEFAULT 0 COMMENT '카테고리 깊이',
    register_user VARCHAR(100)    NOT NULL COMMENT '등록한 사용자',
    register_date DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    update_user   VARCHAR(100)    NOT NULL COMMENT '수정한 사용자',
    update_date   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (idx),
    UNIQUE KEY idx_mb_category_unique (user_key, name),
    FOREIGN KEY (user_key) REFERENCES mb_user (user_key) ON DELETE CASCADE,
    FOREIGN KEY (parent_idx) REFERENCES mb_category (idx) ON DELETE SET NULL,
    KEY idx_mb_category_user_key_depth (user_key, depth),
    KEY idx_mb_category_parent_idx (parent_idx),
    KEY idx_mb_category_user_key (user_key),
    KEY idx_mb_category_update_date (update_date)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS mb_budget
(
    idx              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_key         VARCHAR(100)    NOT NULL COMMENT '사용자 키',
    account_idx      BIGINT UNSIGNED NOT NULL COMMENT '계좌 인덱스',
    category_idx     BIGINT UNSIGNED NOT NULL COMMENT '카테고리 인덱스',
    type             VARCHAR(10)     NOT NULL COMMENT '예산 타입 (INCOME, EXPENSE 등)',
    amount           DECIMAL(18, 2)  NOT NULL COMMENT '금액',
    comment          TEXT                     DEFAULT NULL COMMENT '코멘트',
    transaction_date DATETIME        NOT NULL COMMENT '거래 날짜',
    register_user    VARCHAR(100)    NOT NULL COMMENT '등록한 사용자',
    register_date    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    update_user      VARCHAR(100)    NOT NULL COMMENT '수정한 사용자',
    update_date      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (idx),
    FOREIGN KEY (user_key) REFERENCES mb_user (user_key) ON DELETE CASCADE,
    FOREIGN KEY (account_idx) REFERENCES mb_account (idx) ON DELETE CASCADE,
    FOREIGN KEY (category_idx) REFERENCES mb_category (idx) ON DELETE CASCADE,
    KEY idx_mb_budget_user_key (user_key),
    KEY idx_mb_budget_transaction_date (transaction_date),
    KEY idx_mb_budget_account_idx (account_idx),
    KEY idx_mb_budget_category_idx (category_idx),
    KEY idx_mb_budget_update_date (update_date)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS mb_budget_category
(
    idx              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_key         VARCHAR(100)    NOT NULL COMMENT '사용자 키',
    account_idx      BIGINT UNSIGNED NOT NULL COMMENT '계좌 인덱스',
    category_idx     BIGINT UNSIGNED NOT NULL COMMENT '카테고리 인덱스',
    transaction_date DATE            NOT NULL COMMENT '거래 날짜',
    amount           DECIMAL(18, 2)  NOT NULL COMMENT '금액',
    income           DECIMAL(18, 2)  NOT NULL COMMENT '수입',
    expense          DECIMAL(18, 2)  NOT NULL COMMENT '지출',
    register_user    VARCHAR(100)    NOT NULL COMMENT '등록한 사용자',
    register_date    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    update_user      VARCHAR(100)    NOT NULL COMMENT '수정한 사용자',
    update_date      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (idx),
    UNIQUE KEY idx_mb_budget_category_unique (user_key, account_idx, category_idx, transaction_date),
    FOREIGN KEY (user_key) REFERENCES mb_user (user_key) ON DELETE CASCADE,
    FOREIGN KEY (account_idx) REFERENCES mb_account (idx) ON DELETE CASCADE,
    FOREIGN KEY (category_idx) REFERENCES mb_category (idx) ON DELETE CASCADE,
    KEY idx_mb_budget_category_user_key (user_key),
    KEY idx_mb_budget_category_account_idx (account_idx),
    KEY idx_mb_budget_category_category_idx (category_idx),
    KEY idx_mb_budget_category_transaction_date (transaction_date),
    KEY idx_mb_budget_category_update_date (update_date)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS mb_budget_account
(
    idx              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_key         VARCHAR(100)    NOT NULL COMMENT '사용자 키',
    account_idx      BIGINT UNSIGNED NOT NULL COMMENT '계좌 인덱스',
    transaction_date DATE            NOT NULL COMMENT '거래 날짜',
    amount           DECIMAL(18, 2)  NOT NULL COMMENT '금액',
    income           DECIMAL(18, 2)  NOT NULL COMMENT '수입',
    expense          DECIMAL(18, 2)  NOT NULL COMMENT '지출',
    register_user    VARCHAR(100)    NOT NULL COMMENT '등록한 사용자',
    register_date    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    update_user      VARCHAR(100)    NOT NULL COMMENT '수정한 사용자',
    update_date      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (idx),
    UNIQUE KEY idx_mb_budget_account_unique (user_key, account_idx, transaction_date),
    FOREIGN KEY (user_key) REFERENCES mb_user (user_key) ON DELETE CASCADE,
    FOREIGN KEY (account_idx) REFERENCES mb_account (idx) ON DELETE CASCADE,
    KEY idx_mb_budget_account_user_key (user_key),
    KEY idx_mb_budget_account_account_idx (account_idx),
    KEY idx_mb_budget_account_transaction_date (transaction_date),
    KEY idx_mb_budget_account_update_date (update_date)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS ab_test_event
(
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'PK',
    experiment_key  VARCHAR(100)    NOT NULL COMMENT '실험 키',
    variant         VARCHAR(50)     NOT NULL COMMENT '배정된 Variant',
    user_id         BIGINT UNSIGNED NOT NULL COMMENT '사용자 ID',
    condition_name  VARCHAR(150)    NOT NULL COMMENT 'Condition 클래스명',
    allocator_name  VARCHAR(150)    NOT NULL COMMENT 'Allocator 클래스명',
    attributes_json TEXT                     DEFAULT NULL COMMENT '사용자 속성 JSON',
    assigned_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '배정 시점',
    PRIMARY KEY (id),
    KEY idx_ab_test_event_experiment_key (experiment_key),
    KEY idx_ab_test_event_user_id (user_id),
    KEY idx_ab_test_event_assigned_at (assigned_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;
