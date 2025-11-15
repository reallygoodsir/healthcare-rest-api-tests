Feature: Healthcare API - Login Flows

  Background:
    Given The correct API URL

  Scenario: Admin logs in, checks session, and logs out successfully
    Given The Login endpoint is "/authorization/"
    And I have admin credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    Given The Check Session endpoint is "/authorization/check-session"
    When I send a POST request to check session
    Then The response status code should be 200
    And The session should be valid and contain role "ADMIN"

    When I send a DELETE request to log out
    Then The response status code should be 204
    And I logged out successfully

  Scenario: Log in with bad password
  Scenario: Log in with bad email

