test:
	mvn verify jacoco:report
	rm -rf coverage
	mv target/site/jacoco coverage

clean:
	mvn clean

publish: clean
	mvn -Prelease deploy

purge: clean
	rm -rf .idea *.iml pom.xml.releaseBackup release.properties
