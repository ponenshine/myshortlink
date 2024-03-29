public class Test {
    private static final String sql = "CREATE TABLE `t_uri_gid_%d` (\n" +
            "  `id` bigint NOT NULL,\n" +
            "  `uri` varchar(16) DEFAULT NULL,\n" +
            "  `gid` varchar(32) DEFAULT NULL,\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  UNIQUE KEY `index_unique_uri` (`uri`)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8;\n";

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.printf((sql) + "%n", i);
        }
    }
}
