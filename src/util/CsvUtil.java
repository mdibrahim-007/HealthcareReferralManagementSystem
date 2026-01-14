package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvUtil {
    public static String[] splitCsvLine(String line) {
        if (line == null) return new String[0];

        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();

        boolean inQuotes = false;
        int i = 0;

        while (i < line.length()) {
            char ch = line.charAt(i);

            if (inQuotes) {
                if (ch == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        cur.append('"');
                        i += 2;
                        continue;
                    } else {
                        // End quote
                        inQuotes = false;
                        i++;
                        continue;
                    }
                } else {
                    cur.append(ch);
                    i++;
                    continue;
                }
            } else {
                if (ch == '"') {
                    inQuotes = true;
                    i++;
                    continue;
                }

                if (ch == ',') {
                    out.add(cur.toString().trim());
                    cur.setLength(0);
                    i++;
                    continue;
                }

                cur.append(ch);
                i++;
            }
        }

        out.add(cur.toString().trim());
        return out.toArray(new String[0]);
    }

    public static List<String[]> readAll(Path file) throws IOException {
        List<String[]> rows = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            boolean first = true;

            while ((line = br.readLine()) != null) {
                if (first) { // skip header
                    first = false;
                    continue;
                }

                if (line.trim().isEmpty()) continue;

                rows.add(splitCsvLine(line));
            }
        }

        return rows;
    }

    public static String toCsvRow(String... fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(escape(fields[i]));
        }
        return sb.toString();
    }

    public static String escape(String s) {
        if (s == null) s = "";
        boolean mustQuote = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        if (!mustQuote) return s;

        String escaped = s.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }
}
