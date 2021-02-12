# Historical VaR Portfolio Calculator

The package, the installation guide and the user guide has been shared by email. Note that the application accepts only well formatted CSV (details available on the user guide and code comments).

There is a Test package to test the VaR Class. Some additional time would have been needed for further unit tests as implementing the two GUIs (this application and the previous version) asked me a lot of time. Note that the GUI has been tested via integration tests (app's reactions to the inputs). A directory `data` can be found with CSV files for integration tests.
According to the tests, I conclude that the historical VaR of a portfolio is not the sum of the VaR of the item of the portfolio (see unit tests). Obviously, it depends if the VaR selected day is the same for all instruments of the portfolio, which is unlikely - except if adding the same instrument 2 times for example.

The calculation methods for the VaR has been taken on the following links.
  
  - [Historical return](https://corporatefinanceinstitute.com/resources/knowledge/trading-investing/historical-returns/)
  - [VaR of an item](https://corporatefinanceinstitute.com/resources/knowledge/trading-investing/value-at-risk-var/#:~:text=The%20historical%20method%20is%20the,250%20scenarios%20for%20future%20value.) 
  - [VaR of a portfolio](https://www.youtube.com/watch?v=55O4JB9nw9k) where, for a given asset and historical return, the historical return is weighted by the investment of the asset, and the sum for all assets gives the portfolio price fluctuation between two days. The VaR is then obtained by sorting the calculated list of portfolio fluctuations, and taking the right value depending on the confidence level.

Note that the application includes an external code, the class `ButtonColumn.java`, in order to integrate buttons in the table - this is not an default functionnality of the library `javax.swing`. The code has been taken on this (link)[http://www.camick.com/java/source/ButtonColumn.java] and have been modified a few for the targeted rendered button in order to not to waste time.



