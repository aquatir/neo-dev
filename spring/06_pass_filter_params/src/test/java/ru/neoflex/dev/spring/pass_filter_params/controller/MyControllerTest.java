package ru.neoflex.dev.spring.pass_filter_params.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class MyControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testObjectMapping() throws Exception {
        var request = new HashMap<String, String>();
        request.put("date", "2019-01-02 23:30:30");
        request.put("my_name", "tip-top");
        request.put("very_other_value", "22");

        var jsonRequest = objectMapper.writeValueAsString(request);

        var result = mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/testcall1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        Assert.assertEquals("2019-01-02T23:30:30tip-top22", result.getResponse().getContentAsString());
    }

    @Test
    public void testFilterParamsMapping() throws Exception {
        var result = mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/testcall2/1/?name=test,age=5")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        Assert.assertEquals("1test,age=5", result.getResponse().getContentAsString());
    }

}
