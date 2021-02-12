package Application;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * The VaR class calculate the historical VaR of the portfolio according
 * to the to the method in the README on the GitHub (for each item, the
 * historical returns are weighted by the investment)
 *
 */
public class VaR {

    public static final double EPSILON = 0.001;

    private final Double confidenceLevel;
    private List<Double> investments = null;
    private final VaRValue value;

    public List<Double> getInvestments() {
        return investments;
    }

    public VaRValue getValue() {
        return value;
    }

    /**
     *
     * @param investments should not be empty
     */
    public VaR(Double confidenceLevel, @NotNull List<Double> investments){
        this.confidenceLevel = confidenceLevel;
        value = new VaRValue();
        if (investments.isEmpty()){
            Logger.getLogger(VaR.class.getName()).log(Level.SEVERE, "[Logger] Unexpected error: investments are empty when initializing the VaR (see VaR.java line 41).");
            value.setErrorMessage("Unexpected error: investments are empty when initializing the VaR.");
        }
        else {
            this.investments = investments;
        }
    }

    /**
     * Calculate the historical VaR of the portfolio. The value/error message
     * is set in the VaRValue attribute.
     *
     * Only accept a prices list with at least one item, with each item having same length,
     * and for a given j, prices.get(i).(j) is null for all i or non-null for all i
     *
     * @param prices List of each prices from the csv file(s) for each item
     */
    public Integer calculateHistoricalVaR(@NotNull List<List<Double>> prices) {

        // Checking the confidence level
        if ((confidenceLevel == null) || (confidenceLevel<EPSILON) || (100-confidenceLevel<EPSILON)){
            value.setErrorMessage("Please enter a valid confidence level value between 0.001 and 99.999.");
            return null;
        }

        // Checking the number of csv files are the number of items
        if (investments.size() != prices.size()){
            value.setErrorMessage("Unexpected error: the number of csv files doesn't match the number of items in the portfolio.");
            return null;
        }

        // Compute the weighted portfolio price fluctuations according to the method in the README on the GitHub (for each item, the historical returns are weighted by the investment)
        List<Double> historicalPortfolioReturnValues = calculateHistoricalPortfolioReturnValues(prices);

        // Find the VaR value depending on the confidence level
        int numberOfDays = historicalPortfolioReturnValues.size();
        if (numberOfDays== 0){
            value.setErrorMessage("Please add at least one item to the portfolio, and two rows with data in each CSV file for the given columns.");
        }
        else {
            historicalPortfolioReturnValues.sort(Double::compareTo);
            int indexVaR = (int) ((numberOfDays * (1 - confidenceLevel / 100)) - 1);
            value.setValue(-historicalPortfolioReturnValues.get(indexVaR));
        }

        return numberOfDays;
    }

    /**
     *
     * Only accept a prices list with at least one item, with each item having same length,
     * and for a given j, prices.get(i).(j) is null for all i or non-null for all i.
     *
     * @param prices List of each prices from the csv file(s) for each item. Should not be empty
     * @return the weighted portfolio price fluctuations according to the method in the README on the GitHub (for each item, the historical returns are weighted by the investment)
     */
    private List<Double> calculateHistoricalPortfolioReturnValues(@NotNull List<List<Double>> prices) {

        List<Double> historicalPortfolioReturnValues = new ArrayList<>();
        List<Double> previousPrice = new ArrayList<>();
        List<Double> nextPrice = new ArrayList<>();

        for (int item=0; item<prices.size(); item++){
            previousPrice.add(null);
            nextPrice.add(null);
        }

        // Compute the weighted portfolio price fluctuations
        if (prices.size()>0) {
            for (int price = 0; price < prices.get(0).size(); price++) {
                if (prices.get(0).get(price) != null) { // means != null for all items as all the csv files either have data or no data for a given row, checked in function actionPerformed of the VaRComputerListener.java

                    if (previousPrice.get(0) != null) { // needs 2 prices to calculate an historical return
                        historicalPortfolioReturnValues.add(0.);
                    }

                    // Compute the weighted portfolio price fluctuations
                    for (int item = 0; item < prices.size(); item++) {

                        nextPrice.set(item, prices.get(item).get(price));

                        if (previousPrice.get(item) != null) { // needs 2 prices to calculate an historical return

                            // historical return of item for previousPrice/nextPrice
                            Double historicalReturnOfItem = (nextPrice.get(item) - previousPrice.get(item)) / Math.abs(previousPrice.get(item));

                            // Update the portfolio fluctuation value with the item fluctuation weighted by investment
                            historicalPortfolioReturnValues.set(historicalPortfolioReturnValues.size() - 1, historicalReturnOfItem * investments.get(item) + historicalPortfolioReturnValues.get(historicalPortfolioReturnValues.size() - 1));
                        }

                        previousPrice.set(item, nextPrice.get(item));
                    }
                }
            }
        }
        else {
            value.setErrorMessage("Please add at least one item to the portfolio.");
        }

        return historicalPortfolioReturnValues;
    }


}
