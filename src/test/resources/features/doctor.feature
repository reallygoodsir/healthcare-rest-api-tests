Feature: Healthcare API - Flows

  Background:
    Given The correct API URL

Scenario: Admin performs full doctor lifecycle including invalid operations
Given The Login endpoint is "/authorization/"
And I have credentials "greatadmin@gmail.com" and "73629175"
When I send a POST request to login
Then The response status code should be 200
And The session cookie "session_id" should exist and not be empty
And The response should contain role "ADMIN"

    # --- CREATE DOCTOR INVALID CASES ---
When I create new doctor without a session
Then The response status code should be 401
And The response should contain message "Session id is empty"

When I create new doctor with an invalid session_id
Then The response status code should be 401
And The response should contain message "Not authorized. Session id has incorrect format"

When I create new doctor with session id that does not exist
Then The response status code should be 401
And The response should contain message "Not authorized. Session id does not exist"

When I create new doctor with non empty id
Then The response status code should be 400
And The response should contain message "Doctor id must be empty when new doctor is created"

      # --- CREATE DOCTOR SUCCESS ---
When I create new doctor
Then The response status code should be 201
And The response JSON should be valid
And The doctor response has correct data

When I create new doctor with empty first name
Then The response status code should be 400
And The response should contain message "First name has the wrong format"

When I create new doctor with empty last name
Then The response status code should be 400
And The response should contain message "Last name has the wrong format"

When I create new doctor with empty specialization id
Then The response status code should be 400
And The response should contain message "Specialization id is empty"

When I create new doctor with specialization id that does not exist
Then The response status code should be 400
And The response should contain message "Specialization id does not exist"

When I create new doctor with invalid email format
Then The response status code should be 400
And The response should contain message "Email has the wrong format"

When I create new doctor with duplicate email
Then The response status code should be 400
And The response should contain message "Email already exists"

When I create new doctor with invalid phone format
Then The response status code should be 400
And The response should contain message "Phone number has the wrong format"

When I create new doctor with duplicate phone
Then The response status code should be 400
And The response should contain message "Phone number already exist"

When I create new doctor with invalid photo
Then The response status code should be 400
And The response should contain message "Photo is not valid"


    # --- UPDATE DOCTOR INVALID CASES ---
Given Another doctor exists
When I update the doctor without a session
Then The response status code should be 401
And The response should contain message "Session id is empty"

When I update the doctor with an invalid session_id
Then The response status code should be 401
And The response should contain message "Not authorized. Session id has incorrect format"

When I update the doctor with session id that does not exist
Then The response status code should be 401
And The response should contain message "Not authorized. Session id does not exist"

When I update the doctor with empty id
Then The response status code should be 400
And The response should contain message "Doctor id must not be empty when existing doctor is updated"

When I update the doctor with id that does not exist
Then The response status code should be 400
And The response should contain message "Doctor id does not exist"

When I update the doctor with empty first name
Then The response status code should be 400
And The response should contain message "First name has the wrong format"

When I update the doctor with empty last name
Then The response status code should be 400
And The response should contain message "Last name has the wrong format"

When I update the doctor with empty specialization id
Then The response status code should be 400
And The response should contain message "Specialization id is empty"

When I update the doctor with specialization id that does not exist
Then The response status code should be 400
And The response should contain message "Specialization id does not exist"

When I update the doctor with invalid email format
Then The response status code should be 400
And The response should contain message "Email has the wrong format"

When I update the doctor with duplicate email
Then The response status code should be 400
And The response should contain message "Email already exists"

When I update the doctor with invalid phone format
Then The response status code should be 400
And The response should contain message "Phone number has the wrong format"

When I update the doctor with duplicate phone
Then The response status code should be 400
And The response should contain message "Phone number already exist"

When I update the doctor with invalid photo
Then The response status code should be 400
And The response should contain message "Doctor photo is not valid"

      # --- UPDATE DOCTOR SUCCESS ---
When I update the doctor
Then The response status code should be 200
And The doctor update response has correct data

    # --- DELETE DOCTOR SUCCESS ---
When I delete the doctor
Then The response status code should be 204

    # --- DELETE DOCTOR INVALID CASES ---
When I delete the doctor without a session
Then The response status code should be 401
And The response should contain message "Session id is empty"

When I delete the doctor with an invalid session_id
Then The response status code should be 401
And The response should contain message "Not authorized. Session id has incorrect format"

When I delete the doctor with session id that does not exist
Then The response status code should be 401
And The response should contain message "Not authorized. Session id does not exist"

When I delete a doctor with empty id
Then The response status code should be 405

When I delete a doctor with id that does not exist
Then The response status code should be 400
And The response should contain message "Doctor id does not exist"


When I clear the session
Then The session should be empty

Given I have credentials "ccaguy@gmail.com" and "18923574"
When I send a POST request to login
Then The response status code should be 200
And The session cookie "session_id" should exist and not be empty
And The response should contain role "CALL_CENTER_AGENT"

When I update the doctor as a non admin user
Then The response status code should be 403
And The response should contain message "Forbidden to access resource. Role is not allowed."

When I delete the doctor as a non admin user
Then The response status code should be 403
And The response should contain message "Forbidden to access resource. Role is not allowed."

When I create new doctor as a non admin user
Then The response status code should be 403
And The response should contain message "Forbidden to access resource. Role is not allowed."








  Scenario: Get doctor by id (valid and invalid cases)
    Given The Login endpoint is "/authorization/"
    And I have credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"
    When I create new doctor

    When I clear the session
    Then The session should be empty
    Given I have credentials "ccaguy@gmail.com" and "18923574"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "CALL_CENTER_AGENT"

  # Valid case
    When I get the doctor by id
    Then The response status code should be 200
    And The doctor response has correct data

  # Invalid cases
    When I get the doctor by id without session
    Then The response status code should be 401
    And The response should contain message "Session id is empty"

    When I get the doctor by id with invalid session
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id has incorrect format"

    When I get the doctor by id with non-existent id
    Then The response status code should be 400
    And The response should contain message "Doctor id does not exist"


  Scenario: Get all doctors (valid and invalid cases)
    Given The Login endpoint is "/authorization/"
    And I have credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"
    When I create new doctor

  # Valid case
    When I get all doctors
    Then The response status code should be 200
    And The response JSON should be a valid list

  # Invalid cases
    When I get all doctors without session
    Then The response status code should be 401
    And The response should contain message "Session id is empty"

    When I get all doctors with invalid session
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id has incorrect format"


  Scenario: Get doctors by service (valid and invalid cases)
    Given The Login endpoint is "/authorization/"
    And I have credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    When I create new doctor
    When I create new service
    Then The response status code should be 200
    And The response JSON should be valid
    And The service response has correct data

    When I clear the session
    Then The session should be empty
    Given I have credentials "ccaguy@gmail.com" and "18923574"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "CALL_CENTER_AGENT"

  # Valid case
    When I get doctors by service
    Then The response status code should be 200

  # Invalid cases
    When I get doctors by service without session
    Then The response status code should be 401
    And The response should contain message "Session id is empty"

    When I get doctors by service with invalid session
    Then The response status code should be 401
    And The response should contain message "Not authorized. Session id has incorrect format"

    When I get doctors by service with non-existent service id
    Then The response status code should be 400
    And The response should contain message "Service id does not exist"



  Scenario: Get doctor id by credential (valid and invalid cases)
    Given The Login endpoint is "/authorization/"
    And I have credentials "adrian@gmail.com" and "39963516"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "DOCTOR"
    And The credential id should be saved


  # Valid case
    When I get the doctor id by credential
    Then The response status code should be 200

  # Invalid cases
    When I get the doctor id by credential without session
    Then The response status code should be 404

    When I get the doctor id by credential with wrong credential id
    Then The response status code should be 400
    And The response should contain message "Credential id does not belong to existing session"

    When I clear the session
    Then The session should be empty

    And I have credentials "greatadmin@gmail.com" and "73629175"
    When I send a POST request to login
    Then The response status code should be 200
    And The session cookie "session_id" should exist and not be empty
    And The response should contain role "ADMIN"

    When I get the doctor id by credential
    Then The response status code should be 403
    And The response should contain message "Forbidden to access resource. Role is not allowed."