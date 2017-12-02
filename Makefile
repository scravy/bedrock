test: clean
	mvn verify jacoco:report
	mv target/site/jacoco coverage

clean:
	mvn clean
	rm -rf coverage

publish: clean
	yes ' ' | head -n3 | mvn release:prepare
	mvn release:perform

purge: clean
	rm -rf .idea *.iml pom.xml.releaseBackup release.properties
