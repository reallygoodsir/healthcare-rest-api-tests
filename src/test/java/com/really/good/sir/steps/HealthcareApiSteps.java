package com.really.good.sir.steps;

import com.really.good.sir.config.ConfigLoader;
import com.really.good.sir.dto.DoctorDTO;
import com.really.good.sir.dto.PatientDTO;
import com.really.good.sir.dto.ServiceDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.path.json.JsonPath;

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
        assertThat(response.getBody().asString(), not(isEmptyString()));

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
        List<String> fieldList = Arrays.asList(fields.split(",\\s*"));
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
        doctorPhone = randomPhoneNumber(9);
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
        patientPhone = randomPhoneNumber(9);
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

    private String randomLetters(int length) {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(letters.charAt(random.nextInt(letters.length())));
        }
        return sb.toString();
    }


    private String randomPhoneNumber(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        sb.append(random.nextInt(9) + 1);
        for (int i = 1; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
