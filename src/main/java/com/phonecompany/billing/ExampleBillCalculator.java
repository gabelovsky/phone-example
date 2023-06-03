package com.phonecompany.billing;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class ExampleBillCalculator implements TelephoneBillCalculator {

    /**
     * Calculates total phone bill and discounts
     *
     * @param phoneLog - csv phone log in format number, call start time, call end time
     * @return final charge value
     */
    public BigDecimal calculate(String phoneLog) {

        List<PhoneLog> phoneCallList;
        Map<String, PhoneBill> phoneBillMap = new HashMap<>();
        int primeStartHour = 8;
        int primeEndHour = 16;

        BigDecimal primePrice = new BigDecimal(1);
        BigDecimal basePrice = new BigDecimal(0.5);
        BigDecimal extraMinutesPrice = new BigDecimal(0.2);

        String mostCalled = "";
        int mostCalledCount = Integer.MIN_VALUE;

        try {
            MappingIterator<PhoneLog> phoneCallIterator = new CsvMapper().readerWithTypedSchemaFor(PhoneLog.class)
                    .readValues(phoneLog);
            phoneCallList = phoneCallIterator.readAll();
        } catch (Exception e) {
            //assuming input is always correctly formed
            e.printStackTrace();
            return new BigDecimal(0);
        }


        for (PhoneLog log : phoneCallList) {
            long duration = log.endTime.getTime() - log.startTime.getTime();
            int totalMinutes = (int) Math.ceil((double) duration / (1000 * 60));
            // for simplicity's sake ignoring calls that are longer than 16 hours(multi day calls)
            int primeMinutes = getPrimeTimeMinutes(
                    setPrimeTime(log.startTime, primeStartHour),
                    setPrimeTime(log.endTime, primeEndHour),
                    log.startTime,
                    log.endTime
            );

            BigDecimal charge = new BigDecimal(totalMinutes - primeMinutes).multiply(basePrice);
            charge = charge.add(new BigDecimal(primeMinutes).multiply(primePrice));
            if (totalMinutes > 5) {
                charge = charge.add(new BigDecimal(totalMinutes - 5).multiply(extraMinutesPrice));
            }
            charge = charge.setScale(2, RoundingMode.DOWN);


            if (phoneBillMap.containsKey(log.phoneNumber)) {
                phoneBillMap.get(log.phoneNumber).addCall(charge);
            } else {
                phoneBillMap.put(log.phoneNumber, new PhoneBill(charge));
            }


            if (phoneBillMap.get(log.phoneNumber).getCallCount() > mostCalledCount ||
                    (phoneBillMap.get(log.phoneNumber).getCallCount() == mostCalledCount
                            && Long.parseLong(mostCalled) < Long.parseLong(log.phoneNumber))
            ) {
                mostCalled = log.phoneNumber;
                mostCalledCount = phoneBillMap.get(log.phoneNumber).getCallCount();
            }
        }

        phoneBillMap.get(mostCalled).setZeroCharge();

        return phoneBillMap.values().stream().map(log -> log.getCharge()).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get the number of minutes between two date intervals
     *
     * @param primeStart - prime start time
     * @param primeEnd   - prime end time
     * @param callStart  - call start time
     * @param callEnd    - call end time
     * @return number of minutes in the prime time
     */
    private int getPrimeTimeMinutes(Date primeStart, Date primeEnd, Date callStart, Date callEnd) {

        if (callStart.after(primeEnd) || callEnd.before(primeStart)) {
            return 0;
        }
        Date intervalStart = (primeStart.after(callStart) ? primeStart : callStart);
        Date intervalEnd = (primeEnd.before(callEnd) ? primeEnd : callEnd);
        long duration = intervalEnd.getTime() - intervalStart.getTime();

        return (int) Math.ceil((double) duration / (1000 * 60));
    }

    /**
     * Set the hours of the call to the prime time(ignoring multi-day calls)
     *
     * @param callTime - original call time
     * @param hour     - hour value to set
     * @return time with the hour minute and second values overwritten
     */
    private Date setPrimeTime(Date callTime, int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(callTime);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, hour);
        return calendar.getTime();
    }

}
