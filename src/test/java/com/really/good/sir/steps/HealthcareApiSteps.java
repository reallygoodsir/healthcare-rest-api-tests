package com.really.good.sir.steps;

import com.really.good.sir.config.ConfigLoader;
import com.really.good.sir.dto.DoctorDTO;
import com.really.good.sir.dto.PatientDTO;
import com.really.good.sir.dto.ServiceDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.really.good.sir.dto.UserSessionDTO;
import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.path.json.JsonPath;
import org.hamcrest.Matchers;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.is;

import java.util.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class HealthcareApiSteps {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String baseUrl;
    private String sessionId;
    private Response response;
    private String requestBody;
    private String serviceName;
    private Integer servicePrice;
    private String doctorFirstName;
    private String doctorLastName;
    private String doctorEmail;
    private String doctorPhone;
    private Integer doctorSpecializationId;
    private String patientFirstName;
    private String patientLastName;
    private String patientEmail;
    private String patientPhone;
    private String patientAddress;
    private String patientDateOfBirth;
    private Integer lastServiceId;
    private String originalPatientEmail;
    private String originalPatientPhone;
    private Integer existingPatientId;
    private String otherPatientEmail;
    private String otherPatientPhone;

    @Given("The correct API URL")
    public void the_api_base_url_is() {
        this.baseUrl = ConfigLoader.get("api.base.url");
        RestAssured.baseURI = this.baseUrl;
    }

    @Given("The Login endpoint is {string}")
    public void the_login_endpoint_is(String endpoint) {
        this.baseUrl += endpoint;
    }

    @Given("I have admin credentials {string} and {string}")
    public void i_have_admin_credentials(String email, String password) {
        this.requestBody = String.format("{\"email\":\"%s\", \"password\":\"%s\"}", email, password);
    }

    @When("I send a POST request to login")
    public void i_send_a_post_request_to_login() {
        response = given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .post(baseUrl)
                .then().extract().response();

        if (response.getCookie("session_id") != null)
            sessionId = response.getCookie("session_id");
    }

    @Then("The response status code should be {int}")
    public void the_response_status_code_should_be(Integer code) {
        assertThat("Unexpected status code", response.statusCode(), equalTo(code));
    }

    @Then("The session cookie {string} should exist and not be empty")
    public void the_session_cookie_should_exist(String cookieName) {
        String cookie = response.getCookie(cookieName);
        assertThat(cookieName + " cookie should exist", cookie, notNullValue());
        assertThat(cookieName + " cookie should not be empty", cookie.isEmpty(), is(false));
        sessionId = cookie;
    }

    @Then("The response should contain role {string}")
    public void the_response_should_contain_role(String role) {
        assertThat(response.jsonPath().getString("role"), equalTo(role));
    }

    @When("I create new service")
    public void i_create_new_service() throws JsonProcessingException {
        serviceName = "ServiceTest" + randomLetters(6);
        servicePrice = new Random().nextInt(901) + 100;

        ServiceDTO serviceDTO = new ServiceDTO();
        serviceDTO.setName(serviceName);
        serviceDTO.setPrice(servicePrice);

        requestBody = objectMapper.writeValueAsString(serviceDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/services")
                .then().extract().response();

        // Save last created service ID
        lastServiceId = response.jsonPath().getInt("id");
    }

    @When("I send a POST request to {string} with body:")
    public void i_send_a_post_request_with_body(String endpoint, String body) {
        requestBody = body;
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(body)
                .post(RestAssured.baseURI + endpoint)
                .then().extract().response();
    }

    @Then("The response JSON should be valid")
    public void the_response_json_should_be_valid() {
        try {
            response.then().contentType("application/json");
            JsonPath jsonPath = response.jsonPath();
            assertThat("Response should contain JSON data", jsonPath.getMap("$"), is(not(anEmptyMap())));
        } catch (Exception e) {
            throw new AssertionError("Response is not valid JSON: " + e.getMessage());
        }
    }

    @Then("The service response has correct data")
    public void the_service_response_has_correct_data() throws JsonProcessingException {
        final ServiceDTO serviceDTO = objectMapper.readValue(response.asString(), ServiceDTO.class);

        assertThat("Service id is incorrect", serviceDTO.getId(), greaterThan(0));
        assertThat("Service name is incorrect", serviceDTO.getName(), equalTo(serviceName));
        assertThat("Service price is incorrect", serviceDTO.getPrice(), equalTo(servicePrice));
    }

    @Then("The service response has correct data after update")
    public void the_service_response_has_correct_data_after_update() throws JsonProcessingException {
        final ServiceDTO serviceDTO = objectMapper.readValue(response.asString(), ServiceDTO.class);

        assertThat("Service id is incorrect", serviceDTO.getId(), equalTo(lastServiceId));
        assertThat("Service name is incorrect", serviceDTO.getName(), equalTo(serviceName));
        assertThat("Service price is incorrect", serviceDTO.getPrice(), equalTo(servicePrice));
    }

    @Then("The field {string} should exist and not be null")
    public void the_field_should_exist_and_not_be_null(String field) {
        Object value = response.jsonPath().get(field);
        assertThat("Field '" + field + "' should exist", value, notNullValue());
    }

    @Then("The field {string} should equal {string}")
    public void the_field_should_equal(String field, String expectedValue) {
        Object actualValue = response.jsonPath().get(field);
        assertThat("Mismatch for field: " + field, actualValue.toString(), equalTo(expectedValue));
    }

    @Then("The field {string} should equal {int}")
    public void the_field_should_equal(String field, Integer expectedValue) {
        Object actualValue = response.jsonPath().get(field);

        double actual;
        if (actualValue instanceof Number) {
            actual = ((Number) actualValue).doubleValue();
        } else {
            actual = Double.parseDouble(actualValue.toString());
        }

        assertThat("Mismatch for field: " + field, actual, equalTo(expectedValue.doubleValue()));
    }

    @Then("The response should be a valid JSON object for {string}")
    public void the_response_should_be_a_valid_json_object(String entityType) {
        assertThat(response.getBody().asString(), not(Matchers.notNullValue()));

        Map<String, Object> json = response.jsonPath().getMap("$");
        assertThat("Response should be a JSON object", json, is(notNullValue()));
        assertThat("Response should contain an id", json.get("id"), is(notNullValue()));

        switch (entityType.toLowerCase()) {
            case "doctor":
                assertThat("firstName should not be null", json.get("firstName"), is(notNullValue()));
                assertThat("lastName should not be null", json.get("lastName"), is(notNullValue()));
                assertThat("Doctor should have email", json.get("email"), notNullValue());
                assertThat("Doctor should have phone", json.get("phone"), notNullValue());
                assertThat("Doctor specializationId should exist", json.get("specializationId"), notNullValue());
                break;

            case "patient":
                assertThat("firstName should not be null", json.get("firstName"), is(notNullValue()));
                assertThat("lastName should not be null", json.get("lastName"), is(notNullValue()));
                assertThat("Patient should have email", json.get("email"), notNullValue());
                assertThat("Patient should have phone", json.get("phone"), notNullValue());
                assertThat("Patient should have address", json.get("address"), notNullValue());
                assertThat("Patient should have dateOfBirth", json.get("dateOfBirth"), notNullValue());
                break;

            case "service":
                assertThat("Service should have name", json.get("name"), notNullValue());
                assertThat("Service should have price", json.get("price"), notNullValue());
                break;

            default:
                throw new IllegalArgumentException("Unknown entity type: " + entityType);
        }
    }

    @Then("The response should match the request body for fields {string}")
    public void the_response_should_match_the_request_body_for_fields(String fields) {
        String[] fieldList = fields.split(",\\s*");
        Map<String, Object> responseJson = response.jsonPath().getMap("$");
        Map<String, Object> requestJson = JsonPath.from(requestBody).getMap("$");

        for (String field : fieldList) {
            Object expected = requestJson.get(field);
            Object actual = responseJson.get(field);
            assertThat("Field " + field + " should match between request and response", actual, equalTo(expected));
        }
    }

    @When("I create new doctor")
    public void i_create_new_doctor() throws JsonProcessingException {
        doctorFirstName = "DoctorTest" + randomLetters(10);
        doctorLastName = "Medic" + randomLetters(10);
        doctorEmail = (doctorFirstName + doctorLastName).toLowerCase() + "@gmail.com";
        doctorPhone = randomPhoneNumber();
        doctorSpecializationId = 1;

        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setFirstName(doctorFirstName);
        doctorDTO.setLastName(doctorLastName);
        doctorDTO.setEmail(doctorEmail);
        doctorDTO.setPhone(doctorPhone);
        doctorDTO.setSpecializationId(doctorSpecializationId);

        requestBody = objectMapper.writeValueAsString(doctorDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }

    @Then("The doctor response has correct data")
    public void the_doctor_response_has_correct_data() throws JsonProcessingException {
        final DoctorDTO doctorDTO = objectMapper.readValue(response.asString(), DoctorDTO.class);

        assertThat("Doctor id is incorrect", doctorDTO.getId(), greaterThan(0));
        assertThat("Doctor firstName mismatch", doctorDTO.getFirstName(), equalTo(doctorFirstName));
        assertThat("Doctor lastName mismatch", doctorDTO.getLastName(), equalTo(doctorLastName));
        assertThat("Doctor email mismatch", doctorDTO.getEmail(), equalTo(doctorEmail));
        assertThat("Doctor phone mismatch", doctorDTO.getPhone(), equalTo(doctorPhone));
        assertThat("Doctor specializationId mismatch", doctorDTO.getSpecializationId(), equalTo(doctorSpecializationId));
    }

    @When("I create new patient")
    public void i_create_new_patient() throws JsonProcessingException {
        patientFirstName = "PatientTest" + randomLetters(10);
        patientLastName = "User" + randomLetters(10);
        patientEmail = (patientFirstName + patientLastName).toLowerCase() + "@gmail.com";
        patientPhone = randomPhoneNumber();
        patientAddress = "123 Main Street";
        patientDateOfBirth = "1990-01-01";

        // Save originals only if not already set
        if (originalPatientEmail == null) originalPatientEmail = patientEmail;
        if (originalPatientPhone == null) originalPatientPhone = patientPhone;

        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setFirstName(patientFirstName);
        patientDTO.setLastName(patientLastName);
        patientDTO.setEmail(patientEmail);
        patientDTO.setPhone(patientPhone);
        patientDTO.setAddress(patientAddress);
        patientDTO.setDateOfBirth(patientDateOfBirth);

        requestBody = objectMapper.writeValueAsString(patientDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/patients")
                .then().extract().response();
    }

    @Then("The patient response has correct data")
    public void the_patient_response_has_correct_data() throws JsonProcessingException {
        final PatientDTO patientDTO = objectMapper.readValue(response.asString(), PatientDTO.class);

        assertThat("Patient id is incorrect", patientDTO.getId(), greaterThan(0));
        assertThat("Patient firstName mismatch", patientDTO.getFirstName(), equalTo(patientFirstName));
        assertThat("Patient lastName mismatch", patientDTO.getLastName(), equalTo(patientLastName));
        assertThat("Patient email mismatch", patientDTO.getEmail(), equalTo(patientEmail));
        assertThat("Patient phone mismatch", patientDTO.getPhone(), equalTo(patientPhone));
        assertThat("Patient address mismatch", patientDTO.getAddress(), equalTo(patientAddress));
        assertThat("Patient dateOfBirth mismatch", patientDTO.getDateOfBirth(), equalTo(patientDateOfBirth));
    }

    @Given("The Check Session endpoint is {string}")
    public void the_check_session_endpoint_is(String endpoint) {
        this.baseUrl = RestAssured.baseURI + endpoint;
    }

    @When("I send a POST request to check session")
    public void i_send_a_post_request_to_check_session() throws JsonProcessingException {
        Map<String, Object> body = new HashMap<>();
        body.put("sessionId", sessionId);

        requestBody = objectMapper.writeValueAsString(body);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(baseUrl)
                .then().extract().response();
    }

    @Then("The session should be valid and contain role {string}")
    public void the_session_should_be_valid_and_contain_role(String expectedRole) throws JsonProcessingException {
        final UserSessionDTO sessionDTO = objectMapper.readValue(response.asString(), UserSessionDTO.class);

        assertThat("Session id should be greater than 0", sessionDTO.getId(), greaterThan(0));
        assertThat("Credential id should be greater than 0", sessionDTO.getCredentialId(), greaterThan(0));
        assertThat("Role mismatch", sessionDTO.getRole(), equalTo(expectedRole));
        assertThat("Login date should not be null", sessionDTO.getLoginDateTime(), notNullValue());
    }

    @When("I send a DELETE request to log out")
    public void i_send_a_delete_request_to_log_out() {
        int sessionNumericId = response.jsonPath().getInt("id");

        response = given()
                .cookie("session_id", sessionId)
                .delete(RestAssured.baseURI + "/authorization/" + sessionNumericId)
                .then().extract().response();
    }

    @Then("I logged out successfully")
    public void i_logged_out_successfully() {
        assertThat("Expected 204 status", response.statusCode(), equalTo(204));

        String setCookieHeader = response.getHeader("Set-Cookie");
        assertThat("Set-Cookie header should exist", setCookieHeader, notNullValue());
        assertThat("Cookie should be expired", setCookieHeader, containsString("Max-Age=0"));
        assertThat("Cookie should be marked as deleted", setCookieHeader, containsString("Session deleted"));
    }

    @When("I update the service")
    public void i_update_the_service() throws JsonProcessingException {
        if (lastServiceId == null) {
            throw new AssertionError("No service ID saved to update");
        }

        serviceName = serviceName + "Updated";
        servicePrice = servicePrice + 100;

        ServiceDTO serviceDTO = new ServiceDTO();
        serviceDTO.setId(lastServiceId);
        serviceDTO.setName(serviceName);
        serviceDTO.setPrice(servicePrice);

        requestBody = objectMapper.writeValueAsString(serviceDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/services")
                .then().extract().response();
    }


    @Then("The service was updated successfully")
    public void the_service_was_updated_successfully() throws JsonProcessingException {
        the_response_status_code_should_be(200);
        the_response_json_should_be_valid();
        the_service_response_has_correct_data();
    }

    @When("I delete the service")
    public void i_delete_the_service() {
        if (lastServiceId == null) {
            throw new AssertionError("No service ID saved to delete");
        }

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .delete(RestAssured.baseURI + "/services/" + lastServiceId)
                .then().extract().response();
    }

    @When("I get all services")
    public void i_get_all_services() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .get(RestAssured.baseURI + "/services")
                .then().extract().response();

        the_response_status_code_should_be(200);
        the_response_json_should_be_valid_list();
    }

    public void the_response_json_should_be_valid_list() {
        try {
            response.then().contentType("application/json");
            List<?> list = response.jsonPath().getList("$");
            assertThat("Response should be a JSON array", list, is(notNullValue()));
        } catch (Exception e) {
            throw new AssertionError("Response is not a valid JSON array: " + e.getMessage());
        }
    }

    @Then("The service with id should exist")
    public void the_service_with_id_should_exist() {
        if (lastServiceId == null) {
            throw new AssertionError("No service ID saved to check existence");
        }

        Response getResponse = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .get(RestAssured.baseURI + "/services")
                .then().extract().response();

        List<Map<String, Object>> services = getResponse.jsonPath().getList("$");

        boolean found = services.stream()
                .anyMatch(s -> s.get("id").equals(lastServiceId));

        assertThat("Service with id " + lastServiceId + " should exist", found, is(true));
    }

    @Then("The service with id should not exist")
    public void the_service_with_id_should_not_exist() {
        if (lastServiceId == null) {
            throw new AssertionError("No service ID saved to check non-existence");
        }

        Response getResponse = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .get(RestAssured.baseURI + "/services")
                .then().extract().response();

        List<Map<String, Object>> services = getResponse.jsonPath().getList("$");

        boolean found = services.stream()
                .anyMatch(s -> s.get("id").equals(lastServiceId));

        assertThat("Service with id " + lastServiceId + " should NOT exist", found, is(false));
    }

    @When("I create new patient without a session")
    public void i_create_new_patient_without_session() throws JsonProcessingException {
        patientFirstName = "PatientTest" + randomLetters(10);
        patientLastName = "User" + randomLetters(10);
        patientEmail = (patientFirstName + patientLastName).toLowerCase() + "@gmail.com";
        patientPhone = randomPhoneNumber();
        patientAddress = "123 Main Street";
        patientDateOfBirth = "1990-01-01";

        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setFirstName(patientFirstName);
        patientDTO.setLastName(patientLastName);
        patientDTO.setEmail(patientEmail);
        patientDTO.setPhone(patientPhone);
        patientDTO.setAddress(patientAddress);
        patientDTO.setDateOfBirth(patientDateOfBirth);

        requestBody = objectMapper.writeValueAsString(patientDTO);

        response = given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .post(RestAssured.baseURI + "/patients")
                .then().extract().response();
    }

    @Then("The response should contain message {string}")
    public void the_response_should_contain_message(String expectedMessage) {
        String actualMessage = response.jsonPath().getString("message");
        assertThat("Response message mismatch: " + actualMessage, actualMessage, equalTo(expectedMessage));
    }

    @When("I create new patient with an invalid session_id")
    public void i_create_new_patient_with_invalid_session_id() throws JsonProcessingException {
        // Valid patient data, invalid session cookie
        patientFirstName = "PatientTest" + randomLetters(10);
        patientLastName = "User" + randomLetters(10);
        patientEmail = (patientFirstName + patientLastName).toLowerCase() + "@gmail.com";
        patientPhone = randomPhoneNumber();
        patientAddress = "456 Fake Street";
        patientDateOfBirth = "1990-01-01";

        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setFirstName(patientFirstName);
        patientDTO.setLastName(patientLastName);
        patientDTO.setEmail(patientEmail);
        patientDTO.setPhone(patientPhone);
        patientDTO.setAddress(patientAddress);
        patientDTO.setDateOfBirth(patientDateOfBirth);

        requestBody = objectMapper.writeValueAsString(patientDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", "999999") // invalid numeric id
                .body(requestBody)
                .post(RestAssured.baseURI + "/patients")
                .then().extract().response();
    }

    @When("I create new patient with empty first name")
    public void i_create_new_patient_with_empty_first_name() throws JsonProcessingException {
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setFirstName(""); // invalid
        patientDTO.setLastName("User" + randomLetters(6));
        patientDTO.setEmail("invalidfirst@gmail.com");
        patientDTO.setPhone(randomPhoneNumber());
        patientDTO.setAddress("123 Main Street");
        patientDTO.setDateOfBirth("1990-01-01");

        requestBody = objectMapper.writeValueAsString(patientDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/patients")
                .then().extract().response();
    }

    @When("I create new patient with an empty last name")
    public void i_create_new_patient_with_empty_last_name() throws JsonProcessingException {
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setFirstName("Patient" + randomLetters(6));
        patientDTO.setLastName(""); // invalid
        patientDTO.setEmail("invalidlast@gmail.com");
        patientDTO.setPhone(randomPhoneNumber());
        patientDTO.setAddress("123 Main Street");
        patientDTO.setDateOfBirth("1990-01-01");

        requestBody = objectMapper.writeValueAsString(patientDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/patients")
                .then().extract().response();
    }

    @When("I create new patient without an address")
    public void i_create_new_patient_without_an_address() throws JsonProcessingException {
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setFirstName("Patient" + randomLetters(6));
        patientDTO.setLastName("User" + randomLetters(6));
        patientDTO.setEmail("noaddress@gmail.com");
        patientDTO.setPhone(randomPhoneNumber());
        patientDTO.setAddress(""); // invalid
        patientDTO.setDateOfBirth("1990-01-01");

        requestBody = objectMapper.writeValueAsString(patientDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/patients")
                .then().extract().response();
    }

    @When("I create new patient with an invalid birth date")
    public void i_create_new_patient_with_an_invalid_birth_date() throws JsonProcessingException {
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setFirstName("Patient" + randomLetters(6));
        patientDTO.setLastName("User" + randomLetters(6));
        patientDTO.setEmail("invaliddate@gmail.com");
        patientDTO.setPhone(randomPhoneNumber());
        patientDTO.setAddress("456 Street");
        patientDTO.setDateOfBirth("2050-01-01"); // 🚫 future date

        requestBody = objectMapper.writeValueAsString(patientDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/patients")
                .then().extract().response();
    }

    @When("I create new patient with an empty email")
    public void i_create_new_patient_with_empty_email() throws JsonProcessingException {
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setFirstName("Patient" + randomLetters(6));
        patientDTO.setLastName("User" + randomLetters(6));
        patientDTO.setEmail(""); // empty email
        patientDTO.setPhone(randomPhoneNumber());
        patientDTO.setAddress("123 Main Street");
        patientDTO.setDateOfBirth("1990-01-01");

        requestBody = objectMapper.writeValueAsString(patientDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/patients")
                .then().extract().response();
    }

    @When("I create new patient with an invalid email format")
    public void i_create_new_patient_with_invalid_email_format() throws JsonProcessingException {
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setFirstName("Patient" + randomLetters(6));
        patientDTO.setLastName("User" + randomLetters(6));
        patientDTO.setEmail("invalid-email"); // invalid format
        patientDTO.setPhone(randomPhoneNumber());
        patientDTO.setAddress("123 Main Street");
        patientDTO.setDateOfBirth("1990-01-01");

        requestBody = objectMapper.writeValueAsString(patientDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/patients")
                .then().extract().response();
    }

    @When("I create new patient with a duplicate email")
    public void i_create_new_patient_with_duplicate_email() throws JsonProcessingException {
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setFirstName("Patient" + randomLetters(6));
        patientDTO.setLastName("User" + randomLetters(6));
        patientDTO.setEmail(originalPatientEmail); // use original
        patientDTO.setPhone(randomPhoneNumber());
        patientDTO.setAddress("123 Main Street");
        patientDTO.setDateOfBirth("1990-01-01");

        requestBody = objectMapper.writeValueAsString(patientDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/patients")
                .then().extract().response();
    }

    @When("I create new patient with an empty phone")
    public void i_create_new_patient_with_empty_phone() throws JsonProcessingException {
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setFirstName("Patient" + randomLetters(6));
        patientDTO.setLastName("User" + randomLetters(6));
        patientDTO.setEmail("unique" + randomLetters(6) + "@gmail.com");
        patientDTO.setPhone(""); // empty phone
        patientDTO.setAddress("123 Main Street");
        patientDTO.setDateOfBirth("1990-01-01");

        requestBody = objectMapper.writeValueAsString(patientDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/patients")
                .then().extract().response();
    }

    @When("I create new patient with an invalid phone format")
    public void i_create_new_patient_with_invalid_phone_format() throws JsonProcessingException {
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setFirstName("Patient" + randomLetters(6));
        patientDTO.setLastName("User" + randomLetters(6));
        patientDTO.setEmail("unique" + randomLetters(6) + "@gmail.com");
        patientDTO.setPhone("123abc456"); // invalid phone format
        patientDTO.setAddress("123 Main Street");
        patientDTO.setDateOfBirth("1990-01-01");

        requestBody = objectMapper.writeValueAsString(patientDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/patients")
                .then().extract().response();
    }

    @When("I create new patient with a duplicate phone")
    public void i_create_new_patient_with_duplicate_phone() throws JsonProcessingException {
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setFirstName("Patient" + randomLetters(6));
        patientDTO.setLastName("User" + randomLetters(6));
        patientDTO.setEmail("unique" + randomLetters(6) + "@gmail.com");
        patientDTO.setPhone(originalPatientPhone);
        patientDTO.setAddress("123 Main Street");
        patientDTO.setDateOfBirth("1990-01-01");

        requestBody = objectMapper.writeValueAsString(patientDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/patients")
                .then().extract().response();
    }

    @When("I update the patient's first name")
    public void i_update_the_patient_first_name() throws JsonProcessingException {
        patientFirstName = "Updated" + randomLetters(6);

        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setId(existingPatientId);
        patientDTO.setFirstName(patientFirstName);
        patientDTO.setLastName(patientLastName);
        patientDTO.setEmail(patientEmail); // same email allowed
        patientDTO.setPhone(patientPhone); // same phone allowed
        patientDTO.setAddress(patientAddress);
        patientDTO.setDateOfBirth(patientDateOfBirth);

        requestBody = objectMapper.writeValueAsString(patientDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/patients")
                .then().extract().response();
    }

    @When("I update the patient with an email which exists")
    public void i_update_patient_with_duplicate_email() throws JsonProcessingException {
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setId(existingPatientId);
        patientDTO.setFirstName("PatientDup");
        patientDTO.setLastName("UserDup");
        patientDTO.setEmail(otherPatientEmail); // email of a different patient
        patientDTO.setPhone(randomPhoneNumber());
        patientDTO.setAddress("123 Main Street");
        patientDTO.setDateOfBirth("1990-01-01");

        requestBody = objectMapper.writeValueAsString(patientDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/patients")
                .then().extract().response();
    }

    @When("I update the patient without a session")
    public void i_update_the_patient_without_session() throws JsonProcessingException {
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setId(existingPatientId);
        patientDTO.setFirstName(patientFirstName);
        patientDTO.setLastName(patientLastName);
        patientDTO.setEmail(patientEmail);
        patientDTO.setPhone(patientPhone);
        patientDTO.setAddress(patientAddress);
        patientDTO.setDateOfBirth(patientDateOfBirth);

        requestBody = objectMapper.writeValueAsString(patientDTO);

        response = given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .put(RestAssured.baseURI + "/patients")
                .then().extract().response();
    }

    @When("I update the patient with an invalid session_id")
    public void i_update_the_patient_with_invalid_session_id() throws JsonProcessingException {
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setId(existingPatientId);
        patientDTO.setFirstName(patientFirstName);
        patientDTO.setLastName(patientLastName);
        patientDTO.setEmail(patientEmail);
        patientDTO.setPhone(patientPhone);
        patientDTO.setAddress(patientAddress);
        patientDTO.setDateOfBirth(patientDateOfBirth);

        requestBody = objectMapper.writeValueAsString(patientDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", "999999") // invalid
                .body(requestBody)
                .put(RestAssured.baseURI + "/patients")
                .then().extract().response();
    }

    @When("I update the patient with empty first name")
    public void i_update_patient_with_empty_first_name() throws JsonProcessingException {
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setId(existingPatientId);
        patientDTO.setFirstName(""); // invalid
        patientDTO.setLastName(patientLastName);
        patientDTO.setEmail(patientEmail);
        patientDTO.setPhone(patientPhone);
        patientDTO.setAddress(patientAddress);
        patientDTO.setDateOfBirth(patientDateOfBirth);

        requestBody = objectMapper.writeValueAsString(patientDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/patients")
                .then().extract().response();
    }

    @When("I update the patient with an empty last name")
    public void i_update_patient_with_empty_last_name() throws JsonProcessingException {
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setId(existingPatientId);
        patientDTO.setFirstName(patientFirstName);
        patientDTO.setLastName(""); // invalid
        patientDTO.setEmail(patientEmail);
        patientDTO.setPhone(patientPhone);
        patientDTO.setAddress(patientAddress);
        patientDTO.setDateOfBirth(patientDateOfBirth);

        requestBody = objectMapper.writeValueAsString(patientDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/patients")
                .then().extract().response();
    }

    @When("I update the patient without an address")
    public void i_update_patient_without_address() throws JsonProcessingException {
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setId(existingPatientId);
        patientDTO.setFirstName(patientFirstName);
        patientDTO.setLastName(patientLastName);
        patientDTO.setEmail(patientEmail);
        patientDTO.setPhone(patientPhone);
        patientDTO.setAddress(""); // invalid
        patientDTO.setDateOfBirth(patientDateOfBirth);

        requestBody = objectMapper.writeValueAsString(patientDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/patients")
                .then().extract().response();
    }

    @When("I update the patient with an invalid birth date")
    public void i_update_patient_with_invalid_birth_date() throws JsonProcessingException {
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setId(existingPatientId);
        patientDTO.setFirstName(patientFirstName);
        patientDTO.setLastName(patientLastName);
        patientDTO.setEmail(patientEmail);
        patientDTO.setPhone(patientPhone);
        patientDTO.setAddress(patientAddress);
        patientDTO.setDateOfBirth("2050-01-01"); // invalid future date

        requestBody = objectMapper.writeValueAsString(patientDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/patients")
                .then().extract().response();
    }

    @When("I update the patient with an empty email")
    public void i_update_patient_with_empty_email() throws JsonProcessingException {
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setId(existingPatientId);
        patientDTO.setFirstName(patientFirstName);
        patientDTO.setLastName(patientLastName);
        patientDTO.setEmail(""); // invalid
        patientDTO.setPhone(patientPhone);
        patientDTO.setAddress(patientAddress);
        patientDTO.setDateOfBirth(patientDateOfBirth);

        requestBody = objectMapper.writeValueAsString(patientDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/patients")
                .then().extract().response();
    }

    @When("I update the patient with an invalid email format")
    public void i_update_patient_with_invalid_email_format() throws JsonProcessingException {
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setId(existingPatientId);
        patientDTO.setFirstName(patientFirstName);
        patientDTO.setLastName(patientLastName);
        patientDTO.setEmail("invalid-email"); // invalid format
        patientDTO.setPhone(patientPhone);
        patientDTO.setAddress(patientAddress);
        patientDTO.setDateOfBirth(patientDateOfBirth);

        requestBody = objectMapper.writeValueAsString(patientDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/patients")
                .then().extract().response();
    }

    @When("I update the patient with an empty phone")
    public void i_update_patient_with_empty_phone() throws JsonProcessingException {
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setId(existingPatientId);
        patientDTO.setFirstName(patientFirstName);
        patientDTO.setLastName(patientLastName);
        patientDTO.setEmail(patientEmail);
        patientDTO.setPhone(""); // invalid
        patientDTO.setAddress(patientAddress);
        patientDTO.setDateOfBirth(patientDateOfBirth);

        requestBody = objectMapper.writeValueAsString(patientDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/patients")
                .then().extract().response();
    }

    @When("I update the patient with an invalid phone format")
    public void i_update_patient_with_invalid_phone_format() throws JsonProcessingException {
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setId(existingPatientId);
        patientDTO.setFirstName(patientFirstName);
        patientDTO.setLastName(patientLastName);
        patientDTO.setEmail(patientEmail);
        patientDTO.setPhone("123abc456"); // invalid
        patientDTO.setAddress(patientAddress);
        patientDTO.setDateOfBirth(patientDateOfBirth);

        requestBody = objectMapper.writeValueAsString(patientDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/patients")
                .then().extract().response();
    }

    @When("I update the patient with an existing phone")
    public void i_update_patient_with_duplicate_phone() throws JsonProcessingException {
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setId(existingPatientId);
        patientDTO.setFirstName(patientFirstName);
        patientDTO.setLastName(patientLastName);
        patientDTO.setEmail(patientEmail);
        patientDTO.setPhone(otherPatientPhone); // phone of a different patient
        patientDTO.setAddress(patientAddress);
        patientDTO.setDateOfBirth(patientDateOfBirth);

        requestBody = objectMapper.writeValueAsString(patientDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/patients")
                .then().extract().response();
    }

    @Given("Another patient exists for duplicate tests")
    public void another_patient_exists_for_duplicate_tests() throws JsonProcessingException {
        if (sessionId == null) {
            throw new IllegalStateException("Admin must be logged in first!");
        }
        long timestamp = System.currentTimeMillis();
        String uniqueEmail = "duplicate+" + timestamp + "@example.com";
        String uniquePhone = "555" + (1000000 + (timestamp % 9000000)); // ensures 7 digits

        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setFirstName("DupTest");
        patientDTO.setLastName("Patient");
        patientDTO.setEmail(uniqueEmail);
        patientDTO.setPhone(uniquePhone);
        patientDTO.setAddress("123 Dup Street");
        patientDTO.setDateOfBirth("1990-01-01");

        String requestBody = objectMapper.writeValueAsString(patientDTO);

        Response dupResponse = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/patients")
                .then()
                .extract()
                .response();
        Object idObj = dupResponse.jsonPath().get("id");
        if (idObj == null) {
            throw new IllegalStateException("Response did not contain 'id': " + dupResponse.asString());
        }
        otherPatientEmail = patientDTO.getEmail();
        otherPatientPhone = patientDTO.getPhone();
    }


    @Given("An existing patient is available")
    public void an_existing_patient_is_available() throws JsonProcessingException {
        i_create_new_patient(); // reuse creation step
        existingPatientId = response.jsonPath().getInt("id"); // save ID for update
    }

    private String randomLetters(int length) {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(letters.charAt(random.nextInt(letters.length())));
        }
        return sb.toString();
    }

    private String randomPhoneNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        sb.append(random.nextInt(9) + 1);
        for (int i = 1; i < 9; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
