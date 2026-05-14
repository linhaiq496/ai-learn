-- AI Learning OS schema
-- This script is kept separate from ruoyi-vue-pro.sql so it can be reviewed
-- and merged into the main initializer or a Flyway/Liquibase migration later.

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ai_learning_plan
-- ----------------------------
DROP TABLE IF EXISTS `ai_learning_plan`;
CREATE TABLE `ai_learning_plan` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Plan ID',
  `user_id` bigint NOT NULL COMMENT 'Admin user ID',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'Plan title',
  `goal` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'Learning goal',
  `goal_prompt` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Extra planning prompt',
  `source_summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'AI summary of source materials',
  `cycle_days` int NOT NULL DEFAULT 0 COMMENT 'Learning cycle in days',
  `daily_study_minutes` int NOT NULL DEFAULT 0 COMMENT 'Daily study minutes',
  `total_study_minutes` int NOT NULL DEFAULT 0 COMMENT 'Total planned study minutes',
  `plan_status` tinyint NOT NULL DEFAULT 0 COMMENT 'Plan status, enum: AiLearningPlanStatusEnum',
  `current_phase_id` bigint NULL DEFAULT NULL COMMENT 'Current active phase ID',
  `overall_progress` decimal(5,2) NOT NULL DEFAULT 0.00 COMMENT 'Overall progress percent',
  `overall_mastery` decimal(5,2) NOT NULL DEFAULT 0.00 COMMENT 'Overall mastery percent',
  `latest_score` decimal(5,2) NULL DEFAULT NULL COMMENT 'Latest exam score',
  `average_score` decimal(5,2) NULL DEFAULT NULL COMMENT 'Average exam score',
  `highest_score` decimal(5,2) NULL DEFAULT NULL COMMENT 'Highest exam score',
  `plan_generated_time` datetime NULL DEFAULT NULL COMMENT 'Plan generated time',
  `last_studied_time` datetime NULL DEFAULT NULL COMMENT 'Last studied time',
  `next_review_time` datetime NULL DEFAULT NULL COMMENT 'Next review time',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT 'Creator',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT 'Updater',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT 'Deleted flag',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_ai_learning_plan_user_status` (`user_id`, `plan_status`) USING BTREE,
  KEY `idx_ai_learning_plan_current_phase_id` (`current_phase_id`) USING BTREE,
  KEY `idx_ai_learning_plan_next_review_time` (`next_review_time`) USING BTREE
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI learning plan';

-- ----------------------------
-- Table structure for ai_learning_material
-- ----------------------------
DROP TABLE IF EXISTS `ai_learning_material`;
CREATE TABLE `ai_learning_material` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Material ID',
  `plan_id` bigint NOT NULL COMMENT 'Plan ID',
  `user_id` bigint NOT NULL COMMENT 'Admin user ID',
  `knowledge_id` bigint NULL DEFAULT NULL COMMENT 'Linked ai_knowledge ID',
  `knowledge_document_id` bigint NULL DEFAULT NULL COMMENT 'Linked ai_knowledge_document ID',
  `file_id` bigint NULL DEFAULT NULL COMMENT 'Linked infra file ID',
  `material_type` tinyint NOT NULL DEFAULT 0 COMMENT 'Material type, enum: AiLearningMaterialTypeEnum',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'Material name',
  `storage_path` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Storage path or URL',
  `content_hash` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Content hash',
  `original_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'Original pasted text',
  `parsed_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'Parsed material text',
  `parse_status` tinyint NOT NULL DEFAULT 0 COMMENT 'Parse status, enum: AiLearningMaterialParseStatusEnum',
  `error_message` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Parse error message',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT 'Creator',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT 'Updater',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT 'Deleted flag',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_ai_learning_material_plan_id` (`plan_id`) USING BTREE,
  KEY `idx_ai_learning_material_document_id` (`knowledge_document_id`) USING BTREE,
  KEY `idx_ai_learning_material_parse_status` (`parse_status`) USING BTREE
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI learning source material';

-- ----------------------------
-- Table structure for ai_learning_phase
-- ----------------------------
DROP TABLE IF EXISTS `ai_learning_phase`;
CREATE TABLE `ai_learning_phase` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Phase ID',
  `plan_id` bigint NOT NULL COMMENT 'Plan ID',
  `user_id` bigint NOT NULL COMMENT 'Admin user ID',
  `phase_index` int NOT NULL DEFAULT 1 COMMENT 'Phase index starting from 1',
  `phase_title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'Phase title',
  `phase_goal` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'Phase goal',
  `phase_summary` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Phase summary',
  `mastery_targets` json NULL COMMENT 'Target mastery indicators JSON',
  `expected_days` int NOT NULL DEFAULT 0 COMMENT 'Expected days',
  `expected_minutes` int NOT NULL DEFAULT 0 COMMENT 'Expected study minutes',
  `sort` int NOT NULL DEFAULT 0 COMMENT 'Display sort',
  `phase_status` tinyint NOT NULL DEFAULT 0 COMMENT 'Phase status, enum: AiLearningPhaseStatusEnum',
  `start_time` datetime NULL DEFAULT NULL COMMENT 'Phase actual start time',
  `end_time` datetime NULL DEFAULT NULL COMMENT 'Phase actual end time',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT 'Creator',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT 'Updater',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT 'Deleted flag',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_ai_learning_phase_plan_phase_index` (`plan_id`, `phase_index`) USING BTREE,
  KEY `idx_ai_learning_phase_plan_status` (`plan_id`, `phase_status`) USING BTREE
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI learning phase';

-- ----------------------------
-- Table structure for ai_learning_task
-- ----------------------------
DROP TABLE IF EXISTS `ai_learning_task`;
CREATE TABLE `ai_learning_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Task ID',
  `plan_id` bigint NOT NULL COMMENT 'Plan ID',
  `phase_id` bigint NULL DEFAULT NULL COMMENT 'Phase ID',
  `user_id` bigint NOT NULL COMMENT 'Admin user ID',
  `task_date` date NOT NULL COMMENT 'Task date',
  `start_time` datetime NULL DEFAULT NULL COMMENT 'Scheduled start time',
  `end_time` datetime NULL DEFAULT NULL COMMENT 'Scheduled end time',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'Task title',
  `task_type` tinyint NOT NULL DEFAULT 0 COMMENT 'Task type, enum: AiLearningTaskTypeEnum',
  `task_mode` tinyint NOT NULL DEFAULT 0 COMMENT 'Task mode, enum: AiLearningTaskModeEnum',
  `task_status` tinyint NOT NULL DEFAULT 0 COMMENT 'Task status, enum: AiLearningTaskStatusEnum',
  `content_outline` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'Task outline',
  `learning_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'Generated learning content',
  `todo_question_count` int NOT NULL DEFAULT 0 COMMENT 'Planned question count',
  `completion_rate` decimal(5,2) NOT NULL DEFAULT 0.00 COMMENT 'Completion rate percent',
  `mastery_before` decimal(5,2) NULL DEFAULT NULL COMMENT 'Mastery before task',
  `mastery_after` decimal(5,2) NULL DEFAULT NULL COMMENT 'Mastery after task',
  `actual_study_minutes` int NOT NULL DEFAULT 0 COMMENT 'Actual study minutes',
  `exam_id` bigint NULL DEFAULT NULL COMMENT 'Linked exam ID',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT 'Creator',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT 'Updater',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT 'Deleted flag',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_ai_learning_task_user_date_status` (`user_id`, `task_date`, `task_status`) USING BTREE,
  KEY `idx_ai_learning_task_plan_phase` (`plan_id`, `phase_id`) USING BTREE,
  KEY `idx_ai_learning_task_exam_id` (`exam_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI learning task';

-- ----------------------------
-- Table structure for ai_learning_exam
-- ----------------------------
DROP TABLE IF EXISTS `ai_learning_exam`;
CREATE TABLE `ai_learning_exam` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Exam ID',
  `plan_id` bigint NOT NULL COMMENT 'Plan ID',
  `phase_id` bigint NULL DEFAULT NULL COMMENT 'Phase ID',
  `task_id` bigint NULL DEFAULT NULL COMMENT 'Task ID',
  `user_id` bigint NOT NULL COMMENT 'Admin user ID',
  `exam_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'Exam name',
  `wrong_question_mode` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Whether wrong-question reinforcement is enabled',
  `single_choice_count` int NOT NULL DEFAULT 0 COMMENT 'Single choice count',
  `multiple_choice_count` int NOT NULL DEFAULT 0 COMMENT 'Multiple choice count',
  `judge_count` int NOT NULL DEFAULT 0 COMMENT 'Judge count',
  `essay_count` int NOT NULL DEFAULT 0 COMMENT 'Essay count',
  `total_question_count` int NOT NULL DEFAULT 0 COMMENT 'Total question count',
  `exam_status` tinyint NOT NULL DEFAULT 0 COMMENT 'Exam status, enum: AiLearningExamStatusEnum',
  `score` decimal(5,2) NULL DEFAULT NULL COMMENT 'Final score',
  `objective_score` decimal(5,2) NULL DEFAULT NULL COMMENT 'Objective score',
  `subjective_score` decimal(5,2) NULL DEFAULT NULL COMMENT 'Subjective score',
  `average_correct_rate` decimal(5,2) NULL DEFAULT NULL COMMENT 'Average correct rate',
  `weak_points` json NULL COMMENT 'Weak points JSON',
  `started_time` datetime NULL DEFAULT NULL COMMENT 'Start time',
  `submitted_time` datetime NULL DEFAULT NULL COMMENT 'Submit time',
  `graded_time` datetime NULL DEFAULT NULL COMMENT 'Grade time',
  `duration_minutes` int NOT NULL DEFAULT 0 COMMENT 'Duration minutes',
  `error_message` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Error message',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT 'Creator',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT 'Updater',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT 'Deleted flag',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_ai_learning_exam_user_status` (`user_id`, `exam_status`) USING BTREE,
  KEY `idx_ai_learning_exam_plan_phase` (`plan_id`, `phase_id`) USING BTREE,
  KEY `idx_ai_learning_exam_task_id` (`task_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI learning exam';

-- ----------------------------
-- Table structure for ai_learning_exam_question
-- ----------------------------
DROP TABLE IF EXISTS `ai_learning_exam_question`;
CREATE TABLE `ai_learning_exam_question` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Exam question ID',
  `exam_id` bigint NOT NULL COMMENT 'Exam ID',
  `plan_id` bigint NOT NULL COMMENT 'Plan ID',
  `phase_id` bigint NULL DEFAULT NULL COMMENT 'Phase ID',
  `user_id` bigint NOT NULL COMMENT 'Admin user ID',
  `question_no` int NOT NULL DEFAULT 1 COMMENT 'Question number',
  `question_type` tinyint NOT NULL DEFAULT 0 COMMENT 'Question type, enum: AiLearningQuestionTypeEnum',
  `stem` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Question stem',
  `options_json` json NULL COMMENT 'Options JSON',
  `correct_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'Correct answer',
  `answer_analysis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'Answer analysis',
  `knowledge_points` json NULL COMMENT 'Knowledge points JSON',
  `user_answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'User answer',
  `is_correct` tinyint(1) NULL DEFAULT NULL COMMENT 'Whether answer is correct',
  `score` decimal(5,2) NOT NULL DEFAULT 0.00 COMMENT 'Question score',
  `earned_score` decimal(5,2) NOT NULL DEFAULT 0.00 COMMENT 'Earned score',
  `wrong_tag` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Whether tagged as wrong question',
  `needs_review` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Whether review is needed',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT 'Creator',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT 'Updater',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT 'Deleted flag',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_ai_learning_exam_question_exam_no` (`exam_id`, `question_no`) USING BTREE,
  KEY `idx_ai_learning_exam_question_user_correct` (`user_id`, `is_correct`) USING BTREE,
  KEY `idx_ai_learning_exam_question_review` (`needs_review`, `wrong_tag`) USING BTREE
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI learning exam question';

-- ----------------------------
-- Table structure for ai_learning_review_item
-- ----------------------------
DROP TABLE IF EXISTS `ai_learning_review_item`;
CREATE TABLE `ai_learning_review_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Review item ID',
  `user_id` bigint NOT NULL COMMENT 'Admin user ID',
  `plan_id` bigint NOT NULL COMMENT 'Plan ID',
  `phase_id` bigint NULL DEFAULT NULL COMMENT 'Phase ID',
  `task_id` bigint NULL DEFAULT NULL COMMENT 'Task ID',
  `exam_id` bigint NULL DEFAULT NULL COMMENT 'Exam ID',
  `question_id` bigint NULL DEFAULT NULL COMMENT 'Exam question ID',
  `source_type` tinyint NOT NULL DEFAULT 0 COMMENT 'Review source type, enum: AiLearningTaskTypeEnum',
  `content_title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'Review title',
  `review_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'Review content',
  `review_status` tinyint NOT NULL DEFAULT 0 COMMENT 'Review status, enum: AiLearningReviewStatusEnum',
  `review_count` int NOT NULL DEFAULT 0 COMMENT 'Review count',
  `ease_factor` decimal(4,2) NOT NULL DEFAULT 2.50 COMMENT 'Spaced repetition ease factor',
  `interval_days` int NOT NULL DEFAULT 0 COMMENT 'Review interval days',
  `next_review_time` datetime NULL DEFAULT NULL COMMENT 'Next review time',
  `last_review_time` datetime NULL DEFAULT NULL COMMENT 'Last review time',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT 'Creator',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT 'Updater',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT 'Deleted flag',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_ai_learning_review_user_status_next` (`user_id`, `review_status`, `next_review_time`) USING BTREE,
  KEY `idx_ai_learning_review_plan_phase` (`plan_id`, `phase_id`) USING BTREE,
  KEY `idx_ai_learning_review_question_id` (`question_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI learning review item';

-- ----------------------------
-- Table structure for ai_learning_profile_snapshot
-- ----------------------------
DROP TABLE IF EXISTS `ai_learning_profile_snapshot`;
CREATE TABLE `ai_learning_profile_snapshot` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Profile snapshot ID',
  `user_id` bigint NOT NULL COMMENT 'Admin user ID',
  `plan_id` bigint NULL DEFAULT NULL COMMENT 'Plan ID',
  `snapshot_type` tinyint NOT NULL DEFAULT 0 COMMENT 'Snapshot type, enum: AiLearningProfileSnapshotTypeEnum',
  `snapshot_date` date NOT NULL COMMENT 'Snapshot date',
  `strengths` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Strength summary',
  `weaknesses` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Weakness summary',
  `habits` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Habit summary',
  `memory_points` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'Long-term memory points',
  `ai_summary` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'AI generated summary',
  `metrics_json` json NULL COMMENT 'Metrics JSON',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT 'Creator',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT 'Updater',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT 'Deleted flag',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_ai_learning_profile_user_type_date` (`user_id`, `snapshot_type`, `snapshot_date`) USING BTREE,
  KEY `idx_ai_learning_profile_plan_id` (`plan_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI learning profile snapshot';

-- ----------------------------
-- Table structure for ai_learning_badge_record
-- ----------------------------
DROP TABLE IF EXISTS `ai_learning_badge_record`;
CREATE TABLE `ai_learning_badge_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Badge record ID',
  `user_id` bigint NOT NULL COMMENT 'Admin user ID',
  `plan_id` bigint NULL DEFAULT NULL COMMENT 'Plan ID',
  `badge_type` tinyint NOT NULL DEFAULT 0 COMMENT 'Badge type, enum: AiLearningBadgeTypeEnum',
  `badge_status` tinyint NOT NULL DEFAULT 0 COMMENT 'Badge status, enum: AiLearningBadgeStatusEnum',
  `progress_value` decimal(8,2) NOT NULL DEFAULT 0.00 COMMENT 'Current progress value',
  `progress_target` decimal(8,2) NOT NULL DEFAULT 0.00 COMMENT 'Target progress value',
  `unlocked_time` datetime NULL DEFAULT NULL COMMENT 'Unlocked time',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Badge remark',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT 'Creator',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT 'Updater',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT 'Deleted flag',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_ai_learning_badge_user_type` (`user_id`, `badge_type`) USING BTREE,
  KEY `idx_ai_learning_badge_status` (`badge_status`) USING BTREE,
  KEY `idx_ai_learning_badge_plan_id` (`plan_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI learning badge record';

SET FOREIGN_KEY_CHECKS = 1;
