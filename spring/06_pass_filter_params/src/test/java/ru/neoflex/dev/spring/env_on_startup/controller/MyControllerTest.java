package ru.neoflex.dev.spring.env_on_startup.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class MyControllerTest {

    @Autowired private MockMvc mockMvc;

    @Test
    public void testRequestParam() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/test?firstValue=5&second=aaa&third=bbb")
                )
                .andExpect(status().isOk())
                .andExpect(content().string("5aaabbb"));
    }
}
