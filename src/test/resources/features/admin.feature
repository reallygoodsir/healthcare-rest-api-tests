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
    And The response should contain message "Not authorized"

    When I delete the patient with an invalid session_id
    Then The response status code should be 403
    And The response should contain message "Forbidden to access resource"

    When I delete the patient without an id
    Then The response status code should be 405
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
    And The response should contain message "Not authorized"

    When I delete the service with an invalid session_id
    Then The response status code should be 403
    And The response should contain message "Forbidden to access resource"

    When I delete a service with invalid id
    Then The response status code should be 404

    When I delete a service without an id
    Then The response status code should be 405

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
    And The response should contain message "Date must be not be in the past"

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

    When I create a valid doctor schedule
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
    And The response should contain message "Date must be not be in the past"

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

# ----------------------------------------------------------------------------------------------------------------------

  Scenario: Call center agent creates an appointment

    # --- ADMIN LOGIN ---
    Given The Login endpoint is "/authorization/"
    And I have credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    Given An existing doctor is available
    And An existing patient is available
    And An existing doctor schedule is available

    When I clear the session
    Then The session should be empty

    # Given The Login endpoint is "/authorization/"
    And I have credentials "ccaguy@gmail.com" and "18923574"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "CALL_CENTER_AGENT"

    When I find the patient by phone number
    Then The response status code should be 200
    And The response JSON should contain the patient data

    When I create an appointment
    Then The response status code should be 200
    And The response JSON should contain valid appointment data

    When I fetch doctor schedules for the doctor
    Then The response status code should be 200
    And The response JSON should be a valid list

    When I fetch all appointments
    Then The response status code should be 200
    And The response should contain the appointment with correct data

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
    And The response should contain message "Not authorized"

    When I create an appointment with an invalid session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id does not exist."

    When I clear the session
    Then The session should be empty

      # --- INVALID: SESSION ID WRONG FORMAT ---
    When I create an appointment with a malformed session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id has incorrect format."

    # --- INVALID: SESSION DOES NOT EXIST ---
    When I create an appointment with a non-existing session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id does not exist."

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
    And The response should contain message "Not authorized"

    When I create an appointment outcome with an invalid session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id does not exist."

    When I create an appointment outcome with an invalid appointment_id
    Then The response status code should be 400
    And The response should contain message "Incorrect appointment id provided"

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
    And The response should contain message "Not authorized"

    When I delete the appointment with an invalid session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id does not exist."

    When I delete an appointment with invalid id
    Then The response status code should be 400
    And The response should contain message "Appointment id not found"

    When I delete an appointment without supplying an id
    Then The response status code should be 405



  Scenario: Call center agent retrieves all appointments
    # --- LOGIN ---
    Given The Login endpoint is "/authorization/"
    Given I have credentials "ccaguy@gmail.com" and "18923574"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "CALL_CENTER_AGENT"

    # --- SUCCESS: GET ALL APPOINTMENTS ---
    When I get all appointments
    Then The response status code should be 200
    And The response JSON should be a valid list
    # --- UNAUTHORIZED: NO SESSION ---
    When I get all appointments without a session
    Then The response status code should be 401
    And The response should contain message "Not authorized"

    # --- NON-EXISTING SESSION ---
    When I get all appointments with a non-existing session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id does not exist."

    # --- FORBIDDEN ROLE ---
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

    # --- LOGIN AS DOCTOR ---
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
    And I have credentials "adrian@gmail.com" and "39963516"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "DOCTOR"

    # --- SUCCESS: GET APPOINTMENT BY ID ---
    When I get appointment by id
    Then The response status code should be 200
    And The response JSON should be valid

    # --- UNAUTHORIZED: NO SESSION ---
    When I get appointment by id without a session
    Then The response status code should be 401
    And The response should contain message "Not authorized"

    # --- UNAUTHORIZED: MALFORMED SESSION ---
    When I get appointment by id with a malformed session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id has incorrect format"

    # --- UNAUTHORIZED: SESSION DOES NOT EXIST ---
    When I get appointment by id with a non-existing session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id does not exist."

    # --- INVALID: APPOINTMENT DOES NOT EXIST ---
    When I get appointment with invalid id
    Then The response status code should be 500
    And The response should contain message "Failed to get an appointment by id"

      # --- FORBIDDEN: WRONG ROLE ---
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
    And The response should contain message "Not authorized"

  # --- MALFORMED SESSION ---
    When I get appointment outcome by id with a malformed session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id has incorrect format"

  # --- NON-EXISTING SESSION ---
    When I get appointment outcome by id with a non-existing session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id does not exist."

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

    # --- LOGIN AS ADMIN TO PREPARE DATA ---
    Given The Login endpoint is "/authorization/"
    And I have credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    # --- PREPARE DATA ---
    When I create new doctor
    Then The response status code should be 201

    When I create a valid doctor schedule
    Then The response status code should be 200

    When I create new patient
    Then The response status code should be 201

    # login as call center and create appointment
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

    # --- LOGIN AS DOCTOR ---
    When I clear the session
    Then The session should be empty
    And I have credentials "adrian@gmail.com" and "39963516"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "DOCTOR"

    # --- SUCCESS: UPDATE STATUS ---
    When I update appointment status to "COMPLETED"
    Then The response status code should be 204

    # --- MALFORMED SESSION ---
    When I update appointment status to "COMPLETED" with a malformed session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id has incorrect format"

    # --- NON-EXISTING SESSION ---
    When I update appointment status to "COMPLETED" with a non-existing session_id
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id does not exist."

    # --- INVALID APPOINTMENT ID ---
    When I update appointment status with invalid id
    Then The response status code should be 400
    And The response should contain message "Appointment id not found"

    # --- FORBIDDEN ROLE ---
    When I clear the session
    Then The session should be empty
    And I have credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The response should contain role "ADMIN"

    When I update appointment status to "COMPLETED"
    Then The response status code should be 403
    And The response should contain message "Forbidden to access resource. Role is not allowed."
