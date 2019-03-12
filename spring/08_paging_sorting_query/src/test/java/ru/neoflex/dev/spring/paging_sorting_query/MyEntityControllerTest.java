package ru.neoflex.dev.spring.paging_sorting_query;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@Sql("/test-data.sql")
public class MyEntityControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetMyEntities() throws Exception {
        var result = mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/myEntity")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        var list = (PageWithTotalResponse<MyEntity>) objectMapper.readValue(result.getResponse().getContentAsString(), PageWithTotalResponse.class);
        Assert.assertEquals(6, list.getTotalElements());
    }
}
