-- AI 学习系统表结构
-- 当前脚本独立于 ruoyi-vue-pro.sql，便于后续评审后合并到主初始化脚本
-- 或迁移到 Flyway / Liquibase 中统一管理。

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 表结构：ai_learning_plan
-- ----------------------------
DROP TABLE IF EXISTS `ai_learning_plan`;
CREATE TABLE `ai_learning_plan` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '学习计划编号',
  `user_id` bigint NOT NULL COMMENT '管理员用户编号',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '计划标题',
  `goal` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '学习目标',
  `goal_prompt` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '补充规划提示词',
  `source_summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'AI 生成的资料总结',
  `cycle_days` int NOT NULL DEFAULT 0 COMMENT '学习周期天数',
  `daily_study_minutes` int NOT NULL DEFAULT 0 COMMENT '每日学习时长（分钟）',
  `total_study_minutes` int NOT NULL DEFAULT 0 COMMENT '计划总学习时长（分钟）',
  `plan_status` tinyint NOT NULL DEFAULT 0 COMMENT '计划状态，见 AiLearningPlanStatusEnum',
  `current_phase_id` bigint NULL DEFAULT NULL COMMENT '当前进行中的阶段编号',
  `total_phase_count` int NOT NULL DEFAULT 0 COMMENT '总阶段数量',
  `overall_mastery` decimal(5,2) NOT NULL DEFAULT 0.00 COMMENT '整体掌握度（百分比）',
  `latest_score` decimal(5,2) NULL DEFAULT NULL COMMENT '最近一次考试得分',
  `average_score` decimal(5,2) NULL DEFAULT NULL COMMENT '考试平均分',
  `highest_score` decimal(5,2) NULL DEFAULT NULL COMMENT '历史最高分',
  `last_studied_time` datetime NULL DEFAULT NULL COMMENT '最近学习时间',
  `next_review_time` datetime NULL DEFAULT NULL COMMENT '下次复习时间',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_ai_learning_plan_user_status` (`user_id`, `plan_status`) USING BTREE,
  KEY `idx_ai_learning_plan_current_phase_id` (`current_phase_id`) USING BTREE,
  KEY `idx_ai_learning_plan_next_review_time` (`next_review_time`) USING BTREE
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 学习计划表';

-- ----------------------------
-- 表结构：ai_learning_phase
-- ----------------------------
DROP TABLE IF EXISTS `ai_learning_phase`;
CREATE TABLE `ai_learning_phase` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '学习阶段编号',
  `plan_id` bigint NOT NULL COMMENT '学习计划编号',
  `user_id` bigint NOT NULL COMMENT '管理员用户编号',
  `phase_index` int NOT NULL DEFAULT 1 COMMENT '阶段序号，从 1 开始',
  `phase_title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '阶段标题',
  `phase_goal` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '阶段目标',
  `phase_summary` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '阶段学习说明',
  `mastery_targets` json NULL COMMENT '阶段掌握指标 JSON',
  `sort` int NOT NULL DEFAULT 0 COMMENT '展示排序',
  `phase_status` tinyint NOT NULL DEFAULT 0 COMMENT '阶段状态，见 AiLearningPhaseStatusEnum',
  `start_time` datetime NULL DEFAULT NULL COMMENT '阶段实际开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '阶段实际结束时间',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_ai_learning_phase_plan_phase_index` (`plan_id`, `phase_index`) USING BTREE,
  KEY `idx_ai_learning_phase_plan_status` (`plan_id`, `phase_status`) USING BTREE
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 学习阶段表';

-- ----------------------------
-- 表结构：ai_learning_material
-- ----------------------------
DROP TABLE IF EXISTS `ai_learning_material`;
CREATE TABLE `ai_learning_material` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '学习资料编号',
  `phase_id` bigint NOT NULL COMMENT '学习阶段编号',
  `user_id` bigint NOT NULL COMMENT '管理员用户编号',
  `knowledge_id` bigint NULL DEFAULT NULL COMMENT '关联的 ai_knowledge 编号',
  `knowledge_document_id` bigint NULL DEFAULT NULL COMMENT '关联的 ai_knowledge_document 编号',
  `file_id` bigint NULL DEFAULT NULL COMMENT '关联的 infra 文件编号',
  `material_type` tinyint NOT NULL DEFAULT 0 COMMENT '资料类型，见 AiLearningMaterialTypeEnum',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '资料名称',
  `storage_path` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '存储路径或外部链接',
  `content_hash` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '内容哈希值',
  `original_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '原始输入内容',
  `parsed_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '解析后的资料内容',
  `sort` int NOT NULL DEFAULT 0 COMMENT '资料排序',
  `parse_status` tinyint NOT NULL DEFAULT 0 COMMENT '解析状态，见 AiLearningMaterialParseStatusEnum',
  `error_message` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '解析错误信息',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_ai_learning_material_phase_id` (`phase_id`) USING BTREE,
  KEY `idx_ai_learning_material_document_id` (`knowledge_document_id`) USING BTREE,
  KEY `idx_ai_learning_material_parse_status` (`parse_status`) USING BTREE
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 阶段学习资料表';

-- ----------------------------
-- 表结构：ai_learning_task
-- ----------------------------
DROP TABLE IF EXISTS `ai_learning_task`;
CREATE TABLE `ai_learning_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '学习任务编号',
  `plan_id` bigint NOT NULL COMMENT '学习计划编号',
  `phase_id` bigint NOT NULL COMMENT '学习阶段编号',
  `user_id` bigint NOT NULL COMMENT '管理员用户编号',
  `task_date` date NOT NULL COMMENT '任务日期',
  `start_time` datetime NULL DEFAULT NULL COMMENT '计划开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '计划结束时间',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '任务标题',
  `task_type` tinyint NOT NULL DEFAULT 0 COMMENT '任务类型，见 AiLearningTaskTypeEnum',
  `task_mode` tinyint NOT NULL DEFAULT 0 COMMENT '任务模式，见 AiLearningTaskModeEnum',
  `task_status` tinyint NOT NULL DEFAULT 0 COMMENT '任务状态，见 AiLearningTaskStatusEnum',
  `content_outline` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '任务学习大纲',
  `completion_rate` decimal(5,2) NOT NULL DEFAULT 0.00 COMMENT '任务完成率（百分比）',
  `actual_study_minutes` int NOT NULL DEFAULT 0 COMMENT '实际学习时长（分钟）',
  `exam_id` bigint NULL DEFAULT NULL COMMENT '关联考试编号',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_ai_learning_task_user_date_status` (`user_id`, `task_date`, `task_status`) USING BTREE,
  KEY `idx_ai_learning_task_plan_phase` (`plan_id`, `phase_id`) USING BTREE,
  KEY `idx_ai_learning_task_exam_id` (`exam_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 学习任务表';

-- ----------------------------
-- 表结构：ai_learning_exam
-- ----------------------------
DROP TABLE IF EXISTS `ai_learning_exam`;
CREATE TABLE `ai_learning_exam` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'AI 考试编号',
  `plan_id` bigint NOT NULL COMMENT '学习计划编号',
  `phase_id` bigint NOT NULL COMMENT '学习阶段编号',
  `task_id` bigint NULL DEFAULT NULL COMMENT '学习任务编号',
  `user_id` bigint NOT NULL COMMENT '管理员用户编号',
  `exam_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '考试名称',
  `wrong_question_mode` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否开启历史错题强化考核',
  `single_choice_count` int NOT NULL DEFAULT 0 COMMENT '单选题数量',
  `multiple_choice_count` int NOT NULL DEFAULT 0 COMMENT '多选题数量',
  `judge_count` int NOT NULL DEFAULT 0 COMMENT '判断题数量',
  `essay_count` int NOT NULL DEFAULT 0 COMMENT '问答题数量',
  `total_question_count` int NOT NULL DEFAULT 0 COMMENT '题目总数',
  `exam_status` tinyint NOT NULL DEFAULT 0 COMMENT '考试状态，见 AiLearningExamStatusEnum',
  `score` decimal(5,2) NULL DEFAULT NULL COMMENT '总分',
  `objective_score` decimal(5,2) NULL DEFAULT NULL COMMENT '客观题得分',
  `subjective_score` decimal(5,2) NULL DEFAULT NULL COMMENT '主观题得分',
  `average_correct_rate` decimal(5,2) NULL DEFAULT NULL COMMENT '平均正确率',
  `weak_points` json NULL COMMENT '薄弱知识点 JSON',
  `started_time` datetime NULL DEFAULT NULL COMMENT '开始答题时间',
  `submitted_time` datetime NULL DEFAULT NULL COMMENT '提交答卷时间',
  `graded_time` datetime NULL DEFAULT NULL COMMENT '评分完成时间',
  `duration_minutes` int NOT NULL DEFAULT 0 COMMENT '考试时长（分钟）',
  `error_message` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '异常错误信息',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_ai_learning_exam_user_status` (`user_id`, `exam_status`) USING BTREE,
  KEY `idx_ai_learning_exam_plan_phase` (`plan_id`, `phase_id`) USING BTREE,
  KEY `idx_ai_learning_exam_task_id` (`task_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 学习考试表';

-- ----------------------------
-- 表结构：ai_learning_exam_question
-- ----------------------------
DROP TABLE IF EXISTS `ai_learning_exam_question`;
CREATE TABLE `ai_learning_exam_question` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '考试题目编号',
  `exam_id` bigint NOT NULL COMMENT 'AI 考试编号',
  `plan_id` bigint NOT NULL COMMENT '学习计划编号',
  `phase_id` bigint NOT NULL COMMENT '学习阶段编号',
  `user_id` bigint NOT NULL COMMENT '管理员用户编号',
  `question_no` int NOT NULL DEFAULT 1 COMMENT '题目序号',
  `question_type` tinyint NOT NULL DEFAULT 0 COMMENT '题目类型，见 AiLearningQuestionTypeEnum',
  `stem` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '题干内容',
  `options_json` json NULL COMMENT '选项内容 JSON',
  `correct_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '标准答案',
  `answer_analysis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '答案解析',
  `knowledge_points` json NULL COMMENT '关联知识点 JSON',
  `user_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '用户作答内容',
  `is_correct` tinyint(1) NULL DEFAULT NULL COMMENT '是否答对',
  `score` decimal(5,2) NOT NULL DEFAULT 0.00 COMMENT '题目分值',
  `earned_score` decimal(5,2) NOT NULL DEFAULT 0.00 COMMENT '实际得分',
  `wrong_tag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否标记为错题',
  `needs_review` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否需要复习',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_ai_learning_exam_question_exam_no` (`exam_id`, `question_no`) USING BTREE,
  KEY `idx_ai_learning_exam_question_user_correct` (`user_id`, `is_correct`) USING BTREE,
  KEY `idx_ai_learning_exam_question_review` (`needs_review`, `wrong_tag`) USING BTREE
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 考试题目表';

-- ----------------------------
-- 表结构：ai_learning_review_item
-- ----------------------------
DROP TABLE IF EXISTS `ai_learning_review_item`;
CREATE TABLE `ai_learning_review_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '复习项编号',
  `user_id` bigint NOT NULL COMMENT '管理员用户编号',
  `plan_id` bigint NOT NULL COMMENT '学习计划编号',
  `phase_id` bigint NULL DEFAULT NULL COMMENT '学习阶段编号',
  `task_id` bigint NULL DEFAULT NULL COMMENT '学习任务编号',
  `exam_id` bigint NULL DEFAULT NULL COMMENT 'AI 考试编号',
  `question_id` bigint NULL DEFAULT NULL COMMENT '考试题目编号',
  `source_type` tinyint NOT NULL DEFAULT 0 COMMENT '复习来源类型，见 AiLearningTaskTypeEnum',
  `content_title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '复习内容标题',
  `review_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '复习内容',
  `review_status` tinyint NOT NULL DEFAULT 0 COMMENT '复习状态，见 AiLearningReviewStatusEnum',
  `review_count` int NOT NULL DEFAULT 0 COMMENT '累计复习次数',
  `ease_factor` decimal(4,2) NOT NULL DEFAULT 2.50 COMMENT '间隔重复难度系数',
  `interval_days` int NOT NULL DEFAULT 0 COMMENT '复习间隔天数',
  `next_review_time` datetime NULL DEFAULT NULL COMMENT '下次复习时间',
  `last_review_time` datetime NULL DEFAULT NULL COMMENT '最近复习时间',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_ai_learning_review_user_status_next` (`user_id`, `review_status`, `next_review_time`) USING BTREE,
  KEY `idx_ai_learning_review_plan_phase` (`plan_id`, `phase_id`) USING BTREE,
  KEY `idx_ai_learning_review_question_id` (`question_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 学习复习项表';

-- ----------------------------
-- 表结构：ai_learning_profile_snapshot
-- ----------------------------
DROP TABLE IF EXISTS `ai_learning_profile_snapshot`;
CREATE TABLE `ai_learning_profile_snapshot` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '画像快照编号',
  `user_id` bigint NOT NULL COMMENT '管理员用户编号',
  `plan_id` bigint NULL DEFAULT NULL COMMENT '学习计划编号',
  `snapshot_type` tinyint NOT NULL DEFAULT 0 COMMENT '快照类型，见 AiLearningProfileSnapshotTypeEnum',
  `snapshot_date` date NOT NULL COMMENT '快照日期',
  `strengths` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '优势总结',
  `weaknesses` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '薄弱点总结',
  `habits` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '学习习惯总结',
  `memory_points` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '长期记忆要点',
  `ai_summary` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'AI 生成总结',
  `metrics_json` json NULL COMMENT '分析指标 JSON',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_ai_learning_profile_user_type_date` (`user_id`, `snapshot_type`, `snapshot_date`) USING BTREE,
  KEY `idx_ai_learning_profile_plan_id` (`plan_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 学习画像快照表';

-- ----------------------------
-- 表结构：ai_learning_badge_record
-- ----------------------------
DROP TABLE IF EXISTS `ai_learning_badge_record`;
CREATE TABLE `ai_learning_badge_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '勋章记录编号',
  `user_id` bigint NOT NULL COMMENT '管理员用户编号',
  `plan_id` bigint NULL DEFAULT NULL COMMENT '学习计划编号',
  `badge_type` tinyint NOT NULL DEFAULT 0 COMMENT '勋章类型，见 AiLearningBadgeTypeEnum',
  `badge_status` tinyint NOT NULL DEFAULT 0 COMMENT '勋章状态，见 AiLearningBadgeStatusEnum',
  `progress_value` decimal(8,2) NOT NULL DEFAULT 0.00 COMMENT '当前进度值',
  `progress_target` decimal(8,2) NOT NULL DEFAULT 0.00 COMMENT '目标进度值',
  `unlocked_time` datetime NULL DEFAULT NULL COMMENT '解锁时间',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '勋章备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_ai_learning_badge_user_type` (`user_id`, `badge_type`) USING BTREE,
  KEY `idx_ai_learning_badge_status` (`badge_status`) USING BTREE,
  KEY `idx_ai_learning_badge_plan_id` (`plan_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 学习勋章记录表';

SET FOREIGN_KEY_CHECKS = 1;
