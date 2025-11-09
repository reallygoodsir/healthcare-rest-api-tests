Feature: Healthcare API - Flows

  Background:
    Given The correct API URL

  Scenario: Admin logs in and creates doctor, patient, and service successfully
    Given The Login endpoint is "/authorization/"
    And I have admin credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    When I create new service
    Then The response status code should be 200
    And The response JSON should be valid
    And The service response has correct data

    When I create new doctor
    Then The response status code should be 201
    And The response JSON should be valid
    And The doctor response has correct data

    When I create new patient
    Then The response status code should be 201
    And The response JSON should be valid
    And The patient response has correct data