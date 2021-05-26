# An Online Test using Java Swing with an Excel sheet to store and retrieve data (Apache POI Library)
 ***
 
 ## Using Java Swing to create a GUI and Apache POI Library to work with Microsoft Docs (working with Excel here).
 ### The Excel Sheet is essentially working as a database.
 
 Project Demo: [Online Test System](https://replit.com/@MajinVegetaSSJ2/Online-Test?v=1)
 
 ---
 Download Apache POI and configure its libraries in Eclipse: https://www.toolsqa.com/blogs/download-apache-poi/
 
 ---
 
The user is required to login either as a student or as a teacher.

![Capture_OnlineTest_StudentLogin.PNG](https://github.com/04xRaynal/OnlineTest_JavaSwing_ApachePOI/blob/a7504a9aa21a330fa9a237a1a936c648886c609f/Captured%20Images/Capture_OnlineTest_StudentLogin.PNG)

![Capture_OnlineTest_TeacherLogin.PNG](https://github.com/04xRaynal/OnlineTest_JavaSwing_ApachePOI/blob/a7504a9aa21a330fa9a237a1a936c648886c609f/Captured%20Images/Capture_OnlineTest_TeacherLogin.PNG)

---
When a student logs in, different subjects are displayed and can choose a subject to start the quiz.

![Capture_OnlineTest_MainMenu.PNG](https://github.com/04xRaynal/OnlineTest_JavaSwing_ApachePOI/blob/a7504a9aa21a330fa9a237a1a936c648886c609f/Captured%20Images/Capture_OnlineTest_MainMenu.PNG)

---

When a subject is clicked a question is displayed along with 4 options, the student has to click on any 1 option,
there are 3 buttons, previous, to go to the previous question, next, to go to the next question, and result which ends the test and displays the final result.

![Capture_OnlineTest_StudentQuiz.PNG](https://github.com/04xRaynal/OnlineTest_JavaSwing_ApachePOI/blob/a7504a9aa21a330fa9a237a1a936c648886c609f/Captured%20Images/Capture_OnlineTest_StudentQuiz.PNG)

---

The student has to attempt 10 questions and is required to finish the test in 15 minutes.
Once 15 minutes are exceeded or the student clicks on the result button, the quiz is ended and the result is displayed along with the score obtained.

![Capture_OnlineTest_StudentResult](https://github.com/04xRaynal/OnlineTest_JavaSwing_ApachePOI/blob/a7504a9aa21a330fa9a237a1a936c648886c609f/Captured%20Images/Capture_OnlineTest_StudentResult.PNG)

Each student can appear for each subject test thrice, after 3 attempts the student is'nt allowed to reappear that particular subject test.

---

When a teacher logs in, different subjects are displayed and can choose a subject to input values for that particular subject.
Input fields are displayed, a question field, 4 option fields and an answer field.
The teacher can input values in these fields and click on the submit button to submit his input.

![Capture_OnlineTest_TeacherInput](https://github.com/04xRaynal/OnlineTest_JavaSwing_ApachePOI/blob/a7504a9aa21a330fa9a237a1a936c648886c609f/Captured%20Images/Capture_OnlineTest_TeacherInput.PNG)

If a subject already has 25 questions present, an error dialog is displayed.

---

Future Implementations:

Create grades and different subjects for each grade.

Teacher can download the class result for each subject test.

A head master who controls which subjects get added to which grade and which teacher handles which grade.