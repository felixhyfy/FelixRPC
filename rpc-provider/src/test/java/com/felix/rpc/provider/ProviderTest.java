package com.felix.rpc.provider;

import com.felix.rpc.provider.facade.HelloFacadeImpl;
import org.junit.Test;

/**
 * @description:
 * @author: Felix
 * @date: 2021/2/25 18:57
 */
public class ProviderTest {

    @Test
    public void test() {
        HelloFacadeImpl helloFacade = new HelloFacadeImpl();
        helloFacade.hello("felix rpc");
    }
}
