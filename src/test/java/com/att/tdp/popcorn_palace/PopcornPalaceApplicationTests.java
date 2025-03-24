package com.att.tdp.popcorn_palace;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PopcornPalaceApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void endToEndTest() throws Exception {
		// ====== Step 1: Add 2 Movies ======
		String movie1Json = """
            { "title": "Interstellar", "genre": "Sci-Fi", "duration": 169, "rating": 8.6, "releaseYear": 2014 }
        """;
		String movie2Json = """
            { "title": "The Dark Knight", "genre": "Action", "duration": 152, "rating": 9.0, "releaseYear": 2008 }
        """;

		Long movie1Id = extractIdFromResponse(mockMvc.perform(post("/movies")
				.contentType(MediaType.APPLICATION_JSON)
				.content(movie1Json)).andExpect(status().isOk()).andReturn());

		Long movie2Id = extractIdFromResponse(mockMvc.perform(post("/movies")
				.contentType(MediaType.APPLICATION_JSON)
				.content(movie2Json)).andExpect(status().isOk()).andReturn());

		// ====== Step 2: Add 4 Showtimes ======
		List<Long> showtimeIds = new ArrayList<>();
		List<String> showtimeBodies = new ArrayList<>();
		Instant now = Instant.now();
		int[] durationsInHours = {1, 2, 3, 1};
		for (int i = 0; i < 4; i++) {
			Instant start = now.plusSeconds((i+1) * 3 * 3600L);
			Instant end = start.plusSeconds(durationsInHours[i] * 3600L);
			Long movieId = (i < 2) ? movie1Id : movie2Id;

			String showtimeJson = String.format("""
                {
                    "movieId": %d,
                    "price": %.2f,
                    "theater": "Theater-%d",
                    "startTime": "%s",
                    "endTime": "%s"
                }
            """, movieId, 25.0 + i, i + 1, start.toString(), end.toString());

			Long showtimeId = extractIdFromResponse(mockMvc.perform(post("/showtimes")
					.contentType(MediaType.APPLICATION_JSON)
					.content(showtimeJson)).andExpect(status().isOk()).andReturn());
			showtimeIds.add(showtimeId);
			showtimeBodies.add(showtimeJson);
		}

		// ====== Step 3: Add 8 Bookings (2 per showtime) ======
		for (int i = 0; i < showtimeIds.size(); i++) {
			Long showtimeId = showtimeIds.get(i);
			for (int seat = 1; seat <= 2; seat++) {
				String bookingJson = String.format("""
                    {
                        "showtimeId": %d,
                        "seatNumber": %d,
                        "userId": "%s"
                    }
                """, showtimeId, seat, UUID.randomUUID());

				mockMvc.perform(post("/bookings")
						.contentType(MediaType.APPLICATION_JSON)
						.content(bookingJson)).andExpect(status().isOk());
			}
		}

		// ====== Step 4: Update 1 Showtime (send full body) ======
		Long showtimeToUpdate = showtimeIds.get(0);
		JsonNode originalShowtime = objectMapper.readTree(showtimeBodies.get(0));
		String updatedShowtime = String.format("""
            {
                "movieId": %d,
                "price": %.2f,
                "theater": "%s",
                "startTime": "%s",
                "endTime": "%s"
            }
        """,
				originalShowtime.get("movieId").asLong(),
				99.99,  // update price only
				originalShowtime.get("theater").asText(),
				originalShowtime.get("startTime").asText(),
				originalShowtime.get("endTime").asText()
		);

		mockMvc.perform(post("/showtimes/update/" + showtimeToUpdate)
				.contentType(MediaType.APPLICATION_JSON)
				.content(updatedShowtime)).andExpect(status().isOk());

		// ====== Step 5: Delete all data by deleting the movies ======
		mockMvc.perform(delete("/movies/Interstellar")).andExpect(status().isOk());
		mockMvc.perform(delete("/movies/The Dark Knight")).andExpect(status().isOk());
	}

	private Long extractIdFromResponse(MvcResult result) throws Exception {
		String responseContent = result.getResponse().getContentAsString();
		JsonNode node = objectMapper.readTree(responseContent);
		return node.get("id").asLong();
	}
}
