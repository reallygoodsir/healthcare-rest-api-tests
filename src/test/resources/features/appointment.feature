Feature: Healthcare API - Flows

  Background:
    Given The correct API URL


  Scenario: Call center agent creates, updates, and deletes an appointment
    # --- ADMIN LOGIN AND SETUP ---
    Given The Login endpoint is "/authorization/"
    And I have credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    When I create new doctor
    Then The response status code should be 201
    And The response JSON should be valid
    And The doctor response has correct data

    When I create a valid doctor schedule
    Then The response status code should be 200
    And The doctor schedule response has correct data

    When I create new patient
    Then The response status code should be 201
    And The response JSON should be valid
    And The patient response has correct data


    When I clear the session
    Then The session should be empty

    Given I have credentials "ccaguy@gmail.com" and "18923574"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "CALL_CENTER_AGENT"

    # --- CREATE APPOINTMENT ---
    When I create an appointment
    Then The response status code should be 200
    And The response JSON should contain valid appointment data

    When I create an appointment without a session
    Then The response status code should be 401
    And The response should contain message "Session id is empty"

    When I create an appointment with an invalid session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id does not exist"

    When I clear the session
    Then The session should be empty

      # --- INVALID: SESSION ID WRONG FORMAT ---
    When I create an appointment with a malformed session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id has incorrect format"

    # --- INVALID: SESSION DOES NOT EXIST ---
    When I create an appointment with a non-existing session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id does not exist"

    # --- INVALID: ROLE NOT ALLOWED (ADMIN tries) ---
    Given I have credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200

    When I create an appointment as an admin
    Then The response status code should be 403
    And The response should contain message "Forbidden to access resource. Role is not allowed."

    When I clear the session
    Then The session should be empty

    # --- VALID CALL CENTER AGENT LOGIN AGAIN ---
    Given I have credentials "ccaguy@gmail.com" and "18923574"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "CALL_CENTER_AGENT"


    # --- INVALID: appointmentId SHOULD NOT EXIST ---
    When I create an appointment with a preexisting appointmentId
    Then The response status code should be 400
    And The response should contain message "The appointment should have no preexisting appointment id"

    # --- INVALID: status SHOULD NOT BE PROVIDED ---
    When I create an appointment with a preexisting status
    Then The response status code should be 400
    And The response should contain message "The appointment should have no preexisting status"

    # --- INVALID: patientId IS MISSING ---
    When I create an appointment without patientId
    Then The response status code should be 400
    And The response should contain message "No patient id provided"

    # --- INVALID: patientId DOES NOT EXIST ---
    When I create an appointment with an invalid patientId
    Then The response status code should be 400
    And The response should contain message "Incorrect patient id provided"

    # --- INVALID: doctorId IS MISSING ---
    When I create an appointment without doctorId
    Then The response status code should be 400
    And The response should contain message "No doctor id provided"

    # --- INVALID: doctorId DOES NOT EXIST ---
    When I create an appointment with an invalid doctorId
    Then The response status code should be 400
    And The response should contain message "Incorrect doctor id provided"

    # --- INVALID: scheduleId IS INVALID ---
    When I create an appointment with an invalid schedule id
    Then The response status code should be 400
    And The response should contain message "Incorrect schedule id provided"

    # --- INVALID: scheduleId DOES NOT EXIST OR NULL ---
    When I create an appointment without schedule id
    Then The response status code should be 400
    And The response should contain message "No schedule id provided"


    When I clear the session
    Then The session should be empty

    Given I have credentials "adrian@gmail.com" and "39963516"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "DOCTOR"

    When I create an appointment outcome
    Then The response status code should be 200
    And The response JSON should be valid

    When I create an appointment outcome without a session
    Then The response status code should be 401
    And The response should contain message "Session id is empty"

    When I create an appointment outcome with an invalid session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id does not exist"

    When I create an appointment outcome with a malformed session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id has incorrect format"

    When I create an appointment outcome with an invalid appointment_id
    Then The response status code should be 400
    And The response should contain message "Incorrect appointment id provided"

    When I clear the session
    Then The session should be empty

    Given I have credentials "ccaguy@gmail.com" and "18923574"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "CALL_CENTER_AGENT"

    When I create an appointment outcome
    Then The response status code should be 403
    And The response should contain message "Forbidden to access resource. Role is not allowed."

#     --- DELETE APPOINTMENT ---
    When I clear the session
    Then The session should be empty

    Given I have credentials "ccaguy@gmail.com" and "18923574"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "CALL_CENTER_AGENT"

    When I delete the appointment
    Then The response status code should be 204

    When I delete the appointment without a session
    Then The response status code should be 401
    And The response should contain message "Session id is empty"

    When I delete the appointment with an invalid session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id does not exist"

    When I delete the appointment with a malformed session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id has incorrect format"

    When I delete an appointment with invalid id
    Then The response status code should be 400
    And The response should contain message "Appointment id not found"

    When I delete an appointment without supplying an id
    Then The response status code should be 405

    When I clear the session
    Then The session should be empty

    Given I have credentials "adrian@gmail.com" and "39963516"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "DOCTOR"

    When I delete the appointment
    Then The response status code should be 403
    And The response should contain message "Forbidden to access resource. Role is not allowed."

  Scenario: Call center agent retrieves all appointments
        Given The Login endpoint is "/authorization/"
    Given I have credentials "ccaguy@gmail.com" and "18923574"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "CALL_CENTER_AGENT"

    When I get all appointments
    Then The response status code should be 200
    And The response JSON should be a valid list

    When I get all appointments without a session
    Then The response status code should be 401
    And The response should contain message "Session id is empty"

    When I get all appointments with a non-existing session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id does not exist"

    When I get all appointments with a malformed session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id has incorrect format"

    When I clear the session
    Then The session should be empty

    And I have credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    When I get all appointments
    Then The response status code should be 403
    And The response should contain message "Forbidden to access resource. Role is not allowed."

  Scenario: Doctor retrieves an appointment by id
        Given The Login endpoint is "/authorization/"
    And I have credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    When I create new doctor
    Then The response status code should be 201

    When I create a valid doctor schedule
    Then The response status code should be 200

    When I create new patient
    Then The response status code should be 201

    When I clear the session
    Then The session should be empty
    Given I have credentials "ccaguy@gmail.com" and "18923574"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "CALL_CENTER_AGENT"

    When I create an appointment
    Then The response status code should be 200
    And The response JSON should contain valid appointment data

    When I clear the session
    Then The session should be empty
    And I have credentials "adrian@gmail.com" and "39963516"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "DOCTOR"

    When I get appointment by id
    Then The response status code should be 200
    And The response JSON should be valid

    When I get appointment by id without a session
    Then The response status code should be 401
    And The response should contain message "Session id is empty"

    When I get appointment by id with a malformed session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id has incorrect format"

    When I get appointment by id with a non-existing session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id does not exist"

    When I get appointment with invalid id
    Then The response status code should be 500
    And The response should contain message "Failed to get an appointment by id"

    When I clear the session
    Then The session should be empty

    And I have credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The response should contain role "ADMIN"

    When I get appointment by id
    Then The response status code should be 403
    And The response should contain message "Forbidden to access resource. Role is not allowed."

  Scenario: Doctor retrieves appointment outcome
    Given The Login endpoint is "/authorization/"
    And I have credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    # --- PREPARE: CREATE SUPPORTING DATA ---
    When I create new doctor
    Then The response status code should be 201

    When I create a valid doctor schedule
    Then The response status code should be 200

    When I create new patient
    Then The response status code should be 201

    When I clear the session
    Then The session should be empty
    Given I have credentials "ccaguy@gmail.com" and "18923574"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "CALL_CENTER_AGENT"

    When I create an appointment
    Then The response status code should be 200
    And The response JSON should contain valid appointment data

    When I clear the session
    Then The session should be empty

  # --- LOGIN AS DOCTOR ---
    And I have credentials "adrian@gmail.com" and "39963516"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "DOCTOR"

    When I create an appointment outcome
    Then The response status code should be 200
    And The response JSON should be valid

  # --- SUCCESS: GET OUTCOME ---
    When I get appointment outcome by id
    Then The response status code should be 200
    And The response JSON should be valid

  # --- UNAUTHORIZED: NO SESSION ---
    When I get appointment outcome by id without a session
    Then The response status code should be 401
    And The response should contain message "Session id is empty"

  # --- MALFORMED SESSION ---
    When I get appointment outcome by id with a malformed session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id has incorrect format"

  # --- NON-EXISTING SESSION ---
    When I get appointment outcome by id with a non-existing session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id does not exist"

  # --- INVALID APPOINTMENT ID ---
    When I get appointment outcome with invalid id
    Then The response status code should be 500
    And The response should contain message "Failed to get an appointment outcome"

  # --- FORBIDDEN ROLE ---
    When I clear the session
    Then The session should be empty
    And I have credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The response should contain role "ADMIN"

    When I get appointment outcome by id
    Then The response status code should be 403
    And The response should contain message "Forbidden to access resource. Role is not allowed."

  Scenario: Doctor updates appointment status
    Given The Login endpoint is "/authorization/"
    And I have credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    When I create new doctor
    Then The response status code should be 201

    When I create a valid doctor schedule
    Then The response status code should be 200

    When I create new patient
    Then The response status code should be 201

    When I clear the session
    Then The session should be empty
    And I have credentials "ccaguy@gmail.com" and "18923574"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "CALL_CENTER_AGENT"

    When I create an appointment
    Then The response status code should be 200
    And The response JSON should contain valid appointment data

    When I clear the session
    Then The session should be empty
    And I have credentials "adrian@gmail.com" and "39963516"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "DOCTOR"

    When I update appointment status to "COMPLETED"
    Then The response status code should be 204

    When I update appointment status to "wontpass"
    Then The response status code should be 400
    And The response should contain message "Invalid status provided"

    When I update appointment status to ""
    Then The response status code should be 405

    When I update appointment status to "COMPLETED" without a session_id
    Then The response status code should be 401
    And The response should contain message "Session id is empty"

    When I update appointment status to "COMPLETED" with a malformed session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id has incorrect format"

    When I update appointment status to "COMPLETED" with a non-existing session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id does not exist"

    When I update appointment status with invalid id
    Then The response status code should be 400
    And The response should contain message "Appointment id not found"

    When I clear the session
    Then The session should be empty
    And I have credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The response should contain role "ADMIN"

    When I update appointment status to "COMPLETED"
    Then The response status code should be 403
    And The response should contain message "Forbidden to access resource. Role is not allowed."