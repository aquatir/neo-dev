package ru.neoflex.dev.spring.api_version_resolver;

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

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGet_url_content_type_v1_expectStatus200() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/url_content_type")
                        .accept("application/vnd.myapi.v1+json")
                )
                .andExpect(status().isOk())
                .andExpect(content().string("url1_accept"));
    }

    @Test
    public void testGet_url_content_type_v2_expectStatus200() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/url_content_type")
                        .accept("application/vnd.myapi.v2+json")
                )
                .andExpect(status().isOk())
                .andExpect(content().string("url2_accept"));
    }

    @Test
    public void testGet_url1_header_expectStatus200() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/url_header")
                        .header("v", 1)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("url1_header"));
    }

    @Test
    public void testGet_url2_header_expectStatus200() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/url_header")
                        .header("v", 2)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("url2_header"));
    }
}
