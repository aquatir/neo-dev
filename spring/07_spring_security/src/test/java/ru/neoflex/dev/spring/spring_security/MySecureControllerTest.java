package ru.neoflex.dev.spring.spring_security;

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
    public void testUser1_ExceptSuccess() throws Exception {

        var result = mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/secure")
                        .with(httpBasic("user1", "password1"))
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        Assert.assertEquals("Current principal: user1", result.getResponse().getContentAsString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUser2_ExceptFailure() throws Exception {

        var result = mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/secure")
                        .with(httpBasic("user2", "password2"))
                )
                .andExpect(status().isOk())
                .andDo(print());
    }
}
