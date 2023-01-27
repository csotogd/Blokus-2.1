# AI that plays the boardgame Blokus.

We create a program of the Blokus boardgame (.jar). When running such program you can play blokus vs friends or vs one/multiple AI agents.
The AI agent was implemented using MCTS, evolutionary algorithms to compute the optimal evaluation function and modifications of alpha-beta pruning. 
We challenge you to beat the AI! We can not.



The following instructions work in IntelliJ Idea as it is the IDE we all used

1.unzip

2. open project as intelli j idea project

3. go to File -> Libraries 

4 Click on the + and add the entire lib folder of the javafx sdk

5. Go to Game.StartScreen.java file and click on Edit run configurations, just to the left of the 
green triangle, in the top right of the screen

6. In the VM Options, write, 
"-p path --add-modules javafx.controls"
where path is the path to your java fx sdk path, C:\Program Files\Java\javafx-sdk-11.0.2\lib in our case

7. Add Junit classpath

8. Run Game.StartScreen

9 Enjoy!
