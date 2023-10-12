package de.werum.coprs.cadip.cadip_mock.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class TestOdataController {
	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testStatus() throws Exception {
		this.mockMvc.perform(get("/odata/v1/$metadata").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

	}

	@Test
	public void testSessionHasValues() throws Exception {
		ResultActions t = this.mockMvc.perform(get("/odata/v1/Sessions").contentType(MediaType.APPLICATION_JSON));
		t.andExpect(jsonPath("value").isArray());
	}

	@Test
	public void testQueryFunctionsCountTopSkip() throws Exception {
		MvcResult f = this.mockMvc.perform(get("/odata/v1/Files?$count=true").contentType(MediaType.APPLICATION_JSON))
				.andReturn();
		JsonNode response = toJsonNode(f);
		JsonNode files = response.findValue("value");
		int newSize = files.size() - 2;
		MvcResult f2 = this.mockMvc.perform(
				get("/odata/v1/Files?$count=true&$skip=1&$top=" + newSize).contentType(MediaType.APPLICATION_JSON))
				.andReturn();
		JsonNode newResponse = toJsonNode(f2);
		JsonNode newFiles = newResponse.findValue("value");
		assertThat(files.get(1)).isEqualTo(newFiles.get(0));
		assertThat(newFiles.size()).isEqualTo(newSize);
		assertThat(newResponse.findValue("@odata.count").asInt()).isEqualTo(response.findValue("@odata.count").asInt() - 2);
	}
	
	@Test
	public void testQueryFunctionOrderBy() throws Exception {
		// By default its ordered by PublicationDate ascending
		MvcResult f = this.mockMvc.perform(get("/odata/v1/Files").contentType(MediaType.APPLICATION_JSON))
				.andReturn();
		JsonNode response = toJsonNode(f);
		JsonNode file = response.findValue("value").get(0);
		
		MvcResult f2 = this.mockMvc.perform(
				get("/odata/v1/Files?$orderby=PublicationDate desc").contentType(MediaType.APPLICATION_JSON))
				.andReturn();
		JsonNode newResponse = toJsonNode(f2);
		JsonNode newFiles = newResponse.findValue("value");
		assertThat(file).isEqualTo(newFiles.get(newFiles.size()-1));
	}
	
	@Test
	public void testQueryFunctionFilter() throws Exception {
		MvcResult f = this.mockMvc.perform(get("/odata/v1/Files").contentType(MediaType.APPLICATION_JSON))
				.andReturn();
		JsonNode response = toJsonNode(f);
		JsonNode files = response.findValue("value");
		JsonNode mid = files.get((int) Math.floor(files.size()/2));
		String blockNumber = mid.get("BlockNumber").asText();
		String channel = mid.get("Channel").asText();
		
		MvcResult f2 = this.mockMvc.perform(
				get("/odata/v1/Files?$filter=BlockNumber eq "+blockNumber+" and Channel eq "+channel).contentType(MediaType.APPLICATION_JSON))
				.andReturn();
		JsonNode newResponse = toJsonNode(f2);
		JsonNode newFiles = newResponse.findValue("value");
		newFiles.forEach(o -> {
			assertThat(o.get("Channel").asText()).isEqualTo(channel);
			assertThat(o.get("BlockNumber").asText()).isEqualTo(blockNumber);
		});
	}

	public JsonNode toJsonNode(MvcResult o)
			throws JsonMappingException, JsonProcessingException, UnsupportedEncodingException {
		return toJsonNode(o.getResponse().getContentAsString());
	}

	public JsonNode toJsonNode(String jsonString) throws JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode actualObj = mapper.readTree(jsonString);
		return actualObj;
	}
}
