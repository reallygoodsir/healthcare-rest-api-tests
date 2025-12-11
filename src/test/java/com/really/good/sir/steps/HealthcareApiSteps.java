package com.really.good.sir.steps;

import com.really.good.sir.config.ConfigLoader;
import com.really.good.sir.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.cienvironment.internal.com.eclipsesource.json.Json;
import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.path.json.JsonPath;
import org.hamcrest.Matchers;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.is;

import java.time.LocalDate;
import java.util.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

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
    private Integer appointmentId;
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
    private String otherDoctorPhone;
    private String otherDoctorEmail;
    private Integer patientId;
    private Integer lastDoctorId;
    private Integer existingDoctorId;
    private Integer lastScheduleId;
    private Integer overlapScheduleId;
    private Integer staleDoctorId;
    private String staleDoctorFirstName;
    private String staleDoctorLastName;
    private String staleDoctorEmail;
    private String staleDoctorPhone;
    private Integer staleDoctorSpecializationId;
    private String staleCredentialId;
    private byte[] staleDoctorPhoto;
    private Integer lastSpecializationId;

    @Given("The correct API URL")
    public void the_api_base_url_is() {
        this.baseUrl = ConfigLoader.get("api.base.url");
        RestAssured.baseURI = this.baseUrl;
    }

    @Given("The Login endpoint is {string}")
    public void the_login_endpoint_is(String endpoint) {
        this.baseUrl += endpoint;
    }

    @Given("I have credentials {string} and {string}")
    public void i_have_credentials(String email, String password) {
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

    @And("The credential id should be saved")
    public void the_credential_id_should_be_saved() {
        staleCredentialId = response.jsonPath().getString("credentialId");
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

    @Then("The response JSON should be a valid list")
    public void the_response_json_should_be_a_valid_list() {
        try {
            response.then().contentType("application/json");
            JsonPath jsonPath = response.jsonPath();

            List<Object> list = jsonPath.getList("$");

            assertThat("Response should contain a JSON list", list, is(not(empty())));
        } catch (Exception e) {
            throw new AssertionError("Response is not a valid JSON list: " + e.getMessage());
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
        doctorSpecializationId = 2;
        byte[] doctorPhoto = "dummy photo content".getBytes();

        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setFirstName(doctorFirstName);
        doctorDTO.setLastName(doctorLastName);
        doctorDTO.setEmail(doctorEmail);
        doctorDTO.setPhone(doctorPhone);
        doctorDTO.setSpecializationId(doctorSpecializationId);
        doctorDTO.setPhoto(doctorPhoto);

        requestBody = objectMapper.writeValueAsString(doctorDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/doctors")
                .then().extract().response();

        Object idObj = response.jsonPath().get("id");

        if (idObj == null) {
            throw new IllegalStateException("Doctor creation failed, no id returned: " + response.asString());
        }


        staleDoctorFirstName = doctorFirstName;
        staleDoctorLastName = doctorLastName;
        staleDoctorEmail = doctorEmail;
        staleDoctorPhone = doctorPhone;
        staleDoctorSpecializationId = doctorSpecializationId;
        staleDoctorPhoto = doctorPhoto;
        staleDoctorId = (Integer) idObj;
        lastDoctorId = ((Number) idObj).intValue();
        existingDoctorId = lastDoctorId;
    }


    @Then("The doctor update response has correct data")
    public void the_doctor_update_response_has_correct_data() throws JsonProcessingException {
        final DoctorDTO doctorDTO = objectMapper.readValue(response.asString(), DoctorDTO.class);

        assertThat("Doctor id is incorrect", doctorDTO.getId(), equalTo(staleDoctorId));
        assertThat("Doctor firstName mismatch", doctorDTO.getFirstName(), equalTo(doctorFirstName));
        assertThat("Doctor lastName mismatch", doctorDTO.getLastName(), equalTo(doctorLastName));
        assertThat("Doctor email mismatch", doctorDTO.getEmail(), equalTo(doctorEmail));
        assertThat("Doctor phone mismatch", doctorDTO.getPhone(), equalTo(doctorPhone));
        assertThat("Doctor specializationId mismatch", doctorDTO.getSpecializationId(), equalTo(doctorSpecializationId));
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

    @When("I clear the session")
    public void i_clear_the_session() {
        sessionId = null;
    }

    @Then("The session should be empty")
    public void the_session_should_be_empty() {
        if (sessionId != null) {
            throw new IllegalStateException("Session was not cleared!");
        }
    }

    @When("I find the patient by phone number")
    public void i_find_the_patient_by_phone_number() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .get(RestAssured.baseURI + "/patients/visits/" + patientPhone)
                .then().extract().response();
    }

    @Then("The response JSON should contain the patient data")
    public void response_json_should_contain_patient_data() {
        response.then().body("firstName", equalTo(patientFirstName));
        response.then().body("lastName", equalTo(patientLastName));
        response.then().body("email", equalTo(patientEmail));
    }

    // --- CREATE APPOINTMENT ---
    @When("I create an appointment")
    public void i_create_an_appointment() throws JsonProcessingException {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setDoctorId(lastDoctorId);
        dto.setPatientId(existingPatientId);
        dto.setScheduleId(lastScheduleId);

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/appointments")
                .then().extract().response();
        Object idObj = response.jsonPath().get("appointmentId");
        if (idObj != null) {
            appointmentId = ((Number) idObj).intValue();
        }
    }

    @When("I create an appointment without a session")
    public void i_create_appointment_without_session() throws JsonProcessingException {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setDoctorId(lastDoctorId);
        dto.setPatientId(existingPatientId);
        dto.setScheduleId(lastScheduleId);

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .post(RestAssured.baseURI + "/appointments")
                .then().extract().response();
    }

    @When("I create an appointment with an invalid session_id")
    public void i_create_appointment_invalid_session() throws JsonProcessingException {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setDoctorId(lastDoctorId);
        dto.setPatientId(existingPatientId);
        dto.setScheduleId(lastScheduleId);

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", "999999")
                .body(requestBody)
                .post(RestAssured.baseURI + "/appointments")
                .then().extract().response();
    }

    @When("I create an appointment with an invalid schedule id")
    public void i_create_appointment_invalid_schedule() throws JsonProcessingException {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setDoctorId(lastDoctorId);
        dto.setPatientId(existingPatientId);
        dto.setScheduleId(99999); // invalid

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/appointments")
                .then().extract().response();
    }

    @When("I create an appointment without schedule id")
    public void i_create_appointment_no_schedule() throws JsonProcessingException {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setDoctorId(lastDoctorId);
        dto.setPatientId(existingPatientId);
        dto.setScheduleId(null); // nonexistent

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/appointments")
                .then().extract().response();
    }

    @When("I create an appointment outcome")
    public void i_create_appointment_outcome() throws JsonProcessingException {
        AppointmentOutcomeDTO dto = new AppointmentOutcomeDTO();
        dto.setAppointmentId(appointmentId);       // use the appointment created earlier
        dto.setDiagnosis("Test diagnosis");
        dto.setRecommendations("Test recommendations");

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)     // must be a DOCTOR session if your resource requires it
                .body(requestBody)
                .put(RestAssured.baseURI + "/appointments")
                .then().extract().response();
    }

    @When("I create an appointment outcome without a session")
    public void create_appointment_outcome_without_session() throws JsonProcessingException {
        AppointmentOutcomeDTO dto = new AppointmentOutcomeDTO();
        dto.setAppointmentId(appointmentId);
        dto.setDiagnosis("Test diagnosis");
        dto.setRecommendations("Test recommendations");

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .body(requestBody) // no session cookie
                .put(RestAssured.baseURI + "/appointments")
                .then().extract().response();
    }

    @When("I create an appointment outcome with an invalid session_id")
    public void create_appointment_outcome_with_invalid_session() throws JsonProcessingException {
        AppointmentOutcomeDTO dto = new AppointmentOutcomeDTO();
        dto.setAppointmentId(appointmentId);
        dto.setDiagnosis("Test diagnosis");
        dto.setRecommendations("Test recommendations");

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", "999999") // invalid session
                .body(requestBody)
                .put(RestAssured.baseURI + "/appointments")
                .then().extract().response();
    }

    @When("I create an appointment outcome with an invalid appointment_id")
    public void create_appointment_outcome_with_invalid_appointment_id() throws JsonProcessingException {
        AppointmentOutcomeDTO dto = new AppointmentOutcomeDTO();
        dto.setAppointmentId(0); // invalid ID
        dto.setDiagnosis("Test diagnosis");
        dto.setRecommendations("Test recommendations");

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/appointments")
                .then().extract().response();
    }


    // --- DELETE APPOINTMENT ---
    @When("I delete the appointment")
    public void i_delete_the_appointment() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .delete(RestAssured.baseURI + "/appointments/" + appointmentId)
                .then().extract().response();
    }

    @When("I delete the appointment without a session")
    public void i_delete_appointment_without_session() {
        response = given()
                .header("Content-Type", "application/json")
                .delete(RestAssured.baseURI + "/appointments/" + appointmentId)
                .then().extract().response();
    }

    @When("I delete the appointment with an invalid session_id")
    public void i_delete_appointment_invalid_session() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", "999999")
                .delete(RestAssured.baseURI + "/appointments/" + appointmentId)
                .then().extract().response();
    }

    @When("I delete an appointment with invalid id")
    public void i_delete_appointment_invalid_id() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .delete(RestAssured.baseURI + "/appointments/99999")
                .then().extract().response();
    }

    @When("I delete an appointment without supplying an id")
    public void i_delete_appointment_without_id() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .delete(RestAssured.baseURI + "/appointments/")
                .then().extract().response();
    }


    @Then("The response JSON should contain valid appointment data")
    public void response_json_should_contain_valid_appointment_data() {
        response.then().body("doctorId", equalTo(lastDoctorId));
        response.then().body("scheduleId", equalTo(lastScheduleId));
        response.then().body("patientId", equalTo(existingPatientId));
        response.then().body("status", equalTo("SCHEDULED"));
    }

    @When("I fetch doctor schedules for the doctor")
    public void i_fetch_doctor_schedules_for_doctor() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .get(RestAssured.baseURI + "/doctor-schedules/" + lastDoctorId)
                .then().extract().response();
    }

    @Then("The response should contain the appointment scheduleId")
    public void response_should_contain_appointment_schedule_id() {
        response.then().body("scheduleId", hasItem(lastScheduleId));
    }

    @When("I fetch all appointments")
    public void i_fetch_all_appointments() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .get(RestAssured.baseURI + "/appointments")
                .then().extract().response();
    }

    @Then("The response should contain the appointment with correct data")
    public void response_should_contain_appointment_with_correct_data() {
        response.then().body("appointmentId", hasItem(appointmentId));
        response.then().body("doctorId", hasItem(lastDoctorId));
        response.then().body("scheduleId", hasItem(lastScheduleId));
        response.then().body("patientId", hasItem(existingPatientId));
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

        // --- Save the returned patient ID for later use ---
        Object idObj = response.jsonPath().get("id");
        if (idObj == null) {
            throw new IllegalStateException("Patient creation failed, no id returned: " + response.asString());
        }

        existingPatientId = ((Number) idObj).intValue();
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
        String uniqueEmail = "duplicate+" + timestamp + "@gmail.com";
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


//    @Given("An existing patient is available")
//    public void an_existing_patient_is_available() throws JsonProcessingException {
//        i_create_new_patient(); // reuse creation step
//        existingPatientId = response.jsonPath().getInt("id"); // save ID for update
//    }
//
//    @Given("An existing patient is available")
//    public void an_existing_patient_is_available() throws JsonProcessingException {
//        if (sessionId == null) {
//            throw new IllegalStateException("Admin must be logged in first!");
//        }
//
//        long ts = System.currentTimeMillis();
//        PatientDTO patient = new PatientDTO();
//        patient.setFirstName("John");
//        patient.setLastName("Doe");
//        patient.setEmail("user+" + ts + "@gmail.com");
//        patient.setPhone("555" + (1000000 + (ts % 9000000)));
//        patient.setAddress("123 Street");
//        patient.setDateOfBirth("1990-01-01");
//
//        String body = objectMapper.writeValueAsString(patient);
//
//        response = given()
//                .header("Content-Type", "application/json")
//                .cookie("session_id", sessionId)
//                .body(body)
//                .post(RestAssured.baseURI + "/patients")
//                .then()
//                .extract()
//                .response();
//
//        Object idObj = response.jsonPath().get("id");
//        if (idObj == null) {
//            throw new IllegalStateException("Patient creation failed: " + response.asString());
//        }
//
//        patientId = ((Number) idObj).intValue();
//    }

    @Given("An existing patient is available")
    public void an_existing_patient_is_available() throws JsonProcessingException {
        if (sessionId == null) {
            throw new IllegalStateException("Admin must be logged in first!");
        }

        long ts = System.currentTimeMillis();

        // Create a unique patient
        PatientDTO patient = new PatientDTO();
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setEmail("user+" + ts + "@gmail.com");
        patient.setPhone("555" + (1000000 + (ts % 9000000)));
        patient.setAddress("123 Street");
        patient.setDateOfBirth("1990-01-01");

        // Save values for update tests
        patientFirstName = patient.getFirstName();
        patientLastName = patient.getLastName();
        patientEmail = patient.getEmail();
        patientPhone = patient.getPhone();
        patientAddress = patient.getAddress();
        patientDateOfBirth = patient.getDateOfBirth();

        String body = objectMapper.writeValueAsString(patient);

        // Send request
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(body)
                .post(RestAssured.baseURI + "/patients")
                .then()
                .extract()
                .response();

        Object idObj = response.jsonPath().get("id");
        if (idObj == null) {
            throw new IllegalStateException("Patient creation failed: " + response.asString());
        }

        // Store patient id for update/delete tests
        patientId = ((Number) idObj).intValue();
        existingPatientId = patientId;
    }

    @When("I delete the patient")
    public void i_delete_the_patient() {
        response = given()
                .cookie("session_id", sessionId)
                .delete(RestAssured.baseURI + "/patients/" + patientId)
                .then()
                .extract()
                .response();
    }

    @Then("The delete patient response has correct data")
    public void the_patient_deletion_response_has_correct_data() {
        assertEquals(204, response.statusCode());
        assertTrue(response.asString().isEmpty());
    }

    @When("I delete the patient without a session")
    public void i_delete_the_patient_without_a_session() {
        response = given()
                .delete(RestAssured.baseURI + "/patients/" + patientId)
                .then()
                .extract()
                .response();
    }

    @When("I delete the patient with an invalid session_id")
    public void i_delete_the_patient_with_an_invalid_session_id() {
        response = given()
                .cookie("session_id", "999999") // does not exist
                .delete(RestAssured.baseURI + "/patients/" + patientId)
                .then()
                .extract()
                .response();
    }

    @When("I delete the patient without an id")
    public void i_delete_the_patient_without_an_id() {
        response = given()
                .cookie("session_id", sessionId)
                .delete(RestAssured.baseURI + "/patients")
                .then()
                .extract()
                .response();
    }

    @When("I create new service without a session")
    public void i_create_new_service_without_session() throws JsonProcessingException {
        ServiceDTO serviceDTO = new ServiceDTO();
        serviceDTO.setName("ServiceTestNoSession" + randomLetters(5));
        serviceDTO.setPrice(200);

        requestBody = objectMapper.writeValueAsString(serviceDTO);

        response = given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .post(RestAssured.baseURI + "/services")
                .then().extract().response();
    }

    @When("I create new service with an invalid session_id")
    public void i_create_new_service_with_invalid_session_id() throws JsonProcessingException {
        ServiceDTO serviceDTO = new ServiceDTO();
        serviceDTO.setName("ServiceTestInvalidSession" + randomLetters(5));
        serviceDTO.setPrice(300);

        requestBody = objectMapper.writeValueAsString(serviceDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", "999999") // invalid session
                .body(requestBody)
                .post(RestAssured.baseURI + "/services")
                .then().extract().response();
    }

    @When("I create new service with empty name")
    public void i_create_new_service_with_empty_name() throws JsonProcessingException {
        ServiceDTO serviceDTO = new ServiceDTO();
        serviceDTO.setName(""); // empty
        serviceDTO.setPrice(250);

        requestBody = objectMapper.writeValueAsString(serviceDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/services")
                .then().extract().response();
    }

    @When("I create new service with a duplicate name")
    public void i_create_new_service_with_duplicate_name() throws JsonProcessingException {
        if (serviceName == null) {
            throw new AssertionError("No previous service name to duplicate");
        }

        ServiceDTO serviceDTO = new ServiceDTO();
        serviceDTO.setName(serviceName); // duplicate
        serviceDTO.setPrice(400);

        requestBody = objectMapper.writeValueAsString(serviceDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/services")
                .then().extract().response();
    }

    @When("I create new service with invalid price")
    public void i_create_new_service_with_invalid_price() throws JsonProcessingException {
        ServiceDTO serviceDTO = new ServiceDTO();
        serviceDTO.setName("ServiceInvalidPrice" + randomLetters(5));
        serviceDTO.setPrice(-50); // invalid price

        requestBody = objectMapper.writeValueAsString(serviceDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/services")
                .then().extract().response();
    }

    @Given("An existing service is available")
    public void an_existing_service_is_available() throws JsonProcessingException {
        if (lastServiceId == null) {
            // Create a service if none exists
            i_create_new_service();
            the_response_status_code_should_be(200); // ensure creation success
        }
    }

    @When("I update the service name")
    public void i_update_the_service_name() throws JsonProcessingException {
        serviceName = "UpdatedService" + randomLetters(5);

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

    @When("I update the service without a session")
    public void i_update_the_service_without_a_session() throws JsonProcessingException {
        ServiceDTO serviceDTO = new ServiceDTO();
        serviceDTO.setId(lastServiceId);
        serviceDTO.setName("NoSessionService" + randomLetters(5));
        serviceDTO.setPrice(servicePrice);

        requestBody = objectMapper.writeValueAsString(serviceDTO);

        response = given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .put(RestAssured.baseURI + "/services")
                .then().extract().response();
    }

    @When("I update the service with an invalid session_id")
    public void i_update_the_service_with_invalid_session_id() throws JsonProcessingException {
        ServiceDTO serviceDTO = new ServiceDTO();
        serviceDTO.setId(lastServiceId);
        serviceDTO.setName("InvalidSessionService" + randomLetters(5));
        serviceDTO.setPrice(servicePrice);

        requestBody = objectMapper.writeValueAsString(serviceDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", "999999") // invalid session
                .body(requestBody)
                .put(RestAssured.baseURI + "/services")
                .then().extract().response();
    }

    @When("I update the service with an empty name")
    public void i_update_the_service_with_empty_name() throws JsonProcessingException {
        ServiceDTO serviceDTO = new ServiceDTO();
        serviceDTO.setId(lastServiceId);
        serviceDTO.setName(""); // empty name
        serviceDTO.setPrice(servicePrice);

        requestBody = objectMapper.writeValueAsString(serviceDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/services")
                .then().extract().response();
    }

    @When("I update the service with a duplicate name")
    public void i_update_the_service_with_a_duplicate_name() throws JsonProcessingException {
        // Ensure another service exists with a name to duplicate
        String duplicateName = "DuplicateService" + randomLetters(5);
        ServiceDTO anotherService = new ServiceDTO();
        anotherService.setName(duplicateName);
        anotherService.setPrice(300);

        String anotherRequestBody = objectMapper.writeValueAsString(anotherService);

        Response createResponse = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(anotherRequestBody)
                .post(RestAssured.baseURI + "/services")
                .then().extract().response();

        int anotherServiceId = createResponse.jsonPath().getInt("id");

        // Now update lastServiceId to duplicate the name
        ServiceDTO serviceDTO = new ServiceDTO();
        serviceDTO.setId(lastServiceId);
        serviceDTO.setName(duplicateName); // duplicate
        serviceDTO.setPrice(servicePrice);

        requestBody = objectMapper.writeValueAsString(serviceDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/services")
                .then().extract().response();
    }

    @When("I update the service with invalid price")
    public void i_update_the_service_with_invalid_price() throws JsonProcessingException {
        ServiceDTO serviceDTO = new ServiceDTO();
        serviceDTO.setId(lastServiceId);
        serviceDTO.setName(serviceName);
        serviceDTO.setPrice(-100); // invalid

        requestBody = objectMapper.writeValueAsString(serviceDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/services")
                .then().extract().response();
    }

    @When("I delete the service without a session")
    public void i_delete_the_service_without_a_session() {
        response = given()
                .header("Content-Type", "application/json")
                .delete(RestAssured.baseURI + "/services/" + lastServiceId)
                .then().extract().response();
    }

    // DELETE with invalid session_id
    @When("I delete the service with an invalid session_id")
    public void i_delete_the_service_with_invalid_session_id() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", "999999") // invalid session
                .delete(RestAssured.baseURI + "/services/" + lastServiceId)
                .then().extract().response();
    }

    // DELETE with invalid service id
    @When("I delete a service with invalid id")
    public void i_delete_a_service_with_invalid_id() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId) // valid session
                .delete(RestAssured.baseURI + "/services/abc") // invalid ID
                .then().extract().response();
    }

    // DELETE without an id
    @When("I delete a service without an id")
    public void i_delete_a_service_without_an_id() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId) // valid session
                .delete(RestAssured.baseURI + "/services/") // missing id
                .then().extract().response();
    }

    @When("I create new doctor without a session")
    public void i_create_new_doctor_without_a_session() throws JsonProcessingException {
        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setFirstName("TestFirst");
        doctorDTO.setLastName("TestLast");
        doctorDTO.setEmail("testemail@gmail.com");
        doctorDTO.setPhone("1234567890");
        doctorDTO.setSpecializationId(1);

        requestBody = objectMapper.writeValueAsString(doctorDTO);

        response = given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .post(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }

    @When("I create new doctor with an invalid session_id")
    public void i_create_new_doctor_with_invalid_session_id() throws JsonProcessingException {
        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setFirstName("TestFirst");
        doctorDTO.setLastName("TestLast");
        doctorDTO.setEmail("testemail2@gmail.com");
        doctorDTO.setPhone("1234567891");
        doctorDTO.setSpecializationId(1);

        requestBody = objectMapper.writeValueAsString(doctorDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", "b") // invalid
                .body(requestBody)
                .post(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }

    @When("I create new doctor with empty specialization id")
    public void i_create_new_doctor_with_empty_specialization_id() throws JsonProcessingException {
        DoctorDTO dto = new DoctorDTO();
        dto.setFirstName("ValidFirst");
        dto.setLastName("ValidLast");
        dto.setEmail("validemail@gmail.com");
        dto.setPhone("1234567890");
        dto.setSpecializationId(null); // empty

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }


    @When("I create new doctor with non empty id")
    public void i_create_new_doctor_with_non_empty_id() throws JsonProcessingException {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(999); // not allowed on create
        dto.setFirstName("Test");
        dto.setLastName("Doctor");
        dto.setEmail("validemail3@gmail.com");
        dto.setPhone("1234567899");
        dto.setSpecializationId(1);

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }

    @When("I create new doctor as a non admin user")
    public void i_create_new_doctor_as_non_admin_user() throws JsonProcessingException {
        DoctorDTO dto = new DoctorDTO();
        dto.setFirstName("TestFirst");
        dto.setLastName("TestLast");
        dto.setEmail("nonadminemail@gmail.com");
        dto.setPhone("1234567800");
        dto.setSpecializationId(1);

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }

    @When("I create new doctor with session id that does not exist")
    public void i_create_new_doctor_with_session_id_that_does_not_exist() throws JsonProcessingException {
        DoctorDTO dto = new DoctorDTO();
        dto.setFirstName("Valid");
        dto.setLastName("Doctor");
        dto.setEmail("invalidsession@gmail.com");
        dto.setPhone("1234567811");
        dto.setSpecializationId(1);

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", "000000") // nonexistent session
                .body(requestBody)
                .post(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }

    @When("I create new doctor with specialization id that does not exist")
    public void i_create_new_doctor_with_specialization_id_that_does_not_exist() throws JsonProcessingException {
        DoctorDTO dto = new DoctorDTO();
        dto.setFirstName("ValidFirst");
        dto.setLastName("ValidLast");
        dto.setEmail("invalidspec@gmail.com");
        dto.setPhone("1234567822");
        dto.setSpecializationId(99999); // invalid

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }

    @When("I create new doctor with invalid email format")
    public void i_create_new_doctor_with_invalid_email_format() throws JsonProcessingException {
        DoctorDTO dto = new DoctorDTO();
        dto.setFirstName("ValidFirst");
        dto.setLastName("ValidLast");
        dto.setEmail("invalid-email-format"); // invalid
        dto.setPhone("1234567833");
        dto.setSpecializationId(1);

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }

    @When("I create new doctor with invalid phone format")
    public void i_create_new_doctor_with_invalid_phone_format() throws JsonProcessingException {
        DoctorDTO dto = new DoctorDTO();
        dto.setFirstName("ValidFirst");
        dto.setLastName("ValidLast");
        dto.setEmail("validphoneformat@gmail.com");
        dto.setPhone("12-34-ABCD"); // invalid format
        dto.setSpecializationId(1);

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }
    @When("I create new doctor with invalid photo")
    public void i_create_new_doctor_with_invalid_photo() throws JsonProcessingException {
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
        doctorDTO.setPhoto(new byte[0]);

        String body = objectMapper.writeValueAsString(doctorDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(body)
                .post(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }



    @When("I update the doctor with session id that does not exist")
    public void i_update_the_doctor_with_session_id_that_does_not_exist() throws JsonProcessingException {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(lastDoctorId);
        dto.setFirstName("UpdatedFirst");
        dto.setLastName(doctorLastName);
        dto.setEmail(doctorEmail);
        dto.setPhone(doctorPhone);
        dto.setSpecializationId(doctorSpecializationId);

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", "000000")
                .body(requestBody)
                .put(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }

    @When("I update the doctor as a non admin user")
    public void i_update_the_doctor_as_a_non_admin_user() throws JsonProcessingException {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(lastDoctorId);
        dto.setFirstName("UpdatedNonAdmin");
        dto.setLastName(doctorLastName);
        dto.setEmail(doctorEmail);
        dto.setPhone(doctorPhone);
        dto.setSpecializationId(doctorSpecializationId);

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }

    @When("I update the doctor with empty id")
    public void i_update_the_doctor_with_empty_id() throws JsonProcessingException {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(null); // empty id
        dto.setFirstName("First");
        dto.setLastName("Last");
        dto.setEmail("update@mail.com");
        dto.setPhone("1234567890");
        dto.setSpecializationId(1);

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }

    @When("I update the doctor with id that does not exist")
    public void i_update_the_doctor_with_id_that_does_not_exist() throws JsonProcessingException {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(9999999);
        dto.setFirstName("First");
        dto.setLastName("Last");
        dto.setEmail("update@mail.com");
        dto.setPhone("1234567890");
        dto.setSpecializationId(1);

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }

    @When("I update the doctor with empty specialization id")
    public void i_update_the_doctor_with_empty_specialization_id() throws JsonProcessingException {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(lastDoctorId);
        dto.setFirstName(doctorFirstName);
        dto.setLastName(doctorLastName);
        dto.setEmail(doctorEmail);
        dto.setPhone(doctorPhone);
        dto.setSpecializationId(null); // empty

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }

    @When("I update the doctor with specialization id that does not exist")
    public void i_update_the_doctor_with_specialization_id_that_does_not_exist() throws JsonProcessingException {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(lastDoctorId);
        dto.setFirstName(doctorFirstName);
        dto.setLastName(doctorLastName);
        dto.setEmail(doctorEmail);
        dto.setPhone(doctorPhone);
        dto.setSpecializationId(99999);

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }

    @When("I update the doctor with invalid email format")
    public void i_update_the_doctor_with_invalid_email_format() throws JsonProcessingException {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(lastDoctorId);
        dto.setFirstName(doctorFirstName);
        dto.setLastName(doctorLastName);
        dto.setEmail("invalid-email");
        dto.setPhone(doctorPhone);
        dto.setSpecializationId(doctorSpecializationId);

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }

    @When("I update the doctor with invalid phone format")
    public void i_update_the_doctor_with_invalid_phone_format() throws JsonProcessingException {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(lastDoctorId);
        dto.setFirstName(doctorFirstName);
        dto.setLastName(doctorLastName);
        dto.setEmail(doctorEmail);
        dto.setPhone("ABC-12345"); // invalid
        dto.setSpecializationId(doctorSpecializationId);

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }

    @When("I update the doctor with invalid photo")
    public void i_update_the_doctor_with_invalid_photo() throws JsonProcessingException {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(lastDoctorId);
        dto.setFirstName(doctorFirstName);
        dto.setLastName(doctorLastName);
        dto.setEmail(doctorEmail);
        dto.setPhone(doctorPhone);
        dto.setSpecializationId(doctorSpecializationId);
        dto.setPhoto(new byte[0]);


        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }


    @When("I delete the doctor with session id that does not exist")
    public void i_delete_the_doctor_with_session_id_that_does_not_exist() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", "000000")
                .delete(RestAssured.baseURI + "/doctors/" + lastDoctorId)
                .then().extract().response();
    }

    @When("I delete the doctor as a non admin user")
    public void i_delete_the_doctor_as_a_non_admin_user() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .delete(RestAssured.baseURI + "/doctors/" + lastDoctorId)
                .then().extract().response();
    }

    @When("I delete a doctor with empty id")
    public void i_delete_a_doctor_with_empty_id() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .delete(RestAssured.baseURI + "/doctors/")
                .then().extract().response();
    }

    @When("I delete a doctor with id that does not exist")
    public void i_delete_a_doctor_with_id_that_does_not_exist() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .delete(RestAssured.baseURI + "/doctors/0")
                .then().extract().response();
    }



    @When("I create new doctor with empty first name")
    public void i_create_new_doctor_with_empty_first_name() throws JsonProcessingException {
        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setFirstName(""); // empty
        doctorDTO.setLastName("ValidLast");
        doctorDTO.setEmail("validemail1@gmail.com");
        doctorDTO.setPhone("1234567892");
        doctorDTO.setSpecializationId(1);

        requestBody = objectMapper.writeValueAsString(doctorDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }

    @When("I create new doctor with empty last name")
    public void i_create_new_doctor_with_empty_last_name() throws JsonProcessingException {
        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setFirstName("ValidFirst");
        doctorDTO.setLastName(""); // empty
        doctorDTO.setEmail("validemail2@gmail.com");
        doctorDTO.setPhone("1234567893");
        doctorDTO.setSpecializationId(1);

        requestBody = objectMapper.writeValueAsString(doctorDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }

    @When("I create new doctor with empty email")
    public void i_create_new_doctor_with_empty_email() throws JsonProcessingException {
        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setFirstName("ValidFirst");
        doctorDTO.setLastName("ValidLast");
        doctorDTO.setEmail(""); // empty
        doctorDTO.setPhone("1234567894");
        doctorDTO.setSpecializationId(1);

        requestBody = objectMapper.writeValueAsString(doctorDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }

    @When("I create new doctor with duplicate email")
    public void i_create_new_doctor_with_duplicate_email() throws JsonProcessingException {
        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setFirstName("AnotherFirst");
        doctorDTO.setLastName("AnotherLast");
        doctorDTO.setEmail(doctorEmail); // same as last created
        doctorDTO.setPhone(randomPhoneNumber());
        doctorDTO.setSpecializationId(1);

        requestBody = objectMapper.writeValueAsString(doctorDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }

    @When("I create new doctor with empty phone")
    public void i_create_new_doctor_with_empty_phone() throws JsonProcessingException {
        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setFirstName("ValidFirst");
        doctorDTO.setLastName("ValidLast");
        doctorDTO.setEmail("uniqueemail@gmail.com");
        doctorDTO.setPhone(""); // empty
        doctorDTO.setSpecializationId(1);

        requestBody = objectMapper.writeValueAsString(doctorDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }

    @When("I create new doctor with duplicate phone")
    public void i_create_new_doctor_with_duplicate_phone() throws JsonProcessingException {
        doctorFirstName = "DoctorTest" + randomLetters(10);
        doctorLastName = "Medic" + randomLetters(10);
        doctorEmail = (doctorFirstName + doctorLastName).toLowerCase() + "@gmail.com";
        doctorSpecializationId = 2;

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

    @Given("An existing doctor is available")
    public void an_existing_doctor_is_available() throws JsonProcessingException {
        if (sessionId == null) {
            throw new IllegalStateException("Admin must be logged in first!");
        }

        long ts = System.currentTimeMillis();

        // Generate valid doctor data
        doctorFirstName = "Doctor" + randomLetters(5); // matches NAME_REGEX
        doctorLastName = "Medic" + randomLetters(5);   // matches NAME_REGEX
        doctorEmail = "doctor" + ts + "@gmail.com";  // valid email format
        doctorPhone = "+380" + (100000000 + (ts % 900000000)); // matches PHONE_REGEX
        doctorSpecializationId = 1; // must exist in DB

        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setFirstName(doctorFirstName);
        doctorDTO.setLastName(doctorLastName);
        doctorDTO.setEmail(doctorEmail);
        doctorDTO.setPhone(doctorPhone);
        doctorDTO.setSpecializationId(doctorSpecializationId);

        requestBody = objectMapper.writeValueAsString(doctorDTO);

        // Send request to create doctor
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/doctors")
                .then().statusCode(201)
                .extract().response();

        Object idObj = response.jsonPath().get("id");
        if (idObj == null) {
            throw new IllegalStateException("Doctor creation failed: " + response.asString());
        }

        lastDoctorId = ((Number) idObj).intValue();
    }


    @Given("Another doctor exists for duplicate tests")
    public void another_doctor_exists_for_duplicate_tests() throws JsonProcessingException {
        if (sessionId == null) {
            throw new IllegalStateException("Admin must be logged in first!");
        }

        long ts = System.currentTimeMillis();

        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setFirstName("DupDoctor");
        doctorDTO.setLastName("Medic");
        doctorDTO.setEmail("dupdoctor+" + ts + "@gmail.com");
        doctorDTO.setPhone("+380" + (200000000 + (ts % 900000000)));
        doctorDTO.setSpecializationId(1);

        String body = objectMapper.writeValueAsString(doctorDTO);

        Response dupResponse = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(body)
                .post(RestAssured.baseURI + "/doctors")
                .then().statusCode(201)
                .extract().response();

        Object idObj = dupResponse.jsonPath().get("id");
        if (idObj == null) {
            throw new IllegalStateException("Duplicate doctor creation failed: " + dupResponse.asString());
        }

        otherDoctorEmail = doctorDTO.getEmail();
        otherDoctorPhone = doctorDTO.getPhone();
    }

    @When("I update the doctor")
    public void i_update_the_doctor() throws JsonProcessingException {
        doctorFirstName = "DoctorTest" + randomLetters(10);
        doctorLastName = "Medic" + randomLetters(10);
        doctorEmail = (doctorFirstName + doctorLastName).toLowerCase() + "@gmail.com";
        doctorPhone = randomPhoneNumber();
        doctorSpecializationId = 2;
        byte[] doctorPhoto = "new dummy photo content".getBytes();

        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setId(staleDoctorId);
        doctorDTO.setFirstName(doctorFirstName);
        doctorDTO.setLastName(doctorLastName);
        doctorDTO.setEmail(doctorEmail);
        doctorDTO.setPhone(doctorPhone);
        doctorDTO.setSpecializationId(doctorSpecializationId);
        doctorDTO.setPhoto(doctorPhoto);
        String body = objectMapper.writeValueAsString(doctorDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(body)
                .put(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }



    // Update without session
    @When("I update the doctor without a session")
    public void i_update_the_doctor_without_a_session() throws JsonProcessingException {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(lastDoctorId);
        dto.setFirstName("UpdatedFirst" + randomLetters(5));
        dto.setLastName(doctorLastName);
        dto.setEmail(doctorEmail);
        dto.setPhone(doctorPhone);
        dto.setSpecializationId(doctorSpecializationId);

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .put(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }

    // Update with invalid session
    @When("I update the doctor with an invalid session_id")
    public void i_update_the_doctor_with_invalid_session_id() throws JsonProcessingException {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(lastDoctorId);
        dto.setFirstName("UpdatedFirst" + randomLetters(5));
        dto.setLastName(doctorLastName);
        dto.setEmail(doctorEmail);
        dto.setPhone(doctorPhone);
        dto.setSpecializationId(doctorSpecializationId);

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", "b")
                .body(requestBody)
                .put(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }

    // Empty first name
    @When("I update the doctor with empty first name")
    public void i_update_the_doctor_with_empty_first_name() throws JsonProcessingException {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(lastDoctorId);
        dto.setFirstName("");
        dto.setLastName(doctorLastName);
        dto.setEmail(doctorEmail);
        dto.setPhone(doctorPhone);
        dto.setSpecializationId(doctorSpecializationId);

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }

    // Empty last name
    @When("I update the doctor with empty last name")
    public void i_update_the_doctor_with_empty_last_name() throws JsonProcessingException {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(lastDoctorId);
        dto.setFirstName(doctorFirstName);
        dto.setLastName("");
        dto.setEmail(doctorEmail);
        dto.setPhone(doctorPhone);
        dto.setSpecializationId(doctorSpecializationId);

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }

    // Empty email
    @When("I update the doctor with empty email")
    public void i_update_the_doctor_with_empty_email() throws JsonProcessingException {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(lastDoctorId);
        dto.setFirstName(doctorFirstName);
        dto.setLastName(doctorLastName);
        dto.setEmail("");
        dto.setPhone(doctorPhone);
        dto.setSpecializationId(doctorSpecializationId);

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }

    @Given("Another doctor exists")
    public void another_doctor_exists() throws JsonProcessingException{
        doctorFirstName = "DoctorTest" + randomLetters(10);
        doctorLastName = "Medic" + randomLetters(10);
        doctorEmail = (doctorFirstName + doctorLastName).toLowerCase() + "@gmail.com";
        doctorPhone = randomPhoneNumber();
        doctorSpecializationId = 2;
        byte[] doctorPhoto = "dummy photo content".getBytes();

        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setFirstName(doctorFirstName);
        doctorDTO.setLastName(doctorLastName);
        doctorDTO.setEmail(doctorEmail);
        doctorDTO.setPhone(doctorPhone);
        doctorDTO.setSpecializationId(doctorSpecializationId);
        doctorDTO.setPhoto(doctorPhoto);

        requestBody = objectMapper.writeValueAsString(doctorDTO);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/doctors")
                .then().extract().response();

        Object idObj = response.jsonPath().get("id");

        if (idObj == null) {
            throw new IllegalStateException("Doctor creation failed, no id returned: " + response.asString());
        }
        lastDoctorId = ((Number) idObj).intValue();
        existingDoctorId = lastDoctorId;
    }
    // Duplicate email
    @When("I update the doctor with duplicate email")
    public void i_update_doctor_with_duplicate_email() throws JsonProcessingException {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(lastDoctorId);
        dto.setFirstName(doctorFirstName);
        dto.setLastName(doctorLastName);
        dto.setEmail(staleDoctorEmail);
        dto.setPhone(doctorPhone);
        dto.setSpecializationId(doctorSpecializationId);
        dto.setPhoto(staleDoctorPhoto);

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }

    // Empty phone
    @When("I update the doctor with empty phone")
    public void i_update_the_doctor_with_empty_phone() throws JsonProcessingException {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(lastDoctorId);
        dto.setFirstName(doctorFirstName);
        dto.setLastName(doctorLastName);
        dto.setEmail(doctorEmail);
        dto.setPhone("");
        dto.setSpecializationId(doctorSpecializationId);

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }

    // Duplicate phone
    @When("I update the doctor with duplicate phone")
    public void i_update_the_doctor_with_duplicate_phone() throws JsonProcessingException {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(lastDoctorId);
        dto.setFirstName(doctorFirstName);
        dto.setLastName(doctorLastName);
        dto.setEmail(doctorEmail);
        dto.setPhone(staleDoctorPhone); // from another doctor
        dto.setSpecializationId(doctorSpecializationId);

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }

    @When("I delete the doctor")
    public void i_delete_the_doctor() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .delete(RestAssured.baseURI + "/doctors/" + staleDoctorId)
                .then().extract().response();
    }

    @When("I delete the doctor without a session")
    public void i_delete_the_doctor_without_a_session() {
        response = given()
                .header("Content-Type", "application/json")
                .delete(RestAssured.baseURI + "/doctors/" + staleDoctorId)
                .then().extract().response();
    }

    @When("I delete the doctor with an invalid session_id")
    public void i_delete_the_doctor_with_invalid_session_id() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", "b") // invalid session
                .delete(RestAssured.baseURI + "/doctors/" + staleDoctorId)
                .then().extract().response();
    }

    @When("I delete a doctor with invalid id")
    public void i_delete_doctor_with_invalid_id() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .delete(RestAssured.baseURI + "/doctors/9999999") // non-existent id
                .then().extract().response();
    }

    @When("I delete a doctor without an id")
    public void i_delete_doctor_without_id() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .delete(RestAssured.baseURI + "/doctors/") // missing id in path
                .then().extract().response();
    }

    @Given("An existing doctor is available for schedule")
    public void an_existing_doctor_is_available_for_schedule() throws JsonProcessingException {
        if (sessionId == null) {
            throw new IllegalStateException("Admin must be logged in first!");
        }

        long ts = System.currentTimeMillis();

        DoctorDTO doctor = new DoctorDTO();
        doctor.setFirstName("DoctorScheduler" + randomLetters(5));
        doctor.setLastName("LastDoctorScheduler" + randomLetters(3));
        doctor.setEmail("doctor" + randomLetters(6) + "@gmail.com");
        doctor.setPhone("555" + (1000000 + (ts % 9000000)));
        doctor.setSpecializationId(1);
        byte[] doctorPhoto = "dummy photo content".getBytes();
        doctor.setPhoto(doctorPhoto);

        // Save values if needed later
        doctorFirstName = doctor.getFirstName();
        doctorLastName = doctor.getLastName();
        doctorEmail = doctor.getEmail();
        doctorPhone = doctor.getPhone();
        String body = objectMapper.writeValueAsString(doctor);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(body)
                .post(RestAssured.baseURI + "/doctors")
                .then()
                .extract()
                .response();

        Object idObj = response.jsonPath().get("id");

        if (idObj == null) {
            throw new IllegalStateException(
                    "Doctor creation failed (no id returned): " + response.asString()
            );
        }

        lastDoctorId = ((Number) idObj).intValue();
        staleDoctorId = lastDoctorId;
        existingDoctorId = lastDoctorId;
    }


    @When("I create a valid doctor schedule")
    public void i_create_a_valid_doctor_schedule() throws JsonProcessingException {
        DoctorScheduleDTO schedule = new DoctorScheduleDTO();
        schedule.setDoctorId(lastDoctorId);
        schedule.setScheduleDate(LocalDate.now().plusDays(1).toString());
        schedule.setStartTime("09:00");
        schedule.setEndTime("11:00");

        requestBody = objectMapper.writeValueAsString(schedule);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/doctor-schedules")
                .then().extract().response();

        lastScheduleId = response.jsonPath().getInt("id");
    }

    @When("I create a doctor schedule with empty doctor id")
    public void i_create_a_doctor_schedule_with_empty_doctor_id() throws JsonProcessingException {
        DoctorScheduleDTO dto = new DoctorScheduleDTO();
        dto.setDoctorId(null);
        dto.setScheduleDate(LocalDate.now().plusDays(1).toString());
        dto.setStartTime("10:00");
        dto.setEndTime("11:00");

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/doctor-schedules")
                .then().extract().response();
    }

    @When("I create a doctor schedule with non existing doctor id")
    public void i_create_a_doctor_schedule_with_non_existing_doctor_id() throws JsonProcessingException {
        DoctorScheduleDTO dto = new DoctorScheduleDTO();
        dto.setDoctorId(999999); // invalid / non-existing id
        dto.setScheduleDate(LocalDate.now().plusDays(1).toString());
        dto.setStartTime("10:00");
        dto.setEndTime("11:00");

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/doctor-schedules")
                .then().extract().response();
    }

    @Then("The doctor schedule response has correct data")
    public void the_doctor_schedule_response_has_correct_data() {
        assertThat(response.jsonPath().getInt("doctorId"), equalTo(lastDoctorId));
        assertNotNull(response.jsonPath().getString("scheduleDate"));
        assertNotNull(response.jsonPath().getString("startTime"));
        assertNotNull(response.jsonPath().getString("endTime"));
    }

    @When("I create a doctor schedule without a session")
    public void i_create_a_doctor_schedule_without_session() throws JsonProcessingException {
        DoctorScheduleDTO schedule = new DoctorScheduleDTO();
        schedule.setDoctorId(lastDoctorId);
        schedule.setScheduleDate(LocalDate.now().plusDays(1).toString());
        schedule.setStartTime("10:00");
        schedule.setEndTime("12:00");

        requestBody = objectMapper.writeValueAsString(schedule);

        response = given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .post(RestAssured.baseURI + "/doctor-schedules")
                .then().extract().response();
    }

    @When("I create a doctor schedule with an invalid session_id")
    public void i_create_a_doctor_schedule_with_invalid_session_id() throws JsonProcessingException {
        DoctorScheduleDTO schedule = new DoctorScheduleDTO();
        schedule.setDoctorId(lastDoctorId);
        schedule.setScheduleDate(LocalDate.now().plusDays(1).toString());
        schedule.setStartTime("10:00");
        schedule.setEndTime("12:00");

        requestBody = objectMapper.writeValueAsString(schedule);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", "999999")
                .body(requestBody)
                .post(RestAssured.baseURI + "/doctor-schedules")
                .then().extract().response();
    }

    @When("I create a doctor schedule with a malformed session_id")
    public void i_create_a_doctor_schedule_with_malformed_session_id() throws JsonProcessingException {
        DoctorScheduleDTO schedule = new DoctorScheduleDTO();
        schedule.setDoctorId(lastDoctorId);
        schedule.setScheduleDate(LocalDate.now().plusDays(1).toString());
        schedule.setStartTime("10:00");
        schedule.setEndTime("12:00");

        requestBody = objectMapper.writeValueAsString(schedule);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", "b")
                .body(requestBody)
                .post(RestAssured.baseURI + "/doctor-schedules")
                .then().extract().response();
    }

    @When("I create a doctor schedule with a past date")
    public void i_create_a_doctor_schedule_with_a_past_date() throws JsonProcessingException {
        DoctorScheduleDTO schedule = new DoctorScheduleDTO();
        schedule.setDoctorId(lastDoctorId);
        schedule.setScheduleDate(LocalDate.now().minusDays(1).toString());
        schedule.setStartTime("10:00");
        schedule.setEndTime("11:00");

        requestBody = objectMapper.writeValueAsString(schedule);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/doctor-schedules")
                .then().extract().response();
    }

    @When("I create a doctor schedule with invalid time range")
    public void i_create_a_doctor_schedule_with_invalid_time_range() throws JsonProcessingException {
        DoctorScheduleDTO schedule = new DoctorScheduleDTO();
        schedule.setDoctorId(lastDoctorId);
        schedule.setScheduleDate(LocalDate.now().plusDays(1).toString());
        schedule.setStartTime("14:00");
        schedule.setEndTime("13:00"); // end < start (invalid)

        requestBody = objectMapper.writeValueAsString(schedule);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/doctor-schedules")
                .then().extract().response();
    }

    @When("I create a doctor schedule that overlaps")
    public void i_create_a_doctor_schedule_that_overlaps() throws JsonProcessingException {
        // overlapping means use same date & time as the valid schedule created first
        DoctorScheduleDTO schedule = new DoctorScheduleDTO();
        schedule.setDoctorId(lastDoctorId);
        schedule.setScheduleDate(LocalDate.now().plusDays(1).toString());
        schedule.setStartTime("09:30");
        schedule.setEndTime("10:30");

        requestBody = objectMapper.writeValueAsString(schedule);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/doctor-schedules")
                .then().extract().response();
    }

    // Create the main schedule for update
    @Given("An existing doctor schedule is available")
    public void an_existing_doctor_schedule_is_available() throws JsonProcessingException {
        if (sessionId == null) {
            throw new IllegalStateException("Admin must be logged in first!");
        }

        DoctorScheduleDTO dto = new DoctorScheduleDTO();
        dto.setDoctorId(lastDoctorId);
        dto.setScheduleDate(LocalDate.now().plusDays(1).toString());
        dto.setStartTime("09:00");
        dto.setEndTime("11:00");

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/doctor-schedules")
                .then().extract().response();

        Object idObj = response.jsonPath().get("id");
        if (idObj == null) {
            throw new IllegalStateException(
                    "Schedule creation failed (no id returned). Response: " + response.asString()
            );
        }

        lastScheduleId = ((Number) idObj).intValue();
    }

    // Create a second schedule for overlapping test setup (non-overlapping initially)
    @Given("Another doctor schedule exists for overlapping tests")
    public void another_doctor_schedule_exists_for_overlapping_tests() throws JsonProcessingException {

        DoctorScheduleDTO dto = new DoctorScheduleDTO();
        dto.setDoctorId(lastDoctorId);
        dto.setScheduleDate(LocalDate.now().plusDays(1).toString());
        dto.setStartTime("12:00"); // non-overlapping
        dto.setEndTime("14:00");

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/doctor-schedules")
                .then().extract().response();

        Object idObj = response.jsonPath().get("id");
        if (idObj == null) {
            throw new IllegalStateException(
                    "Another schedule creation failed (no id returned). Response: " + response.asString()
            );
        }

        overlapScheduleId = ((Number) idObj).intValue();
    }


    @When("I update the doctor schedule")
    public void i_update_the_doctor_schedule() throws JsonProcessingException {
        DoctorScheduleDTO dto = new DoctorScheduleDTO();
        dto.setId(lastScheduleId);
        dto.setDoctorId(lastDoctorId);
        dto.setScheduleDate(LocalDate.now().plusDays(5).toString());
        dto.setStartTime("13:00");
        dto.setEndTime("15:00");
        System.out.println(dto);
        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/doctor-schedules")
                .then().extract().response();
    }

    // Update without session
    @When("I update the doctor schedule without a session")
    public void i_update_the_doctor_schedule_without_a_session() throws JsonProcessingException {
        DoctorScheduleDTO dto = new DoctorScheduleDTO();
        dto.setId(lastScheduleId);
        dto.setDoctorId(lastDoctorId);
        dto.setScheduleDate(LocalDate.now().plusDays(2).toString());
        dto.setStartTime("13:00");
        dto.setEndTime("15:00");

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .put(RestAssured.baseURI + "/doctor-schedules") // ✅ no /{id}
                .then().extract().response();
    }

    // Update with invalid session
    @When("I update the doctor schedule with an invalid session_id")
    public void i_update_with_invalid_session_id() throws JsonProcessingException {
        DoctorScheduleDTO dto = new DoctorScheduleDTO();
        dto.setId(lastScheduleId);
        dto.setDoctorId(lastDoctorId);
        dto.setScheduleDate(LocalDate.now().plusDays(2).toString());
        dto.setStartTime("13:00");
        dto.setEndTime("15:00");

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", "9999999")
                .body(requestBody)
                .put(RestAssured.baseURI + "/doctor-schedules") // ✅ no /{id}
                .then().extract().response();
    }

    @When("I update the doctor schedule with a malformed session_id")
    public void i_update_with_malformed_session_id() throws JsonProcessingException {
        DoctorScheduleDTO dto = new DoctorScheduleDTO();
        dto.setId(lastScheduleId);
        dto.setDoctorId(lastDoctorId);
        dto.setScheduleDate(LocalDate.now().plusDays(2).toString());
        dto.setStartTime("13:00");
        dto.setEndTime("15:00");

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", "b")
                .body(requestBody)
                .put(RestAssured.baseURI + "/doctor-schedules") // ✅ no /{id}
                .then().extract().response();
    }

    // Update with past date
    @When("I update the doctor schedule with a past date")
    public void i_update_doctor_schedule_with_past_date() throws JsonProcessingException {
        DoctorScheduleDTO dto = new DoctorScheduleDTO();
        dto.setId(lastScheduleId);
        dto.setDoctorId(lastDoctorId);
        dto.setScheduleDate(LocalDate.now().minusDays(50).toString());
        dto.setStartTime("09:00");
        dto.setEndTime("11:00");

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/doctor-schedules") // ✅ no /{id}
                .then().extract().response();
    }

    // Invalid time (start > end)
    @When("I update the doctor schedule with invalid start or end time")
    public void i_update_doctor_schedule_with_invalid_time() throws JsonProcessingException {
        DoctorScheduleDTO dto = new DoctorScheduleDTO();
        dto.setId(lastScheduleId);
        dto.setDoctorId(lastDoctorId);
        dto.setScheduleDate(LocalDate.now().plusDays(1).toString());
        dto.setStartTime("15:00");
        dto.setEndTime("14:30"); // invalid

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/doctor-schedules") // ✅ no /{id}
                .then().extract().response();
    }

    // Trigger overlapping time (update first schedule to overlap)
    @When("I update the doctor schedule to an overlapping time")
    public void i_update_to_overlapping_time() throws JsonProcessingException {
        DoctorScheduleDTO dto = new DoctorScheduleDTO();
        dto.setId(lastScheduleId);
        dto.setDoctorId(lastDoctorId);
        dto.setScheduleDate(LocalDate.now().plusDays(1).toString());
        dto.setStartTime("12:30"); // overlaps with 12:00–14:00
        dto.setEndTime("13:30");

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/doctor-schedules") // ✅ no /{id}
                .then().extract().response();
    }

    @When("I update the doctor schedule with empty schedule id")
    public void i_update_doctor_schedule_with_empty_schedule_id() throws JsonProcessingException {
        DoctorScheduleDTO dto = new DoctorScheduleDTO();
        dto.setDoctorId(existingDoctorId); // valid doctor
        dto.setScheduleDate(LocalDate.now().plusDays(1).toString());
        dto.setStartTime("09:00");
        dto.setEndTime("10:00");
        // ID intentionally NOT set → empty/null

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/doctor-schedules")
                .then().extract().response();
    }

    @When("I update the doctor schedule with non existing schedule id")
    public void i_update_doctor_schedule_with_non_existing_schedule_id() throws JsonProcessingException {
        DoctorScheduleDTO dto = new DoctorScheduleDTO();
        dto.setId(99999999); // definitely not existing
        dto.setDoctorId(existingDoctorId);
        dto.setScheduleDate(LocalDate.now().plusDays(1).toString());
        dto.setStartTime("09:00");
        dto.setEndTime("10:00");

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/doctor-schedules")
                .then().extract().response();
    }

    @When("I update the doctor schedule with empty doctor id")
    public void i_update_doctor_schedule_with_empty_doctor_id() throws JsonProcessingException {
        DoctorScheduleDTO dto = new DoctorScheduleDTO();
        dto.setId(lastScheduleId); // valid schedule id
        dto.setScheduleDate(LocalDate.now().plusDays(1).toString());
        dto.setStartTime("09:00");
        dto.setEndTime("10:00");
        // doctorId intentionally NOT set → null

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/doctor-schedules")
                .then().extract().response();
    }

    @When("I update the doctor schedule with non existing doctor id")
    public void i_update_doctor_schedule_with_non_existing_doctor_id() throws JsonProcessingException {
        DoctorScheduleDTO dto = new DoctorScheduleDTO();
        dto.setId(lastScheduleId);
        dto.setDoctorId(99999999); // definitely not existing doctor
        dto.setScheduleDate(LocalDate.now().plusDays(1).toString());
        dto.setStartTime("09:00");
        dto.setEndTime("10:00");

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .put(RestAssured.baseURI + "/doctor-schedules")
                .then().extract().response();
    }

    // Delete schedule normally
    @When("I delete the doctor schedule")
    public void i_delete_the_doctor_schedule() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .delete(RestAssured.baseURI + "/doctor-schedules/" + lastDoctorId + "/" + lastScheduleId)
                .then().extract().response();
    }

    // Delete schedule without session
    @When("I delete the doctor schedule without a session")
    public void i_delete_the_doctor_schedule_without_a_session() {
        response = given()
                .header("Content-Type", "application/json")
                .delete(RestAssured.baseURI + "/doctor-schedules/" + lastDoctorId + "/" + lastScheduleId)
                .then().extract().response();
    }

    // Delete schedule with invalid session
    @When("I delete the doctor schedule with an invalid session_id")
    public void i_delete_the_doctor_schedule_with_invalid_session_id() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", "9999999")
                .delete(RestAssured.baseURI + "/doctor-schedules/" + lastDoctorId + "/" + lastScheduleId)
                .then().extract().response();
    }

    @When("I delete the doctor schedule with a malformed session_id")
    public void i_delete_the_doctor_schedule_with_malformed_session_id() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", "b")
                .delete(RestAssured.baseURI + "/doctor-schedules/" + lastDoctorId + "/" + lastScheduleId)
                .then().extract().response();
    }


    // Delete schedule with invalid ID
    @When("I delete a doctor schedule with invalid id")
    public void i_delete_doctor_schedule_with_invalid_id() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .delete(RestAssured.baseURI + "/doctor-schedules/" + lastDoctorId + "/9999999")
                .then().extract().response();
    }

    // Delete schedule without providing scheduleId
    @When("I delete a doctor schedule without a schedule id")
    public void i_delete_doctor_schedule_without_schedule_id() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .delete(RestAssured.baseURI + "/doctor-schedules/" + lastDoctorId + "/") // missing scheduleId
                .then().extract().response();
    }

    // GET schedules by doctor
    @When("I get schedules by doctor")
    public void i_get_schedules_by_doctor() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .get(RestAssured.baseURI + "/doctor-schedules/" + lastDoctorId)
                .then().extract().response();
    }

    // GET schedules by doctor without session
    @When("I get schedules by doctor without a session")
    public void i_get_schedules_by_doctor_without_a_session() {
        response = given()
                .header("Content-Type", "application/json")
                .get(RestAssured.baseURI + "/doctor-schedules/" + lastDoctorId)
                .then().extract().response();
    }

    // GET schedules by doctor with invalid session
    @When("I get schedules by doctor with an invalid session_id")
    public void i_get_schedules_by_doctor_with_invalid_session_id() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", "9999999") // guaranteed invalid
                .get(RestAssured.baseURI + "/doctor-schedules/" + lastDoctorId)
                .then().extract().response();
    }

    @When("I get schedules by doctor with a malformed session_id")
    public void i_get_schedules_by_doctor_with_malformed_session_id() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", "b") // guaranteed invalid
                .get(RestAssured.baseURI + "/doctor-schedules/" + lastDoctorId)
                .then().extract().response();
    }

    // GET schedules with appointments (for doctor)
    @When("I get schedules with appointments")
    public void i_get_schedules_with_appointments() {
        System.out.println(staleDoctorId);
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .get(RestAssured.baseURI + "/doctor-schedules/appointments/" + staleDoctorId)
                .then().extract().response();
    }

    // GET schedules with appointments without session
    @When("I get schedules with appointments without a session")
    public void i_get_schedules_with_appointments_without_a_session() {
        response = given()
                .header("Content-Type", "application/json")
                .get(RestAssured.baseURI + "/doctor-schedules/appointments/" + staleDoctorId)
                .then().extract().response();
    }

    // GET schedules with appointments with invalid session
    @When("I get schedules with appointments with an invalid session_id")
    public void i_get_schedules_with_appointments_with_invalid_session_id() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", "9999999")
                .get(RestAssured.baseURI + "/doctor-schedules/appointments/" + staleDoctorId)
                .then().extract().response();
    }

    @When("I get schedules with appointments with a malformed session_id")
    public void i_get_schedules_with_appointments_with_malformed_session_id() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", "b")
                .get(RestAssured.baseURI + "/doctor-schedules/appointments/" + staleDoctorId)
                .then().extract().response();
    }


    // --- MALFORMED SESSION ID ---
    @When("I create an appointment with a malformed session_id")
    public void i_create_appointment_with_malformed_session_id() throws JsonProcessingException {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setDoctorId(lastDoctorId);
        dto.setPatientId(existingPatientId);
        dto.setScheduleId(lastScheduleId);

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", "INVALID_SESSION")  // deliberately malformed  
                .body(requestBody)
                .post(RestAssured.baseURI + "/appointments")
                .then().extract().response();

    }

    // --- NON-EXISTING SESSION ID ---
    @When("I create an appointment with a non-existing session_id")
    public void i_create_appointment_with_non_existing_session_id() throws JsonProcessingException {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setDoctorId(lastDoctorId);
        dto.setPatientId(existingPatientId);
        dto.setScheduleId(lastScheduleId);

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", "999999")  // arbitrary non-existing session id  
                .body(requestBody)
                .post(RestAssured.baseURI + "/appointments")
                .then().extract().response();

    }

    // --- ADMIN ROLE FORBIDDEN ---
    @When("I create an appointment as an admin")
    public void i_create_appointment_as_admin() throws JsonProcessingException {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setDoctorId(lastDoctorId);
        dto.setPatientId(existingPatientId);
        dto.setScheduleId(lastScheduleId);

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)  // admin session  
                .body(requestBody)
                .post(RestAssured.baseURI + "/appointments")
                .then().extract().response();

    }

    // --- PREEXISTING APPOINTMENT ID ---
    @When("I create an appointment with a preexisting appointmentId")
    public void i_create_appointment_with_preexisting_appointmentId() throws JsonProcessingException {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setAppointmentId(appointmentId);  // intentionally set
        dto.setDoctorId(lastDoctorId);
        dto.setPatientId(existingPatientId);
        dto.setScheduleId(lastScheduleId);

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/appointments")
                .then().extract().response();

    }

    // --- PREEXISTING STATUS ---
    @When("I create an appointment with a preexisting status")
    public void i_create_appointment_with_preexisting_status() throws JsonProcessingException {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setStatus("SCHEDULED");  // intentionally set
        dto.setDoctorId(lastDoctorId);
        dto.setPatientId(existingPatientId);
        dto.setScheduleId(lastScheduleId);

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/appointments")
                .then().extract().response();

    }

    // --- MISSING PATIENT ID ---
    @When("I create an appointment without patientId")
    public void i_create_appointment_without_patientId() throws JsonProcessingException {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setDoctorId(lastDoctorId);
        dto.setScheduleId(lastScheduleId);
// patientId intentionally omitted

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/appointments")
                .then().extract().response();

    }

    // --- INVALID PATIENT ID ---
    @When("I create an appointment with an invalid patientId")
    public void i_create_appointment_with_invalid_patientId() throws JsonProcessingException {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setPatientId(999999);  // non-existing patient
        dto.setDoctorId(lastDoctorId);
        dto.setScheduleId(lastScheduleId);

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/appointments")
                .then().extract().response();

    }

    // --- MISSING DOCTOR ID ---
    @When("I create an appointment without doctorId")
    public void i_create_appointment_without_doctorId() throws JsonProcessingException {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setPatientId(existingPatientId);
        dto.setScheduleId(lastScheduleId);
// doctorId intentionally omitted

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/appointments")
                .then().extract().response();

    }

    // --- INVALID DOCTOR ID ---
    @When("I create an appointment with an invalid doctorId")
    public void i_create_appointment_with_invalid_doctorId() throws JsonProcessingException {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setDoctorId(999999);  // non-existing doctor
        dto.setPatientId(existingPatientId);
        dto.setScheduleId(lastScheduleId);

        requestBody = objectMapper.writeValueAsString(dto);

        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .body(requestBody)
                .post(RestAssured.baseURI + "/appointments")
                .then().extract().response();

    }

    @When("I get all appointments")
    public void i_get_all_appointments() {
        response = given()
                .cookie("session_id", sessionId)
                .get(RestAssured.baseURI + "/appointments")
                .then().extract().response();
    }

    @When("I get all appointments without a session")
    public void i_get_all_appointments_without_session() {
        response = given()
                .get(RestAssured.baseURI + "/appointments")
                .then().extract().response();
    }

    @When("I get all appointments with a non-existing session_id")
    public void i_get_all_appointments_with_non_existing_session() {
        response = given()
                .cookie("session_id", "999999")
                .get(RestAssured.baseURI + "/appointments")
                .then().extract().response();
    }

    @When("I get appointment by id")
    public void i_get_appointment_by_id() {
        response = given()
                .cookie("session_id", sessionId)
                .get(RestAssured.baseURI + "/appointments/" + appointmentId)
                .then().extract().response();
    }

    @When("I get appointment by id without a session")
    public void i_get_appointment_by_id_without_session() {
        response = given()
                .get(RestAssured.baseURI + "/appointments/" + appointmentId)
                .then().extract().response();
    }

    @When("I get appointment by id with a malformed session_id")
    public void i_get_appointment_by_id_with_malformed_session() {
        response = given()
                .cookie("session_id", "abc123")
                .get(RestAssured.baseURI + "/appointments/" + appointmentId)
                .then().extract().response();
    }

    @When("I get appointment by id with a non-existing session_id")
    public void i_get_appointment_by_id_with_non_existing_session() {
        response = given()
                .cookie("session_id", "999999999")
                .get(RestAssured.baseURI + "/appointments/" + appointmentId)
                .then().extract().response();
    }

    @When("I get appointment with invalid id")
    public void i_get_appointment_with_invalid_id() {
        int invalidId = 999999999;
        response = given()
                .cookie("session_id", sessionId)
                .get(RestAssured.baseURI + "/appointments/" + invalidId)
                .then().extract().response();
    }

    @When("I get appointment outcome by id")
    public void i_get_appointment_outcome_by_id() {
        System.out.println(appointmentId);
        response = given()
                .cookie("session_id", sessionId)
                .get(RestAssured.baseURI + "/appointments/" + appointmentId + "/outcome")
                .then().extract().response();
    }

    @When("I get appointment outcome by id without a session")
    public void i_get_appointment_outcome_by_id_without_session() {
        response = given()
                .get(RestAssured.baseURI + "/appointments/" + appointmentId + "/outcome")
                .then().extract().response();
    }

    @When("I get appointment outcome by id with a malformed session_id")
    public void i_get_appointment_outcome_by_id_with_malformed_session() {
        response = given()
                .cookie("session_id", "abc123")
                .get(RestAssured.baseURI + "/appointments/" + appointmentId + "/outcome")
                .then().extract().response();
    }

    @When("I get appointment outcome by id with a non-existing session_id")
    public void i_get_appointment_outcome_by_id_with_a_non_existing_session() {
        response = given()
                .cookie("session_id", "999999999")
                .get(RestAssured.baseURI + "/appointments/" + appointmentId + "/outcome")
                .then().extract().response();
    }

    @When("I get appointment outcome with invalid id")
    public void i_get_appointment_outcome_with_invalid_id() {
        int invalidId = 999999999;

        response = given()
                .cookie("session_id", sessionId)
                .get(RestAssured.baseURI + "/appointments/" + invalidId + "/outcome")
                .then().extract().response();
    }

    @When("I update appointment status to {string}")
    public void i_update_appointment_status(String status) {
        System.out.println(status);
        response = given()
                .cookie("session_id", sessionId)
                .patch(RestAssured.baseURI + "/appointments/" + appointmentId + "/" + status)
                .then().extract().response();
    }

    @When("I update appointment status to {string} with a malformed session_id")
    public void i_update_status_with_malformed_session(String status) {
        response = given()
                .cookie("session_id", "abc123")
                .patch(RestAssured.baseURI + "/appointments/" + appointmentId + "/" + status)
                .then().extract().response();
    }

    @When("I update appointment status to {string} with a non-existing session_id")
    public void i_update_status_with_non_existing_session(String status) {
        response = given()
                .cookie("session_id", "999999")
                .patch(RestAssured.baseURI + "/appointments/" + appointmentId + "/" + status)
                .then().extract().response();
    }

    @When("I update appointment status with invalid id")
    public void i_update_status_with_invalid_id() {
        int invalidId = 999999999;
        response = given()
                .cookie("session_id", sessionId)
                .patch(RestAssured.baseURI + "/appointments/" + invalidId + "/COMPLETED")
                .then().extract().response();
    }

    // GET doctor by id
    @When("I get the doctor by id")
    public void i_get_the_doctor_by_id() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .get(RestAssured.baseURI + "/doctors/" + existingDoctorId)
                .then().extract().response();
    }

    @When("I get the doctor by id without session")
    public void i_get_the_doctor_by_id_without_session() {
        response = given()
                .header("Content-Type", "application/json")
                .get(RestAssured.baseURI + "/doctors/" + existingDoctorId)
                .then().extract().response();
    }

    @When("I get the doctor by id with invalid session")
    public void i_get_the_doctor_by_id_with_invalid_session() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", "invalid_session")
                .get(RestAssured.baseURI + "/doctors/" + existingDoctorId)
                .then().extract().response();
    }

    @When("I get the doctor by id with non-existent id")
    public void i_get_the_doctor_by_id_with_nonexistent_id() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .get(RestAssured.baseURI + "/doctors/999999")
                .then().extract().response();
    }

    // GET all doctors
    @When("I get all doctors")
    public void i_get_all_doctors() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .get(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }

    @When("I get all doctors without session")
    public void i_get_all_doctors_without_session() {
        response = given()
                .header("Content-Type", "application/json")
                .get(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }

    @When("I get all doctors with invalid session")
    public void i_get_all_doctors_with_invalid_session() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", "invalid_session")
                .get(RestAssured.baseURI + "/doctors")
                .then().extract().response();
    }

    // GET doctors by service
    @When("I get doctors by service")
    public void i_get_doctors_by_service() {
        System.out.println(lastServiceId);
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .get(RestAssured.baseURI + "/doctors/service/" + lastServiceId)
                .then().extract().response();
    }

    @When("I get doctors by service without session")
    public void i_get_doctors_by_service_without_session() {
        response = given()
                .header("Content-Type", "application/json")
                .get(RestAssured.baseURI + "/doctors/service/" + staleDoctorSpecializationId)
                .then().extract().response();
    }

    @When("I get doctors by service with invalid session")
    public void i_get_doctors_by_service_with_invalid_session() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", "invalid_session")
                .get(RestAssured.baseURI + "/doctors/service/" + staleDoctorSpecializationId)
                .then().extract().response();
    }

    @When("I get doctors by service with non-existent service id")
    public void i_get_doctors_by_service_with_nonexistent_service() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .get(RestAssured.baseURI + "/doctors/service/999999")
                .then().extract().response();
    }

    // GET doctor id by credential
    @When("I get the doctor id by credential")
    public void i_get_doctor_id_by_credential() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .get(RestAssured.baseURI + "/doctors/credential/" + staleCredentialId)
                .then().extract().response();
    }

    @When("I get the doctor id by credential without session")
    public void i_get_doctor_id_by_credential_without_session() {
        response = given()
                .header("Content-Type", "application/json")
                .get(RestAssured.baseURI + "/doctors/credential/")
                .then().extract().response();
    }

    @When("I get the doctor id by credential with invalid session")
    public void i_get_doctor_id_by_credential_with_invalid_session() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", "invalid_session")
                .get(RestAssured.baseURI + "/doctors/credential/" + "b")
                .then().extract().response();
    }

    @When("I get the doctor id by credential with wrong credential id")
    public void i_get_doctor_id_by_credential_with_wrong_credential_id() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .get(RestAssured.baseURI + "/doctors/credential/999999") // wrong credential
                .then().extract().response();
    }

    @Given("An existing specialization is available")
    public void an_existing_specialization_is_available() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .get(RestAssured.baseURI + "/specializations")
                .then().extract().response();

        List<Map<String, Object>> list = response.jsonPath().getList("$");
        lastSpecializationId = (Integer) list.get(0).get("id");
    }

    @When("I get all specializations")
    public void i_get_all_specializations() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .get(RestAssured.baseURI + "/specializations")
                .then().extract().response();
    }

    @When("I get all specializations without a session")
    public void i_get_all_specializations_without_session() {
        response = given()
                .header("Content-Type", "application/json")
                .get(RestAssured.baseURI + "/specializations")
                .then().extract().response();
    }

    @When("I get all specializations with an invalid session_id")
    public void i_get_all_specializations_with_invalid_session_id() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", "99999999")
                .get(RestAssured.baseURI + "/specializations")
                .then().extract().response();
    }

    @When("I get all specializations with a malformed session_id")
    public void i_get_all_specializations_with_malformed_session_id() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", "notANumber")
                .get(RestAssured.baseURI + "/specializations")
                .then().extract().response();
    }

    @When("I get specialization by id")
    public void i_get_specialization_by_id() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .get(RestAssured.baseURI + "/specializations/" + lastSpecializationId)
                .then().extract().response();
    }

    @When("I get specialization by id without a session")
    public void i_get_specialization_by_id_without_session() {
        response = given()
                .header("Content-Type", "application/json")
                .get(RestAssured.baseURI + "/specializations/" + lastSpecializationId)
                .then().extract().response();
    }

    @When("I get specialization by id with a malformed session_id")
    public void i_get_specialization_by_id_with_malformed_session_id() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", "invalid-format")
                .get(RestAssured.baseURI + "/specializations/" + lastSpecializationId)
                .then().extract().response();
    }

    @When("I get specialization by id with an invalid session_id")
    public void i_get_specialization_by_id_with_invalid_session_id() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", "5")
                .get(RestAssured.baseURI + "/specializations/" + lastSpecializationId)
                .then().extract().response();
    }

    @When("I get specialization by id with a non-existing id")
    public void i_get_specialization_by_id_with_non_existing_id() {
        response = given()
                .header("Content-Type", "application/json")
                .cookie("session_id", sessionId)
                .get(RestAssured.baseURI + "/specializations/999999")
                .then().extract().response();
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
