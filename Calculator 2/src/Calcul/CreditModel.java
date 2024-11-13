package Calcul;

import java.util.ArrayList;
import java.util.List;

class CreditConditions {
    char type;          
    double totalCredit;
    double rate;
    int term;

    public CreditConditions(char type, double totalCredit, double rate, int term) {
        this.type = type;
        this.totalCredit = totalCredit;
        this.rate = rate;
        this.term = term;
    }
}

public class CreditModel {
    private List<Double> payments = new ArrayList<>();
    private double overpayment;
    private double totalPayment;

    public List<Double> getPayments() {
        return payments;
    }

    public double getOverpayment() {
        return overpayment;
    }

    public double getTotalPayment() {
        return totalPayment;
    }

    public void calculateCredit(CreditConditions conditions) {
        payments.clear();
        if (conditions.type == 'a') {
            double monthlyRate = conditions.rate / 1200;
            double annuityPayment = (conditions.totalCredit * monthlyRate) /
                    (1 - Math.pow(1 + monthlyRate, -conditions.term));
            payments.add(annuityPayment);
            totalPayment = conditions.term * annuityPayment;
            overpayment = totalPayment - conditions.totalCredit;
        } else {
            double firstPayment = 0;
            double daysInPeriod = 30.4166545;
            double remainingDebt = conditions.totalCredit;
            double differentiatedPayment;
            overpayment = 0;

            for (int m = 1; m <= conditions.term; m++) {
                double interestPayment = (remainingDebt * (conditions.rate / 100) * daysInPeriod) / 365;
                overpayment += interestPayment;
                differentiatedPayment = (conditions.totalCredit / conditions.term) + interestPayment;
                remainingDebt -= conditions.totalCredit / conditions.term;
                if (firstPayment == 0) {
                    firstPayment = differentiatedPayment;
                }
            }
            payments.add(firstPayment);
            totalPayment = overpayment + conditions.totalCredit;
            payments.add((conditions.totalCredit / conditions.term) +
                    (remainingDebt * (conditions.rate / 100) * daysInPeriod) / 365);
        }
    }
}
