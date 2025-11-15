Feature: Healthcare API - Flows

  Background:
    Given The correct API URL

  Scenario: Admin logs in and manages service successfully
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

    When I update the service
    Then The response status code should be 200
    And The response JSON should be valid
    And The service response has correct data after update

    When I get all services
    Then The service with id should exist

    When I delete the service
    Then The response status code should be 204

  Scenario: Admin logs in and creates the patient
    Given The Login endpoint is "/authorization/"
    And I have admin credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    When I create new patient
    Then The response status code should be 201
    And The response JSON should be valid
    And The patient response has correct data

    When I create new patient without a session
    Then The response status code should be 401
    And The response should contain message "Not authorized"

    When I create new patient with an invalid session_id
    Then The response status code should be 403
    And The response should contain message "Forbidden to access resource"

    When I create new patient with empty first name
    Then The response status code should be 400
    And The response should contain message "First name has the wrong format"

    When I create new patient with an empty last name
    Then The response status code should be 400
    And The response should contain message "Last name has the wrong format"

    When I create new patient without an address
    Then The response status code should be 400
    And The response should contain message "No Address provided"

    When I create new patient with an invalid birth date
    Then The response status code should be 400
    And The response should contain message "Unfitting date of birth"

    When I create new patient with an empty email
    Then The response status code should be 400
    And The response should contain message "Email is either of the wrong format or already exists"

    When I create new patient with an invalid email format
    Then The response status code should be 400
    And The response should contain message "Email is either of the wrong format or already exists"

    When I create new patient with a duplicate email
    Then The response status code should be 400
    And The response should contain message "Email is either of the wrong format or already exists"

    When I create new patient with an empty phone
    Then The response status code should be 400
    And The response should contain message "Phone number is either of the wrong format or already exists"

    When I create new patient with an invalid phone format
    Then The response status code should be 400
    And The response should contain message "Phone number is either of the wrong format or already exists"

    When I create new patient with a duplicate phone
    Then The response status code should be 400
    And The response should contain message "Phone number is either of the wrong format or already exists"

  Scenario: Admin logs in and updates the patient
    Given The Login endpoint is "/authorization/"
    And I have admin credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    Given An existing patient is available
    When I update the patient's first name
    Then The response status code should be 200
    And The patient response has correct data

    When I update the patient without a session
    Then The response status code should be 401
    And The response should contain message "Not authorized"

    When I update the patient with an invalid session_id
    Then The response status code should be 403
    And The response should contain message "Forbidden to access resource"

    When I update the patient with empty first name
    Then The response status code should be 400
    And The response should contain message "First name has the wrong format"

    When I update the patient with an empty last name
    Then The response status code should be 400
    And The response should contain message "Last name has the wrong format"

    When I update the patient without an address
    Then The response status code should be 400
    And The response should contain message "No Address provided"

    When I update the patient with an invalid birth date
    Then The response status code should be 400
    And The response should contain message "Unfitting date of birth"

    When I update the patient with an empty email
    Then The response status code should be 400
    And The response should contain message "Email is either of the wrong format or already exists"

    When I update the patient with an invalid email format
    Then The response status code should be 400
    And The response should contain message "Email is either of the wrong format or already exists"

    Given Another patient exists for duplicate tests
    When I update the patient with an email which exists
    Then The response status code should be 400
    And The response should contain message "Email is either of the wrong format or already exists"

    When I update the patient with an empty phone
    Then The response status code should be 400
    And The response should contain message "Phone number is either of the wrong format or already exists"

    When I update the patient with an invalid phone format
    Then The response status code should be 400
    And The response should contain message "Phone number is either of the wrong format or already exists"

    When I update the patient with an existing phone
    Then The response status code should be 400
    And The response should contain message "Phone number is either of the wrong format or already exists"



#    When I create new patient
#    Then The response status code should be 201
#    And The response JSON should be valid
#    And The patient response has correct data

#    When I create new doctor
#    Then The response status code should be 201
#    And The response JSON should be valid
#    And The doctor response has correct data
#
