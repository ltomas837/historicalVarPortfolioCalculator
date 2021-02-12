package Test;

import Application.*;
import java.util.ArrayList;
import java.util.List;

public class TestVaR {

    private static VaR var;
    private static int testCounter = 1;

    public static void main(String[] args) {


        List<List<Double>> prices;
        List<Double> investments;
        double EPSILON = 0.0000000000000001;

        ///////////////////  TEST confidence level ///////////////////

        prices = new ArrayList<>();
        investments = new ArrayList<>();
        List<Double> itemPrice1 = new ArrayList<>();
        itemPrice1.add(0.);
        itemPrice1.add(0.);
        prices.add(itemPrice1);
        investments.add(1.);

        /// Test 1 ///
        var = new VaR(null, investments);
        var.calculateHistoricalVaR(prices);
        printError();
        assert((var.getValue().getErrorMessage()) != null);

        /// Test 2 ///
        var = new VaR(0., investments);
        var.calculateHistoricalVaR(prices);
        printError();
        assert((var.getValue().getErrorMessage()) != null);

        /// Test 3 ///
        var = new VaR(100., investments);
        var.calculateHistoricalVaR(prices);
        printError();
        assert((var.getValue().getErrorMessage()) != null);

        /// Test 4 ///
        var = new VaR(-1., investments);
        var.calculateHistoricalVaR(prices);
        printError();
        assert((var.getValue().getErrorMessage()) != null);

        /// Test 5 ///
        var = new VaR(Double.longBitsToDouble(0x7fefffffffffffffL), investments); // maximum double value
        var.calculateHistoricalVaR(prices);
        printError();
        assert((var.getValue().getErrorMessage()) != null);

        /// Test 6 ///
        var = new VaR(Double.longBitsToDouble(0x0010000000000000L), investments);// minimum double value
        var.calculateHistoricalVaR(prices);
        printError();
        assert((var.getValue().getErrorMessage()) != null);

        /// Test 7 ///
        var = new VaR(95., investments);
        var.calculateHistoricalVaR(prices);
        printError();
        assert((var.getValue().getErrorMessage()) == null);

        ///////////////////  TEST investments  ///////////////////

        /// Test 8 ///
        var = new VaR(95., new ArrayList<>());
        printError();
        assert(var.getInvestments() == null); // log an error as planned but the test is good

        /// Test 9 ///
        var = new VaR(95., investments);
        printError();
        assert(var.getInvestments() != null);


        ///////////////////  TEST calculateHistoricalVaR  ///////////////////

        /// Test 10 ///
        var.getValue().setErrorMessage(null);
        prices = new ArrayList<>();
        var.calculateHistoricalVaR(prices);
        printError();
        assert(var.getValue().getErrorMessage() != null);

        /// Test 11 ///
        prices.add(new ArrayList<>());
        prices.add(new ArrayList<>());
        var.calculateHistoricalVaR(prices);
        printError();
        assert(var.getValue().getErrorMessage() != null);

        /// Test 12 ///
        // here investments is length 2 and each list of price is length 1
        prices.get(0).add(-1.);
        prices.get(1).add(1.);
        prices.get(0).add(7.);
        prices.get(1).add(6.);
        var = new VaR(95., investments);
        var.calculateHistoricalVaR(prices);
        printError();
        assert(var.getValue().getErrorMessage() != null);

        /// Test 13 ///
        // here investments is length 2 and each list of price is length 2
        investments.add(1.);
        var = new VaR(95., investments);
        var.calculateHistoricalVaR(prices);
        printError();
        assert(var.getValue().getErrorMessage() == null);

        /// Test 14 ///
        investments = new ArrayList<>();
        investments.add(10.);
        prices = new ArrayList<>();
        itemPrice1 = new ArrayList<>();
        itemPrice1.add(1.);
        for (int i=0; i<9; i++){
            itemPrice1.add(itemPrice1.get(i)*2.);
        }
        prices.add(itemPrice1);
        var = new VaR(99., investments);
        var.calculateHistoricalVaR(prices);
        printError();
        assert(var.getValue().getErrorMessage() == null);
        assert(Math.abs(var.getValue().getValue()+10.) < EPSILON);

        /// Test 15 ///
        List<Double> itemPrice2 = new ArrayList<>();
        for (int i=0; i<9; i++){
            itemPrice2.add(-2.);
        }
        itemPrice2.add(-3.);
        prices.remove(0);
        prices.add(itemPrice2);
        investments.remove(0);
        investments.add(100.);
        var = new VaR(99., investments);
        var.calculateHistoricalVaR(prices);
        printError();
        assert(var.getValue().getErrorMessage() == null);
        assert(Math.abs(var.getValue().getValue()-50.) < EPSILON);

        /// Test 16 ///
        prices.add(itemPrice1);
        investments.add(10.);
        var = new VaR(99., investments);
        var.calculateHistoricalVaR(prices);
        printError();
        assert(var.getValue().getErrorMessage() == null);
        assert(Math.abs(var.getValue().getValue()-40.) < EPSILON);

        /// Test 17 ///
        // The VaR is not especially the sum of the VaR, see below. Indeed, -10+50 != 20
        prices.get(1).set(9, prices.get(1).get(9)*2.);
        var.calculateHistoricalVaR(prices);
        printError();
        assert(var.getValue().getErrorMessage() == null);
        assert(Math.abs(var.getValue().getValue()-20.) < EPSILON);

        investments.remove(0);
        prices.remove(0);
        var = new VaR(99., investments);
        var.calculateHistoricalVaR(prices);
        assert(var.getValue().getErrorMessage() == null);
        assert(Math.abs(var.getValue().getValue()+10.) < EPSILON);

        System.out.println("One log error is expected, this is not an error, this is printed from VaR.java line 41 on Test 8.");

        /*
         * function calculateHistoricalPortfolioReturnValues is not tested as private
         *
         */



    }

    private static void printError() {
        System.out.println("/////////////// Test "+testCounter+" ///////////////\n");
        if (var.getValue().getErrorMessage() == null)
            System.out.println("No user error message expected (maybe logger)\n");
        else
            System.out.println("The error message display to the user is: "+var.getValue().getErrorMessage()+"\n");
        testCounter++;
    }

}
