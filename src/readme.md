javac -cp "../lib/gson-2.12.1.jar" *.java  
javac DictionaryServer.java  
java -cp .:lib/gson-2.12.1.jar DictionaryServer

javac -cp "../lib/gson-2.12.1.jar" *.java
Javac DictionaryClient.java   
java -cp .:lib/gson-2.12.1.jar DictionaryClient
