# EPP
Peer Review Application (Évaluation Par les Pairs)<br>
Author: Vincent Lacasse (vincent.lacasse@etsmtl.ca)
<p>
The application computes a factor which represents the evaluation of a student as evaluated by its coworkers.
This application will take a CSV file as an input. The CSV file is the output of the 
'Export des Évaluations, sans multiligne' function of Workshop ÉTS.
The application outputs a XLSX file including the factor for each single evaluated students.
</p>

- To build a jar target including dependencies, run Maven 'install' target.
- The target will be created in the EPP/target/ directory
- The target will be named: EPP-\<version\>-SNAPSHOT-jar-with-dependencies.jar
- Rename the target as desired (typically: EPP-\<version\>.jar)
- This target is fully independent and can be run on any computer (Windows, OSX or Linux)
