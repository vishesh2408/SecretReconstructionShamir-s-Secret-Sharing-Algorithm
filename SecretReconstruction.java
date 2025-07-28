import java.io.FileReader;
import java.math.BigInteger;
import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;



public class SecretReconstruction {

    public static void main(String[] args) throws Exception {
        String[] testFiles = {"testcase1.json", "testcase2.json"};
        for (String file : testFiles) {
            JSONObject json = (JSONObject) new JSONParser().parse(new FileReader(file));
            JSONObject keys = (JSONObject) json.get("keys");
            int n = Integer.parseInt(keys.get("n").toString());
            int k = Integer.parseInt(keys.get("k").toString());

            List<BigInteger> xList = new ArrayList<>();
            List<BigInteger> yList = new ArrayList<>();

            for (Object key : json.keySet()) {
                if (key.equals("keys")) continue;

                int x = Integer.parseInt(key.toString());
                JSONObject point = (JSONObject) json.get(key);
                int base = Integer.parseInt(point.get("base").toString());
                String value = point.get("value").toString();
                BigInteger y = new BigInteger(value, base);
                xList.add(BigInteger.valueOf(x));
                yList.add(y);
            }

        
            BigInteger secret = reconstructSecret(xList.subList(0, k), yList.subList(0, k));
            System.out.println("Secret (constant term c) for " + file + ": " + secret);
        }
    }

    
    public static BigInteger reconstructSecret(List<BigInteger> xList, List<BigInteger> yList) {
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < xList.size(); i++) {
            BigInteger xi = xList.get(i);
            BigInteger yi = yList.get(i);

            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < xList.size(); j++) {
                if (i == j) continue;

                BigInteger xj = xList.get(j);

                numerator = numerator.multiply(xj.negate()); // -xj
                denominator = denominator.multiply(xi.subtract(xj));
            }

            BigInteger li = numerator.multiply(denominator.modInverse(BigInteger.valueOf(Long.MAX_VALUE)));
            result = result.add(yi.multiply(li));
        }

        return result;
    }
}
