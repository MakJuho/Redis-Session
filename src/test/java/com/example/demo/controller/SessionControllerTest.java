package com.example.demo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;11
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class SessionControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	@Test
	void 세션에_값_저장() throws Exception {
		mockMvc.perform(post("/session")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"key\":\"name\",\"value\":\"makjuho\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.key").value("name"))
				.andExpect(jsonPath("$.value").value("makjuho"))
				.andExpect(jsonPath("$.sessionId").exists());
	}

	@Test
	void 세션에서_값_조회() throws Exception {
		MockHttpSession session = new MockHttpSession();

		mockMvc.perform(post("/session")
						.session(session)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"key\":\"name\",\"value\":\"makjuho\"}"))
				.andExpect(status().isOk());

		mockMvc.perform(get("/session")
						.session(session)
						.param("key", "name"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.key").value("name"))
				.andExpect(jsonPath("$.value").value("makjuho"));
	}

	@Test
	void 존재하지_않는_키_조회시_404() throws Exception {
		MockHttpSession session = new MockHttpSession();

		mockMvc.perform(get("/session")
						.session(session)
						.param("key", "nonexistent"))
				.andExpect(status().isNotFound());
	}

	@Test
	void 세션_삭제() throws Exception {
		MockHttpSession session = new MockHttpSession();

		mockMvc.perform(post("/session")
						.session(session)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"key\":\"name\",\"value\":\"makjuho\"}"))
				.andExpect(status().isOk());

		mockMvc.perform(delete("/session")
						.session(session))
				.andExpect(status().isNoContent());
	}

	@Test
	void 세션_삭제_후_조회시_404() throws Exception {
		MockHttpSession session = new MockHttpSession();

		mockMvc.perform(post("/session")
						.session(session)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"key\":\"name\",\"value\":\"makjuho\"}"))
				.andExpect(status().isOk());

		mockMvc.perform(delete("/session")
						.session(session))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/session")
						.param("key", "name"))
				.andExpect(status().isNotFound());
	}

	@Test
	void 여러_키_저장_후_각각_조회() throws Exception {
		MockHttpSession session = new MockHttpSession();

		mockMvc.perform(post("/session")
						.session(session)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"key\":\"name\",\"value\":\"makjuho\"}"))
				.andExpect(status().isOk());

		mockMvc.perform(post("/session")
						.session(session)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"key\":\"role\",\"value\":\"developer\"}"))
				.andExpect(status().isOk());

		mockMvc.perform(get("/session")
						.session(session)
						.param("key", "name"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.value").value("makjuho"));

		mockMvc.perform(get("/session")
						.session(session)
						.param("key", "role"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.value").value("developer"));
	}
}
