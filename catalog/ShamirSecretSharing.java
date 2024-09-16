import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

class JSON {
    Map<String, Map<String, String>> data = new HashMap<>();

    void parse(BufferedReader reader) throws Exception {
        String line;
        String currentKey = "";
        while ((line = reader.readLine()) != null) {
            line = line.replaceAll("\\s+", "");
            if (line.isEmpty() || line.charAt(0) == '{' || line.charAt(0) == '}') continue;

            int colonPos = line.indexOf(':');
            if (colonPos == -1) continue;

            String key = line.substring(0, colonPos).replaceAll("\"", "");
            String value = line.substring(colonPos + 1).replaceAll("\"", "").replaceAll(",", "");

            if (value.equals("{")) {
                currentKey = key;
            } else {
                if (currentKey.isEmpty()) {
                    currentKey = "root";
                }
                data.computeIfAbsent(currentKey, k -> new HashMap<>()).put(key, value);
            }
        }
    }
}

 class ShamirSecretSharing {
    private static long convertToDecimal(String value, int base) {
        long result = 0;
        for (char c : value.toCharArray()) {
            if (Character.isDigit(c)) {
                result = result * base + (c - '0');
            } else if (Character.isLetter(c)) {
                result = result * base + (Character.toLowerCase(c) - 'a' + 10);
            } else {
                throw new RuntimeException("Invalid character in number: " + c);
            }
            if (result < 0) {
                throw new ArithmeticException("Number too large for long");
            }
        }
        return result;
    }

    private static long findSecret(List<Pair<Long, Long>> points) {
        long secret = 0;
        int n = points.size();

        for (int i = 0; i < n; i++) {
            long term = points.get(i).second;
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    if (points.get(i).first.equals(points.get(j).first)) {
                        throw new RuntimeException("Duplicate x values in points");
                    }
                    term = term * (-points.get(j).first) / (points.get(i).first - points.get(j).first);
                }
            }
            secret += term;
        }

        return secret;
    }

    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            JSON input = new JSON();
            input.parse(reader);

            if (!input.data.containsKey("keys")) {
                throw new RuntimeException("Missing 'keys' in input");
            }

            int n = Integer.parseInt(input.data.get("keys").get("n"));
            int k = Integer.parseInt(input.data.get("keys").get("k"));

            List<Pair<Long, Long>> points = new ArrayList<>();

            for (Map.Entry<String, Map<String, String>> entry : input.data.entrySet()) {
                if (!entry.getKey().equals("keys")) {
                    long x = Long.parseLong(entry.getKey());
                    Map<String, String> point = entry.getValue();
                    if (!point.containsKey("base") || !point.containsKey("value")) {
                        throw new RuntimeException("Missing 'base' or 'value' for point " + entry.getKey());
                    }
                    int base = Integer.parseInt(point.get("base"));
                    long y = convertToDecimal(point.get("value"), base);
                    points.add(new Pair<>(x, y));
                }
            }

            if (points.size() < k) {
                throw new RuntimeException("Not enough points provided. Need at least " + k + " points, but got " + points.size());
            }

            List<Pair<Long, Long>> usedPoints = points.subList(0, k);
            long secret = findSecret(usedPoints);

            System.out.println(secret);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    static class Pair<T, U> {
        T first;
        U second;

        Pair(T first, U second) {
            this.first = first;
            this.second = second;
        }
    }
}
//test case 1 
// {
//     "keys": {
//         "n": 4,
//         "k": 3
//     },
//     "1": {
//         "base": "10",
//         "value": "4"
//     },
//     "2": {
//         "base": "2",
//         "value": "111"
//     },
//     "3": {
//         "base": "10",
//         "value": "12"
//     },
//     "6": {
//         "base": "4",
//         "value": "213"
//     }
// }
//output  - 3


//test case 2
// "2": {
//     "base": "2",
//     "value": "111"
// }
//output  - 7


//test case - 3
// {
//     "keys": {
//         "n": 9,
//         "k": 6
//     },
//     "1": {
//         "base": "10",
//         "value": "28735619723837"
//     },
//     "2": {
//         "base": "16",
//         "value": "1A228867F0CA"
//     },
//     "3": {
//         "base": "12",
//         "value": "32811A4AA0B7B"
//     },
//     "4": {
//         "base": "11",
//         "value": "917978721331A"
//     },
//     "5": {
//         "base": "16",
//         "value": "1A22886782E1"
//     },
//     "6": {
//         "base": "10",
//         "value": "28735619654702"
//     },
//     "7": {
//         "base": "14",
//         "value": "71AB5070CC4B"
//     },
//     "8": {
//         "base": "9",
//         "value": "122662581541670"
//     },
//     "9": {
//         "base": "8",
//         "value": "642121030037605"
//     }
// }
//output - 28735619723846