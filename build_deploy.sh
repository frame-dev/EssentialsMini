mvn clean package
mvn deploy
rm EssentialsMini.jar
mv target/EssentialsMini-* EssentialsMini.jar
