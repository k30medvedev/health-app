Feature: login helper, reused by other features via call read('login.feature')

  Scenario:
    * url baseUrl
    Given path '/api/auth/login'
    And request { username: 'demo', password: 'demo123' }
    When method post
    Then status 200
    * def token = response.token
