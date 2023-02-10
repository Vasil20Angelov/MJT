package bg.sofia.uni.fmi.mjt.wallet;

import bg.sofia.uni.fmi.mjt.dto.Asset;
import bg.sofia.uni.fmi.mjt.exceptions.CryptoNotFoundException;
import bg.sofia.uni.fmi.mjt.exceptions.InsufficientAmountException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CryptoWallet implements Wallet {
    private static final double INITIAL_BALANCE = 0.0d;
    private double balance;
    private final Map<String, List<Investment>> activeInvestments;

    public CryptoWallet() {
        balance = INITIAL_BALANCE;
        activeInvestments = new HashMap<>();
    }

    @Override
    public double getBalance() {
        return balance;
    }

    @Override
    public Map<String, List<Investment>> getActiveInvestments() {
        return Collections.unmodifiableMap(activeInvestments);
    }

    @Override
    public void depositMoney(double amount) {
        validateNumericValues(amount);
        balance += amount;
    }

    @Override
    public double buyAsset(String cryptoName, double cryptoPrice, double money) throws InsufficientAmountException {
        validateCryptoName(cryptoName);
        validateNumericValues(cryptoPrice, money);

        if (money > balance) {
            throw new InsufficientAmountException("Not enough money in the wallet for this transaction!");
        }

        double cryptoAmount = money / cryptoPrice;
        Investment investment = new Investment(cryptoAmount, cryptoPrice);
        if (activeInvestments.containsKey(cryptoName)) {
            activeInvestments.get(cryptoName).add(investment);
        } else {
            List<Investment> newInvestmentList = new ArrayList<>();
            newInvestmentList.add(investment);
            activeInvestments.put(cryptoName, newInvestmentList);
        }
        balance -= money;

        return cryptoAmount;
    }

    @Override
    public double sellAsset(String cryptoName, double cryptoPrice) throws CryptoNotFoundException {
        validateCryptoName(cryptoName);
        validateNumericValues(cryptoPrice);

        if (!activeInvestments.containsKey(cryptoName)) {
            throw new CryptoNotFoundException("Crypto not found in the wallet!");
        }

        double totalAmount = 0.0;
        for (Investment investment : activeInvestments.get(cryptoName)) {
            totalAmount += investment.amount();
        }

        activeInvestments.remove(cryptoName);

        double earnings = totalAmount * cryptoPrice;
        balance += earnings;

        return earnings;
    }

    @Override
    public String getWalletInfo() {
        StringBuilder info = new StringBuilder("Balance: " + balance + "$" + System.lineSeparator());
        for (Map.Entry<String, List<Investment>> crypto : activeInvestments.entrySet()) {
            double amount = crypto.getValue().stream()
                    .mapToDouble(Investment::amount)
                    .sum();

            info.append(String.format("Crypto: %s, Amount: ", crypto.getKey()))
                .append(amount)
                .append(System.lineSeparator());
        }

        return info.toString();
    }

    @Override
    public String getWalletOverallStats(Map<String, Asset> assets) {
        StringBuilder info = new StringBuilder();

        double totalPNL = 0.0;
        for (Map.Entry<String, List<Investment>> crypto : activeInvestments.entrySet()) {

            double profitAndLost = calculateProfitAndLostValue(crypto, assets);
            totalPNL += profitAndLost;

            info.append(String.format("Crypto: %s, P&L: %.2f$", crypto.getKey(), profitAndLost))
                .append(System.lineSeparator());
        }

        info.append(String.format("Total P&L: %.2f$", totalPNL))
            .append(System.lineSeparator());

        return info.toString();
    }

    private double calculateProfitAndLostValue(
            Map.Entry<String, List<Investment>> crypto, Map<String, Asset> assets) {

        double amount = 0.0;
        double cryptoBoughtFor = 0.0;
        for (Investment investment : crypto.getValue()) {
            amount += investment.amount();
            cryptoBoughtFor += investment.amount() * investment.price();
        }

        double currentPrice = 0.0;
        if (assets.containsKey(crypto.getKey())) {
            currentPrice = assets.get(crypto.getKey()).getPrice();
        }

        double cryptoSellFor = currentPrice * amount;

        return cryptoSellFor - cryptoBoughtFor;
    }

    private void validateCryptoName(String cryptoName) {
        if (cryptoName == null || cryptoName.isBlank()) {
            throw new IllegalArgumentException("Invalid crypto name!");
        }
    }

    private void validateNumericValues(Double... values) {
        final double delta = 0.00000000000000001;
        for (double value : values) {
            if (value - delta <= 0.0) {
                throw new IllegalArgumentException("Any given number should be positive!");
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CryptoWallet that = (CryptoWallet) o;
        return Double.compare(that.balance, balance) == 0 && Objects.equals(activeInvestments, that.activeInvestments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(balance, activeInvestments);
    }
}
