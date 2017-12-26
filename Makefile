test: clean
	mvn verify jacoco:report
	mv target/site/jacoco coverage

clean:
	mvn clean
	rm -rf coverage

publish: clean
	mvn deploy

purge: clean
	rm -rf .idea *.iml pom.xml.releaseBackup release.properties
