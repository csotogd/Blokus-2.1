# Blokus-2.1
DSAI project 2.1. Blokus game


The following instructions work in IntelliJ Idea as it is the IDE we all used

1.unzip

2. open project as intelli j idea project

3. go to File -> Libraries 

4 Click on the + and add the entire lib folder of the javafx sdk

5. Go to StartScreen.java file and click on Edit run configurations, just to the left of the 
green triangle, in the top right of the screen

6. In the VM Options, write, 
"-p path --add-modules javafx.controls"
where path is the path to your java fx sdk path, C:\Program Files\Java\javafx-sdk-11.0.2\lib in our case

7. Add Junit classpath

8. Run StartScreen

9 Enjoy!
