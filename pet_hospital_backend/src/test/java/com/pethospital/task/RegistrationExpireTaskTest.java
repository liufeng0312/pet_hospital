package com.pethospital.task;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RegistrationExpireTaskTest {

    @Autowired
    private RegistrationExpireTask registrationExpireTask;

    @Test
    public void testManualExpire() {
        int count = registrationExpireTask.manualExpire();
        System.out.println("处理了 " + count + " 条过期挂号记录");
    }
}
