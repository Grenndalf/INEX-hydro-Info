package Others.Utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ListsUtils {

    private final List<BigDecimal> list;

    public ListsUtils (List<BigDecimal> list) {
        this.list = list;
    }

    private BigDecimal averageMaxValue () {
        if (list.size () > 1) {
            BigDecimal result = BigDecimal.ZERO;
            for (BigDecimal value : list) {
                result = result.add (value);
            }
            return result.divide (BigDecimal.valueOf (list.size ()), 4, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal averageNatLog () {
        BigDecimal result = BigDecimal.ZERO;
        if (list.size () > 0) {
            for (int i = 1; i <= list.size (); i++) {
                result = result.add (BigDecimal.valueOf (Math.log (i)));
            }
            return result.divide (BigDecimal.valueOf (list.size ()), 4, RoundingMode.HALF_UP);
        } else {
            return result;
        }
    }

    private List<BigDecimal> getModifiedMaxValueList () {
        return list.stream ()
                .map (bigDecimal -> bigDecimal.subtract (averageMaxValue ()))
                .collect (Collectors.toList ());
    }

    private List<BigDecimal> getLogModifiedMaxValueList () {
        return IntStream.range (1, (list.size ()))
                .mapToObj (i -> BigDecimal.valueOf (Math.log (i)).subtract (averageNatLog ()))
                .collect (Collectors.toList ());
    }

    private BigDecimal calculateB1 () {
        if (list.size () > 1) {
            BigDecimal sumOfMultiplyAverageValues = BigDecimal.ZERO;
            BigDecimal sumOfPowLogValues = BigDecimal.ZERO;
            int i = 0;
            while (i < list.size ()) {
                sumOfPowLogValues = sumOfPowLogValues.add (BigDecimal.valueOf (
                        Math.pow ((getLogModifiedMaxValueList ().get (i).doubleValue ()), 2))).setScale (4,
                                                                                                         RoundingMode.HALF_UP);
                sumOfMultiplyAverageValues = sumOfMultiplyAverageValues.add (BigDecimal.valueOf (
                        getLogModifiedMaxValueList ().get (i).doubleValue ()).multiply (getModifiedMaxValueList ().get (i))).setScale (4, RoundingMode.HALF_UP);
                i++;
            }
            if (sumOfPowLogValues.signum () > 0) {
                return sumOfMultiplyAverageValues.divide (sumOfPowLogValues, 4, RoundingMode.HALF_UP);
            }
            return BigDecimal.ZERO;
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal calculateB0 () {
        if (list.size () > 1) {
            final BigDecimal averageLogNumValue = averageNatLog ();
            final BigDecimal averageMaxFlowValue = averageMaxValue ();
            return averageMaxFlowValue.subtract (averageLogNumValue.multiply (calculateB1 ()))
                    .setScale (4, RoundingMode.HALF_UP);
        } else {
            return BigDecimal.ZERO;
        }
    }

    private String calculateHelperValue () {
        if ((getBigDecimalValueDecile (0.5).subtract (getBigDecimalValueDecile (1))).setScale (2,
                                                                                               RoundingMode.HALF_UP).doubleValue () != 0.0) {
            return (getQuantilePointer ().multiply (getBigDecimalValueDecile (0.5)))
                    .divide (getBigDecimalValueDecile (0.5).subtract (getBigDecimalValueDecile (1)), 2,
                             RoundingMode.HALF_UP)
                    .toEngineeringString ();
        }
        return "0";
    }

    private BigDecimal getBigDecimalValueDecile (double v) {
        return calculateDeciles (calculateB0 (),
                                 calculateB1 (),
                                 BigDecimal.valueOf (v).multiply (BigDecimal.valueOf (list.size ())));
    }

    private BigDecimal calculateDeciles (BigDecimal b0, BigDecimal b1, BigDecimal x) {
        if (x.compareTo (BigDecimal.valueOf (1)) > 0 && x.compareTo (BigDecimal.valueOf (100)) < 0) {
            return (b0.add ((b1).multiply (BigDecimal.valueOf (Math.log (x.doubleValue ()))))).setScale (2,
                                                                                                         RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal getQuantilePointer () {
        return (getBigDecimalValueDecile (0.1).subtract (getBigDecimalValueDecile (0.9)))
                .divide (getBigDecimalValueDecile (0.5).multiply (BigDecimal.valueOf (2)),
                         RoundingMode.HALF_UP);
    }

}
