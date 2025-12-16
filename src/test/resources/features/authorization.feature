Feature: Authorization session lifecycle

  Background:
    Given The correct API URL

  Scenario: Validate and delete an authorization session
    Given The Login endpoint is "/authorization/"
    And I have credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    When I send a POST request to validate the session
    Then The response status code should be 200

    When I send a DELETE request to logout
    Then The response status code should be 204
