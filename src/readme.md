javac -cp "../lib/gson-2.12.1.jar" *.java  
javac Server.java  
java -cp .:lib/gson-2.12.1.jar Server 4555 Dictionary.json

javac -cp "../lib/gson-2.12.1.jar" *.java
Javac Client.java   
java -cp .:lib/gson-2.12.1.jar Client 4555
