Feature: Specializations retrieval

  Background:
    Given The correct API URL

  Scenario: Admin logs in and retrieves all specializations
    Given The Login endpoint is "/authorization/"
    And I have credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    When I get all specializations
    Then The response status code should be 200
    And The response JSON should be a valid list

    When I get all specializations without a session
    Then The response status code should be 401
    And The response should contain message "Session id is empty"

    When I get all specializations with an invalid session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id does not exist"

    When I get all specializations with a malformed session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id has incorrect format"

    When I clear the session
    Then The session should be empty

    Given I have credentials "adrian@gmail.com" and "39963516"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "DOCTOR"

    When I get all specializations
    Then The response status code should be 403
    And The response should contain message "Forbidden to access resource. Role is not allowed."


  Scenario: Admin retrieves specialization by id
    Given The Login endpoint is "/authorization/"
    And I have credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    Given An existing specialization is available

    When I get specialization by id
    Then The response status code should be 200
    And The response JSON should be valid

    When I get specialization by id without a session
    Then The response status code should be 401
    And The response should contain message "Session id is empty"

    When I get specialization by id with a malformed session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id has incorrect format"

    When I get specialization by id with an invalid session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id does not exist"

    When I get specialization by id with a non-existing id
    Then The response status code should be 400
    And The response should contain message "Specialization id does not exist"

    When I clear the session
    Then The session should be empty

    Given I have credentials "adrian@gmail.com" and "39963516"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "DOCTOR"

    When I get specialization by id
    Then The response status code should be 403
    And The response should contain message "Forbidden to access resource. Role is not allowed."
