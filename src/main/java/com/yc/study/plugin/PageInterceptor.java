package com.yc.study.plugin;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

/**
 * @author admin
 * @date 2020/9/2 12:44:54
 * @description 分页拦截器
 */
//拦截具体细节(拦截的类 、 拦截类中的哪个方法 、 拦截的属性)
@Intercepts(@Signature(type = StatementHandler.class,
        method = "prepare", args = {Connection.class,
        Integer.class}))
public class PageInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        /*Invocation:
         target:当前拦截的类 statementHandler
         method：当前拦截的方法 prepare
         args:当前拦截方法的属性 Connection 超时时间*/

        //1.检测当前是否满足分页条件
        //判断属性对象是否包含分页类
        StatementHandler handler = (StatementHandler) invocation.getTarget();
        Object parameterObject = handler.getBoundSql().getParameterObject();

        Page page = null;
        if (parameterObject instanceof Page) {
            page = (Page) parameterObject;
        } else if (parameterObject instanceof Map) {
            page = (Page) ((Map) parameterObject).values().stream().filter(v -> v instanceof Page).findFirst().orElse(null);
        }
        if (page == null) {
            return invocation.proceed();
        }
        //2.设置总行数
        int count = selectCount(invocation);

        page.setTotal(count);
        //3.修改原有SQL
        BoundSql boundSql = handler.getBoundSql();
        String newSql =String.format("%s  limit %s offset %s",boundSql.getSql(),page.getSize(),page.getOffset());
        SystemMetaObject.forObject(boundSql).setValue("sql",newSql);
        //放行
        return invocation.proceed();
    }

    private int selectCount(Invocation invocation) throws SQLException {
        int count = 0;
        //设置sql
        StatementHandler handler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = handler.getBoundSql();
        String countSql = String.format("select count(*) from (%s) as _page", boundSql.getSql());
        Connection connection = (Connection) invocation.getArgs()[0];
        PreparedStatement preparedStatement = connection.prepareStatement(countSql);
        //设置sql属性
        handler.getParameterHandler().setParameters(preparedStatement);
        //执行sql
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            count = resultSet.getInt(1);
        }
        resultSet.close();
        preparedStatement.close();
        return count;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
