package com.yc.study.plugin;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author tommy
 * @title: MonitorInterceptor
 * @projectName coderead-mybatis
 * @description: 监控拦截器
 * @date 2020/6/209:33 PM
 * <p>
 * 1.统计表的增删改查次数
 */
@Intercepts(
        @Signature(
                type = StatementHandler.class,
                method = "prepare",
                args = {Connection.class, Integer.class}
        )
)
public class MonitorInterceptor implements Interceptor {
    private static Map<String, ExecuteInfo> map = new HashMap<>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = statementHandler.getBoundSql();
        String sql = boundSql.getSql();


        //解析SQL
        //druid--> sqlUtil
        SqlStatParse parse = new SqlStatParse(sql, "mySql");
        parse.getAll().stream().forEach(s -> {
            ExecuteInfo executeInfo =
                    map.computeIfAbsent(s.getTableName(), k -> new ExecuteInfo(s.getTableName()));
            executeInfo.add(s.getModel());
        });
        map.values().stream().forEach(System.out::println);
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

    public static class ExecuteInfo {
        public String tableName;
        public int selectCount;
        public int updateCount;
        public int deleteCount;
        public int insertCount;
        public ExecuteInfo(String tableName) {
            this.tableName = tableName;
        }

        private void add(String model) {
            switch (model) {
                case "insert":
                    insertCount++;
                    break;
                case "update":
                    updateCount++;
                    break;
                case "select":
                    selectCount++;
                    break;
                case "delete":
                    deleteCount++;
                    break;
            }
        }

        @Override
        public String toString() {
            return "ExecuteInfo{" +
                    "tableName='" + tableName + '\'' +
                    ", selectCount=" + selectCount +
                    ", updateCount=" + updateCount +
                    ", deleteCount=" + deleteCount +
                    ", insertCount=" + insertCount +
                    '}';
        }
    }
}
