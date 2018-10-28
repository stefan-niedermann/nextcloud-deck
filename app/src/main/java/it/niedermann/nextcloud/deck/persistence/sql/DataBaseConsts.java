package it.niedermann.nextcloud.deck.persistence.sql;

import java.util.HashMap;

public class DataBaseConsts {
// LazyKit:
//    public static final String TABLE_{JS var txt = "{$1}"; txt = txt.toUpperCase(); txt JS} = "{$1}";
//    public static final String SQL_CREATE_{JS var txt = "{$1}"; txt = txt.toUpperCase(); txt JS}_TABLE = "CREATE TABLE IF NOT EXISTS '" + TABLE_{JS var txt = "{$1}"; txt = txt.toUpperCase(); txt JS} + "' (" +
//    {JS
//        var txt = "{$2}";
//        var lines = txt.split("\n") ;
//        var res = "";
//        for (var i = 0; i<lines.length; i++) {
//            res = res + "\"" +lines[i]+"\"";
//            if (i < lines.length-1) res = res + " +\n"; else res = res + ";";
//        }
//        res
//        JS}

    // INPUT:
//    CREATE TABLE IF NOT EXISTS `oc_deck_stacks` (
//            `id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
//	`title`	VARCHAR ( 100 ) NOT NULL COLLATE BINARY,
//	`board_id`	BIGINT NOT NULL,
//            `order`	BIGINT DEFAULT NULL,
//            `deleted_at`	BIGINT UNSIGNED DEFAULT 0
//            );
//    CREATE TABLE IF NOT EXISTS `oc_deck_labels` (
//            `id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
//	`title`	VARCHAR ( 100 ) DEFAULT NULL COLLATE BINARY,
//	`color`	VARCHAR ( 6 ) DEFAULT NULL COLLATE BINARY,
//	`board_id`	BIGINT NOT NULL
//);
//    CREATE TABLE IF NOT EXISTS `oc_deck_cards` (
//            `id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
//	`title`	VARCHAR ( 100 ) NOT NULL COLLATE BINARY,
//	`description`	CLOB DEFAULT NULL COLLATE BINARY,
//            `stack_id`	BIGINT NOT NULL,
//            `type`	VARCHAR ( 64 ) NOT NULL DEFAULT 'plain' COLLATE BINARY,
//	`last_modified`	INTEGER UNSIGNED DEFAULT 0,
//            `created_at`	INTEGER UNSIGNED DEFAULT 0,
//            `owner`	VARCHAR ( 64 ) NOT NULL COLLATE BINARY,
//	`order`	BIGINT DEFAULT NULL,
//            `archived`	BOOLEAN DEFAULT '0',
//            `duedate`	DATETIME DEFAULT NULL,
//            `notified`	BOOLEAN DEFAULT '0',
//            `deleted_at`	BIGINT UNSIGNED DEFAULT 0
//            );
//    CREATE TABLE IF NOT EXISTS `oc_deck_boards` (
//            `id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
//	`title`	VARCHAR ( 100 ) NOT NULL COLLATE BINARY,
//	`owner`	VARCHAR ( 64 ) NOT NULL COLLATE BINARY,
//	`color`	VARCHAR ( 6 ) DEFAULT NULL COLLATE BINARY,
//	`archived`	BOOLEAN DEFAULT '0',
//            `deleted_at`	BIGINT UNSIGNED DEFAULT 0
//            );
//    CREATE TABLE IF NOT EXISTS `oc_deck_board_acl` (
//            `id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
//	`board_id`	BIGINT NOT NULL,
//            `type`	INTEGER NOT NULL,
//            `participant`	VARCHAR ( 64 ) NOT NULL COLLATE BINARY,
//	`permission_edit`	BOOLEAN DEFAULT '0',
//            `permission_share`	BOOLEAN DEFAULT '0',
//            `permission_manage`	BOOLEAN DEFAULT '0'
//            );
//    CREATE TABLE IF NOT EXISTS `oc_deck_attachment` (
//            `id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
//	`card_id`	BIGINT NOT NULL,
//            `type`	VARCHAR ( 64 ) NOT NULL COLLATE BINARY,
//	`data`	VARCHAR ( 255 ) DEFAULT NULL COLLATE BINARY,
//	`last_modified`	BIGINT UNSIGNED DEFAULT 0,
//            `created_at`	BIGINT UNSIGNED DEFAULT 0,
//            `created_by`	VARCHAR ( 64 ) NOT NULL COLLATE BINARY,
//	`deleted_at`	BIGINT UNSIGNED DEFAULT 0
//            );
//    CREATE TABLE IF NOT EXISTS `oc_deck_assigned_users` (
//            `id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
//	`participant`	VARCHAR ( 64 ) NOT NULL COLLATE BINARY,
//	`card_id`	INTEGER NOT NULL DEFAULT 0
//            );
//    CREATE TABLE IF NOT EXISTS `oc_deck_assigned_labels` (
//            `id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
//	`label_id`	INTEGER NOT NULL DEFAULT 0,
//            `card_id`	INTEGER NOT NULL DEFAULT 0
//            );
//    CREATE INDEX IF NOT EXISTS `deck_stacks_order_index` ON `oc_deck_stacks` (
//            `order`
//            );
//    CREATE INDEX IF NOT EXISTS `deck_stacks_board_id_index` ON `oc_deck_stacks` (
//            `board_id`
//            );
//    CREATE INDEX IF NOT EXISTS `deck_labels_board_id_index` ON `oc_deck_labels` (
//            `board_id`
//            );
//    CREATE INDEX IF NOT EXISTS `deck_cards_stack_id_index` ON `oc_deck_cards` (
//            `stack_id`
//            );
//    CREATE INDEX IF NOT EXISTS `deck_cards_order_index` ON `oc_deck_cards` (
//            `order`
//            );
//    CREATE INDEX IF NOT EXISTS `deck_cards_archived_index` ON `oc_deck_cards` (
//            `archived`
//            );
//    CREATE UNIQUE INDEX IF NOT EXISTS `deck_board_acl_uq_i` ON `oc_deck_board_acl` (
//            `board_id`,
//            `type`,
//            `participant`
//            );
//    CREATE INDEX IF NOT EXISTS `deck_board_acl_idx_i` ON `oc_deck_board_acl` (
//            `board_id`
//            );
//    CREATE INDEX IF NOT EXISTS `deck_assigned_users_idx_p` ON `oc_deck_assigned_users` (
//            `participant`
//            );
//    CREATE INDEX IF NOT EXISTS `deck_assigned_users_idx_c` ON `oc_deck_assigned_users` (
//            `card_id`
//            );
//    CREATE INDEX IF NOT EXISTS `deck_assigned_labels_idx_i` ON `oc_deck_assigned_labels` (
//            `label_id`
//            );
//    CREATE INDEX IF NOT EXISTS `deck_assigned_labels_idx_c` ON `oc_deck_assigned_labels` (
//            `card_id`
//            );
    public static final String TABLE_ACCOUNTS = "ACCOUNTS";
    public static final String SQL_CREATE_ACCOUNTS_TABLE = "CREATE TABLE " + TABLE_ACCOUNTS +
            " ( " +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "ACCOUNT_NAME TEXT NOT NULL UNIQUE" +
            " )";

    public static final String TABLE_OC_DECK_STACKS = "oc_deck_stacks";
    public static final String SQL_CREATE_OC_DECK_STACKS_TABLE = "CREATE TABLE IF NOT EXISTS '" + TABLE_OC_DECK_STACKS + "' (" +
            "`id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            "	`title`	VARCHAR ( 100 ) NOT NULL COLLATE BINARY," +
            "	`board_id`	BIGINT NOT NULL," +
            "	`order`	BIGINT DEFAULT NULL," +
            "	`deleted_at`	BIGINT UNSIGNED DEFAULT 0" +
            ")";

    public static final String TABLE_OC_DECK_LABELS = "oc_deck_labels";
    public static final String SQL_CREATE_OC_DECK_LABELS_TABLE = "CREATE TABLE IF NOT EXISTS '" + TABLE_OC_DECK_LABELS + "' (" +
            "`id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            "	`title`	VARCHAR ( 100 ) DEFAULT NULL COLLATE BINARY," +
            "	`color`	VARCHAR ( 6 ) DEFAULT NULL COLLATE BINARY," +
            "	`board_id`	BIGINT NOT NULL" +
            ")";

    public static final String TABLE_OC_DECK_CARDS = "oc_deck_cards";
    public static final String SQL_CREATE_OC_DECK_CARDS_TABLE = "CREATE TABLE IF NOT EXISTS '" + TABLE_OC_DECK_CARDS + "' (" +
            "`id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            "	`title`	VARCHAR ( 100 ) NOT NULL COLLATE BINARY," +
            "	`description`	CLOB DEFAULT NULL COLLATE BINARY," +
            "	`stack_id`	BIGINT NOT NULL," +
            "	`type`	VARCHAR ( 64 ) NOT NULL DEFAULT 'plain' COLLATE BINARY," +
            "	`last_modified`	INTEGER UNSIGNED DEFAULT 0," +
            "	`created_at`	INTEGER UNSIGNED DEFAULT 0," +
            "	`owner`	VARCHAR ( 64 ) NOT NULL COLLATE BINARY," +
            "	`order`	BIGINT DEFAULT NULL," +
            "	`archived`	BOOLEAN DEFAULT '0'," +
            "	`duedate`	DATETIME DEFAULT NULL," +
            "	`notified`	BOOLEAN DEFAULT '0'," +
            "	`deleted_at`	BIGINT UNSIGNED DEFAULT 0" +
            ")";

    public static final String TABLE_OC_DECK_BOARDS = "oc_deck_boards";
    public static final String SQL_CREATE_OC_DECK_BOARDS_TABLE = "CREATE TABLE IF NOT EXISTS '" + TABLE_OC_DECK_BOARDS + "' (" +
            "`id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            "	`title`	VARCHAR ( 100 ) NOT NULL COLLATE BINARY," +
            "	`owner`	VARCHAR ( 64 ) NOT NULL COLLATE BINARY," +
            "	`color`	VARCHAR ( 6 ) DEFAULT NULL COLLATE BINARY," +
            "	`archived`	BOOLEAN DEFAULT '0'," +
            "	`deleted_at`	BIGINT UNSIGNED DEFAULT 0" +
            ")";

    public static final String TABLE_OC_DECK_BOARD_ACL = "oc_deck_board_acl";
    public static final String SQL_CREATE_OC_DECK_BOARD_ACL_TABLE = "CREATE TABLE IF NOT EXISTS '" + TABLE_OC_DECK_BOARD_ACL + "' (" +
            "`id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            "	`board_id`	BIGINT NOT NULL," +
            "	`type`	INTEGER NOT NULL," +
            "	`participant`	VARCHAR ( 64 ) NOT NULL COLLATE BINARY," +
            "	`permission_edit`	BOOLEAN DEFAULT '0'," +
            "	`permission_share`	BOOLEAN DEFAULT '0'," +
            "	`permission_manage`	BOOLEAN DEFAULT '0'" +
            ")";

    public static final String TABLE_OC_DECK_ATTACHMENT = "oc_deck_attachment";
    public static final String SQL_CREATE_OC_DECK_ATTACHMENT_TABLE = "CREATE TABLE IF NOT EXISTS '" + TABLE_OC_DECK_ATTACHMENT + "' (" +
            "`id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            "	`card_id`	BIGINT NOT NULL," +
            "	`type`	VARCHAR ( 64 ) NOT NULL COLLATE BINARY," +
            "	`data`	VARCHAR ( 255 ) DEFAULT NULL COLLATE BINARY," +
            "	`last_modified`	BIGINT UNSIGNED DEFAULT 0," +
            "	`created_at`	BIGINT UNSIGNED DEFAULT 0," +
            "	`created_by`	VARCHAR ( 64 ) NOT NULL COLLATE BINARY," +
            "	`deleted_at`	BIGINT UNSIGNED DEFAULT 0" +
            ")";

    public static final String TABLE_OC_DECK_ASSIGNED_USERS = "oc_deck_assigned_users";
    public static final String SQL_CREATE_OC_DECK_ASSIGNED_USERS_TABLE = "CREATE TABLE IF NOT EXISTS '" + TABLE_OC_DECK_ASSIGNED_USERS + "' (" +
            "`id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            "	`participant`	VARCHAR ( 64 ) NOT NULL COLLATE BINARY," +
            "	`card_id`	INTEGER NOT NULL DEFAULT 0" +
            ")";

    public static final String TABLE_OC_DECK_ASSIGNED_LABELS = "oc_deck_assigned_labels";
    public static final String SQL_CREATE_OC_DECK_ASSIGNED_LABELS_TABLE = "CREATE TABLE IF NOT EXISTS '" + TABLE_OC_DECK_ASSIGNED_LABELS + "' (" +
            "`id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            "	`label_id`	INTEGER NOT NULL DEFAULT 0," +
            "	`card_id`	INTEGER NOT NULL DEFAULT 0" +
            ");";



    public static final String[] ALL_TABLES = new String[]{
            TABLE_ACCOUNTS,
            TABLE_OC_DECK_STACKS,
            TABLE_OC_DECK_LABELS,
            TABLE_OC_DECK_CARDS,
            TABLE_OC_DECK_BOARDS,
            TABLE_OC_DECK_BOARD_ACL,
            TABLE_OC_DECK_ATTACHMENT,
            TABLE_OC_DECK_ASSIGNED_USERS,
            TABLE_OC_DECK_ASSIGNED_LABELS
    };

    public static final String[] ALL_CREATE_INDICES = new String[]{

            "CREATE INDEX IF NOT EXISTS `deck_stacks_order_index` ON `oc_deck_stacks` (" +
                    "	`order`" +
                    ")",

            "CREATE INDEX IF NOT EXISTS `deck_stacks_board_id_index` ON `oc_deck_stacks` (" +
                    "	`board_id`" +
                    ")",

            "CREATE INDEX IF NOT EXISTS `deck_labels_board_id_index` ON `oc_deck_labels` (" +
                    "	`board_id`" +
                    ")",

            "CREATE INDEX IF NOT EXISTS `deck_cards_stack_id_index` ON `oc_deck_cards` (" +
                    "	`stack_id`" +
                    ")",

            "CREATE INDEX IF NOT EXISTS `deck_cards_order_index` ON `oc_deck_cards` (" +
                    "	`order`" +
                    ")",

            "CREATE INDEX IF NOT EXISTS `deck_cards_archived_index` ON `oc_deck_cards` (" +
                    "	`archived`" +
                    ")",

            "CREATE INDEX IF NOT EXISTS `deck_board_acl_idx_i` ON `oc_deck_board_acl` (" +
                    "	`board_id`" +
                    ")",

            "CREATE INDEX IF NOT EXISTS `deck_assigned_users_idx_p` ON `oc_deck_assigned_users` (" +
                    "	`participant`" +
                    ")",

            "CREATE INDEX IF NOT EXISTS `deck_assigned_users_idx_c` ON `oc_deck_assigned_users` (" +
                    "	`card_id`" +
                    ")",

            "CREATE INDEX IF NOT EXISTS `deck_assigned_labels_idx_i` ON `oc_deck_assigned_labels` (" +
                    "	`label_id`" +
                    ")",

            "CREATE INDEX IF NOT EXISTS `deck_assigned_labels_idx_c` ON `oc_deck_assigned_labels` (" +
                    "	`card_id`" +
                    ");"
    };

    public static final String[] ALL_CREATES = new String[]{
            SQL_CREATE_ACCOUNTS_TABLE,
            SQL_CREATE_OC_DECK_STACKS_TABLE,
            SQL_CREATE_OC_DECK_LABELS_TABLE,
            SQL_CREATE_OC_DECK_CARDS_TABLE,
            SQL_CREATE_OC_DECK_BOARDS_TABLE,
            SQL_CREATE_OC_DECK_BOARD_ACL_TABLE,
            SQL_CREATE_OC_DECK_ATTACHMENT_TABLE,
            SQL_CREATE_OC_DECK_ASSIGNED_USERS_TABLE,
            SQL_CREATE_OC_DECK_ASSIGNED_LABELS_TABLE
    };
}
