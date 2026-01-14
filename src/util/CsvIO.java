package util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsvIO {

    public static class CsvData {
        public final String[] header;
        public final List<String[]> rows;

        public CsvData(String[] header, List<String[]> rows) {
            this.header = header;
            this.rows = rows;
        }
    }

    public static CsvData readWithHeader(Path csvPath) throws IOException {
        List<String> lines = Files.readAllLines(csvPath, StandardCharsets.UTF_8);
        if (lines.isEmpty()) return new CsvData(new String[0], new ArrayList<>());

        String[] header = splitCsvLine(lines.get(0));
        List<String[]> rows = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line == null || line.trim().isEmpty()) continue;
            rows.add(splitCsvLine(line));
        }
        return new CsvData(header, rows);
    }

    public static void writeAll(Path out, String[] header, List<String[]> rows) throws IOException {
        Files.createDirectories(out.getParent());

        StringBuilder sb = new StringBuilder();
        sb.append(String.join(",", header)).append(System.lineSeparator());
        for (String[] r : rows) {
            sb.append(String.join(",", sanitizeRow(r, header.length))).append(System.lineSeparator());
        }

        Files.writeString(out, sb.toString(), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private static String[] splitCsvLine(String line) {
        String[] parts = line.split(",", -1);
        for (int i = 0; i < parts.length; i++) parts[i] = parts[i].trim();
        return parts;
    }

    private static String[] sanitizeRow(String[] r, int len) {
        String[] out = Arrays.copyOf(r, len);
        for (int i = 0; i < out.length; i++) {
            if (out[i] == null) out[i] = "";
            // basic safety: remove newlines
            out[i] = out[i].replace("\n", " ").replace("\r", " ");
        }
        return out;
    }
}
