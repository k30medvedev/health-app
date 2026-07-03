Feature: Users API

  Background:
    * url baseUrl

  Scenario: create a user returns 201 with a Location header
    * def email = 'karate-' + karate.uuid() + '@example.com'
    Given path '/api/users'
    And request { fullName: 'Karate User', email: '#(email)' }
    When method post
    Then status 201
    And match response == { id: '#number', fullName: 'Karate User', email: '#(email)', createdAt: '#string' }
    And match header Location == '#present'

  Scenario: create a user with an invalid body returns 400
    Given path '/api/users'
    And request { fullName: '', email: 'not-an-email' }
    When method post
    Then status 400
    And match response.status == 400

  Scenario: create a user with a duplicate email returns 409
    * def email = 'karate-dup-' + karate.uuid() + '@example.com'
    Given path '/api/users'
    And request { fullName: 'First', email: '#(email)' }
    When method post
    Then status 201

    Given path '/api/users'
    And request { fullName: 'Second', email: '#(email)' }
    When method post
    Then status 409
    And match response.status == 409

  Scenario: get a user by id returns 200
    * def email = 'karate-get-' + karate.uuid() + '@example.com'
    Given path '/api/users'
    And request { fullName: 'Gettable User', email: '#(email)' }
    When method post
    Then status 201
    * def userId = response.id

    Given path '/api/users', userId
    When method get
    Then status 200
    And match response.id == userId
    And match response.email == email

  Scenario: get a missing user returns 404
    Given path '/api/users/999999'
    When method get
    Then status 404
    And match response.status == 404

  Scenario: list users returns a page
    Given path '/api/users'
    When method get
    Then status 200
    And match response.content == '#array'
