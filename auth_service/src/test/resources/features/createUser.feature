Feature: Creating a new user account

  Scenario: User account creation
    Given I want to create a user with username as "my_user" and the password as "ThisIsMyPassword!1"
    When I make a POST request to "/user/create" with the following body:
      | username | password           |
      | my_user  | ThisIsMyPassword!1 |
    Then a user is created in the database with the following properties:
      | username | id | password           |
      | my_user  | 1  | ThisIsMyPassword!1 |