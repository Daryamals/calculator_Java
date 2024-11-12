package Calcul;

class DepositConditions {
    double deposit;
    double rate;
    int term;
    double tax;
    double replenishments;
    double withdrawals;
    int monthR;
    int monthW;
    int periodicity;
    char capitalization;

    public DepositConditions(double deposit, double rate, int term, double tax,
                             double replenishments, double withdrawals,
                             int monthR, int monthW, int periodicity, char capitalization) {
        this.deposit = deposit;
        this.rate = rate;
        this.term = term;
        this.tax = tax;
        this.replenishments = replenishments;
        this.withdrawals = withdrawals;
        this.monthR = monthR;
        this.monthW = monthW;
        this.periodicity = periodicity;
        this.capitalization = capitalization;
    }
}

public class DepositModel {
    private double income;
    private double taxOutcome;
    private double totalSum;

    public double getIncome() {
        return income;
    }

    public double getTaxOutcome() {
        return taxOutcome;
    }

    public double getTotalSum() {
        return totalSum;
    }

    public void calculateDeposit(DepositConditions conditions) {
        double accruedInterest = conditions.deposit;
        double taxAmount;
        double depositAmount;

        if (conditions.capitalization == 'y') {
            for (int i = 1; i <= conditions.term; i++) {
                if (conditions.replenishments != 0 && conditions.monthR != 0 && i % conditions.monthR == 0) {
                    accruedInterest += conditions.replenishments;
                }
                if (conditions.withdrawals != 0 && conditions.monthW != 0 && i % conditions.monthW == 0) {
                    accruedInterest -= conditions.withdrawals;
                }
                if (conditions.periodicity != 0 && i % conditions.periodicity == 0) {
                    accruedInterest += accruedInterest * (conditions.rate / 12 / 100);
                }
            }
        } else {
            for (int i = 1; i <= conditions.term; i++) {
                if (conditions.replenishments != 0 && conditions.monthR != 0 && i % conditions.monthR == 0) {
                    conditions.deposit += conditions.replenishments;
                }
                if (conditions.withdrawals != 0 && conditions.monthW != 0 && i % conditions.monthW == 0) {
                    conditions.deposit -= conditions.withdrawals;
                }
            }
            accruedInterest = conditions.deposit + (conditions.deposit * (conditions.rate / 12 / 100) * conditions.term);
        }

        taxAmount = (accruedInterest - conditions.deposit) * conditions.tax / 100;
        depositAmount = accruedInterest - taxAmount;
        income = accruedInterest - conditions.deposit;
        taxOutcome = taxAmount;
        totalSum = depositAmount;
    }
}