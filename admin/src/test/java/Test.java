public class Test {
    private static final String sql = "CREATE TABLE `t_group_%d` (\n" +
            "  `id` bigint NOT NULL COMMENT 'ID',\n" +
            "  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '分组标识',\n" +
            "  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '分组名称',\n" +
            "  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '创建分组用户名',\n" +
            "  `sort_order` int DEFAULT '0' COMMENT '分组排序',\n" +
            "  `create_time` datetime DEFAULT NULL COMMENT '创建时间',\n" +
            "  `update_time` datetime DEFAULT NULL COMMENT '修改时间',\n" +
            "  `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  UNIQUE KEY `idx_unique_username_gid` (`gid`,`username`) USING BTREE\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8;\n";

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.printf((sql) + "%n", i);
        }
    }
}
