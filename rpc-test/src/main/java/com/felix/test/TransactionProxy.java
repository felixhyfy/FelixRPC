package com.felix.test;

import java.lang.reflect.Proxy;

/**
 * @description:
 * @author: Felix
 * @date: 2021/2/24 20:48
 */
public class TransactionProxy {
    private Object target;

    public TransactionProxy(Object target) {
        this.target = target;
    }

    public Object genProxyInstance() {
        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                ((proxy, method, args) -> {
                    System.out.println("start transaction");
                    Object result = method.invoke(target, args);
                    System.out.println("submit transaction");
                    return result;
                })
        );
    }

    public static void main(String[] args) {
        UserDao userDao = new UserDaoImpl();
        UserDao proxyInstance = (UserDao) new TransactionProxy(userDao).genProxyInstance();
        proxyInstance.insert();
    }
}
