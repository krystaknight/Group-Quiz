**Group Project Preliminary Requirements**

1. Identify a User
	* Create a sign-up screen to identify the person
	* At minimum, users will already be available on the server side
	
2. Download a quiz and parse the data
	* Save Quiz to SQLite
	* Save Questions to the Quiz in SQLite
	
3. Present the user with multiple choice questions to answer
	* Users should be able to assign points to multiple answers using the available points
	
4. Once the user has completed the “self assessment” upload the users answers
	* User must be able to return to the last question they have answered even if the app crashes
	
5. Assign the user to a group
	* Users will have, at minimum, a predefined group from the service.
	
6. Allow the group to answer the questions
	* Group needs to have an assigned “leader”
	* The number of tries reflects the number of points the group will get for the answer
	
7. Provide immediate feedback
	* 	Show the group if they correctly answered once selected
	* 	Show the cumulative points (running total) for the quiz as they progress

Data Base Ideas
 id, title , quizScore ( over all score ) 
