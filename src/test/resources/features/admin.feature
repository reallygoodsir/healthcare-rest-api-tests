Feature: Healthcare API - Flows

  Background:
    Given The correct API URL

  Scenario: Admin logs in
    Given The Login endpoint is "/authorization/"
    And I have credentials "greatadmin@gmail.com" and "00000"
    When I send a POST request to login
    Then The response status code should be 401

    And I have credentials "greattubby@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 401

    And I have credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"
#  ---------------------------------------------------------------------------------------------------------------------

  Scenario: Admin logs in and manages service successfully
    Given The Login endpoint is "/authorization/"
    And I have credentials "greatadmin@gmail.com" and "73629175"
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
#  ---------------------------------------------------------------------------------------------------------------------

  Scenario: Admin logs in and creates the patient
    Given The Login endpoint is "/authorization/"
    And I have credentials "greatadmin@gmail.com" and "73629175"
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
    And The response should contain message "Session id is empty"

    When I create new patient with an invalid session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id does not exist"

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
    And The response should contain message "Email has the wrong format"

    When I create new patient with an invalid email format
    Then The response status code should be 400
    And The response should contain message "Email has the wrong format"

    When I create new patient with a duplicate email
    Then The response status code should be 400
    And The response should contain message "Email already exists"

    When I create new patient with an empty phone
    Then The response status code should be 400
    And The response should contain message "Phone number has the wrong format"

    When I create new patient with an invalid phone format
    Then The response status code should be 400
    And The response should contain message "Phone number has the wrong format"

    When I create new patient with a duplicate phone
    Then The response status code should be 400
    And The response should contain message "Phone number already exists"

  Scenario: Admin logs in and updates the patient
    Given The Login endpoint is "/authorization/"
    And I have credentials "greatadmin@gmail.com" and "73629175"
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
    And The response should contain message "Session id is empty"

    When I update the patient with an invalid session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id does not exist"

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
    And The response should contain message "Email has the wrong format"

    When I update the patient with an invalid email format
    Then The response status code should be 400
    And The response should contain message "Email has the wrong format"

    Given Another patient exists for duplicate tests
    When I update the patient with an email which exists
    Then The response status code should be 400
    And The response should contain message "Email already exists"

    When I update the patient with an empty phone
    Then The response status code should be 400
    And The response should contain message "Phone number has the wrong format"

    When I update the patient with an invalid phone format
    Then The response status code should be 400
    And The response should contain message "Phone number has the wrong format"

    When I update the patient with an existing phone
    Then The response status code should be 400
    And The response should contain message "Phone number already exists"

  Scenario: Admin logs in and deletes the patient
    Given The Login endpoint is "/authorization/"
    And I have credentials "greatadmin@gmail.com" and "73629175"
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
    And The response should contain message "Session id is empty"

    When I delete the patient with an invalid session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id does not exist"

    When I delete the patient without an id
    Then The response status code should be 405

  Scenario: Admin gets all patients
    Given The Login endpoint is "/authorization/"
    And I have credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    When I get all patients
    Then The response status code should be 200
    And The response JSON should be a valid list

    When I get all patients without a session
    Then The response status code should be 401
    And The response should contain message "Session id is empty"

    When I get all patients with malformed session id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id has incorrect format"

    When I get all patients with non existing session
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id does not exist"


  Scenario: Patient gets own patient id by credential
    Given The Login endpoint is "/authorization/"
    And I have credentials "gray@gmail.com" and "64986912"
    When I send a POST request to login
    Then The response status code should be 200
    And The response should contain role "PATIENT"
    And The credential id should be saved

    When I get patient id by credential
    Then The response status code should be 200

    When I get patient id by credential without session
    Then The response status code should be 401
    And The response should contain message "Session id is empty"

    When I get patient id by credential with malformed session id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id has incorrect format"

    When I get patient id by credential with non existing credential id
    Then The response status code should be 400
    And The response should contain message "Credential id does not exist"


  Scenario: Doctor gets patient by id

    Given The Login endpoint is "/authorization/"
    Given I have credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    Given An existing patient is available

    When I clear the session
    Then The session should be empty

    And I have credentials "adrian@gmail.com" and "39963516"
    When I send a POST request to login
    Then The response status code should be 200
    And The response should contain role "DOCTOR"

    When I get patient by id
    Then The response status code should be 200
    And The response JSON should be valid

    When I get patient by id without session
    Then The response status code should be 401
    And The response should contain message "Session id is empty"

    When I get patient by id with a malformed session
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id has incorrect format"

    When I get patient by id with a non-existing session
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id does not exist"

    When I get patient by id with non existing id
    Then The response status code should be 400
    And The response should contain message "Patient id does not exist"


  Scenario: Call center agent gets patient by phone number
    Given The Login endpoint is "/authorization/"
    Given I have credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    Given An existing patient is available

    When I clear the session
    Then The session should be empty

    And I have credentials "ccaguy@gmail.com" and "18923574"
    When I send a POST request to login
    Then The response status code should be 200
    And The response should contain role "CALL_CENTER_AGENT"

    When I get patient by phone number
    Then The response status code should be 200
    And The response JSON should be valid

    When I get patient by phone with non existing phone
    Then The response status code should be 400
    And The response should contain message "Phone number does not exist"

#  ---------------------------------------------------------------------------------------------------------------------

  Scenario: Admin logs in and creates a service
    Given The Login endpoint is "/authorization/"
    And I have credentials "greatadmin@gmail.com" and "73629175"
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
    And The response should contain message "Session id is empty"

    When I create new service with an invalid session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id does not exist"

    When I create new service with a malformed session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id has incorrect format"

    When I create new service with empty name
    Then The response status code should be 400
    And The response should contain message "Service name has the wrong format"

    When I create new service with a duplicate name
    Then The response status code should be 400
    And The response should contain message "Service name already exists"

    When I create new service with invalid price
    Then The response status code should be 400
    And The response should contain message "Price has to be over 0"

  Scenario: Admin logs in and updates a service
    Given The Login endpoint is "/authorization/"
    And I have credentials "greatadmin@gmail.com" and "73629175"
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
    And The response should contain message "Session id is empty"

    When I update the service with an invalid session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id does not exist"

    When I update the service with a malformed session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id has incorrect format"

    When I update the service with an empty name
    Then The response status code should be 400
    And The response should contain message "Service name has the wrong format"

    When I update the service with a duplicate name
    Then The response status code should be 400
    And The response should contain message "Service name already exists"

    When I update the service with invalid price
    Then The response status code should be 400
    And The response should contain message "Price has to be over 0"

  Scenario: Admin logs in and deletes a service
    Given The Login endpoint is "/authorization/"
    And I have credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    Given An existing service is available
    When I delete the service
    Then The response status code should be 204

    When I delete the service without a session
    Then The response status code should be 401
    And The response should contain message "Session id is empty"

    When I delete the service with an invalid session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id does not exist"

    When I delete the service with a malformed session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id has incorrect format"

    When I delete a service with invalid id
    Then The response status code should be 400

    When I delete a service without an id
    Then The response status code should be 405

  Scenario: Admin gets all services
    Given The Login endpoint is "/authorization/"
    And I have credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    When I get all services
    Then The response status code should be 200
    And The response JSON should be a valid list

    When I get all services without a session
    Then The response status code should be 401
    And The response should contain message "Session id is empty"

    When I get all services with malformed session id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id has incorrect format"

    When I get all services with non existing session
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id does not exist"


  Scenario: Admin gets service by id
    Given The Login endpoint is "/authorization/"
    And I have credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    Given An existing service is available
    When I get service by id
    Then The response status code should be 200
    And The response JSON should be valid

    When I get service by id without a session
    Then The response status code should be 401
    And The response should contain message "Session id is empty"

    When I get service by id with malformed session id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id has incorrect format"

    When I get service by id with non existing session
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id does not exist"

    When I get service by id with non existing id
    Then The response status code should be 400
    And The response should contain message "Service id does not exist"

#  ---------------------------------------------------------------------------------------------------------------------

  Scenario: Admin logs in and creates a doctor schedule
    Given The Login endpoint is "/authorization/"
    And I have credentials "greatadmin@gmail.com" and "73629175"
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
    And The response should contain message "Session id is empty"

    When I create a doctor schedule with an invalid session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id does not exist"

    When I create a doctor schedule with a malformed session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id has incorrect format"

    When I create a doctor schedule with a past date
    Then The response status code should be 400
    And The response should contain message "Date must not be in the past"

    When I create a doctor schedule with invalid time range
    Then The response status code should be 400
    And The response should contain message "Invalid start/end time"

    When I create a doctor schedule that overlaps
    Then The response status code should be 400
    And The response should contain message "Time overlaps with an existing schedule"

    When I create a doctor schedule with empty doctor id
    Then The response status code should be 400
    And The response should contain message "Doctor id must not be empty when new schedule is created"

    When I create a doctor schedule with non existing doctor id
    Then The response status code should be 400
    And The response should contain message "Doctor id does not exist"

    When I clear the session
    Then The session should be empty

    Given I have credentials "ccaguy@gmail.com" and "18923574"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "CALL_CENTER_AGENT"

    When I create a doctor schedule with the wrong role
    Then The response status code should be 403
    And The response should contain message "Forbidden to access resource. Role is not allowed."


  Scenario: Admin logs in and updates a doctor schedule
    Given The Login endpoint is "/authorization/"
    And I have credentials "greatadmin@gmail.com" and "73629175"
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
    And The response should contain message "Session id is empty"

    When I update the doctor schedule with an invalid session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id does not exist"

    When I update the doctor schedule with a malformed session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id has incorrect format"

    When I update the doctor schedule with a past date
    Then The response status code should be 400
    And The response should contain message "Date must not be in the past"

    When I update the doctor schedule with invalid start or end time
    Then The response status code should be 400
    And The response should contain message "Invalid start/end time"

    When I update the doctor schedule to an overlapping time
    Then The response status code should be 400
    And The response should contain message "Time overlaps with an existing schedule"

    When I update the doctor schedule with empty schedule id
    Then The response status code should be 400
    And The response should contain message "Doctor schedule id must not be empty when existing schedule is updated"

    When I update the doctor schedule with non existing schedule id
    Then The response status code should be 400
    And The response should contain message "Schedule id does not exist"

    When I update the doctor schedule with empty doctor id
    Then The response status code should be 400
    And The response should contain message "Doctor id must not be empty when existing doctor is updated"

    When I update the doctor schedule with non existing doctor id
    Then The response status code should be 400
    And The response should contain message "Doctor id does not exist"

    When I clear the session
    Then The session should be empty
    Given I have credentials "ccaguy@gmail.com" and "18923574"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "CALL_CENTER_AGENT"

    When I update the doctor schedule
    Then The response status code should be 403
    And The response should contain message "Forbidden to access resource. Role is not allowed."


  Scenario: Admin logs in and deletes a doctor schedule
    Given The Login endpoint is "/authorization/"
    And I have credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    Given An existing doctor is available for schedule
    And An existing doctor schedule is available

    When I delete the doctor schedule
    Then The response status code should be 204

    When I delete the doctor schedule without a session
    Then The response status code should be 401
    And The response should contain message "Session id is empty"

    When I delete the doctor schedule with an invalid session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id does not exist"

    When I delete the doctor schedule with a malformed session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id has incorrect format"

    When I delete a doctor schedule with invalid id
    Then The response status code should be 400
    And The response should contain message "Schedule id does not exist"

    When I clear the session
    Then The session should be empty
    Given I have credentials "ccaguy@gmail.com" and "18923574"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "CALL_CENTER_AGENT"

    When I delete the doctor schedule
    Then The response status code should be 403
    And The response should contain message "Forbidden to access resource. Role is not allowed."



  Scenario: Admin gets schedules by doctor
    Given The Login endpoint is "/authorization/"
    And I have credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    Given An existing doctor is available for schedule
    And An existing doctor schedule is available

    When I get schedules by doctor
    Then The response status code should be 200
    And The response JSON should be a valid list

    When I get schedules by doctor without a session
    Then The response status code should be 401
    And The response should contain message "Session id is empty"

    When I get schedules by doctor with an invalid session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id does not exist"

    When I get schedules by doctor with a malformed session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id has incorrect format"

    When I clear the session
    Then The session should be empty
    Given I have credentials "adrian@gmail.com" and "39963516"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "DOCTOR"

    When I get schedules by doctor
    Then The response status code should be 403
    And The response should contain message "Forbidden to access resource. Role is not allowed."

    When I clear the session
    Then The session should be empty
    And I have credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    When I delete the doctor schedule
    Then The response status code should be 204


  Scenario: Doctor gets schedules with appointments
    Given The Login endpoint is "/authorization/"
    And I have credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    Given An existing doctor is available for schedule
    And An existing doctor schedule is available

    When I clear the session
    Then The session should be empty
    And I have credentials "adrian@gmail.com" and "39963516"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "DOCTOR"

    When I get schedules with appointments
    Then The response status code should be 200

    When I get schedules with appointments without a session
    Then The response status code should be 401
    And The response should contain message "Session id is empty"

    When I get schedules with appointments with an invalid session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id does not exist"

    When I get schedules with appointments with a malformed session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id has incorrect format"

    When I clear the session
    Then The session should be empty
    And I have credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    When I get schedules with appointments
    Then The response status code should be 403

    When I delete the doctor schedule
    Then The response status code should be 204
    When I delete the doctor
    Then The response status code should be 204