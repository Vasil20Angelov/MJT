package bg.sofia.uni.fmi.mjt.wallet;

import bg.sofia.uni.fmi.mjt.dto.Asset;
import bg.sofia.uni.fmi.mjt.exceptions.CryptoNotFoundException;
import bg.sofia.uni.fmi.mjt.exceptions.InsufficientAmountException;

import java.util.List;
import java.util.Map;

public interface Wallet {
    double getBalance();

    Map<String, List<Investment>> getActiveInvestments();

    void depositMoney(double amount);

    double buyAsset(String cryptoName, double cryptoPrice, double money) throws InsufficientAmountException;

    double sellAsset(String cryptoName, double cryptoPrice) throws CryptoNotFoundException;

    String getWalletInfo();

    String getWalletOverallStats(Map<String, Asset> assets);
}
