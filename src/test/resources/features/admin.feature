Feature: Healthcare API - Flows

  Background:
    Given The correct API URL

  Scenario: Admin logs in
    Given The Login endpoint is "/authorization/"
    And I have admin credentials "greatadmin@gmail.com" and "00000"
    When I send a POST request to login
    Then The response status code should be 401

    And I have admin credentials "greattubby@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 401

    And I have admin credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"
#  ---------------------------------------------------------------------------------------------------------------------

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

  Scenario: Admin logs in and deletes the patient
    Given The Login endpoint is "/authorization/"
    And I have admin credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    Given An existing patient is available
    When I delete the patient
    Then The response status code should be 204
    And The delete patient response has correct data

    When I delete the patient without a session
    Then The response status code should be 401
    And The response should contain message "Not authorized"

    When I delete the patient with an invalid session_id
    Then The response status code should be 403
    And The response should contain message "Forbidden to access resource"

    When I delete the patient without an id
    Then The response status code should be 405
#  ---------------------------------------------------------------------------------------------------------------------

  Scenario: Admin logs in and creates a service
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

    When I create new service without a session
    Then The response status code should be 401
    And The response should contain message "Not authorized"

    When I create new service with an invalid session_id
    Then The response status code should be 403
    And The response should contain message "Forbidden to access resource"

    When I create new service with empty name
    Then The response status code should be 400
    And The response should contain message "Name has the wrong format"

    When I create new service with a duplicate name
    Then The response status code should be 400
    And The response should contain message "Name already exists"

    When I create new service with invalid price
    Then The response status code should be 400
    And The response should contain message "Price has to be over 0"

  Scenario: Admin logs in and updates a service
    Given The Login endpoint is "/authorization/"
    And I have admin credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    Given An existing service is available
    When I update the service name
    Then The response status code should be 200
    And The service response has correct data

    When I update the service without a session
    Then The response status code should be 401
    And The response should contain message "Not authorized"

    When I update the service with an invalid session_id
    Then The response status code should be 403
    And The response should contain message "Forbidden to access resource"

    When I update the service with an empty name
    Then The response status code should be 400
    And The response should contain message "Name has the wrong format"

    When I update the service with a duplicate name
    Then The response status code should be 400
    And The response should contain message "Name already exists"

    When I update the service with invalid price
    Then The response status code should be 400
    And The response should contain message "Price has to be over 0"

  Scenario: Admin logs in and deletes a service
    Given The Login endpoint is "/authorization/"
    And I have admin credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    Given An existing service is available
    When I delete the service
    Then The response status code should be 204

    When I delete the service without a session
    Then The response status code should be 401
    And The response should contain message "Not authorized"

    When I delete the service with an invalid session_id
    Then The response status code should be 403
    And The response should contain message "Forbidden to access resource"

    When I delete a service with invalid id
    Then The response status code should be 404

    When I delete a service without an id
    Then The response status code should be 405

#  ---------------------------------------------------------------------------------------------------------------------

  Scenario: Admin logs in and creates the doctor
    Given The Login endpoint is "/authorization/"
    And I have admin credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    When I create new doctor
    Then The response status code should be 201
    And The response JSON should be valid
    And The doctor response has correct data

    When I create new doctor without a session
    Then The response status code should be 401
    And The response should contain message "Not authorized"

    When I create new doctor with an invalid session_id
    Then The response status code should be 403
    And The response should contain message "Forbidden to access resource"

    When I create new doctor with empty first name
    Then The response status code should be 400
    And The response should contain message "First name has the wrong format"

    When I create new doctor with empty last name
    Then The response status code should be 400
    And The response should contain message "Last name has the wrong format"

    When I create new doctor with empty email
    Then The response status code should be 400
    And The response should contain message "Email is either of the wrong format or already exists"

    When I create new doctor with duplicate email
    Then The response status code should be 400
    And The response should contain message "Email is either of the wrong format or already exists"

    When I create new doctor with empty phone
    Then The response status code should be 400
    And The response should contain message "Phone number is either of the wrong format or already exists"

    When I create new doctor with duplicate phone
    Then The response status code should be 400
    And The response should contain message "Phone number is either of the wrong format or already exists"

  Scenario: Admin logs in and updates the doctor
    Given The Login endpoint is "/authorization/"
    And I have admin credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    Given An existing doctor is available
    And Another doctor exists for duplicate tests

    When I update the doctor's first name
    Then The response status code should be 200
    And The doctor response has correct data

    When I update the doctor without a session
    Then The response status code should be 401
    And The response should contain message "Not authorized"

    When I update the doctor with an invalid session_id
    Then The response status code should be 403
    And The response should contain message "Forbidden to access resource"

    When I update the doctor with empty first name
    Then The response status code should be 400
    And The response should contain message "First name has the wrong format"

    When I update the doctor with empty last name
    Then The response status code should be 400
    And The response should contain message "Last name has the wrong format"

    When I update the doctor with empty email
    Then The response status code should be 400
    And The response should contain message "Email is either of the wrong format or already exists"

    When I update the doctor with duplicate email
    Then The response status code should be 400
    And The response should contain message "Email is either of the wrong format or already exists"

    When I update the doctor with empty phone
    Then The response status code should be 400
    And The response should contain message "Phone number is either of the wrong format or already exists"

    When I update the doctor with duplicate phone
    Then The response status code should be 400
    And The response should contain message "Phone number is either of the wrong format or already exists"


  Scenario: Admin logs in and deletes the doctor
    Given The Login endpoint is "/authorization/"
    And I have admin credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    Given An existing doctor is available
    When I delete the doctor
    Then The response status code should be 204

    When I delete the doctor without a session
    Then The response status code should be 401
    And The response should contain message "Not authorized"

    When I delete the doctor with an invalid session_id
    Then The response status code should be 403
    And The response should contain message "Forbidden to access resource"

    When I delete a doctor with invalid id
    Then The response status code should be 400
    And The response should contain message "Incorrect/absent id"

    When I delete a doctor without an id
    Then The response status code should be 405

#  ---------------------------------------------------------------------------------------------------------------------

  Scenario: Admin logs in and creates a doctor schedule
    Given The Login endpoint is "/authorization/"
    And I have admin credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    Given An existing doctor is available for schedule
    When I create a valid doctor schedule
    Then The response status code should be 200
    And The doctor schedule response has correct data

    When I create a doctor schedule without a session
    Then The response status code should be 401
    And The response should contain message "Not authorized"

    When I create a doctor schedule with an invalid session_id
    Then The response status code should be 403
    And The response should contain message "Forbidden to access resource"

    When I create a doctor schedule with a past date
    Then The response status code should be 400
    And The response should contain message "Date must be not be in the past"

    When I create a doctor schedule with invalid time range
    Then The response status code should be 400
    And The response should contain message "Invalid start/end time"

    When I create a doctor schedule that overlaps
    Then The response status code should be 400
    And The response should contain message "Time overlaps with an existing schedule"


  Scenario: Admin logs in and updates a doctor schedule
    Given The Login endpoint is "/authorization/"
    And I have admin credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    Given An existing doctor is available for schedule
    And An existing doctor schedule is available
    And Another doctor schedule exists for overlapping tests

    When I update the doctor schedule
    Then The response status code should be 200
    And The response JSON should be valid

    When I update the doctor schedule without a session
    Then The response status code should be 401
    And The response should contain message "Not authorized"

    When I update the doctor schedule with an invalid session_id
    Then The response status code should be 403
    And The response should contain message "Forbidden to access resource"

    When I update the doctor schedule with a past date
    Then The response status code should be 400
    And The response should contain message "Date must be not be in the past"

    When I update the doctor schedule with invalid start or end time
    Then The response status code should be 400
    And The response should contain message "Invalid start/end time"

    When I update the doctor schedule to an overlapping time
    Then The response status code should be 400
    And The response should contain message "Time overlaps with an existing schedule"

  Scenario: Admin logs in and deletes a doctor schedule
    Given The Login endpoint is "/authorization/"
    And I have admin credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    Given An existing doctor is available
    And An existing doctor schedule is available

    When I delete the doctor schedule
    Then The response status code should be 204

    When I delete the doctor schedule without a session
    Then The response status code should be 401
    And The response should contain message "Not authorized"

    When I delete the doctor schedule with an invalid session_id
    Then The response status code should be 403
    And The response should contain message "Forbidden to access resource"

    When I delete a doctor schedule with invalid id
    Then The response status code should be 400
    And The response should contain message "Incorrect/absent id"

    When I delete a doctor schedule without a schedule id
    Then The response status code should be 405


