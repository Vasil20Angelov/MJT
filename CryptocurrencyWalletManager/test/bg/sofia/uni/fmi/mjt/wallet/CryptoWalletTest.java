package bg.sofia.uni.fmi.mjt.wallet;

import bg.sofia.uni.fmi.mjt.dto.Asset;
import bg.sofia.uni.fmi.mjt.exceptions.CryptoNotFoundException;
import bg.sofia.uni.fmi.mjt.exceptions.InsufficientAmountException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CryptoWalletTest {

    private final CryptoWallet cryptoWallet = new CryptoWallet();
    private static final double DELTA = 0.001;
    @Test
    public void testCryptoWalletStartsWithZeroBalance() {
        assertEquals(0.0, cryptoWallet.getBalance(), DELTA,
                "New crypto wallet should be initialized with zero balance!");
    }
    @Test
    public void testDepositMoney() {
        double toDeposit = 20;
        cryptoWallet.depositMoney(toDeposit);

        assertEquals(toDeposit, cryptoWallet.getBalance(), DELTA, "Incorrect deposit!");
    }

    @Test
    public void testBuyCryptoWithInvalidCryptoName() {
        assertThrows(IllegalArgumentException.class,
                () -> cryptoWallet.buyAsset(null, 10, 1),
                "Expected IllegalArgumentException to be thrown when the crypto name is null!\"");
    }

    @Test
    public void testBuyCryptoWithNonPositiveMoneyValue() {
        assertThrows(IllegalArgumentException.class,
                () -> cryptoWallet.buyAsset("BTC", 1, -0.1),
                "Expected IllegalArgumentException to be thrown when money is negative amount!\"");
    }

    @Test
    public void testBuyCryptoWithNonPositiveCryptoPriceValue() {
        assertThrows(IllegalArgumentException.class,
                () -> cryptoWallet.buyAsset("BTC", 0.0, 2),
                "Expected IllegalArgumentException to be thrown when the crypto price is zero!\"");
    }

    @Test
    public void testBuyCryptoWithNotEnoughBalance() {
        assertThrows(InsufficientAmountException.class,
                () -> cryptoWallet.buyAsset("BTC", 10, 1),
                "Expected InsufficientAmountException to be thrown!");
    }

    @Test
    public void testBuyCryptoWithEnoughBalance() {
        cryptoWallet.depositMoney(3);

        Investment investment = new Investment(0.24, 10);
        Map<String, List<Investment>> expectedInvestments = Map.of("BTC", List.of(investment));

        assertDoesNotThrow(() -> cryptoWallet.buyAsset("BTC", 10, 2.4),
                "Call should not throw any exception!");

        assertEquals(0.6, cryptoWallet.getBalance(), DELTA,
                "Wrongly calculated balance!");

        assertEquals(expectedInvestments, cryptoWallet.getActiveInvestments(),
                "Wrongly filled activeInvestments map!");
    }

    @Test
    public void testBuyCryptoWithMultipleCalls() throws InsufficientAmountException {
        cryptoWallet.depositMoney(10);
        cryptoWallet.buyAsset("BTC", 10, 2.4);
        cryptoWallet.buyAsset("ETH", 2, 3);
        cryptoWallet.buyAsset("BTC", 12, 2);

        Investment investment1 = new Investment(0.24, 10);
        Investment investment2 = new Investment(1.5, 2);
        Investment investment3 = new Investment(0.1666667, 12);
        Map<String, List<Investment>> expectedInvestments = Map.of("BTC", List.of(investment1, investment3),
                                                                "ETH", List.of(investment2));

        assertEquals(2.6, cryptoWallet.getBalance(), DELTA,
                "Wrongly calculated balance!");

        assertEquals(expectedInvestments, cryptoWallet.getActiveInvestments(),
                "Wrongly filled activeInvestments map!");
    }

    @Test
    public void testSellCryptoWithInvalidCryptoName() {
        assertThrows(IllegalArgumentException.class,
                () -> cryptoWallet.sellAsset(null, 10),
                "Expected IllegalArgumentException to be thrown when the crypto name is null!\"");
    }

    @Test
    public void testSellCryptoWithNonPositiveNumericValues() {
        assertThrows(IllegalArgumentException.class,
                () -> cryptoWallet.sellAsset("BTC", -0.1),
                "Expected IllegalArgumentException to be thrown when money is negative amount!\"");
    }

    @Test
    public void testSellCryptoWithNonExistingCryptoNameInTheInvestments() {
        assertThrows(CryptoNotFoundException.class,
                () -> cryptoWallet.sellAsset("ET2", 2),
                "Expected CryptoNotFoundException exception to be thrown!");
    }

    @Test
    public void testSellCryptoWithExistingCryptoNameInTheInvestments() throws InsufficientAmountException {
        cryptoWallet.depositMoney(10);
        cryptoWallet.buyAsset("BTC", 1, 1);
        cryptoWallet.buyAsset("ETH", 0.5, 4);
        cryptoWallet.buyAsset("ETH", 0.6, 3);

        Investment investment = new Investment(1, 1);
        Map<String, List<Investment>> expectedInvestments = Map.of("BTC", List.of(investment));

        assertDoesNotThrow(() -> cryptoWallet.sellAsset("ETH", 0.3),
                "Call should not throw any exception!");

        assertEquals(5.9, cryptoWallet.getBalance(), DELTA,
                "Wrongly calculated balance!");

        assertEquals(expectedInvestments, cryptoWallet.getActiveInvestments(),
                "Wrongly filled activeInvestments map!");
    }

    @Test
    public void testGetWalletInfoWithNoInvestmentsDone() {
        cryptoWallet.depositMoney(10);

        String expected = "Balance: 10.0$" + System.lineSeparator();

        assertEquals(expected, cryptoWallet.getWalletInfo(), "Unexpected wallet info returned!");
    }

    @Test
    public void testGetWalletInfo() throws InsufficientAmountException {
        cryptoWallet.depositMoney(10);
        cryptoWallet.buyAsset("BTC", 1, 1);
        cryptoWallet.buyAsset("ETH", 0.5, 4);
        cryptoWallet.buyAsset("ETH", 0.6, 3);

        String expected = "Balance: 2.0$" + System.lineSeparator() +
                "Crypto: BTC, Amount: 1.0" + System.lineSeparator() +
                "Crypto: ETH, Amount: 13.0" + System.lineSeparator();

        assertEquals(expected, cryptoWallet.getWalletInfo(), "Unexpected wallet info returned!");
    }

    @Test
    public void testGetWalletOverallStatsWithNoInvestmentsDone() {
        String expected = "Total P&L: 0,00$" + System.lineSeparator();

        assertEquals(expected, cryptoWallet.getWalletOverallStats(null),
                "Unexpected wallet overall stats returned!");
    }

    @Test
    public void testGetWalletOverallStats() throws InsufficientAmountException {
        cryptoWallet.depositMoney(10);
        cryptoWallet.buyAsset("BTC", 1, 1);
        cryptoWallet.buyAsset("ETH", 0.5, 4);
        cryptoWallet.buyAsset("ETH", 0.6, 3);

        Asset asset1 = new Asset("BTC", "Bitcoin", 1.3, 1);
        Asset asset2 = new Asset("ETH", "Ethereum", 0.4, 1);
        Map<String, Asset> currentPrices = Map.of(asset1.getId(), asset1, asset2.getId(), asset2);

        String expected = "Crypto: BTC, P&L: 0,30$" + System.lineSeparator() +
                          "Crypto: ETH, P&L: -1,80$" + System.lineSeparator() +
                          "Total P&L: -1,50$" + System.lineSeparator();

        assertEquals(expected, cryptoWallet.getWalletOverallStats(currentPrices),
                "Unexpected wallet overall stats  returned!");
    }
}
