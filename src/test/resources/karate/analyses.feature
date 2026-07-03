Feature: Analyses API

  Background:
    * url baseUrl
    * def auth = call read('login.feature')
    * configure headers = { Authorization: '#("Bearer " + auth.token)' }
    * def email = 'karate-analysis-' + karate.uuid() + '@example.com'
    Given path '/api/users'
    And request { fullName: 'Analysis Owner', email: '#(email)' }
    When method post
    Then status 201
    * def userId = response.id

  Scenario: create an analysis for an existing user returns 201
    Given path '/api/users', userId, 'analyses'
    And request { testName: 'Hemoglobin', resultValue: '145', unit: 'g/L', referenceRange: '130-160', takenAt: '2026-01-01' }
    When method post
    Then status 201
    And match response ==
      """
      {
        id: '#number',
        userId: '#(userId)',
        testName: 'Hemoglobin',
        resultValue: '145',
        unit: 'g/L',
        referenceRange: '130-160',
        takenAt: '2026-01-01',
        createdAt: '#string'
      }
      """

  Scenario: create an analysis for a missing user returns 404
    Given path '/api/users/999999/analyses'
    And request { testName: 'Hemoglobin', resultValue: '145', unit: 'g/L', referenceRange: '130-160', takenAt: '2026-01-01' }
    When method post
    Then status 404
    And match response.status == 404

  Scenario: create an analysis with an invalid body returns 400
    Given path '/api/users', userId, 'analyses'
    And request { testName: '', resultValue: '', unit: null, referenceRange: null, takenAt: '2099-01-01' }
    When method post
    Then status 400
    And match response.status == 400

  Scenario: list analyses for a user
    Given path '/api/users', userId, 'analyses'
    And request { testName: 'Glucose', resultValue: '5.1', unit: 'mmol/L', referenceRange: '3.9-5.6', takenAt: '2026-01-01' }
    When method post
    Then status 201

    Given path '/api/users', userId, 'analyses'
    When method get
    Then status 200
    And match response == '#[1]'
    And match response[0].testName == 'Glucose'

  Scenario: get, then delete an analysis
    Given path '/api/users', userId, 'analyses'
    And request { testName: 'Cholesterol', resultValue: '4.8', unit: 'mmol/L', referenceRange: '<5.2', takenAt: '2026-01-01' }
    When method post
    Then status 201
    * def analysisId = response.id

    Given path '/api/analyses', analysisId
    When method get
    Then status 200
    And match response.id == analysisId

    Given path '/api/analyses', analysisId
    When method delete
    Then status 204

    Given path '/api/analyses', analysisId
    When method get
    Then status 404

  Scenario: get a missing analysis returns 404
    Given path '/api/analyses/999999'
    When method get
    Then status 404
    And match response.status == 404

  Scenario: delete a missing analysis returns 404
    Given path '/api/analyses/999999'
    When method delete
    Then status 404
    And match response.status == 404
