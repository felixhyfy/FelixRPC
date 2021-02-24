package com.felix.test;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @description:
 * @author: Felix
 * @date: 2021/2/24 21:00
 */
public class CglibTransactionProxy implements MethodInterceptor {
    private Object target;

    public CglibTransactionProxy(Object target) {
        this.target = target;
    }

    public Object genProxyInstance() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(this);
        return enhancer.create();
    }

    @Override
    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        System.out.println("start transaction");
        Object result = methodProxy.invokeSuper(object, args);
        System.out.println("submit transaction");
        return result;
    }

    public static void main(String[] args) {
        UserDao userDao = new UserDaoImpl();
        UserDao proxyInstance = (UserDao) new CglibTransactionProxy(userDao).genProxyInstance();
        proxyInstance.insert();
    }
}
