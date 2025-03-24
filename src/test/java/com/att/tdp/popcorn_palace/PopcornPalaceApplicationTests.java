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

/*
"Scenario test".
Add stage:
In this test the program will add two movies.
then connects two showtime to each movie (total 4).
then connects two bookings to each showtime (total 8).

Update check:
After, one of the showtime will be updated by its price.

Delete data:
Then, a deletion of the whole data would proceed via the delete requests of the movies.

all requests shall be handled, response of Ok shall be received.
 */

@SpringBootTest
@AutoConfigureMockMvc
public class PopcornPalaceApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void endToEndTest() throws Exception {
		/*             Add stage                     */
		// Add 2 Movie
		String movie1Json = """
            { "title": "X-Men", "genre": "Action", "duration": 120, "rating": 9.9, "releaseYear": 2000 }
        """;
		String movie2Json = """
            { "title": "X2", "genre": "Action", "duration": 120, "rating": 9.8, "releaseYear": 2003 }
        """;

		Long movie1Id = extractIdFromResponse(mockMvc.perform(post("/movies")
				.contentType(MediaType.APPLICATION_JSON)
				.content(movie1Json)).andExpect(status().isOk()).andReturn());

		Long movie2Id = extractIdFromResponse(mockMvc.perform(post("/movies")
				.contentType(MediaType.APPLICATION_JSON)
				.content(movie2Json)).andExpect(status().isOk()).andReturn());

		// Add 4 Showtime, two for movie1Id and two for movie2Id
		List<Long> showtimeIds = new ArrayList<>();
		List<String> showtimeBodies = new ArrayList<>();
		Instant now = Instant.now();
		Long movieId;
		int[] durationsInHours = {1, 2, 3, 1};
		for (int i = 0; i < 4; i++) {
			Instant start = now.plusSeconds((i+10) * 3600L); // (i+10) otherwise the program would terminate at i = 0
			Instant end = start.plusSeconds(durationsInHours[i] * 3600L);
			if (i < 2){
				movieId = movie1Id;
			}
			else{
				movieId = movie2Id;
			}

			String showtimeJson = String.format("""
                {
                    "movieId": %d,
                    "price": %.2f,
                    "theater": "Theater-%d",
                    "startTime": "%s",
                    "endTime": "%s"
                }
            """, movieId, 25.0, i, start.toString(), end.toString());

			Long showtimeId = extractIdFromResponse(mockMvc.perform(post("/showtimes")
					.contentType(MediaType.APPLICATION_JSON)
					.content(showtimeJson)).andExpect(status().isOk()).andReturn());
			showtimeIds.add(showtimeId);
			showtimeBodies.add(showtimeJson);
		}

		// Add 8 Bookings, two for each Showtime
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

		/*             Update stage                     */
		// update the first showtime price to 99.0, have to extract and send request of the whole showtime since API format requests
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

		/*             Delete stage                     */
		// delete all entities via deletion requests for the movies.
		mockMvc.perform(delete("/movies/X-Men")).andExpect(status().isOk());
		mockMvc.perform(delete("/movies/X2")).andExpect(status().isOk());
	}

	private Long extractIdFromResponse(MvcResult result) throws Exception {
		String responseContent = result.getResponse().getContentAsString();
		JsonNode node = objectMapper.readTree(responseContent);
		return node.get("id").asLong();
	}
}
