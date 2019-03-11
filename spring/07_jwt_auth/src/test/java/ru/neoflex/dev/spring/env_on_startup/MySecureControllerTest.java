package ru.neoflex.dev.spring.env_on_startup;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class MySecureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testFilterParamsMapping() throws Exception {

        var result = mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/secure")
                        .with(httpBasic("user", "password"))
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        Assert.assertEquals("Current principal: user", result.getResponse().getContentAsString());
    }
}
