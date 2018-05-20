@web @android @ios
Feature: Login 

Scenario: Successful login 
	When I login as "validUser" 
	Then I see the user menu
