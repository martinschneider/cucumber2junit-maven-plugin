Feature: Search and tags 

@web @stable @sanity
Scenario Outline: Filter by tags 
	Given I am on the homepage 
	When I go to the tags page 
	And I filter for "<tag>" 
	And I select the tag "<tag>" 
	And I select the first question
	Then the question is tagged with "<tag>"	
	Examples:
		| tag |
		| selenium |
		| appium | 
	
@web @android @ios @stable @sanity
Scenario Outline: Use the search function 
	Given I am on the homepage 
	When I search for "<tag>"
	And I select the first question 
	Then the question is tagged with "<tag>"
	Examples:
		| tag |
		| selenium |
		| appium |