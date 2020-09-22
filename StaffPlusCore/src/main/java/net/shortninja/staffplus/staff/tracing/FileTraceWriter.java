package net.shortninja.staffplus.staff.tracing;

import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.unordered.trace.TraceWriter;
import net.shortninja.staffplus.unordered.trace.TraceWriterType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

public class FileTraceWriter implements TraceWriter {

    private static final String PATH = StaffPlus.get().getDataFolder() + "/trace/";

    private final BufferedWriter writer;
    private final String fileName;


    public FileTraceWriter(UUID tracedUuid) {
        try {
            Player traced = Bukkit.getPlayer(tracedUuid);
            assert traced != null;

            String timeStamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
            fileName = PATH + "trace_" + traced.getName() + "_" + timeStamp + ".txt";
            File traceFile = new File(fileName);
            traceFile.getParentFile().mkdirs();
            if (traceFile.createNewFile()) {
                writer = new BufferedWriter(new FileWriter(fileName, true));
            } else {
                throw new RuntimeException("Could not start writing to File, unable to create trace file");
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not start writing to File, unable to open trace file", e);
        }
    }

    @Override
    public void writeToTrace(String message) {
        try {
            String traceMessage = "[" + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_DATE_TIME) + "] : " + message;
            writer.write(traceMessage);
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Could not write to trace file", e);
        }
    }

    @Override
    public void stopTrace() {
        try {
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("Unable to end trace", e);
        }
    }

    @Override
    public String getResource() {
        return fileName;
    }

    @Override
    public TraceWriterType getType() {
        return TraceWriterType.FILE;
    }
}
