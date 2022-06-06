CREATE TABLE `articles` (
    `id` int NOT NULL,
    `title` varchar(255) NOT NULL,
    `content` text NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
CREATE TABLE `messages` (
    `messageid` bigint NOT NULL,
    `pageid` int NOT NULL,
    `user_prompt` varchar(255) NOT NULL,
    `current_index` int NOT NULL DEFAULT '0',
    `source` varchar(255) NOT NULL,
    PRIMARY KEY (`messageid`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
CREATE TABLE `user_prompts` (
    `id` int NOT NULL,
    `user_prompt` varchar(255) NOT NULL,
    `title` varchar(255) NOT NULL,
    `source` varchar(255) NOT NULL,
    PRIMARY KEY (`user_prompt`, `title`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci
