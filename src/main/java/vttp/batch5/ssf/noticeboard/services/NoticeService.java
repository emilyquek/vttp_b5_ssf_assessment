package vttp.batch5.ssf.noticeboard.services;

import java.io.StringReader;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp.batch5.ssf.noticeboard.models.Notice;
import vttp.batch5.ssf.noticeboard.repositories.NoticeRepository;

@Service
public class NoticeService {

	@Autowired
	private NoticeRepository noticeRepo;

	@Value("${notice.publishing.server}")
	private String ENDPOINT_URL;

	public static final String ID_UUID = UUID.randomUUID().toString();

	// TODO: Task 3
	// You can change the signature of this method by adding any number of parameters
	// and return any type
	public String postToNoticeServer(Notice notice) {

		List<String> categoriesList = notice.getCategories();
		
		JsonArrayBuilder categoriesJsonBuilder = Json.createArrayBuilder();

		for (int i = 0; i < categoriesList.size(); i++) {
			categoriesJsonBuilder.add(categoriesList.get(i));
		}

		JsonArray categoriesJsonArray = categoriesJsonBuilder.build();

		JsonObject noticeObj = Json.createObjectBuilder()
			.add("title", notice.getTitle())
			.add("poster", notice.getPoster())
			.add("postDate", notice.getPostDate().getTime())
			.add("categories", categoriesJsonArray)
			.add("text", notice.getText())
			.build();

		RequestEntity<String> req = RequestEntity
			.post(ENDPOINT_URL)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.body(noticeObj.toString(), String.class);

		RestTemplate template = new RestTemplate();

		try {
			ResponseEntity<String> resp = template.exchange(req, String.class);
			String payload = resp.getBody();
			
			JsonReader reader = Json.createReader(new StringReader(payload));
			JsonObject result = reader.readObject();
	
			String id = result.getString("id");

			noticeRepo.insertNotices(id, payload);

			if (resp.getStatusCode().isError()) {
				return getResponseFail(payload);
			}

			return payload;

		} catch (Exception ex) {
			return ex.getMessage();
		}
	}

	public String getResponseSuccess(String json) {

		JsonReader reader = Json.createReader(new StringReader(json));
		JsonObject result = reader.readObject();

		String id = result.getString("id");
		String timestamp = result.getJsonNumber("timestamp").toString();

		String response = id + "," + timestamp;

		noticeRepo.insertNotices(id, response);

		return response;
	}

	public String getResponseFail(String json) {

		JsonReader reader = Json.createReader(new StringReader(json));
		JsonObject result = reader.readObject();

		String message = result.getString("message");
		String timestamp = result.getJsonNumber("timestamp").toString();

		String response = message + "," + timestamp;

		return response;
	}

	public boolean existingId(String id) {
		return noticeRepo.idExists(id);
	}

	public String getRandomId() {
		return noticeRepo.getRandomId();
	}
}
