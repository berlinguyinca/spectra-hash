package edu.ucdavis.fiehnlab.spectra.hash.rest;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.*;


import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.jayway.restassured.module.mockmvc.RestAssuredMockMvc.*;
import static com.jayway.restassured.module.mockmvc.matcher.RestAssuredMockMvcMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class RestControllerTest {

    private MockMvc mockMvc;

    private String spectra = "{\n" +
            "\"ions\": [\n" +
            "{\n" +
            "\"mass\": 100,\n" +
            "\"intensity\": 1\n" +
            "},\n" +
            "{\n" +
            "\"mass\": 101,\n" +
            "\"intensity\": 2\n" +
            "},\n" +
            "{\n" +
            "\"mass\": 102,\n" +
            "\"intensity\": 3\n" +
            "}\n" +
            "],\n" +
            "\"metaData\": { }\n," +
            "\"type\": \"MS\"\n" +
            "}";

    @Autowired
    private WebApplicationContext context;


    @Value("${local.server.port}")
            int port;

    @org.junit.Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context).build();
        RestAssuredMockMvc.mockMvc = mockMvc;

        RestAssured.port = port;
    }

    @org.junit.After
    public void tearDown() throws Exception {

    }

    @org.junit.Test
    @Ignore
    public void testConvert() throws Exception {

        given().log().all()
                .contentType("application/json")
                .body(spectra)
                .when()
                .post("/splash/it")
                .then().log().all().statusCode(HttpStatus.OK.value());
    }

    @org.junit.Test
    @Ignore
    public void testConvert2() throws Exception {

        given().log().all()
                .contentType("application/json")
                .body(spectra)
                .when()
                .post("/splash/it")
                .then().log().all().statusCode(HttpStatus.OK.value());
    }

}